package de.kontext_e.jqassistant.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

import java.io.File;

import static java.lang.String.format;

public class JqassistantGradlePlugin implements Plugin<Project> {
    public void apply(Project project) {
        final JqassistantPluginExtension jqassistantPluginExtension = project.getExtensions().create("jqassistant", JqassistantPluginExtension.class);

        final Configuration config = project.getConfigurations().create("dataFiles")
                .setVisible(true)
                .setDescription("The data artifacts to be processed for this plugin.");

        config.defaultDependencies(dependencies -> {
            dependencies.add(project.getDependencies().create("com.buschmais.jqassistant.cli:jqassistant-commandline-neo4jv3:"+jqassistantPluginExtension.getToolVersion()));
            dependencies.add(project.getDependencies().create("com.buschmais.jqassistant.plugin:java:"+jqassistantPluginExtension.getToolVersion()));
            if (jqassistantPluginExtension.getPlugins() != null) {
                for (String plugin : jqassistantPluginExtension.getPlugins()) {
                    dependencies.add(project.getDependencies().create(plugin));
                }

            }
        });

        registerTask(project, config, jqassistantPluginExtension, "analyze");
        registerTask(project, config, jqassistantPluginExtension, "server");
        registerTask(project, config, jqassistantPluginExtension, "report");
        registerTask(project, config, jqassistantPluginExtension, "available-scopes");
        registerTask(project, config, jqassistantPluginExtension, "available-rules");
        registerTask(project, config, jqassistantPluginExtension, "effective-rules");


        final Jqassistant scan = project.getTasks().create("scan", Jqassistant.class, jqassistant -> {
            jqassistant.setDataFiles(config);
            jqassistant.addArg("scan");
            jqassistant.setExtension(jqassistantPluginExtension);

            final JavaPluginConvention javaPluginConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
            if(javaPluginConvention != null && !javaPluginConvention.getSourceSets().isEmpty()) {
                jqassistant.addArg("-f");

                for (SourceSet sourceSet : javaPluginConvention.getSourceSets()) {
                    FileCollection presentClassDirs = sourceSet.getOutput().getClassesDirs().filter(File::exists);
                    final String asPath = presentClassDirs.getAsPath();
                    if(asPath != null && !asPath.isEmpty()) {
                        jqassistant.addArg("java:classpath::" + asPath);
                    }
                    // FIXME subprojects
                }
            }
        });
        scan.setDescription("Executes jQAssistant task 'scan'.");

        // this is for dynamically defined task of this type in build.gradle
        project.getTasks().withType(Jqassistant.class).configureEach(jqassistant -> jqassistant.setDataFiles(config));
    }

    private void registerTask(Project project, Configuration config, JqassistantPluginExtension jqassistantPluginExtension, String name) {
        final Jqassistant read = project.getTasks().create(name, Jqassistant.class, jqassistant -> {
            jqassistant.setDataFiles(config);
            jqassistant.addArg(name);
            jqassistant.setExtension(jqassistantPluginExtension);
        });
        read.setDescription(format("Executes jQAssistant task '%s'.",name));
    }

}
