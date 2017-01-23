package com.librefra.daoliangshu.librefra.daoliangboom;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.activity_manager.GameInfoPanel;
import com.librefra.daoliangshu.librefra.main.DBHelper;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TirAChoix_GameActivity extends Activity {

    private String word;
    private TirAChoixView gameView;
    private GameInfoPanel infoPanel;
    private Timer timer;
    private boolean timeRunning = true;
    private int timerValue;
    private int maxTimerValue;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        dbHelper = DBHelper.getInstance(getApplicationContext());
        String filename = getIntent().getStringExtra("lesson");
        int activityIndex = getIntent().getIntExtra("activityIndex", -1);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.daoliangboom_layout);
        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            Log.e("ERROR__", "No bundle has been passed");
        }
        if (filename == null || filename.equals("")) filename = "1.lec";
        gameView = (TirAChoixView) findViewById(R.id.boom_view);
        gameView.setRootActivity(this);
        try {
            gameView.setLesson(getAssets().open("lessons/" + filename), activityIndex);
        } catch (IOException exp) {
            exp.printStackTrace();
            System.exit(1);
        }
        /* Top information panel*/
        infoPanel = new GameInfoPanel(
                getApplicationContext(),
                (TextView) findViewById(R.id.dlb_word_to_guess),
                (TextView) findViewById(R.id.dlb_trans_of_word),
                (TextView) findViewById(R.id.dlb_time),
                (TextView) findViewById(R.id.dlb_score));

        Button settings = (Button) findViewById(R.id.dlb_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewFlipper vf = (ViewFlipper) findViewById(R.id.dlb_head_flipper);
                        vf.setDisplayedChild(1);
                    }
                });
            }
        });
        timerValue = 0;
        maxTimerValue = 9000;
        final Thread timerUpdateThread = new Thread(new Runnable() {
            public void run() {
                if (timeRunning) {
                    timerValue += 1;
                    if (timerValue >= maxTimerValue) {
                        timerValue = 0;
                        infoPanel.setTimer(timerValue);
                    } else {
                        infoPanel.incrementTimer();
                    }
                }

            }
        });
        timer = new Timer();
        TimerTask timerTaskObj = new TimerTask() {
            public void run() {
                runOnUiThread(timerUpdateThread);
            }
        };
        timer.schedule(timerTaskObj, 0, 1500);

        /*---- Flip View ---*/
        Button retryButton = (Button) findViewById(R.id.dlb_retry_btn);
        Button quitButton = (Button) findViewById(R.id.dlb_quit_btn);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onDestroy();
                    }
                });
            }
        });

        int[] backButtonId = {R.id.dlb_back, R.id.dlb_back2};
        for (int i = 0; i < backButtonId.length; i++) {
            Button back = (Button) findViewById(backButtonId[i]);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ViewFlipper vf = (ViewFlipper) findViewById(R.id.dlb_head_flipper);
                            vf.setDisplayedChild(0);
                        }
                    });
                }
            });
        }


        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Go back to game */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeRunning = true;
                        View v1 = findViewById(R.id.game_layout);

                        View v = findViewById(R.id.dlb_game_over_view);
                        v.setVisibility(View.GONE);
                        gameView.resetLife();
                        gameView.setRunning(true);
                        v1.setVisibility(View.VISIBLE);
                        timerValue = 0;
                    }
                });
            }
        });


        /*--------_Speed Settings -------------*/
        RadioGroup radioGroupSpeed = (RadioGroup) findViewById(R.id.radioGroupSpeed);

        int currSpeed = DLB_Config.getSpeedCode();
        int selectedRadio;
        switch (currSpeed) {
            case 0:
                selectedRadio = R.id.radio_slow;
                break;
            case 2:
                selectedRadio = R.id.radio_fast;
                break;
            default:
                selectedRadio = R.id.radio_medium;
                break;
        }
        RadioButton selectedRdb = (RadioButton) findViewById(selectedRadio);
        selectedRdb.setChecked(true);


        radioGroupSpeed.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_slow:
                        DLB_Config.currentSpeed = DLB_Config.SPEED_SLOW;
                        break;
                    case R.id.radio_medium:
                        DLB_Config.currentSpeed = DLB_Config.SPEED_MEDIUM;
                        break;
                    case R.id.radio_fast:
                        DLB_Config.currentSpeed = DLB_Config.SPEED_FAST;
                        break;
                    default:
                        DLB_Config.currentSpeed = DLB_Config.SPEED_MEDIUM;
                }
            }
        });

        Button showHintButton = (Button) findViewById(R.id.dlb_show_hint_button);
        showHintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayHint();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.setRunning(true);
    }


    @Override
    protected void onPause() {

        super.onPause();
        gameView.setRunning(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameView.leaveThread();
        gameView.getThread().leaveThread();
        this.finish();
    }

    public void computeAndAddScore(final int baseInt, final float weight) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int scoreToAdd = (int) (baseInt * weight);
                infoPanel.incrementScore(scoreToAdd);
            }
        });
    }

    public void pauseGame() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timeRunning = false;
                View v = findViewById(R.id.dlb_game_over_view);
                if (v.getVisibility() == View.VISIBLE) return;
                View v1 = findViewById(R.id.game_layout);
                v1.setVisibility(View.GONE);

                v.setVisibility(View.VISIBLE);

                timerValue = 0;
                TextView tvScore = (TextView) findViewById(R.id.dlb_score_gameover);
                tvScore.setText(String.valueOf(String.valueOf(infoPanel.getScore())));
                infoPanel.setScore(0);
            }
        });
    }


    Thread thread = new Thread(new Runnable() {
        public void run() {
            infoPanel.setWord(word, true);
        }
    });

    public void setInfoWord(final String word) {
        this.word = word;
        runOnUiThread(thread);
    }

    public void setHintButtonState(boolean state) {
        if (state) {
            runOnUiThread(new Thread(new Runnable() {
                @Override
                public void run() {
                    Button hintButton = (Button) findViewById(R.id.dlb_show_hint_button);
                    hintButton.setVisibility(View.VISIBLE);
                }
            }));
        }
    }

    public void displayHint() {
        final String curHint = gameView.getCurrentHint();
        if (curHint == null) return;
        runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                TextView tvHint = (TextView) findViewById(R.id.textview_hint);
                tvHint.setText(curHint);
                ViewFlipper vf = (ViewFlipper) findViewById(R.id.dlb_head_flipper);
                vf.setDisplayedChild(2);

            }
        }));
    }

    public void restoreMainInfoDisplay() {
        runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                ViewFlipper vf = (ViewFlipper) findViewById(R.id.dlb_head_flipper);
                vf.setDisplayedChild(0);
            }
        }));
    }


}