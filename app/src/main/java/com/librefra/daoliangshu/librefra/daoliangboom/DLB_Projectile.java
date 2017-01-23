package com.librefra.daoliangshu.librefra.daoliangboom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.librefra.daoliangshu.librefra.R;

import java.util.Random;

/**
 * Created by gigitintin on 04/07/16.
 * Projectile representation in game DLB
 */
public class DLB_Projectile implements DrawableSpriteInterface {
    public static final int STATE_ACTIVE = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_IDLE_DRAWABLE = 2;

    /*Projectiles available*/
    private static Bitmap projectiles[];
    private final static int PROJ_TYPE_COUNT = 4;
    private int projIndex = 0;
    private static Random rand;


    private Context myContext;
    private RectF rectF;
    private int state;

    private float xSpeed;
    private DisplayMetrics metrics;

    public DLB_Projectile(Context context, Rect posRect) {
        this(context, new RectF(posRect));
    }

    public DLB_Projectile(Context context, RectF posRect) {
        myContext = context;
        WindowManager wm = (WindowManager) myContext.getSystemService(Context.WINDOW_SERVICE);
        metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        rectF = posRect;
        if (projectiles == null) {
            Bitmap temp = BitmapFactory.decodeResource(myContext.getResources(),
                    R.drawable.projectiles_set1);
            temp = Bitmap.createScaledBitmap(temp, (int) posRect.width() * PROJ_TYPE_COUNT,
                    (int) posRect.height(), true);
            projectiles = new Bitmap[4];
            for (int i = 0; i < PROJ_TYPE_COUNT; i++) {
                projectiles[i] = Bitmap.createBitmap(temp,
                        (int) (i * posRect.width()), 0,
                        (int) posRect.width(),
                        (int) posRect.height());
            }
            rand = new Random();
        }

        state = STATE_IDLE;
    }

    public void onDraw(Canvas canvas) {
        update();
        if (state == STATE_ACTIVE || state == STATE_IDLE_DRAWABLE) {
            canvas.drawBitmap(projectiles[projIndex], rectF.left, rectF.top, null);
        }
    }

    /**
     * Not yet implemented -> refers to isCollideAt in view
     *
     * @param rect
     * @return
     */
    public boolean isCollide(Rect rect) {
        return false;
    }

    public void setPos(float x, float y) {
        rectF.offsetTo(x, y);
    }

    public void moveOffset(float dx, float dy) {
        rectF.offset(dx, dy);
    }

    public Rect getRect() {
        Rect r = new Rect();
        rectF.round(r);
        return r;
    }

    public RectF getPositionRectF() {
        return this.rectF;
    }

    public void setSpeed(float dx) {
        xSpeed = dx;
    }

    public void update() {
        if (state == STATE_ACTIVE || state == STATE_IDLE_DRAWABLE) {
            rectF.offset(xSpeed, 0);
            if (rectF.left >= metrics.widthPixels) {
                state = STATE_IDLE;
            }
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int STATE) {
        state = STATE;
        if (state == STATE_ACTIVE) {
            projIndex = Math.abs(rand.nextInt() % PROJ_TYPE_COUNT);
        }
    }

}
