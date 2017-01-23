package com.librefra.daoliangshu.librefra.tools;

import android.content.Context;

import com.librefra.daoliangshu.librefra.R;

/**
 * Created by gigitintin on 13/04/16.
 * Utilities for processing french related formatage,
 * as well as extraction of raw columns from database
 */
public class FraUtils {

    public static final int SUBST = 101;
    public static final int VERB = 102;
    public static final int OTHER = 105;


    public static String getGenre_fromOtherTable(Context c, char code) {
        String str;
        switch (code) {
            case 'a':
                return c.getResources().getString(R.string.adj);
            case 'p':
                return c.getResources().getString(R.string.pron);
            case 'q':
                return c.getResources().getString(R.string.prep);
            case 'e':
                return c.getResources().getString(R.string.expr);
            case 'r':
                return c.getResources().getString(R.string.proverb);
            case 'd':
                return c.getResources().getString(R.string.adv);
            default:
                return null;
        }
    }

    public static final boolean isVoyel(char c) {
        switch (c) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
            case 'é':
            case 'è':
                return true;
            default:
                return false;
        }
    }

    public static String formatGenre(int genreCode) {
        switch (genreCode) {
            case 0:
                return "陽性.";
            case 1:
                return "陰性.";
            case 2:
                return "陽性/陰性.";
            case 3:
                return "plu.";
            default:
                return "non.";
        }
    }

    public static String formatPhonetic(String phon) {
        if (phon == null) return null;
        String res = "";
        if (phon.length() <= 0) return "";
        if (phon.charAt(0) != '[') res += "[";
        for (int i = 0; i < phon.length(); i++) {
            res += FraUtils.formatPhoneticUnit(phon.charAt(i));
        }
        if (res.endsWith("]") == false) res += "]";
        return res;
    }

    public static String formatPhoneticUnit(char c) {
        String res = "" + c;
        switch (c) {
            case '§':
                res = "ɔ";
                break;
            case '@':
                res = "ɑ̃";
                break;
            case 'N':
                res = "ɲ";
                break;
            case 'S':
                res = "ʃ";
                break;
            case 'R':
                res = "ʁ";
                break;
            case 'Z':
                res = "ʒ";
                break;
            case 'E':
                res = "ɛ";
                break;
            case '°':
                res = "ø";
                break;
            case '5':
                res = "ɛ̃";
                break;
            case '2':
                res = "œ";
                break;
            case '1':
                res = "œ̃";
                break;
            case 'O':
                res = "ɔ";
                break;
            case '8':
                res = "ɥ";
                break;
            case 'G':
                res = "ŋ";
                break;
            default:
        }
        return res;
    }

    public static final String getDefiniteArticle(int code, boolean startWithVoyel) {
        switch (code) {
            case 0:
                if (startWithVoyel == false) return "le";
            case 1:
                if (startWithVoyel == false) return "la";
                return "l\'";
            case 2:
                return "le/la";
            case 3:
                return "les";
            default:
                return "#";
        }
    }

    public static boolean isEnable_TTS = false; // TextToSpeech state ( false if not available)
}
