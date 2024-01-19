package org.bullithulli.utils;

import org.junit.Test;

import static org.bullithulli.utils.textUtils.*;
import static org.junit.Assert.assertEquals;

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

    }
}
