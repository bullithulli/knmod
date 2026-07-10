package org.bullithulli.feature;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

import static org.bullithulli.Modder2.realArgs;
import static org.bullithulli.Modder2.version;
import static org.bullithulli.rpyparser.parser.pattern_for_block_symbols;
import static org.bullithulli.rpyparser.parser.pattern_for_speaker_text;
import static org.bullithulli.rpyparser.parser.pattern_for_no_speaker_texts;
import static org.bullithulli.utils.modUtils.knmodSay;

/**
 * Streaming single-pass KNMOD implementation.
 * Replaces the tree-based KNMod approach with O(n) time, O(1) memory, no recursion.
 */
@Slf4j
public class KNModFast {

    // Pre-computed sets for O(1) lookup
    private static final Set<String> RETAIN_BLOCKS = Set.of(
            "python", "define", "style", "screen", "image",
            "scene", "show", "init", "class", "transform", "camera"
    );

    private static final char[] REMOVE_CHARACTERS = {'"', '\'', '[', ']', '{', '}', '(', ')'};

    // Char-indexed prefix maps for O(1) dispatch instead of O(n) linear scan
    private final Map<Character, List<String>> forceKNModPrefixMap = new HashMap<>();
    private final Map<Character, List<String>> forceDontKNModPrefixMap = new HashMap<>();
    // Case-insensitive set for O(1) exact-match lookup
    private final Set<String> forceDontKNModFor = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    public KNModFast() {
        for (String s : Arrays.asList(
                "$ renpy.show_screen", "$ renpy.jump", "$ renpy.load",
                "$ui.interact", "$ ui.interact", "return ", "call", "menu",
                "renpy.quit", "$ renpy.quit", "renpy.call", "$ renpy.call",
                "renpy.block", "$ renpy.block", "if ", "else ", "elif ",
                "label", "show screen ", "((", "$ MainMenu(", "$MainMenu(",
                "$ renpy.quit(", "$renpy.quit(", "$ renpy.full_restart",
                "$renpy.full_restart"
        )) {
            addForceKNModPrefix(s);
        }
        addForceDontKNModPrefix("label start");
        addForceDontKNModPrefix("default ");
        forceDontKNModFor.addAll(Arrays.asList("or", "and"));
    }

    /**
     * Adds a prefix to the force-KNMod prefix map, indexed by its first character.
     * Lines starting with this prefix will be wrapped as KN_MOD.
     *
     * <p>Example:
     * <pre>
     *   addForceKNModPrefix("call");
     *   // Internally stores: {'c' -> ["call"]} in forceKNModPrefixMap
     *   // Now any trimmed line starting with "call" (e.g. "call myLabel") will be KN_MOD wrapped.
     * </pre>
     *
     * @param prefix the prefix string to add (e.g. "$ renpy.jump", "return ", "menu")
     */
    public void addForceKNModPrefix(String prefix) {
        char key = prefix.charAt(0);
        forceKNModPrefixMap.computeIfAbsent(key, k -> new ArrayList<>()).add(prefix);
    }

    /**
     * Adds a prefix to the force-don't-KNMod prefix map, indexed by its first character.
     * Lines starting with this prefix will NOT be wrapped as KN_MOD, even if other rules say they should.
     *
     * <p>Example:
     * <pre>
     *   addForceDontKNModPrefix("label start");
     *   // Internally stores: {'l' -> ["label start"]} in forceDontKNModPrefixMap
     *   // Now "label start:" will be passed through without KN_MOD wrapping,
     *   // while "label chapter1:" would still be wrapped.
     * </pre>
     *
     * @param prefix the prefix string to add (e.g. "label start", "default ")
     */
    public void addForceDontKNModPrefix(String prefix) {
        char key = prefix.charAt(0);
        forceDontKNModPrefixMap.computeIfAbsent(key, k -> new ArrayList<>()).add(prefix);
    }

