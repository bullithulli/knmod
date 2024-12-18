package org.bullithulli.utils;

import static org.bullithulli.utils.textUtils.removeBrackets;
import static org.bullithulli.utils.textUtils.removeFlowerBrackets;
import static org.bullithulli.utils.textUtils.removeQuotesFromLine;
import static org.bullithulli.utils.textUtils.removeSquareBrackets;

public class modUtils {
	public static String knmodSay(String trimmed_line) {
		return "KN_MOD " + "\"" + removeSquareBrackets(removeFlowerBrackets(removeBrackets(removeQuotesFromLine(trimmed_line)))) + "\"";
	}
}
