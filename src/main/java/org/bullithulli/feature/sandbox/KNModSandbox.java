package org.bullithulli.feature.sandbox;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class KNModSandbox {

    private String startLabel = "start";
    private int maxDepth = 500;
    private Set<String> skipLabels = new HashSet<>();
    private boolean includeOrphans = true;
    private boolean removeFromSource = false;
    private String workDir = null;
    private java.util.List<String> groupBy = new java.util.ArrayList<>();

    public void setGroupBy(java.util.List<String> groupBy) {
        this.groupBy = groupBy;
    }

    public String analyze(String sourceDirectory) throws Exception {
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(sourceDirectory));

        Map<String, Integer> frequencies = scanner.getDimensionFrequencies();

        Map<String, Integer> fileCounts = new HashMap<>();
        for (SandboxScanner.LabelEntry entry : scanner.getLabelRegistry().values()) {
            String fileName = new File(entry.filePath).getName();
            fileCounts.put(fileName, fileCounts.getOrDefault(fileName, 0) + 1);
        }

        Map<String, Set<String>> dimensionValues = new HashMap<>();
        for (SandboxScanner.LabelEntry entry : scanner.getLabelRegistry().values()) {
            for (Map.Entry<String, String> cond : entry.conditions.entrySet()) {
                dimensionValues.computeIfAbsent(cond.getKey(), k -> new HashSet<>()).add(cond.getValue());
            }
        }

        List<Map.Entry<String, Integer>> sortedFreq = new ArrayList<>(frequencies.entrySet());
        sortedFreq.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        List<Map.Entry<String, Integer>> sortedFiles = new ArrayList<>(fileCounts.entrySet());
        sortedFiles.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("              SANDBOX ANALYSIS COMPLETE\n");
        sb.append("============================================================\n");
        sb.append("[INFO] Found ").append(scanner.getLabelRegistry().size()).append(" playable labels.\n\n");
        sb.append("[INFO] STRONGEST STORY DIMENSIONS (Variables gating scenes):\n");
        int count = 1;
        for (Map.Entry<String, Integer> e : sortedFreq) {
            String varName = e.getKey();
            Set<String> vals = dimensionValues.getOrDefault(varName, new HashSet<>());
            List<String> valList = new ArrayList<>(vals);
            Collections.sort(valList);
            String valStr = "";
            if (!valList.isEmpty()) {
                if (valList.size() > 5) {
                    valStr = " - Values: [" + String.join(", ", valList.subList(0, 5)) + ", ...]";
                } else {
                    valStr = " - Values: [" + String.join(", ", valList) + "]";
                }
            } else {
                valStr = " - (Modified/Assigned but not checked)";
            }
            sb.append(String.format("  %d. '%s' (Controls %d labels)%s\n", count++, varName, e.getValue(), valStr));
            if (count > 25) break;
        }

        sb.append("\n[INFO] STRONGEST FILE GROUPINGS:\n");
        count = 1;
        for (Map.Entry<String, Integer> e : sortedFiles) {
            sb.append("  ").append(count++).append(". '").append(e.getKey()).append("'   (").append(e.getValue()).append(" labels)\n");
            if (count > 15) break;
        }

        sb.append("\n============================================================\n");
        sb.append("               HOW TO FLATTEN THIS GAME\n");
        sb.append("============================================================\n");
        sb.append("Use the --groupBy flag to slice the game.\n");

        return sb.toString();
    }

    private static final Set<String> SKIP_LABEL_PREFIXES = Set.of("_", "after_load", "before_main_menu", "main_menu", "splashscreen");

    public void setStartLabel(String startLabel) {
        this.startLabel = startLabel;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void setSkipLabels(Set<String> skipLabels) {
        this.skipLabels = skipLabels;
    }

    public void setIncludeOrphans(boolean includeOrphans) {
        this.includeOrphans = includeOrphans;
    }

    public void setRemoveFromSource(boolean removeFromSource) {
        this.removeFromSource = removeFromSource;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    public void assemble(String sourceDirectory, String destinationPath) throws Exception {
        String scanDirectory = sourceDirectory;

        if (removeFromSource) {
            if (workDir == null || workDir.isBlank()) {
                throw new IllegalArgumentException("--workDir is mandatory when --removeFromSource=true. " +
                        "Provide a path (e.g. --workDir=/tmp/sandbox_work) where .rpy files will be copied.");
            }
            scanDirectory = workDir;
            copyRpyFiles(new File(sourceDirectory), new File(workDir), new File(sourceDirectory));
            log.info("Copied .rpy files from {} to {} (originals untouched)", sourceDirectory, workDir);
        }

        log.info("Scanning directory: {}", scanDirectory);
        SandboxScanner scanner = new SandboxScanner();
        scanner.scan(new File(scanDirectory));

        Map<String, SandboxScanner.LabelEntry> labelRegistry = scanner.getLabelRegistry();
        Map<String, List<String>> screenRegistry = scanner.getScreenRegistry();

        Map<String, String> fallThroughMap = scanner.getFallThroughMap();
        log.info("Found {} fall-through connections", fallThroughMap.size());

        List<String> startingLabels = new ArrayList<>(labelRegistry.keySet());
        startingLabels.remove(startLabel);
        if (groupBy != null && !groupBy.isEmpty()) {
            startingLabels.sort((label1, label2) -> {
                SandboxScanner.LabelEntry a = labelRegistry.get(label1);
                SandboxScanner.LabelEntry b = labelRegistry.get(label2);
                for (String group : groupBy) {
                    String valA;
                    String valB;
                    if ("FILE".equalsIgnoreCase(group)) {
                        valA = new File(a.filePath).getName();
                        valB = new File(b.filePath).getName();
                    } else {
                        valA = a.conditions.get(group);
                        valB = b.conditions.get(group);
                    }
                    if (valA == null && valB != null) return 1;
                    if (valA != null && valB == null) return -1;
                    if (valA != null && valB != null) {
                        int comp = valA.compareTo(valB);
                        if (comp != 0) return comp;
                    }
                }
                return 0;
            });
            log.info("Flattening sorted by groupBy={}", groupBy);
            if (labelRegistry.containsKey(startLabel)) {
                startingLabels.add(0, startLabel);
            }
        } else {
            log.info("Flattening from start label '{}'...", startLabel);
            startingLabels.clear();
            startingLabels.add(startLabel);
        }

        List<String> flattenedLines = new ArrayList<>();
        SandboxFlattener flattener = new SandboxFlattener(labelRegistry, screenRegistry, fallThroughMap, maxDepth, skipLabels);

        for (String currentStart : startingLabels) {
            if (flattener.getVisited().contains(currentStart)) continue;
            if (shouldSkipOrphan(currentStart)) continue;
            flattenedLines.addAll(flattener.flatten(currentStart, false));
        }

        if (includeOrphans && (groupBy == null || groupBy.isEmpty())) {
            Set<String> visited = flattener.getVisited();
            List<String> orphans = new ArrayList<>();
            for (Map.Entry<String, SandboxScanner.LabelEntry> entry : labelRegistry.entrySet()) {
                String labelName = entry.getKey();
                if (visited.contains(labelName)) continue;
                if (shouldSkipOrphan(labelName)) continue;

                SandboxScanner.LabelEntry label = entry.getValue();
                orphans.add("label " + labelName + ":");
                orphans.addAll(label.content);
            }
            if (!orphans.isEmpty()) {
                flattenedLines.add("");
                flattenedLines.add("# ========== UNVISITED CONTENT ==========");
                flattenedLines.add("# Labels below were not reached from '" + startLabel + "'");
                flattenedLines.addAll(orphans);
            }
        }

        log.info("Writing assembled output to: {}", destinationPath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destinationPath), 65536)) {
            for (String line : flattenedLines) {
                writer.write(line);
                writer.write("\n");
            }
            writer.flush();
        }

        Set<String> visited = flattener.getVisited();
        int totalLabels = labelRegistry.size();
        int visitedCount = 0;
        for (String v : visited) {
            if (labelRegistry.containsKey(v)) visitedCount++;
        }
        int orphanCount = totalLabels - visitedCount;

        log.info("Assembly complete: {} lines, {}/{} labels visited ({} orphans), output: {}",
                flattenedLines.size(), visitedCount, totalLabels, orphanCount, destinationPath);

        if (orphanCount > 0 && orphanCount <= 50) {
            List<String> missed = new ArrayList<>();
            for (String name : labelRegistry.keySet()) {
                if (!visited.contains(name) && !shouldSkipOrphan(name)) {
                    missed.add(name);
                }
            }
            if (!missed.isEmpty()) {
                log.info("Orphan labels appended at end: {}", missed);
            }
        } else if (orphanCount > 50) {
            log.info("{} orphan labels appended at end (too many to list)", orphanCount);
        }

        if (removeFromSource) {
            removeProcessedLabelsFromSource(labelRegistry, visited);
            log.info("Processed labels removed from COPY at: {}. Inspect leftover content there.", workDir);
        }
    }

    private void copyRpyFiles(File sourceDir, File destDir, File sourceRoot) throws IOException {
        File[] files = sourceDir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                copyRpyFiles(f, destDir, sourceRoot);
            } else if (f.getName().endsWith(".rpy")) {
                Path relativePath = sourceRoot.toPath().relativize(f.toPath());
                Path targetPath = destDir.toPath().resolve(relativePath);
                Files.createDirectories(targetPath.getParent());
                Files.copy(f.toPath(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private void removeProcessedLabelsFromSource(Map<String, SandboxScanner.LabelEntry> labelRegistry,
                                                  Set<String> visited) throws IOException {
        Map<String, List<SandboxScanner.LabelEntry>> byFile = new LinkedHashMap<>();
        for (String labelName : visited) {
            SandboxScanner.LabelEntry entry = labelRegistry.get(labelName);
            if (entry == null) continue;
            byFile.computeIfAbsent(entry.filePath, k -> new ArrayList<>()).add(entry);
        }

        int filesModified = 0;
        int labelsRemoved = 0;

        for (Map.Entry<String, List<SandboxScanner.LabelEntry>> fileEntry : byFile.entrySet()) {
            String filePath = fileEntry.getKey();
            List<SandboxScanner.LabelEntry> labels = fileEntry.getValue();

            Set<Integer> linesToRemove = new TreeSet<>();
            for (SandboxScanner.LabelEntry label : labels) {
                for (int line = label.lineNumber; line < label.endLineNumber; line++) {
                    linesToRemove.add(line);
                }
            }

            if (linesToRemove.isEmpty()) continue;

            List<String> allLines = Files.readAllLines(Path.of(filePath));
            List<String> remaining = new ArrayList<>();
            for (int i = 0; i < allLines.size(); i++) {
                if (!linesToRemove.contains(i)) {
                    remaining.add(allLines.get(i));
                }
            }

            Files.write(Path.of(filePath), remaining);
            filesModified++;
            labelsRemoved += labels.size();
        }

        log.info("Removed {} labels from {} source files. Check remaining content to verify completeness.",
                labelsRemoved, filesModified);
    }

    private boolean shouldSkipOrphan(String labelName) {
        if (skipLabels.contains(labelName)) return true;
        for (String prefix : SKIP_LABEL_PREFIXES) {
            if (labelName.startsWith(prefix)) return true;
        }
        return false;
    }
}
