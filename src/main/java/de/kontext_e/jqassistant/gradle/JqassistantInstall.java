package de.kontext_e.jqassistant.gradle;

import net.lingala.zip4j.ZipFile;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
            installationDirectory.mkdirs();
        }

        if (new File(getProject().getRootDir() + extension.getInstallLocation() + "/bin").exists()) {
            logger.warn("JQA appears to be already installed. If you want to upgrade to a newer jqassistant version, please delete the installation directory.");
            return;
        }

        int neo4jVersion = extension.getNeo4jVersion();
        Path zipFile = downloadJQA(installationDirectory, neo4jVersion);
        extractJQA(zipFile, installationDirectory, neo4jVersion);
    }

    private @NotNull Path downloadJQA(File installationDirectory, int neo4jVersion) {
        String toolVersion = extension.getToolVersion();
        String jqaURL = String.format(URL, neo4jVersion, toolVersion, neo4jVersion, toolVersion);
        Path zipFile = Paths.get(installationDirectory.getAbsolutePath(), "jqassistant.zip");
        try (InputStream inputStream = new URL(jqaURL).openStream()){
            Files.copy(inputStream, zipFile);
        } catch (IOException e) {
            logger.error("Error while downloading jQAssistant: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return zipFile;
    }

    private void extractJQA(Path zipFile, File installationDirectory, int neo4jVersion) {
        try (ZipFile file = new ZipFile(zipFile.toFile())){
            String zipParentDirectory = String.format("jqassistant-commandline-neo4jv%s-%s/", neo4jVersion, extension.getToolVersion());
            file.extractFile(zipParentDirectory + "bin/", installationDirectory.getPath(), "/bin");
            file.extractFile(zipParentDirectory + "lib/", installationDirectory.getPath(), "/lib");
        } catch (IOException e) {
            logger.error("Error while extracting jQAssistant: {}", e.getMessage());
            new File(zipFile.toUri()).delete();
            throw new RuntimeException(e);
        }
        new File(zipFile.toUri()).delete();
    }

    public void setExtension(JqassistantPluginExtension jqassistantPluginExtension) {
        this.extension = jqassistantPluginExtension;
    }
}
