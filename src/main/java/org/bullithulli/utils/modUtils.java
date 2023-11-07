package org.bullithulli.utils;

import static org.bullithulli.utils.textUtils.*;

public class modUtils {
    public static String knmodSay(String line) {
        return "KN_MOD " + "\"" + removeSquareBrackets(removeFlowerBrackets(removeBrackets(removeQuotesFromLine(line)))) + "\"";
    }
}
