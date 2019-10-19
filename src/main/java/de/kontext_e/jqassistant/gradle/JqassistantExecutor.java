package de.kontext_e.jqassistant.gradle;

import com.buschmais.jqassistant.commandline.CliExecutionException;
import com.buschmais.jqassistant.commandline.Main;
import com.buschmais.jqassistant.commandline.Task;
import com.buschmais.jqassistant.commandline.task.DefaultTaskFactoryImpl;
import com.buschmais.jqassistant.commandline.task.ServerTask;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.core.store.impl.EmbeddedGraphStore;
import com.buschmais.jqassistant.neo4j.backend.bootstrap.EmbeddedNeo4jServer;

import java.util.concurrent.CountDownLatch;

public class JqassistantExecutor implements JqassistantWorker {
    public static final CountDownLatch LATCH = new CountDownLatch(1);

    @Override
    public JqassistantResult run(JqassistantSpec spec) {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(DefaultTaskFactoryImpl.class.getClassLoader());

            DefaultTaskFactoryImpl taskFactory = new DefaultTaskFactoryImpl() {
                @Override
                public Task fromName(String name) throws CliExecutionException {
                    if("server".equalsIgnoreCase(name)) {
                        return new ServerTask() {
                            @Override
                            protected void executeTask(Store store) throws CliExecutionException {
                                EmbeddedGraphStore embeddedGraphStore = (EmbeddedGraphStore) store;
                                EmbeddedNeo4jServer server = embeddedGraphStore.getServer();
                                server.start();
                                System.out.println("Server started, to stop press CTRL-C");
                                try {
                                    LATCH.await();
                                    System.out.println("Stopping server....");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                server.stop();
                            }
                        };
                    }
                    return super.fromName(name);
                }
            };
            try {
                Main main = new Main(taskFactory);
                main.run(spec.getArgs().toArray(new String[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return new JqassistantResult();
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }

    }
}
