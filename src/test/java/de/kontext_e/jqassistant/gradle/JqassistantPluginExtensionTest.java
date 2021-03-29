package de.kontext_e.jqassistant.gradle;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class JqassistantPluginExtensionTest {

    @Test
    void thatOnlyStringOptionsWereSet() {
        JqassistantPluginExtension extension = new JqassistantPluginExtension();

        extension.options(1, "option", true);

        assertEquals(1, extension.getOptions().size());
        assertEquals("option", extension.getOptions().get(0));
    }

    @Test
    void applyPluginsRegisterScanTask() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("de.kontext_e.jqassistant.gradle");

        assertTrue(project.getTasks().getByName("scan") instanceof Jqassistant);
    }

    @Test
    void scanAndAnalyzeExecuted() {
        File testProjectDir = new File("./testProject");
        File expectedAnalyzeReportFile = new File("./testProject/jqassistant/report/jqassistant-report.xml");
        if (expectedAnalyzeReportFile.exists()) {
            assertTrue(expectedAnalyzeReportFile.delete(), "pre condition");
        }

        BuildResult result = GradleRunner.create()
          .withProjectDir(testProjectDir)
          .withArguments("scan", "analyze")
          .build();

        BuildTask scanTask = result.task(":scan");
        assertNotNull(scanTask);
        assertEquals(TaskOutcome.SUCCESS, scanTask.getOutcome());

        BuildTask analyzeTask = result.task(":analyze");
        assertNotNull(analyzeTask);
        assertEquals(TaskOutcome.SUCCESS, analyzeTask.getOutcome());
        assertTrue(expectedAnalyzeReportFile.exists());
    }
}
