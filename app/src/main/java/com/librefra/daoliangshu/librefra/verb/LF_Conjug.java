package com.librefra.daoliangshu.librefra.verb;

/**
 * Created by daoliangshu on 2016/11/20.
 */

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class LF_Conjug {
    public final static String PRESENT = "present";
    public final static String FUTUR = "futur";
    public final static String SIMPLE_PAST = "simple_past";
    public final static String SUBJ_PRES = "sub_present";
    public final static String IMPARFAIT = "imparfait";
    public final static String SUBJ_IMP = "sub_imparfait";
    public final static String COND_PRES = "cond_pres";
    public final static String COND_PAST = "cond_past";
    public final static String PART_PAST = "ppast";
    public final static String PART_PRES = "ppres";

    final static public int PRESENT_INT = 1;
    final static public int FUTUR_INT = 2;
    final static public int PASS_SIMPLE_INT = 3;
    final static public int IMPARFAIT_INT = 4;
    final static public int SUBJ_PRESENT_INT = 5;
    final static public int SUBJ_IMPARFAIT_INT = 6;


    public final static String[] tenseList = {
            PRESENT, FUTUR, SIMPLE_PAST, SUBJ_PRES, IMPARFAIT, SUBJ_IMP
    };

    private LF_TermTree rootTerm;
    private Element irregRes;
    private Element irregTerm;
    private Element listTerm;
    private String[] listIrregModels;
    private String[] listIrregIr;
    private String[] verbs;

    private Context context;

    /*------------------------------------------------*/
    /*-----------CONSTRUCTOR--------------------------*/
    /*------------------------------------------------*/
    public LF_Conjug(Context context) {
        this.context = context;
        init();
    }

    /*------------------------------------------------*/
    /*--------------INITIALIZATORS--------------------*/
    /*------------------------------------------------*/
    public void init() {

        try {
            String myContent = "";
            InputStream is = context.getAssets().open("v/irreg_conjugate.xml");
            if (is == null) {
                System.out.println("Failed to load resources.");
                return;
            }
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String str2;

            while ((str2 = br.readLine()) != null) {
                myContent += str2;
            }
            br.close();
            is = new ByteArrayInputStream(myContent.getBytes("UTF-8"));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();


            Document doc = dBuilder.parse(is, "UTF-8");
            doc.getDocumentElement().normalize();

            listIrregModels = doc.getElementsByTagName("list").item(0).getTextContent().split(",");
            listIrregIr = doc.getElementsByTagName("list_ir").item(0).getTextContent().split(",");
            irregTerm = (Element) doc.getElementsByTagName("term_irreg").item(0);
            listTerm = (Element) doc.getElementsByTagName("term_list").item(0);
            verbs = doc.getElementsByTagName("list_verbs").item(0).getTextContent().split("(\\s|\n)+");
        } catch (Exception e) {
            e.printStackTrace();
        }
        initTerminationTree();

    }

    public void initTerminationTree() {
        rootTerm = new LF_TermTree(null, "");
        HashMap<String, ArrayList<String[]>> termMap =
                new HashMap<>();
        for (String t : LF_Conjug.tenseList) {
            /* Iterate throught tenses */
            NodeList nl = listTerm.getElementsByTagName(t);

            for (int n = 0; n < nl.getLength(); n++) {
                /* Iterate through terminaison types in tense */
                Node el = nl.item(n).getFirstChild();
                do {

                    if (el.getNodeName().startsWith("term")) {
                        String persons[] = el.getTextContent().split(",");
                        for (int i = 0; i < persons.length; i++) {
                            if (persons.length == 7) {
                                Log.e("ERR", "7, but expected 6");
                            }
                        /* Iterate through persons */
                            String[] str = {t, String.valueOf(i), el.getNodeName()};
                            if (termMap.containsKey(persons[i])) {
                                termMap.get(persons[i]).add(str);
                            } else {
                                termMap.put(persons[i], new ArrayList<String[]>());
                                termMap.get(persons[i]).add(str);
                            }
                        }
                    }
                    el = el.getNextSibling();
                } while (el != null);
            }//end  term iteration
        }//end tense iteration

        for (String k : termMap.keySet()) {
            ArrayList<String[]> s = termMap.get(k);
            for (String[] arr : s) {
                rootTerm.addToLeaf(k, arr);
            }
        }
    }


    /*------------------------------------------------*/
    /*--------------GETTERS---------------------------*/
    /*------------------------------------------------*/
    /*
     * Retrieve posible irregular infinitives according to the possible radical form of the getConjugate word
     * Of course, it doesn't means that this verb exist, but it eliminates choices when searching in a database
     */
    public ArrayList<String> getPossibleIrregInfinitives(String possibleRadicalForm) {
        Node n = irregTerm.getFirstChild();
        ArrayList<String> res = new ArrayList<>();
        while (n != null) {
			/* Iterate in irregular radicals by type */
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) ((Element) n).getElementsByTagName("rad").item(0);
                if (el != null) {
                    String[] rads = el.getTextContent().split(",");
                    for (int i = 0; i < rads.length; i++) {
                        if (possibleRadicalForm.endsWith(rads[i]) || rads[i].equals("$")) {
                            String nodeName = n.getNodeName();
                            if (n.getNodeName().equals("item")) {
                                nodeName = ((Element) n).getAttribute("name");
                            }
                            if (rads[i].equals("$")) {
                                res.add(possibleRadicalForm + nodeName);
                            } else {
                                res.add(possibleRadicalForm.substring(0, possibleRadicalForm.length() - rads[i].length())
                                        //res.add(possibleRadicalForm
                                        + nodeName);
                            }
                            break;
                        }
                    }
                }
            }
            n = n.getNextSibling();
        }
        if (res.size() >= 1) return res;
        return null;
    }

    public ArrayList<String> getInfinitivesToCheck(String word) {
        ArrayList<String> r = rootTerm.getRadicals(word);
        for (String rad : r) {
            Log.i("rad: ", rad);
        }
        ArrayList<String> res = new ArrayList<String>();
        for (String str : r) {
            ArrayList<String> s = getPossibleIrregInfinitives(str);
            if (res == null) res = s;
            else {
                if (s != null) {
                    res.addAll(s);
                }
            }
            for (String str2 : s) {
                System.out.println("RADICCALS FOUND: " + str2 + "  " + str);
            }
            if (word.length() > 2) {
				/* If the verb is regular, the  real infinitif should be one of them :*/
                res.add(str + "er");
                res.add(str + "ir");
            }
        }
        ArrayList<String> finalList = new ArrayList<>();
        ArrayList<String[]> conditions = rootTerm.getAllPossibilities(word);
        for (String s : res) {
            if (checkInfinitifMatchConjugated(s, word, conditions)) {
                finalList.add(s);
            }
        }
        return finalList;
    }


    public boolean checkInfinitifMatchConjugated(String infinitif,
                                                 String conjugated,
                                                 ArrayList<String[]> conditions) {
        // Only check for possible case
        for (String[] cond : conditions) {
            Log.i("Person:", cond[1]);
            String subRes = getConjugate(infinitif, cond[0], Integer.parseInt(cond[1]));
            if (conjugated.equals(subRes)) {
                return true;
            }
        }
        return false;
    }

    public String getConjugate(String infinitiv, int time, int person) {
        switch (time) {
            case PRESENT_INT:
                return getPresent(infinitiv, person);
            case IMPARFAIT_INT:
                return getImparfait(infinitiv, person);
            case FUTUR_INT:
                return getFutur(infinitiv, person);
            case PASS_SIMPLE_INT:
                return getSimplePast(infinitiv, person);
            case SUBJ_PRESENT_INT:
                return getSubjPresent(infinitiv, person);
            case SUBJ_IMPARFAIT_INT:
                return getSubjImparfait(infinitiv, person);
            default:
                return null;
        }
    }

    public String getConjugate(String infinitiv, String time, int person) {
        if (time.equals(PRESENT)) {
            return getPresent(infinitiv, person);
        } else if (time.equals(IMPARFAIT)) {
            return getImparfait(infinitiv, person);
        } else if (time.equals(SIMPLE_PAST)) {
            return getSimplePast(infinitiv, person);
        } else if (time.equals(FUTUR)) {
            return getFutur(infinitiv, person);
        } else if (time.equals(SUBJ_PRES)) {
            return getSubjPresent(infinitiv, person);
        } else if (time.equals(SUBJ_IMP)) {
            return getSubjImparfait(infinitiv, person);
        } else {
            return null;
        }
    }

    /**
     * Check for irregular model for the specified infinitf and getConjugate it according to that.
     * If is not irregular, returns null
     *
     * @param infinitif infinitive form of a verb
     * @param tense     the tense at which to getConjugate the verb
     * @param person    from 1 to 6
     * @return conjugate form of the irreg verb at given tense and person
     */
    private String getIrregular(String infinitif, String tense, int person) {
        String[] radicals = null;
        String code = "000000";
        int termType = 1;
        String res = "";
        String model = getIrregModel(infinitif);
        if (model == null) return null;
        String prefix = infinitif.substring(0, infinitif.length() - model.length());
        Element el = (Element) irregTerm.getElementsByTagName(model).item(0);
        if (el == null) {
            NodeList nl = irregTerm.getElementsByTagName("item");
            for (int i = 0; i < nl.getLength(); i++) {
                if (((Element) nl.item(i)).getAttribute("name").equals(model)) {
                    el = (Element) nl.item(i);
                    break;
                }
            }
        }
        if (el.getElementsByTagName("rad").getLength() > 0) {
			/* Load radicals for the specified verb */
            radicals = el.getElementsByTagName("rad").item(0).getTextContent().split(",");
        }

        Element tenseInfo = (Element) el.getElementsByTagName(tense).
                item(0);
        if (tenseInfo != null) {
			/* If info are available for this tense */
			/*(1) retrieve code */
            if (tenseInfo.getElementsByTagName("code").item(0) != null &&
                    tenseInfo.getElementsByTagName("code").item(0).
                            getTextContent().length() > 0) {
						/* Retrieves the code for spanning the radicals if exists
						 *  for the specified tense */
                code = tenseInfo.getElementsByTagName("code").item(0).
                        getTextContent();

            }
			/*(2) retrieve termination type */
            if (tenseInfo.getElementsByTagName("t").getLength() > 0) {
				/* Indicate which terminaison to use */
                termType = Integer.parseInt(tenseInfo.getElementsByTagName("t").item(0).
                        getTextContent());
            }
        }
        Element tense_term = (Element) listTerm.getElementsByTagName(tense).item(0);
        String finalRadical;
        if (code != null && code.length() >= person) {
            int index = Integer.parseInt(
                    String.format(Locale.ENGLISH, "%s", code.charAt(person)));
            if (radicals != null && index >= 0 && index < radicals.length)
                finalRadical = radicals[index];
            else finalRadical = " ";
        } else {
            finalRadical = model.substring(0, model.length() - 2);
        }

        String finalTerm = tense_term.getElementsByTagName("term" + termType).item(0).
                getTextContent().split(",")[person];
        if (finalTerm.length() > 0 && finalRadical.length() > 0) {
            finalRadical = getMobileVowel(finalRadical, finalTerm.charAt(0));
        }
        if (!finalRadical.equals("$")) prefix += finalRadical;
        if (!finalTerm.equals("$")) {
            prefix += finalTerm;
        }
        return prefix;
    }

    /**
     * 0 = first group
     * 1 = second group
     * 2 = third group
     *
     * @param infinitif
     * @return the group of the verb
     */
    public int getGroup(String infinitif) {
        if (infinitif.length() > 2 && infinitif.endsWith("er") && !infinitif.endsWith("aller")) {
			/* Regular verb from first group */
            return 0;
        } else if (infinitif.length() > 2 &&
                (infinitif.endsWith("ir") || infinitif.endsWith("îr")) &&
                !isVowel(infinitif.charAt(infinitif.length() - 3))) {
			/* Is candidate for second group */
            for (int i = 0; i < listIrregIr.length; i++) {
                String irrg = listIrregIr[i].trim();
                if (infinitif.endsWith(irrg)) {
					/* Is not second group */
                    return 2;
                }
            }
			/* Is second group */
            return 1;
        }
        return 2;
    }

    /**
     * @param prefix    the prefix of the word
     * @param startTerm first vowel of the termination
     * @return New prefix with mobile voyel or phonetic symbol added
     */
    private String getMobileVowel(String prefix, char startTerm) {
        int goOn = -1;
        switch (startTerm) {
            case 'a':
            case 'o':
            case 'â':
            case 'û':
                goOn = 0;
                break;
            case 'r':
                goOn = 1;
                break;
            default:
        }
        if (goOn > -1) {
            switch (prefix.charAt(prefix.length() - 1)) {
                case 's':
                    if (goOn == 1) return prefix + 'e';
                    break;
                case 'g':
                    return prefix + 'e';
                case 'c':
                    if (goOn == 1) return prefix + 'e';
                    else if (goOn == 0) return prefix.substring(0, prefix.length() - 1) + 'ç';
                    break;
                default:
            }
        }
        return prefix;
    }


    /*--------------------------------------------------------*/
	/*----------------PRESENT--------------------------*/
	/*--------------------------------------------------------*/
    public String getPresent(String infinitif, int person) {
        String finalTerm;
        String prefix;
        switch (getGroup(infinitif)) {
            case 0:
			/* Regular verb from First Group */
                finalTerm = ((Element) listTerm.getElementsByTagName(LF_Conjug.PRESENT).item(0)).
                        getElementsByTagName("term1").item(0).getTextContent().split(",")[person];
                prefix = infinitif.substring(0, infinitif.length() - 2);
                prefix = getMobileVowel(prefix,
                        finalTerm.charAt(0));
                return prefix + finalTerm;
            case 1:
			/* Second Group */
                finalTerm = ((Element) listTerm.getElementsByTagName(LF_Conjug.PRESENT).item(0)).
                        getElementsByTagName("term2").item(0).getTextContent().split(",")[person];
                prefix = infinitif.substring(0, infinitif.length() - 2);
                return prefix + finalTerm;

            case 2:
			/* IRREG */
                return getIrregular(infinitif, LF_Conjug.PRESENT, person);
            default:
                return null;
        }
    }


    /*--------------------------------------------------------*/
	/*------------------IMPARFAIT----------------------*/
	/*--------------------------------------------------------*/
    public String getImparfait(String infinitif, int person) {
        String prefix;
        String finalTerm;
        int termIndex = 1;
        int group = 1;
        group = getGroup(infinitif);
        if (group == 2) return getIrregular(infinitif, LF_Conjug.IMPARFAIT, person);
        if (group == 1) {
            termIndex = 2;
        }
        finalTerm = ((Element) listTerm.
                getElementsByTagName(LF_Conjug.IMPARFAIT).item(0)).
                getElementsByTagName("term" + termIndex).item(0).getTextContent().split(",")[person];
        prefix = infinitif.substring(0, infinitif.length() - 2);
        prefix = getMobileVowel(prefix, finalTerm.charAt(0));
        return prefix + finalTerm;
    }

    /*--------------------------------------------------------*/
	/*-------------SIMPLE_PAST-------------------------*/
	/*--------------------------------------------------------*/
    public String getSimplePast(String infinitif, int person) {
        String prefix;
        String finalTerm;
        int termIndex = 1;
        int group = 1;
        group = getGroup(infinitif);
        if (group == 2) return getIrregular(infinitif, LF_Conjug.SIMPLE_PAST, person);
        if (group == 1) {
            termIndex = 2;
        }
        finalTerm = ((Element) listTerm.
                getElementsByTagName(LF_Conjug.SIMPLE_PAST).item(0)).
                getElementsByTagName("term" + termIndex).item(0).getTextContent().split(",")[person];
        prefix = infinitif.substring(0, infinitif.length() - 2);
        prefix = getMobileVowel(prefix, finalTerm.charAt(0));
        return prefix + finalTerm;
    }

    /*--------------------------------------------------------*/
	/*----------------------FUTUR----------------------*/
	/*--------------------------------------------------------*/
    public String getFutur(String infinitif, int person) {
        String prefix;
        String finalTerm;
        int termIndex = 1;
        int group = 1;
        group = getGroup(infinitif);
        if (group == 2) return getIrregular(infinitif, LF_Conjug.FUTUR, person);
        if (group == 1) {
            termIndex = 1;
        }
        finalTerm = ((Element) listTerm.
                getElementsByTagName(LF_Conjug.FUTUR).item(0)).
                getElementsByTagName("term" + termIndex).item(0).getTextContent().split(",")[person];
        prefix = infinitif.substring(0, infinitif.length() - 1);
        prefix = getMobileVowel(prefix, finalTerm.charAt(0));
        return prefix + finalTerm;
    }

    /*--------------------------------------------------------*/
	/*--------------SUBJONTIF PRESENT------------------*/
	/*--------------------------------------------------------*/
    public String getSubjPresent(String infinitif, int person) {
        String prefix;
        String finalTerm;
        int termIndex = 1;
        int group = 1;
        group = getGroup(infinitif);
        if (group == 2) return getIrregular(infinitif, LF_Conjug.SUBJ_PRES, person);
        if (group == 1) {
            termIndex = 2;
        }
        finalTerm = ((Element) listTerm.
                getElementsByTagName(LF_Conjug.SUBJ_PRES).item(0)).
                getElementsByTagName("term" + termIndex).item(0).getTextContent().split(",")[person];
        prefix = infinitif.substring(0, infinitif.length() - 1);
        prefix = getMobileVowel(prefix, finalTerm.charAt(0));
        return prefix + finalTerm;
    }

    /*--------------------------------------------------------*/
	/*--------------SUBJONTIF IMPARFAIT---------------*/
	/*--------------------------------------------------------*/
    public String getSubjImparfait(String infinitif, int person) {
        String prefix;
        String finalTerm;
        int termIndex = 1;
        int group = 1;
        group = getGroup(infinitif);
        if (group == 2) return getIrregular(infinitif, LF_Conjug.SUBJ_IMP, person);
        if (group == 1) {
            termIndex = 2;
        }
        finalTerm = ((Element) listTerm.
                getElementsByTagName(LF_Conjug.SUBJ_IMP).item(0)).
                getElementsByTagName("term" + termIndex).item(0).getTextContent().split(",")[person];
        prefix = infinitif.substring(0, infinitif.length() - 1);
        prefix = getMobileVowel(prefix, finalTerm.charAt(0));
        return prefix + finalTerm;
    }


    /*--------------------------------------------------------*/
	/*------------------INFINITIF----------------------*/
	/*--------------------------------------------------------*/
    public String getInfinitif(String verb) {
        if (getGroup(verb) != 2 && getIrregModel(verb) != null) {
			/* Is already an infinitif */
            return verb;
        }
        return null;
    }

    /**
     * If the verb is an irrregular verb, it extracts the model to getConjugate it.
     *
     * @param infinitif the infinitive form of a verb
     * @return conjugason model, if not found returns null
     */
    public String getIrregModel(String infinitif) {
        if (listIrregModels == null) return null;
        for (int i = 0; i < listIrregModels.length; i++) {
            if (infinitif.endsWith(listIrregModels[i].trim())) {
				/* Check if the infinit is mathcing a irregular verb */
                return listIrregModels[i].trim();
            }
        }
        return null;
    }

    public boolean isVowel(char c) {
        switch (c) {
            case 'a':
            case 'u':
            case 'e':
            case 'y':
            case 'o':
            case 'i':
            case 'â':
            case 'î':
            case 'è':
                return true;
            default:
        }
        return false;
    }

    /**
     * @return string array containing verbs ( for check/debugging purpose)
     */
    public String[] getVerbs() {
        return verbs;
    }
}




