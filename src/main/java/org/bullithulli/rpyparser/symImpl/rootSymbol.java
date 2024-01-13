package org.bullithulli.rpyparser.symImpl;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;
import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;

import java.util.ArrayList;

public class rootSymbol extends renpySymbol {

    public rootSymbol(RENPY_SYMBOL_TYPE renpySymbolType, String line) {
        super(renpySymbolType, line, true);
        //setRenpySymbolType(renpySymbolType);
        setHIERARCHY_LEVEL(-1);
    }

    public ArrayList<renpyLabel> getInnerLabels() {
        ArrayList<renpyLabel> renpyLabels = new ArrayList<>();
        for (renpySymbol symbol : getHIERARCHY_CHILD_SYMBOL()) {
            if (symbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_LABEL) {
                renpyLabels.add((renpyLabel) symbol);
            }
        }
        return renpyLabels;
    }

    public renpyLabel getInnerLabelByName(String target_label_name) {
        for (renpyLabel renpyLabel : getInnerLabels()) {
            if (renpyLabel.getLabelName().equalsIgnoreCase(target_label_name)) {
                return renpyLabel;
            }
        }
        return null;
    }

    public renpyLabel getInnerLabelByNameSearchRecursivly(String target_label_name) {
        for (renpyLabel renpyLabel : getInnerLabels()) {
            if (renpyLabel.getLabelName().equalsIgnoreCase(target_label_name)) {
                return renpyLabel;
            } else {
                renpyLabel innerLabel = renpyLabel.getInnerLabelByNameSearchRecursivly(target_label_name);
                if (innerLabel != null) {
                    return innerLabel;  // Return the result of the recursive call if found
                }
            }
        }
        return null;
    }
}
