package org.bullithulli.rpyparser.symImpl.blockSymbols;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;

public class renpyLabel extends renpyGenericBlockSymbol {
	String labelName;

	public renpyLabel(RENPY_SYMBOL_TYPE renpySymbolType, String line) {
		super(renpySymbolType, line);
		labelName = RENPY_TRIMMED_LINE.split("\\s+")[1].replaceAll(":", "").trim();
	}

	public String getLabelName() {
		return labelName;
	}
}
