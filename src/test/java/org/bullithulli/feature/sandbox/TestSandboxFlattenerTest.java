package org.bullithulli.feature.sandbox;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static org.junit.Assert.*;

public class TestSandboxFlattenerTest {

    private String getTestResourcePath() {
        return Objects.requireNonNull(getClass().getClassLoader().getResource("sandboxtests")).getPath();
    }

    @Test
    public void testScanFindsAllLabels() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        Map<String, SandboxScanner.LabelEntry> labels = scanner.getLabelRegistry();
        assertTrue("Should find label start", labels.containsKey("start"));
        assertTrue("Should find label hub", labels.containsKey("hub"));
        assertTrue("Should find label kitchen", labels.containsKey("kitchen"));
        assertTrue("Should find label bedroom", labels.containsKey("bedroom"));
        assertTrue("Should find label park", labels.containsKey("park"));
        assertTrue("Should find label fountain", labels.containsKey("fountain"));
        assertTrue("Should find label bench", labels.containsKey("bench"));
        assertTrue("Should find label ending", labels.containsKey("ending"));
        assertTrue("Should find label make_coffee", labels.containsKey("make_coffee"));
        assertTrue("Should find label read_letter", labels.containsKey("read_letter"));
    }

    @Test
    public void testScanFindsScreenTargets() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        Map<String, List<String>> screens = scanner.getScreenRegistry();
        assertTrue("Should find screen park_navigation", screens.containsKey("park_navigation"));
        List<String> targets = screens.get("park_navigation");
        assertTrue("Screen should have Jump to fountain", targets.contains("fountain"));
        assertTrue("Screen should have Jump to bench", targets.contains("bench"));
        assertTrue("Screen should have Jump to hub", targets.contains("hub"));
    }

    @Test
    public void testFlattenVisitsAllLabels() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        SandboxFlattener flattener = new SandboxFlattener(
                scanner.getLabelRegistry(), scanner.getScreenRegistry(), scanner.getFallThroughMap(), 500, null);
        List<String> output = flattener.flatten("start");

        Set<String> visited = flattener.getVisited();
        assertTrue("Should visit start", visited.contains("start"));
        assertTrue("Should visit hub", visited.contains("hub"));
        assertTrue("Should visit kitchen", visited.contains("kitchen"));
        assertTrue("Should visit bedroom", visited.contains("bedroom"));
        assertTrue("Should visit park", visited.contains("park"));
        assertTrue("Should visit ending", visited.contains("ending"));
    }

    @Test
    public void testFlattenBreaksLoops() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        SandboxFlattener flattener = new SandboxFlattener(
                scanner.getLabelRegistry(), scanner.getScreenRegistry(), scanner.getFallThroughMap(), 500, null);
        List<String> output = flattener.flatten("start");

        String joined = String.join("\n", output);
        long hubCount = output.stream().filter(l -> l.equals("label hub:")).count();
        assertEquals("Hub label should appear exactly once (loop broken)", 1, hubCount);

        assertTrue("Should have 'already visited' marker for hub loop-back",
                joined.contains("[Already visited: hub]"));
    }

    @Test
    public void testFlattenExpandsMenuChoices() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        SandboxFlattener flattener = new SandboxFlattener(
                scanner.getLabelRegistry(), scanner.getScreenRegistry(), scanner.getFallThroughMap(), 500, null);
        List<String> output = flattener.flatten("start");

        String joined = String.join("\n", output);
        assertTrue("Should contain Kitchen menu choice", joined.contains("Go to Kitchen"));
        assertTrue("Should contain Bedroom menu choice", joined.contains("Go to Bedroom"));
        assertTrue("Should contain Park menu choice", joined.contains("Go to Park"));
        assertTrue("Should contain Leave menu choice", joined.contains("Leave the game"));
    }

    @Test
    public void testFlattenExpandsIfElseBranches() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        SandboxFlattener flattener = new SandboxFlattener(
                scanner.getLabelRegistry(), scanner.getScreenRegistry(), scanner.getFallThroughMap(), 500, null);
        List<String> output = flattener.flatten("start");

        String joined = String.join("\n", output);
        assertTrue("Should contain the if-branch content (alive)",
                joined.contains("Alice is cooking breakfast"));
        assertTrue("Should contain the else-branch content (dead)",
                joined.contains("The kitchen is empty"));
    }

    @Test
    public void testFlattenInlinesCallTargets() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        SandboxFlattener flattener = new SandboxFlattener(
                scanner.getLabelRegistry(), scanner.getScreenRegistry(), scanner.getFallThroughMap(), 500, null);
        List<String> output = flattener.flatten("start");

        String joined = String.join("\n", output);
        assertTrue("Should inline call make_coffee content",
                joined.contains("You make yourself a cup of coffee"));
    }

    @Test
    public void testFlattenHandlesCallScreen() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        SandboxFlattener flattener = new SandboxFlattener(
                scanner.getLabelRegistry(), scanner.getScreenRegistry(), scanner.getFallThroughMap(), 500, null);
        List<String> output = flattener.flatten("start");

        Set<String> visited = flattener.getVisited();
        assertTrue("Should visit fountain via call screen", visited.contains("fountain"));
        assertTrue("Should visit bench via call screen", visited.contains("bench"));
    }

    @Test
    public void testFullPipelineAssemble() throws Exception {
        String outPath = System.getProperty("java.io.tmpdir") + "/sandbox_test_out.rpy";
        KNModSandbox sandbox = new KNModSandbox();
        sandbox.setStartLabel("start");
        sandbox.assemble(getTestResourcePath(), outPath);

        String content = Files.readString(Path.of(outPath));
        assertTrue("Output should have label start", content.contains("label start:"));
        assertTrue("Output should have label hub", content.contains("label hub:"));
        assertTrue("Output should have label kitchen", content.contains("label kitchen:"));
        assertTrue("Output should have intro text", content.contains("Welcome to the game"));
        assertTrue("Output should have ending text", content.contains("Thanks for playing"));

        long lineCount = content.lines().count();
        assertTrue("Output should have substantial content, got " + lineCount, lineCount > 20);

        new File(outPath).delete();
    }

    @Test
    public void testFallThroughBetweenLabels() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        Map<String, String> fallThrough = scanner.getFallThroughMap();
        assertTrue("scene_part1 should fall through to scene_part2",
                fallThrough.containsKey("scene_part1"));
        assertEquals("scene_part2", fallThrough.get("scene_part1"));

        SandboxFlattener flattener = new SandboxFlattener(
                scanner.getLabelRegistry(), scanner.getScreenRegistry(), scanner.getFallThroughMap(), 500, null);

        // Flatten starting from scene_part1 directly
        List<String> output = flattener.flatten("scene_part1");
        String joined = String.join("\n", output);

        assertTrue("Should contain part 1 content",
                joined.contains("This is part 1 of the scene"));
        assertTrue("Should contain part 2 content via fall-through",
                joined.contains("This is part 2 of the scene"));
        assertTrue("Should contain both labels",
                joined.contains("label scene_part1:") && joined.contains("label scene_part2:"));
    }

    @Test
    public void testMaxDepthPreventsRunaway() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        SandboxFlattener flattener = new SandboxFlattener(
                scanner.getLabelRegistry(), scanner.getScreenRegistry(), scanner.getFallThroughMap(), 2, null);
        List<String> output = flattener.flatten("start");

        String joined = String.join("\n", output);
        assertTrue("Should hit max depth limit with depth=2",
                joined.contains("[Max depth reached"));
    }

    @Test
    public void testScanFindsNamedMenus() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        Map<String, SandboxScanner.LabelEntry> labels = scanner.getLabelRegistry();
        assertTrue("Should find named menu 'tavern_choice' as a label entry",
                labels.containsKey("tavern_choice"));
        SandboxScanner.LabelEntry menuEntry = labels.get("tavern_choice");
        String content = String.join("\n", menuEntry.content);
        assertTrue("Named menu content should contain choice text",
                content.contains("Order a drink"));
    }

    @Test
    public void testFlattenVisitsNamedMenu() throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(getTestResourcePath()));

        SandboxFlattener flattener = new SandboxFlattener(
                scanner.getLabelRegistry(), scanner.getScreenRegistry(), scanner.getFallThroughMap(), 500, null);
        List<String> output = flattener.flatten("start");

        Set<String> visited = flattener.getVisited();
        assertTrue("Should visit tavern label", visited.contains("tavern"));
        String joined = String.join("\n", output);
        assertTrue("Output should contain tavern content",
                joined.contains("You enter the tavern"));
    }

    @Test
    public void testRemoveFromSourceCopiesOnlyRpyFiles() throws Exception {
        Path tmpWorkDir = Files.createTempDirectory("sandbox_rfs_test_");
        try {
            String outPath = System.getProperty("java.io.tmpdir") + "/sandbox_rfs_out.rpy";
            KNModSandbox sandbox = new KNModSandbox();
            sandbox.setStartLabel("start");
            sandbox.setRemoveFromSource(true);
            sandbox.setWorkDir(tmpWorkDir.toString());
            sandbox.assemble(getTestResourcePath(), outPath);

            List<Path> copiedFiles = new ArrayList<>();
            Files.walkFileTree(tmpWorkDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    copiedFiles.add(file);
                    return FileVisitResult.CONTINUE;
                }
            });

            assertFalse("Work dir should contain copied files", copiedFiles.isEmpty());
            for (Path f : copiedFiles) {
                assertTrue("Only .rpy files should be copied, found: " + f.getFileName(),
                        f.toString().endsWith(".rpy"));
            }

            new File(outPath).delete();
        } finally {
            deleteDir(tmpWorkDir);
        }
    }

    @Test
    public void testRemoveFromSourceStripsVisitedLabels() throws Exception {
        Path tmpWorkDir = Files.createTempDirectory("sandbox_rfs_strip_");
        try {
            String outPath = System.getProperty("java.io.tmpdir") + "/sandbox_rfs_strip_out.rpy";
            KNModSandbox sandbox = new KNModSandbox();
            sandbox.setStartLabel("start");
            sandbox.setRemoveFromSource(true);
            sandbox.setWorkDir(tmpWorkDir.toString());
            sandbox.assemble(getTestResourcePath(), outPath);

            Path copiedScript = tmpWorkDir.resolve("script.rpy");
            assertTrue("script.rpy should exist in workDir", Files.exists(copiedScript));
            String remaining = Files.readString(copiedScript);
            assertFalse("label start should be removed from the copy",
                    remaining.contains("label start:"));

            File originalScript = new File(getTestResourcePath(), "script.rpy");
            String original = Files.readString(originalScript.toPath());
            assertTrue("Original script.rpy must still contain label start",
                    original.contains("label start:"));

            new File(outPath).delete();
        } finally {
            deleteDir(tmpWorkDir);
        }
    }

    @Test
    public void testRemoveFromSourceRequiresWorkDir() {
        KNModSandbox sandbox = new KNModSandbox();
        sandbox.setStartLabel("start");
        sandbox.setRemoveFromSource(true);
        try {
            sandbox.assemble(getTestResourcePath(), "/tmp/sandbox_no_workdir_out.rpy");
            fail("Should throw IllegalArgumentException when workDir is not set");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("--workDir"));
        } catch (Exception e) {
            fail("Expected IllegalArgumentException but got: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testRemoveFromSourcePreservesDirectoryStructure() throws Exception {
        Path tmpSource = Files.createTempDirectory("sandbox_struct_src_");
        Path subDir = tmpSource.resolve("chapter1");
        Files.createDirectories(subDir);
        Files.writeString(tmpSource.resolve("main.rpy"),
                "label start:\n    \"Hello\"\n    jump ch1\n");
        Files.writeString(subDir.resolve("ch1.rpy"),
                "label ch1:\n    \"Chapter 1 content\"\n    return\n");

        Path tmpWorkDir = Files.createTempDirectory("sandbox_struct_work_");
        try {
            String outPath = System.getProperty("java.io.tmpdir") + "/sandbox_struct_out.rpy";
            KNModSandbox sandbox = new KNModSandbox();
            sandbox.setStartLabel("start");
            sandbox.setRemoveFromSource(true);
            sandbox.setWorkDir(tmpWorkDir.toString());
            sandbox.assemble(tmpSource.toString(), outPath);

            Path copiedCh1 = tmpWorkDir.resolve("chapter1").resolve("ch1.rpy");
            assertTrue("Subdirectory structure should be preserved", Files.exists(copiedCh1));

            Path originalCh1 = subDir.resolve("ch1.rpy");
            String originalContent = Files.readString(originalCh1);
            assertTrue("Original file must be untouched",
                    originalContent.contains("label ch1:"));

            new File(outPath).delete();
        } finally {
            deleteDir(tmpSource);
            deleteDir(tmpWorkDir);
        }
    }

    private void deleteDir(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path d, IOException exc) throws IOException {
                Files.delete(d);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
