package de.kontext_e.jqassistant.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.*;

import static java.lang.String.format;

public class JqassistantGradlePlugin implements Plugin<Project> {
    public void apply(Project project) {
        JqassistantPluginExtension jqassistantPluginExtension = getPluginExtension(project);
        Configuration config = getConfiguration(project, jqassistantPluginExtension);
        registerTasks(project, config, jqassistantPluginExtension);
    }

    private JqassistantPluginExtension getPluginExtension(Project project) {
        return project.getExtensions().create("jqassistant", JqassistantPluginExtension.class);
    }

    private Configuration getConfiguration(Project project, JqassistantPluginExtension jqassistantPluginExtension) {
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
        final String toolVersion = jqassistantPluginExtension.getToolVersion();

        config.defaultDependencies(dependencies -> {
            addDependencyForCli(project, dependencies, jqassistantPluginExtension);
            addDependencyForJava(project, dependencies, toolVersion);
            addAdditionalPlugins(project, dependencies, jqassistantPluginExtension);
        });

        manageLoggerImplementations(config);
    }

    private void manageLoggerImplementations(Configuration config) {
        config.resolutionStrategy(resolutionStrategy ->
            resolutionStrategy.eachDependency(dependencyDetails -> {
                if (dependencyDetails.getRequested().getName().equals("slf4j-simple")){
                    dependencyDetails.useVersion("2.0.9");
                }
            })
        );
    }

    private void addDependencyForCli(Project project, DependencySet dependencies, JqassistantPluginExtension extension) {
        int neo4jVersion = extension.getNeo4jVersion();
        System.out.println("Using Neo4J Version: " + neo4jVersion);
        String artifact = "com.buschmais.jqassistant.cli:jqassistant-commandline-neo4jv" + neo4jVersion +":";
        Dependency dependency = project.getDependencies().create(artifact + extension.getToolVersion());
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
        for (Tasks task : Tasks.values()) {
            registerTask(project, jqassistantPluginExtension, task.toString());
        }
    }


        // this is for dynamically defined task of this type in build.gradle
        project.getTasks().withType(Jqassistant.class).configureEach(jqassistant -> jqassistant.setClasspath(config));
    }

    private Jqassistant registerTask(Project project, Configuration config, JqassistantPluginExtension jqassistantPluginExtension, String name) {
        return project.getTasks().create(name, Jqassistant.class, jqassistant -> {
            jqassistant.getMainClass().set("com.buschmais.jqassistant.commandline.Main");
            jqassistant.setStandardInput(System.in);
            jqassistant.setStandardOutput(System.out);
            jqassistant.setErrorOutput(System.err);
            jqassistant.setClasspath(config);
            jqassistant.addArg(name);
            jqassistant.setExtension(jqassistantPluginExtension);
            jqassistant.setGroup("jQAssistant");
            jqassistant.setDescription(format("Executes jQAssistant task '%s'.",name));
        });
    }
}
