package com.librefra.daoliangshu.librefra.daoliangboom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.librefra.daoliangshu.librefra.R;

/**
 * Created by gigitintin on 02/07/16.
 * Display the remaining life of the player on screen
 */
public class LifeInfoPanel {

    private RectF rect;
    private int ajustY;
    private Paint paint;
    private Bitmap heart_empty, heart_full, background;

    //Life control
    private int maxLife = 5;
    private int curLife = 4;


    public LifeInfoPanel(Context myContext, Rect posRect) {
        paint = new Paint();
        paint.setColor(Color.rgb(200, 255, 200));
        rect = new RectF(posRect);

        background = BitmapFactory.decodeResource(myContext.getResources(),
                R.drawable.scorebar1);
        background = Bitmap.createScaledBitmap(background, posRect.width(), posRect.height(), true);
        if (heart_empty == null) {
            heart_empty = BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.heart1);
            heart_empty = Bitmap.createScaledBitmap(heart_empty, posRect.height(), posRect.height(), true);
        }
        if (heart_full == null) {
            heart_full = BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.heart_full);
            heart_full = Bitmap.createScaledBitmap(heart_full, posRect.height(), posRect.height(), true);
        }

        ajustY = (int) rect.top + (int) ((rect.height() - this.heart_empty.getHeight()) / 2);
    }


    /**
     * @param value is the value to increment to current life
     * @return if current life + value is lower than 0, return false to signal that player lose
     */
    public boolean incLife(int value) {
        if ((curLife + value) < 0) {
            curLife = 0;
            return false;
        } else {
            curLife += value;
            return true;
        }
    }

    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(background, 0, rect.top, paint);
        /*Drawing Life*/
        for (int i = 0; i < maxLife; i++) {
            canvas.drawBitmap(curLife < i + 1 ? heart_empty : heart_full,
                    rect.height() * (i + 1),
                    rect.top,
                    paint);
        }
    }

    public void refill() {
        this.curLife = this.maxLife;
    }
}
