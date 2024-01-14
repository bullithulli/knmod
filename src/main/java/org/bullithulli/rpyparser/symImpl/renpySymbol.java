package org.bullithulli.rpyparser.symImpl;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;

import java.util.ArrayList;

import static org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE.RENPY_JUMP;
import static org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE.RENPY_RETURN;
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
    boolean isBlockSymbol;

    public renpySymbol(RENPY_SYMBOL_TYPE renpySymbolType, String line, boolean isBlockSymbol) {
        setRenpySymbolType(renpySymbolType);
        setRENPY_UNTRIMMED_LINE(line);
        setRENPY_TRIMMED_LINE(line.trim());
        setBlockSymbol(isBlockSymbol);
    }

    public boolean isBlockSymbol() {
        return isBlockSymbol;
    }

    public void setBlockSymbol(boolean blockSymbol) {
        isBlockSymbol = blockSymbol;
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

    public void removeHIERARCHY_CHILD_SYMBOL(renpySymbol HIERARCHY_CHILD_SYMBOL) {
        this.HIERARCHY_CHILD_SYMBOL.remove(HIERARCHY_CHILD_SYMBOL);
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

    public ArrayList<renpySymbol> getChainSymbols(int thresholdingLevel, boolean followLowerHierarchy, boolean includeSameHierarchy, boolean stopOnJumpOrReturn) {
        ArrayList<renpySymbol> list = new ArrayList<>();
        //End Check
        RENPY_SYMBOL_TYPE TYPE = getRenpySymbolType();
        if (stopOnJumpOrReturn) {// && (getHIERARCHY_LEVEL() == thresholdingLevel || getHIERARCHY_LEVEL() == thresholdingLevel + 1) && includeSameHierarchy) {
            if (TYPE == RENPY_RETURN || TYPE == RENPY_JUMP) { //not applicable for followLowerHierarchy
                if ((getHIERARCHY_LEVEL() == thresholdingLevel || getHIERARCHY_LEVEL() == thresholdingLevel + 1)) {
                    list.add(this);
                    return list;
                }
            }
        }

        //add self?
        if (includeSameHierarchy) {
            if (getHIERARCHY_LEVEL() >= thresholdingLevel) {
                list.add(this);
            }
        } else {
            if (getHIERARCHY_LEVEL() > thresholdingLevel) {
                list.add(this);
            }
        }

        //Do recursion
        if (getCHAIN_CHILD_SYMBOL() != null) {
            if (followLowerHierarchy) {
                list.addAll(getCHAIN_CHILD_SYMBOL().getChainSymbols(thresholdingLevel, true, includeSameHierarchy, stopOnJumpOrReturn));
            } else {
                /*
                if SameHierarchy content display is enabled and if nextLine of rpy code is in next low level hierarchy stop processing child.
                 */
                if (includeSameHierarchy && getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() < thresholdingLevel) {
                    return list;
                } else if (!includeSameHierarchy && getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() <= thresholdingLevel) {
                    return list;
                } else {
                    list.addAll(getCHAIN_CHILD_SYMBOL().getChainSymbols(thresholdingLevel, false, includeSameHierarchy, stopOnJumpOrReturn));
                }
            }
        }
        return list;
    }

    /**
     * when followLowerHierarchy is enabled, no point of setting includeSameHierarchy to false
     *
     * @param thresholdingLevel
     * @param followLowerHierarchy
     * @param includeSameHierarchy
     * @return
     */
    public renpySymbol getLastChainSymbol(int thresholdingLevel, boolean followLowerHierarchy, boolean includeSameHierarchy, boolean stopOnJumpOrReturn) {
        RENPY_SYMBOL_TYPE TYPE = getRenpySymbolType();
        if (stopOnJumpOrReturn) {// && (getHIERARCHY_LEVEL() == thresholdingLevel || getHIERARCHY_LEVEL() == thresholdingLevel + 1) && includeSameHierarchy) {
            if (TYPE == RENPY_RETURN || TYPE == RENPY_JUMP) { //not applicable for followLowerHierarchy
                if ((getHIERARCHY_LEVEL() == thresholdingLevel || getHIERARCHY_LEVEL() == thresholdingLevel + 1)) {
                    return this;
                }
            }
        }

        if (getCHAIN_CHILD_SYMBOL() != null) {
            if (followLowerHierarchy) {
                return getCHAIN_CHILD_SYMBOL().getLastChainSymbol(thresholdingLevel, true, includeSameHierarchy, stopOnJumpOrReturn);
            } else {
                /*
                if SameHierarchy content display is enabled and if nextLine of rpy code is in next low level hierarchy stop processing child.
                 */
                if (includeSameHierarchy && getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() < thresholdingLevel) {
                    return this;
                } else if (!includeSameHierarchy && getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() <= thresholdingLevel) {
                    return this;
                } else {
                    return getCHAIN_CHILD_SYMBOL().getLastChainSymbol(thresholdingLevel, false, includeSameHierarchy, stopOnJumpOrReturn);
                }
            }
        }
        if (includeSameHierarchy) {
            return this;
        } else {
            if (getHIERARCHY_LEVEL() > thresholdingLevel) {
                return this;
            } else {
                return getCHAIN_PARENT_SYMBOL();
            }
        }
    }
}
