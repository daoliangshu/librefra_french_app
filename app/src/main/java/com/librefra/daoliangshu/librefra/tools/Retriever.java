package com.librefra.daoliangshu.librefra.tools;

import android.content.Context;

import com.librefra.daoliangshu.librefra.basic_dic.EntryElement;
import com.librefra.daoliangshu.librefra.lf_lesson.LessonState;
import com.librefra.daoliangshu.librefra.lf_lesson.WaveSet;
import com.librefra.daoliangshu.librefra.main.DBHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by gigitintin on 07/08/16.
 * Provides methods for retrieving different things
 */
public class Retriever {

    static DocumentBuilderFactory factory = null;
    static DocumentBuilder builder = null;


    public static int getBeginningWordIndex(int a, String text) {
        while ((a > 0) && (Checker.isSymbolOutsideWord(text.charAt(a - 1)) == false)) {
            a--;
        }
        return a;
    }

    public static int getEndWordIndex(int a, String text) {
        while ((a < text.length()) && Checker.isSymbolOutsideWord(text.charAt(a)) == false) {
            a++;
        }
        return a;
    }

    public static int getBeginningSentenceIndex(int a, String text) {
        while ((a > 0) && (Checker.isSymbolOutsideSentence(text.charAt(a - 1)) == false)) {
            a--;
        }
        return a;
    }

    public static int getEndSentenceIndex(int a, String text) {
        while ((a < text.length()) && Checker.isSymbolOutsideSentence(text.charAt(a)) == false) {
            a++;
        }
        return a;
    }


    /**
     * @param a                : index of the char that has been touched
     * @param text             : input text as String
     * @param makeStatusUpdate : Set whether the LessonState variables should be updated accordingly
     * @return
     */
    public static String getSentence(int a, String text, boolean makeStatusUpdate) {
        int start = Retriever.getBeginningSentenceIndex(a, text);
        int end = Retriever.getEndSentenceIndex(a, text);
        if (makeStatusUpdate == true) {
            LessonState.curSpeakingPortionStartIndex = start;
            LessonState.curSpeakingPortionEndIndex = end;
        }
        return text.substring(start, end);
    }

    /**
     * @param a                : index of the char that has been touched
     * @param text             : input text as String
     * @param makeStatusUpdate : Set whether the LessonState variables should be updated accordingly
     * @return
     */
    public static String getWord(int a, String text, boolean makeStatusUpdate) {
        int start = Retriever.getBeginningWordIndex(a, text);
        int end = Retriever.getEndWordIndex(a, text);
        if (makeStatusUpdate == true) {
            LessonState.curSelectedWordStartIndex = start;
            LessonState.curSelectedWordEndIndex = end;
        }
        return text.substring(start, end);
    }

    public static ArrayList<WaveSet> getActivitiesListFromInputStream(Context context,
                                                                      String isAsString,
                                                                      String fn) {
        InputStream is = null;
        if (factory == null) {
            factory = DocumentBuilderFactory.newInstance();
        }
        try {
            is = new ByteArrayInputStream(isAsString.getBytes("UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //DocumentBuilder builder = null;
        Document document = null;
        if (is == null) return null;
        if (builder == null) {
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException pe) {
                pe.printStackTrace();
            }
        }
        try {
            document = builder.parse(is);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        }
        if (document != null) {
            Element root = document.getDocumentElement();
            Element unit = (Element) document.getElementsByTagName("unit").item(0);
            NodeList actList = ((Element) (unit).
                    getElementsByTagName("activity_list").item(0)).
                    getElementsByTagName("activity");
            ArrayList<WaveSet> res = new ArrayList<>();
            for (int j = 0; j < actList.getLength(); j++) {
                WaveSet ws = new WaveSet(context, (Element) actList.item(j), j);

                ws.setFilename(fn);
                res.add(ws);
            }
            return res;
        }
        return null;

    }

    public static EntryElement createEntryElementFromMap(Context c, HashMap<String, String> entry,
                                                         int index) {
        EntryElement ee = new EntryElement(c, index);

        ee.setWord(entry.get(DBHelper.WORD));
        ee.setTrans(0, entry.get(DBHelper.TRANS_ID_1));
        ee.setTrans(1, entry.get(DBHelper.TRANS_ID_2));
        ee.setTrans(2, entry.get(DBHelper.TRANS_ID_3));
        String info = entry.get(DBHelper.INFO);

        ee.setInfos(info);
        if (entry.get(DBHelper.PHONETIC) != null)
            ee.setPhonetic(FraUtils.formatPhonetic(entry.get(DBHelper.PHONETIC)));
        int table = Integer.parseInt(entry.get(DBHelper.TB_TABLE_STR));
        if (table == DBHelper.SUBST)
            ee.setGenre("名詞. " +
                    FraUtils.formatGenre(Integer.parseInt(entry.get(DBHelper.GENRE))));
        else if (table == DBHelper.VERB) {
            ee.setHiddenButtonVisibility(true);
            ee.setGenre("v. ");
        } else if (table == DBHelper.OTHER) {
            if (entry.containsKey(DBHelper.OTHER_TYPE)) {
                if (entry.get(DBHelper.OTHER_TYPE).length() > 0) {
                    ee.setGenre(FraUtils.getGenre_fromOtherTable(c, entry.get(DBHelper.OTHER_TYPE).charAt(0)));
                }
            } else if (entry.containsKey("genre")) {
                /* When UNION : type becomes -> genre */
                if (entry.get("genre").length() > 0) {
                    ee.setGenre(FraUtils.getGenre_fromOtherTable(c, entry.get("genre").charAt(0)));
                }
            }
        }
        return ee;
    }


}


