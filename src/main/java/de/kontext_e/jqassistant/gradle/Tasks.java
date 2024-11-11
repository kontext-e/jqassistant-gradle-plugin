package de.kontext_e.jqassistant.gradle;

public enum Tasks {

    //TODO: Install Task
    RESET, SCAN, ANALYZE, REPORT, SERVER,
    AVAILABLE_SCOPES, AVAILABLE_RULES, EFFECTIVE_RULES, EFFECTIVE_CONFIGURATION, LIST_PLUGINS;

    @Override
    public String toString() {
        return name().toLowerCase().replace('_', '-');
    }
}