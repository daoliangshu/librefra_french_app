package com.librefra.daoliangshu.librefra.daoliangboom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.daoliangboom.service.Creator;
import com.librefra.daoliangshu.librefra.daoliangboom.service.Drawer;
import com.librefra.daoliangshu.librefra.daoliangboom.service.EventManager;
import com.librefra.daoliangshu.librefra.daoliangboom.service.Updater;
import com.librefra.daoliangshu.librefra.lf_lesson.LessonSet;
import com.librefra.daoliangshu.librefra.lf_lesson.WaveSet;
import com.librefra.daoliangshu.librefra.lf_lesson.WaveUnit;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

/**
 * Created by gigitintin on 01/07/16.
 * View for the multiple choice game
 */
public class TirAChoixView extends SurfaceView implements SurfaceHolder.Callback {
    private static Handler threadHandler;
    private TirAChoix_GameActivity rootActivity;
    private WaveSet curWaveSet;
    private LessonSet curLesson;
    private int topButtonY;
    private int currentWaveIndex = -1;
    private ArrayList<Integer> correctChoiceList = null;
    private ArrayList<Integer> currentChoiceMap = new ArrayList();

    private boolean clearWave = false;

    //Interface Elements
    private Background background;
    private Bitmap bottomFond;
    private DLB_SimpleButton jumpButton;
    private DLB_SimpleButton jumpButtonInv;
    private DLB_SimpleButton shootButton;
    private DLB_SimpleButton dirButton;
    private LifeInfoPanel scorePanel;
    private DLB_TextDisplay questionPanel;

    private TirAChoixThread thread;
    public Context myContext;

    public TirAChoixView self;
    private int canvasWidth;
    private float moveUnitY = 1.0f; // Depends on res
    private float moveUnitX = 1.0f;

    /* Main Character */
    private Player main_char;
    /* Projectiles */
    private ArrayList<DLB_Projectile> projectiles;
    private int maxProjectile;
    private int nextProjectile;
    /* TextBoxes */
    private ArrayList<ChoiceBox> textBoxes;

    /*-------State --------*/
    private boolean needPauseGame = false;
    private boolean stopThread = false;
    Random rand = new Random();
    private Stack<Integer> stackWaveOrder;

    /*
     * View Contructor
     */
    public TirAChoixView(Context context, AttributeSet attrs) {
        super(context, attrs);
        self = this;
        for (int i = 0; i < 4; i++) {
            currentChoiceMap.add(i);
        }
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        threadHandler = new Handler() {
            @Override
            public void handleMessage(Message m) {
                ;
            }
        };
        thread = new TirAChoixThread(holder, context, threadHandler);

        setFocusable(true);
    }

    public TirAChoixThread getThread() {
        return this.thread;
    }

    /*
    * Thread
    */
    class TirAChoixThread extends Thread {
        private static final int FRAME_DELAY = 15;
        private long mLastTime;
        private SurfaceHolder mySurfaceHolder;
        private Canvas canvas;
        private boolean running = true;
        private boolean thread_running = true;

        public TirAChoixThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            mySurfaceHolder = surfaceHolder;
            myContext = context;
        }

