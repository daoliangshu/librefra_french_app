package com.librefra.daoliangshu.librefra.daoliangboom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.activity_manager.GameSettingsSlideFragment;
import com.librefra.daoliangshu.librefra.activity_manager.GameStatusSlideFragment;
import com.librefra.daoliangshu.librefra.activity_manager.ZoomOutPageTransformer;
import com.librefra.daoliangshu.librefra.main.DBHelper;

import java.io.IOException;

public class TirAChoix_GameActivity extends AppCompatActivity {

    private String word;
    private TirAChoixView gameView;
    private DBHelper dbHelper;

    private GameSettingsSlideFragment gameSettings;
    private GameStatusSlideFragment gameStatus;

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

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Go back to game */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameStatus.restartTimer();
                        View v1 = findViewById(R.id.game_layout);
                        View v = findViewById(R.id.dlb_game_over_view);
                        v.setVisibility(View.GONE);
                        gameView.resetLife();
                        gameView.setRunning(true);
                        v1.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        ViewPager mGameMenuPager = (ViewPager) findViewById(R.id.game_menu_pager);
        PagerAdapter mPagerAdapterMain = new SlidePagerAdapter(getSupportFragmentManager()
        );
        mGameMenuPager.setAdapter(mPagerAdapterMain);
        mGameMenuPager.setPageTransformer(true, new ZoomOutPageTransformer());



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
                gameStatus.incrementScore(scoreToAdd);
            }
        });
    }

    public void pauseGame() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameStatus.pauseTimer();
                View v = findViewById(R.id.dlb_game_over_view);
                if (v.getVisibility() == View.VISIBLE) return;
                View v1 = findViewById(R.id.game_layout);
                v1.setVisibility(View.GONE);

                v.setVisibility(View.VISIBLE);
                TextView tvScore = (TextView) findViewById(R.id.dlb_score_gameover);
                tvScore.setText(String.valueOf(String.valueOf(gameStatus.getScore())));
                gameStatus.setScore(0);
            }
        });
    }


    Thread thread = new Thread(new Runnable() {
        public void run() {
            gameStatus.setWord(word, true);
        }
    });

    public void setInfoWord(final String word) {
        this.word = word;
        runOnUiThread(thread);
    }

    private class SlidePagerAdapter extends FragmentStatePagerAdapter {
        private Fragment mCurrentFragment;

        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    GameStatusSlideFragment res = new GameStatusSlideFragment();
                    gameStatus = res;
                    return res;
                case 1:
                    GameSettingsSlideFragment res2 = new GameSettingsSlideFragment();
                    gameSettings = res2;
                    return res2;
            }
            return null;
        }


        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


}