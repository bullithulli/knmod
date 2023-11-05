package org.bullithulli.utils;

import static org.bullithulli.utils.textUtils.removeQuotesFromLine;

public class modUtils {
    public static String knmodSay(String line) {
        return "KN_MOD " + "\"" + removeQuotesFromLine(line) + "\"";
    }
}
