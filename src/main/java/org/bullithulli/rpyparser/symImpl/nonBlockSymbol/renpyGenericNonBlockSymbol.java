package org.bullithulli.rpyparser.symImpl.nonBlockSymbol;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;
import org.bullithulli.rpyparser.symImpl.renpySymbol;

public class renpyGenericNonBlockSymbol extends renpySymbol {
    public renpyGenericNonBlockSymbol(RENPY_SYMBOL_TYPE renpySymbolType, String line) {
        super(renpySymbolType, line, false);
    }
}
