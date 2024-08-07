package de.kontext_e.jqassistant.gradle;

import groovy.lang.GString;
import org.gradle.api.JavaVersion;
import org.gradle.api.tasks.options.Option;

import java.util.*;

/**
 * Enables configuration of Plugin through new, custom gradle 'jqassistant'-block
 * Setters expose fields to gradle
 */
public class JqassistantPluginExtension {

    private String toolVersion = "2.1.0";
    private int neo4jVersion = 0;
    private List<String> options = new ArrayList<>();
    private final List<String> plugins = new ArrayList<>();
    private final List<String> scanDirs = new ArrayList<>();
    private final List<String> configFiles = new ArrayList<>();

    /* Getters */

    public String getToolVersion() {
        return toolVersion;
    }

    public int getNeo4jVersion(){
        //If not specified, use the latest compatible with current java version
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

    private static int getJavaVersion() {
        return Integer.parseInt(JavaVersion.current().getMajorVersion());
    }

    public Collection<String> getPlugins() {
        return Collections.unmodifiableList(plugins);
    }

    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public List<String> getScanDirs() {
        return Collections.unmodifiableList(scanDirs);
    }

    public List<String> getConfigFiles(){
        return Collections.unmodifiableList(configFiles);
    }

    /* Setters */


    public void setToolVersion(String toolVersion) {
        this.toolVersion = toolVersion;
    }

    public void setNeo4jVersion(Integer neo4jVersion){
        this.neo4jVersion = neo4jVersion;
    }

    public void setConfigFile(String configFileLocation){
        this.configFiles.add(configFileLocation);
    }

    /**
     * Adds plugins to jQAssistant
     * @deprecated Use Config File instead.
     * Specify location of Config File by using "config" property.
     * Default location is jqassistant/.jqassistant.yml
     * @see <a href="https://jqassistant.github.io/jqassistant/doc/2.1.0/#_yaml_files">jQA User Manual</a>
     *
     * @param plugin The plugin to be added to jQA using the gradle dependency syntax
     */
    @Deprecated
    public void setPlugin(String plugin){
        fillInto(new String[] {plugin}, plugins);
    }

    @Deprecated
    @Option(option = "args", description = "Command line arguments passed to the main class.")
    public JqassistantPluginExtension setArgsString(String args) {
        return setOptions(Arrays.asList(args.split(" ")));
    }

    /**
     * Sets misc Commandline options for jQAssistant
     * @deprecated Use Config File instead.
     * Specify location of Config File by using "config" property.
     * Default location is jqassistant/.jqassistant.yml
     * @see <a href="https://jqassistant.github.io/jqassistant/doc/2.1.0/#_yaml_files">jQA User Manual</a>
     *
     * @param applicationArgs The Arguments for JQAssistant
     */
    @Deprecated
    public JqassistantPluginExtension setOptions(List<String> applicationArgs) {
        this.options = applicationArgs;
        return this;
    }

    /* Gradle DSL Setters */

    public void configFile(Object... args){
        fillInto(args, configFiles);
    }

    public void neo4jVersion(int neo4jVersion){
        this.neo4jVersion = neo4jVersion;
    }

    /**
     * Adds plugins to jQAssistant
     * @deprecated Use Config File instead.
     * Specify location of Config File by using "config" property.
     * Default location is jqassistant/.jqassistant.yml
     * @see <a href="https://jqassistant.github.io/jqassistant/doc/2.1.0/#_yaml_files">jQA User Manual</a>
     *
     * @param args The plugin(s) to be added to jQA using the gradle dependency syntax
     */
    @Deprecated
    public void plugin(Object... args) {
        fillInto(args, plugins);
    }

    /**
     * Declares what directories jQAssistant should scan. Java sources are automatically added and don't need to be declared explicitly.
     * @deprecated Use Config File instead.
     * Specify location of Config File by using "config" property.
     * Default location is jqassistant/.jqassistant.yml
     * <b>If config file is set, this option won't work, and java sources are not added automatically!</b>
     * @see <a href="https://jqassistant.github.io/jqassistant/doc/2.1.0/#_yaml_files">jQA User Manual</a>
     *
     * @param args the (list of) directories to be scanned
     */
    @Deprecated
    public void scanDir(Object... args) {
        fillInto(args, scanDirs);
    }

    /**
     * Sets misc Commandline options for jQAssistant
     * @deprecated Use Config File instead.
     * Specify location of Config File by using "config" property.
     * Default location is jqassistant/.jqassistant.yml
     * @see <a href="https://jqassistant.github.io/jqassistant/doc/2.1.0/#_yaml_files">jQA User Manual</a>
     *
     * @param args The Arguments for JQAssistant
     */
    @Deprecated
    public void options(Object... args) {
        fillInto(args, options);
    }

    private void fillInto(Object[] args, List<String> target) {
        for (Object arg : args) {
            if(arg instanceof String) {
                target.add((String) arg);
            } else if(arg instanceof GString) {
                target.add(arg.toString());
            } else {
                System.out.println("Unknown type: "+arg.getClass().getName());
            }
        }
    }
}
