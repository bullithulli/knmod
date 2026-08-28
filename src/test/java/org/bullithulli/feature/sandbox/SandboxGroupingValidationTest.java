package org.bullithulli.feature.sandbox;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.*;

public class SandboxGroupingValidationTest {
    @Test
    public void testAmnesiaFlattenerBugPrevention() throws Exception {
        KNModSandbox sandbox = new KNModSandbox();
        sandbox.setStartLabel("start");
        // Setting a groupBy triggers the loop over all starting labels
        sandbox.setGroupBy(java.util.Arrays.asList("FILE"));
        
        String outPath = System.getProperty("java.io.tmpdir") + "/sandbox_amnesia.rpy";
        String testDir = java.util.Objects.requireNonNull(getClass().getClassLoader().getResource("sandboxtests/amnesia_bug")).getPath();
        sandbox.assemble(testDir, outPath);

        String content = java.nio.file.Files.readString(java.nio.file.Path.of(outPath));

        // The main_story should only be physically written ONCE!
        int count = content.split("label main_story:").length - 1;
        assertEquals("Amnesia Bug! main_story was duplicated. The visited list was likely cleared between orphans.", 1, count);

        // The orphan click should correctly show that it was blocked from duplicating the story
        assertTrue("Orphan must hit the already visited marker", content.contains("[Already visited: main_story]"));

        new java.io.File(outPath).delete();
    }
    @Test
    public void testTrickyRenpyEdgeCases() throws Exception {
        KNModSandbox sandbox = new KNModSandbox();
        sandbox.setStartLabel("tricky_start");
        sandbox.setIncludeOrphans(false); // we only want to see what is natively reached!
        String outPath = System.getProperty("java.io.tmpdir") + "/sandbox_tricky.rpy";
        sandbox.assemble(getTestResourcePath(), outPath);

        String content = java.nio.file.Files.readString(java.nio.file.Path.of(outPath));

        assertTrue("Must reach python_call_target via renpy.call", content.contains("Inside python call"));
        assertTrue("Must reach from_clause_target via call ... from", content.contains("Inside from clause target"));
        assertTrue("Must reach screen_call_target via screen action Call", content.contains("Inside screen call"));

        new java.io.File(outPath).delete();
    }

    private String getTestResourcePath() {
        return Objects.requireNonNull(getClass().getClassLoader().getResource("sandboxtests/grouping_logic")).getPath();
    }

    @Test
    
    public void testScannerExtractsIfConditions() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        Map<String, SandboxScanner.LabelEntry> registry = scanner.getLabelRegistry();

        SandboxScanner.LabelEntry bathroom = registry.get("bathroom_week1");
        assertNotNull(bathroom);
        assertTrue("Should extract week variable from 'if week == 1'", bathroom.conditions.containsKey("week"));
        assertEquals("1", bathroom.conditions.get("week"));
        assertTrue("Should extract time_of_day from 'time_of_day == \"morning\"'", bathroom.conditions.containsKey("time_of_day"));
        assertEquals("morning", bathroom.conditions.get("time_of_day"));

