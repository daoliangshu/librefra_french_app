package com.librefra.daoliangshu.librefra.tools;

import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.librefra.daoliangshu.librefra.lf_lesson.LessonState;

/**
 * Created by daoliangshu on 07/08/16.
 * Methods to set different things
 */
public class Setter {

    /***
     * Set the content of the lesson,
     * word currently selected by user is highlighted
     *
     * @param tv
     */
    public static void setContent(TextView tv) {
        Spannable s = (Spannable) tv.getText();
        if (LessonState.prevSelectedWordStartIndex >= 0 &&
                LessonState.prevSelectedWordEndIndex >= 0 &&
                LessonState.prevSelectedWordEndIndex > LessonState.prevSelectedWordStartIndex &&
                LessonState.prevSelectedWordEndIndex < s.length() &&
                LessonState.prevSelectedWordStartIndex < s.length()) {

            s.setSpan(new ForegroundColorSpan(Color.WHITE),
                    LessonState.prevSelectedWordStartIndex,
                    LessonState.prevSelectedWordEndIndex,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        s.setSpan(new ForegroundColorSpan(Color.BLUE),
                LessonState.curSelectedWordStartIndex,
                LessonState.curSelectedWordEndIndex,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }
}
