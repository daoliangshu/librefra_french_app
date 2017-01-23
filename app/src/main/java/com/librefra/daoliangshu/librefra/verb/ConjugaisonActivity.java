package com.librefra.daoliangshu.librefra.verb;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.main.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by gigitintin on 11/04/16.
 */
public class ConjugaisonActivity extends Activity {


    private Button btnNext;
    private Button btnPrev;
    private String[] tenseList;
    private int curIndex = 0;
    private LF_Conjug conjugator;
    private HashMap<String, Integer> tenseMap;
    private String[] tenseTransList;
    private String curVerb;
    private Random rand;
    private TextView[] textByPerson;
    private TextView textTense;
    private TextView verbTextView;
    private TextView verbTransView;

    private DBHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rand = new Random();
        dbHelper = DBHelper.getInstance(getApplicationContext());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.conjug_layout);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        curVerb = getIntent().getStringExtra("verb");
        if (curVerb == null) {
            curVerb = "dormir";
        }
        this.conjugator = new LF_Conjug(getBaseContext());
        tenseMap = new HashMap<>();
        tenseMap.put(LF_Conjug.PRESENT, LF_Conjug.PRESENT_INT);
        tenseMap.put(LF_Conjug.FUTUR, LF_Conjug.FUTUR_INT);
        tenseMap.put(LF_Conjug.IMPARFAIT, LF_Conjug.IMPARFAIT_INT);
        tenseMap.put(LF_Conjug.SIMPLE_PAST, LF_Conjug.PASS_SIMPLE_INT);
        tenseList = new String[tenseMap.size()];

        tenseList = new String[]{LF_Conjug.PRESENT,
                LF_Conjug.FUTUR,
                LF_Conjug.IMPARFAIT,
                LF_Conjug.SIMPLE_PAST
        };

        tenseTransList = new String[]{"Présent",
                "Futur",
                "Imparfait",
                "Passé Simple"
        };

        init();
        next();
        verbTransView.setText(getVerb());
    }

    private void init() {
        textByPerson = new TextView[6];
        textByPerson[0] = (TextView) findViewById(R.id.tvConjJe);
        textByPerson[1] = (TextView) findViewById(R.id.tvConjTu);
        textByPerson[2] = (TextView) findViewById(R.id.tvConjIl);
        textByPerson[3] = (TextView) findViewById(R.id.tvConjNous);
        textByPerson[4] = (TextView) findViewById(R.id.tvConjVous);
        textByPerson[5] = (TextView) findViewById(R.id.tvConjIls);
        textTense = (TextView) findViewById(R.id.conj_cur_tense);
        verbTextView = (TextView) findViewById(R.id.conj_verb_search_text);
        verbTextView.setText(curVerb);
        verbTransView = (TextView) findViewById(R.id.conj_verb_trans_text);
        verbTransView.setText(curVerb);
        final Button btnVerbSearch = (Button) findViewById(R.id.conj_btn_search);
        btnVerbSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curVerb = verbTextView.getText().toString();
                verbTransView.setText(getVerb());
                update();
            }
        });

        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.i("BUT", "NEXT has been pressed");
                next();
            }
        });

        btnPrev = (Button) findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });

    }


    public void update() {
        setConjugate();
    }

    public void next() {
        curIndex = (curIndex + 1) % tenseList.length;
        setConjugate();
    }

    public void previous() {
        curIndex = curIndex - 1;
        if (curIndex < 0) curIndex = tenseList.length - 1;
        setConjugate();
    }

    public void setConjugate() {
        textTense.setText(tenseTransList[curIndex]);
        for (int i = 0; i < 6; i++) {
            String str = conjugator.getConjugate(curVerb, tenseMap.get(tenseList[curIndex]), i);
            textByPerson[i].setText(str);
        }
    }

    public String getVerb() {
        if (verbTextView.getText().length() <= 3) return "";
        ArrayList<HashMap<String, String>> res =
                dbHelper.getTransList_byPattern(verbTextView.getText().toString(),
                        false,
                        true,
                        false,
                        DBHelper.RESULT_EXACT);
        if (res != null && res.size() > 0) {
            if (res.get(0).containsKey(DBHelper.TRANS_ID_1)) {
                return res.get(0).get(DBHelper.TRANS_ID_1);
            }
            return "";
        } else {
            return "";
        }

    }


}
