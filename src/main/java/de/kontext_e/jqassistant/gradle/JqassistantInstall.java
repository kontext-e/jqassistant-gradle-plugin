package de.kontext_e.jqassistant.gradle;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JqassistantInstall extends Exec {

    private static final Logger logger = Logging.getLogger(JqassistantInstall.class);
    private static final String URL = "https://repo1.maven.org/maven2/com/buschmais/jqassistant/cli/jqassistant-commandline-neo4jv%s/%s/jqassistant-commandline-neo4jv%s-%s-distribution.zip";
    private JqassistantPluginExtension extension;

    public JqassistantInstall() {}

    @Override
    @TaskAction
    public void exec() {
        File installationDirectory = new File(getProject().getRootDir() + extension.getInstallLocation());
        if (!installationDirectory.exists()) {
            commandLine("mkdir -p" + installationDirectory.getAbsolutePath());
        }

        int neo4jVersion = extension.getNeo4jVersion();
        String toolVersion = extension.getToolVersion();
        String jqaURL = String.format(URL, neo4jVersion, toolVersion, neo4jVersion, toolVersion);
        try (InputStream inputStream = new URL(jqaURL).openStream()){
            Files.copy(inputStream, Paths.get(installationDirectory.getAbsolutePath(), "jqassistant.zip"));
        } catch (IOException e) {
            logger.error("Error while downloading jQAssistant: {}", e.getMessage());
            throw new RuntimeException(e);
        }



    }

    public void setExtension(JqassistantPluginExtension jqassistantPluginExtension) {
        this.extension = jqassistantPluginExtension;
    }
}
