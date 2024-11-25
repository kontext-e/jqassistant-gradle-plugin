package de.kontext_e.jqassistant.gradle;

import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.file.Path;
import java.util.StringJoiner;

public class Jqassistant extends Exec {

    private String taskName = "help";
    private JqassistantPluginExtension extension;

    public Jqassistant() {}

    @Override
    @TaskAction
    public void exec() {
        Project project = getProject();
        addDefaultScanDirectoriesToExtension(project);

        Path projectRoot = new File(project.getProjectDir().getAbsolutePath()).toPath();
        Path installationPath = new File(extension.getInstallLocation()).toPath();

        Path absoluteInstallationPath = projectRoot.resolve(installationPath);

        StringJoiner command = new StringJoiner(" ");
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            command.add(absoluteInstallationPath + "/bin/jqassistant.cmd");
        } else {
            command.add(absoluteInstallationPath + "/bin/jqassistant.sh");
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

        workingDir(absoluteInstallationPath);
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

    private void addDefaultScanDirectoriesToExtension(Project project) {
        addProjectSourcesToScanDirectories(project);
        for (Project subproject : project.getSubprojects()) {
            addProjectSourcesToScanDirectories(subproject);
        }
    }

    private void addProjectSourcesToScanDirectories(Project rootProject) {

        final JavaPluginExtension javaPluginExtension = rootProject.getExtensions().getByType(JavaPluginExtension.class);
        if (javaPluginExtension.getSourceSets().isEmpty()) return;

        for (SourceSet sourceSet : javaPluginExtension.getSourceSets()) {
            FileCollection presentClassDirectories = sourceSet.getOutput().getClassesDirs().filter(File::exists);
            for (File asPath : presentClassDirectories.getFiles()) {
                extension.setScanDir("java:classpath::" + asPath.getAbsolutePath());
            }
        }
    }
}
