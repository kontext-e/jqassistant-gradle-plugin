package de.kontext_e.jqassistant.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;

public class JqassistantGradlePlugin implements Plugin<Project> {
    public void apply(@NotNull Project project) {
        JqassistantPluginExtension jqassistantPluginExtension = getPluginExtension(project);
        registerTasks(project, jqassistantPluginExtension);
    }

    private JqassistantPluginExtension getPluginExtension(Project project) {
        return project.getExtensions().create("jqassistant", JqassistantPluginExtension.class);
    }

    private void registerTasks(Project project, JqassistantPluginExtension jqassistantPluginExtension) {
        registerJQAInstallTask(project, jqassistantPluginExtension);
        for (Tasks task : Tasks.values()) {
            registerTask(project, jqassistantPluginExtension, task.toString());
        }
    }

    private void registerJQAInstallTask(Project project, JqassistantPluginExtension jqassistantPluginExtension) {
        project.getTasks().create("installJQA", JqassistantInstall.class, jqassistant -> {
            jqassistant.setStandardInput(System.in);
            jqassistant.setStandardOutput(System.out);
            jqassistant.setErrorOutput(System.err);

            //Set Gradle Information
            jqassistant.setExtension(jqassistantPluginExtension);
            jqassistant.setGroup("jQAssistant");
            jqassistant.setDescription("Install JQA into configured directory");
        });
    }

    private void registerTask(Project project, JqassistantPluginExtension jqassistantPluginExtension, String name) {
        project.getTasks().create(name, Jqassistant.class, jqassistant -> {

            //Set I/O
            jqassistant.setStandardInput(System.in);
            jqassistant.setStandardOutput(System.out);
            jqassistant.setErrorOutput(System.err);

            //Set Gradle Information
            jqassistant.setExtension(jqassistantPluginExtension);
            jqassistant.setGroup("jQAssistant");
            jqassistant.setDescription(format("Executes jQAssistant task '%s'.", name));

            //TODO
            jqassistant.addTaskName(name);
        });
    }
}
