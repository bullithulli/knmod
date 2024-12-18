package org.bullithulli.utils;

import org.bullithulli.rpyparser.RENPY_SYMBOL_POSITION;
import org.bullithulli.rpyparser.parser;
import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.bullithulli.rpyparser.symImpl.renpySymbol;
import org.bullithulli.rpyparser.symImpl.rootSymbol;
import org.junit.Test;

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
	public void test4() throws Exception {
		String vanillaRpyCode = """
				label A:
					label patch1:
						anwar "Hello, mom"
					label C:
						label D:
							label E:
						label F:
							anwar "Bye, landlord"
				""";
		String patchRpyCode = """
				label patch:
					anwar "hey, mom"
				""";
		parser vanillaParser = new parser();
		rootSymbol vRoot = (rootSymbol) vanillaParser.parseLine(vanillaRpyCode, true, 2);
		parser patchParser = new parser();
		rootSymbol pRoot = (rootSymbol) patchParser.parseLine(patchRpyCode, true, 2);
		renpyLabel E = vRoot.getInnerLabelByNameSearchRecursivly("E");
		renpyLabel patch = pRoot.getInnerLabelByNameSearchRecursivly("patch");
		parserUtils.addLabelAfter(E, vanillaParser, patch, RENPY_SYMBOL_POSITION.CHAIN_RIGHT, 0);
		assertEquals("""
				label A:
					label patch1:
						anwar "Hello, mom"
					label C:
						label D:
							label E:
								label patch:
									anwar "hey, mom"
						label F:
							anwar "Bye, landlord"
				""".trim(), vRoot.getChainString(0, -1, true, true).trim());
		assertEquals(patch.getHIERARCHY_PARENT_SYMBOL().getRENPY_TRIMMED_LINE(), "label E:");
	}

	@Test
	public void test6() throws Exception {
		String vanillaRpyCode = """
				label A:
					label patch1:
						anwar "Hello, mom"
					label C:
						label D:
							label E:
						label F:
							anwar "Bye, landlord"
				""";
		String patchRpyCode = """
				label patch:
					anwar "hey, mom"
				""";
		parser vanillaParser = new parser();
		rootSymbol vRoot = (rootSymbol) vanillaParser.parseLine(vanillaRpyCode, true, 2);
		parser patchParser = new parser();
		rootSymbol pRoot = (rootSymbol) patchParser.parseLine(patchRpyCode, true, 2);
		renpyLabel E = vRoot.getInnerLabelByNameSearchRecursivly("E");
		renpyLabel patch = pRoot.getInnerLabelByNameSearchRecursivly("patch");
		parserUtils.addLabelAfter(E, vanillaParser, patch, RENPY_SYMBOL_POSITION.CHAIN_DOWN, 0);
		assertEquals("""
				label A:
					label patch1:
						anwar "Hello, mom"
					label C:
						label D:
							label E:
							label patch:
								anwar "hey, mom"
						label F:
							anwar "Bye, landlord"
				""".trim(), vRoot.getChainString(0, -1, true, true).trim());
		assertEquals(patch.getHIERARCHY_PARENT_SYMBOL().getRENPY_TRIMMED_LINE(), "label D:");
	}

	@Test
	public void test7() throws Exception {
		String vanillaRpyCode = """
				label A:
					label patch1:
						anwar "Hello, mom"
					label C:
						label D:
							label E:
						label F:
							anwar "Bye, landlord"
				""";
		String patchRpyCode = """
				label patch:
					anwar "hey, mom"
				""";
		parser vanillaParser = new parser();
		rootSymbol vRoot = (rootSymbol) vanillaParser.parseLine(vanillaRpyCode, true, 2);
		parser patchParser = new parser();
		rootSymbol pRoot = (rootSymbol) patchParser.parseLine(patchRpyCode, true, 2);
		renpyLabel E = vRoot.getInnerLabelByNameSearchRecursivly("E");
		renpyLabel patch = pRoot.getInnerLabelByNameSearchRecursivly("patch");
		parserUtils.addLabelAfter(E, vanillaParser, patch, RENPY_SYMBOL_POSITION.CHAIN_LEFT, 0);
		assertEquals("""
				label A:
					label patch1:
						anwar "Hello, mom"
					label C:
						label D:
							label E:
							label patch:
								anwar "hey, mom"
						label F:
							anwar "Bye, landlord"
				""".trim(), vRoot.getChainString(0, -1, true, true).trim());
		assertEquals(patch.getHIERARCHY_PARENT_SYMBOL().getRENPY_TRIMMED_LINE(), "label D:");
	}

	@Test
	public void test8() throws Exception {
		String vanillaRpyCode = """
				label A:
					label patch1:
						anwar "Hello, mom"
					label C:
						label D:
							label E:
						label F:
							anwar "Bye, landlord"
				""";
		String patchRpyCode = """
				label patch:
					anwar "hey, mom"
				""";
		parser vanillaParser = new parser();
		rootSymbol vRoot = (rootSymbol) vanillaParser.parseLine(vanillaRpyCode, true, 2);
		parser patchParser = new parser();
		rootSymbol pRoot = (rootSymbol) patchParser.parseLine(patchRpyCode, true, 2);
		renpyLabel E = vRoot.getInnerLabelByNameSearchRecursivly("E");
		renpyLabel patch = pRoot.getInnerLabelByNameSearchRecursivly("patch");
		parserUtils.addLabelAfter(E, vanillaParser, patch, RENPY_SYMBOL_POSITION.CHAIN_LEFT, 1);
		assertEquals("""
				label A:
					label patch1:
						anwar "Hello, mom"
					label C:
						label D:
							label E:
						label patch:
							anwar "hey, mom"
						label F:
							anwar "Bye, landlord"
				""".trim(), vRoot.getChainString(0, -1, true, true).trim());
		assertEquals(patch.getHIERARCHY_PARENT_SYMBOL().getRENPY_TRIMMED_LINE(), "label C:");
	}

	@Test
	public void test9() throws Exception {
		String vanillaRpyCode = """
				label A:
					label B:
						anwar "Hello, landlord"
					label C:
						label D:
							label E:
				""";
		String patchRpyCode = """
				label patch:
					anwar "hey, mom"
				""";
		parser vanillaParser = new parser();
		rootSymbol vRoot = (rootSymbol) vanillaParser.parseLine(vanillaRpyCode, true, 2);
		parser patchParser = new parser();
		rootSymbol pRoot = (rootSymbol) patchParser.parseLine(patchRpyCode, true, 2);
		renpyLabel E = vRoot.getInnerLabelByNameSearchRecursivly("E");
		renpyLabel patch = pRoot.getInnerLabelByNameSearchRecursivly("patch");
		parserUtils.addLabelAfter(E, vanillaParser, patch, RENPY_SYMBOL_POSITION.CHAIN_LEFT, 2);
		assertEquals("""
				label A:
					label B:
						anwar "Hello, landlord"
					label C:
						label D:
							label E:
					label patch:
						anwar "hey, mom"
				""".trim(), vRoot.getChainString(0, -1, true, true).trim());
		assertEquals(patch.getHIERARCHY_PARENT_SYMBOL().getRENPY_TRIMMED_LINE(), "label A:");
	}

	@Test
	public void test10() throws Exception {
		String vanillaRpyCode = """
				label A:
					label B:
						anwar "Hello, landlord"
					label C:
						label D:
							label E:
				""";
		String patchRpyCode = """
				label patch:
					anwar "hey, mom"
				""";
		parser vanillaParser = new parser();
		rootSymbol vRoot = (rootSymbol) vanillaParser.parseLine(vanillaRpyCode, true, 2);
		parser patchParser = new parser();
		rootSymbol pRoot = (rootSymbol) patchParser.parseLine(patchRpyCode, true, 2);
		renpyLabel E = vRoot.getInnerLabelByNameSearchRecursivly("E");
		renpyLabel patch = pRoot.getInnerLabelByNameSearchRecursivly("patch");
		parserUtils.addLabelAfter(E, vanillaParser, patch, RENPY_SYMBOL_POSITION.CHAIN_LEFT, 3);
		assertEquals("""
				label A:
					label B:
						anwar "Hello, landlord"
					label C:
						label D:
							label E:
				label patch:
					anwar "hey, mom"
				""".trim(), vRoot.getChainString(0, -1, true, true).trim());
		assertEquals(patch.getHIERARCHY_PARENT_SYMBOL().getRENPY_TRIMMED_LINE(), "");
	}

	@Test(expected = Exception.class)
	public void test11() throws Exception {
		String vanillaRpyCode = """
				label A:
					label B:
						anwar "Hello, landlord"
					label C:
						label D:
							label E:
				""";
		String patchRpyCode = """
				label patch:
					anwar "hey, mom"
				""";
		parser vanillaParser = new parser();
		rootSymbol vRoot = (rootSymbol) vanillaParser.parseLine(vanillaRpyCode, true, 2);
		parser patchParser = new parser();
		rootSymbol pRoot = (rootSymbol) patchParser.parseLine(patchRpyCode, true, 2);
		renpyLabel E = vRoot.getInnerLabelByNameSearchRecursivly("E");
		renpyLabel patch = pRoot.getInnerLabelByNameSearchRecursivly("patch");
		parserUtils.addLabelAfter(E, vanillaParser, patch, RENPY_SYMBOL_POSITION.CHAIN_LEFT, 4);
	}

	@Test
	public void test5() throws Exception {
		String vanillaRpyCode = """
				label A:
					label patch1:
						anwar "Hello, mom"
					label C:
						label D:
							label E:
								"Loading"
						label F:
							anwar "Bye, landlord"
				""";
		String patchRpyCode = """
				label patch:
					anwar "hey, mom"
				""";
		parser vanillaParser = new parser();
		rootSymbol vRoot = (rootSymbol) vanillaParser.parseLine(vanillaRpyCode, true, 2);
		parser patchParser = new parser();
		rootSymbol pRoot = (rootSymbol) patchParser.parseLine(patchRpyCode, true, 2);
		renpyLabel E = vRoot.getInnerLabelByNameSearchRecursivly("E");
		renpySymbol loading = E.getCHAIN_CHILD_SYMBOL();
		renpyLabel patch = pRoot.getInnerLabelByNameSearchRecursivly("patch");
		parserUtils.addLabelAfter(loading, vanillaParser, patch, RENPY_SYMBOL_POSITION.CHAIN_RIGHT, 0);
		assertEquals("""
				label A:
					label patch1:
						anwar "Hello, mom"
					label C:
						label D:
							label E:
								"Loading"
								label patch:
									anwar "hey, mom"
						label F:
							anwar "Bye, landlord"
				""".trim(), vRoot.getChainString(0, -1, true, true).trim());
		assertEquals(patch.getHIERARCHY_PARENT_SYMBOL().getRENPY_TRIMMED_LINE(), "label E:");
	}

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
	public void test2() throws Exception {
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
		parserUtils.addLabelAfter(xxxx, oldRenpyParser, D, RENPY_SYMBOL_POSITION.CHAIN_RIGHT, 0);
		renpyLabel DD = oldRootSymbol.getInnerLabelByNameSearchRecursivly("D");
		assertEquals(2, DD.getHIERARCHY_LEVEL());
	}

	@Test
	public void test3() throws Exception {
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
		parserUtils.addLabelAfter(xxxx, sourceParser, D, RENPY_SYMBOL_POSITION.CHAIN_RIGHT, 0);
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
		parserUtils.addLabelAfter(variable, sourceParser, B, RENPY_SYMBOL_POSITION.CHAIN_RIGHT, 0);
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
		parserUtils.addLabelAfter(eight, sourceParser, A, RENPY_SYMBOL_POSITION.CHAIN_RIGHT, 0);

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
		assertEquals(out.trim(), sourceRoot.getChainString(0, -1, true, true).trim().replaceAll("\t", "    "));
	}

	@Test
	public void test12() throws Exception {
		String rpyCode = """
				label A:
					label B:
						label C:
							label D:
					label E:
						label F:
					dasdas
					label G:
				""";
		String patchRpyCode = """
				label patch:
					anwar "hey, mom"
				""";
		parser vanillaParser = new parser();
		parser patchParser = new parser();
		rootSymbol vRoot = (rootSymbol) vanillaParser.parseLine(rpyCode, true, 2);
		rootSymbol pRoot = (rootSymbol) patchParser.parseLine(patchRpyCode, true, 2);
		renpyLabel G = vRoot.getInnerLabelByNameSearchRecursivly("G");
		renpyLabel patch = pRoot.getInnerLabelByNameSearchRecursivly("patch");
		parserUtils.replaceLabel(G, vanillaParser, patch, patchParser);
		assertEquals("""
				label A:
					label B:
						label C:
							label D:
					label E:
						label F:
					dasdas
					label patch:
						anwar "hey, mom"
				""".trim(), vRoot.getChainString(0, -1, true, true).trim());
		assertEquals("", pRoot.getChainString(0, -1, true, true).trim());
	}

	@Test
	public void test13() throws Exception {
		String rpyCode = """
				label A:
					label B:
						label C:
							label D:
					label E:
						label F:
					dasdas
					label G:
				""";
		String patchRpyCode = """
				label patch:
					anwar "hey, mom"
				""";
		parser vanillaParser = new parser();
		parser patchParser = new parser();
		rootSymbol vRoot = (rootSymbol) vanillaParser.parseLine(rpyCode, true, 2);
		rootSymbol pRoot = (rootSymbol) patchParser.parseLine(patchRpyCode, true, 2);
		renpyLabel D = vRoot.getInnerLabelByNameSearchRecursivly("D");
		renpyLabel patch = pRoot.getInnerLabelByNameSearchRecursivly("patch");
		parserUtils.replaceLabel(D, vanillaParser, patch, patchParser);
		assertEquals("""
				label A:
					label B:
						label C:
							label patch:
								anwar "hey, mom"
					label E:
						label F:
					dasdas
					label G:
				""".trim(), vRoot.getChainString(0, -1, true, true).trim());
		assertEquals("", pRoot.getChainString(0, -1, true, true).trim());
	}

	@Test
	public void test14() throws Exception {
		String rpyCode = """
				label A:
					label B:
						label C:
							label D:
					label E:
						label F:
					dasdas
					label G:
				""";
		String patchRpyCode = """
				label patch:
					anwar "hey, mom"
				""";
		parser vanillaParser = new parser();
		parser patchParser = new parser();
		rootSymbol vRoot = (rootSymbol) vanillaParser.parseLine(rpyCode, true, 2);
		rootSymbol pRoot = (rootSymbol) patchParser.parseLine(patchRpyCode, true, 2);
		renpyLabel E = vRoot.getInnerLabelByNameSearchRecursivly("E");
		renpyLabel patch = pRoot.getInnerLabelByNameSearchRecursivly("patch");
		parserUtils.replaceLabel(E, vanillaParser, patch, patchParser);
		assertEquals("""
				label A:
					label B:
						label C:
							label D:
					label patch:
						anwar "hey, mom"
					label G:
				""".trim(), vRoot.getChainString(0, -1, true, true).trim());
		assertEquals("", pRoot.getChainString(0, -1, true, true).trim());
	}

	@Test
	public void test15() throws Exception {
		String rpyCode = """
				label A:
					label B:
						label C:
							label D:
				label E:
					label F:
				dasdas
				label G:
				""";
		String patchRpyCode = """
				label patch:
					anwar "hey, mom"
				""";
		parser vanillaParser = new parser();
		parser patchParser = new parser();
		rootSymbol vRoot = (rootSymbol) vanillaParser.parseLine(rpyCode, true, 2);
		rootSymbol pRoot = (rootSymbol) patchParser.parseLine(patchRpyCode, true, 2);
		renpyLabel E = vRoot.getInnerLabelByNameSearchRecursivly("E");
		renpyLabel patch = pRoot.getInnerLabelByNameSearchRecursivly("patch");
		parserUtils.replaceLabel(E, vanillaParser, patch, patchParser);
		assertEquals("""
				label A:
					label B:
						label C:
							label D:
				label patch:
					anwar "hey, mom"
				label G:
				""".trim(), vRoot.getChainString(0, -1, true, true).trim());
		assertEquals("", pRoot.getChainString(0, -1, true, true).trim());
	}

	@Test
	public void test16() throws Exception {
		String vanillaCode = """
				label A:
					end of A
				label B:
					end of B
				label C:
					end of C
				""";
		String patchRpyCode = """
				label patch:
					anwar "hey, mom"
				""";
		parser vanillaParser = new parser();
		parser patchParser = new parser();
		rootSymbol vRoot = (rootSymbol) vanillaParser.parseLine(vanillaCode, true, 2);
		rootSymbol pRoot = (rootSymbol) patchParser.parseLine(patchRpyCode, true, 2);
		renpyLabel B = vRoot.getInnerLabelByNameSearchRecursivly("B");
		renpyLabel patch = pRoot.getInnerLabelByNameSearchRecursivly("patch");
		parserUtils.replaceLabel(B, vanillaParser, patch, patchParser);
		assertEquals("""
				label A:
					end of A
				label patch:
					anwar "hey, mom"
				label C:
					end of C
				""".trim(), vRoot.getChainString(0, -1, true, true).trim());
	}
}
