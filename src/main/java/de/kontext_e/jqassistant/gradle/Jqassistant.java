package de.kontext_e.jqassistant.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.internal.worker.WorkerProcessFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Jqassistant extends DefaultTask {
    private static final Logger LOGGER = Logging.getLogger(Jqassistant.class);

    private final ConfigurableFileCollection classpath;
    private final List<String> args = new ArrayList<>();
    private JqassistantPluginExtension extension;
    private Project projectToScan;

    public Jqassistant() {
        classpath = getProject().files();
    }

    @InputFiles
    public FileCollection getClasspath() {
        return classpath;
    }

    public void setClasspath(FileCollection classpath) {
        this.classpath.setFrom(classpath);
    }

    @Inject
    public WorkerProcessFactory getWorkerProcessBuilderFactory() {
        throw new UnsupportedOperationException();
    }

    @TaskAction
    public void process() {
        addDefaultScanDirs();

        JqassistantWorkerManager manager = new JqassistantWorkerManager();
        manager.runWorker(getProject().getProjectDir(), getWorkerProcessBuilderFactory(), getClasspath(), createSpec());
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
        final JqassistantSpec jqassistantSpec = new JqassistantSpec()
                .addArgs(args)
                .addArgs(extension.getOptions());

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
