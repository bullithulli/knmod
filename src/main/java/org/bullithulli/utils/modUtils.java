package org.bullithulli.utils;

import java.util.ArrayList;

import static org.bullithulli.utils.textUtils.*;

public class modUtils {
    public static String knmodSay(String trimmed_line, ArrayList<String> silenceKNMOD_for) {
        for (String item : silenceKNMOD_for) {
            if (trimmed_line.startsWith(item)) {
                return trimmed_line;
            }
        }
        return "KN_MOD " + "\"" + removeSquareBrackets(removeFlowerBrackets(removeBrackets(removeQuotesFromLine(trimmed_line)))) + "\"";
    }
}
