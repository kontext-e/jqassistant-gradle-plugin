package de.kontext_e.jqassistant.gradle;

import java.util.Collection;

public class JqassistantPluginExtension {
    private String toolVersion = "1.7.0";
    private Collection<String> plugins;

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
}
