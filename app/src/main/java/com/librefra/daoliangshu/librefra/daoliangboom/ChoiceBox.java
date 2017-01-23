package com.librefra.daoliangshu.librefra.daoliangboom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;

import com.librefra.daoliangshu.librefra.R;

/**
 * Created by gigitintin on 02/07/16.
 * This class to provide a simple way to create a box including text
 * It is used for learning games
 */
public class ChoiceBox {
    private int waveType = DLB_Config.WAVE_TYPE_MULTCHOICE;
    private int transparency = 255;
    private int waveDisapearingFactor = 1;
    private boolean isVanishing = false;
    private int index;
    private int choiceIndex;
    private int spriteIndex = 0;
    private static int[] colors;
    private float moveUnitX = 1.0f;

    /*Status */
    private boolean isDrawable;
    private boolean isCollide;
    private boolean isEnded;
    private boolean isShootable = true;
    private boolean isCorrect;
    private int ptrWord = 0;
    private Paint paint;
    private Paint paint2;
    private TextPaint mTextPaint;
    private StaticLayout mTextLayout;
    private int order = 0;

    /*-------Resources---------*/
    public static Bitmap[] headCase;

    private RectF default_rect; // Store the default position to put ( when reenabling the box)
    private RectF rect;
    private TirAChoixView view;
    private Bitmap bmp;
    private String text;
    private String text_end; //  before box to disappear

    public ChoiceBox(TirAChoixView gameView, Bitmap b, String text, int index, int waveIndex) {
        this(gameView, b, text, index, waveIndex, null);
    }

    public ChoiceBox(TirAChoixView gameView, Bitmap b, String text,
                     int index, int waveIndex, String text_end) {
        if (colors == null) {
            colors = new int[]{
                    gameView.getContext().getResources().getColor(R.color.midDarkGreen),
                    gameView.getContext().getResources().getColor(R.color.midDarkRed),
                    gameView.getContext().getResources().getColor(R.color.lightCoral),
                    gameView.getContext().getResources().getColor(R.color.plum),
                    gameView.getContext().getResources().getColor(R.color.midDarkOrange),
                    gameView.getContext().getResources().getColor(R.color.yellowGreen)
            };
        }
        waveDisapearingFactor = 1;
        view = gameView;
        bmp = b;
        this.index = index;
        setText(text);
        this.text_end = text_end;
        isCorrect = false;
        paint = new Paint();


        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setShadowLayer(1, 0, 0, Color.BLACK);
        mTextPaint.setAntiAlias(true);

        mTextPaint.setTextSize(32);
    }


    public static void init(View view, int x, int y) {
        headCase = new Bitmap[2];
        headCase[0] = BitmapFactory.decodeResource(view.getResources(), R.drawable.box_head_1);
        headCase[0] = Bitmap.createScaledBitmap(headCase[0], x, y, false);
        headCase[1] = BitmapFactory.decodeResource(view.getResources(), R.drawable.box_head_2);
        headCase[1] = Bitmap.createScaledBitmap(headCase[1], x, y, false);

    }

    public String getText() {
        return text;
    }


    /**
     * Get the next word in the text box
     *
     * @return
     */
    public String getNextWord() {
        String res = null;
        if (text.length() > 0) {
            String str[] = text.split(" |\\.|,|'");
            try {
                res = str[ptrWord];
            } catch (Exception e) {
                e.printStackTrace();
                if (res == null) res = text;
                else res = str[0];
                ptrWord = 0;
            }
            ptrWord = (ptrWord + 1) % str.length;
            return res;
        }
        return text;
    }


    public void setRect(Rect r) {
        default_rect = new RectF(r);
        rect = new RectF(r);
        this.setText(this.text);
        //Shader lg = new LinearGradient(0, 0, r.width(), r.height(),
        //        new int[]{Color.argb(100, 0,0,50), Color.argb(20, 255,255,255), Color.argb(50, 0,0,0)},
        //        new float[]{0.25f,0.5f,0.75f}, Shader.TileMode.MIRROR);
        Shader lg2 = new LinearGradient(r.width() / 2, 0, r.width() / 2, r.height(),
                new int[]{Color.argb(175, 0, 0, 0), Color.argb(50, 255, 255, 255), Color.argb(175, 0, 0, 0)},
                new float[]{0.0f, 0.25f, 0.75f}, Shader.TileMode.REPEAT);
        paint2 = new Paint();
        paint2.setStrokeWidth(6);
        paint2.setShader(lg2);
    }

    public void setChoiceIndex(int choiceIndex) {
        this.choiceIndex = choiceIndex;
    }

    public void setWaveCorrectness(boolean isCorrect) {
        if (!isCorrect) waveDisapearingFactor = -1;
    }

