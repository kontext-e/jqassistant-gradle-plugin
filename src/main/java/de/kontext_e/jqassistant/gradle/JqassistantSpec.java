package de.kontext_e.jqassistant.gradle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JqassistantSpec implements Serializable {
    private List<String> args = new ArrayList<>();

    public List<String> getArgs() {
        return args;
    }

    public JqassistantSpec addArgs(List<String> args) {
        this.args.addAll(args);
        return this;
    }
}
