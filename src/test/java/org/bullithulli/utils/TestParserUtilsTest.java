package org.bullithulli.utils;

import org.bullithulli.rpyparser.parser;
import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.bullithulli.rpyparser.symImpl.renpySymbol;
import org.bullithulli.rpyparser.symImpl.rootSymbol;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestParserUtilsTest {
    final String rpyCode = """
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

    @Test
    public void test2() {
        parser oldRenpyParser = new parser();
        String rpyCode1 = """
                label one:
                    label xxxx:
                """;
        rootSymbol oldRootSymbol = (rootSymbol) oldRenpyParser.parseLine(rpyCode1, true, 2);
        parser newRenpyParser = new parser();
        String newRpyCode = """
                label A:
                	label B:
                		Anwar "hello World"
                		return
                	label C:
                		label D:
                			x=6
                			return
                	return
                """;
        rootSymbol newRootSymbol = (rootSymbol) newRenpyParser.parseLine(newRpyCode, true, 2);
        renpyLabel D = newRootSymbol.getInnerLabelByNameSearchRecursivly("D");
        renpyLabel xxxx = oldRootSymbol.getInnerLabelByNameSearchRecursivly("xxxx");
        parserUtils.addLabelAfter(xxxx, D);
        renpyLabel DD = oldRootSymbol.getInnerLabelByNameSearchRecursivly("D");
        assertEquals(2, DD.getHIERARCHY_LEVEL());
    }

    @Test
    public void test3() {
        parser oldRenpyParser = new parser();
        rootSymbol oldRootSymbol = (rootSymbol) oldRenpyParser.parseLine(rpyCode, true, 2);
        parser newRenpyParser = new parser();
        String newRpyCode = """
                label A:
                	label B:
                		Anwar "hello World"
                		return
                	label C:
                		label D:
                			x=6
                			return
                	return
                """;
        rootSymbol newRootSymbol = (rootSymbol) newRenpyParser.parseLine(newRpyCode, true, 2);
        renpyLabel D = newRootSymbol.getInnerLabelByNameSearchRecursivly("D");
        renpyLabel xxxx = oldRootSymbol.getInnerLabelByNameSearchRecursivly("xxxx");
        parserUtils.deleteInnerLabel(D);
        parserUtils.addLabelAfter(xxxx, D);
        assertEquals("""
                label one:
                	label two:
                		return
                		label three:
                			label xxxx:
                				label D:
                					x=6
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
                """.trim(), oldRootSymbol.getChainString(0, -1, true, true).trim());
        assertEquals("""
                label A:
                	label B:
                		Anwar "hello World"
                		return
                	label C:
                	return
                """.trim(), newRootSymbol.getChainString(0, -1, true, true).trim());

        renpyLabel DD = oldRootSymbol.getInnerLabelByNameSearchRecursivly("D");
        renpySymbol variable = DD.getCHAIN_CHILD_SYMBOL(); //x=6
        renpyLabel B = newRootSymbol.getInnerLabelByNameSearchRecursivly("B");
        parserUtils.deleteInnerLabel(B);
        parserUtils.addLabelAfter(variable, B);
        assertEquals("""
                label one:
                	label two:
                		return
                		label three:
                			label xxxx:
                				label D:
                					x=6
                					label B:
                						Anwar "hello World"
                						return
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
                """.trim(), oldRootSymbol.getChainString(0, -1, true, true).trim());
        assertEquals("""
                label A:
                	label C:
                	return
                """.trim(), newRootSymbol.getChainString(0, -1, true, true).trim());

        renpyLabel eight = oldRootSymbol.getInnerLabelByNameSearchRecursivly("eight");
        renpyLabel A = newRootSymbol.getInnerLabelByNameSearchRecursivly("A");
        parserUtils.deleteInnerLabel(A);
        parserUtils.addLabelAfter(eight, A);

        assertEquals("""
                label one:
                	label two:
                		return
                		label three:
                			label xxxx:
                				label D:
                					x=6
                					label B:
                						Anwar "hello World"
                						return
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
                	label A:
                		label C:
                		return
                	label nine:
                		label ten:
                			return
                """.trim(), oldRootSymbol.getChainString(0, -1, true, true).trim());
        assertEquals("".trim(), newRootSymbol.getChainString(0, -1, true, true).trim());
    }
}
