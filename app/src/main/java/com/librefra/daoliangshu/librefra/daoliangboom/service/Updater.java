package com.librefra.daoliangshu.librefra.daoliangboom.service;

import android.util.Log;

import com.librefra.daoliangshu.librefra.daoliangboom.ChoiceBox;
import com.librefra.daoliangshu.librefra.daoliangboom.DLB_Config;
import com.librefra.daoliangshu.librefra.lf_lesson.WaveSet;
import com.librefra.daoliangshu.librefra.lf_lesson.WaveUnit;

import java.util.ArrayList;

/**
 * Created by gigitintin on 06/08/16.
 * Methods to update different things
 */
public class Updater {

    public static void disableWaveColision(ArrayList<ChoiceBox> textBoxes, boolean isCorrect) {
        for (ChoiceBox tc : textBoxes) {
            tc.setIsShootable(false);
            tc.setWaveCorrectness(isCorrect);
        }
    }


    public static boolean updateBoxes(ArrayList<?> textBoxes,
                                      WaveSet items, int curWaveIndex,
                                      boolean clearWave) {
        Boolean res = false;
        int waveIndex = curWaveIndex;
        float speed = (float) (DLB_Config.currentSpeed *
                Math.sqrt(items.getWave(curWaveIndex).getSpeed()));
        for (ChoiceBox t : (ArrayList<ChoiceBox>) textBoxes) {
            t.update(speed);
            res = t.getIsEnded();
            if (clearWave) {
                t.setVanishing(true);
            }
            if (res) {
                return true;
            }
        }
        return false;
    }

    public static void assignNextWave(int newWaveIndex,
                                      WaveUnit newWave,
                                      ArrayList<Integer> newChoiceMap,
                                      ArrayList<ChoiceBox> boxes) {
        for (ChoiceBox t : boxes) {
            t.setBoxHorizontalAxis();
            t.setCollide(false);
            t.setText(newWave.getTextAt(newChoiceMap.get(t.getIndex())));
            t.setType(newWave.getType());
            t.setChoiceIndex(newChoiceMap.get(t.getIndex()));
            if (newWave.getType() == DLB_Config.WAVE_TYPE_ORDER) {
                Log.e("SET_ORDER", "tb with index : " +
                        t.getIndex() +
                        " mapped " +
                        newChoiceMap.get(t.getIndex()) + " is order : " +
                        newWave.getOrderAt(newChoiceMap.get(t.getIndex()))
                );
                t.setOrder(newWave.getOrderAt(newChoiceMap.get(t.getIndex())));
            } else {

                t.setCorrectness(newWave.isCorrectAt(newChoiceMap.get(t.getIndex())));
            }
        }

    }

}
