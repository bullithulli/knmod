package org.bullithulli.utils;

import org.bullithulli.rpyparser.parser;
import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.bullithulli.rpyparser.symImpl.renpySymbol;
import org.bullithulli.rpyparser.symImpl.rootSymbol;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        parserUtils.deleteInnerLabel(label_to_delete, renpyParser);
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
        parserUtils.deleteInnerLabel(label_to_delete, renpyParser);
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
        parserUtils.deleteInnerLabel(label_to_delete, renpyParser);
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
        parserUtils.deleteInnerLabel(label_to_delete, renpyParser);
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
        parserUtils.deleteInnerLabel(label_to_delete, renpyParser);
        assertEquals("""
                label seven:
                jump nine
                label eight:
                	label nine:
                """.trim(), rootSymbol.getChainString(0, -1, true, true).trim());
        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("one");
        assertNull(label_to_delete);


        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("seven");
        parserUtils.deleteInnerLabel(label_to_delete, renpyParser);
        assertEquals("""
                label eight:
                	label nine:
                """.trim(), rootSymbol.getChainString(0, -1, true, true).trim());
        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("seven");
        assertNull(label_to_delete);

        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("nine");
        parserUtils.deleteInnerLabel(label_to_delete, renpyParser);
        assertEquals("""
                label eight:
                """.trim(), rootSymbol.getChainString(0, -1, true, true).trim());
        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("nine");
        assertNull(label_to_delete);

        label_to_delete = rootSymbol.getInnerLabelByNameSearchRecursivly("eight");
        parserUtils.deleteInnerLabel(label_to_delete, renpyParser);
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
        parserUtils.addLabelAfter(xxxx, oldRenpyParser, D);
        renpyLabel DD = oldRootSymbol.getInnerLabelByNameSearchRecursivly("D");
        assertEquals(2, DD.getHIERARCHY_LEVEL());
    }

    @Test
    public void test3() throws IOException {
        parser sourceParser = new parser();
        rootSymbol sourceRoot = (rootSymbol) sourceParser.parseLine(rpyCode, true, 2);
        parser patchParser = new parser();
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
        rootSymbol patchRoot = (rootSymbol) patchParser.parseLine(newRpyCode, true, 2);
        renpyLabel D = patchRoot.getInnerLabelByNameSearchRecursivly("D");
        renpyLabel xxxx = sourceRoot.getInnerLabelByNameSearchRecursivly("xxxx");
        parserUtils.deleteInnerLabel(D, patchParser);
        parserUtils.addLabelAfter(xxxx, sourceParser, D);
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
                """.trim(), sourceRoot.getChainString(0, -1, true, true).trim());

        assertEquals("""
                label A:
                	label B:
                		Anwar "hello World"
                		return
                	label C:
                	return
                """.trim(), patchRoot.getChainString(0, -1, true, true).trim());
        renpyLabel DD = sourceRoot.getInnerLabelByNameSearchRecursivly("D");
        renpySymbol variable = DD.getCHAIN_CHILD_SYMBOL(); //x=6
        renpyLabel B = patchRoot.getInnerLabelByNameSearchRecursivly("B");
        parserUtils.deleteInnerLabel(B, patchParser);
        parserUtils.addLabelAfter(variable, sourceParser, B);
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
                """.trim(), sourceRoot.getChainString(0, -1, true, true).trim());
        assertEquals("""
                label A:
                	label C:
                	return
                """.trim(), patchRoot.getChainString(0, -1, true, true).trim());

        renpyLabel eight = sourceRoot.getInnerLabelByNameSearchRecursivly("eight");
        renpyLabel A = patchRoot.getInnerLabelByNameSearchRecursivly("A");
        parserUtils.deleteInnerLabel(A, patchParser);
        parserUtils.addLabelAfter(eight, sourceParser, A);

        String xx = """
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
                """;

        assertEquals(xx.trim(), sourceRoot.getChainString(0, -1, true, true).trim());
        assertEquals("".trim(), patchRoot.getChainString(0, -1, true, true).trim());

        parserUtils.writeChainString("/tmp/out", sourceRoot, 0, -1, true, true);
        String out = new String(Files.readAllBytes(Paths.get("/tmp/out")));
        assertEquals(out.trim(), sourceRoot.getChainString(0, -1, true, true).trim());
    }
}
