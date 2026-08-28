package org.bullithulli.feature.sandbox;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SandboxScanner {

    private static final Pattern LABEL_PATTERN = Pattern.compile("^label\\s+(\\w+)\\s*(?:\\(.*\\))?\\s*:");
    private static final Pattern NAMED_MENU_PATTERN = Pattern.compile("^menu\\s+(\\w+)\\s*:");
    private static final Pattern SCREEN_PATTERN = Pattern.compile("^screen\\s+(\\w+)\\s*\\(");
    private static final Pattern JUMP_IN_SCREEN_PATTERN = Pattern.compile("Jump\\s*\\(\\s*[\"'](\\w+)[\"']\\s*\\)");
    private static final Pattern CALL_IN_SCREEN_PATTERN = Pattern.compile("Call\\s*\\(\\s*[\"'](\\w+)[\"']\\s*\\)");

    private static final Pattern CONDITION_PATTERN = Pattern.compile("([a-zA-Z_]\\w*)\\s*(==|>=|<=|>|<|!=)\\s*([a-zA-Z0-9_\"']+)");
    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("^\\s*\\$\\s*([a-zA-Z_]\\w*)\\s*[-+*\\/]?=");

    private final Map<String, LabelEntry> labelRegistry = new LinkedHashMap<>();
    private final Map<String, List<String>> screenRegistry = new LinkedHashMap<>();
    private final Map<String, String> fallThroughMap = new LinkedHashMap<>();

    public void scan(File directory) throws IOException {
        List<File> rpyFiles = new ArrayList<>();
        collectRpyFiles(directory, rpyFiles);
        Collections.sort(rpyFiles);
        for (File file : rpyFiles) {
            scanFile(file);
        }
        log.info("Scanned {} files, found {} labels, {} screens",
                rpyFiles.size(), labelRegistry.size(), screenRegistry.size());
    }

    private void collectRpyFiles(File dir, List<File> result) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                collectRpyFiles(f, result);
            } else if (f.getName().endsWith(".rpy")) {
                result.add(f);
            }
        }
    }

    private void scanFile(File file) throws IOException {
        List<String> allLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allLines.add(line);
            }
        }

        List<String> labelsInFileOrder = new ArrayList<>();

        for (int i = 0; i < allLines.size(); i++) {
            String line = allLines.get(i);
            String trimmed = line.trim();

            Matcher labelMatcher = LABEL_PATTERN.matcher(trimmed);
            if (labelMatcher.find()) {
                String labelName = labelMatcher.group(1);
                int baseIndent = countLeadingSpaces(line);
                BlockResult block = extractBlock(allLines, i + 1, baseIndent);
                LabelEntry entry = new LabelEntry(labelName, file.getAbsolutePath(), i, block.endIndex, block.lines);
                extractVariables(entry);
                if (labelRegistry.containsKey(labelName)) {
                    log.warn("Duplicate label '{}' in {} (already in {}), keeping first",
                            labelName, file.getName(), labelRegistry.get(labelName).filePath);
                } else {
                    labelRegistry.put(labelName, entry);
                    if (baseIndent == 0) {
                        labelsInFileOrder.add(labelName);
                    }
                }
            }

            Matcher namedMenuMatcher = NAMED_MENU_PATTERN.matcher(trimmed);
            if (namedMenuMatcher.find()) {
                String menuName = namedMenuMatcher.group(1);
                if (!labelRegistry.containsKey(menuName)) {
                    int baseIndent = SandboxScanner.countLeadingSpaces(line);
                    BlockResult block = extractBlock(allLines, i + 1, baseIndent);
                    LabelEntry entry = new LabelEntry(menuName, file.getAbsolutePath(), i, block.endIndex, block.lines);
                    extractVariables(entry);
                    labelRegistry.put(menuName, entry);
                }
            }

            Matcher screenMatcher = SCREEN_PATTERN.matcher(trimmed);
            if (screenMatcher.find()) {
                String screenName = screenMatcher.group(1);
                int baseIndent = countLeadingSpaces(line);
                BlockResult block = extractBlock(allLines, i + 1, baseIndent);
                List<String> jumpTargets = new ArrayList<>();
                for (String bodyLine : block.lines) {
                    Matcher jumpMatcher = JUMP_IN_SCREEN_PATTERN.matcher(bodyLine);
                    Matcher callScreenMatcher = CALL_IN_SCREEN_PATTERN.matcher(bodyLine);
                    while (callScreenMatcher.find()) {
                        jumpTargets.add(callScreenMatcher.group(1));
                    }
                    while (jumpMatcher.find()) {
                        jumpTargets.add(jumpMatcher.group(1));
                    }
                }
                if (!jumpTargets.isEmpty()) {
                    screenRegistry.put(screenName, jumpTargets);
                }
            }
        }

        for (int i = 0; i < labelsInFileOrder.size() - 1; i++) {
            String current = labelsInFileOrder.get(i);
            String next = labelsInFileOrder.get(i + 1);
            LabelEntry entry = labelRegistry.get(current);
            if (entry != null && !endsWithJumpOrReturn(entry.content)) {
                fallThroughMap.put(current, next);
            }
        }
    }

    private void extractVariables(LabelEntry entry) {
        for (String line : entry.content) {
            String trimmed = line.trim();
            if (trimmed.startsWith("if ") || trimmed.startsWith("elif ") || trimmed.startsWith("while ") || trimmed.contains(" if ")) {
                Matcher m = CONDITION_PATTERN.matcher(line);
                while (m.find()) {
                    entry.conditions.put(m.group(1), m.group(3).replace("\"", "").replace("'", ""));
                }
            }
            Matcher assignMatcher = ASSIGNMENT_PATTERN.matcher(line);
            if (assignMatcher.find()) {
                entry.variablesTouched.add(assignMatcher.group(1));
            }
        }
    }

    private boolean endsWithJumpOrReturn(List<String> content) {
        for (int i = content.size() - 1; i >= 0; i--) {
            String trimmed = content.get(i).trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            return trimmed.startsWith("jump ") || trimmed.startsWith("return")
                    || trimmed.startsWith("$ renpy.jump(") || trimmed.startsWith("$renpy.jump(");
        }
        return false;
    }

    private BlockResult extractBlock(List<String> allLines, int startIndex, int baseIndent) {
        List<String> content = new ArrayList<>();
        int endIndex = startIndex;
        for (int i = startIndex; i < allLines.size(); i++) {
            String line = allLines.get(i);
            if (line.trim().isEmpty()) {
                content.add(line);
                endIndex = i + 1;
                continue;
            }
            int indent = countLeadingSpaces(line);
            if (indent <= baseIndent) {
                break;
            }
            content.add(line);
            endIndex = i + 1;
        }
        return new BlockResult(content, endIndex);
    }

    static class BlockResult {
        final List<String> lines;
        final int endIndex;
        BlockResult(List<String> lines, int endIndex) {
            this.lines = lines;
            this.endIndex = endIndex;
        }
    }

    static int countLeadingSpaces(String line) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == ' ') count++;
            else if (c == '\t') count += 4;
            else break;
        }
        return count;
    }

    public Map<String, LabelEntry> getLabelRegistry() {
        return labelRegistry;
    }

    public Map<String, List<String>> getScreenRegistry() {
        return screenRegistry;
    }

    public Map<String, Integer> getDimensionFrequencies() {
        Map<String, Integer> frequencies = new HashMap<>();
        for (LabelEntry entry : labelRegistry.values()) {
            for (String condition : entry.conditions.keySet()) {
                frequencies.put(condition, frequencies.getOrDefault(condition, 0) + 1);
            }
            for (String variable : entry.variablesTouched) {
                frequencies.put(variable, frequencies.getOrDefault(variable, 0) + 1);
            }
        }
        return frequencies;
    }

    public Map<String, String> getFallThroughMap() {
        return fallThroughMap;
    }

    public static class LabelEntry {
        public final String name;
        public final String filePath;
        public final int lineNumber;
        public final int endLineNumber;
        public final List<String> content;
        public final Map<String, String> conditions = new HashMap<>();
        public final Set<String> variablesTouched = new HashSet<>();

        public LabelEntry(String name, String filePath, int lineNumber, int endLineNumber, List<String> content) {
            this.name = name;
            this.filePath = filePath;
            this.lineNumber = lineNumber;
            this.endLineNumber = endLineNumber;
            this.content = content;
        }
    }
}
