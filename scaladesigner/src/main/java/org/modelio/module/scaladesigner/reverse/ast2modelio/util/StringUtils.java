package org.modelio.module.scaladesigner.reverse.ast2modelio.util;

public class StringUtils {
    public static String prefix(String fullString, String suffix) {
        return fullString.substring(0,fullString.lastIndexOf(suffix));
    }

    public static String beforeFirst(String string, char symbol) {
        return string.substring(0, string.indexOf(symbol));
    }

    public static String afterFirst(String string, char symbol) {
        return string.substring(string.indexOf(symbol)+1);
    }

    public static String beforeFirstDot(String string) {
        return beforeFirst(string,'.');
    }

    public static String afterFirstDot(String string) {
        return afterFirst(string, '.');
    }

    public static String afterLast(String string, char symbol) {
        return string.substring(string.lastIndexOf(symbol)+1);
    }

    public static String afterLastDot(String string) {
        return afterLast(string, '.');
    }
}
