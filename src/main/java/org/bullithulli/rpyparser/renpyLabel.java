package org.bullithulli.rpyparser;

import java.util.ArrayList;

public class renpyLabel extends renpySymbol {
    String labelName;

    public renpyLabel(RENPY_SYMBOL_TYPE renpySymbolType, String line) {
        super(renpySymbolType, line);
        labelName = RENPY_TRIMMED_LINE.split("\\s+")[1].replaceAll(":", "").trim();
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

    public String getLabelName() {
        return labelName;
    }
}
