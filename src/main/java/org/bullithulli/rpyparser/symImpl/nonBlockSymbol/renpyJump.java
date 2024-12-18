package org.bullithulli.rpyparser.symImpl.nonBlockSymbol;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;

public class renpyJump extends renpyGenericNonBlockSymbol {
	private String JumpINTOLabelName;

	public renpyJump(RENPY_SYMBOL_TYPE renpySymbolType, String line) {
		super(renpySymbolType, line);
		JumpINTOLabelName = getRENPY_TRIMMED_LINE().split("\\s+")[1];
	}

	public String getJumpINTOLabelName() {
		return JumpINTOLabelName;
	}

	private void setJumpINTOLabelName(String jumpINTOLabelName) {
		this.JumpINTOLabelName = jumpINTOLabelName;
	}

	public void updateJumpINTOLabelName(String to) {
		String from = JumpINTOLabelName;
		setRENPY_UNTRIMMED_LINE(getRENPY_UNTRIMMED_LINE().replaceAll(from, to));
		setRENPY_TRIMMED_LINE(getRENPY_TRIMMED_LINE().replaceAll(from, to));
		setJumpINTOLabelName(to);
	}
}
