package com.librefra.daoliangshu.librefra.activity_manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.daoliangboom.TirAChoix_GameActivity;
import com.librefra.daoliangshu.librefra.lf_lesson.WaveSet;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by daoliangshu on 2016/10/20.
 * THis is the Linear Layout which represents a activity in the lesson
 */


public class ActivityDisplayedEntry extends RelativeLayout {
    private String fn;
    private int activityIndex = 0;

    TextView lbTitleFr;
    TextView tvTitleFr;

    TextView lbTitleZh;
    TextView tvTitleZh;

    TextView tvActivityType;

    TextView lbWaveCount;
    TextView tvWaveCount;
    TextView tvThematic;
    TextView tvLevel;
    TextView tvContentType; // default:Nope, possible choice: Grammar, Vocabular, Mixed

    public ActivityDisplayedEntry(Context c) {
        super(c);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.activity_entry_element, this, true);

        GradientDrawable border = new GradientDrawable();
        border.setColor(ContextCompat.getColor(c, R.color.darkBlue));
        border.setStroke(1, ContextCompat.getColor(c, R.color.lightGreen));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackgroundDrawable(border);
        } else {
            this.setBackground(border);
        }

        lbTitleFr = (TextView) findViewById(R.id.lbTitleFr);
        tvTitleFr = (TextView) findViewById(R.id.tvTitleFr);
        lbTitleZh = (TextView) findViewById(R.id.lbTitleZh);
        tvTitleZh = (TextView) findViewById(R.id.tvTitleZh);
        lbWaveCount = (TextView) findViewById(R.id.lbWaveCount);
        tvWaveCount = (TextView) findViewById(R.id.tvWaveCount);
        tvThematic = (TextView) findViewById(R.id.tvThematic);
        tvThematic.setBackgroundColor(Color.rgb(11, 11, 111));
        tvActivityType = (TextView) findViewById(R.id.tvActivityType);
        tvLevel = (TextView) findViewById(R.id.tvLevel);
        /*
        GradientDrawable border = new GradientDrawable();
        border.setColor(ContextCompat.getColor(c, R.color.darkBlue));
        border.setStroke(1, ContextCompat.getColor(c, R.color.lightGreen));
        if(Build.VERSION.SDK_INT < 16){
            this.setBackgroundDrawable(border);
        }else{
            this.setBackground(border);
        }*/


        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activIntent = new Intent(getContext(), TirAChoix_GameActivity.class);
                activIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activIntent.putExtra("lesson", fn);
                activIntent.putExtra("activityIndex", activityIndex);
                getContext().startActivity(activIntent);
            }
        });
        lbTitleFr.setText("Titre:");
        lbTitleZh.setText("Titre:");
        lbWaveCount.setText("#:");
        tvWaveCount = new TextView(c);
        tvWaveCount.setText("0");
        tvActivityType.setText("QCM_test");
    }


    /*------------------------SETTERS-----------------------------*/
    public void setWaveCount(int waveCount) {
        this.tvWaveCount.setText(String.format(Locale.ENGLISH, "%d", waveCount));
    }

    public void setTitleFr(String title) {
        this.tvTitleFr.setText(title);
    }

    public void setTitleZh(String title) {
        this.tvTitleZh.setText(title);
    }

    public void setFilename(String filename) {
        this.fn = filename;
    }

    public void setLevel(String level) {
        this.tvLevel.setText(level);
    }

    public void setThematic(String theme) {
        this.tvThematic.setText(theme);
    }

    public void set(HashMap<String, String> actInfo) {
        if (actInfo.get("titleFr") == null) setTitleFr("Pas de titre");
        else setTitleFr(actInfo.get("titleFr"));
        if (actInfo.get("titleZh") == null) setTitleZh("Pas de titre2");
        else setTitleZh(actInfo.get("titleZh"));
        setFilename(actInfo.get("filename"));

    }

    public void setActivityIndex(int actIndex) {
        this.activityIndex = actIndex;
    }

    public void set(WaveSet ws) {
        setTitleZh(ws.getTitleZh());
        setTitleFr(ws.getTitleFr());
        setFilename(ws.getFilename());
        setActivityIndex(ws.getActivityIndex());
        setThematic(ws.getThematic());
        setLevel(ws.getLevel());
        setWaveCount(ws.getWaveCount());

    }

    /*------------------------GETTERS-----------------------------*/
    public String getTitleFr() {
        return this.tvTitleFr.getText().toString();
    }

    public String getTitleZh() {
        return this.tvTitleZh.getText().toString();
    }

    public int getActivityIndex() {
        return this.activityIndex;
    }

    public String getFilename() {
        return this.fn;
    }

}
