package org.bullithulli.rpyparser;

import org.bullithulli.rpyparser.exception.renpyUnkownSymbolException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestParserTest {
    @Test
    public void parseTest1() throws renpyUnkownSymbolException {
        String rpyCode = "label one:\n" +
                "\tlabel two:\n" +
                "\t\tlabel three:\n" +
                "\tlabel four:\n" +
                "\tlabel five:\n" +
                "label six:\n" +
                "\tlabel seven:";
        parser renpyParser = new parser();
        assertEquals(renpyParser.parseLine(rpyCode, true, 2).getChainString(0, -1,true, true).trim(), rpyCode);
    }

    @Test
    public void parseTest2() throws renpyUnkownSymbolException {
        String rpyCode = "label car:\n" +
                "\t\t\tlabel bike:\n" +
                "\tlabel z:";
        String sol = """
                label car:
                \tlabel bike:
                \tlabel z:""";
        parser renpyParser = new parser();
        assertEquals(sol, renpyParser.parseLine(rpyCode, true, 2).getChainString(0, -1,true, true).trim());
    }

    @Test(expected = renpyUnkownSymbolException.class)
    public void parseTest3() throws renpyUnkownSymbolException {
        String rpyCode = "label car:\n" +
                "\t\t\tlabel bike:\n" +
                "\txcxzcx z:";
        parser renpyParser = new parser();
        renpyParser.parseLine(rpyCode, true, 2);
    }

    @Test
    public void parseTest4() throws renpyUnkownSymbolException {
        String rpyCode = "label one:\n" +
                "\tlabel two:\n" +
                "\t\tlabel three:\n" +
                "\tlabel four:\n" +
                "\tlabel five:\n" +
                "label six:\n" +
                "\tlabel seven:";
        parser renpyParser = new parser();
        assertEquals(renpyParser.parseLine(rpyCode, true, 2).getHIERARCHY_CHILD_SYMBOL().size(), 2);
    }
}
