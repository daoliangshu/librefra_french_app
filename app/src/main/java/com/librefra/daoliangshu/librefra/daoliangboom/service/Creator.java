package com.librefra.daoliangshu.librefra.daoliangboom.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.daoliangboom.ChoiceBox;
import com.librefra.daoliangshu.librefra.daoliangboom.DLB_Config;
import com.librefra.daoliangshu.librefra.daoliangboom.TirAChoixView;
import com.librefra.daoliangshu.librefra.lf_lesson.WaveUnit;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by gigitintin on 05/08/16.
 * Methods for creating diferent things
 */
public class Creator {

    public static ArrayList<Integer> getRandomOrder(int range, int size) {
        ArrayList<Integer> a = new ArrayList<>();
        Random rand = new Random();
        int tmp = rand.nextInt(range);
        a.add(tmp);
        if (range < size) {
            Log.e("ERR", "Required Size larger than available range");
            return null;
        }
        while (a.size() < size) {
            tmp = rand.nextInt(range);
            if (a.contains(tmp) == false) {
                a.add(tmp);
            }
        }
        return a;
    }

    static public ArrayList<ChoiceBox> createTextBoxes(TirAChoixView v,
                                                       int boxCount,
                                                       WaveUnit wave,
                                                       ArrayList<Integer> choiceMap,
                                                       float moveUnitX) {
        if (boxCount < 0 || boxCount > 6) return null;
        ArrayList<ChoiceBox> textBoxes = new ArrayList<>();
        int boxH = 0, boxW = 0;
        if (DLB_Config.height > DLB_Config.width) {
            boxH = (int) (v.getHeight() * (DLB_Config.GAME_PANEL_RATIO) / boxCount);
            boxW = DLB_Config.width / 3;
        }
        ChoiceBox.init(v, boxW / 5, boxH);
        Bitmap tmp = BitmapFactory.decodeResource(v.myContext.getResources(), R.drawable.box2);
        tmp = Bitmap.createScaledBitmap(tmp, boxW, boxH, true);
        int startX, startY;
        for (int i = 0; i < boxCount; i++) {
            startX = DLB_Config.width;
            startY = DLB_Config.gameTop + boxH * i;
            textBoxes.add(new ChoiceBox(v.self,
                    tmp,
                    wave == null ? " none " : wave.getTextAt(i),
                    i,
                    0));
            int choiceIndex = choiceMap.get(i);
            textBoxes.get(i).setMoveUnitX(moveUnitX);
            textBoxes.get(i).setRect(new Rect(startX, startY, startX + boxW, startY + boxH));
            textBoxes.get(i).setCorrectness(wave.getCorrectness(choiceIndex));
            textBoxes.get(i).setChoiceIndex(choiceIndex);
        }
        return textBoxes;
    }

}
