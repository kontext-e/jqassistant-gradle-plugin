package de.kontext_e.jqassistant.gradle;

import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.StringJoiner;

public class Jqassistant extends Exec {

    private String taskName = "help";
    private JqassistantPluginExtension extension;

    public Jqassistant() {}

    @Override
    @TaskAction
    public void exec() {
        addDefaultScanDirectoriesToExtension(getProject());

        String installLocation;
        if (new File(extension.getInstallLocation()).isAbsolute()){
            installLocation = extension.getInstallLocation();
        } else {
            installLocation = getProject().getProjectDir() + extension.getInstallLocation();
        }

        StringJoiner command = new StringJoiner(" ");
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            command.add(installLocation + "/bin/jqassistant.cmd");
        } else {
            command.add(installLocation +"./bin/jqassistant.sh");
        }

        command.add(taskName);

        if (!extension.getConfigFiles().isEmpty()) {
            command.add("-C");
            extension.getConfigFiles().forEach(command::add);
        }

        if (!extension.getScanDirs().isEmpty()) {
            command.add("-f");
            extension.getScanDirs().forEach(command::add);
        }

        if (!extension.getScanUrls().isEmpty()){
            command.add("-u");
            extension.getScanUrls().forEach(command::add);
        }

        workingDir(extension.getInstallLocation());
        commandLine((Object[]) command.toString().split(" "));
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
