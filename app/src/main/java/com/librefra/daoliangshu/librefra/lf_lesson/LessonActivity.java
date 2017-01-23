package com.librefra.daoliangshu.librefra.lf_lesson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AlignmentSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.basic_dic.EntryElement;
import com.librefra.daoliangshu.librefra.daoliangboom.TirAChoix_GameActivity;
import com.librefra.daoliangshu.librefra.lettrabulle.LettrabulleActivity;
import com.librefra.daoliangshu.librefra.main.DBHelper;
import com.librefra.daoliangshu.librefra.tools.Checker;
import com.librefra.daoliangshu.librefra.tools.DrawerListAdapter;
import com.librefra.daoliangshu.librefra.tools.Retriever;
import com.librefra.daoliangshu.librefra.tools.Setter;
import com.librefra.daoliangshu.librefra.verb.LF_Conjug;
import com.librefra.daoliangshu.librefra.vocab.VocabularyListSet;
import com.librefra.daoliangshu.librefra.vocab.VocabularyUnit;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;


/**
 * Created by gigitintin on 28/06/16.
 * Activity displaying the main window from the course content
 */
public class LessonActivity extends Activity implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {


    public static final int MODE_NORMAL = 0;
    public static final int MODE_WITH_NUMBERS = 1;
    public static final int MODE_NO_LINE_RETURN = 2;
    LessonSet courseContainer;

    /*Left Slide Menu Control */
    private DrawerLayout menuLayout;
    private ListView menuElementsList; // Menu
    private ActionBarDrawerToggle menuToggle; // Manage opn/close menu

    /*Text to Speech Controls*/
    private TextToSpeech ttsObj;
    private int speakStartIndex = 0;
    private int speakUtteranceCount = 0;
    private int speakCurrentPos = 0;
    private boolean isSpeaking = false;

    /*Main drawable components*/
    private ScrollView scroller;
    private TextView lessonView;
    private TextView lessonHead1;
    private TextView lessonHeadTranslation;
    private ViewGroup hiddenPanel;
    private ViewGroup tbSpeakers;
    private ScrollView resultView;
    private TextView tvFullTranslation;
    private LinearLayout explanationSide;
    private LinearLayout ll;
    private int count = 0;
    private ArrayList<Integer[]> lineInfo;
    private ViewFlipper infoFlipper;
    private TextView explanationView;

