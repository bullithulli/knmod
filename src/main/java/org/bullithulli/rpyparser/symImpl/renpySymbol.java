package org.bullithulli.rpyparser.symImpl;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;

import java.util.ArrayList;

import static org.bullithulli.utils.textUtils.getTabbedString;

public abstract class renpySymbol {
    public String RENPY_UNTRIMMED_LINE;
    public String RENPY_TRIMMED_LINE;
    int HIERARCHY_LEVEL = 0;
    RENPY_SYMBOL_TYPE renpySymbolType;
    renpySymbol HIERARCHY_PARENT_SYMBOL;
    ArrayList<renpySymbol> HIERARCHY_CHILD_SYMBOL = new ArrayList<>();
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

    public int getHIERARCHY_LEVEL() {
        return HIERARCHY_LEVEL;
    }

    public void setHIERARCHY_LEVEL(int HIERARCHY_LEVEL) {
        this.HIERARCHY_LEVEL = HIERARCHY_LEVEL;
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

    public ArrayList<renpySymbol> getHIERARCHY_CHILD_SYMBOL() {
        return HIERARCHY_CHILD_SYMBOL;
    }

    public void addHIERARCHY_CHILD_SYMBOL(renpySymbol HIERARCHY_CHILD_SYMBOL) {
        this.HIERARCHY_CHILD_SYMBOL.add(HIERARCHY_CHILD_SYMBOL);
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

    public String getChainString(int subtractIndents, int thresholdingLevel, boolean followLowerHierarchy, boolean includeSameHierarchy) {
        String out = "";
        if (includeSameHierarchy) {
            if (getHIERARCHY_LEVEL() >= thresholdingLevel) {
                out = getTabbedString(HIERARCHY_LEVEL - subtractIndents).concat(getRENPY_TRIMMED_LINE()).concat("\n");
            }
        } else {
            if (getHIERARCHY_LEVEL() > thresholdingLevel) {
                out = getTabbedString(HIERARCHY_LEVEL - subtractIndents).concat(getRENPY_TRIMMED_LINE()).concat("\n");
            }
        }

        if (getCHAIN_CHILD_SYMBOL() != null) {
            if (followLowerHierarchy) {
                out += getCHAIN_CHILD_SYMBOL().getChainString(subtractIndents, thresholdingLevel, true, includeSameHierarchy);
            } else {
                /*
                if SameHierarchy content display is enabled and if nextLine of rpy code is in next low level hierarchy stop processing child.
                 */
                if (includeSameHierarchy && getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() < thresholdingLevel) {
                    return out;
                } else if (!includeSameHierarchy && getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() <= thresholdingLevel) {
                    return out;
                } else {
                    out += getCHAIN_CHILD_SYMBOL().getChainString(subtractIndents, thresholdingLevel, false, includeSameHierarchy);
                }
            }
        }
        return out;
    }

    public String toString() {
        return getRENPY_UNTRIMMED_LINE();
    }

    public ArrayList<renpySymbol> getChainSymbols(int thresholdingLevel, boolean followLowerHierarchy, boolean includeSameHierarchy) {
        ArrayList<renpySymbol> list = new ArrayList<>();
        if (includeSameHierarchy) {
            if (getHIERARCHY_LEVEL() >= thresholdingLevel) {
                list.add(this);
            }
        } else {
            if (getHIERARCHY_LEVEL() > thresholdingLevel) {
                list.add(this);
            }
        }

        if (getCHAIN_CHILD_SYMBOL() != null) {
            if (followLowerHierarchy) {
                list.addAll(getCHAIN_CHILD_SYMBOL().getChainSymbols(thresholdingLevel, true, includeSameHierarchy));
            } else {
                /*
                if SameHierarchy content display is enabled and if nextLine of rpy code is in next low level hierarchy stop processing child.
                 */
                if (includeSameHierarchy && getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() < thresholdingLevel) {
                    return list;
                } else if (!includeSameHierarchy && getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() <= thresholdingLevel) {
                    return list;
                } else {
                    list.addAll(getCHAIN_CHILD_SYMBOL().getChainSymbols(thresholdingLevel, false, includeSameHierarchy));
                }
            }
        }
        return list;
    }
}
