package org.bullithulli.utils;

import org.junit.Test;

import static org.bullithulli.utils.textUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestTextUtilsTest {
    @Test
    public void countLeadingWhitespacesTest() {
        assertEquals(countLeadingWhitespaces("\t"), 1);
        assertEquals(countLeadingWhitespaces("\t\t some string"), 3);
        assertEquals(countLeadingWhitespaces("\t\tsome string"), 2);
        assertEquals(countLeadingWhitespaces("    "), 4);
        assertEquals(countLeadingWhitespaces(" "), 1);
    }

    @Test
    public void countIndentationsTest() {
        assertEquals(countIndentations(0, true, 0), 0);
        assertEquals(countIndentations(2, true, 0), 2);
        assertEquals(countIndentations(2, true, 312), 2);
        assertEquals(countIndentations(4, false, 4), 1);
        assertEquals(countIndentations(4, false, 2), 2);
        assertEquals(countIndentations(8, false, 4), 2);
    }

    @Test
    public void getUncommentedStringTest() {
        assertEquals("$  mename = renpy.input(\"Какое твое имя? (Стандартное - 'Элис')\", exclude=\"[]{}\")", getUncommentedString("$  mename = renpy.input(\"Какое твое имя? (Стандартное - 'Элис')\", exclude=\"[]{}\")"));
        assertEquals("old \"Self-voicing disabled.\"", getUncommentedString("old \"Self-voicing disabled.\""));
        assertEquals("abcd", getUncommentedString("#abcd"));
        assertEquals("abcd", getUncommentedString("#      abcd"));
        assertEquals("\"abcd\"", getUncommentedString("#      \"abcd\""));
        assertEquals("'abcd'", getUncommentedString("#      'abcd'"));
        assertEquals("'abcd'", getUncommentedString("#    #######  'abcd'"));
        assertEquals("", getUncommentedString("#   !!!"));
    }

    @Test
    public void removeAndContainHelpersCoverSymbolAndWordBoundaries() {
        assertEquals("abc", removeQuotesFromLine("\"a'b\"c'"));
        assertEquals("call screen hi", removeBrackets("call (screen) hi"));
        assertEquals("call screen hi", removeFlowerBrackets("call {screen} hi"));
        assertEquals("call screen hi", removeSquareBrackets("call [screen] hi"));

        assertTrue(containsExactWord("and or menu", "and"));
        assertTrue(containsExactWord("and\tor\tmenu", "or"));
        assertFalse(containsExactWord("android menu", "and"));
        assertFalse(containsExactWord("menu or", "orx"));
        assertFalse(containsExactWord("", "or"));
    }

    @Test
    public void tabAndIndentHelpersHandleExtremeInputs() {
        assertEquals("", getTabbedString(0));
        assertEquals("\t\t\t", getTabbedString(3));
        assertEquals("", getTabbedString(-2));
        assertEquals(0, countIndentations(0, false, 4));
        assertEquals(3, countIndentations(3, true, 4));
        assertEquals(2, countIndentations(8, false, 4));
        assertEquals(1, countIndentations(7, false, 7));
    }
}
