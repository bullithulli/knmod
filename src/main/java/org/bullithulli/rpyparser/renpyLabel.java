package org.bullithulli.rpyparser;

public class renpyLabel extends renpySymbol {
    String labelName;

    public renpyLabel(RENPY_SYMBOL_TYPE renpySymbolType, String line) {
        super(renpySymbolType, line);
        labelName = RENPY_TRIMMED_LINE.split("\\s+")[1];
    }

    public String getLabelName() {
        return labelName;
    }
}