    /**
     * Bulk-adds multiple prefixes to the force-KNMod prefix map.
     *
     * <p>Example:
     * <pre>
     *   addAllForceKNModPrefixes(Arrays.asList("call", "jump ", "menu"));
     *   // Adds all three prefixes: {'c' -> ["call"], 'j' -> ["jump "], 'm' -> ["menu"]}
     * </pre>
     *
     * @param prefixes collection of prefix strings to add
     */
    public void addAllForceKNModPrefixes(Collection<String> prefixes) {
        for (String p : prefixes) addForceKNModPrefix(p);
    }

    /**
     * Bulk-adds multiple prefixes to the force-don't-KNMod prefix map.
     *
     * <p>Example:
     * <pre>
     *   addAllForceDontKNModPrefixes(Arrays.asList("label start", "default "));
     *   // Adds both: {'l' -> ["label start"], 'd' -> ["default "]}
     * </pre>
     *
     * @param prefixes collection of prefix strings to add
     */
    public void addAllForceDontKNModPrefixes(Collection<String> prefixes) {
        for (String p : prefixes) addForceDontKNModPrefix(p);
    }

    /**
     * Checks if any prefix in the given char-indexed map matches the start of the line.
     * Only checks prefixes sharing the same first character — O(bucket) instead of O(n).
     *
     * <p>Example:
     * <pre>
     *   // prefixMap = {'c' -> ["call", "class"], 'j' -> ["jump "]}
     *   matchesAnyPrefix(prefixMap, "call myLabel")  → true  (matches "call")
     *   matchesAnyPrefix(prefixMap, "jump away")     → true  (matches "jump ")
     *   matchesAnyPrefix(prefixMap, "show bg park")  → false (no 's' bucket)
     *   matchesAnyPrefix(prefixMap, "")               → false (empty line)
     * </pre>
     *
     * @param prefixMap char-indexed map of prefixes (first char → list of prefixes)
     * @param line      the trimmed line to check
     * @return true if any prefix in the map matches the start of the line
     */
    private static boolean matchesAnyPrefix(Map<Character, List<String>> prefixMap, String line) {
        if (line.isEmpty()) return false;
        List<String> bucket = prefixMap.get(line.charAt(0));
        if (bucket == null) return false;
        for (String prefix : bucket) {
            if (line.startsWith(prefix)) return true;
        }
        return false;
    }

    /**
     * Escapes special characters for safe embedding inside KN_MOD "..." strings.
     * Removes quotes, brackets, braces, parentheses, and colons.
     * Matches exactly the behavior of KNMod.escapeStringForPython.
     *
     * <p>Example:
     * <pre>
     *   escapeStringForPython("jump chapter1")           → "jump chapter1"
     *   escapeStringForPython("if health > 0:")          → "if health > 0"
     *   escapeStringForPython("call screen(\"hi\")")      → "call screenhi"
     *   escapeStringForPython("say \"hello [name]\"")    → "say hello name"
     *   escapeStringForPython(null)                       → ""
     * </pre>
     *
     * @param input the raw line text to escape (may be null)
     * @return the escaped string safe for KN_MOD embedding
     */
    private static String escapeStringForPython(String input) {
        if (input == null) {
            return "";
        }
        String result = input
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\t", "\\t")
                .replace("\r", "\\r")
                .replace(":", "");
        for (char c : REMOVE_CHARACTERS) {
            result = result.replace(String.valueOf(c), "");
        }
        return result;
    }

