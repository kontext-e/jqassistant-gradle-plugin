package de.kontext_e.jqassistant.gradle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JqassistantPluginExtensionTest {

    @Test
    void thatOnlyStringOptionsWereSet() {
        JqassistantPluginExtension extension = new JqassistantPluginExtension();

        extension.options(1, "option", true);

        assertEquals(1, extension.getOptions().size());
        assertEquals("option", extension.getOptions().get(0));
    }
}
