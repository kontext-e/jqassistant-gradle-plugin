package de.kontext_e.jqassistant.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

import java.io.File;

public class JqassistantGradlePlugin implements Plugin<Project> {
    public void apply(Project project) {
        final JqassistantPluginExtension jqassistantPluginExtension = project.getExtensions().create("jqassistant", JqassistantPluginExtension.class);

        final Configuration config = project.getConfigurations().create("dataFiles")
                .setVisible(true)
                .setDescription("The data artifacts to be processed for this plugin.");

        config.defaultDependencies(new Action<DependencySet>() {
            public void execute(DependencySet dependencies) {
                dependencies.add(project.getDependencies().create("com.buschmais.jqassistant.cli:jqassistant-commandline-neo4jv3:"+jqassistantPluginExtension.getToolVersion()));
                dependencies.add(project.getDependencies().create("com.buschmais.jqassistant.plugin:java:"+jqassistantPluginExtension.getToolVersion()));
                if (jqassistantPluginExtension.getPlugins() != null) {
                    for (String plugin : jqassistantPluginExtension.getPlugins()) {
                        dependencies.add(project.getDependencies().create(plugin));
                    }

                }
            }
        });

        final Jqassistant server = project.getTasks().create("read", Jqassistant.class, new Action<Jqassistant>() {
            @Override
            public void execute(Jqassistant jqassistant) {
                jqassistant.setDataFiles(config);
                jqassistant.addArg("read");
            }
        });
        server.setDescription("Read from console.");



        final Jqassistant help = project.getTasks().create("jqa-help", Jqassistant.class, new Action<Jqassistant>() {
            @Override
            public void execute(Jqassistant jqassistant) {
                jqassistant.setDataFiles(config);
                jqassistant.addArg("-help");
            }
        });
        help.setDescription("Executes jQAssistant task '-help'.");

        final Jqassistant read = project.getTasks().create("server", Jqassistant.class, new Action<Jqassistant>() {
            @Override
            public void execute(Jqassistant jqassistant) {
                jqassistant.setDataFiles(config);
                jqassistant.addArg("server");
            }
        });
        read.setDescription("Executes jQAssistant task 'server'.");

        final Jqassistant availableRules = project.getTasks().create("available-rules", Jqassistant.class, new Action<Jqassistant>() {
            @Override
            public void execute(Jqassistant jqassistant) {
                jqassistant.setDataFiles(config);
                jqassistant.addArg("available-rules");
            }
        });
        availableRules.setDescription("Executes jQAssistant task 'available-rules'.");

        final Jqassistant availableScopes = project.getTasks().create("available-scopes", Jqassistant.class, new Action<Jqassistant>() {
            @Override
            public void execute(Jqassistant jqassistant) {
                jqassistant.setDataFiles(config);
                jqassistant.addArg("available-scopes");
            }
        });
        availableScopes.setDescription("Executes jQAssistant task 'available-scopes'.");

        final Jqassistant scan = project.getTasks().create("scan", Jqassistant.class, new Action<Jqassistant>() {
            @Override
            public void execute(Jqassistant jqassistant) {
                jqassistant.setDataFiles(config);
                jqassistant.addArg("scan");

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
            }
        });
        scan.setDescription("Executes jQAssistant task 'scan'.");

        project.getTasks().create("deb", new Action<Task>() {
            @Override
            public void execute(Task task) {
                final JavaPluginConvention javaPluginConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
                if(javaPluginConvention != null) {
                    for (SourceSet sourceSet : javaPluginConvention.getSourceSets()) {
                        FileCollection presentClassDirs = sourceSet.getOutput().getClassesDirs().filter(File::exists);
                    }
                }
            }
        });

        project.getTasks().withType(Jqassistant.class).configureEach(new Action<Jqassistant>() {
            public void execute(Jqassistant jqassistant) {
                jqassistant.setDataFiles(config);
            }
        });
    }

}
