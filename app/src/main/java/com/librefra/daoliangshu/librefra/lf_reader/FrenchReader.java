package com.librefra.daoliangshu.librefra.lf_reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.basic_dic.EntryElement;
import com.librefra.daoliangshu.librefra.lf_lesson.LessonState;
import com.librefra.daoliangshu.librefra.main.DBHelper;
import com.librefra.daoliangshu.librefra.tools.Retriever;
import com.librefra.daoliangshu.librefra.tools.Setter;
import com.librefra.daoliangshu.librefra.verb.LF_Conjug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by daoliangshu on 2016/10/16.
 * This activity aims to provide convenient way for reading french.
 * It integrate the dictionary and other functionalities of the app
 * Currently: Can read txt files only
 */
public class FrenchReader extends Activity {
    private final int RESULT_READ_PATH = 10;
    private Context context;
    private TextView trans;
    private TextView lessonHead1;
    private TextView mainContent;
    private ScrollView scrollView;
    private Button btnPrevious;
    private Button btnNext;
    private Button btnPageDisplay;
    private ArrayList<String> pages;
    private int curPage = 0;
    private float click_offset[] = {0, 0};
    private int click_cnt = 0;
    boolean isClick = false;

    private ScrollView resultView;
    private LinearLayout ll;
    private int count = 0;
    private HashMap<String, String> currentDictionaryEntries;
    private LF_Conjug conj;
    DBHelper dbHelper;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.french_reader_layout);

        context = this;
        dbHelper = DBHelper.getInstance(getApplicationContext());
        conj = new LF_Conjug(this.context);
        trans = (TextView) findViewById(R.id.reader_trans);
        lessonHead1 = (TextView) findViewById(R.id.reader_word);
        resultView = (ScrollView) findViewById(R.id.lesson_info_scrollview);
        mainContent = (TextView) findViewById(R.id.reader_view);
        scrollView = (ScrollView) findViewById(R.id.reader_scroll);
        btnNext = (Button) findViewById(R.id.btn_nextPage);
        btnPrevious = (Button) findViewById(R.id.btn_previousPage);
        btnPageDisplay = (Button) findViewById(R.id.btn_pageDisplay);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPage();
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousPage();
            }
        });
        btnPageDisplay.setEnabled(false);

        mainContent.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

               /* WHEN USER TOUCH A WORD */

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isClick = true;
                    click_offset[0] = event.getX();
                    click_offset[1] = event.getY();

                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if ((Math.abs(event.getX() - click_offset[0]) > 10) ||
                           /*Avoid to setSelected word without purpose*/
                            (Math.abs(event.getY() - click_offset[1]) > 10)) {
                        isClick = false;
                    }

                } else if (event.getAction() == MotionEvent.ACTION_UP && isClick) {
                    float[] X_Y = new float[2];
                    X_Y[0] = event.getX();
                    X_Y[1] = event.getY();
                    Log.e("POS", "x : " + X_Y[0] + "   y : " + X_Y[1]);
                    int a = mainContent.getOffsetForPosition(X_Y[0], X_Y[1]);
                    int end = a;
                    int start = a;
                    if (a >= 0) {
                        click_cnt++;
                        String text = mainContent.getText().toString();
                        String tmp = "";
                       /*Compute bounds of the selected word*/
                        start = Retriever.getBeginningWordIndex(start, text);
                        end = Retriever.getEndWordIndex(end, text);
                        tmp = text.substring(start, end);


                        LessonState.prevSelectedWordEndIndex = LessonState.curSelectedWordEndIndex;
                        LessonState.prevSelectedWordStartIndex = LessonState.curSelectedWordStartIndex;
                        LessonState.curSelectedWordStartIndex = start;
                        LessonState.curSelectedWordEndIndex = end;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Setter.setContent(mainContent);
                            }
                        });

                        setSelectionInfo(tmp);
                    }
                }
                return true;
            }
        });
        try {
            readFromAssets(context, "book_test.txt");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        if (this.pages != null)
            this.mainContent.setText(this.pages.get(0), TextView.BufferType.SPANNABLE);

        Button openFileButton = (Button) findViewById(R.id.reader_open_file_button);
        openFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseFile();
            }
        });

    }

    public void readFromAssets(Context context, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
        this.pages = new ArrayList<>();
        String mLine = reader.readLine();
        int count;
        while (mLine != null) {
            count = 0;
            StringBuilder sb = new StringBuilder();
            while (count < 50 && mLine != null) {
                sb.append(String.format(Locale.ENGLISH, "%s\n", mLine)); // process line
                mLine = reader.readLine();
                count += 1;
            }
            this.pages.add(sb.toString());

        }
        curPage = 0;
        this.btnPageDisplay.setText(
                String.format(Locale.ENGLISH, "%d/%d",
                        curPage, pages.size() - 1));
        reader.close();
    }

    public void readFile(Context context, String path) throws IOException {
        path = path.substring(path.indexOf("/storage"), path.length());
        File file = new File(path);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        this.pages = new ArrayList<>();
        String mLine = reader.readLine();
        int count;
        while (mLine != null) {
            count = 0;
            StringBuilder sb = new StringBuilder();
            while (count < 50 && mLine != null) {
                sb.append(String.format(Locale.ENGLISH, "%s\n", mLine)); // process line
                mLine = reader.readLine();
                count += 1;
            }
            this.pages.add(sb.toString());

        }
        curPage = 0;
        this.btnPageDisplay.setText(
                String.format(Locale.ENGLISH, "%d/%d",
                        curPage, pages.size() - 1));
        reader.close();

        if (this.pages != null & this.pages.size() > 0)
            this.mainContent.setText(this.pages.get(0), TextView.BufferType.SPANNABLE);
    }


    private void nextPage() {
        if (curPage >= this.pages.size() - 1) {
            return;
        }
        curPage += 1;
        this.mainContent.setText(this.pages.get(curPage), TextView.BufferType.SPANNABLE);
        this.btnPageDisplay.setText(
                String.format(Locale.ENGLISH, "%d/%d",
                        curPage, pages.size() - 1));
        this.scrollView.scrollTo(0, scrollView.getTop());
    }

    private void previousPage() {
        if (curPage >= this.pages.size() || curPage <= 0) {
            return;
        }
        curPage -= 1;
        this.mainContent.setText(this.pages.get(curPage), TextView.BufferType.SPANNABLE);
        this.btnPageDisplay.setText(
                String.format(Locale.ENGLISH, "%d/%d",
                        curPage, pages.size() - 1));
        this.scrollView.scrollTo(0, scrollView.getBottom());
    }

    public void setSelectionInfo(String selWord) {
        String selectedWord = selWord.toLowerCase();
        if (this.ll == null) {
            ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            this.resultView.addView(ll);
        } else {
            ll.removeAllViews();
        }
        this.currentDictionaryEntries = new HashMap<>();
        lessonHead1.setText(selectedWord);
        if (selectedWord.equals("")) return;

        ArrayList<HashMap<String, String>> subRes =
                dbHelper.getTransList_byPattern(selectedWord,
                        true, true, true, DBHelper.RESULT_EXACT);
        ArrayList<HashMap<String, String>> temp = null;

        if (selectedWord.endsWith("s")) {
            /* check for pluriel */
            String mod2 = selectedWord.substring(0, selectedWord.length() - 1);
            temp = dbHelper.getTransList_byPattern(mod2,
                    true, false, true, DBHelper.RESULT_EXACT);
            if (temp != null) {
                if (subRes == null) subRes = temp;
                else subRes.addAll(temp);
            }
            selectedWord = selectedWord.substring(0, selectedWord.length() - 1);
        }


        if (selectedWord.endsWith("ve")) {
            /* Check for adjectif f -> ve (fem.) */
            String mod1 = selectedWord.substring(0, selectedWord.length() - 2) + "f";
            temp = dbHelper.getTransList_byPattern(mod1,
                    false, false, true, DBHelper.RESULT_EXACT);

        } else if (selectedWord.endsWith("euse")) {
            /* eur -> euse */
            String mod3 = selectedWord.substring(0, selectedWord.length() - 2) + "r";
            temp = dbHelper.getTransList_byPattern(mod3,
                    true, false, false, DBHelper.RESULT_EXACT);

            /* eux -> euse */
            mod3 = selectedWord.substring(0, selectedWord.length() - 2) + "x";
            if (temp != null) {
                ArrayList<HashMap<String, String>> temp2 =
                        dbHelper.getTransList_byPattern(mod3,
                                false, false, true, DBHelper.RESULT_EXACT);
                if (temp2 != null) temp.addAll(temp2);
            } else {
                temp = dbHelper.getTransList_byPattern(mod3,
                        false, false, true, DBHelper.RESULT_EXACT);
            }
        } else if (selectedWord.endsWith("trice")) {
            String mod4 = selectedWord.substring(0, selectedWord.length() - 4) + "eur";
            temp = dbHelper.getTransList_byPattern(mod4,
                    true, false, false, DBHelper.RESULT_EXACT);
        } else if (selectedWord.endsWith("e")) {
            /* Simple feminin */
            String mod4 = selectedWord.substring(0, selectedWord.length() - 1);
            temp = dbHelper.getTransList_byPattern(mod4,
                    true, false, false, DBHelper.RESULT_EXACT);
        }
        if (temp != null) {
            if (subRes != null)
                subRes.addAll(temp);
            else subRes = temp;
        }

        //Search for conjugated verb if nothing was found
        ArrayList<String> ar = this.conj.getInfinitivesToCheck(selectedWord);
        if (ar != null) {
            for (String potentialVerb : ar) {
                temp = dbHelper.getTransList_byPattern(potentialVerb,
                        false, true, false, DBHelper.RESULT_EXACT);
                if (temp != null) {
                    if (subRes != null)
                        subRes.addAll(temp);
                    else subRes = temp;
                }
            }
        }
        if (subRes == null) return;
        for (HashMap<String, String> wordFound : subRes) {
            if (!this.currentDictionaryEntries.containsKey(wordFound.get(DBHelper.WORD) + "_" + wordFound.get(DBHelper.TB_TABLE_STR))) {
                this.currentDictionaryEntries.put(wordFound.get(DBHelper.WORD) + "_" + wordFound.get(DBHelper.TB_TABLE_STR), "1");
                EntryElement ee = Retriever.createEntryElementFromMap(context, wordFound, count);
                ll.addView(ee);
            }
        }
    }


    public void onChooseFile() {
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Intent intent = new Intent();
        intent.setType("text/plain");
        if (isKitKat) intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, RESULT_READ_PATH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("INFO", "reqCode = " + requestCode + "   resCode : " + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_READ_PATH) {
                String selectedFile = data.getData().getPath();
                try {
                    readFile(context, selectedFile);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

            }
        }
    }


}