        public void init(SurfaceHolder surfaceHolder, Context context) {
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
                    if (mySurfaceHolder.getSurface().isValid() && running) {
                        canvas = mySurfaceHolder.lockCanvas(null);
                        if (canvas != null && running) {
                            synchronized (mySurfaceHolder) {
                                if (running) {
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
                if (needPauseGame) {
                    running = false;
                    needPauseGame = false;
                    rootActivity.pauseGame();
                }
            }
        }

        public void leaveThread() {
            thread_running = false;
            stopThread = true;
            thread.interrupt();
        }

        private void draw(Canvas canvas) {
            background.OnDraw(canvas);
            scorePanel.onDraw(canvas);
            canvas.drawBitmap(bottomFond, 0, topButtonY, null);
            jumpButton.onDraw(canvas);
            jumpButtonInv.onDraw(canvas);
            shootButton.onDraw(canvas);
            Drawer.drawProjectiles(canvas, projectiles);
            questionPanel.onDraw(canvas);
            Drawer.drawBoxes(canvas, textBoxes);
            main_char.onDraw(canvas);
        }

        public void update() {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    checkCollisions();
                    boolean res = Updater.updateBoxes(textBoxes,
                            curWaveSet,
                            currentWaveIndex,
                            clearWave);
                    if (res) {
                         /* When a new wave need to be set */
                        currentWaveIndex = Math.abs(rand.nextInt() % curWaveSet.getWaveCount());

                        updateHintButtonState();
                        shuffleRandMap();
                        setNewWave();
                    }
                    clearWave = false;
                }
            })).start();
            (new Thread() {
                @Override
                public void run() {
                    background.update();
                }
            }).start();
        }

        private void setNewWave() {
            Updater.assignNextWave(currentWaveIndex,
                    curWaveSet.getWave(currentWaveIndex),
                    currentChoiceMap,
                    textBoxes);
            if (curWaveSet.getWave(currentWaveIndex).getType() == WaveUnit.TYPE_ORDER) {
                stackWaveOrder = new Stack<Integer>();
                int[] temp = curWaveSet.getWave(currentWaveIndex).getOrderFromFirstToLast();
                for (int i = 3; i >= 0; i--) {
                    stackWaveOrder.push(temp[i]);
                }
            }
            questionPanel.setText(curWaveSet.
                    getWave(currentWaveIndex).getTitle());
            correctChoiceList = curWaveSet.getWave(currentWaveIndex).getCorrectList();
        }

        private void checkCollisions() {
            for (ChoiceBox t : textBoxes) {
                if (!t.getIsShootable()) {
                    continue;
                }
                for (DLB_Projectile p : projectiles) {
                    if (p.getState() == DLB_Projectile.STATE_ACTIVE &&
                            t.getIsShootable() &&
                            isCollideAt(t.getRectF(), p.getRect().centerX(), p.getRect().centerY())) {
                        boolean finishWave = false;
                        clearWave = false;

                        boolean isCorrect = false;
                        if (t.getType() == DLB_Config.WAVE_TYPE_ORDER) {
                            /*
                                the choiceBox of a wave of type 'order' should have its choice index
                                matching the top of the waveOrder stack to be count as correct.
                             */
                            if (stackWaveOrder.pop() == t.getChoiceIndex()) {
                                // When is a type 'order' wave, correctness should be set so that
                                // accordingly color os displayed
                                t.setCorrectness(true);
                                isCorrect = true;
                            } else {
                                t.setCorrectness(false);
                            }
                        } else {
                            isCorrect = t.isCorrect();
                        }
                        t.setCollide(true);
                        if (!isCorrect) {
                            if (!scorePanel.incLife(-1)) {
                                //Game Over if no more life
                                needPauseGame = true;
                            }
                            finishWave = true;
                        } else {
                            // Correct answer
                            t.setIsShootable(false);

                            float distanceRatio = 1 - (p.getRect().centerX() / canvasWidth - 20);
                            rootActivity.computeAndAddScore(DLB_Config.SCORE_UNIT,
                                    distanceRatio * DLB_Config.CUR_SCORE_WEIGHT
                            );
                            switch (t.getType()) {
                                case DLB_Config.WAVE_TYPE_MULTCHOICE:
                                    if (popCorrectChoice(t.getChoiceIndex()) <= 0)
                                        finishWave = true;
                                    break;
                                case DLB_Config.WAVE_TYPE_ORDER:
                                    if (stackWaveOrder.size() <= 0) finishWave = true;
                                    break;
                                default:
                                    break;
                            }

                        }

                        if (finishWave) {
                            /*
                                Wave finishes when incorrect answer is provide, or when no
                                more correct answer can be found.
                             */
                            t.setSpriteIndex(1);
                            clearWave = true;
                            Updater.disableWaveColision(textBoxes, isCorrect);
                        }
                        p.setState(DLB_Projectile.STATE_IDLE);


                    } else if (isCollideAt(t.getRectF(),
                            (int) main_char.getPositionRectF().centerX(),
                            (int) main_char.getPositionRectF().centerY())) {
                        clearWave = true;
                        t.setCollide(true);
                    }
                }
            }
        }


        public void setSurfaceSize(int width, int height) {
            synchronized (mySurfaceHolder) {
                DLB_Config.width = width;
                moveUnitY = height / 640;
                moveUnitX = width / 480;
                canvasWidth = width;
                DLB_Config.height = height;

                DLB_Config.gameTop = (int) (height * (DLB_Config.SCORE_PANEL_RATIO +
                        DLB_Config.TITLE_PANEL_RATION));
                DLB_Config.gameBottom = (int) (height * (1 - DLB_Config.BUTTONS_PANEL_RATIO));
                background = new Background(getContext(),
                        new Rect(0, DLB_Config.gameTop, width,
                                DLB_Config.gameTop + (int) (height * DLB_Config.GAME_PANEL_RATIO)));
                Rect characterBounds = new Rect(0, 0, height / 9, height / 9);
                main_char = new Player(self, characterBounds, moveUnitY);

                // Buttons creation
                Bitmap b;
                b = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.btn1);
                b = Bitmap.createScaledBitmap(b, (width > height) ? width / 8 : height / 8,
                        (width > height) ? width / 9 : height / 9, true);
                jumpButton = new DLB_SimpleButton(b);
                jumpButton.setRect(new Rect(getWidth() - getWidth() / 4, getHeight() - getHeight() / 8, getWidth(),
                        getHeight()));
                jumpButton.setImage(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.btn_up));
                jumpButtonInv = new DLB_SimpleButton(b);
                jumpButtonInv.setRect(new Rect(10, getHeight() - getHeight() / 8, 10 + getWidth() / 4,
                        getHeight()));
                jumpButtonInv.setImage(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.btn_down));
                dirButton = new DLB_SimpleButton(b);
                shootButton = new DLB_SimpleButton(b);
                shootButton.setRect(new Rect(getWidth() - 2 * getWidth() / 4 - 10,
                        getHeight() - getHeight() / 8,
                        getWidth() - getWidth() / 4 - 10,
                        getHeight()));
                shootButton.setImage(BitmapFactory.decodeResource(myContext.getResources(), R.drawable.shoot_btn));
                dirButton.setRect(new Rect(10,
                        (int) (getHeight() * (1f - DLB_Config.BUTTONS_PANEL_RATIO)) - 10,
                        getWidth() / 5,
                        (int) (getHeight() * DLB_Config.BUTTONS_PANEL_RATIO)));
                b = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.mur1);
                bottomFond = Bitmap.createScaledBitmap(b,
                        width,
                        (int) (height * DLB_Config.BUTTONS_PANEL_RATIO),
                        false);
                topButtonY = (int) (height - height * DLB_Config.BUTTONS_PANEL_RATIO);


                initProjectiles();
                if (curWaveSet != null && curWaveSet.getWaveCount() > 0) {
                    shuffleRandMap();
                    textBoxes = Creator.createTextBoxes(self,
                            DLB_Config.BOX_PER_WAVE,
                            curWaveSet.getWave(0), currentChoiceMap, moveUnitX);
                    currentWaveIndex = 0;
                    correctChoiceList = curWaveSet.getWave(currentWaveIndex).getCorrectList();

                    updateHintButtonState();
                }

                    /*----------------------------------------*/
                    /* Initialization of Interface components */
                    /*----------------------------------------*/

                int questionTop = DLB_Config.scoreTop + (int) (DLB_Config.height * DLB_Config.SCORE_PANEL_RATIO);
                scorePanel = new LifeInfoPanel(myContext,
                        new Rect(0,
                                DLB_Config.scoreTop + 1,
                                getWidth(),
                                DLB_Config.scoreTop + (int) (DLB_Config.height * DLB_Config.SCORE_PANEL_RATIO))
                );

                questionPanel = new DLB_TextDisplay(
                        new Rect(0,
                                questionTop,
                                DLB_Config.width,
                                (int) (DLB_Config.height * DLB_Config.TITLE_PANEL_RATION)),
                        curWaveSet.getWave(0).getTitle());
                setNewWave();
            }
            thread.setRunning(true);
            if (thread.getState() == Thread.State.NEW) {
                thread.start();
            }

        }

        public void stopThread() {
            running = false;
        }

        public void setRunning(boolean b) {
            if (!b) needPauseGame = true;
            else {
                running = b;
            }
        }
    }


    /*--------------------------------------------------*/
    /*-------------Events/Collision check---------------*/
    /*--------------------------------------------------*/
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        EventManager.checkTouch_ActiveState(event, self);
        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (myContext == null) {
            Log.e("ERR", "myContext is null in view ...");
        }
        getThread().mySurfaceHolder = holder;
        thread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.setRunning(false);
        stopThread = true;
        thread.interrupt();
    }

    public void leaveThread() {
        thread.stopThread();
        thread.interrupt();
    }

    public void stopThread() {
        thread.stopThread();
    }

    public boolean isCollideAt(Rect r, int posX, int posY) {
        if (posX > r.left && posX < (r.right)) {
            if (posY > r.top && posY < (r.bottom)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCollideAt(RectF r, int posX, int posY) {
        if (posX > r.left && posX < (r.right)) {
            if (posY > r.top && posY < (r.bottom)) {
                return true;
            }
        }
        return false;
    }

    /*--------------------------------------------------*/
    /*-------------------Initializators-----------------*/
    /*--------------------------------------------------*/
    private void initProjectiles() {
        //init projectiles
        this.maxProjectile = 3;
        this.nextProjectile = 0;
        this.projectiles = new ArrayList<>();
        Rect r = new Rect(0, 0, getWidth() / 10, getHeight() / 10);
        for (int i = 0; i < maxProjectile; i++) {
            projectiles.add(new DLB_Projectile(myContext, r));
            projectiles.get(i).setState(DLB_Projectile.STATE_IDLE);
        }
    }

    /*--------------------------------------------------*/
    /*-------------------Getters------------------------*/
    /*--------------------------------------------------*/
    public Rect getJumpBtnRect() {
        return this.jumpButton.getRect();
    }

    public Rect getJumpInvBtnRect() {
        return this.jumpButtonInv.getRect();
    }

    public Rect getShootBtnRect() {
        return this.shootButton.getRect();
    }

    public ArrayList<ChoiceBox> getTextBoxes() {
        return this.textBoxes;
    }

    public Player getMainCharacter() {
        return this.main_char;
    }

    public ArrayList<DLB_Projectile> getProjectiles() {
        return this.projectiles;
    }

    public int getNextProjectileIndex() {
        return this.nextProjectile;
    }

    public String getCurrentHint() {
        if (this.textBoxes == null || this.textBoxes.size() <= 0) return null;
        return this.curWaveSet.getWave(currentWaveIndex).getHint();
    }

    /*--------------------------------------------------*/
    /*-------------------Setters------------------------*/
    /*--------------------------------------------------*/
    public void setRootActivity(TirAChoix_GameActivity activity) {
        this.rootActivity = activity;
    }

    /**
     * Control the game state
     *
     * @param state
     */
    public void setRunning(boolean state) {
        thread.setRunning(state);
    }

    public void setLesson(InputStream is, int activityIndex) {
        this.curLesson = new LessonSet(is, "filename_not+provided", getContext());
        if (activityIndex == -1 || activityIndex >= this.curLesson.lessons.get(0).getWaveSetCount()) {
            this.curWaveSet = this.curLesson.lessons.get(0).getWaveSet(0);
        } else {
            this.curWaveSet = this.curLesson.lessons.get(0).getWaveSet(activityIndex);
        }
    }

    /**
     * Set the Panel displaying word information when user touches a box
     *
     * @param boxIndex: index of the box to select
     */
    public void setCurrentSelectedBox(int boxIndex) {
        rootActivity.setInfoWord(this.textBoxes.get(boxIndex).getNextWord());
    }

    /*--------------------------------------------------*/
    /*-------------------Updates------------------------*/
    /*--------------------------------------------------*/
    public void updateNextProjectileIndex() {
        nextProjectile = (nextProjectile + 1) % maxProjectile;
    }

    public void resetLife() {
        scorePanel.refill();
    }

    private int popCorrectChoice(int index) {
        Log.i("CORRECT_CHOICE", index + "   choices: ");
        for (int i : correctChoiceList) {
            Log.i("index : ", String.valueOf(i));
        }
        if (correctChoiceList == null || correctChoiceList.size() == 0) return -1;
        if (correctChoiceList.contains(index)) {
            correctChoiceList.remove(correctChoiceList.indexOf(index));
        }
        return correctChoiceList.size();
    }

    public void shuffleRandMap() {
        Collections.shuffle(currentChoiceMap);
    }

    public ArrayList<Integer> getChoiceMap() {
        return this.currentChoiceMap;
    }

    public void updateHintButtonState() {
        if (currentWaveIndex < 0 || currentWaveIndex >= curWaveSet.getWaveCount()) {
            rootActivity.setHintButtonState(false);
            return;
        }
        if (curWaveSet.getWave(currentWaveIndex).getHint() != null &&
                !curWaveSet.getWave(currentWaveIndex).getHint().trim().equals("")) {
            rootActivity.setHintButtonState(true);

        } else {
            rootActivity.setHintButtonState(false);
        }
        rootActivity.restoreMainInfoDisplay();
    }
}

