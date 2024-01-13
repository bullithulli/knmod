package org.bullithulli.rpyparser.symImpl.blockSymbols;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;
import org.bullithulli.rpyparser.symImpl.renpySymbol;

public class renpyGenericBlockSymbol extends renpySymbol {
    public renpyGenericBlockSymbol(RENPY_SYMBOL_TYPE renpySymbolType, String line) {
        super(renpySymbolType, line, true);
    }
}
