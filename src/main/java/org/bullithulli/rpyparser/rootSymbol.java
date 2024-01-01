package org.bullithulli.rpyparser;

import java.util.ArrayList;

public class rootSymbol extends renpySymbol {

    public rootSymbol(RENPY_SYMBOL_TYPE renpySymbolType, String line) {
        super(renpySymbolType, line);
        //setRenpySymbolType(renpySymbolType);
        setHIERARCHY_LEVEL(-1);
    }

    public ArrayList<renpyLabel> getInnerLabels() {
        ArrayList<renpyLabel> renpyLabels = new ArrayList<>();
        for (renpySymbol symbol : HIERARCHY_CHILD_SYMBOL) {
            if (symbol.getRenpySymbolType() == RENPY_SYMBOL_TYPE.RENPY_LABEL) {
                renpyLabels.add((renpyLabel) symbol);
            }
        }
        return renpyLabels;
    }

    public renpyLabel getInnerLabelByName(String key) {
        for (renpyLabel renpyLabel : getInnerLabels()) {
            if (renpyLabel.getLabelName().equalsIgnoreCase(key)) {
                return renpyLabel;
            }
        }
        return null;
    }
}