        SandboxScanner.LabelEntry kitchenCorrupt = registry.get("kitchen_corrupt");
        assertTrue("Should extract corruption from 'if corruption >= 10'", kitchenCorrupt.conditions.containsKey("corruption"));
        assertEquals("10", kitchenCorrupt.conditions.get("corruption"));
    }

    @Test
    
    public void testScannerExtractsInlineMenuConditions() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        Map<String, SandboxScanner.LabelEntry> registry = scanner.getLabelRegistry();

        SandboxScanner.LabelEntry kitchenNormal = registry.get("kitchen_normal");
        assertNotNull(kitchenNormal);

        // Inline conditions in menus should map to the label containing them
        assertTrue("Should extract week from '\"Cook meal\" if week < 3:'", kitchenNormal.conditions.containsKey("week"));
        assertTrue("Should extract corruption from '\"Clean\" if corruption < 5:'", kitchenNormal.conditions.containsKey("corruption"));
    }

    @Test
    
    public void testScannerExtractsAssignments() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        Map<String, SandboxScanner.LabelEntry> registry = scanner.getLabelRegistry();
        SandboxScanner.LabelEntry start = registry.get("start");

        // Ensure default variables are picked up
        
        

        SandboxScanner.LabelEntry kitchenCorrupt = registry.get("kitchen_corrupt");
        assertTrue("Should detect assignment $ corruption += 2", kitchenCorrupt.variablesTouched.contains("corruption"));
    }

    @Test
    
    public void testDimensionCounting() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        Map<String, Integer> frequencies = scanner.getDimensionFrequencies();
        assertNotNull(frequencies);

        // 'week' is used in bathroom_week1, bathroom_week2, kitchen_normal
        assertTrue("Dimension counting should track 'week'", frequencies.containsKey("week"));
        assertTrue("Week is checked multiple times", frequencies.get("week") >= 2);

        assertTrue("Dimension counting should track 'corruption'", frequencies.containsKey("corruption"));
        assertTrue("Dimension counting should track 'time_of_day'", frequencies.containsKey("time_of_day"));
    }

    @Test
    
    public void testAnalyzeModeFormatting() throws Exception {
        KNModSandbox sandbox = new KNModSandbox();
        String output = sandbox.analyze(getTestResourcePath());

        assertTrue("Output should mention strongest dimensions", output.contains("STRONGEST STORY DIMENSIONS"));
        assertTrue("Output should list 'week' as a dimension", output.contains("week"));
        assertTrue("Output should list 'corruption' as a dimension", output.contains("corruption"));
        assertTrue("Output should list file groupings", output.contains("STRONGEST FILE GROUPINGS"));
        assertTrue("Output should list 'bathroom.rpy'", output.contains("bathroom.rpy"));
        assertTrue("Output should suggest how to use groupBy", output.contains("--groupBy flag"));
    }

    @Test
    
    public void testGroupBySortsLabelsCorrectly() throws Exception {
        String outPath = System.getProperty("java.io.tmpdir") + "/sandbox_group_sort.rpy";
        KNModSandbox sandbox = new KNModSandbox();
        sandbox.setStartLabel("start");

        // Group by week. week 1 events should appear before week 2 events.
        sandbox.setGroupBy(Arrays.asList("week", "FILE"));
        sandbox.assemble(getTestResourcePath(), outPath);

        String content = Files.readString(Path.of(outPath));

        int week1Index = content.indexOf("label bathroom_week1:");
        int week2Index = content.indexOf("label bathroom_week2:");

        assertTrue("Output must contain bathroom_week1", week1Index != -1);
        assertTrue("Output must contain bathroom_week2", week2Index != -1);
        assertTrue("bathroom_week1 must appear before bathroom_week2 when grouping by week", week1Index < week2Index);

        new File(outPath).delete();
    }

    @Test
    
    public void testGroupByPreservesSequentialJumps() throws Exception {
        String outPath = System.getProperty("java.io.tmpdir") + "/sandbox_group_seq.rpy";
        KNModSandbox sandbox = new KNModSandbox();
        sandbox.setStartLabel("start");

        // Even if we group by some completely different property that might otherwise sort part 2 first...
        // The visited set must protect it.
        sandbox.setGroupBy(Arrays.asList("corruption"));
        sandbox.assemble(getTestResourcePath(), outPath);

        String content = Files.readString(Path.of(outPath));

        int part1Index = content.indexOf("label bathroom_week1:");
        int part2Index = content.indexOf("label bathroom_week1_part2:");

        assertTrue("Output must contain bathroom_week1", part1Index != -1);
        assertTrue("Output must contain bathroom_week1_part2", part2Index != -1);
        assertTrue("bathroom_week1_part2 MUST immediately follow its sequential jump, not pulled out by sorting",
            part1Index < part2Index);

        new File(outPath).delete();
    }
}
