package com.librefra.daoliangshu.librefra.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.daoliangboom.DLB_Config;
import com.librefra.daoliangshu.librefra.lettrabulle.LB_Config;


/**
 * Created by daoliangshu on 1/5/17.
 * Activity where can be set the settings for the app.
 */

public class GlobalSettingActivity extends Activity {
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.lightMediumGray));
        setContentView(R.layout.global_settings_layout);

        context = this;
        final RadioButton btnlbulleSlow = (RadioButton) findViewById(R.id.radio_lbulle_slow);
        final RadioButton btnlbulleMedium = (RadioButton) findViewById(R.id.radio_lbulle_medium);
        final RadioButton btnlbulleFast = (RadioButton) findViewById(R.id.radio_lbulle_fast);
        final RadioButton btnQcmSlow = (RadioButton) findViewById(R.id.radio_qcm_slow);
        final RadioButton btnQcmMedium = (RadioButton) findViewById(R.id.radio_qcm_medium);
        final RadioButton btnQcmFast = (RadioButton) findViewById(R.id.radio_qcm_fast);
        Button btnSave = (Button) findViewById(R.id.settings_save_btn);
        /* Check for configuration or create it */
        int lb_speed = LB_Config.getSpeedCode();
        switch (lb_speed) {
            case 0:
                btnlbulleSlow.setChecked(true);
                break;
            case 1:
                btnlbulleMedium.setChecked(true);
                break;
            case 2:
                btnlbulleFast.setChecked(true);
                break;
        }

        int qcm_speed = DLB_Config.getSpeedCode();
        switch (qcm_speed) {
            case 0:
                btnQcmSlow.setChecked(true);
                break;
            case 1:
                btnQcmMedium.setChecked(true);
                break;
            case 2:
                btnQcmFast.setChecked(true);
                break;
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnlbulleSlow.isChecked()) LB_Config.setSpeed(0);
                else if (btnlbulleFast.isChecked()) LB_Config.setSpeed(2);
                else LB_Config.setSpeed(1);

                if (btnQcmSlow.isChecked()) DLB_Config.setSpeed(0);
                else if (btnQcmFast.isChecked()) DLB_Config.setSpeed(2);
                else LB_Config.setSpeed(1);

                SettingsManager.saveSettings(context);
            }
        });


    }
}