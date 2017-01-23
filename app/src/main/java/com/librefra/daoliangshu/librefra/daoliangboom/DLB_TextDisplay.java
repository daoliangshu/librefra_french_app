package com.librefra.daoliangshu.librefra.daoliangboom;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * Created by daoliangshu on 2016/9/22.
 * Represents the rectanglem displaying the question in
 * the DLB game.
 */
public class DLB_TextDisplay {
    private Rect rect;
    private Paint paint;
    private String curText = "";
    private StaticLayout mTextLayout;

    public DLB_TextDisplay(Rect rect, String str) {
        this.paint = new Paint();
        this.rect = rect;
        this.curText = str;
        setText(str);
    }

    public void onDraw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(32);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);
        RectF rf = new RectF(rect);
        if (mTextLayout.getHeight() > rect.height()) rf.bottom = rf.top + mTextLayout.getHeight();
        canvas.drawRoundRect(rf, rect.width() / 6, rect.height() / 6, paint);

        canvas.save();
        canvas.translate(rect.left, rect.top + (rf.height() - mTextLayout.getHeight()) / 2);
        mTextLayout.draw(canvas);
        canvas.restore();
    }

    public void setText(String text) {
        this.curText = text;
        TextPaint mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setShadowLayer(1, 0, 0, Color.BLACK);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(32);
        mTextLayout = new StaticLayout(text, mTextPaint,
                rect.width(), Layout.Alignment.ALIGN_CENTER,
                1.0f, 0.0f, false);
    }

}
