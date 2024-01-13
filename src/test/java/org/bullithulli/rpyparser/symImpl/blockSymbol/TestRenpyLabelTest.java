package org.bullithulli.rpyparser.symImpl.blockSymbol;

import org.bullithulli.rpyparser.parser;
import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.bullithulli.rpyparser.symImpl.renpySymbol;
import org.bullithulli.rpyparser.symImpl.rootSymbol;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

    // TODO: 1/13/24 work on this 
    @Test
    public void test5() {
        String rpyCode = """
                label one:
                	label two:
                	    return
                		label three:
                		    label xxxx:
                	label four:
                	return
                	label five:
                label six:
                	label seven:
                	jump seven
                label seven:
                jump nine
                label eight:
                    label nine:
                        label ten:
                            return
                """;
        parser renpyParser = new parser();
        rootSymbol root = (rootSymbol) renpyParser.parseLine(rpyCode, true, 2);
        renpyLabel one = root.getInnerLabelByName("one");
        assertEquals("label three:", one.getInnerLabelByNameSearchRecursivly("three").getRENPY_TRIMMED_LINE());
        assertNull(one.getInnerLabelByNameSearchRecursivly("six"));
        assertNull(one.getInnerLabelByNameSearchRecursivly("sixxxx"));
        assertEquals("label xxxx:", one.getInnerLabelByNameSearchRecursivly("xxxx").getRENPY_TRIMMED_LINE());
        assertEquals("label five:", one.getInnerLabelByNameSearchRecursivly("five").getRENPY_TRIMMED_LINE());
        assertEquals("label five:", root.getInnerLabelByNameSearchRecursivly("five").getRENPY_TRIMMED_LINE());
        assertEquals("label ten:", root.getInnerLabelByNameSearchRecursivly("ten").getRENPY_TRIMMED_LINE());
        assertNull(root.getInnerLabelByNameSearchRecursivly("texxxn"));
    }
}
