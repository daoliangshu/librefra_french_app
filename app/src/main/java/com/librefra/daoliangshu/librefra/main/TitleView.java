package com.librefra.daoliangshu.librefra.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.librefra.daoliangshu.librefra.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by gigitintin on 01/04/16.
 * Title view of the app.
 */
public class TitleView extends SurfaceView implements SurfaceHolder.Callback {
    /*Class variables */
    //Draw tools
    Paint p;
    //Screen
    int scrW, scrH;
    private Rect scrRect;
    private float scale;
    //Buttons:
    private Bitmap[] b1;
    private int b1x, b1y;
    private Rect b1Dst;

    //Buttons
    private Paint pb1Text, pb2Text;

    private TitleThread thread;
    public Context myContext;

    //Images:
    private Bitmap img1;
    private Bitmap flake;
    private int img1x, img1y;
    private String btn_startText = "";

    //States:
    boolean b1IsDown;

    private ArrayList<SubVector> flakePos;
    /*end Class variables*/


    public TitleView(Context context, AttributeSet attrs) {
        super(context);
        myContext = context;
        p = new Paint();
        pb1Text = new Paint();
        pb2Text = new Paint();
        b1 = new Bitmap[2];
        b1[0] = BitmapFactory.decodeResource(getResources(),
                R.drawable.boutton1_herbe);
        b1[1] = BitmapFactory.decodeResource(getResources(),
                R.drawable.boutton2_herbe);
        b1IsDown = false;
        img1 = BitmapFactory.decodeResource(getResources(), R.drawable.librefra_logo_big);
        flake = BitmapFactory.decodeResource(getResources(), R.drawable.faucon1);

        scale = context.getResources().getDisplayMetrics().density;
        initText();

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        thread = new TitleView.TitleThread(holder, getContext(),
                new Handler() {
                    @Override
                    public void handleMessage(Message m) {
                        ;
                    }
                });
        setFocusable(true);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scrW = w;
        scrH = h;
        b1[0] = Bitmap.createScaledBitmap(b1[0], (int) (scrW * 0.8), (int) (scrH * 0.15), false);
        b1[1] = Bitmap.createScaledBitmap(b1[1], (int) (scrW * 0.8), (int) (scrH * 0.15), false);
        if (scrW >= 3 * scrH / 2) {
            img1 = Bitmap.createScaledBitmap(
                    img1,
                    (3 * scrH / 2),
                    (3 * scrH / 2),
                    false);
        } else {
            img1 = Bitmap.createScaledBitmap(
                    img1,
                    scrW,
                    scrW,
                    false);
        }

        flake = Bitmap.createScaledBitmap(flake, scrW / 30, scrW / 30, false);
        scrRect = new Rect(0, 0, scrW, scrH);
        initFlakes();

        b1x = (int) (scrW * 0.1);
        b1y = (int) (scrH - scrH * 0.20);

        img1x = (scrW - img1.getWidth()) / 2;
        img1y = 0;

        b1Dst = new Rect(b1x, b1y, b1x + b1[0].getWidth(), b1y + b1[0].getHeight());


    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (myContext == null) {
            Log.e("ERR", "myContext is null in view ...");
        }
        getThread().mySurfaceHolder = holder;
    }

    public TitleThread getThread() {
        return this.thread;
    }

    /*
    * Thread
    */
    class TitleThread extends Thread {
        private static final int FRAME_DELAY = 25;
        private long mLastTime;
        private SurfaceHolder mySurfaceHolder;
        private Canvas canvas;
        private boolean running = true;
        private boolean thread_running = true;

        private TitleThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            mySurfaceHolder = surfaceHolder;
            myContext = context;
        }

