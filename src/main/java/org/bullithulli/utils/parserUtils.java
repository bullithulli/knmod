package org.bullithulli.utils;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;
import org.bullithulli.rpyparser.parser;
import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.bullithulli.rpyparser.symImpl.nonBlockSymbol.renpyCall;
import org.bullithulli.rpyparser.symImpl.nonBlockSymbol.renpyJump;
import org.bullithulli.rpyparser.symImpl.renpySymbol;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.bullithulli.utils.textUtils.getTabbedString;

public class parserUtils {
    public static void deleteInnerLabel(renpyLabel label_to_be_deleted, parser sourceRenpyParser) {
        sourceRenpyParser.pathMatrix.remove(label_to_be_deleted.getLabelName());
        // TODO: 1/14/24 Not required.
        // REMEMBER, here i'm deleting entire row from pathmatrix of the label. But this leads to confusion among calls and jumps as they are still pointing to this row, but row is already deleted
        renpySymbol parentLabel = label_to_be_deleted.getHIERARCHY_PARENT_SYMBOL();
        parentLabel.removeHIERARCHY_CHILD_SYMBOL(label_to_be_deleted);

        renpySymbol parentSymbol_of_label_to_be_deleted = label_to_be_deleted.getCHAIN_PARENT_SYMBOL();
        renpySymbol endOfLabel_label_to_be_deleted = label_to_be_deleted.getLastChainSymbol(label_to_be_deleted.getHIERARCHY_LEVEL(), false, true, true, true, label_to_be_deleted);
        renpySymbol nextOfLabelEnd = endOfLabel_label_to_be_deleted.getCHAIN_CHILD_SYMBOL();

        parentSymbol_of_label_to_be_deleted.setCHAIN_CHILD_SYMBOL(nextOfLabelEnd);
        if (nextOfLabelEnd != null)
            nextOfLabelEnd.setCHAIN_PARENT_SYMBOL(parentSymbol_of_label_to_be_deleted);
        label_to_be_deleted = null;
        System.gc();
    }

    public static void addLabelAfter(renpySymbol source, parser sourceParser, renpyLabel patchLabel) {
        int adjustHierarchyBy = 0;
        renpySymbol sourceNextChildSymbol = source.getCHAIN_CHILD_SYMBOL();
        renpySymbol end_symbol_of_label_to_be_added = patchLabel.getLastChainSymbol(patchLabel.getHIERARCHY_LEVEL(), false, true, true, true, patchLabel);
        int h = source.getHIERARCHY_LEVEL();
        if (source.isBlockSymbol()) {
            adjustHierarchyBy = 1;
        }

        renpySymbol patchChainParent = patchLabel.getCHAIN_PARENT_SYMBOL();
        patchChainParent.removeHIERARCHY_CHILD_SYMBOL(patchLabel);
        renpySymbol afterDestinationLabel_symbol = end_symbol_of_label_to_be_added.getCHAIN_CHILD_SYMBOL();
        patchChainParent.setCHAIN_CHILD_SYMBOL(afterDestinationLabel_symbol);
        if (afterDestinationLabel_symbol != null)
            afterDestinationLabel_symbol.setCHAIN_PARENT_SYMBOL(patchChainParent);

        renpySymbol destinationHierarchyParent = patchLabel.getHIERARCHY_PARENT_SYMBOL();
        destinationHierarchyParent.removeHIERARCHY_CHILD_SYMBOL(patchLabel);

        ArrayList<renpySymbol> childChainSymbols = patchLabel.getChainSymbols(patchLabel.getHIERARCHY_LEVEL(), false, true, true, true, patchLabel);
        childChainSymbols.remove(patchLabel);//remove label symbol itself from its child-chain symbol list, as we pulled using includeSameHierarchy
        makeLabelHierarchyStartFromZero(patchLabel, childChainSymbols);
        patchLabel.setHIERARCHY_LEVEL(h + adjustHierarchyBy);
        for (renpySymbol symbol : childChainSymbols) {
            symbol.setHIERARCHY_LEVEL(symbol.getHIERARCHY_LEVEL() + h + adjustHierarchyBy);
            if (symbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_CALL) {
                String labelName = ((renpyCall) symbol).getCallINTOLabelName();
                ArrayList<renpySymbol> list = sourceParser.pathMatrix.get(labelName);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(symbol);
                sourceParser.pathMatrix.put(labelName, list);
            } else if (symbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_JUMP) {
                String labelName = ((renpyJump) symbol).getJumpINTOLabelName();
                ArrayList<renpySymbol> list = sourceParser.pathMatrix.get(labelName);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(symbol);
                sourceParser.pathMatrix.put(labelName, list);
            }
        }

        source.setCHAIN_CHILD_SYMBOL(patchLabel);
        patchLabel.setCHAIN_PARENT_SYMBOL(source);

        end_symbol_of_label_to_be_added.setCHAIN_CHILD_SYMBOL(sourceNextChildSymbol);
        if (sourceNextChildSymbol != null)
            sourceNextChildSymbol.setCHAIN_PARENT_SYMBOL(end_symbol_of_label_to_be_added);
        if (source.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_LABEL) {
            patchLabel.setHIERARCHY_PARENT_SYMBOL(source);
            patchLabel.setCHAIN_PARENT_SYMBOL(end_symbol_of_label_to_be_added);
            source.addHIERARCHY_CHILD_SYMBOL(patchLabel);
        }
    }

    // TODO: 1/14/24 public static void addLabelAt(renpySymbol source,int position, parser sourceParser, renpyLabel patchLabel) {
    // TODO: 1/14/24 public static void addLabelAtLeft(renpySymbol source, parser sourceParser, renpyLabel patchLabel) {

    private static void makeLabelHierarchyStartFromZero(renpyLabel label, ArrayList<renpySymbol> childChainSymbols) {
        int h = label.getHIERARCHY_LEVEL();
        for (renpySymbol symbol : childChainSymbols) {
            symbol.setHIERARCHY_LEVEL(symbol.getHIERARCHY_LEVEL() - h);
        }
        label.setHIERARCHY_LEVEL(0);
    }

    /*
        Similar:
        getLastChainSymbol
        getChainSymbols
        getChainString
        writeChainString
     */
    public static void writeChainString(String filePath, renpySymbol start, int subtractIndents, int thresholdingLevel, boolean followLowerHierarchy, boolean includeSameHierarchy) throws IOException {
        renpySymbol current = start;
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false));
        while (current != null) {
            if (includeSameHierarchy) {
                if (current.getHIERARCHY_LEVEL() >= thresholdingLevel) {
                    writer.write(getTabbedString(current.getHIERARCHY_LEVEL() - subtractIndents).concat(current.getRENPY_TRIMMED_LINE()).concat("\n"));
                }
            } else {
                if (current.getHIERARCHY_LEVEL() > thresholdingLevel) {
                    writer.write(getTabbedString(current.getHIERARCHY_LEVEL() - subtractIndents).concat(current.getRENPY_TRIMMED_LINE()).concat("\n"));
                }
            }

            if (current.getCHAIN_CHILD_SYMBOL() != null) {
                if (followLowerHierarchy) {
                    current = current.getCHAIN_CHILD_SYMBOL();
                } else {
                    if (includeSameHierarchy && current.getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() < thresholdingLevel) {
                        break;
                    } else if (!includeSameHierarchy && current.getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() <= thresholdingLevel) {
                        break;
                    } else {
                        current = current.getCHAIN_CHILD_SYMBOL();
                    }
                }
            } else {
                break;
            }
        }
        writer.close();
    }
}
