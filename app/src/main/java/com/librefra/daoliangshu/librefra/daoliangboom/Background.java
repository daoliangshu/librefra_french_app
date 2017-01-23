package com.librefra.daoliangshu.librefra.daoliangboom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

import com.librefra.daoliangshu.librefra.R;

import java.util.Random;

/**
 * Created by daoliangshu on 12/10/16.
 */

public class Background {
    private Bitmap fond;
    private Bitmap groundObjects;
    final private int groundObjtectCount = 3;
    private Rect bounds;


    /* Objects moving control */
    private int groundObjWidth;
    private int groundObjHeight;
    private int topGoundObject;
    private int[] spriteIndex = {0, 0, 0};
    private Random rand;
    private int indexRightMost = 0;

    private Rect objectInnerBound[];
    private RectF objectBounds[];
    private Context context;
    private Paint paint = new Paint();


    public Background(Context context, Rect rect) {
        bounds = rect;
        this.context = context;
        fond = BitmapFactory.decodeResource(context.getResources(), R.drawable.fond_sombre2);
        fond = Bitmap.createScaledBitmap(fond, rect.width(), rect.height(), false);

        groundObjects = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_objects);
        groundObjWidth = 2 * rect.height() / 6;
        groundObjHeight = 2 * rect.height() / 3;
        topGoundObject = bounds.bottom - groundObjHeight;
        groundObjects = Bitmap.createScaledBitmap(groundObjects,
                groundObjWidth * 3,
                groundObjHeight,
                true);
        Shader lg = new LinearGradient(0, 0, bounds.width() + 150, bounds.height() + 150,
                new int[]{Color.rgb(0, 0, 50), Color.argb(50, 255, 255, 255), Color.WHITE},
                new float[]{0, 0.4f, 1f}, Shader.TileMode.REPEAT);
        Shader lg2 = new LinearGradient(bounds.width() / 2, 0, bounds.width() / 2, bounds.height(),
                new int[]{Color.rgb(100, 0, 50), Color.argb(50, 255, 255, 255), Color.WHITE},
                new float[]{0, 0.5f, 1f}, Shader.TileMode.REPEAT);
        paint.setShader(lg2);
        Canvas ca = new Canvas(fond);
        ca.drawRect(0, 0, bounds.width(), bounds.height(), paint);
        paint.setShader(lg);
        ca.drawRect(0, 0, bounds.width(), bounds.height(), paint);
        indexRightMost = 0;
        int right = 0;
        int range = groundObjWidth;
        objectBounds = new RectF[3];
        rand = new Random();
        for (int i = 0; i < 3; i++) {
            int newLeft = right + Math.abs(rand.nextInt() % range);
            objectBounds[i] = new RectF(newLeft,
                    topGoundObject,
                    newLeft + groundObjWidth,
                    topGoundObject + groundObjHeight);
            right = (int) objectBounds[i].right;
            spriteIndex[i] = Math.abs(rand.nextInt() % groundObjtectCount);
            indexRightMost = i;
        }

        objectInnerBound = new Rect[3];
        for (int i = 0; i < 3; i++) {
            objectInnerBound[i] = new Rect(i * groundObjWidth,
                    0,
                    i * groundObjWidth + groundObjWidth,
                    groundObjHeight);
        }

    }

    public void OnDraw(Canvas canvas) {
        canvas.drawBitmap(fond, bounds.left, bounds.top, null);
        for (int i = 0; i < 3; i++) {
            if (objectBounds[i].left <= bounds.right) {
                canvas.drawBitmap(groundObjects,
                        objectInnerBound[spriteIndex[i]],
                        objectBounds[i],
                        null);
            }
        }
    }

    public void update() {
        for (int i = 0; i < 3; i++) {
            objectBounds[i].offset(DLB_Config.currentSpeed * 30, 0);
            if (objectBounds[i].right < 0) {
                int right = (int) objectBounds[indexRightMost].right;
                int range = groundObjWidth;

                int newLeft = right + Math.abs(rand.nextInt() % range);
                objectBounds[i].offsetTo(newLeft, topGoundObject);
                spriteIndex[i] = Math.abs(rand.nextInt() % groundObjtectCount);
                indexRightMost = i;
            }
        }
    }
}
