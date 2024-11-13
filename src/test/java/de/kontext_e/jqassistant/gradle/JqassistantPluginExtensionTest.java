package de.kontext_e.jqassistant.gradle;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class JqassistantPluginExtensionTest {

    @Test
    void thatOnlyStringOptionsWereSet() {
        JqassistantPluginExtension extension = new JqassistantPluginExtension();

        extension.setOption("option");

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
    void scanAndAnalyzeExecuted(@TempDir final Path tempDir) throws Exception {
        Path buildFile = Paths.get(getClass().getResource("build.gradle").toURI());
        Files.copy(buildFile, tempDir.resolve(buildFile.getFileName()));

        BuildResult result = GradleRunner.create()
          .withProjectDir(tempDir.toFile())
          .withPluginClasspath()
          .withArguments("scan", "analyze", "--stacktrace")
          .build();

        BuildTask scanTask = result.task(":scan");
        assertNotNull(scanTask);
        assertEquals(TaskOutcome.SUCCESS, scanTask.getOutcome());

        BuildTask analyzeTask = result.task(":analyze");
        assertNotNull(analyzeTask);
        assertEquals(TaskOutcome.SUCCESS, analyzeTask.getOutcome());
        Path expectedAnalyzeReportFile = tempDir.resolve("jqassistant/report/jqassistant-report.xml");
        assertTrue(Files.exists(expectedAnalyzeReportFile));
    }
}
