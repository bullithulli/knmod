package org.bullithulli.rpyparser.symImpl;

import org.bullithulli.rpyparser.parser;
import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestRenpySymbolTest {
    @Test
    public void test0() {
        String rpyCode = """
                label one:
                	label two:
                		label three:
                		    label xxxx:
                	label four:
                	label five:
                label six:
                	label seven:
                """;
        parser renpyParser = new parser();
        rootSymbol rootSymbol = (rootSymbol) renpyParser.parseLine(rpyCode, true, 2);
        renpyLabel one = rootSymbol.getInnerLabelByName("one");
        renpyLabel temp = (renpyLabel) one.getLastChainSymbol(one.getHIERARCHY_LEVEL(), false, false, false, false, one);
        assertEquals("five", temp.getLabelName());
        temp = (renpyLabel) one.getLastChainSymbol(one.getHIERARCHY_LEVEL(), false, true, false, false, one);
        assertEquals("seven", temp.getLabelName());

        renpyLabel two = one.getInnerLabelByName("two");
        temp = (renpyLabel) two.getLastChainSymbol(two.getHIERARCHY_LEVEL(), false, false, false, false, two);
        assertEquals("xxxx", temp.getLabelName());

        temp = (renpyLabel) two.getLastChainSymbol(two.getHIERARCHY_LEVEL(), false, true, false, false, two);
        assertEquals("five", temp.getLabelName());

        renpyLabel three = two.getInnerLabelByName("three");
        temp = (renpyLabel) three.getLastChainSymbol(three.getHIERARCHY_LEVEL(), false, false, false, false, three);
        assertEquals("xxxx", temp.getLabelName());

        temp = (renpyLabel) three.getLastChainSymbol(three.getHIERARCHY_LEVEL(), false, true, false, false, three);
        assertEquals("xxxx", temp.getLabelName());

        temp = (renpyLabel) three.getLastChainSymbol(three.getHIERARCHY_LEVEL(), true, true, false, false, three);
        assertEquals("seven", temp.getLabelName());
    }


    @Test
    public void test2() {
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
        rootSymbol rootSymbol = (rootSymbol) renpyParser.parseLine(rpyCode, true, 2);
        renpyLabel one = rootSymbol.getInnerLabelByName("one");
        renpySymbol temp = one.getLastChainSymbol(one.getHIERARCHY_LEVEL(), false, false, true, false, one);
        assertEquals("return", temp.getRENPY_TRIMMED_LINE());
        assertEquals("label four:", temp.getCHAIN_PARENT_SYMBOL().getRENPY_TRIMMED_LINE());

        renpyLabel two = one.getInnerLabelByName("two");
        temp = two.getLastChainSymbol(two.getHIERARCHY_LEVEL(), false, false, true, false, two);
        assertEquals("return", temp.getRENPY_TRIMMED_LINE());
        assertEquals("label two:", temp.getCHAIN_PARENT_SYMBOL().getRENPY_TRIMMED_LINE());

        renpyLabel three = two.getInnerLabelByName("three");
        temp = three.getLastChainSymbol(three.getHIERARCHY_LEVEL(), false, false, true, false, three);
        assertEquals("label xxxx:", temp.getRENPY_TRIMMED_LINE());
        temp = three.getLastChainSymbol(three.getHIERARCHY_LEVEL(), false, true, true, false, three);
        assertEquals("label xxxx:", temp.getRENPY_TRIMMED_LINE());
        assertEquals("label three:", temp.getCHAIN_PARENT_SYMBOL().getRENPY_TRIMMED_LINE());

        renpyLabel five = one.getInnerLabelByName("five");
        temp = five.getLastChainSymbol(five.getHIERARCHY_LEVEL(), false, false, true, false, five);
        assertEquals("label five:", temp.getRENPY_TRIMMED_LINE());
        temp = five.getLastChainSymbol(five.getHIERARCHY_LEVEL(), false, true, true, false, five);
        assertEquals("label five:", temp.getRENPY_TRIMMED_LINE());

        renpyLabel six = rootSymbol.getInnerLabelByName("six");
        temp = six.getLastChainSymbol(six.getHIERARCHY_LEVEL(), false, false, true, false, six);
        assertEquals("jump seven", temp.getRENPY_TRIMMED_LINE());
        temp = six.getLastChainSymbol(six.getHIERARCHY_LEVEL(), false, true, true, false, six);
        assertEquals("jump seven", temp.getRENPY_TRIMMED_LINE());

        renpyLabel seven = rootSymbol.getInnerLabelByName("seven");
        temp = seven.getLastChainSymbol(seven.getHIERARCHY_LEVEL(), false, false, true, false, seven);
        assertEquals("label seven:", temp.getRENPY_TRIMMED_LINE());
        temp = seven.getLastChainSymbol(seven.getHIERARCHY_LEVEL(), false, true, true, false, seven);
        assertEquals("jump nine", temp.getRENPY_TRIMMED_LINE());

        renpyLabel eight = rootSymbol.getInnerLabelByName("eight");
        temp = eight.getLastChainSymbol(eight.getHIERARCHY_LEVEL(), false, false, true, false, eight);
        assertEquals("return", temp.getRENPY_TRIMMED_LINE());
        temp = eight.getLastChainSymbol(eight.getHIERARCHY_LEVEL(), false, true, true, false, eight);
        assertEquals("return", temp.getRENPY_TRIMMED_LINE());

        renpyLabel ten = (renpyLabel) temp.getCHAIN_PARENT_SYMBOL();
        temp = ten.getLastChainSymbol(ten.getHIERARCHY_LEVEL(), false, false, true, false, ten);
        assertEquals("return", temp.getRENPY_TRIMMED_LINE());
        temp = ten.getLastChainSymbol(ten.getHIERARCHY_LEVEL(), false, true, true, false, ten);
        assertEquals("return", temp.getRENPY_TRIMMED_LINE());
    }

    @Test
    public void test1() {
        // TODO: 1/13/24 to be implemnted
        String x = """
                label z:
                    label car:
                        awnawr "hello" <--- last 
                """;
        String x1 = """
                label z:
                    label car: <--- last
                awnawr "hello" <--- last 
                """;
        String x2 = """
                label z:
                    label car: <--- last
                awnawr "hello"
                awnawr "hello" <--- last 
                """;
    }

    @Test
    public void test3() {
        String rpyCode = """
                label one:
                	label two:
                		label three:
                		    label xxxx:
                	label four:
                	return
                	label five:
                label six:
                	label seven:
                """;
        parser renpyParser = new parser();
        rootSymbol rootSymbol = (rootSymbol) renpyParser.parseLine(rpyCode, true, 2);
        renpyLabel two = rootSymbol.getInnerLabelByNameSearchRecursivly("two");
        assertEquals("label xxxx:", two.getLastChainSymbol(two.getHIERARCHY_LEVEL(), false, true, true, true, two).getRENPY_TRIMMED_LINE());
        assertEquals("label xxxx:", two.getLastChainSymbol(two.getHIERARCHY_LEVEL(), false, false, true, false, two).getRENPY_TRIMMED_LINE());
        assertEquals("label five:", two.getLastChainSymbol(two.getHIERARCHY_LEVEL(), false, true, false, false, two).getRENPY_TRIMMED_LINE());
        assertEquals("return", two.getLastChainSymbol(two.getHIERARCHY_LEVEL(), false, true, true, false, two).getRENPY_TRIMMED_LINE());
    }
}
