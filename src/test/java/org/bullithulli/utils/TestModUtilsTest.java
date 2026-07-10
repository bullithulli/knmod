package org.bullithulli.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestModUtilsTest {

    @Test
    public void knmodSayStripsOnlyTheCharactersTheProductActuallyStrips() {
        assertEquals("KN_MOD \"Hello\"", modUtils.knmodSay("\"Hello\""));
        assertEquals("KN_MOD \"call screen hi\"", modUtils.knmodSay("call (screen) [hi]"));
        assertEquals("KN_MOD \"score = 1 + 2\"", modUtils.knmodSay("score = 1 + 2"));
        assertEquals("KN_MOD \"emoji 😀 stays\"", modUtils.knmodSay("emoji 😀 stays"));
        assertEquals("KN_MOD \"\"", modUtils.knmodSay(""));
    }
}
