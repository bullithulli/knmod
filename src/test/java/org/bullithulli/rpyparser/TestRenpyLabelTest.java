package org.bullithulli.rpyparser;

import org.bullithulli.rpyparser.exception.renpyUnkownSymbolException;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TestRenpyLabelTest {
    @Test
    public void test1() throws renpyUnkownSymbolException {
        String rpyCode = "label one:\n" +
                "\tlabel two:\n" +
                "\t\tlabel three:\n" +
                "\tlabel four:\n" +
                "\tlabel five:\n" +
                "label six:\n" +
                "\tlabel seven:";
        parser renpyParser = new parser();
        rootSymbol root = (rootSymbol) renpyParser.parseLine(rpyCode, true, 2);
        ArrayList<renpySymbol> symbols = root.getChainSymbols(root.getHIERARCHY_LEVEL(), true, true);
        assertEquals(symbols.size(), 8);
        renpySymbol one = symbols.get(1);
        assertEquals(4, one.getChainSymbols(one.getHIERARCHY_LEVEL(), false, false).size());
    }
}
