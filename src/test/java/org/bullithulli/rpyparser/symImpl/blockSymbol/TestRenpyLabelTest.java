package org.bullithulli.rpyparser.symImpl.blockSymbol;

import org.bullithulli.rpyparser.parser;
import org.bullithulli.rpyparser.symImpl.renpySymbol;
import org.bullithulli.rpyparser.symImpl.rootSymbol;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TestRenpyLabelTest {
    @Test
    public void test1() {
        String rpyCode = """
                label one:
                \tlabel two:
                \t\tlabel three:
                \tlabel four:
                \tlabel five:
                label six:
                \tlabel seven:""";
        parser renpyParser = new parser();
        rootSymbol root = (rootSymbol) renpyParser.parseLine(rpyCode, true, 2);
        ArrayList<renpySymbol> symbols = root.getChainSymbols(root.getHIERARCHY_LEVEL(), true, true);
        assertEquals(symbols.size(), 8);
        renpySymbol one = symbols.get(1);
        assertEquals(4, one.getChainSymbols(one.getHIERARCHY_LEVEL(), false, false).size());
    }

    @Test
    public void test2() {
        String rpyCode = """
                label one:
                	label two:
                		label three:
                			label eight:
                		label nine:
                	label four:
                	label five:
                label six:
                """;
        parser renpyParser = new parser();
        assertEquals(renpyParser.parseLine(rpyCode, true, 2).getChainString(0, -1, true, true).trim(), rpyCode.trim());
    }

    @Test
    public void test3() {
        String rpyCode = """
                label one:
                	label two:
                		label three:
                			label four:
                				label five:
                			label six:
                		label seven:
                	label eight:
                label nine""";
        parser renpyParser = new parser();
        assertEquals(renpyParser.parseLine(rpyCode, true, 2).getChainString(0, -1, true, true).trim(), rpyCode);
    }

    @Test
    public void test4() {
        String rpyCode = """
                label one:
                	label car:
                	label two:
                """;
        parser renpyParser = new parser();
        assertEquals(renpyParser.parseLine(rpyCode, true, 2).getChainString(0, -1, true, true).trim(), rpyCode.trim());
    }
}
