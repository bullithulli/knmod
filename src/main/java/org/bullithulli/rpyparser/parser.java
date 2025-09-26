package org.bullithulli.rpyparser;

import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyGenericBlockSymbol;
import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.bullithulli.rpyparser.symImpl.nonBlockSymbol.*;
import org.bullithulli.rpyparser.symImpl.renpySymbol;
import org.bullithulli.rpyparser.symImpl.rootSymbol;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE.*;
import static org.bullithulli.utils.textUtils.*;

public class parser {
    //todo unitTest
    //define sb_i_phone_nude_event = Event(
    //        location="home_isabelroom",
    //        category="technical",
    //        label="sb_i_phone_switch_naked_clothes",
    //        title=_(""),
    //        main_condition=( "isa.ap_tier>=14 and 'sb_j_cooking.level10' in seen_sb and 'sb_i_phone.level7' in seen_sb", _("")),
    //        type="auto"
    //    )---true

    // if asdasd and
    //		UserDataHandler =23 --->true


    // if asdasd or
    //		UserDataHandler =23 --->true

    final String regex_detect_for_block_symbols = "^\\s*([a-zA-Z_,.:$\\-\"'’\\?@í][â-ẑ\\;\\—a-zA-Z0-9_А-Яа-яЁё,‘’–\\$í@:\\\\<>.*…&+/%\\-=\"(\\)\\{\\}\\s\\t”“#’'\\?\\|\\]\\[!]*)\\s*(:|and|or|\\{|\\()\\s*(?:#.*)?$";
    final String regex_detect_for_speak_text = "^[a-z|A-Z]\\w*\\s+\".+";
    final String regex_detect_for_no_speaker_texts = "^\\s*\"";
    final String regex_detect_tl_originalLine = "^\\s*(#|old)[a-zA-Z0-9_А-Яа-яЁё,<>.…&+\\-=\"(\\)\\{\\}\\s\\t”“#’'\\?\\|\\]\\[!]+\\\".+\".*";
    final String regex_get_between_doubleQuotes = "\".*\"";
    /**
     * This will store path matrix, like
     * labelName,ArrayListOfSymbol who will call or jumps
     */
    public HashMap<String, ArrayList<renpySymbol>> pathMatrix = new HashMap<>();
    rootSymbol root = new rootSymbol(ROOT_LABEL, "\n");
    int CURRENT_HIERARCHY = 0;
    renpySymbol previousHierarchyParent = root;
    renpySymbol previousChainParent = root;
    Pattern pattern_for_speaker_text = Pattern.compile(regex_detect_for_speak_text);
    Pattern pattern_for_no_speaker_texts = Pattern.compile(regex_detect_for_no_speaker_texts);
    Pattern pattern_for_block_symbols = Pattern.compile(regex_detect_for_block_symbols);
    Pattern pattern_for_tl_original_line = Pattern.compile(regex_detect_tl_originalLine);
    Pattern pattern_get_between_doubleQuotes = Pattern.compile(regex_get_between_doubleQuotes);

    public HashMap<String, String> createTranslationTable(File fromFile) throws FileNotFoundException {
        HashMap<String, String> translationMap = new HashMap<>();
        Scanner sc = new Scanner(fromFile);
        boolean keyFound = false;
        String key = null, value = null;
        while (sc.hasNextLine()) {
            String untrimmedLine = sc.nextLine();
            String trimmedLine = untrimmedLine.trim();
            if (trimmedLine.isEmpty()) {
                continue;
            }

            if (pattern_for_tl_original_line.matcher(trimmedLine).find()) {
                keyFound = true;
                Matcher m = pattern_get_between_doubleQuotes.matcher(trimmedLine);
                if (m.find()) {
                    String underDoubleQuotes = m.group().trim();
                    key = getUncommentedString(underDoubleQuotes);
                }
            } else if ((pattern_for_speaker_text.matcher(trimmedLine).find() || pattern_for_no_speaker_texts.matcher(trimmedLine).find()) && keyFound) {
                Matcher m = pattern_get_between_doubleQuotes.matcher(trimmedLine);
                if (m.find()) {
                    String underDoubleQuotes = m.group().trim();
                    value = getUncommentedString(underDoubleQuotes);
                    translationMap.put(key, value);
                }
                keyFound = false;
            }
        }
        sc.close();
        return translationMap;
    }


