package org.bullithulli.feature.sandbox;

import org.junit.Test;

import java.io.File;
import java.util.Objects;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class SandboxAnalyzeHeuristicTest {

    private String getTestResourcePath(String folder) {
        return Objects.requireNonNull(getClass().getClassLoader().getResource("sandboxtests/" + folder)).getPath();
    }

    @Test
    public void testLinearVisualNovelDetection() throws Exception {
        KNModSandbox sandbox = new KNModSandbox();
        // grouping_logic only has 1 screen, so it should be detected as Linear VN
        String output = sandbox.analyze(getTestResourcePath("grouping_logic"));

        assertTrue("Output should detect Linear VN", output.contains("Result: LINEAR VISUAL NOVEL"));
        assertTrue("Output should suggest standard KNMOD", output.contains("You probably do NOT need KNMOD_SANDBOX"));
        assertFalse("Output should not detect Sandbox", output.contains("Result: SANDBOX / NON-LINEAR GAME"));
    }

    @Test
    public void testSandboxGameDetection() throws Exception {
        KNModSandbox sandbox = new KNModSandbox();
        // heuristic_sandbox has 5 screens with Jump actions, so it should be detected as Sandbox
        String output = sandbox.analyze(getTestResourcePath("heuristic_sandbox"));

        assertTrue("Output should detect Sandbox", output.contains("Result: SANDBOX / NON-LINEAR GAME"));
        assertTrue("Output should mention interactive screens", output.contains("interactive map/menu screens"));
        assertFalse("Output should not suggest standard KNMOD", output.contains("You probably do NOT need KNMOD_SANDBOX"));
    }
}
