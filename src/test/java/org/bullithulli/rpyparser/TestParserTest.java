package org.bullithulli.rpyparser;

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
        assertEquals(renpyParser.parseLine(rpyCode, true, 2).getChainString().trim(), rpyCode);
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
        assertEquals(sol, renpyParser.parseLine(rpyCode, true, 2).getChainString().trim());
    }
}
