package com.librefra.daoliangshu.librefra.daoliangboom.service;

import android.graphics.Canvas;

import com.librefra.daoliangshu.librefra.daoliangboom.ChoiceBox;
import com.librefra.daoliangshu.librefra.daoliangboom.DLB_Projectile;

import java.util.ArrayList;

/**
 * Created by gigitintin on 05/07/16.
 * Drawer for the lesson view
 */
public class Drawer {
    /**
     * Draw the boxes representing a wave
     *
     * @param canvas
     * @param textBoxes
     * @return
     */
    public static void drawBoxes(Canvas canvas,
                                 ArrayList<?> textBoxes) {
        for (ChoiceBox t : (ArrayList<ChoiceBox>) textBoxes) {
            t.onDraw(canvas);
        }
    }

    public static void drawProjectiles(Canvas canvas, ArrayList<DLB_Projectile> projectiles) {
        for (DLB_Projectile p : projectiles) {
            p.onDraw(canvas);
        }
    }

}
