package org.bullithulli.feature.sandbox;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SandboxFlattener {

    private static final Pattern JUMP_PATTERN = Pattern.compile("^jump\\s+(\\w+)");
    private static final Pattern CALL_PATTERN = Pattern.compile("^call\\s+(\\w+)");
    private static final Pattern CALL_SCREEN_PATTERN = Pattern.compile("^call\\s+screen\\s+(\\w+)");
    private static final Pattern RENPY_JUMP_PATTERN = Pattern.compile("renpy\\.jump\\s*\\(\\s*[\"'](\\w+)[\"']\\s*\\)");
    private static final Pattern RENPY_CALL_PATTERN = Pattern.compile("renpy\\.call\\s*\\(\\s*[\"'](\\w+)[\"']\\s*\\)");
    private static final Pattern MENU_CHOICE_PATTERN = Pattern.compile("^\".*\"\\s*(?:if\\s+.*)?\\s*:");

    private final Map<String, SandboxScanner.LabelEntry> labelRegistry;
    private final Map<String, List<String>> screenRegistry;
    private final Map<String, String> fallThroughMap;
    private final Set<String> visited = new LinkedHashSet<>();
    private final List<String> output = new ArrayList<>();
    private final int maxDepth;
    private final Set<String> skipLabels;
    private int currentDepth = 0;

    public SandboxFlattener(Map<String, SandboxScanner.LabelEntry> labelRegistry,
                            Map<String, List<String>> screenRegistry,
                            Map<String, String> fallThroughMap,
                            int maxDepth,
                            Set<String> skipLabels) {
        this.labelRegistry = labelRegistry;
        this.screenRegistry = screenRegistry;
        this.fallThroughMap = fallThroughMap != null ? fallThroughMap : Collections.emptyMap();
        this.maxDepth = maxDepth;
        this.skipLabels = skipLabels != null ? skipLabels : Collections.emptySet();
    }

    public List<String> flatten(String startLabel) {
        output.clear();
        visited.clear();
        currentDepth = 0;
        flattenLabel(startLabel);
        return output;
    }

    public Set<String> getVisited() {
        return visited;
    }

    private void flattenLabel(String labelName) {
        if (visited.contains(labelName)) {
            output.add("    \"[Already visited: " + labelName + "]\"");
            return;
        }
        if (skipLabels.contains(labelName)) {
            output.add("    \"[Skipped: " + labelName + "]\"");
            return;
        }
        if (currentDepth >= maxDepth) {
            output.add("    \"[Max depth reached at: " + labelName + "]\"");
            log.warn("Max depth {} reached at label '{}'", maxDepth, labelName);
            return;
        }

        visited.add(labelName);
        SandboxScanner.LabelEntry entry = labelRegistry.get(labelName);
        if (entry == null) {
            output.add("    \"[Label not found: " + labelName + "]\"");
            log.warn("Label '{}' not found in registry", labelName);
            return;
        }

        output.add("label " + labelName + ":");
        currentDepth++;
        processLines(entry.content, 0, entry.content.size());
        currentDepth--;

        String fallThrough = fallThroughMap.get(labelName);
        if (fallThrough != null && !visited.contains(fallThrough)) {
            flattenLabel(fallThrough);
        }
    }

    private void processLines(List<String> lines, int from, int to) {
        int i = from;
        while (i < to) {
            String line = lines.get(i);
            String trimmed = line.trim();

            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                i++;
                continue;
            }

            // WHILE TRUE / WHILE 1 — process body once, break the loop
            if (trimmed.startsWith("while True") || trimmed.startsWith("while 1")) {
                output.add(line);
                int baseIndent = SandboxScanner.countLeadingSpaces(line);
                BlockRange body = extractIndentedBlock(lines, i + 1, baseIndent);
                processLines(body.lines, 0, body.lines.size());
                i = body.endIndex;
                continue;
            }

            // CALL SCREEN — extract Jump targets from screen registry
            Matcher callScreenMatcher = CALL_SCREEN_PATTERN.matcher(trimmed);
            if (callScreenMatcher.find()) {
                String screenName = callScreenMatcher.group(1);
                output.add(line);
                List<String> targets = screenRegistry.get(screenName);
                if (targets != null) {
                    for (String target : targets) {
                        flattenLabel(target);
                    }
                }
                i++;
                continue;
            }

            // JUMP — inline target, stop processing this label's remaining lines
            Matcher jumpMatcher = JUMP_PATTERN.matcher(trimmed);
            if (jumpMatcher.find()) {
                String target = jumpMatcher.group(1);
                output.add(line);
                flattenLabel(target);
                return;
            }

            // CALL (not screen) — inline target, then continue
            Matcher renpyCallMatcher = RENPY_CALL_PATTERN.matcher(trimmed);
            if (renpyCallMatcher.find()) {
                String target = renpyCallMatcher.group(1);
                output.add(line);
                flattenLabel(target);
                i++;
                continue;
            }

            Matcher callMatcher = CALL_PATTERN.matcher(trimmed);
            if (callMatcher.find()) {
                String target = callMatcher.group(1);
                output.add(line);
                flattenLabel(target);
                i++;
                continue;
            }

            // $ renpy.jump("label") — dynamic jump with string literal
            Matcher renpyJumpMatcher = RENPY_JUMP_PATTERN.matcher(trimmed);
            if (renpyJumpMatcher.find()) {
                String target = renpyJumpMatcher.group(1);
                output.add(line);
                flattenLabel(target);
                return;
            }

            // MENU — expand all choices inline
            if (trimmed.startsWith("menu")) {
                output.add(line);
                int baseIndent = SandboxScanner.countLeadingSpaces(line);
                List<MenuChoice> choices = parseMenuChoices(lines, i + 1, baseIndent);
                for (MenuChoice choice : choices) {
                    output.add(choice.promptLine);
                    processLines(choice.bodyLines, 0, choice.bodyLines.size());
                }
                i = choices.isEmpty() ? i + 1 : choices.get(choices.size() - 1).endIndex;
                continue;
            }

            // IF / ELIF / ELSE — expand ALL branches inline
            if (trimmed.startsWith("if ") && trimmed.endsWith(":")) {
                int baseIndent = SandboxScanner.countLeadingSpaces(line);
                List<ConditionalBranch> branches = parseConditionalBlock(lines, i, baseIndent);
                for (ConditionalBranch branch : branches) {
                    output.add(branch.headerLine);
                    processLines(branch.bodyLines, 0, branch.bodyLines.size());
                }
                i = branches.isEmpty() ? i + 1 : branches.get(branches.size() - 1).endIndex;
                continue;
            }

            // RETURN — stop processing this label
            if (trimmed.startsWith("return")) {
                output.add(line);
                return;
            }

            // EVERYTHING ELSE — pass through as-is
            output.add(line);
            i++;
        }
    }

    private List<MenuChoice> parseMenuChoices(List<String> lines, int startIndex, int menuBaseIndent) {
        List<MenuChoice> choices = new ArrayList<>();
        int choiceIndent = menuBaseIndent + 4;
        int i = startIndex;

        while (i < lines.size()) {
            String line = lines.get(i);
            String trimmed = line.trim();

            if (trimmed.isEmpty()) {
                i++;
                continue;
            }

            int indent = SandboxScanner.countLeadingSpaces(line);
            if (indent <= menuBaseIndent) {
                break;
            }

            if (indent <= choiceIndent && MENU_CHOICE_PATTERN.matcher(trimmed).find()) {
                choiceIndent = indent;
                List<String> bodyLines = new ArrayList<>();
                int bodyStart = i + 1;
                int j = bodyStart;
                while (j < lines.size()) {
                    String bodyLine = lines.get(j);
                    String bodyTrimmed = bodyLine.trim();
                    if (bodyTrimmed.isEmpty()) {
                        j++;
                        continue;
                    }
                    int bodyIndent = SandboxScanner.countLeadingSpaces(bodyLine);
                    if (bodyIndent <= choiceIndent) {
                        break;
                    }
                    bodyLines.add(bodyLine);
                    j++;
                }
                choices.add(new MenuChoice(line, bodyLines, j));
                i = j;
            } else {
                i++;
            }
        }
        return choices;
    }

    private List<ConditionalBranch> parseConditionalBlock(List<String> lines, int startIndex, int baseIndent) {
        List<ConditionalBranch> branches = new ArrayList<>();
        int i = startIndex;

        while (i < lines.size()) {
            String line = lines.get(i);
            String trimmed = line.trim();

            if (trimmed.isEmpty()) {
                i++;
                continue;
            }

            int indent = SandboxScanner.countLeadingSpaces(line);
            if (indent < baseIndent) {
                break;
            }

            if (indent == baseIndent &&
                    (trimmed.startsWith("if ") || trimmed.startsWith("elif ") || trimmed.startsWith("else"))) {
                List<String> bodyLines = new ArrayList<>();
                int j = i + 1;
                while (j < lines.size()) {
                    String bodyLine = lines.get(j);
                    String bodyTrimmed = bodyLine.trim();
                    if (bodyTrimmed.isEmpty()) {
                        j++;
                        continue;
                    }
                    int bodyIndent = SandboxScanner.countLeadingSpaces(bodyLine);
                    if (bodyIndent <= baseIndent) {
                        break;
                    }
                    bodyLines.add(bodyLine);
                    j++;
                }
                branches.add(new ConditionalBranch(line, bodyLines, j));
                i = j;
            } else if (indent == baseIndent) {
                break;
            } else {
                i++;
            }
        }
        return branches;
    }

    private BlockRange extractIndentedBlock(List<String> lines, int startIndex, int baseIndent) {
        List<String> blockLines = new ArrayList<>();
        int i = startIndex;
        while (i < lines.size()) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) {
                i++;
                continue;
            }
            int indent = SandboxScanner.countLeadingSpaces(line);
            if (indent <= baseIndent) {
                break;
            }
            blockLines.add(line);
            i++;
        }
        return new BlockRange(blockLines, i);
    }

    static class MenuChoice {
        final String promptLine;
        final List<String> bodyLines;
        final int endIndex;

        MenuChoice(String promptLine, List<String> bodyLines, int endIndex) {
            this.promptLine = promptLine;
            this.bodyLines = bodyLines;
            this.endIndex = endIndex;
        }
    }

    static class ConditionalBranch {
        final String headerLine;
        final List<String> bodyLines;
        final int endIndex;

        ConditionalBranch(String headerLine, List<String> bodyLines, int endIndex) {
            this.headerLine = headerLine;
            this.bodyLines = bodyLines;
            this.endIndex = endIndex;
        }
    }

    static class BlockRange {
        final List<String> lines;
        final int endIndex;

        BlockRange(List<String> lines, int endIndex) {
            this.lines = lines;
            this.endIndex = endIndex;
        }
    }
}
