package de.kontext_e.jqassistant.gradle;

import org.gradle.api.JavaVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enables configuration of Plugin through new, custom gradle 'jqassistant'-block
 * Setters expose fields to Gradle
 */
public class JqassistantPluginExtension {

    private String toolVersion = "2.5.0";
    private String installLocation = "./jqassistant";
    private int neo4jVersion = 0;
    private final List<String> options = new ArrayList<>();
    private final List<String> scanDirs = new ArrayList<>();
    private final List<String> configFiles = new ArrayList<>();
    private final List<String> scanUrls = new ArrayList<>();

    /* Getters */

    public String getToolVersion() {
        return toolVersion;
    }

    public String getInstallLocation() {
        return installLocation;
    }

    private static int getJavaVersion() {
        return Integer.parseInt(JavaVersion.current().getMajorVersion());
    }

    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public List<String> getScanDirs() {
        return Collections.unmodifiableList(scanDirs);
    }

    public List<String> getScanUrls() {
        return Collections.unmodifiableList(scanUrls);
    }

    public List<String> getConfigFiles(){
        return Collections.unmodifiableList(configFiles);
    }

    public int getNeo4jVersion(){
        //If not specified, use the latest compatible with the current java version
        if (neo4jVersion == 0) {
            System.out.println("No Neo4J Version specified. Selecting the latest compatible with current Java version...");
            return getJavaVersion() >= 17 ? 5 : 4;
        }

        if (neo4jVersion != 4 && neo4jVersion != 5){
            System.out.println("No valid Neo4J Version was given. Available are: 4, 5");
            System.out.println("Falling back to latest compatible version");
            return getJavaVersion() >= 17 ? 5 : 4;
        }

        if (neo4jVersion == 5 &&  getJavaVersion() < 17){
            System.out.println("Selected Neo4J Version (5) is incompatible with Java < 17");
            System.out.println("Falling back to neo4J Version 4...");
            return 4;
        }

        if (neo4jVersion == 4 && getJavaVersion() > 17){
            System.out.println("Selected Neo4J Version (4) is incompatible with Java > 17");
            System.out.println("Falling back to neo4J Version 5...");
            return 5;
        }

        return neo4jVersion;
    }

    /* Setters */

    /**
     * Set the desired jQAssistant version for installation.
     * Works for 2.1.0+
     *
     * @param toolVersion the desired jQAssistant version.
     */
    public void setToolVersion(String toolVersion) {
        this.toolVersion = toolVersion;
    }

    /**
     * Sets the desired neo4j version, available are: 4 or 5.
     * Favors compatibility with the java version over user choice.
     *
     * @param neo4jVersion the desired neo4j version of jQAssistant
     */
    public void setNeo4jVersion(Integer neo4jVersion){
        this.neo4jVersion = neo4jVersion;
    }

    /**
     * Sets the installation location of jQAssistant for this project.
     * If jQA is not already installed there, it can be done so using the gradle installJQA task.
     * If there is already jQAssistant installed at the given location, the installJQA task can be skipped and the existing installation will be used.
     *
     * @param installLocation the location where jQAssistant is/will be installed.
     */
    public void setInstallLocation(String installLocation) {
        this.installLocation = installLocation;
    }

    /**
     * Adds a Config File to be read by jQAssistant.
     * Default location is installationDirectory + /.jqassistant.yml
     * @see <a href="https://jqassistant.github.io/jqassistant/doc/2.1.0/#_yaml_files">jQA User Manual</a>
     *
     * @param configFileLocation the location of the config file.
     */
    public void setConfigFile(String... configFileLocation){
        this.configFiles.addAll(List.of(configFileLocation));
    }

    /**
     * Sets misc Commandline options for jQAssistant
     * @deprecated Use Config File instead.
     * @see <a href="https://jqassistant.github.io/jqassistant/doc/2.1.0/#_yaml_files">jQA User Manual</a>
     *
     * @param option The Arguments for JQAssistant
     */
    @Deprecated
    public void setOption(String... option){
        this.options.addAll(List.of(option));
    }

    /**
     * Declares what directories jQAssistant should scan.
     * @deprecated Use Config File instead.
     * <b>If config file is set, this option won't work, and java sources are not added automatically!</b>
     * @see <a href="https://jqassistant.github.io/jqassistant/doc/2.1.0/#_yaml_files">jQA User Manual</a>
     *
     * @param dir the (list of) directories to be scanned
     */
    @Deprecated
    public void setScanDir(String... dir) {
        scanDirs.addAll(List.of(dir));
    }

    /**
     * Declares what urls jQAssistant should scan.
     * @deprecated Use Config File instead.
     * <b>If config file is set, this option won't work, and java sources are not added automatically!</b>
     * @see <a href="https://jqassistant.github.io/jqassistant/doc/2.1.0/#_yaml_files">jQA User Manual</a>
     *
     * @param url the (list of) directories to be scanned
     */
    @Deprecated
    public void setScanUrl(String... url) {
        scanUrls.addAll(List.of(url));
    }
}
