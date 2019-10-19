package de.kontext_e.jqassistant.gradle;

import java.io.Serializable;

public class JqassistantSpec implements Serializable {
    private String[] args;

    public String[] getArgs() {
        return args;
    }

    public JqassistantSpec withArgs(String[] args) {
        this.args = args;
        return this;
    }
}
