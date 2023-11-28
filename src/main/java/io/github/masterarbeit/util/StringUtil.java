package io.github.masterarbeit.util;

public class StringUtil {
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String firstCharToLowercase(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String removeFirstChar(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(1);
    }

    public static String removeLastChar(String str) {
        return removeChars(str, 1);
    }

    public static String removeChars(String str, int numberOfCharactersToRemove) {
        if (str != null && !str.trim().isEmpty()) {
            return str.substring(0, str.length() - numberOfCharactersToRemove);
        }
        return "";
    }

}
