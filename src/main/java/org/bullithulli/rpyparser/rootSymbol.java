package org.bullithulli.rpyparser;

public class rootSymbol extends renpySymbol {

    public rootSymbol(RENPY_SYMBOL_TYPE renpySymbolType, String line) {
        super(renpySymbolType, line);
        setNUMBER_OF_HIERARCHY(-1);
    }
}
