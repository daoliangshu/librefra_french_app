package com.librefra.daoliangshu.librefra.tools;

/**
 * Created by gigitintin on 07/08/16.
 * Methods for checking different things
 */
public class Checker {

    public static boolean isSymbolOutsideSentence(char a) {
        if (Character.isLetterOrDigit(a) || a == '\'' || a == ' ') return false;
        else return true;
    }

    public static boolean isSymbolOutsideWord(char a) {
        if (Character.isLetterOrDigit(a)) return false;
        else return true;
    }
}
