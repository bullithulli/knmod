package org.bullithulli.rpyparser;

import org.bullithulli.rpyparser.exception.renpyUnkownSymbolException;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class TestrootSymbolTest {
    @Test
    public void test1() {
        rootSymbol rootSymbol = new rootSymbol(RENPY_SYMBOL_TYPE.ROOT_FILE, "\n");
        assertEquals(rootSymbol.HIERARCHY_LEVEL, -1);
        assertNull(rootSymbol.getCHAIN_PARENT_SYMBOL());
        assertNull(rootSymbol.getHIERARCHY_PARENT_SYMBOL());
        assertEquals(rootSymbol.toString(), "\n");
    }

    @Test
    public void rootParseTest2() throws renpyUnkownSymbolException {
        String rpyCode = "label one:\n" +
                "\tlabel two:\n" +
                "\t\tlabel three:\n" +
                "\tlabel four:\n" +
                "\tlabel five:\n" +
                "label six:\n" +
                "\tlabel seven:";

        parser renpyParser = new parser();
        rootSymbol root = (rootSymbol) renpyParser.parseLine(rpyCode, true, 2);
        ArrayList<renpyLabel> labels = root.getInnerLabels();
        assertEquals(labels.size(), 2);
        assertEquals("one", labels.get(0).getLabelName());
        assertEquals("six", root.getInnerLabelByName("six").getLabelName());
        assertNull(root.getInnerLabelByName("ten"));

        renpyLabel one = root.getInnerLabelByName("one");
        labels = one.getInnerLabels();
        assertEquals(labels.size(), 3);
        assertEquals("two", labels.get(0).getLabelName());
        assertEquals("four", labels.get(1).getLabelName());
        assertEquals("five", labels.get(2).getLabelName());
        assertNull(one.getInnerLabelByName("ten"));

        String temp =
                "label two:\n" +
                        "\tlabel three:\n" +
                        "label four:\n" +
                        "label five:\n";
        assertEquals(temp, one.getChainString(one.getHIERARCHY_LEVEL() + 1, one.getHIERARCHY_LEVEL(), false, false));
        temp = "label one:\n" +
                "\tlabel two:\n" +
                "\t\tlabel three:\n" +
                "\tlabel four:\n" +
                "\tlabel five:\n" +
                "label six:\n" +
                "\tlabel seven:\n";
        assertEquals(temp, one.getChainString(one.getHIERARCHY_LEVEL(), one.getHIERARCHY_LEVEL(), false, true));


        renpyLabel two = one.getInnerLabelByName("two");
        temp = "label three:\n";
        assertEquals(temp, two.getChainString(two.getHIERARCHY_LEVEL() + 1, two.getHIERARCHY_LEVEL(), false, false));
        temp = "label two:\n" +
                "\tlabel three:\n" +
                "label four:\n" +
                "label five:\n";
        assertEquals(temp, two.getChainString(two.getHIERARCHY_LEVEL(), two.getHIERARCHY_LEVEL(), false, true));
    }
}
