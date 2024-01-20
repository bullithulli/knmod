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
    final String regex_detect_for_block_symbols = "^\\s*([a-zA-Z_,.$\\-\"'’\\?][a-zA-Z0-9_А-Яа-яЁё,<>.…&+\\-=\"(\\)\\{\\}\\s\\t”“#’'\\?\\|\\]\\[!]*)\\s*[:|{]\\s*(?:#.*)?$";
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
                    String underDoubleQuotes = m.group();
                    key = getUncommentedString(underDoubleQuotes);
                }
            } else if (pattern_for_speaker_text.matcher(trimmedLine).find() && keyFound) {
                Matcher m = pattern_get_between_doubleQuotes.matcher(trimmedLine);
                if (m.find()) {
                    String underDoubleQuotes = m.group();
                    value = getUncommentedString(underDoubleQuotes);
                    translationMap.put(key, value);
                }
                keyFound = false;
            }
        }
        sc.close();
        return translationMap;
    }


    public renpySymbol parseLine(String lines, boolean basedOnTabCounts, int spaceSize) {
        // TODO: 1/13/24 add init based initialization for GlobalVariables, so multiple calls on parseLine will not affect rootSymbol
        String[] linesArray = lines.split("\n");
        for (String untrimmedLine : linesArray) {
            String trimmedLine = untrimmedLine.trim();
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                continue;//skip new lines, empty lines
            }
            String symbol = trimmedLine.split("\\s+")[0];
            if (pattern_for_block_symbols.matcher(untrimmedLine).find()) {
                if (symbol.startsWith("label")) {
                    sectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_LABEL);
                } else {
                    sectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_GENERIC_BLOCK_SYMBOLS);
                }
            } else {
                if (pattern_for_speaker_text.matcher(trimmedLine).find()) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_SPEAKER_TEXT);
                } else if (pattern_for_no_speaker_texts.matcher(trimmedLine).find()) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_NO_SPEAKER_TEXT);
                } else if (trimmedLine.startsWith("return")) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_RETURN);
                } else if (trimmedLine.startsWith("jump ")) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_JUMP);
                } else if (trimmedLine.startsWith("call ")) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_CALL);
                } else {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_GENERIC_NON_BLOCK_SYMBOLS);
                }
            }
        }
        return root;
    }

    public renpySymbol parseFrom(File file, boolean basedOnTabCounts, int spaceSize, boolean requiredTranslate, File tl_file) throws FileNotFoundException {
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
                    String underDoubleQuotes = m.group();
                    untrimmedLine = untrimmedLine.replace(underDoubleQuotes, translationTable.getOrDefault(underDoubleQuotes, underDoubleQuotes));
                    trimmedLine = untrimmedLine.trim();
                }
            }

            String symbol = trimmedLine.split("\\s+")[0];
            if (pattern_for_block_symbols.matcher(untrimmedLine).find()) {
                if (symbol.startsWith("label")) {
                    sectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_LABEL);
                } else {
                    sectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_GENERIC_BLOCK_SYMBOLS);
                }
            } else {
                if (pattern_for_speaker_text.matcher(trimmedLine).find()) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_SPEAKER_TEXT);
                } else if (pattern_for_no_speaker_texts.matcher(trimmedLine).find()) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_NO_SPEAKER_TEXT);
                } else if (trimmedLine.startsWith("return")) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_RETURN);
                } else if (trimmedLine.startsWith("jump ")) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_JUMP);
                } else if (trimmedLine.startsWith("call ")) {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_CALL);
                } else {
                    nonSectionSymbolsParserHelper(untrimmedLine, basedOnTabCounts, spaceSize, RENPY_GENERIC_NON_BLOCK_SYMBOLS);
                }
            }
        }
        return root;
    }

    private void sectionSymbolsParserHelper(String untrimmedLine, boolean basedOnTabCounts, int spaceSize, RENPY_SYMBOL_TYPE symbolType) {
        renpyGenericBlockSymbol theSymbol = null;
        if (symbolType == RENPY_LABEL) {
            theSymbol = new renpyLabel(RENPY_LABEL, untrimmedLine);
        } else {
            theSymbol = new renpyGenericBlockSymbol(RENPY_GENERIC_BLOCK_SYMBOLS, untrimmedLine);
        }
        int h = countIndentations(countLeadingWhitespaces(untrimmedLine), basedOnTabCounts, spaceSize);
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

    private void nonSectionSymbolsParserHelper(String untrimmedLine, boolean basedOnTabCounts, int spaceSize, RENPY_SYMBOL_TYPE symbolType) {
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

        int h = countIndentations(countLeadingWhitespaces(untrimmedLine), basedOnTabCounts, spaceSize);
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
// TODO: 12/23/23 Implement parser to solve below
/*
 *
 *  if(incestPath==true){
 *      run this  <-----pull only this
 *  }
 *  else{
 *      run this
 *  }
 *
 *
 *  label x:
 *      shouldBeReplaceBy
 *  label z:
 *      replaceBleContents
 *
 *
 *
 *  label x:
 *      Anwar "Russian Translate"
 *
 *  replace by english
 *
 *
 * Add subtract labels
 *
 */