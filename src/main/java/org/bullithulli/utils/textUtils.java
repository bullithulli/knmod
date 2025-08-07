package org.bullithulli.utils;

public class textUtils {
    /**
     * @return : 3
     * input: ---car-----
     */
    public static int countLeadingWhitespaces(String input) {
        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            if (!Character.isWhitespace(input.charAt(i))) {
                break;
            }
            count++;
        }
        return count;
    }

    public static int countIndentations(int whiteSpacesCount, boolean isTab, int NumberOfSpacesPerIndent) {
        if (isTab) return whiteSpacesCount;
        else return whiteSpacesCount / NumberOfSpacesPerIndent;
    }

    public static String removeQuotesFromLine(String str) {
        return str.replaceAll("\"", "").replaceAll("'", "");
    }


    public static String removeBrackets(String str) {
        return str.replaceAll("\\(", "").replaceAll("\\)", "");
    }

    public static String removeFlowerBrackets(String str) {
        return str.replaceAll("\\{", "").replaceAll("\\}", "");
    }

    public static String removeSquareBrackets(String str) {
        return str.replaceAll("\\[", "").replaceAll("\\]", "");
    }

    public static boolean containsExactWord(String text, String word) {
        String[] words = text.split("\\s+"); // Split by space
        for (String w : words) {
            if (w.equals(word)) {
                return true;
            }
        }
        return false;
    }

    public static String getTabbedString(int n) {
        return "\t".repeat(Math.max(0, n));
    }

    public static String getUncommentedString(String trimmedStr) {
        int k = -1;
        if (trimmedStr.startsWith("#")) {
            for (int i = 0; i < trimmedStr.length(); i++) {
                char c = trimmedStr.charAt(i);
                if ((c >= 'a' && c <= 'z') || c == '"' || c == '\'' || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                    k = i;
                    break;
                }
            }
        } else {
            return trimmedStr;
        }
        if (k == -1) {
            return "";
        } else {
            return trimmedStr.substring(k);
        }
    }
}
