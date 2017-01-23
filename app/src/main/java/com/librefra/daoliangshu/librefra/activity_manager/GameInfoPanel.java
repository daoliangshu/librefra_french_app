package com.librefra.daoliangshu.librefra.activity_manager;

import android.content.Context;
import android.widget.TextView;

import com.librefra.daoliangshu.librefra.main.DBHelper;
import com.librefra.daoliangshu.librefra.tools.FraUtils;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by daoliangshu on 11/26/16.
 * Controls elements of the top information panel
 */

public class GameInfoPanel {
    private TextView currentWord = null;
    private TextView currentTrans = null;
    private TextView currentTime = null;
    private TextView currentScore = null;
    private Context context;
    private int timeValue = 0;

    /*------------------------------------------------*/
    /*------------------CONSTRUCTOR-------------------*/
    /*------------------------------------------------*/
    public GameInfoPanel(
            Context context,
            TextView wordView,
            TextView transView,
            TextView timeView,
            TextView scoreView) {
        this.context = context;
        this.currentScore = scoreView;
        this.currentTime = timeView;
        this.currentTrans = transView;
        this.currentWord = wordView;

        this.currentScore.setText("0");
    }

    /*------------------------------------------------*/
    /*------------------SETTERS-----------------------*/
    /*------------------------------------------------*/
    public void setWord(String newWord) {
        this.currentWord.setText(newWord);
        this.currentWord.postInvalidate();
    }

    public void setWord(String newWord, boolean fetchTrans) {
        if (fetchTrans && newWord.trim().length() > 0) {
            String content;
            HashMap<String, String> trans;
            trans = DBHelper.getInstance(context).getWordTrans(newWord.trim(), DBHelper.SUBST, null);
            content = "";
            if (trans != null) {
                for (int i = 0; i < 3; i++) {
                    if (trans.containsKey("trans" + i)) {
                        content += trans.get("trans" + i);
                    }
                }
                this.setTrans(content);
                if (trans.containsKey("genre")) {
                    String w = newWord.trim();
                    this.setWord(FraUtils.getDefiniteArticle(Integer.parseInt(trans.get("genre")),
                            FraUtils.isVoyel(w.charAt(0))) + " " + w);
                }
            } else {
                this.setTrans("");
                this.setWord(newWord);
            }
        } else {
            setWord(newWord);
        }
    }

    public void setTrans(String newTrans) {
        this.currentTrans.setText(newTrans);
        this.currentTrans.postInvalidate();
    }

    public void setTimer(int newValue) {
        this.timeValue = newValue;
        int minutes = (newValue % 3600) / 60;
        int seconds = newValue % 60;
        String timeString =
                String.format(Locale.US, "%02d:%02d", minutes, seconds);
        this.currentTime.setText(timeString);
    }

    public void setScore(int score) {
        this.currentScore.setText(String.valueOf(score));
    }

    public void incrementScore(int inc) {
        int score = Integer.parseInt(this.currentScore.getText().toString());
        score += inc;
        this.currentScore.setText(String.valueOf(score));
    }

    public void incrementTimer() {
        this.timeValue += 1;
        int minutes = (this.timeValue % 3600) / 60;
        int seconds = this.timeValue % 60;
        String timeString =
                String.format(Locale.US, "%02d:%02d", minutes, seconds);
        this.currentTime.setText(timeString);
    }


    /*------------------------------------------------*/
    /*-------------------GETTERS----------------------*/
    /*------------------------------------------------*/
    public int getScore() {
        return Integer.parseInt(this.currentScore.getText().toString());
    }
}
