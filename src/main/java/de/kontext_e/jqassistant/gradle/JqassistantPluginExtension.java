package de.kontext_e.jqassistant.gradle;

import groovy.lang.GString;
import org.gradle.api.tasks.options.Option;

import java.util.*;

public class JqassistantPluginExtension {
    private String toolVersion = "1.9.1";
    private List<String> plugins = new ArrayList<>();
    private List<String> options = new ArrayList<>();
    private List<String> scanDirs = new ArrayList<>();

    public String getToolVersion() {
        return toolVersion;
    }

    public void setToolVersion(String toolVersion) {
        this.toolVersion = toolVersion;
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

    @Option(option = "args", description = "Command line arguments passed to the main class.")
    public JqassistantPluginExtension setArgsString(String args) {
        return setOptions(Arrays.asList(args.split(" ")));
    }

    public JqassistantPluginExtension setOptions(List<String> applicationArgs) {
        this.options = applicationArgs;
        return this;
    }

    public JqassistantPluginExtension plugins(Object... args) {
        fillInto(args, plugins);
        return this;
    }

    public JqassistantPluginExtension scanDirs(Object... args) {
        fillInto(args, scanDirs);
        return this;
    }

    public JqassistantPluginExtension options(Object... args) {
        fillInto(args, options);
        return this;
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
