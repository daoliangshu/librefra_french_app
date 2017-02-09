package com.librefra.daoliangshu.librefra.main;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.activity_manager.MenuButtonFragment;
import com.librefra.daoliangshu.librefra.tools.SettingsManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private TitleView tView;
    private ViewPager menu;
    private final float RATIO_SCALE = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(
                R.anim.slide_in,
                R.anim.stay
        );
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //tView = (TitleView)findViewById(R.id.title_view);
        setContentView(R.layout.title_screen);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        copyVoc("vocab", "librefra_vocab");
        copyVoc("lessons", "librefra_lessons");
        SettingsManager.copyAssetFileToData(getApplicationContext(), "vocab");
        SettingsManager.copyAssetFileToData(getApplicationContext(), "lessons");
        SettingsManager.loadSettings(getApplicationContext());

        menu = (ViewPager) findViewById(R.id.menu_pager);

        menu.setClipToPadding(false);
        menu.setPadding(50, 0, 50, 0);
        menu.setPageMargin(2);
        menu.setOffscreenPageLimit(7);
        PagerAdapter mPagerAdapterMain = new SlidePagerAdapter(getSupportFragmentManager());
        menu.setAdapter(mPagerAdapterMain);
        //menu.setPageTransformer(true, new ZoomOutPageTransformer());

        menu.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i("", "onPageScrolled: " + position);

                MenuButtonFragment sampleFragment = (MenuButtonFragment) ((SlidePagerAdapter) menu.getAdapter()).getRegisteredFragment(position);


                float scale = 1 - (positionOffset * RATIO_SCALE);

                // Just a shortcut to findViewById(R.id.image).setScale(scale);
                sampleFragment.scaleImage(scale);


                if (position + 1 < menu.getAdapter().getCount()) {
                    sampleFragment = (MenuButtonFragment) ((SlidePagerAdapter) menu.getAdapter()).getRegisteredFragment(position + 1);
                    scale = positionOffset * RATIO_SCALE + (1 - RATIO_SCALE);
                    sampleFragment.scaleImage(scale);
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.i("", "onPageSelected: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i("", "onPageScrollStateChanged: " + state);
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    MenuButtonFragment fragment = (MenuButtonFragment) ((SlidePagerAdapter) menu.getAdapter()).getRegisteredFragment(menu.getCurrentItem());
                    fragment.scaleImage(1);
                    if (menu.getCurrentItem() > 0) {
                        fragment = (MenuButtonFragment) ((SlidePagerAdapter) menu.getAdapter()).getRegisteredFragment(menu.getCurrentItem() - 1);
                        fragment.scaleImage(1 - RATIO_SCALE);
                    }

                    if (menu.getCurrentItem() + 1 < menu.getAdapter().getCount()) {
                        fragment = (MenuButtonFragment) ((SlidePagerAdapter) menu.getAdapter()).getRegisteredFragment(menu.getCurrentItem() + 1);
                        fragment.scaleImage(1 - RATIO_SCALE);
                    }
                }

            }
        });
    }

    public void copyVoc(String assetFolderName, String newFolderName) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "librefra_voc_list");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory");
                return;
            }
        }
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list(assetFolderName);
        } catch (IOException e) {
            Log.e("vocab", "Failed to get asset file list.", e);
        }
        if (files == null) return;
        for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(assetFolderName + "/" + filename);
                File outFile = new File(getExternalFilesDir(newFolderName + "/"), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }

    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (tView != null) tView.getThread().leaveThread();
    }


    private class SlidePagerAdapter extends FragmentStatePagerAdapter {
        private Fragment mCurrentFragment;
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new MenuButtonFragment();
            ((MenuButtonFragment) fragment).setPosition(position);
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
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
            return MenuButtonFragment.PAGE_COUNT;
        }
    }
}
