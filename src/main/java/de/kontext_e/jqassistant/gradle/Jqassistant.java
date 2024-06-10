package de.kontext_e.jqassistant.gradle;

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
        //Necessary to use neo4Jv4 with Java 17 and above
        //Problem is: "unkown option '--add-opens'" although it works on commandline
//        setJvmArgs(List.of(
//                "--add-opens java.base/java.lang=ALL-UNNAMED",
//                "--add-opens java.base/sun.nio.ch=ALL-UNNAMED",
//                "--add-opens java.base/java.io=ALL-UNNAMED"
//        ));
        setStandardInput(System.in);
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
            final JavaPluginExtension javaPluginExtension = rootProject.getExtensions().getByType(JavaPluginExtension.class);
            if (!javaPluginExtension.getSourceSets().isEmpty()) {
                for (SourceSet sourceSet : javaPluginExtension.getSourceSets()) {
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
