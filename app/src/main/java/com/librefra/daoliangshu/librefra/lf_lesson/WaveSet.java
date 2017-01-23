package com.librefra.daoliangshu.librefra.lf_lesson;

import android.content.Context;
import android.util.Log;

import com.librefra.daoliangshu.librefra.R;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by gigitintin on 08/08/16.
 * WaveSet represents an Activity containing the Waves
 */
public class WaveSet {
    public int activityIndex = 0;
    public String fn = "";
    public ArrayList<WaveUnit> waves;
    public String type;
    public String titleFr;
    public String titleZh;
    public int index = -1;
    private String level;
    private String thematic;

    public WaveSet(Context context, Element activity, int actIndex) {
        this(context, activity);
        setActivityIndex(actIndex);
    }

    public WaveSet(Context context, Element activity) {
        waves = new ArrayList<>();
        this.type = activity.getAttribute("type");
        Log.i("INFO TAG:", activity.getTagName());
        Log.i("INFO_TYPE:", activity.getAttribute("type"));
        if (activity.hasAttribute("index")) {
            Log.i("INFO_INDEX:", activity.getAttribute("index"));
            this.index = Integer.parseInt(activity.getAttribute("index"));
        } else {
            this.index = -1;
        }
        if (activity.hasAttribute("level")) {
            int levelInt = Integer.parseInt(activity.getAttribute("level"));
            switch (levelInt) {
                case 0:
                    this.level = context.getResources().getString(R.string.beginner);
                    break;
                case 1:
                    this.level = context.getResources().getString(R.string.intermediate);
                    break;
                case 2:
                    this.level = context.getResources().getString(R.string.advanced);
                    break;
                default:
                    this.level = context.getResources().getString(R.string.beginner);
            }
        } else {
            this.level = context.getResources().getString(R.string.beginner);
        }

        if (activity.hasAttribute("thematic")) {
            thematic = activity.getAttribute("thematic");

        } else {
            thematic = context.getResources().getString(R.string.default_lesson_thematic);
        }
        this.titleFr = activity.getElementsByTagName("title").item(0).getTextContent();
        if (activity.getElementsByTagName("title_zh").getLength() > 0)
            this.titleZh = activity.getElementsByTagName("title_zh").item(0).getTextContent();


        NodeList waveList = activity.getElementsByTagName("wave");
        for (int i = 0; i < waveList.getLength(); i++) {
            waves.add(new WaveUnit((Element) waveList.item(i)));
        }
    }

    public void initWave(ArrayList<WaveUnit> waveSet) {
        waves = waveSet;
    }

    public void addWave(WaveUnit w) {
        if (waves == null) waves = new ArrayList<>();
        waves.add(w);
    }

    /* ------------------------------------------------*/
    /* ----------------- Setters ----------------------*/
    /* ------------------------------------------------*/
    public void setActivityIndex(int actIndex) {
        this.activityIndex = actIndex;
    }

    public void setFilename(String filename) {
        this.fn = filename;
    }

    /* ------------------------------------------------*/
    /* ----------------- Getters ----------------------*/
    /* ------------------------------------------------*/
    public WaveUnit getWave(int index) {
        if (index >= 0 && index < this.waves.size()) {
            return this.waves.get(index);
        }
        return null;
    }

    public String getType() {
        return this.type;
    }

    public String getTitleFr() {
        return this.titleFr;
    }

    public String getTitleZh() {
        return this.titleZh;
    }

    public int getWaveCount() {
        return this.waves.size();
    }

    public int getActivityIndex() {
        return this.activityIndex;
    }

    public String getLevel() {
        return this.level;
    }

    public String getThematic() {
        return this.thematic;
    }

    public String getFilename() {
        return this.fn;
    }

    public String getLessonChooserFormatedData() {
        if (this.waves == null || this.waves.size() <= 0) return null;
        return String.format(Locale.ENGLISH,
                "%s,%s,%s,%s,%s",
                getTitleFr(), getTitleZh(), getFilename(), getLevel(), getThematic());
    }

}
