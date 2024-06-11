package de.kontext_e.jqassistant.gradle;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Jqassistant extends JavaExec {
    private static final Logger LOGGER = Logging.getLogger(Jqassistant.class);

    private final List<String> args = new ArrayList<>();
    private JqassistantPluginExtension extension;
    private Project projectToScan;

    public Jqassistant() {
    }

    @Override
    @TaskAction
    public void exec() {
        if (getJavaMajorVersion() > 11) {
            makeJQAExecutableWithJava11();
        }

        addDefaultScanDirectoriesToExtension();
        addArgumentsFromJQAPluginExtension();
        super.exec();
    }

    private static int getJavaMajorVersion() {
        return Integer.parseInt(JavaVersion.current().getMajorVersion());
    }

    private void makeJQAExecutableWithJava11() {
        setJvmArgs(List.of(
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
            "--add-opens=java.base/java.io=ALL-UNNAMED",
            "--add-opens=java.base/java.nio=ALL-UNNAMED"
        ));
    }

    /**
     * Adds java sources to directories to be scanned, does not work, when config file is active
     */
    private void addDefaultScanDirectoriesToExtension() {
        if(projectToScan == null) return;

        addProjectSourcesToScanDirectories(projectToScan);
        for (Project subproject : projectToScan.getSubprojects()) {
            addProjectSourcesToScanDirectories(subproject);
        }

    }

    @SuppressWarnings("deprecation")
    private void addProjectSourcesToScanDirectories(Project rootProject) {
        try {
            //necessary for compatibility with gradle 6.9-8.8; for gradle 7.2-9.0+ convention-->Extension
            final JavaPluginConvention javaPluginConvention = rootProject.getConvention().getPlugin(JavaPluginConvention.class);
            if (!javaPluginConvention.getSourceSets().isEmpty()) {
                addSourceSetScanDirectoriesToExtension(javaPluginConvention);
            }
        } catch (Exception e) {
            LOGGER.error("Error while adding scan directories from convention: "+e, e);
        }
    }

    @SuppressWarnings("deprecation")
    private void addSourceSetScanDirectoriesToExtension(JavaPluginConvention javaPluginConvention) {
        for (SourceSet sourceSet : javaPluginConvention.getSourceSets()) {
            FileCollection presentClassDirectories = sourceSet.getOutput().getClassesDirs().filter(File::exists);
            for (File asPath : presentClassDirectories.getFiles()) {
                extension.scanDir("java:classpath::" + asPath.getAbsolutePath());
            }
        }
    }

    private void addArgumentsFromJQAPluginExtension() {
        args(createSpec().getArgs());
    }

    private JqassistantSpec createSpec() {
        final JqassistantSpec jqassistantSpec = new JqassistantSpec();

        addMiscArgumentsFromExtensionToSpec(jqassistantSpec);
        addConfigFilesFromExtensionToSpec(jqassistantSpec);
        addScanDirectoriesFromExtensionToSpec(jqassistantSpec);

        return jqassistantSpec;
    }

    private void addConfigFilesFromExtensionToSpec(JqassistantSpec jqassistantSpec) {
        jqassistantSpec.addArgs("-C");
        jqassistantSpec.addArgs("jqassistant/.jqassistant.yml");
        jqassistantSpec.addArgs(extension.getConfigFiles());
    }

    private void addScanDirectoriesFromExtensionToSpec(JqassistantSpec jqassistantSpec) {
        if (!extension.getScanDirs().isEmpty()) {
            jqassistantSpec.addArgs("-f");
            jqassistantSpec.addArgs(extension.getScanDirs());
        }
    }

    private void addMiscArgumentsFromExtensionToSpec(JqassistantSpec jqassistantSpec) {
        jqassistantSpec.addArgs(args);
        jqassistantSpec.addArgs(extension.getOptions());
    }

    /* Setters */

    void addArg(String arg) {
        args.add(arg);
    }

    void setExtension(JqassistantPluginExtension extension) {
        this.extension = extension;
    }

    void projectToScan(Project rootProject) {
        this.projectToScan = rootProject;
    }
}