    /**
     * Counts the number of leading whitespace characters (spaces/tabs) in a line.
     * Used to determine indentation level for retained block hierarchy tracking.
     *
     * <p>Example:
     * <pre>
     *   countLeadingWhitespace("    show bg park")  → 4
     *   countLeadingWhitespace("        pass")      → 8
     *   countLeadingWhitespace("label start:")      → 0
     *   countLeadingWhitespace("")                   → 0
     * </pre>
     *
     * @param line the raw (untrimmed) line from the source file
     * @return the number of leading whitespace characters
     */
    private static int countLeadingWhitespace(String line) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (!Character.isWhitespace(line.charAt(i))) {
                break;
            }
            count++;
        }
        return count;
    }

    /**
     * Extracts the first word from a trimmed line, delimited by space, colon, or parenthesis.
     * Used to identify the keyword of a line (e.g. "python", "screen", "label").
     *
     * <p>Example:
     * <pre>
     *   getFirstWord("python:")           → "python"
     *   getFirstWord("label chapter1:")   → "label"
     *   getFirstWord("screen myScreen:")  → "screen"
     *   getFirstWord("show bg park")      → "show"
     *   getFirstWord("define(x, y)")      → "define"
     *   getFirstWord("return")            → "return"
     * </pre>
     *
     * @param trimmed the trimmed (no leading whitespace) line
     * @return the first word up to the first space, colon, or parenthesis
     */
    private static String getFirstWord(String trimmed) {
        int idx = trimmed.indexOf(' ');
        int idx2 = trimmed.indexOf(':');
        int idx3 = trimmed.indexOf('(');
        int end = trimmed.length();
        if (idx >= 0) end = Math.min(end, idx);
        if (idx2 >= 0) end = Math.min(end, idx2);
        if (idx3 >= 0) end = Math.min(end, idx3);
        return trimmed.substring(0, end);
    }

    /**
     * Checks if a trimmed line is a block symbol (a line ending with ':', 'and', 'or', '{', or '(').
     * Uses the same regex pattern as the parser (pattern_for_block_symbols).
     *
     * <p>Example:
     * <pre>
     *   isBlockSymbol("label start:")       → true   (ends with ':')
     *   isBlockSymbol("if health > 0:")     → true   (ends with ':')
     *   isBlockSymbol("python:")            → true   (ends with ':')
     *   isBlockSymbol("jump chapter1")      → false  (no block terminator)
     *   isBlockSymbol("\"Hello world\"")  → false  (dialogue line)
     * </pre>
     *
     * @param trimmedLine the trimmed line to check
     * @return true if the line matches the block symbol regex
     */
    private static boolean isBlockSymbol(String trimmedLine) {
        return pattern_for_block_symbols.matcher(trimmedLine).find();
    }

    /**
     * Checks if a trimmed line is a speaker dialogue line (character name followed by quoted text).
     * Uses the same regex pattern as the parser (pattern_for_speaker_text).
     *
     * <p>Example:
     * <pre>
     *   isSpeakerText("alice \"Hello there!\"")  → true
     *   isSpeakerText("mc \"What do you mean?\"") → true
     *   isSpeakerText("\"Hello world\"")           → false (no speaker name)
     *   isSpeakerText("jump chapter1")              → false (not dialogue)
     * </pre>
     *
     * @param trimmedLine the trimmed line to check
     * @return true if the line matches the speaker text pattern
     */
    private static boolean isSpeakerText(String trimmedLine) {
        return pattern_for_speaker_text.matcher(trimmedLine).find();
    }

    /**
     * Checks if a trimmed line is a narrator/no-speaker text line (starts with a double-quote).
     * Uses the same regex pattern as the parser (pattern_for_no_speaker_texts).
     *
     * <p>Example:
     * <pre>
     *   isNoSpeakerText("\"It was a dark night.\"")  → true  (starts with '"')
     *   isNoSpeakerText("alice \"Hello\"")            → false (has a speaker)
     *   isNoSpeakerText("jump chapter1")               → false (not text)
     * </pre>
     *
     * @param trimmedLine the trimmed line to check
     * @return true if the line matches the no-speaker text pattern
     */
    private static boolean isNoSpeakerText(String trimmedLine) {
        return pattern_for_no_speaker_texts.matcher(trimmedLine).find();
    }

    /**
     * Determines if a line should be wrapped as {@code KN_MOD "..."}.
     * A line is wrapped if it's a block symbol, jump, return, call, or matches the force-KNMod list,
     * UNLESS it matches the force-don't-KNMod prefix list.
     * Replicates the inner condition from KNMod.processSymbolHierarchy.
     *
     * <p>Example:
     * <pre>
     *   shouldWrapAsKNMod("jump chapter2")       → true   (starts with "jump ")
     *   shouldWrapAsKNMod("call myFunc")          → true   (starts with "call ")
     *   shouldWrapAsKNMod("if health > 0:")       → true   (block symbol + in forceKNMod list)
     *   shouldWrapAsKNMod("label start:")         → false  (matches forceDontKNMod "label start")
     *   shouldWrapAsKNMod("alice \"Hello\"")    → false  (dialogue, not a control line)
     *   shouldWrapAsKNMod("or")                   → true   (exact match in forceDontKNModFor set)
     * </pre>
     *
     * @param trimmedLine the trimmed line to evaluate
     * @return true if the line should be output as KN_MOD "escaped_line"
     */
    private boolean shouldWrapAsKNMod(String trimmedLine) {
        // Priority 1: If line starts with a forceDontKNMod prefix → never wrap.
        // Example RPY:
        //   "label start:"  → starts with "label start" (in forceDontKNMod) → return false
        //   "default x = 1" → starts with "default " (in forceDontKNMod) → return false
        //   "jump chapter2" → no match in forceDontKNMod → continue checking
        if (matchesAnyPrefix(forceDontKNModPrefixMap, trimmedLine)) {
            return false;
        }

        // Priority 2: Check if it's a control line that should always be wrapped.
        // Example RPY → result:
        //   "if health > 0:" → isBlock=true → wrap as KN_MOD
        //   "jump chapter2"  → isJump=true → wrap as KN_MOD
        //   "return"         → isReturn=true → wrap as KN_MOD
        //   "call myFunc"    → isCall=true → wrap as KN_MOD
        //   "alice \"Hi\"" → all false → check force lists below
        boolean isBlock = isBlockSymbol(trimmedLine);
        boolean isJump = trimmedLine.startsWith("jump ");
        boolean isReturn = trimmedLine.startsWith("return");//awcator fix this into data strcut
        boolean isCall = trimmedLine.startsWith("call ");

        // Priority 3: O(bucket) prefix lookup — matches forceKNMod list (e.g. "$ renpy.jump", "menu")
        // Example RPY:
        //   "$ renpy.jump(...)"  → starts with "$ renpy.jump" → matchesForceKNMod=true
        //   "show screen stats"  → starts with "show screen " → matchesForceKNMod=true
        boolean matchesForceKNMod = matchesAnyPrefix(forceKNModPrefixMap, trimmedLine);

        // Priority 4: O(1) case-insensitive exact-match lookup for special keywords.
        // Example RPY:
        //   "or"   → exact match in forceDontKNModFor set → matchesForceDontKNModFor=true → wrap
        //   "and"  → exact match in forceDontKNModFor set → matchesForceDontKNModFor=true → wrap
        //   "alice \"Hi\"" → no match → false
        boolean matchesForceDontKNModFor = forceDontKNModFor.contains(trimmedLine);

        return isBlock || isJump || isReturn || isCall || matchesForceKNMod || matchesForceDontKNModFor;
    }

    /**
     * Checks if a line starts a retained block (a block whose content is preserved verbatim
     * with re-indented hierarchy instead of being KN_MOD wrapped).
     * A retained block is a block symbol whose first keyword is in RETAIN_BLOCKS
     * (python, define, style, screen, image, scene, show, init, class, transform, camera).
     *
     * <p>Example:
     * <pre>
     *   isRetainedBlockStart("python:")             → true  ("python" ∈ RETAIN_BLOCKS)
     *   isRetainedBlockStart("screen myScreen:")    → true  ("screen" ∈ RETAIN_BLOCKS)
     *   isRetainedBlockStart("image bg park = ...") → true  ("image" ∈ RETAIN_BLOCKS)//awcator Todo
     *   isRetainedBlockStart("label chapter1:")     → false ("label" ∉ RETAIN_BLOCKS)
     *   isRetainedBlockStart("if health > 0:")      → false ("if" ∉ RETAIN_BLOCKS)
     *   isRetainedBlockStart("jump chapter2")       → false (not a block symbol)
     * </pre>
     *
     * @param trimmedLine the trimmed line to check
     * @return true if this line starts a retained block
     */
    private boolean isRetainedBlockStart(String trimmedLine) {
        if (!isBlockSymbol(trimmedLine)) {
            return false;
        }
        String firstWord = getFirstWord(trimmedLine);
        return RETAIN_BLOCKS.contains(firstWord);
    }

    /**
     * Mini parser state machine for tracking indentation hierarchy inside retained blocks.
     * Replicates the parser's dynamic indent and hierarchy logic to produce correct
     * re-indentation (4 spaces per level) without building a full AST.
     *
     * <p>Example usage:
     * <pre>
     *   // Given a "python:" block starting at column 4:
     *   RetainedBlockState state = new RetainedBlockState(4);
     *
     *   // Child line "        x = 1" (8 spaces, non-block) → level 1 → output "    x = 1"
     *   state.computeLevel(8, false);  // returns 1
     *
     *   // Child line "        if True:" (8 spaces, block) → level 1 → output "    if True:"
     *   state.computeLevel(8, true);   // returns 1
     *
     *   // Grandchild "            y = 2" (12 spaces, non-block) → level 2 → output "        y = 2"
     *   state.computeLevel(12, false); // returns 2
     * </pre>
     */
    private static class RetainedBlockState {
        int currentHierarchy = 0;
        int prevParentWs;       // whitespace of previousHierarchyParent
        int prevParentLevel;    // level of previousHierarchyParent
        // Stack of hierarchy levels for getParentHierarchySymbol simulation
        Deque<Integer> levelStack = new ArrayDeque<>();

        /**
         * Creates a new retained block state anchored at the given base whitespace.
         *
         * @param baseWs the leading whitespace of the retained block header line
         *               (e.g. 4 if the "python:" line has 4 spaces indent)
         */
        RetainedBlockState(int baseWs) {
            prevParentWs = baseWs;
            prevParentLevel = 0;
            levelStack.push(0); // base block at level 0
        }

        /**
         * Computes the hierarchy level for a line inside the retained block,
         * based on its whitespace and whether it's a block symbol.
         *
         * <p>Example:
         * <pre>
         *   // baseWs = 0, so dynamic indent = 4
         *   computeLevel(4, true)   → 1  (block child at 4 spaces)
         *   computeLevel(8, false)  → 2  (non-block grandchild at 8 spaces)
         *   computeLevel(4, false)  → 1  (back to first indent level)
         * </pre>
         *
         * @param currentWs the leading whitespace count of the current line
         * @param isBlock   true if the current line is a block symbol (ends with ':', etc.)
         * @return the computed hierarchy level (0-based), used for "    ".repeat(level) indentation
         */
        int computeLevel(int currentWs, boolean isBlock) {
            // Dynamic space size (same as parser's getDynmaicIndent)
            // Detects the indent width used in the source file (e.g. 2, 4, or 8 spaces).
            // Example: if prevParentWs=0 and currentWs=4 → diff=4 → dynamicSpaceSize=4
            // Example: if prevParentWs=0 and currentWs=2 → diff=2 → dynamicSpaceSize=2
            int dynamicSpaceSize = 4;
            if (currentWs > prevParentWs) {
                int diff = currentWs - prevParentWs;
                if (diff > 0 && diff <= 8) dynamicSpaceSize = diff;
            }
            
            // h = raw hierarchy guess from whitespace alone
            // Example: currentWs=8, dynamicSpaceSize=4 → h=2
            int h = (dynamicSpaceSize > 0) ? currentWs / dynamicSpaceSize : 0;

            int level;
            if (isBlock) {
                // ── Block symbol: a line ending with ':', 'and', 'or', '{', or '(' ──

                if (h > currentHierarchy) {
                    // Block is DEEPER than current hierarchy → it's a new child block.
                    // Example RenPy scenario:
                    //   python:              ← block at level 0 (currentHierarchy=0)
                    //       if True:          ← block at ws=4, h=1 > currentHierarchy(0) → level=1
                    //           for i in x:   ← block at ws=8, h=2 > currentHierarchy(1) → level=2
                    level = currentHierarchy + 1;
                } else {
                    // Block is at SAME or SHALLOWER depth → dedent back, find its parent.
                    // Example RenPy scenario (h <= currentHierarchy):
                    //   python:              ← level 0
                    //       if True:          ← level 1
                    //           pass
                    //       if False:         ← ws=4, h=1 <= currentHierarchy(1), parentLevel=0 → level=1
                    //   screen myScreen:      ← ws=0, h=0 <= currentHierarchy(1), parentLevel=0 → level=1
                    int parentLevel = findParentLevel(h);
                    level = parentLevel + 1;
                }
                currentHierarchy = level;
                prevParentWs = currentWs;
                prevParentLevel = level;
                // Update level stack to keep only strict ancestors of the current block.
                // Example RPY:
                //   if a:
                //       if b:
                //   if c:
                // Example levels: 1 -> 2 -> 1.
                // When current level returns to 1, pop 2 and old 1, then push new 1.
                while (!levelStack.isEmpty() && levelStack.peek() >= level) {
                    levelStack.pop();
                }
                levelStack.push(level);
            } else {
                // ── Non-block symbol: a leaf line like assignments, function calls, etc. ──

                if (h > currentHierarchy) {
                    // Non-block is DEEPER than current hierarchy → child of the last block.
                    // Example RenPy scenario:
                    //   python:              ← block at level 0
                    //       x = 1            ← ws=4, h=1 > currentHierarchy(0) → level = prevParentLevel+1 = 1
                    //   if True:             ← block at level 1
                    //       y = 2            ← ws=8, h=2 > currentHierarchy(1) → level = prevParentLevel+1 = 2
                    level = prevParentLevel + 1;
                    currentHierarchy = prevParentLevel;
                    // prevParent doesn't change — the last block parent is still the owner
                } else if (h == currentHierarchy) {
                    // Non-block is at SAME depth as current hierarchy → sibling, resets to parent.
                    // Example RenPy scenario:
                    //   python:              ← block at level 0
                    //       if True:          ← block at level 1 (currentHierarchy=1)
                    //           pass
                    //       x = 1            ← ws=4, h=1 == currentHierarchy(1) → level=1, resets to parent(0)
                    //       y = 2            ← ws=4, h=1 == currentHierarchy(1) → level=1, still parent(0)
                    level = h;
                    int parentLevel = findParentLevel(h);
                    prevParentLevel = parentLevel;
                    currentHierarchy = parentLevel;
                } else {
                    // Non-block is SHALLOWER than current hierarchy → dedent, jumps back up.
                    // Example RenPy scenario:
                    //   python:              ← block at level 0
                    //       if True:          ← block at level 1 (currentHierarchy=1)
                    //           x = 1        ← level 2
                    //   y = 2                ← ws=0, h=0 < currentHierarchy(1) → level=0, resets to parent(0)
                    level = h;
                    int parentLevel = findParentLevel(h);
                    prevParentLevel = parentLevel;
                    currentHierarchy = parentLevel;
                }
            }
            return level;
        }

        /**
         * Finds the nearest parent level strictly less than the given hierarchy value.
         * Walks the level stack (most recent first) to find the closest ancestor.
         *
         * <p>Example:
         * <pre>
         *   // levelStack = [2, 1, 0] (top to bottom)
         *   findParentLevel(2) → 1  (first level < 2)
         *   findParentLevel(1) → 0  (first level < 1)
         *   findParentLevel(0) → 0  (fallback to 0)
         * </pre>
         *
         * @param h the current hierarchy value to find a parent for
         * @return the nearest parent level, or 0 if none found
         */
        private int findParentLevel(int h) {
            for (int lvl : levelStack) {
                if (lvl < h) return lvl;
            }
            return 0;
        }
    }

    /**
     * Single-pass streaming KNMOD conversion. Reads a RenPy (.rpy) script file line-by-line
     * and writes a kinetic novel version where interactive elements are wrapped as KN_MOD dialogue.
     *
     * <p>The 3-state machine works as follows:
     * <ul>
     *   <li><b>PASSTHROUGH</b>: lines before {@code startModdingFrom} are copied verbatim</li>
     *   <li><b>MODDING</b>: control lines (jumps, calls, menus, labels) are wrapped as
     *       {@code KN_MOD "escaped_line"}, dialogue lines pass through</li>
     *   <li><b>RETAINED_BLOCK</b>: blocks like python/screen/image are preserved verbatim
     *       with re-indented hierarchy</li>
     * </ul>
     *
     * <p>Example:
     * <pre>
     *   // Input file "script.rpy":
     *   //   label start:
     *   //       "Hello world"
     *   //       jump chapter1
     *   //       python:
     *   //           x = 1
     *
     *   // Output (to destinationPath):
     *   //   define KN_MOD = Character("KN_MOD", color="#ff0000")
     *   //   # java -jar modder-2.jar ...
     *   //   ...
     *   //   label start:
     *   //   "Hello world"
     *   //   KN_MOD "jump chapter1"
     *   //   python:
     *   //       x = 1
     *   //   ...
     * </pre>
     *
     * @param sourceFilePath   path of the source RenPy file (e.g. "/home/user/script.rpy")
     * @param startModdingFrom symbol string from where modding starts (e.g. "label start"),
     *                         or null to mod from the beginning of the file
     * @param destinationPath  destination file path for the output (e.g. "/tmp/out")
     * @throws Exception if the source file cannot be read or the destination cannot be written
     */
    public void convertRenPyToKineticNovel(String sourceFilePath, String startModdingFrom, String destinationPath) throws Exception {
        File sourceFile = new File(sourceFilePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile), 65536);
             BufferedWriter writer = new BufferedWriter(new FileWriter(destinationPath), 65536)) {

            // Write header
            writeLine(writer, "define KN_MOD = Character(\"KN_MOD\", color=\"#ff0000\")");
            writeLine(writer, "# java -jar modder-2.jar " + realArgs);
            writeLine(writer, "# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/");
            writeLine(writer, "# modded by modder2" + version + " program. Created by BulliThulli");

            log.info("parsing......");
            log.info("modding......");

            // State machine variables
            boolean startedModding = (startModdingFrom == null);
            boolean inRetainedBlock = false;
            int retainedBlockBaseWs = 0;
            RetainedBlockState retainedState = null;

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                // STATE 1 (PASSTHROUGH): Haven't reached the start symbol yet → copy lines verbatim.
                // Example RPY with startModdingFrom="label start":
                //   define e = Character("Eileen")   → trimmed doesn't start with "label start" → write as-is
                //   label start:                       → trimmed starts with "label start" → startedModding=true, process
                //   "Hello!"                          → already modding, skip this check
                if (!startedModding) {
                    if (trimmed.startsWith(startModdingFrom)) {
                        startedModding = true;
                        // Fall through to process this line (e.g. "label start:" itself)
                    } else {
                        // Line is before start symbol → preserve it exactly as read.
                        writeLine(writer, line);
                        continue;
                    }
                }

                // Skip empty lines and comments after modding begins.
                // Example RPY lines that are skipped:
                //   (empty line)          → trimmed="" → skip
                //   # This is a comment   → starts with '#' → skip
                //   "Hello world"        → not empty, no '#' → process
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                // STATE 2 (RETAINED_BLOCK): Inside a retained block (python/screen/image/etc.)
                // Lines are preserved verbatim with re-indented hierarchy instead of KN_MOD wrapping.
                // Example RPY (retainedBlockBaseWs=4):
                //       python:              ← retained block header (ws=4, baseWs=4)
                //           x = 1            ← ws=8 > baseWs(4) → still inside, re-indent to level 1
                //           if True:          ← ws=8 > baseWs(4) → still inside, re-indent to level 1
                //               y = 2        ← ws=12 > baseWs(4) → still inside, re-indent to level 2
                //       "Hello"             ← ws=4 <= baseWs(4) → EXITED retained block, process normally
                if (inRetainedBlock) {
                    int currentWhitespace = countLeadingWhitespace(line);
                    if (currentWhitespace <= retainedBlockBaseWs) {
                        // Exited the retained block — whitespace is at or before the block header's level.
                        // Example: "    \"Hello\"" has ws=4 <= baseWs=4 → no longer inside python block
                        inRetainedBlock = false;
                        // Fall through to process this line normally (MODDING state)
                    } else {
                        // Still inside retained block — re-indent using parser-like hierarchy logic.
                        // Example: "        x = 1" has ws=8 > baseWs=4 → compute level → output "    x = 1"
                        boolean childIsBlock = isBlockSymbol(trimmed);
                        int level = retainedState.computeLevel(currentWhitespace, childIsBlock);
                        String indent = "    ".repeat(level);
                        writeLine(writer, indent + trimmed);
                        continue;
                    }
                }

                // Transition check: does this line start a new retained block?
                // Retained blocks are python/screen/image/define/style/etc. whose children
                // are preserved verbatim (not KN_MOD wrapped).
                // Example RPY lines that trigger retained block:
                //   python:              → "python" ∈ RETAIN_BLOCKS → enter RETAINED_BLOCK state
                //   screen myScreen:     → "screen" ∈ RETAIN_BLOCKS → enter RETAINED_BLOCK state
                //   image bg park = ...  → "image" ∈ RETAIN_BLOCKS → enter RETAINED_BLOCK state
                // Example RPY lines that do NOT trigger:
                //   label chapter1:      → "label" ∉ RETAIN_BLOCKS → stays in MODDING state
                //   if health > 0:       → "if" ∉ RETAIN_BLOCKS → stays in MODDING state
                if (isRetainedBlockStart(trimmed)) {
                    inRetainedBlock = true;
                    retainedBlockBaseWs = countLeadingWhitespace(line);
                    retainedState = new RetainedBlockState(retainedBlockBaseWs);
                    writeLine(writer, trimmed); // output header at level 0 (e.g. "python:")
                    continue;
                }

                // STATE 3 (MODDING): Decide whether to KN_MOD wrap or pass through as-is.
                // Example RPY lines that get WRAPPED:
                //   jump chapter2         → KN_MOD "jump chapter2"
                //   call myFunc           → KN_MOD "call myFunc"
                //   if health > 0:        → KN_MOD "if health > 0:"
                //   menu:                 → KN_MOD "menu:"
                //   return                → KN_MOD "return"
                // Example RPY lines that PASS THROUGH (dialogue/text):
                //   "Hello world"        → "Hello world"           (narrator text)
                //   alice "Hi there!"    → alice "Hi there!"      (speaker text)
                //   label start:          → label start:             (in forceDontKNMod list)
                if (shouldWrapAsKNMod(trimmed)) {
                    writeLine(writer, "KN_MOD \"" + escapeStringForPython(trimmed) + "\"");
                } else {
                    writeLine(writer, trimmed);
                }
            }

            // Write footer
            writeLine(writer, knmodSay("ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"));
            writeLine(writer, knmodSay("modded by modder2 " + version + " program. Created by BulliThulli"));
            writeLine(writer, "python:\n" +
                    "    renpy.input(\"Ignore this box. It is just added by me to verify if you reached the end of the game\", length=32)");

            writer.flush();
            log.info("[completed]");
        }
    }


    /**
     * Writes a single line followed by a newline character to the output.
     *
     * <p>Example:
     * <pre>
     *   writeLine(writer, "KN_MOD \"jump chapter1\"");
     *   // Writes: KN_MOD "jump chapter1"\n to the BufferedWriter
     * </pre>
     *
     * @param writer the BufferedWriter for the output file
     * @param line   the line content to write (without trailing newline)
     * @throws IOException if an I/O error occurs during writing
     */
    private void writeLine(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.write("\n");
    }
}