    private Context context;
    private String filename;
    private HashMap<String, String> currentDictionaryEntries;
    private HashMap<Integer, String> explanationMap;
    ArrayList<LessonUnit> lessons;
    private boolean isClick = false;
    private float click_offset[];
    private LF_Conjug conj;
    private DBHelper dbHelper;
    //Test
    int click_cnt = 0;

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = ttsObj.setLanguage(Locale.FRENCH);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "This Language is not supported",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Ready to Speak", Toast.LENGTH_LONG).
                        show();
                ttsObj.setOnUtteranceCompletedListener(this);
            }
            Log.i("TTS", "Passed TTS init");
        }

    }

    @Override
    public void onDestroy() {
        if (ttsObj != null) {
            ttsObj.stop();
            ttsObj.shutdown();
        }
        super.onDestroy();
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
        this.setContentView(R.layout.lesson_display_layout);

        dbHelper = DBHelper.getInstance(getApplicationContext());
        ttsObj = new TextToSpeech(this, this);
        context = this;


        infoFlipper = (ViewFlipper) findViewById(R.id.lesson_info_flipper_view);
        infoFlipper.setDisplayedChild(0);
        hiddenPanel = (ViewGroup) findViewById(R.id.hidden_panel);
        tbSpeakers = (ViewGroup) findViewById(R.id.tb_speakers);
        tvFullTranslation = (TextView) findViewById(R.id.tvFullTranslation);
        Button b = (Button) findViewById(R.id.speakBtn);
        explanationSide = (LinearLayout) findViewById(R.id.lesson_explanation_side);
        explanationView = (TextView) findViewById(R.id.lesson_explanation_text);

        /*Set listener for button that Speak the selected word*/
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSpeaking = false;
                String str = ((TextView) findViewById(R.id.lesson_head1)).getText().toString();
                HashMap<String, String> params = new HashMap<>();

                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
                ttsObj.speak(str,
                        TextToSpeech.QUEUE_FLUSH, params);
            }
        });

        /*Set listener for button that Speak sequencially the text from the start*/
        b = (Button) findViewById(R.id.speakBtn2);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttsObj.isSpeaking()) {
                    ttsObj.stop();
                    isSpeaking = false;
                    LessonState.curSpeakingPortionStartIndex = 0;
                    LessonState.curSpeakingPortionEndIndex = 0;
                    speakStartIndex = 0;
                    speakUtteranceCount = 0;
                } else {
                    isSpeaking = true;
                    String str = ((TextView) findViewById(R.id.lesson_view)).getText().toString();
                    String utterance = "";
                    speakCurrentPos = speakStartIndex;
                    speakCurrentPos = 0;

                    char c = str.charAt(speakCurrentPos);
                    while (Checker.isSymbolOutsideSentence(c)) {
                        utterance += c;
                        ++speakCurrentPos;
                        c = str.charAt(speakCurrentPos);
                    }
                    HashMap<String, String> params = new HashMap<>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utterance);
                    ttsObj.speak(utterance,
                            TextToSpeech.QUEUE_ADD, params);
                    while (Checker.isSymbolOutsideSentence(c)) {
                        ttsObj.speak(String.valueOf(c), TextToSpeech.QUEUE_ADD, null);
                        ++speakCurrentPos;
                        c = str.charAt(speakCurrentPos);
                    }
                }
            }
        });


        /*---------Info flipper View---<Control>-------*/
        Button explanBackButton = (Button) findViewById(R.id.lesson_back_from_expl);
        explanBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoFlipper.setDisplayedChild(0);
            }
        });


        lessonView = (TextView) findViewById(R.id.lesson_view);
        scroller = (ScrollView) findViewById(R.id.lesson_scroll);
        lessonHead1 = (TextView) findViewById(R.id.lesson_head1);
        //lessonHeadTranslation = (TextView) findViewById(R.id.lesson_trans_text);
        resultView = (ScrollView) findViewById(R.id.lesson_info_scrollview);
        menuLayout = (DrawerLayout) findViewById(R.id.menu_layout);
        menuElementsList = (ListView) findViewById(R.id.lesson_menu);
        menuLayout.setDrawerShadow(null, GravityCompat.START);
        String entries[] = getResources().getStringArray(R.array.lesson_menu_items_zh_fr);

        DrawerListAdapter adapter = new DrawerListAdapter(this, R.layout.custom_list,
                Arrays.asList(entries));
        conj = new LF_Conjug(this.context);
        menuElementsList.setAdapter(adapter);
        menuToggle = new ActionBarDrawerToggle(this, menuLayout, null, 0, 0) {
            //execute at the closure of menu
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            //execute at the opening of menu
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); //cal onPrepareOptionsMenu
            }
        };
        menuLayout.addDrawerListener(menuToggle);
        menuElementsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                switch ((int) (id)) {

                    case 4: /* Launch vocabulary practice */
                        Intent goToGame = new Intent(getApplicationContext(), LettrabulleActivity.class);
                        InputStream is;
                        VocabularyListSet vls = null;
                        try {
                            is = getApplication().
                                    getAssets().open("lessons/" + filename);
                            vls = new VocabularyListSet(context, is, filename);
                            if (vls.getVocabularyListCount() <= 0 ||
                                    vls.getVocabularyList(0).getSize() <= 0) {
                                Toast.makeText(getApplicationContext(),
                                        "沒有單子練習",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            is.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        //ArrayList<VocabularyUnit> vocUnits = lessons.get(0).vocList.getAll();
                        if (vls == null) return;
                        ArrayList<VocabularyUnit> vocUnits = vls.getAll();


                        if (vocUnits != null) {
                            Parcelable[] passedArray = new Parcelable[vocUnits.size()];
                            for (int i = 0; i < vocUnits.size(); i++) {
                                passedArray[i] = vocUnits.get(i);
                            }
                            goToGame.putExtra("vocUnits", passedArray);
                            startActivity(goToGame);
                        }
                        break;

                    case 3: /* Launch MultipleChoice activity */
                        Intent myIntent = new Intent(getBaseContext(), TirAChoix_GameActivity.class);
                        //myIntent.putExtra("key", value); //Optional parameters
                        myIntent.putExtra("lesson", getIntent().getStringExtra("lesson"));
                        startActivity(myIntent);
                        break;
                    case 2: /* Toggle translation TextView*/
                        if (hiddenPanel.getVisibility() == View.GONE)
                            hiddenPanel.setVisibility(View.VISIBLE);
                        else
                            hiddenPanel.setVisibility(View.GONE);
                        break;
                    case 1: /* Display/Hide speak options */

                        if (tbSpeakers.getVisibility() == View.GONE)
                            tbSpeakers.setVisibility(View.VISIBLE);
                        else
                            tbSpeakers.setVisibility(View.GONE);
                        break;
                }
            }
        });

        /*
         * Retrieve lesson filename to open
         */
        this.filename = this.getIntent().getStringExtra("lesson");
        Log.i("lesson_filename:", this.filename);
        //LessonUnit cc = lessons.get(0);
        InputStream is;
        Log.i("filename_opened", this.filename);
        try {
            is = getApplication().
                    getAssets().open("lessons/" + filename);
            this.courseContainer = new LessonSet(is, filename, getBaseContext());
            is.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.lessons = courseContainer.lessons;
        setLessonText(this.lessons.get(0), LessonActivity.MODE_WITH_NUMBERS);

        click_offset = new float[2];
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        lessonView.setOnTouchListener(new View.OnTouchListener() {
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
                    int a = lessonView.getOffsetForPosition(X_Y[0], X_Y[1]);
                    int end = a;
                    int start = a;
                    if (a >= 0) {
                        click_cnt++;
                        String text = lessonView.getText().toString();
                        String tmp;
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
                                Setter.setContent(lessonView);
                            }
                        });

                        setSelectionInfo(tmp);
                    }
                }
                return true;
            }
        });
        lessonView.setMovementMethod(new ScrollingMovementMethod());

    }

    /**
     * @param lesson: LessonUnit container
     * @param mode:   Indicates the mode of display of the content
     */
    public void setLessonText(final LessonUnit lesson, int mode) {
        StringBuilder builder_fr = new StringBuilder();
        StringBuilder builder_zh = new StringBuilder();
        this.lineInfo = new ArrayList<>(); //
        int symbolCountFr = 0;
        int symbolCountZh = 0;
        for (int i = 0; i < lesson.getLineCnt(); i++) {
            String str_fr = lesson.getLine(i, LessonUnit.FR);
            String str_zh = lesson.getLine(i, LessonUnit.ZH);
            symbolCountFr += str_fr.length();
            symbolCountZh += str_zh.length();
            switch (mode) {
                case LessonActivity.MODE_NORMAL:
                    builder_fr.append(str_fr);
                    builder_zh.append(str_zh);
                    builder_fr.append("<br>");
                    builder_zh.append("<br>");
                    symbolCountFr++;
                    symbolCountZh++;
                    break;
                case LessonActivity.MODE_WITH_NUMBERS:
                    builder_fr.append(String.format(Locale.ENGLISH,
                            "<font color='red'>(%d)</font>%s", i, str_fr));
                    builder_zh.append(String.format(Locale.ENGLISH,
                            "<font color='red'>(%d)</font>%s", i, str_zh));
                    builder_fr.append("<br>");
                    builder_zh.append("<br>");
                    symbolCountFr++;
                    symbolCountZh++;
                    break;
                case LessonActivity.MODE_NO_LINE_RETURN:
                    builder_fr.append(str_fr);
                    builder_zh.append(str_zh);
                    break;
                default:
            }
            Integer[] lineInfoUnit = {symbolCountFr, symbolCountZh, 0};
            lineInfo.add(lineInfoUnit);

        }

        Spanned text;
        Spanned text_zh;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            text = Html.fromHtml(builder_fr.toString().trim(),
                    Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);

            text_zh = Html.fromHtml(builder_zh.toString().trim(),
                    Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        } else {
            text = Html.fromHtml(builder_fr.toString().trim());
            text_zh = Html.fromHtml(builder_zh.toString().trim());
        }
        this.lessonView.setText(text, TextView.BufferType.SPANNABLE);
        this.lessonView.setTextSize(23);
        this.tvFullTranslation.setText(text_zh, TextView.BufferType.SPANNABLE);

        this.explanationSide.setOrientation(LinearLayout.VERTICAL);


        this.lessonView.post(new Runnable() {
            @Override
            public void run() {

                final HashMap<Integer, ArrayList<Integer>> lineToSentenceMap;
                lineToSentenceMap = new HashMap<>();
                explanationMap = lesson.getExplanations();
                for (int key : explanationMap.keySet()) {
                    int line = lessonView.getLayout().getLineForOffset(lineInfo.get(key)[0]);
                    if (lineToSentenceMap.containsKey(line)) {
                        ArrayList<Integer> tmp = lineToSentenceMap.get(line);
                        tmp.add(key);
                    } else {
                        ArrayList<Integer> tmp = new ArrayList<>();
                        tmp.add(key);
                        lineToSentenceMap.put(line, tmp);
                    }
                }
                Log.v("Line count: ", lessonView.getLineCount() + "");
                //explanationSide.setMinimumHeight(lessonView.getLineHeight()*lessonView.getLineCount());
                for (int i = 0; i < lessonView.getLineCount(); i++) {
                    IndexedButton btn = new IndexedButton(getBaseContext(), null);
                    LinearLayout.LayoutParams params =
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                    lessonView.getLineHeight());
                    params.setMargins(5, 0, 5, 0);
                    btn.setLayoutParams(params);
                    btn.setTextSize(5);
                    if (lineToSentenceMap.containsKey(i)) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            btn.setBackground(getDrawable(R.drawable.button_floral_blue));
                        } else if (Build.VERSION.SDK_INT >= 16) {
                            btn.setBackground(getResources().
                                    getDrawable(R.drawable.button_floral_blue));
                        } else if (Build.VERSION.SDK_INT < 16) {
                            btn.setBackgroundDrawable(getResources().
                                    getDrawable(R.drawable.button_floral_blue));
                        }
                        btn.setIndex(lineToSentenceMap.get(i));

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setExplanationPanel(((IndexedButton) v).getIndex());
                            }
                        });
                    }
                    btn.setText(String.valueOf(i));
                    //btn.setHeight();
                    btn.setWidth(30);
                    explanationSide.addView(btn);
                    //this.lineInfo.get(i)[2] = this.lessonView.get

                }
            }
        });


        if (lesson.getType().equals("letter")) {

            /* Align extremities */
            Spannable letterSpan = (Spannable) lessonView.getText();
            int len = lessonView.getText().length();

            letterSpan.setSpan((new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE)),
                    0,
                    lesson.getLine(0, LessonUnit.FR).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            letterSpan.setSpan((new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE)),
                    len - 10 - lesson.getLine(lesson.getLineCnt() - 1, LessonUnit.FR).length(),
                    len - 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            Spannable letterSpanZh = (Spannable) tvFullTranslation.getText();
            int lenZh = letterSpanZh.length();
            letterSpanZh.setSpan((new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE)),
                    0,
                    lesson.getLine(0, LessonUnit.ZH).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            letterSpanZh.setSpan((new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE)),
                    lenZh - 10 - lesson.getLine(lesson.getLineCnt() - 1, LessonUnit.FR).length(),
                    lenZh - 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        /*--------------------------------------------------------*/
        /* Is triggered when a sentence is finished to be spoken, */
        /* Set the next to speak and update the lessonView        */
        /*--------------------------------------------------------*/
        if (isSpeaking) {
            String str = ((TextView) findViewById(R.id.lesson_view)).getText().toString();
            if (speakCurrentPos >= str.length()) {
                isSpeaking = false;
                return;
            }
            char c = str.charAt(speakCurrentPos);
            while (Checker.isSymbolOutsideSentence(c)) {
                ttsObj.speak(String.valueOf(c), TextToSpeech.QUEUE_ADD, null);
                ++speakCurrentPos;
                c = str.charAt(speakCurrentPos);
                if (speakCurrentPos >= str.length()) {
                    isSpeaking = false;
                    return;
                }
            }
            LessonState.curSpeakingPortionStartIndex = speakCurrentPos;
            String utterance = "";

            while (Checker.isSymbolOutsideSentence(c) == false) {
                utterance += c;
                ++speakCurrentPos;
                c = str.charAt(speakCurrentPos);
                if (speakCurrentPos >= str.length()) {
                    isSpeaking = false;
                    return;
                }
            }

            LessonState.curSpeakingPortionEndIndex = speakCurrentPos;
            Log.e("ERR", "THE WORD : " + utterance);
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utterance);
            ttsObj.speak(utterance,
                    TextToSpeech.QUEUE_ADD, params);
            ttsObj.speak(String.valueOf(c), TextToSpeech.QUEUE_ADD, null);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Setter.setContent(lessonView);
                }
            });

        }
    }

    /**
     * Fetch information in database about the word
     *
     * @param selectedWord
     */
    public void setSelectionInfo(String selectedWord) {
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
        ArrayList<HashMap<String, String>> sub;
        ArrayList<HashMap<String, String>> subRes = new ArrayList<>();
        sub = dbHelper.getTransList_byPattern(selectedWord,
                true, true, true, DBHelper.RESULT_EXACT);
        if (sub != null) subRes.addAll(sub);
        if (selectedWord.endsWith("ve")) {
            /* Check for adjectif f -> ve (fem.) */
            String mod1 = selectedWord.substring(0, selectedWord.length() - 2) + "f";
            sub = dbHelper.getTransList_byPattern(mod1,
                    false, false, true, DBHelper.RESULT_EXACT);
            if (sub != null) subRes.addAll(sub);
        } else if (selectedWord.endsWith("s")) {
            /* check for pluriel */
            String mod2 = selectedWord.substring(0, selectedWord.length() - 1);
            sub = dbHelper.getTransList_byPattern(mod2,
                    true, false, true, DBHelper.RESULT_EXACT);
            if (sub != null) subRes.addAll(sub);
        } else if (selectedWord.endsWith("euse")) {
            /* eur -> euse */
            String mod3 = selectedWord.substring(0, selectedWord.length() - 2) + "r";
            sub = dbHelper.getTransList_byPattern(mod3,
                    true, false, false, DBHelper.RESULT_EXACT);
            if (sub != null) subRes.addAll(sub);
            /* eux -> euse */
            mod3 = selectedWord.substring(0, selectedWord.length() - 2) + "x";
            sub = dbHelper.getTransList_byPattern(mod3,
                    false, false, true, DBHelper.RESULT_EXACT);
            if (sub != null) subRes.addAll(sub);
        } else if (selectedWord.endsWith("trice")) {
            String mod4 = selectedWord.substring(0, selectedWord.length() - 4) + "eur";
            sub = dbHelper.getTransList_byPattern(mod4,
                    true, false, false, DBHelper.RESULT_EXACT);
            if (sub != null) subRes.addAll(sub);
        }


        //Search for conjugated verb if nothing was found
        ArrayList<String> ar = this.conj.getInfinitivesToCheck(selectedWord);
        for (String potentialVerb : ar) {
            sub = dbHelper.getTransList_byPattern(potentialVerb,
                    false, true, false, DBHelper.RESULT_EXACT);
            if (sub != null) {
                subRes.addAll(sub);
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


    private class InTheBackground extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
            return dbHelper.getTransList_byPattern(params[0],
                    false, true, false, DBHelper.RESULT_EXACT);
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            if (result != null) {
                for (HashMap<String, String> mapRes : result) {
                    EntryElement ee = Retriever.createEntryElementFromMap(context, mapRes, count);
                    ll.addView(ee);
                }
            }
        }
    }

    /**
     * @param indexes : Indexes  of the sentence that has been required for explan.
     */
    private void setExplanationPanel(ArrayList<Integer> indexes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indexes.size(); i++) {
            sb.append(this.explanationMap.get(indexes.get(i)));
        }

        //explanationView
        Spanned text;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            text = Html.fromHtml(sb.toString().trim(),
                    Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);

        } else {
            text = Html.fromHtml(sb.toString().trim());
        }
        this.explanationView.setText(text, TextView.BufferType.SPANNABLE);
        this.infoFlipper.setDisplayedChild(1);
    }


}


class IndexedButton extends Button {
    public ArrayList<Integer> indexes = null;

    public IndexedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ArrayList<Integer> getIndex() {
        return this.indexes;
    }

    public void setIndex(ArrayList<Integer> indexes) {
        this.indexes = indexes;
    }
}