        @Override
        public void run() {
            while (thread_running) {
                //Delay control
                long now = System.currentTimeMillis();
                long delay = FRAME_DELAY + mLastTime - now;
                if (delay > 0) try {
                    sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mLastTime = now;
                canvas = null;
                try {
                    if (running && mySurfaceHolder.getSurface().isValid()) {
                        canvas = mySurfaceHolder.lockCanvas(null);
                        if (canvas != null && running) {
                            synchronized (mySurfaceHolder) {
                                if (running && canvas != null) {
                                    draw(canvas);
                                    update();
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (canvas != null) {
                        try {
                            mySurfaceHolder.unlockCanvasAndPost(canvas);
                        } catch (IllegalStateException ise) {
                            Log.e("ERR", " failed to unlock canvas");
                        }
                    }
                }
            }
        }

        private void draw(Canvas canvas) {
            p.setColor(Color.rgb(0, 0, 0));
            p.setStyle(Paint.Style.FILL);
            canvas.drawRect(scrRect, p);

            if (b1IsDown) {
                pb1Text.setColor(Color.BLUE);
                canvas.drawBitmap(b1[1], b1x, b1y, null);
            } else {
                pb1Text.setColor(Color.BLACK);
                canvas.drawBitmap(b1[0], b1x, b1y, null);
            }

            canvas.drawBitmap(img1, img1x, img1y, null);

            canvas.drawText(btn_startText,
                    b1Dst.left + b1Dst.width() / 2,
                    b1Dst.top + (b1Dst.height() + pb1Text.getTextSize()) / 2,
                    pb1Text);
            canvas.drawText(btn_startText,
                    b1Dst.left + b1Dst.width() / 2,
                    b1Dst.top + (b1Dst.height() + pb1Text.getTextSize()) / 2,
                    pb2Text);

            try {
                for (SubVector sv : flakePos) {
                    canvas.drawBitmap(flake, sv.x, sv.y, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void update() {
            for (SubVector sv : flakePos) {
                sv.y += 0.70f;
                if (sv.y > scrH) {
                    Random r = new Random();
                    sv.x = r.nextInt() % scrW;
                    sv.y = -r.nextInt() % scrH - flake.getHeight();
                }
            }
        }

        public void leaveThread() {
            thread_running = false;
        }

        public void setRunning(boolean b) {
            running = b;
        }
    }


    public void initFlakes() {
        Random r = new Random();
        flakePos = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            flakePos.add(new SubVector(r.nextInt() % scrW, r.nextInt() % scrH));
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        } else {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.setRunning(false);
    }


    @Override
    protected void onDraw(Canvas canvas) {


    }

    private void initText() {
        pb1Text.setAntiAlias(true);
        pb1Text.setColor(Color.WHITE);
        pb1Text.setStyle(Paint.Style.FILL);
        pb1Text.setTextAlign(Paint.Align.CENTER);
        pb1Text.setTextSize(scale * 30);
        this.btn_startText = getResources().getString(R.string.btn_begin);

        pb2Text.setAntiAlias(true);
        pb2Text.setColor(Color.BLACK);
        pb2Text.setStyle(Paint.Style.STROKE);
        pb2Text.setTextAlign(Paint.Align.CENTER);
        pb2Text.setTextSize(scale * 30);

    }


    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();
        int X = (int) event.getX();
        int Y = (int) event.getY();

        switch (eventaction) {
            case MotionEvent.ACTION_MOVE:
                if (isRect(b1Dst, X, Y) == false) b1IsDown = false;
                break;
            case MotionEvent.ACTION_DOWN:
                if (isRect(b1Dst, X, Y) == true) b1IsDown = true;
                else b1IsDown = false;
                break;
            case MotionEvent.ACTION_UP:

                if (isRect(b1Dst, X, Y) == true) {
                    if (b1IsDown == true) {
                        Intent selectionIntent = new Intent(myContext, MainMenuSelectionActivity.class);
                        myContext.startActivity(selectionIntent);
                    }
                    b1IsDown = false;
                }
                break;
            default:

        }
        invalidate();
        return true;
    }


    private boolean isRect(Rect rect, int X, int Y) {
        if (X > rect.left && X < rect.right && Y > rect.top && Y < rect.bottom) return true;
        return false;
    }

}

class SubVector {
    public float x = 0f;
    public float y = 0f;

    public SubVector(float x, float y) {
        this.x = x;
        this.y = y;
    }

}