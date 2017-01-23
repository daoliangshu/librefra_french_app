package com.librefra.daoliangshu.librefra.daoliangboom;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by gigitintin on 01/07/16.
 * Representation of interface button in DLB,
 * this button is draw inside the game view canvas
 */
public class DLB_SimpleButton {
    Bitmap btm;
    Rect rect;

    public DLB_SimpleButton(Bitmap b) {
        btm = b;
        rect = new Rect();
        rect.set(0, 0, 30, 30);
    }

    public void setRect(Rect r) {
        rect = r;
    }

    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(btm, rect.left, rect.top, null);
    }

    public void setImage(Bitmap img) {
        Bitmap b = Bitmap.createScaledBitmap(img, rect.width(), rect.height(), true);
        btm = b;
    }

    public Rect getRect() {
        return rect;
    }

}
