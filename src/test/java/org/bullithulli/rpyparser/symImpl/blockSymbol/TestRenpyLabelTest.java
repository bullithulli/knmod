package org.bullithulli.rpyparser.symImpl.blockSymbol;

import org.bullithulli.rpyparser.RENPY_SYMBOL_TYPE;
import org.bullithulli.rpyparser.parser;
import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.bullithulli.rpyparser.symImpl.nonBlockSymbol.renpyCall;
import org.bullithulli.rpyparser.symImpl.renpySymbol;
import org.bullithulli.rpyparser.symImpl.rootSymbol;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
		ArrayList<renpySymbol> symbols = root.getChainSymbols(root.getHIERARCHY_LEVEL(), true, true, false, false, root);

		assertEquals(symbols.size(), 8);
		renpySymbol one = symbols.get(1);
		assertEquals(4, one.getChainSymbols(one.getHIERARCHY_LEVEL(), false, false, false, false, root).size());

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

	@Test
	public void test6() {
		renpyLabel label = new renpyLabel(RENPY_SYMBOL_TYPE.RENPY_LABEL, "label x:");
		assertTrue(label.isBlockSymbol());
		renpyCall call = new renpyCall(RENPY_SYMBOL_TYPE.RENPY_CALL, "call someFunction");
		assertFalse(call.isBlockSymbol());
	}

	@Test
	public void test7() {
		String rpyCode = """
				label one:
					label two:
						label three:
						jump five
					label four:
						label x1:
							return
						return
					label five:
					return
				label six:
					label seven:""";
		parser renpyParser = new parser();
		rootSymbol root = (rootSymbol) renpyParser.parseLine(rpyCode, true, 2);
		ArrayList<renpySymbol> symbols = root.getChainSymbols(root.getHIERARCHY_LEVEL(), true, true, false, false, root);
		assertEquals(symbols.size(), 13);

		renpySymbol four = root.getInnerLabelByNameSearchRecursivly("four");
		assertEquals(3, four.getChainSymbols(four.getHIERARCHY_LEVEL(), false, false, true, false, four).size());
		assertEquals(3 + 1, four.getChainSymbols(four.getHIERARCHY_LEVEL(), false, true, true, false, four).size());

		renpySymbol x1 = root.getInnerLabelByNameSearchRecursivly("x1");
		assertEquals(1, x1.getChainSymbols(x1.getHIERARCHY_LEVEL(), false, false, true, false, x1).size());
		assertEquals(1 + 1, x1.getChainSymbols(x1.getHIERARCHY_LEVEL(), false, true, true, false, x1).size());

		renpySymbol five = root.getInnerLabelByNameSearchRecursivly("five");
		assertEquals(0, five.getChainSymbols(five.getHIERARCHY_LEVEL(), false, false, true, false, five).size());
		assertEquals(2, five.getChainSymbols(five.getHIERARCHY_LEVEL(), false, true, true, false, five).size());

		renpySymbol one = root.getInnerLabelByNameSearchRecursivly("one");
		assertEquals(9, one.getChainSymbols(one.getHIERARCHY_LEVEL(), false, false, true, false, one).size());
		assertEquals(9 + 1, one.getChainSymbols(one.getHIERARCHY_LEVEL(), false, true, true, false, one).size());
	}

	@Test
	public void test8() {
		String rpycode = """
				label x:
					menu:
						"It’s pretty good":
							show sc introhotel h05
							mc"Well, it looks pretty good I must say."
							show sc introhotel h07
				""";
		parser renpyParser = new parser();
		rootSymbol root = (rootSymbol) renpyParser.parseLine(rpycode, true, 2);
		assertEquals(rpycode.trim(), root.getChainString(0, -1, true, true).trim());
	}
}
