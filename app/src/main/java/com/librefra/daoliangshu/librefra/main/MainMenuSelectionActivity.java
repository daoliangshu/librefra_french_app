package com.librefra.daoliangshu.librefra.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.activity_manager.ActivityChooser;
import com.librefra.daoliangshu.librefra.basic_dic.BasicDictionaryActivity;
import com.librefra.daoliangshu.librefra.lf_lesson.LessonChooserActivity;
import com.librefra.daoliangshu.librefra.lf_reader.FrenchReader;
import com.librefra.daoliangshu.librefra.tools.GlobalSettingActivity;
import com.librefra.daoliangshu.librefra.verb.ConjugaisonActivity;
import com.librefra.daoliangshu.librefra.vocab.VocabularyActivity;

/**
 * Created by gigitintin on 03/04/16.
 */
public class MainMenuSelectionActivity extends Activity {

    private Context context;
    MainMenuSelectionActivity self;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context = this;
        self = this;

        setContentView(R.layout.select_menu_dialog);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        Button btnVoc = (Button) findViewById(R.id.btnVoc);
        Button btnConj = (Button) findViewById(R.id.btnConj);
        Button btnLesson = (Button) findViewById(R.id.btnLesson);
        Button btnDictionary = (Button) findViewById(R.id.btnDictionary);
        Button btnReader = (Button) findViewById(R.id.btnReader);
        Button btnMultipleChoices = (Button) findViewById(R.id.btn_multiple_choices);
        Button btnSettings = (Button) findViewById(R.id.btnSettings);
        Button btnQuit = (Button) findViewById(R.id.btnQuit);
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(context, GlobalSettingActivity.class);
                context.startActivity(settingIntent);
            }
        });

        btnMultipleChoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qcmIntent = new Intent(context, ActivityChooser.class);
                context.startActivity(qcmIntent);
            }
        });
        btnVoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Processing: Voc ", Toast.LENGTH_SHORT).show();
                Intent vocabularynIntent = new Intent(context, VocabularyActivity.class);
                context.startActivity(vocabularynIntent);
            }
        });
        btnConj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Processing: Conj ", Toast.LENGTH_LONG).show();
                Intent conjugationIntent = new Intent(context, ConjugaisonActivity.class);
                context.startActivity(conjugationIntent);
            }
        });
        btnDictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dicIntent = new Intent(context, BasicDictionaryActivity.class);
                context.startActivity(dicIntent);
            }
        });
        btnReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dicIntent = new Intent(context, FrenchReader.class);
                context.startActivity(dicIntent);
            }
        });
        btnLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Processing: Cours ", Toast.LENGTH_SHORT).show();
                Intent lessonChooserIntent =
                        new Intent(context, LessonChooserActivity.class);
                context.startActivity(lessonChooserIntent);
            }
        });

    }
}
