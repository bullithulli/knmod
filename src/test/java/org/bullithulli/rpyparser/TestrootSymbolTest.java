package org.bullithulli.rpyparser;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class TestrootSymbolTest {
    @Test
    public void test1() {
        rootSymbol rootSymbol = new rootSymbol(RENPY_SYMBOL_TYPE.ROOT_FILE, "\n");
        assertEquals(rootSymbol.NUMBER_OF_HIERARCHY, -1);
        assertNull(rootSymbol.getCHAIN_PARENT_SYMBOL());
        assertNull(rootSymbol.getHIERARCHY_PARENT_SYMBOL());
        assertEquals(rootSymbol.toString(),"\n");
    }
}
