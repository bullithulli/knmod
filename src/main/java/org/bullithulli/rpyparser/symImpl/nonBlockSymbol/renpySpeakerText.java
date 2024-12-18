package org.bullithulli.rpyparser.symImpl.nonBlockSymbol;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;

public class renpySpeakerText extends renpyGenericNonBlockSymbol {
	private String who;

	public renpySpeakerText(RENPY_SYMBOL_TYPE renpySymbolType, String line) {
		super(renpySymbolType, line);
		setWho(line.split("\\s+")[0]);
	}

	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}
}
