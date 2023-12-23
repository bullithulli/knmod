package org.bullithulli.rpyparser;

import static org.bullithulli.utils.textUtils.getTabbedString;

public abstract class renpySymbol {
    public String RENPY_UNTRIMMED_LINE;
    public String RENPY_TRIMMED_LINE;
    int NUMBER_OF_HIERARCHY = 0;
    RENPY_SYMBOL_TYPE renpySymbolType;
    renpySymbol HIERARCHY_PARENT_SYMBOL;
    renpySymbol HIERARCHY_CHILD_SYMBOL;
    renpySymbol CHAIN_PARENT_SYMBOL;
    renpySymbol CHAIN_CHILD_SYMBOL;

    public renpySymbol(RENPY_SYMBOL_TYPE renpySymbolType, String line) {
        setRenpySymbolType(renpySymbolType);
        setRENPY_UNTRIMMED_LINE(line);
        setRENPY_TRIMMED_LINE(line.trim());
    }

    public String getRENPY_UNTRIMMED_LINE() {
        return RENPY_UNTRIMMED_LINE;
    }

    public void setRENPY_UNTRIMMED_LINE(String RENPY_UNTRIMMED_LINE) {
        this.RENPY_UNTRIMMED_LINE = RENPY_UNTRIMMED_LINE;
    }

    public String getRENPY_TRIMMED_LINE() {
        return RENPY_TRIMMED_LINE;
    }

    public void setRENPY_TRIMMED_LINE(String RENPY_TRIMMED_LINE) {
        this.RENPY_TRIMMED_LINE = RENPY_TRIMMED_LINE;
    }

    public int getNUMBER_OF_HIERARCHY() {
        return NUMBER_OF_HIERARCHY;
    }

    public void setNUMBER_OF_HIERARCHY(int NUMBER_OF_HIERARCHY) {
        this.NUMBER_OF_HIERARCHY = NUMBER_OF_HIERARCHY;
    }

    public RENPY_SYMBOL_TYPE getRenpySymbolType() {
        return renpySymbolType;
    }

    public void setRenpySymbolType(RENPY_SYMBOL_TYPE renpySymbolType) {
        this.renpySymbolType = renpySymbolType;
    }

    public renpySymbol getHIERARCHY_PARENT_SYMBOL() {
        return HIERARCHY_PARENT_SYMBOL;
    }

    public void setHIERARCHY_PARENT_SYMBOL(renpySymbol HIERARCHY_PARENT_SYMBOL) {
        this.HIERARCHY_PARENT_SYMBOL = HIERARCHY_PARENT_SYMBOL;
    }

    public renpySymbol getHIERARCHY_CHILD_SYMBOL() {
        return HIERARCHY_CHILD_SYMBOL;
    }

    public void setHIERARCHY_CHILD_SYMBOL(renpySymbol HIERARCHY_CHILD_SYMBOL) {
        this.HIERARCHY_CHILD_SYMBOL = HIERARCHY_CHILD_SYMBOL;
    }

    public renpySymbol getCHAIN_PARENT_SYMBOL() {
        return CHAIN_PARENT_SYMBOL;
    }

    public void setCHAIN_PARENT_SYMBOL(renpySymbol CHAIN_PARENT_SYMBOL) {
        this.CHAIN_PARENT_SYMBOL = CHAIN_PARENT_SYMBOL;
    }

    public renpySymbol getCHAIN_CHILD_SYMBOL() {
        return CHAIN_CHILD_SYMBOL;
    }

    public void setCHAIN_CHILD_SYMBOL(renpySymbol CHAIN_CHILD_SYMBOL) {
        this.CHAIN_CHILD_SYMBOL = CHAIN_CHILD_SYMBOL;
    }

    public String getChainString() {
        String out = getTabbedString(NUMBER_OF_HIERARCHY).concat(getRENPY_TRIMMED_LINE()).concat("\n");
        if (getCHAIN_CHILD_SYMBOL() != null) {
            out += getCHAIN_CHILD_SYMBOL().getChainString();
        }
        return out;
    }

    public String toString() {
        return getRENPY_UNTRIMMED_LINE();
    }
}
