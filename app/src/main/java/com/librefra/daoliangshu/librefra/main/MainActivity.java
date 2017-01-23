package com.librefra.daoliangshu.librefra.main;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.tools.SettingsManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private TitleView tView;

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
        tView = new TitleView(this, null);
        setContentView(tView);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        copyVoc("vocab", "librefra_vocab");
        copyVoc("lessons", "librefra_lessons");
        SettingsManager.copyAssetFileToData(getApplicationContext(), "vocab");
        SettingsManager.copyAssetFileToData(getApplicationContext(), "lessons");
        SettingsManager.loadSettings(getApplicationContext());
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

}
