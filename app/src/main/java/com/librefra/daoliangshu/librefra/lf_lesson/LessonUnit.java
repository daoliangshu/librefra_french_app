package com.librefra.daoliangshu.librefra.lf_lesson;

import android.content.Context;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.vocab.VocabularyListSet;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gigitintin on 06/08/16.
 * This class represents a lesson contained in tags<unit></unit>
 */
public class LessonUnit {
    public static final int ZH = 1;
    public static final int FR = 0;
    private int lineCnt = 0;
    private String fn;
    private String name = "";
    private String nameZh = "";
    private String thematic = "";
    private String level;
    private String contentType = "normal";
    private ArrayList<String> content_fr;
    private ArrayList<String> content_zh;
    public ArrayList<WaveSet> activities;
    public VocabularyListSet vocList;
    public HashMap<Integer, String> explanationMap;
    public Context context;

    public LessonUnit(Context c) {
        this.context = c;
        content_fr = new ArrayList<>();
    }

    public LessonUnit(Element unit_tree, Context c) {
        this.context = c;
        this.initUnit(unit_tree);
    }


    /**
     * Set a lesson, provided a xml Element tagged "unit"
     *
     * @param unitTree
     */
    public void initUnit(Element unitTree) {
        String title = unitTree.getElementsByTagName("title").
                item(0).getTextContent();
        this.setTitle(title);

        try {
            String title_zh = unitTree.getElementsByTagName("title_zh").
                    item(0).getTextContent();
            setTitleZh(title_zh);
        } catch (Exception ex) {
            ex.printStackTrace();
            setTitleZh("無");
        }

        this.vocList = new VocabularyListSet(context, unitTree);

        Element content_fr = (Element) unitTree.
                getElementsByTagName("content_fr").item(0);
        Element content_zh = (Element) unitTree.getElementsByTagName("content_zh").item(0);
        NodeList item_zh = content_zh.getElementsByTagName("item");
        NodeList items = content_fr.getElementsByTagName("item");
        this.content_fr = new ArrayList();
        this.content_zh = new ArrayList<>();
        this.contentType = unitTree.hasAttribute("type") ? unitTree.getAttribute("type") : "normal";
        if (this.contentType.equals("letter")) {
            String[] headers = getLetterHeaders(unitTree);
            int zhOffset = 4;
            for (int i = 0; i < 2; i++) {
             /* Set top letter headers */
                addContent(headers[i], LessonUnit.FR);
                addContent(headers[zhOffset + i], LessonUnit.ZH);
            }
            this.setCommonContent(items, item_zh);
            for (int i = 0; i < 2; i++) {
             /* Set end letter headers */
                addContent(headers[i + 2], LessonUnit.FR);
                addContent(headers[zhOffset + i + 2], LessonUnit.ZH);
            }
        } else {
            this.setCommonContent(items, item_zh);
        }

        this.activities = new ArrayList<>();
        Element actiList = (Element) unitTree.getElementsByTagName("activity_list").item(0);
        NodeList acties = actiList.getElementsByTagName("activity");

        for (int i = 0; i < acties.getLength(); i++) {
            activities.add(new WaveSet(context, (Element) acties.item(i)));
        }
        if (unitTree.hasAttribute("level")) {
            switch (Integer.parseInt(unitTree.getAttribute("level"))) {
                case 0:
                    level = context.getResources().getString(R.string.beginner);
                    break;
                case 1:
                    level = context.getResources().getString(R.string.intermediate);
                    break;
                case 2:
                    level = context.getResources().getString(R.string.advanced);
                    break;
                default:
                    level = context.getResources().getString(R.string.beginner);
            }
        } else {
            level = context.getResources().getString(R.string.beginner);
        }
        if (unitTree.hasAttribute("thematic")) {
            // Thematic is a custom tag that link units togethers
            thematic = unitTree.getAttribute("thematic");
        } else {
            thematic = context.getResources().getString(R.string.default_lesson_thematic);
        }
        initExplanations(unitTree);
    }

    /**
     * Initialize the list of explanations for the lesson,
     * There are stored in an HashMap where the key is an integer corresponding
     * to the line index in the lesson ( dot not confused with the line index in the TextView
     * ,which has to be calculated separatetly)
     */
    private void initExplanations(Element unitTree) {
        this.explanationMap = new HashMap<Integer, String>();
        NodeList explNodeList = unitTree.getElementsByTagName("explanation");
        if (explNodeList != null && explNodeList.getLength() > 0) {
            NodeList itemNodeList = ((Element) explNodeList.item(0)).getElementsByTagName("item");
            for (int i = 0; i < itemNodeList.getLength(); i++) {
                Element item = (Element) itemNodeList.item(i);
                this.explanationMap.put(Integer.parseInt(item.getAttribute("index")),
                        item.getTextContent());
            }
        }

    }

