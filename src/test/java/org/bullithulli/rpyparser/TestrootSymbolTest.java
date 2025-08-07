package org.bullithulli.rpyparser;

import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyGenericBlockSymbol;
import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.bullithulli.rpyparser.symImpl.rootSymbol;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class TestrootSymbolTest {
    @Test
    public void test1() {
        rootSymbol rootSymbol = new rootSymbol(RENPY_SYMBOL_TYPE.ROOT_LABEL, "\n");
        assertEquals(rootSymbol.getHIERARCHY_LEVEL(), -1);
        assertNull(rootSymbol.getCHAIN_PARENT_SYMBOL());
        assertNull(rootSymbol.getHIERARCHY_PARENT_SYMBOL());
        assertEquals(rootSymbol.toString(), "\n");
    }

    @Test
    public void rootParseTest2() {
        String rpyCode = "label one:\n" +
                "\tlabel two:\n" +
                "\t\tlabel three:\n" +
                "\tlabel four:\n" +
                "\tlabel five:\n" +
                "label six:\n" +
                "\tlabel seven:";
        parser renpyParser = new parser();
        rootSymbol root = (rootSymbol) renpyParser.parseLine(rpyCode, true);
        ArrayList<renpyGenericBlockSymbol> innerBlocks = root.getInnerBlocks();
        assertEquals(innerBlocks.size(), 2);
        assertEquals("one", ((renpyLabel) innerBlocks.get(0)).getLabelName());
        assertEquals("six", root.getInnerLabelByName("six").getLabelName());
        assertNull(root.getInnerLabelByName("ten"));

        renpyLabel one = root.getInnerLabelByName("one");
        innerBlocks = one.getInnerBlocks();
        assertEquals(innerBlocks.size(), 3);
        assertEquals("two", ((renpyLabel) innerBlocks.get(0)).getLabelName());
        assertEquals("four", ((renpyLabel) innerBlocks.get(1)).getLabelName());
        assertEquals("five", ((renpyLabel) innerBlocks.get(2)).getLabelName());
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
