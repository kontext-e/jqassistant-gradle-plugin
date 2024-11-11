package de.kontext_e.jqassistant.gradle;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskAction;

public class Jqassistant extends Exec {
    private static final Logger LOGGER = Logging.getLogger(Jqassistant.class);

    private String taskName = "help";
    private JqassistantPluginExtension extension;

    public Jqassistant() {}

    @Override
    @TaskAction
    public void exec() {
        commandLine(extension.getInstallLocation() + "/bin/jqassistant.cmd", taskName);
        super.exec();
    }

    /* Setters */

    void addTaskName(String taskName) {
        this.taskName = taskName;
    }

    void setExtension(JqassistantPluginExtension extension) {
        this.extension = extension;
    }

}
