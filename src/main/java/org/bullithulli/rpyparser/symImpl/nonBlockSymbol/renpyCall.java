package org.bullithulli.rpyparser.symImpl.nonBlockSymbol;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;

public class renpyCall extends renpyGenericNonBlockSymbol {
    private String CallINTOLabelName;

    public renpyCall(RENPY_SYMBOL_TYPE renpySymbolType, String line) {
        super(renpySymbolType, line);
        CallINTOLabelName = getRENPY_TRIMMED_LINE().split("\\s+")[1];
    }

    public String getCallINTOLabelName() {
        return CallINTOLabelName;
    }

    private void setCallINTOLabelName(String jumpINTOLabelName) {
        this.CallINTOLabelName = jumpINTOLabelName;
    }

    public void updateJumpINTOLabelName(String newLabelName) {
        //path matrix updating is not done here
        String oldLabelName = CallINTOLabelName;
        setRENPY_UNTRIMMED_LINE(getRENPY_UNTRIMMED_LINE().replaceAll(oldLabelName, newLabelName));
        setRENPY_TRIMMED_LINE(getRENPY_TRIMMED_LINE().replaceAll(oldLabelName, newLabelName));
        setCallINTOLabelName(newLabelName);
    }
}