    public void addContent(String line, int lang) {
        switch (lang) {
            case LessonUnit.FR:
                content_fr.add(line);
                break;
            case LessonUnit.ZH:
                content_zh.add(line);
                break;
            default:
        }
    }

    /*---------------------------------------------*/
    /*----------------Setters----------------------*/
    /*---------------------------------------------*/

    /**
     * Set the core text content of the lesson
     *
     * @param itemsFr
     * @param itemsZh
     */
    private void setCommonContent(NodeList itemsFr, NodeList itemsZh) {
        for (int i = 0; i < itemsFr.getLength(); i++) {
            Element el = (Element) itemsFr.item(i);
            Element el_zh = (Element) itemsZh.item(i);
            NodeList sub_fr = el.getElementsByTagName("ctn");
            NodeList sub_zh = el_zh.getElementsByTagName("ctn");
            //"ctn" is now "content"
            if (sub_fr == null || sub_fr.getLength() <= 0) {
                sub_fr = el.getElementsByTagName("content");
                sub_zh = el_zh.getElementsByTagName("content");
            }
            Element line_zh = (Element) sub_zh.item(0);
            Element line_fr = (Element) sub_fr.item(0);
            this.addContent(line_fr.getTextContent(), LessonUnit.FR);
            this.addContent(line_zh.getTextContent(), LessonUnit.ZH);
        }
    }

    public void setTitle(String n) {
        name = n;
    }

    public void setTitleZh(String n) {
        nameZh = n;
    }

    public void setSize(int size) {
        lineCnt = size;
    }

    public void setThematic(String t) {
        thematic = t;
    }


    public void setFilename(String filename) {
        this.fn = filename;
    }


    /*---------------------------------------------*/
    /*----------------Getters----------------------*/
    /*---------------------------------------------*/
    public String getFilename() {
        return this.fn;
    }

    public String getLine(int index, int lang) {
        switch (lang) {
            case LessonUnit.FR:
                return this.content_fr.get(index);
            case LessonUnit.ZH:
                return this.content_zh.get(index);
            default:
                return null;
        }
    }

    public int getLineCnt() {
        return this.content_fr.size();
    }

    public String getType() {
        if (this.contentType == null) return "normal";
        return this.contentType;
    }

    /**
     * [0] top_date_place header
     * [1] top_politeness header
     * [2] end politeness header
     * [3] end signature header
     *
     * @param unit_tree
     * @return
     */
    private String[] getLetterHeaders(Element unit_tree) {
        String[] headers = new String[8];
        int zhOffset = 4;
        String tag_prefix[] = {"letter_header", "letter_tail"};
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                headers[i + 2 * j] = unit_tree.getElementsByTagName(tag_prefix[j] + (1 + i) + "_fr").
                        item(0).getTextContent();
                headers[zhOffset + i + 2 * j] = unit_tree.getElementsByTagName(tag_prefix[0] + (1 + i) + "_zh").
                        item(0).getTextContent();
            }
        }
        return headers;
    }

    public String getTitle() {
        return this.name;
    }

    public String getLevel() {
        return this.level;
    }

    public String getThematic() {
        return this.thematic;
    }

    public String getTitleZh() {
        return this.nameZh;
    }

    public WaveSet getWaveSet(int index) {
        if (index >= 0 && index < this.activities.size()) {
            return this.activities.get(index);
        }
        return null;
    }

    public int getWaveSetCount() {
        return this.activities.size();
    }

    public ArrayList<HashMap<String, String>> getActivitiesInformation() {
        if (this.activities.size() <= 0) return null;
        ArrayList<HashMap<String, String>> res = new ArrayList<>();
        for (int i = 0; i < this.activities.size(); i++) {
            HashMap<String, String> actInfoEntry = new HashMap<>();
            actInfoEntry.put("titleFr", this.activities.get(i).getTitleFr());
            actInfoEntry.put("titleZh", this.activities.get(i).getTitleZh());
            actInfoEntry.put("waveCount", String.valueOf(this.activities.get(i).getWaveCount()));
            actInfoEntry.put("filename", this.fn);
            res.add(actInfoEntry);
        }
        return res;
    }

    public HashMap<Integer, String> getExplanations() {
        return this.explanationMap;
    }
}