    public renpySymbol parseLine(String lines, boolean basedOnTabCounts) {
        // TODO: 1/13/24 add init based initialization for GlobalVariables, so multiple calls on parseLine will not affect rootSymbol
        String[] linesArray = lines.split("\n");
        for (String untrimmedLine : linesArray) {
            String trimmedLine = untrimmedLine.trim();
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                continue;//skip new lines, empty lines
            }
            String symbol = trimmedLine.split("\\s+")[0];
            if (pattern_for_block_symbols.matcher(trimmedLine).find()) {
                if (symbol.startsWith("label")) {
                    sectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_LABEL);
                } else {
                    sectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_GENERIC_BLOCK_SYMBOLS);
                }
            } else {
                if (pattern_for_speaker_text.matcher(trimmedLine).find()) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_SPEAKER_TEXT);
                } else if (pattern_for_no_speaker_texts.matcher(trimmedLine).find()) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_NO_SPEAKER_TEXT);
                } else if (trimmedLine.startsWith("return")) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_RETURN);
                } else if (trimmedLine.startsWith("jump ")) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_JUMP);
                } else if (trimmedLine.startsWith("call ")) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_CALL);
                } else {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_GENERIC_NON_BLOCK_SYMBOLS);
                }
            }
        }
        return root;
    }

    public renpySymbol parseFrom(File file, boolean basedOnTabCounts, boolean requiredTranslate, File tl_file) throws FileNotFoundException {
        HashMap<String, String> translationTable = null;
        if (requiredTranslate) {
            translationTable = createTranslationTable(tl_file);
        }
        // TODO: 1/13/24 add init based initialization for GlobalVariables, so multiple calls on parseLine will not affect rootSymbol
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String untrimmedLine = sc.nextLine();
            String trimmedLine = untrimmedLine.trim();
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                continue;//skip new lines, empty lines
            }

            if (requiredTranslate) {
                Matcher m = pattern_get_between_doubleQuotes.matcher(trimmedLine);
                if (m.find()) {
                    String underDoubleQuotes = m.group().trim();
                    untrimmedLine = untrimmedLine.replace(underDoubleQuotes, translationTable.getOrDefault(underDoubleQuotes, underDoubleQuotes));
                    trimmedLine = untrimmedLine.trim();
                }
            }

            String symbol = trimmedLine.split("\\s+")[0];
            if (pattern_for_block_symbols.matcher(untrimmedLine).find()) {
                if (symbol.startsWith("label")) {
                    sectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_LABEL);
                } else {
                    sectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_GENERIC_BLOCK_SYMBOLS);
                }
            } else {
                if (pattern_for_speaker_text.matcher(trimmedLine).find()) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_SPEAKER_TEXT);
                } else if (pattern_for_no_speaker_texts.matcher(trimmedLine).find()) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_NO_SPEAKER_TEXT);
                } else if (trimmedLine.startsWith("return")) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_RETURN);
                } else if (trimmedLine.startsWith("jump ")) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_JUMP);
                } else if (trimmedLine.startsWith("call ")) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_CALL);
                } else {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, RENPY_GENERIC_NON_BLOCK_SYMBOLS);
                }
            }
        }
        return root;
    }

    /* <<<<<<<<<<<<<<  ✨ Windsurf Command ⭐ >>>>>>>>>>>>>>>> */

    /**
     * Gets the dynamic indent of the given line based on its parent hierarchy.
     *
     * <p>This method first counts the number of leading whitespaces in the given line.
     * If the previous hierarchy parent is not null, this method then calculates the
     * dynamic space size as the difference in whitespaces between the given line and
     * the previous hierarchy parent. If the difference is greater than 0 and less than
     * or equal to 8, the calculated space size is used as the dynamic space size.
     * Otherwise, the default space size of 4 is used.
     *
     * @param basedOnTabCounts True if the given line is indented with tabs, false
     *                         if it is indented with spaces.
     * @param untrimmedLine    The line to get the dynamic indent of.
     * @return The dynamic indent of the given line.
     */
    /* <<<<<<<<<<  4d067055-1097-444b-96a9-8883978296fb  >>>>>>>>>>> */
    private int getDynmaicIndent(boolean basedOnTabCounts, String untrimmedLine) {
        // Get whitespace count for current line
        int currentWhitespaceCount = countLeadingWhitespaces(untrimmedLine);
        int dynamicSpaceSize = 4; // Default to the provided spaceSize

// If we have a parent, calculate dynamic space size
        if (previousHierarchyParent != null) {
            String parentLine = previousHierarchyParent.getRENPY_UNTRIMMED_LINE();
            if (parentLine != null && !parentLine.isEmpty()) {
                int parentWhitespaceCount = countLeadingWhitespaces(parentLine);

                // If there's a difference in whitespace, use it as the dynamic space size
                if (currentWhitespaceCount > parentWhitespaceCount) {
                    int calculatedSpaceSize = currentWhitespaceCount - parentWhitespaceCount;
                    // Only use calculated size if it's valid and reasonable
                    if (calculatedSpaceSize > 0 && calculatedSpaceSize <= 8) {
                        dynamicSpaceSize = calculatedSpaceSize;
                    }
                }
            }
        }

// Use dynamically calculated space size
        return countIndentations(currentWhitespaceCount, basedOnTabCounts, dynamicSpaceSize);
    }

    private void sectionSymbolsParserHelper(String untrimmedLine, boolean basedOnTabCounts, RENPY_SYMBOL_TYPE symbolType) {
        renpyGenericBlockSymbol theSymbol = null;
        if (symbolType == RENPY_LABEL) {
            theSymbol = new renpyLabel(RENPY_LABEL, untrimmedLine);
        } else {
            theSymbol = new renpyGenericBlockSymbol(RENPY_GENERIC_BLOCK_SYMBOLS, untrimmedLine);
        }

        int h = getDynmaicIndent(basedOnTabCounts, untrimmedLine);
        if (h == CURRENT_HIERARCHY) {
            /*
             * label z:
             * label k: <--same hierarchy
             */
            renpySymbol detectedParent = getParentHierarchySymbol(h, previousChainParent);
            theSymbol.setHIERARCHY_PARENT_SYMBOL(detectedParent);
            detectedParent.addHIERARCHY_CHILD_SYMBOL(theSymbol);

            theSymbol.setHIERARCHY_LEVEL(detectedParent.getHIERARCHY_LEVEL() + 1);
            CURRENT_HIERARCHY = detectedParent.getHIERARCHY_LEVEL() + 1;

        } else if (h > CURRENT_HIERARCHY) {
            /*
             * label one:
             *      label nine: <----this
             */
            h = CURRENT_HIERARCHY + 1;
            theSymbol.setHIERARCHY_PARENT_SYMBOL(previousHierarchyParent);
            previousHierarchyParent.addHIERARCHY_CHILD_SYMBOL(theSymbol);

            theSymbol.setHIERARCHY_LEVEL(h);
            CURRENT_HIERARCHY = h;
        } else {
            /*
             * label one:
             *      label two:
             *          label three
             *      label four: <---- here
             */
            renpySymbol detectedParent = getParentHierarchySymbol(h, previousChainParent); //<--label one
            theSymbol.setHIERARCHY_PARENT_SYMBOL(detectedParent);
            detectedParent.addHIERARCHY_CHILD_SYMBOL(theSymbol);
            theSymbol.setHIERARCHY_LEVEL(detectedParent.getHIERARCHY_LEVEL() + 1);
            CURRENT_HIERARCHY = detectedParent.getHIERARCHY_LEVEL() + 1;
        }
        previousChainParent.setCHAIN_CHILD_SYMBOL(theSymbol);
        theSymbol.setCHAIN_PARENT_SYMBOL(previousChainParent);

        previousHierarchyParent = theSymbol;
        previousChainParent = theSymbol;
    }

    private void nonSectionSymbolsParserHelper(String untrimmedLine, boolean basedOnTabCounts, RENPY_SYMBOL_TYPE symbolType) {
        renpyGenericNonBlockSymbol theSymbol = null;
        if (symbolType == RENPY_SPEAKER_TEXT) {
            theSymbol = new renpySpeakerText(symbolType, untrimmedLine);
        } else if (symbolType == RENPY_NO_SPEAKER_TEXT) {
            theSymbol = new renpyNoSpeakerText(symbolType, untrimmedLine);
        } else if (symbolType == RENPY_CALL) {
            theSymbol = new renpyCall(symbolType, untrimmedLine);
            String labelName = ((renpyCall) theSymbol).getCallINTOLabelName();
            ArrayList<renpySymbol> list = pathMatrix.get(labelName);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(theSymbol);
            pathMatrix.put(labelName, list);
        } else if (symbolType == RENPY_JUMP) {
            theSymbol = new renpyJump(symbolType, untrimmedLine);
            String labelName = ((renpyJump) theSymbol).getJumpINTOLabelName();
            ArrayList<renpySymbol> list = pathMatrix.get(labelName);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(theSymbol);
            pathMatrix.put(labelName, list);
        } else if (symbolType == RENPY_RETURN) {
            theSymbol = new renpyReturn(symbolType, untrimmedLine);
        } else {
            theSymbol = new renpyGenericNonBlockSymbol(symbolType, untrimmedLine);
        }

        int h = getDynmaicIndent(basedOnTabCounts, untrimmedLine);
        if (h == CURRENT_HIERARCHY) {
            /*
             * label z:
             * Anwar "h" <--same hierarchy
             */
            renpySymbol detectedParent = getParentHierarchySymbol(h, previousChainParent);
            theSymbol.setHIERARCHY_PARENT_SYMBOL(detectedParent);
            detectedParent.addHIERARCHY_CHILD_SYMBOL(theSymbol);
            previousHierarchyParent = detectedParent;
            CURRENT_HIERARCHY = detectedParent.getHIERARCHY_LEVEL();
        } else if (h > CURRENT_HIERARCHY) {
            /*
             * label one:
             *      Anwar "Hai" <----this
             */

            h = CURRENT_HIERARCHY + 1;
            theSymbol.setHIERARCHY_PARENT_SYMBOL(previousHierarchyParent);
            previousHierarchyParent.addHIERARCHY_CHILD_SYMBOL(theSymbol);
            CURRENT_HIERARCHY = previousHierarchyParent.getHIERARCHY_LEVEL();
        } else {
            /*
             * label one:
             *      label two:
             *          label three
             *      anwar "hello" <---- here
             */
            renpySymbol detectedParent = getParentHierarchySymbol(h, previousChainParent); //<--label one
            theSymbol.setHIERARCHY_PARENT_SYMBOL(detectedParent);
            detectedParent.addHIERARCHY_CHILD_SYMBOL(theSymbol);
            previousHierarchyParent = detectedParent;
            CURRENT_HIERARCHY = previousHierarchyParent.getHIERARCHY_LEVEL();
        }
        theSymbol.setHIERARCHY_LEVEL(h);
        previousChainParent.setCHAIN_CHILD_SYMBOL(theSymbol);
        theSymbol.setCHAIN_PARENT_SYMBOL(previousChainParent);

        previousChainParent = theSymbol;
    }

    public renpySymbol getParentHierarchySymbol(int current_hierarchy, renpySymbol assumedParent) {
        renpySymbol tempParent = assumedParent;
        while (true) {
            if (tempParent.getHIERARCHY_LEVEL() < current_hierarchy) {
                return tempParent;
            } else {
                tempParent = tempParent.getHIERARCHY_PARENT_SYMBOL();
            }
        }
    }
}