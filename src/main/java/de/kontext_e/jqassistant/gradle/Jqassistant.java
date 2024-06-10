package de.kontext_e.jqassistant.gradle;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.JavaPluginExtension;
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
        if (Integer.parseInt(JavaVersion.current().getMajorVersion()) > 11) {
            setJvmArgs(List.of(
                    "--add-opens=java.base/java.lang=ALL-UNNAMED",
                    "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
                    "--add-opens=java.base/java.io=ALL-UNNAMED",
                    "--add-opens=java.base/java.nio=ALL-UNNAMED"
            ));
        }
        args(createSpec().getArgs());
        super.exec();
    }

    private void addDefaultScanDirs() {
        if(projectToScan != null) {
            addConventionScanDirs(projectToScan);

            for (Project subproject : projectToScan.getSubprojects()) {
                addConventionScanDirs(subproject);
            }
        }
    }

    private void addConventionScanDirs(Project rootProject) {
        try {
            //necessary for compatibility with gradle 6.9-8.8; for gradle 7.2-9.0+ convention-->Extension
            final JavaPluginConvention javaPluginConvention = rootProject.getConvention().getPlugin(JavaPluginConvention.class);
            if (javaPluginConvention != null && !javaPluginConvention.getSourceSets().isEmpty()) {
                for (SourceSet sourceSet : javaPluginConvention.getSourceSets()) {
                    FileCollection presentClassDirs = sourceSet.getOutput().getClassesDirs().filter(File::exists);
                    for (File asPath : presentClassDirs.getFiles()) {
                        extension.scanDirs("java:classpath::" + asPath.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while adding scan directories from convention: "+e, e);
        }
    }

    private JqassistantSpec createSpec() {
        final JqassistantSpec jqassistantSpec = new JqassistantSpec();

        jqassistantSpec.addArgs(args);
        jqassistantSpec.addArgs(extension.getOptions());

        if (!extension.getScanDirs().isEmpty()) {
            jqassistantSpec.addArgs("-f");
            jqassistantSpec.addArgs(extension.getScanDirs());
        }
        return jqassistantSpec;
    }

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
