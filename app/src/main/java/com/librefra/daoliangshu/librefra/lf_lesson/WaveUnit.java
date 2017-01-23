package com.librefra.daoliangshu.librefra.lf_lesson;

import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by gigitintin on 07/08/16.
 * A Wave corresponds to a Multiple Choice problem
 */
public class WaveUnit {
    public static final int TYPE_MULTCHOICE = 0;
    public static final int TYPE_ORDER = 1;
    private int speed = 1;
    private int cnt = 0;
    private int waveType = TYPE_MULTCHOICE;
    private int index = -1;
    private int[] correctChoices = null;
    private int[] correctOrder = null;
    private ArrayList<String[]> content;
    private String title;
    private String[] infos = {null, null}; //(0)info, (1)hint

    public WaveUnit() {
        content = new ArrayList<>();
    }

    public WaveUnit(Element wave) {
        this();

        if (wave.hasAttribute("type")) {
            if (wave.getAttribute("type").equals("multiplechoices")) {
                waveType = WaveUnit.TYPE_MULTCHOICE;
            } else if (wave.getAttribute("type").equals("order")) {
                waveType = WaveUnit.TYPE_ORDER;
            }
        }

        // Extract choices
        NodeList items = wave.getElementsByTagName("item");
        for (int i = 0; i < items.getLength(); i++) {
            Element el = (Element) items.item(i);
            String t[] = {el.getTextContent(), "f"};
            content.add(t);
        }

        switch (waveType) {
            case TYPE_MULTCHOICE:
                String correctStr[] = wave.getAttribute("correct").split(",");
                this.correctChoices = new int[correctStr.length];
                for (int i = 0; i < correctStr.length; i++) {
                    this.correctChoices[i] = Integer.parseInt(correctStr[i].trim());
                }
                for (int i = 0; i < correctStr.length; i++) {
                    int cor = Integer.parseInt(correctStr[i]);
                    if (cor >= 0 && cor < 4) {
                        content.get(Integer.parseInt(correctStr[i]))[1] = "t";
                    }
                }
                break;
            case TYPE_ORDER:
                String orderStr[] = wave.getAttribute("order").split(",");
                this.correctOrder = new int[orderStr.length];
                for (int i = 0; i < orderStr.length; i++) {
                    this.correctOrder[i] = Integer.parseInt(orderStr[i].trim());
                    Log.e("ORDER : ", "   " + this.correctOrder[i]);
                }
                int count = 0;
                for (int i = 0; i < orderStr.length; i++) {
                    int cor = Integer.parseInt(orderStr[i]);
                    if (cor >= 0 && cor < 4) {
                        /* Assign the order  to each item */
                        content.get(Integer.parseInt(orderStr[i]))[1] = String.format("%d", count);
                        count++;
                    } else {
                        System.out.print("Error setting waveUnit order");
                    }
                }
                break;
        }


        this.title = wave.getElementsByTagName("title").item(0).getTextContent();

        String index_str = wave.getAttribute("index");
        if (index_str != null && !index_str.equals("")) {
            this.index = Integer.parseInt(index_str);
        }

        if (wave.hasAttribute("spd")) {
            this.speed = Integer.parseInt(wave.getAttribute("spd"));
        }

        int index = 0;
        for (String str : new String[]{"info", "hint"}) {
            if (wave.getElementsByTagName(str) != null &&
                    wave.getElementsByTagName(str).getLength() > 0) {
                infos[index] = wave.getElementsByTagName(str).item(0).getTextContent();
            }
            ++index;
        }


    }

    public void add(String value, boolean correctness) {
        String tmp[] = {value, correctness ? "t" : "f"};
        content.add(tmp);
        cnt += 1;
    }

    public void setTitle(String t) {
        this.title = t;
    }

    public void setSpeed(int spd) {
        this.speed = spd;
    }

    public void setSpeed(String spd) {
        this.speed = Integer.parseInt(spd);
    }

    public void remove(int index) {
        content.remove(index);
    }

    public void setInfo(String str) {
        this.infos[0] = str;
    }

    public void setHint(String str) {
        this.infos[1] = str;
    }


    /*GETTER*/
    public String getTitle() {
        return this.title;
    }

    public ArrayList<String[]> getWaveItems() {
        return this.content;
    }

    public String getTextAt(int index) {
        return this.content.get(index)[0];
    }

    public boolean isCorrectAt(int index) {
        return this.content.get(index)[1].equals("t");
    }

    public boolean getCorrectness(int index) {
        return this.content.get(index)[1].equals("t");
    }

    public int getOrderAt(int index) {
        if (waveType != WaveUnit.TYPE_ORDER) return -1;
        for (int i = 0; i < this.correctOrder.length; i++) {
            if (this.correctOrder[i] == index) return i;
        }
        return -1;
    }

    public int[] getOrderMapped() {
        return correctOrder;
    }

    public int[] getOrderFromFirstToLast() {
        int[] temp = new int[correctOrder.length];
        for (int i = 0; i < temp.length; i++) {
            temp[correctOrder[i]] = i;
        }
        return temp;
    }

    public int getSpeed() {
        return this.speed;
    }

    public int getType() {
        return waveType;
    }

    /**
     * @return info, or null if no info provided
     */
    public String getInfo() {
        return infos[0];
    }

    /**
     * @return hint, or null if no hint provided
     */
    public String getHint() {
        return infos[1];
    }

    public int[] getCorrectArray() {
        return this.correctChoices;
    }

    public ArrayList<Integer> getCorrectList() {
        if (this.correctChoices == null || this.correctChoices.length <= 0) return null;
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < this.correctChoices.length; i++) {
            list.add(this.correctChoices[i]);
        }
        return list;
    }
}
