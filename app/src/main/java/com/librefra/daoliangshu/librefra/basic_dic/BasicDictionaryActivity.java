package com.librefra.daoliangshu.librefra.basic_dic;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.main.DBHelper;
import com.librefra.daoliangshu.librefra.tools.Retriever;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by daoliangshu on 2016/10/11.
 */
public class BasicDictionaryActivity extends Activity {

    private EditText e_SearchEntry;
    private Button btn_SearchEntry;
    private ScrollView resultView;

    private CheckBox check_noun;
    private CheckBox check_verb;
    private CheckBox check_other;

    private DBHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dictionary_layout);
        dbHelper = DBHelper.getInstance(getApplicationContext());
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        init();
        next();
    }

    public void init() {
        this.btn_SearchEntry = (Button) findViewById(R.id.btn_dicSearch);
        this.e_SearchEntry = (EditText) findViewById(R.id.edit_dicSearchEntry);
        this.e_SearchEntry.setTextColor(Color.rgb(255, 255, 255));
        this.resultView = (ScrollView) findViewById(R.id.dic_result_scrollview);
        this.check_noun = (CheckBox) findViewById(R.id.check_noun);
        this.check_verb = (CheckBox) findViewById(R.id.check_verb);
        this.check_other = (CheckBox) findViewById(R.id.check_other);

        this.btn_SearchEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.i("BUT", "NEXT has been pressed");
                confirmSearch();
                next();
            }
        });

        this.e_SearchEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setEntries(s.toString());

            }
        });

    }

    public void next() {
    }

    public void confirmSearch() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        setEntries(this.e_SearchEntry.getText().toString());
        // Add the LinearLayout element to the ScrollView
    }


    public void setEntries(String strToSearch) {
        if (this.ll == null) {
            ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            this.resultView.addView(ll);
        } else {
            ll.removeAllViews();
        }
        if (strToSearch == null) return;

        ArrayList<HashMap<String, String>> subRes = dbHelper.getTransList_byPattern(strToSearch,
                this.check_noun.isChecked(),
                this.check_verb.isChecked(),
                this.check_other.isChecked(),
                DBHelper.RESULT_POST);
        if (subRes == null) return;
        for (HashMap<String, String> wordFound : subRes) {
            EntryElement ee = Retriever.createEntryElementFromMap(getBaseContext(), wordFound, count);
            ll.addView(ee);
        }
        ;
    }

    private int count = 0;
    private LinearLayout ll;


}


