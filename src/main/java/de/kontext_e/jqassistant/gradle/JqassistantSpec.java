package de.kontext_e.jqassistant.gradle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Acts as sort of data object, which allows for collection of all arguments passed to jqa
 */
public class JqassistantSpec implements Serializable {
    private final List<String> args = new ArrayList<>();

    public List<String> getArgs() {
        return args;
    }

    public void addArgs(List<String> args) {
        this.args.addAll(args);
    }

    public void addArgs(String... args) {
        this.args.addAll(asList(args));
    }
}
