package de.kontext_e.jqassistant.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.process.internal.worker.WorkerProcessFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class Jqassistant extends DefaultTask {
    private final ConfigurableFileCollection dataFiles;
    private List<String> args = new ArrayList<>();
    private JqassistantPluginExtension extension;

    public Jqassistant() {
        dataFiles = getProject().files();
    }

    @InputFiles
    public FileCollection getDataFiles() {
        return dataFiles;
    }

    public void setDataFiles(FileCollection dataFiles) {
        this.dataFiles.setFrom(dataFiles);
    }

    @Inject
    public Instantiator getInstantiator() {
        throw new UnsupportedOperationException();
    }

    @Inject
    public WorkerProcessFactory getWorkerProcessBuilderFactory() {
        throw new UnsupportedOperationException();
    }

    @TaskAction
    public void process() {
        JqassistantWorkerManager manager = new JqassistantWorkerManager();
        JqassistantResult result = manager.runWorker(getProject().getProjectDir(), getWorkerProcessBuilderFactory(), getDataFiles(), createSpec());
    }

    private JqassistantSpec createSpec() {
        System.out.println("Args: "+extension.getArgs());
        return new JqassistantSpec()
                .addArgs(args)
                .addArgs(extension.getArgs())
                ;
    }

    public void addArg(String arg) {
        args.add(arg);
    }

    public void setExtension(JqassistantPluginExtension extension) {
        this.extension = extension;
    }

    public JqassistantPluginExtension getExtension() {
        return extension;
    }
}
