package com.librefra.daoliangshu.librefra.lettrabulle;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.librefra.daoliangshu.librefra.vocab.VocabularyUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by daoliangshu on 2016/11/8.
 */

public class LettrabulleActivity extends Activity {

    private LettrabulleView gameView;
    private GameInfoPanel infoPanel;
    private DBHelper dbHelper;
    private Timer timer;
    private boolean timeRunning = true;
    private int timerValue;
    private int maxTimerValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.lettrabulle_layout);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            Parcelable[] ps = extra.getParcelableArray("vocUnits");
            VocabularyUnit[] vocUnits = new VocabularyUnit[ps.length];
            System.arraycopy(ps, 0, vocUnits, 0, ps.length);
            LB_Config.vocList = new ArrayList<>(Arrays.asList(vocUnits));
            LB_Config.mode = LB_Config.MODE_VOC_LIST_DATABASE;
            int i = 0;
        } else {
            LB_Config.mode = LB_Config.MODE_RANDOM_DATABASE;
        }
        dbHelper = DBHelper.getInstance(getApplicationContext());
        gameView = (LettrabulleView) findViewById(R.id.lettrabulle_view);
        gameView.setActivity(this);
        infoPanel = new GameInfoPanel(
                getApplicationContext(),
                (TextView) findViewById(R.id.bulle_word_to_guess),
                (TextView) findViewById(R.id.bulle_trans_of_word),
                (TextView) findViewById(R.id.bulle_time),
                (TextView) findViewById(R.id.bulle_score)
        );
        Button settings = (Button) findViewById(R.id.bulle_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewFlipper vf = (ViewFlipper) findViewById(R.id.lettrabule_head_flipper);
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
        Button retryButton = (Button) findViewById(R.id.lettrabule_retry_btn);
        Button quitButton = (Button) findViewById(R.id.lettrabule_quit_btn);
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
        Button back = (Button) findViewById(R.id.lettrabulle_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewFlipper vf = (ViewFlipper) findViewById(R.id.lettrabule_head_flipper);
                        vf.setDisplayedChild(0);
                    }
                });
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Go back to game */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("CHANGE_VIEW", "VIEW HAS CHANGED");
                        timeRunning = true;
                        View v1 = findViewById(R.id.game_layout);

                        View v = findViewById(R.id.lettrabulle_game_over_view);
                        v.setVisibility(View.GONE);
                        gameView.setRunning(true);
                        v1.setVisibility(View.VISIBLE);
                        timerValue = 0;
                    }
                });
            }
        });
        /*--------_Speed Settings -------------*/
        RadioGroup radioGroupSpeed = (RadioGroup) findViewById(R.id.radioGroupSpeed);
        int currSpeed = LB_Config.getSpeedCode();
        int selectedRadio = 0;
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
                        LB_Config.currentSpeed = LB_Config.SPEED_SLOW;
                        break;
                    case R.id.radio_medium:
                        LB_Config.currentSpeed = LB_Config.SPEED_MEDIUM;
                        break;
                    case R.id.radio_fast:
                        LB_Config.currentSpeed = LB_Config.SPEED_FAST;
                        break;
                    default:
                        LB_Config.currentSpeed = LB_Config.SPEED_MEDIUM;
                }
            }
        });
    }

    String str;
    String strZh;

    Thread thread = new Thread(new Runnable() {
        public void run() {
            infoPanel.setWord(str);
            infoPanel.setTrans(strZh);
        }
    });

    public void updateInfoWord(String str, String strZh) {
        this.str = str;
        this.strZh = strZh;
        runOnUiThread(thread);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.setRunning(true);
        //gameView.getThread();
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

    public void pauseGame() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("CHANGE_VIEW", "VIEW HAS CHANGED");
                timeRunning = false;
                View v = findViewById(R.id.lettrabulle_game_over_view);
                if (v.getVisibility() == View.VISIBLE) return;
                View v1 = findViewById(R.id.game_layout);
                v1.setVisibility(View.GONE);

                v.setVisibility(View.VISIBLE);

                timerValue = 0;
                TextView tvScore = (TextView) findViewById(R.id.lettrabulle_score_gameover);
                tvScore.setText(String.valueOf(String.valueOf(infoPanel.getScore())));
                infoPanel.setScore(0);


            }
        });
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
}