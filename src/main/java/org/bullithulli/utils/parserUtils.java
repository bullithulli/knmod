package org.bullithulli.utils;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;
import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.bullithulli.rpyparser.symImpl.renpySymbol;

import java.util.ArrayList;

public class parserUtils {
    public static void deleteInnerLabel(renpyLabel label_to_be_deleted) {
        renpySymbol parentLabel = label_to_be_deleted.getHIERARCHY_PARENT_SYMBOL();
        parentLabel.removeHIERARCHY_CHILD_SYMBOL(label_to_be_deleted);

        renpySymbol parentSymbol_of_label_to_be_deleted = label_to_be_deleted.getCHAIN_PARENT_SYMBOL();
        renpySymbol endOfLabel_label_to_be_deleted = label_to_be_deleted.getLastChainSymbol(label_to_be_deleted.getHIERARCHY_LEVEL(), false, true, true);
        renpySymbol nextOfLabelEnd = endOfLabel_label_to_be_deleted.getCHAIN_CHILD_SYMBOL();

        parentSymbol_of_label_to_be_deleted.setCHAIN_CHILD_SYMBOL(nextOfLabelEnd);
        if (nextOfLabelEnd != null)
            nextOfLabelEnd.setCHAIN_PARENT_SYMBOL(parentSymbol_of_label_to_be_deleted);
        label_to_be_deleted = null;
        System.gc();
    }

    public static void addLabelAfter(renpySymbol source, renpyLabel label_to_be_added) {
        int adjustHierarchyBy = 0;
        renpySymbol oldSourceNext = source.getCHAIN_CHILD_SYMBOL();
        renpySymbol end_symbol_of_label_to_be_added = label_to_be_added.getLastChainSymbol(label_to_be_added.getHIERARCHY_LEVEL(), false, true, true);
        int h = source.getHIERARCHY_LEVEL();
        if (source.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_LABEL) {
            adjustHierarchyBy = 1;
        }


        ArrayList<renpySymbol> childChainSymbols = label_to_be_added.getChainSymbols(label_to_be_added.getHIERARCHY_LEVEL(), false, true, true);
        childChainSymbols.remove(label_to_be_added);//remove label symbol itself from its child-chain symbol list, as we pulled using includeSameHierarchy
        makeLabelHierarchyStartFromZero(label_to_be_added, childChainSymbols);
        label_to_be_added.setHIERARCHY_LEVEL(h + adjustHierarchyBy);
        for (renpySymbol symbol : childChainSymbols) {
            symbol.setHIERARCHY_LEVEL(symbol.getHIERARCHY_LEVEL() + h + adjustHierarchyBy);
        }

        source.setCHAIN_CHILD_SYMBOL(label_to_be_added);
        label_to_be_added.setCHAIN_PARENT_SYMBOL(source);
        // TODO: 1/13/24 Update global label-id hashmap once new label is added

        end_symbol_of_label_to_be_added.setCHAIN_CHILD_SYMBOL(oldSourceNext);
        if (oldSourceNext != null)
            oldSourceNext.setCHAIN_PARENT_SYMBOL(end_symbol_of_label_to_be_added);
        if (source.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_LABEL) {
            label_to_be_added.setHIERARCHY_PARENT_SYMBOL(source);
            source.addHIERARCHY_CHILD_SYMBOL(label_to_be_added);
        }
    }

    private static void makeLabelHierarchyStartFromZero(renpyLabel label, ArrayList<renpySymbol> childChainSymbols) {
        int h = label.getHIERARCHY_LEVEL();
        for (renpySymbol symbol : childChainSymbols) {
            symbol.setHIERARCHY_LEVEL(symbol.getHIERARCHY_LEVEL() - h);
        }
        label.setHIERARCHY_LEVEL(0);
    }
}
