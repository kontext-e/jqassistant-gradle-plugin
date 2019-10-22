package de.kontext_e.jqassistant.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.util.WrapUtil;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class JqassistantGradlePlugin implements Plugin<Project> {
    public void apply(Project project) {
        final JqassistantPluginExtension jqassistantPluginExtension = project.getExtensions().create("jqassistant", JqassistantPluginExtension.class);

        final Configuration config = project.getConfigurations().create("jqassistant")
                .setVisible(false)
                .setDescription("The data artifacts to be processed for this plugin.");

        config.defaultDependencies(dependencies -> {
            final String toolVersion = jqassistantPluginExtension.getToolVersion();

            addDependencyForCli(project, dependencies, toolVersion);
            addDependencyForJava(project, dependencies, toolVersion);

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
        final Jqassistant scanTask = registerTask(project, config, jqassistantPluginExtension, "scan");
        scanTask.projectToScan(project);

        // this is for dynamically defined task of this type in build.gradle
        project.getTasks().withType(Jqassistant.class).configureEach(jqassistant -> jqassistant.setClasspath(config));
    }

    private void addDependencyForCli(Project project, DependencySet dependencies, String toolVersion) {
        String artifact = "com.buschmais.jqassistant.cli:jqassistant-commandline-neo4jv3:";

        if(toolVersion.startsWith("1.2")) {
            // in 1.2 it was a different name
            artifact = "com.buschmais.jqassistant:commandline:";
        }

        if(toolVersion.startsWith("1.3")) {
            // in 1.2 it was another different name
            artifact = "com.buschmais.jqassistant:jqassistant-commandline:";
        }

        final Dependency dependency = project.getDependencies().create(artifact + toolVersion);
        if(dependency instanceof ModuleDependency) {
            // java plugin brings own asm dependency which conflicts with Neo4js one
            ModuleDependency md = (ModuleDependency) dependency;
            md.exclude(WrapUtil.toMap("module", "asm"));
        }
        dependencies.add(dependency);
    }

    private void addDependencyForJava(Project project, DependencySet dependencies, String toolVersion) {
        String javaPluginVersion = toolVersion;

        // in some previous versions, the version number of Java plugin was only major.minor
        final Map<String, String> toolVersionToJavaPluginVersion = new HashMap<>();
        toolVersionToJavaPluginVersion.put("1.4.0", "1.4");
        toolVersionToJavaPluginVersion.put("1.3.0", "1.3");
        toolVersionToJavaPluginVersion.put("1.2.0", "1.2");
        if(toolVersionToJavaPluginVersion.containsKey(javaPluginVersion)) {
            javaPluginVersion = toolVersionToJavaPluginVersion.get(javaPluginVersion);
        }
        dependencies.add(project.getDependencies().create("com.buschmais.jqassistant.plugin:java:"+ javaPluginVersion));
    }

    private Jqassistant registerTask(Project project, Configuration config, JqassistantPluginExtension jqassistantPluginExtension, String name) {
        final Jqassistant jqa = project.getTasks().create(name, Jqassistant.class, jqassistant -> {
            jqassistant.setClasspath(config);
            jqassistant.addArg(name);
            jqassistant.setExtension(jqassistantPluginExtension);
        });
        jqa.setDescription(format("Executes jQAssistant task '%s'.",name));
        return jqa;
    }
}
