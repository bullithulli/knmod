package org.bullithulli.rpyparser.exception;

public class renpyUnkownSymbolException extends Exception {
    public renpyUnkownSymbolException(String line) {
        super("Could not parse the symbol " + line + ". UNKNOWN_RENPY_SYMBOL");
    }
}
