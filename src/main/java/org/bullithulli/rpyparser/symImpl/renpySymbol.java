package org.bullithulli.rpyparser.symImpl;

import lombok.Getter;
import lombok.Setter;
import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;

import java.util.ArrayList;

import static org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE.RENPY_JUMP;
import static org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE.RENPY_LABEL;
import static org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE.RENPY_RETURN;
import static org.bullithulli.utils.textUtils.getTabbedString;

public abstract class renpySymbol {
	@Setter
	@Getter
	public String RENPY_UNTRIMMED_LINE;
	@Getter
	@Setter
	public String RENPY_TRIMMED_LINE;
	@Setter
	@Getter
	int HIERARCHY_LEVEL = 0;
	@Setter
	@Getter
	RENPY_SYMBOL_TYPE renpySymbolType;
	@Setter
	@Getter
	renpySymbol HIERARCHY_PARENT_SYMBOL;
	@Getter
	ArrayList<renpySymbol> HIERARCHY_CHILD_SYMBOL = new ArrayList<>();
	@Setter
	@Getter
	renpySymbol CHAIN_PARENT_SYMBOL;
	@Setter
	@Getter
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

	public void addHIERARCHY_CHILD_SYMBOL(renpySymbol HIERARCHY_CHILD_SYMBOL) {
		this.HIERARCHY_CHILD_SYMBOL.add(HIERARCHY_CHILD_SYMBOL);
	}

	public void removeHIERARCHY_CHILD_SYMBOL(renpySymbol HIERARCHY_CHILD_SYMBOL) {
		this.HIERARCHY_CHILD_SYMBOL.remove(HIERARCHY_CHILD_SYMBOL);
	}

	/*
	Similar:
	getLastChainSymbol
getChainSymbols
getChainString
writeChainString
 */
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

	/*
	Similar:
	getLastChainSymbol
getChainSymbols
getChainString
writeChainString
 */
	public ArrayList<renpySymbol> getChainSymbols(int thresholdingLevel, boolean followLowerHierarchy, boolean includeSameHierarchy, boolean stopOnJumpOrReturn, boolean stopOnNewLabelFound, renpySymbol source) {
		ArrayList<renpySymbol> list = new ArrayList<>();
		//End Check
		RENPY_SYMBOL_TYPE TYPE = getRenpySymbolType();
		if (stopOnJumpOrReturn || stopOnNewLabelFound) {
			if (stopOnNewLabelFound && TYPE == RENPY_LABEL && getHIERARCHY_LEVEL() == thresholdingLevel && this != source) {
				return list;
			} else if (TYPE == RENPY_RETURN || TYPE == RENPY_JUMP) { //not applicable for followLowerHierarchy
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
				list.addAll(getCHAIN_CHILD_SYMBOL().getChainSymbols(thresholdingLevel, true, includeSameHierarchy, stopOnJumpOrReturn, stopOnNewLabelFound, source));
			} else {
                /*
                if SameHierarchy content display is enabled and if nextLine of rpy code is in next low level hierarchy stop processing child.
                 */
				if (includeSameHierarchy && getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() < thresholdingLevel) {
					return list;
				} else if (!includeSameHierarchy && getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() <= thresholdingLevel) {
					return list;
				} else {
					list.addAll(getCHAIN_CHILD_SYMBOL().getChainSymbols(thresholdingLevel, false, includeSameHierarchy, stopOnJumpOrReturn, stopOnNewLabelFound, source));
				}
			}
		}
		return list;
	}

	/**
	 * when followLowerHierarchy is enabled, no point of setting includeSameHierarchy to false
	 * <p>
	 * Similar:
	 * getLastChainSymbol
	 * getChainSymbols
	 * getChainString
	 * writeChainString
	 *
	 * @param thresholdingLevel
	 * @param followLowerHierarchy
	 * @param includeSameHierarchy
	 * @return
	 */
	public renpySymbol getLastChainSymbol(int thresholdingLevel, boolean followLowerHierarchy, boolean includeSameHierarchy, boolean stopOnJumpOrReturn, boolean stopOnNewLabelFound, renpySymbol source) {
		RENPY_SYMBOL_TYPE TYPE = getRenpySymbolType();
		if (stopOnJumpOrReturn || stopOnNewLabelFound) {
			if (stopOnNewLabelFound && TYPE == RENPY_LABEL && getHIERARCHY_LEVEL() == thresholdingLevel && this != source) {
				return getCHAIN_PARENT_SYMBOL();
			} else if (TYPE == RENPY_RETURN || TYPE == RENPY_JUMP) {
				if ((getHIERARCHY_LEVEL() == thresholdingLevel || getHIERARCHY_LEVEL() == thresholdingLevel + 1)) {
					return this;
				}
			}
		}

		if (getCHAIN_CHILD_SYMBOL() != null) {
			if (followLowerHierarchy) {
				return getCHAIN_CHILD_SYMBOL().getLastChainSymbol(thresholdingLevel, true, includeSameHierarchy, stopOnJumpOrReturn, stopOnNewLabelFound, source);
			} else {
                /*
                if SameHierarchy content display is enabled and if nextLine of rpy code is in next low level hierarchy stop processing child.
                 */
				if (includeSameHierarchy && getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() < thresholdingLevel) {
					return this;
				} else if (!includeSameHierarchy && getCHAIN_CHILD_SYMBOL().getHIERARCHY_LEVEL() <= thresholdingLevel) {
					return this;
				} else {
					return getCHAIN_CHILD_SYMBOL().getLastChainSymbol(thresholdingLevel, false, includeSameHierarchy, stopOnJumpOrReturn, stopOnNewLabelFound, source);
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
