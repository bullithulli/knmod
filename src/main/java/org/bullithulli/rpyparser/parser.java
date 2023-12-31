package org.bullithulli.rpyparser;

import org.bullithulli.rpyparser.exception.renpyUnkownSymbolException;

import java.util.regex.Pattern;

import static org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE.*;
import static org.bullithulli.utils.textUtils.countIndentations;
import static org.bullithulli.utils.textUtils.countLeadingWhitespaces;

public class parser {
    public renpySymbol parseLine(String lines, boolean basedOnTabCounts, int spaceSize) throws renpyUnkownSymbolException {
        rootSymbol root = new rootSymbol(ROOT_FILE, "\n");
        int CURRENT_HIERARCHY = 0;
        renpySymbol previousHierarchyParent = root;
        renpySymbol previousChainParent = root;
        final String regex_detect_for_labels = "^[a-z|A-Z]\\w*\\s+\".+";
        Pattern pattern_for_labels = Pattern.compile(regex_detect_for_labels);

        String[] linesArray = lines.split("\n");
        for (String untrimmedLine : linesArray) {
            String trimmedLine = untrimmedLine.trim();
            String symbol = trimmedLine.split("\\s+")[0];
            if (symbol.startsWith("label") || symbol.startsWith("label:")) {
                renpyLabel label = new renpyLabel(RENPY_LABEL, untrimmedLine);
                int h = countIndentations(countLeadingWhitespaces(untrimmedLine), basedOnTabCounts, spaceSize);
                if (h == CURRENT_HIERARCHY) {
                    /*
                     * label z:
                     * label k: <--same hierarchy
                     */
                    renpySymbol detectedParent = getParentHierarchySymbol(h, previousChainParent);
                    label.setHIERARCHY_PARENT_SYMBOL(detectedParent);
                    detectedParent.addHIERARCHY_CHILD_SYMBOL(label);

                } else if (h > CURRENT_HIERARCHY) {
                    /*
                     * label one:
                     *      label nine: <----this
                     */
                    h = CURRENT_HIERARCHY + 1;
                    label.setHIERARCHY_PARENT_SYMBOL(previousHierarchyParent);
                    previousHierarchyParent.addHIERARCHY_CHILD_SYMBOL(label);
                } else {
                    /*
                     * label one:
                     *      label two:
                     *          label three
                     *      label four: <---- here
                     */
                    renpySymbol detectedParent = getParentHierarchySymbol(h, previousChainParent); //<--label one
                    label.setHIERARCHY_PARENT_SYMBOL(detectedParent);
                    detectedParent.addHIERARCHY_CHILD_SYMBOL(label);
                }
                label.setHIERARCHY_LEVEL(h);
                previousChainParent.setCHAIN_CHILD_SYMBOL(label);
                label.setCHAIN_PARENT_SYMBOL(previousChainParent);

                previousHierarchyParent = label;
                previousChainParent = label;
                CURRENT_HIERARCHY = h;
            } else if (pattern_for_labels.matcher(trimmedLine).find()) {
                // TODO: 12/24/23 speakTextParsingHelper(untrimmedLine,basedOnTabCounts,spaceSize);
            } else {
                throw new renpyUnkownSymbolException(trimmedLine);
            }
        }
        return root;
    }

    private void speakTextParsingHelper(String untrimmedLine, boolean basedOnTabCounts, int spaceSize, int CURRENT_HIERARCHY) {
        renpySpeakerText speakerText = new renpySpeakerText(RENPY_SPEAKER_TEXT, untrimmedLine);
        int h = countIndentations(countLeadingWhitespaces(untrimmedLine), basedOnTabCounts, spaceSize);
        if (h == CURRENT_HIERARCHY) {
            /*
             * label z:
             * Anwar "h" <--same hierarchy
             */
        }
    }

    // TODO: 12/24/23 complete this function 
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
// TODO: 12/23/23 Implemet parser to solve below
/*
 *
 *  if(incentPath==true){
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
 *      replaceBleContetns
 *
 *
 *
 *  label x:
 *      Anwar "Russian Trnslate"
 *
 *  replace by english
 *
 *
 * Add subtract labels
 *
 */