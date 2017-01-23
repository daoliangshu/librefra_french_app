package com.librefra.daoliangshu.librefra.daoliangboom;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by gigitintin on 04/07/16.
 * This interface is used to provide basic common methods for
 * handling the sprites
 */
public interface DrawableSpriteInterface {

    void onDraw(Canvas canvas);

    RectF getPositionRectF();

    boolean isCollide(Rect rect);

    void update();

}
