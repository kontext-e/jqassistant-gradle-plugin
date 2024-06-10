package de.kontext_e.jqassistant.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;

public class JqassistantGradlePlugin implements Plugin<Project> {
    public void apply(@NotNull Project project) {
        JqassistantPluginExtension jqassistantPluginExtension = getPluginExtension(project);
        Configuration config = getPluginConfiguration(project, jqassistantPluginExtension);
        registerTasks(project, config, jqassistantPluginExtension);
    }

    private JqassistantPluginExtension getPluginExtension(Project project) {
        return project.getExtensions().create("jqassistant", JqassistantPluginExtension.class);
    }

    private Configuration getPluginConfiguration(Project project, JqassistantPluginExtension jqassistantPluginExtension) {
        final Configuration config = createConfig(project);
        addDependenciesToConfig(project, jqassistantPluginExtension, config);
        return config;
    }

    private Configuration createConfig(Project project) {
        return project
                .getConfigurations()
                .create("jqassistant")
                .setVisible(false)
                .setDescription("The data artifacts to be processed for this plugin.");
    }

    private void addDependenciesToConfig(Project project, JqassistantPluginExtension jqassistantPluginExtension, Configuration config) {
        config.defaultDependencies(dependencies -> {
            final String toolVersion = jqassistantPluginExtension.getToolVersion();

            addDependencyForCli(project, dependencies, toolVersion);
            addDependencyForJava(project, dependencies, toolVersion);
            addAdditionalPlugins(project, dependencies, jqassistantPluginExtension);
        });
    }

    private void addDependencyForCli(Project project, DependencySet dependencies, String toolVersion) {
        // TODO How to make this dependant on JVM (v5 for > 17 / v4 otherwise)?
        String artifact = "com.buschmais.jqassistant.cli:jqassistant-commandline-neo4jv4:";
        Dependency dependency = project.getDependencies().create(artifact + toolVersion);
        dependencies.add(dependency);
    }

    private void addDependencyForJava(Project project, DependencySet dependencies, String toolVersion) {
        String artifact = "com.buschmais.jqassistant.plugin:java:";
        Dependency dependency = project.getDependencies().create(artifact + toolVersion);
        dependencies.add(dependency);
    }

    private void addAdditionalPlugins(Project project, DependencySet dependencies, JqassistantPluginExtension jqassistantPluginExtension) {
        if (jqassistantPluginExtension.getPlugins() == null) return;

        for (String plugin : jqassistantPluginExtension.getPlugins()) {
            dependencies.add(project.getDependencies().create(plugin));
        }
    }

    private void registerTasks(Project project, Configuration config, JqassistantPluginExtension jqassistantPluginExtension) {
        registerTask(project, config, jqassistantPluginExtension, "analyze");
        registerTask(project, config, jqassistantPluginExtension, "server");
        registerTask(project, config, jqassistantPluginExtension, "report");
        registerTask(project, config, jqassistantPluginExtension, "available-scopes");
        registerTask(project, config, jqassistantPluginExtension, "available-rules");
        registerTask(project, config, jqassistantPluginExtension, "effective-rules");
        registerTask(project, config, jqassistantPluginExtension, "reset");
        registerTask(project, config, jqassistantPluginExtension, "list-plugins");
        registerTask(project, config, jqassistantPluginExtension, "scan").projectToScan(project);

        // this is for dynamically defined task of this type in build.gradle
        project.getTasks().withType(Jqassistant.class).configureEach(jqassistant -> jqassistant.setClasspath(config));
    }

    private Jqassistant registerTask(Project project, Configuration config, JqassistantPluginExtension jqassistantPluginExtension, String name) {
        return project.getTasks().create(name, Jqassistant.class, jqassistant -> {
            jqassistant.getMainClass().set("com.buschmais.jqassistant.commandline.Main");
            jqassistant.setStandardInput(System.in);
            jqassistant.setClasspath(config);
            jqassistant.addArg(name);
            jqassistant.setExtension(jqassistantPluginExtension);
            jqassistant.setGroup("jQAssistant");
            jqassistant.setDescription(format("Executes jQAssistant task '%s'.",name));
        });
    }


}
