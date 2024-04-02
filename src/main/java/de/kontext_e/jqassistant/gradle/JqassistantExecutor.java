package de.kontext_e.jqassistant.gradle;

import com.buschmais.jqassistant.commandline.Main;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.util.concurrent.CountDownLatch;

public class JqassistantExecutor implements JqassistantWorker {
    private static final Logger LOGGER = Logging.getLogger(JqassistantExecutor.class);

    public static final CountDownLatch LATCH = new CountDownLatch(1);

    @Override
    public JqassistantResult run(JqassistantSpec spec) {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            LOGGER.debug("Executing jQAssistant with properties: {}", System.getProperties());
            Main main = new Main();
            main.run(spec.getArgs().toArray(new String[0]));
        } catch (Exception e) {
            LOGGER.error("Error while executing jQAssistant: " + e, e);
        }

        return new JqassistantResult();

    }
}
