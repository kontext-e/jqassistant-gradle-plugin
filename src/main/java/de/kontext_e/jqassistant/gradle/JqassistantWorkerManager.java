package de.kontext_e.jqassistant.gradle;

import org.gradle.api.file.FileCollection;
import org.gradle.process.internal.JavaExecHandleBuilder;
import org.gradle.process.internal.worker.SingleRequestWorkerProcessBuilder;
import org.gradle.process.internal.worker.WorkerProcessException;
import org.gradle.process.internal.worker.WorkerProcessFactory;

import java.io.File;
import java.util.Arrays;

public class JqassistantWorkerManager {

    public JqassistantResult runWorker(File workingDir, WorkerProcessFactory workerFactory, FileCollection classpath, JqassistantSpec spec) {
        try {
            JqassistantWorker worker = createWorkerProcess(workingDir, workerFactory, classpath, spec);
            return worker.run(spec);
        } catch (WorkerProcessException e) {
            e.printStackTrace();
            return new JqassistantResult();
        }
    }

    private JqassistantWorker createWorkerProcess(File workingDir, WorkerProcessFactory workerFactory, FileCollection classpath, JqassistantSpec spec) {
        SingleRequestWorkerProcessBuilder<JqassistantWorker> builder = workerFactory.singleRequestWorker(JqassistantWorker.class, JqassistantExecutor.class);
        builder.setBaseName("Gradle jQAssistant Worker");
        builder.applicationClasspath(classpath);
        builder.sharedPackages(Arrays.asList("com.buschmais.jqassistant"));
        JavaExecHandleBuilder javaCommand = builder.getJavaCommand();
        javaCommand.setWorkingDir(workingDir);
        javaCommand.setDefaultCharacterEncoding("UTF-8");
        javaCommand.setIgnoreExitValue(true);

        // here the standardInput is set
        javaCommand.setStandardInput(System.in);
        javaCommand.setStandardOutput(System.out);
        javaCommand.setErrorOutput(System.err);

        return builder.build();
    }
}
