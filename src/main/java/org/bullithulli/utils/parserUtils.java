package org.bullithulli.utils;

import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.bullithulli.rpyparser.symImpl.renpySymbol;

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
}
