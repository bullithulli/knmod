package org.bullithulli.utils;

import org.bullithulli.rpyparser.parser;
import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.bullithulli.rpyparser.symImpl.rootSymbol;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestParserUtilsTest {
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

    @Test
    public void test1() {
        parser renpyParser = new parser();
        rootSymbol rootSymbol = (rootSymbol) renpyParser.parseLine(rpyCode, true, 2);
        renpyLabel label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("three");
        parserUtils.deleteInnerLabel(label_to_delete);
        assertEquals("""
                label one:
                	label two:
                		return
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
                """.trim(), rootSymbol.getChainString(0, -1, true, true).trim());
        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("three");
        assertNull(label_to_delete);
        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("ten");
        parserUtils.deleteInnerLabel(label_to_delete);
        assertEquals("""
                label one:
                	label two:
                		return
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
                """.trim(), rootSymbol.getChainString(0, -1, true, true).trim());
        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("ten");
        assertNull(label_to_delete);
        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("six");
        parserUtils.deleteInnerLabel(label_to_delete);
        assertEquals("""
                label one:
                	label two:
                		return
                	label four:
                	return
                	label five:
                label seven:
                jump nine
                label eight:
                	label nine:
                """.trim(), rootSymbol.getChainString(0, -1, true, true).trim());
        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("six");
        assertNull(label_to_delete);

        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("five");
        parserUtils.deleteInnerLabel(label_to_delete);
        assertEquals("""
                label one:
                	label two:
                		return
                	label four:
                	return
                label seven:
                jump nine
                label eight:
                	label nine:
                """.trim(), rootSymbol.getChainString(0, -1, true, true).trim());
        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("five");
        assertNull(label_to_delete);

        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("one");
        parserUtils.deleteInnerLabel(label_to_delete);
        assertEquals("""
                label seven:
                jump nine
                label eight:
                	label nine:
                """.trim(), rootSymbol.getChainString(0, -1, true, true).trim());
        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("one");
        assertNull(label_to_delete);


        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("seven");
        parserUtils.deleteInnerLabel(label_to_delete);
        assertEquals("""
                label eight:
                	label nine:
                """.trim(), rootSymbol.getChainString(0, -1, true, true).trim());
        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("seven");
        assertNull(label_to_delete);

        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("nine");
        parserUtils.deleteInnerLabel(label_to_delete);
        assertEquals("""
                label eight:
                """.trim(), rootSymbol.getChainString(0, -1, true, true).trim());
        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("nine");
        assertNull(label_to_delete);

        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("eight");
        parserUtils.deleteInnerLabel(label_to_delete);
        assertEquals("""
                """.trim(), rootSymbol.getChainString(0, -1, true, true).trim());
        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("eight");
        assertNull(label_to_delete);
    }
}
