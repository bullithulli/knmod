package org.bullithulli.rpyparser.symImpl.nonBlockSymbol;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;

public class renpyJump extends renpyGenericNonBlockSymbol {
    // TODO: 1/13/24 Store jumpable renpyLabel variable
    public renpyJump(RENPY_SYMBOL_TYPE renpySymbolType, String line) {
        super(renpySymbolType, line);
    }
}
