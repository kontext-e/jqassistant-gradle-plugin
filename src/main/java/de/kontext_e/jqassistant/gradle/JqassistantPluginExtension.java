package de.kontext_e.jqassistant.gradle;

import org.gradle.api.tasks.options.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class JqassistantPluginExtension {
    private String toolVersion = "1.7.0";
    private Collection<String> plugins;
    private List<String> args = new ArrayList<>();

    public String getToolVersion() {
        return toolVersion;
    }

    public void setToolVersion(String toolVersion) {
        this.toolVersion = toolVersion;
    }

    public Collection<String> getPlugins() {
        return plugins;
    }

    public void setPlugins(Collection<String> plugins) {
        this.plugins = plugins;
    }

    public List<String> getArgs() {
        return args;
    }

    @Option(option = "args", description = "Command line arguments passed to the main class.")
    public JqassistantPluginExtension setArgsString(String args) {
        return setArgs(Arrays.asList(args.split(" ")));
    }

    public JqassistantPluginExtension setArgs(List<String> applicationArgs) {
        this.args = applicationArgs;
        return this;
    }

    public JqassistantPluginExtension scanDirs(Object... args) {
        return this;
    }

    public JqassistantPluginExtension args(Object... args) {
        for (Object arg : args) {
            if(arg instanceof String) {
                this.args.add((String) arg);
            }
        }

        return this;
    }
}
