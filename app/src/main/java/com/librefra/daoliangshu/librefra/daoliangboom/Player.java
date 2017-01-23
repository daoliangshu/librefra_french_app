package com.librefra.daoliangshu.librefra.daoliangboom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;

import com.librefra.daoliangshu.librefra.R;

/**
 * Created by gigitintin on 01/07/16.
 */
public class Player implements DrawableSpriteInterface {
    private RectF positionRect;

    private float gravity = 0.20f;
    private float ySpeed = 0;
    private SurfaceView gameView;


    //Sprite control
    private Bitmap sprites[];
    private int spriteIndex = 0;
    private final int ANIM_ACTIVE_DELAY = 14;
    private boolean state = false;
    private int delayCount = 0;
    private float moveUnitY = 1.0f;

    //displacement range border
    private int top;
    private int bottom;
    private int middle;

    public Player(SurfaceView view, Rect bounds, float moveUnitY) {
        this.top = DLB_Config.gameTop;
        this.bottom = DLB_Config.gameBottom;
        this.middle = this.top + (this.bottom - this.top) / 2;
        this.gameView = view;
        this.positionRect = new RectF(bounds);
        this.moveUnitY = moveUnitY;
        loadRes(view.getContext());
    }

    private void loadRes(Context context) {
        sprites = new Bitmap[2];
        sprites[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.main_char2_active);
        sprites[0] = Bitmap.createScaledBitmap(sprites[0],
                (int) (positionRect.width()),
                (int) (positionRect.height()),
                true);
        sprites[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.main_char2);
        sprites[1] = Bitmap.createScaledBitmap(sprites[1],
                (int) (positionRect.width()),
                (int) (positionRect.height()),
                true);
    }

    public void update() {
        if (positionRect.centerY() > middle - 4 && positionRect.centerY() < middle + 4 && Math.abs(ySpeed) < 0.25f) {
            ySpeed = 0f;
            positionRect.top = middle - sprites[0].getHeight() / 2;
            positionRect.bottom = middle + sprites[0].getHeight() / 2;
        } else {
            if (positionRect.top < this.top) {
                ySpeed = 0;
                positionRect.offsetTo(positionRect.left, this.top);
            } else if (positionRect.bottom > bottom) {
                ySpeed = 0;
                positionRect.offsetTo(positionRect.left, this.bottom - positionRect.height());
            } else {
                if (positionRect.centerY() < middle - 10) {
                    ySpeed += gravity;
                    if (ySpeed < 0) ySpeed /= 1.2f;
                } else if (positionRect.centerY() > middle + 10) {
                    ySpeed -= gravity;
                    if (ySpeed > 0) ySpeed /= 1.2f;
                }
                positionRect.offset(0, moveUnitY * ySpeed);
            }
        }
        if (delayCount > 0) {
            --delayCount;
            if (delayCount == 0) {
                spriteIndex = 0;
                state = false;
            }
        }
    }

    public void onDraw(Canvas canvas) {
        update();
        canvas.drawBitmap(sprites[spriteIndex], positionRect.left, positionRect.top, null);
    }

    public void setSpeedY(int dy) {
        ySpeed += dy;
        update();
    }

    public RectF getPositionRectF() {
        return this.positionRect;
    }

    public boolean isCollide(Rect r) {
        boolean tmp_res = false;
        if (positionRect.left < r.left & positionRect.right > r.left) {
            tmp_res = true;
        } else if (positionRect.left > r.left - 5 && positionRect.left < r.right + 5) {
            tmp_res = true;
        }
        if (tmp_res) {
            if (positionRect.top < r.top && positionRect.bottom > r.top) {
                return true;
            } else if (positionRect.top > r.top && positionRect.top < r.bottom) {
                return true;
            }
        }
        return false;
    }

    public boolean getState() {
        return this.state;
    }

    /**
     * Signal for launching sprite active animation
     */
    public void triggerActive() {
        spriteIndex = 1;
        delayCount = ANIM_ACTIVE_DELAY;
        state = true;
    }


}