    public boolean isCollide(Rect r) {
        return false;
    }

    public void setCorrectness(boolean c) {
        isCorrect = c;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void onDraw(Canvas canvas) {
        if (isDrawable) {
            paint.setColor(Color.BLACK);
            paint.setTextSize(32);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAlpha(transparency);
            if (isCollide) {
                if (isCorrect) {
                    if (waveType == DLB_Config.WAVE_TYPE_MULTCHOICE) {
                        paint.setColor(colors[0]);
                    } else {
                        switch (order) {
                            case 0:
                                paint.setColor(colors[2]);
                                break;
                            case 1:
                                paint.setColor(colors[3]);
                                break;
                            case 2:
                                paint.setColor(colors[4]);
                                break;
                            case 3:
                                paint.setColor(colors[5]);
                                break;
                        }
                    }

                } else paint.setColor(colors[1]);
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(6);
                canvas.drawRoundRect(rect, rect.width() / 6, rect.height() / 6, paint);
                canvas.drawRoundRect(rect, rect.width() / 6, rect.height() / 6, paint2);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawRoundRect(rect, rect.width() / 6, rect.height() / 6, paint);
            } else if (headCase != null) {
                canvas.drawBitmap(bmp, rect.left, rect.top, paint);

            }
            canvas.drawBitmap(headCase[spriteIndex], rect.left - headCase[spriteIndex].getWidth(), rect.top, paint);
            paint.setColor(Color.BLACK);
            mTextPaint.setAlpha(transparency);
            canvas.save();
            canvas.translate(rect.left, rect.top + (rect.height() - mTextLayout.getHeight()) / 2);
            mTextLayout.draw(canvas);
            canvas.restore();
        }
    }

    public void update(float increment) {
        if (isVanishing) {
            /* The boxes are currently disappearing from screen */
            transparency = transparency - 3;
            if (transparency < 0) {
                transparency = 0;
                isEnded = true;
            }
            rect.offset(moveUnitX * waveDisapearingFactor * 15, 0);
        } else if (rect.right >= DLB_Config.width + 20) {
            if (rect.left < DLB_Config.width) isShootable = true;
            rect.offset(moveUnitX * -30, 0);
        } else {
            rect.offset(moveUnitX * increment, 0);
        }

        if (rect.right <= 0) isEnded = true;
    }

    public boolean getIsShootable() {
        return isShootable;
    }


    /**
     * @return the index of the box amongst the initUnit of boxes
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * @return weither the box has finished its path
     */
    public boolean getIsEnded() {
        return this.isEnded;
    }

    public int getType() {
        return this.waveType;
    }

    public void setText(String newText) {
        ptrWord = 0;
        text = newText;
        isDrawable = true;
        // * is the symbol used to mark a empty choice
        if (rect == null) return;
        if (newText.trim().equals("*")) {
            isDrawable = false;
            isShootable = false;
        }
        mTextLayout = new StaticLayout(text, mTextPaint,
                (int) rect.width(), Layout.Alignment.ALIGN_CENTER,
                1.0f, 0.0f, false);
    }

    /**
     * Note that it re-initializes the box to it original position
     */
    public void setBoxHorizontalAxis() {
        waveDisapearingFactor = 1;
        rect = new RectF(default_rect); // retrieve the last original position
        this.isEnded = false;
        this.isVanishing = false;
        this.isShootable = true;
        this.transparency = 255;
        ptrWord = 0;
        spriteIndex = 0;
    }

    public void setVanishing(boolean state) {
        isVanishing = state;
    }

    public RectF getRectF() {
        return this.rect;
    }

    public int getChoiceIndex() {
        return this.choiceIndex;
    }

    public int getOrder() {
        if (waveType != DLB_Config.WAVE_TYPE_ORDER) return -1;
        return this.order;
    }

    /**
     * When state is true, it displays a red/green rect ( if it collide the wrong/right object)
     *
     * @param state
     */
    public void setCollide(boolean state) {
        this.isCollide = state;
        if (state && isCorrect()) spriteIndex = 1;
        if (state) this.isShootable = false;
    }

    public void setIsShootable(boolean b) {
        this.isShootable = b;
    }

    public void setSpriteIndex(int spriteIndex) {
        this.spriteIndex = spriteIndex;
        if (spriteIndex > 1) this.spriteIndex = 1;
    }

    public void setType(int waveType) {
        this.waveType = waveType;
    }

    public void setType(String waveType) {
        if (waveType.equals("order")) {
            this.waveType = DLB_Config.WAVE_TYPE_ORDER;
        } else {
            this.waveType = DLB_Config.WAVE_TYPE_MULTCHOICE;
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setMoveUnitX(float moveUnitX) {
        /* Scale the movement according to screen */
        this.moveUnitX = moveUnitX;
    }
}
