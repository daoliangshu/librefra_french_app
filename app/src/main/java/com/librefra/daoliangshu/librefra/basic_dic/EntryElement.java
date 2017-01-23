package com.librefra.daoliangshu.librefra.basic_dic;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.main.DBHelper;
import com.librefra.daoliangshu.librefra.verb.ConjugaisonActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by daoliangshu on 2016/10/29.
 * Represents graphically an french entry from dictionary,
 * displaying informations concerning the word
 */

public class EntryElement extends RelativeLayout {
    TextView tvWord;
    TextView tvPhonetic;
    TextView tvGenre;
    TextView tvTrans1;
    TextView tvTrans2;
    TextView tvTrans3;

    static final int[] mapTransLayout = {R.id.dic_entry_trans1_layout,
            R.id.dic_entry_trans2_layout,
            R.id.dic_entry_trans3_layout};
    static final int[] mapInfosLayout = {
            R.id.dic_entry_info1,
            R.id.dic_entry_info2,
            R.id.dic_entry_info3
    };
    TextView tvTrans1Spec;
    TextView tvTrans2Spec;
    TextView tvTrans3Spec;

    Button btnVerbConjugDisplayHidden;
    Button btnInfoDisplay;
    boolean[] hasInfo = {false, false, false};
    boolean isInfoDisplay = false;
    boolean hasInfoToDisplay = false;

    public EntryElement(final Context c, int index) {
        super(c);
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.dic_entry_element, this, true);

        GradientDrawable border = new GradientDrawable();
        border.setColor(ContextCompat.getColor(c, R.color.darkBlue));
        border.setStroke(1, ContextCompat.getColor(c, R.color.lightGreen));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackgroundDrawable(border);
        } else {
            this.setBackground(border);
        }
        tvGenre = (TextView) findViewById(R.id.tvGenre);
        tvPhonetic = (TextView) findViewById(R.id.tvPhonetic);
        tvWord = (TextView) findViewById(R.id.tvWord);
        tvTrans1 = (TextView) findViewById(R.id.tvTrans1);
        tvTrans2 = (TextView) findViewById(R.id.tvTrans2);
        tvTrans3 = (TextView) findViewById(R.id.tvTrans3);
        tvWord.setText(String.valueOf(index));
        tvPhonetic.setText(String.valueOf(index));
        tvGenre.setText("Genre");
        tvTrans1Spec = (TextView) findViewById(R.id.tvTrans1Spec);
        tvTrans2Spec = (TextView) findViewById(R.id.tvTrans2Spec);
        tvTrans3Spec = (TextView) findViewById(R.id.tvTrans3Spec);
        btnVerbConjugDisplayHidden = (Button) findViewById(R.id.hidden_button);
        btnVerbConjugDisplayHidden.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent conjActivity = new Intent(getContext(), ConjugaisonActivity.class);
                conjActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                conjActivity.putExtra("verb", tvWord.getText().toString());
                getContext().startActivity(conjActivity);
            }
        });
        btnInfoDisplay = (Button) findViewById(R.id.dic_entry_info_button);
        btnInfoDisplay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInfoDisplay) {
                    int[] wid_id = {
                            R.id.dic_entry_info1_layout,
                            R.id.dic_entry_info2_layout,
                            R.id.dic_entry_info2_layout
                    };
                    for (int i = 0; i < 3; i++) {
                        if (hasInfo[i]) {
                            View infoView = findViewById(wid_id[i]);
                            if (infoView.getVisibility() == GONE) infoView.setVisibility(VISIBLE);
                            else infoView.setVisibility(GONE);
                        }
                    }

                }

            }
        });
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<HashMap<String, String>> res =
                        DBHelper.getInstance(c).getSentences_by_keywords(getWord());
                for (int i = 0; i < res.size(); i++) {
                    Log.i("SEN_FR: ", res.get(i).get(DBHelper.SENTENCE_FR));
                    Log.i("SEN_ZH: ", res.get(i).get(DBHelper.SENTENCE_ZH));
                }
            }
        });
    }

    /*----------------------------------*/
    /*------------Setters---------------*/
    /*----------------------------------*/
    public void setHiddenButtonVisibility(boolean state) {
        if (!state) {
            this.btnVerbConjugDisplayHidden.setVisibility(GONE);
        } else {
            this.btnVerbConjugDisplayHidden.setVisibility(VISIBLE);
        }
    }

    public void setGenre(String genre) {
        this.tvGenre.setText(genre);
    }

    public void setWord(String word) {
        this.tvWord.setText(word);
    }

    public void setPhonetic(String phonetic) {
        this.tvPhonetic.setText(phonetic);
    }

    public void setTrans(String transFromated) {
        this.tvTrans1.setText(transFromated);
    }

    public void setTrans(int index, String str) {
        if (index < 0 || index > 2) {
            return;
        }
        String tmp = str == null || str.equals("") ? "" : "(" + (index + 1) + ") " + str;
        boolean needDisplay = true;
        if (tmp.equals("")) needDisplay = false;
        if (!needDisplay) {
            /* Do not display empty parts */
            View transView = findViewById(mapTransLayout[index]);
            transView.setVisibility(GONE);
        }
        switch (index) {
            case 0:
                this.tvTrans1.setText(tmp);
                break;
            case 1:
                this.tvTrans2.setText(tmp);
                break;
            case 2:
                this.tvTrans3.setText(tmp);
                break;
            default:
        }
    }

    public void setInfos(String str) {
        if (str == null) {
            hasInfoToDisplay = false;
            Button btnInfos = (Button) findViewById(R.id.dic_entry_info_button);
            btnInfos.setVisibility(GONE);
            btnInfos.setEnabled(false);
            return;
        }
        String[] infos = str.split(";");
        hasInfoToDisplay = false;
        for (int i = 0; i < infos.length; i++) {
            if (i >= 3) break; // Only 3 bucket for trans
            if (!infos[i].trim().equals("")) {
                ArrayList<String> cutInfo = cutInfo(infos[i]);
                ArrayList<String> toPutInInfo = new ArrayList<>();
                String typeInfo = "";
                for (String item : cutInfo) {
                    int start = -1;
                    if (item.length() > 3 && item.charAt(0) == '(') {

                        switch (item.charAt(1)) {
                            case '!':
                                if (item.charAt(2) == '=') start = 3;
                                else start = 2;
                                toPutInInfo.add("相反詞: " + item.substring(start, item.length()) + "；");
                                break;
                            case '=':
                                start = 2;
                                toPutInInfo.add("同義詞: " + item.substring(start, item.length() - 1) + "；");
                                break;
                            case 'a':
                                if (item.contains("adj.")) {
                                    if (item.contains("subst")) {
                                        typeInfo += "[名詞]";
                                    } else if (item.contains("masc")) {
                                        typeInfo += "[陽性形容詞]";
                                    } else if (item.contains("indef.")) {
                                        typeInfo += "[不定形容詞]";
                                    } else if (item.contains("inv.")) {
                                        typeInfo += "[不定形容詞]";
                                    }
                                } else {
                                    toPutInInfo.add(item);
                                }
                                break;
                            case 'f':
                                if (item.contains("fem:")) {
                                    start = item.indexOf(':');
                                    if (start < item.length() - 2) start++;
                                    toPutInInfo.add("陰性爲=" + item.substring(start, item.length() - 1) + ";");
                                } else {
                                    toPutInInfo.add(item);
                                }
                                break;
                            case 'm':
                                if (item.contains("masc:")) {
                                    start = item.indexOf(':');
                                    if (start < item.length() - 2) start++;
                                    toPutInInfo.add("陽性爲=" + item.substring(start, item.length() - 1) + ";");
                                } else {
                                    toPutInInfo.add(item);
                                }
                                break;
                            case 'e':
                                if (item.contains("ety.")) {
                                    start = item.indexOf('.');
                                    if (item.length() - 2 > start) {
                                        toPutInInfo.add("ety:" +
                                                item.substring(start + 1, item.length() - 1) + ";");
                                    }
                                } else {
                                    toPutInInfo.add(item);
                                }
                                break;
                            default:
                                toPutInInfo.add(item);
                        }
                    } else if (item.length() > 3 && item.charAt(0) == '{') {
                        switch (item.charAt(1)) {
                            case 'a':
                                if (item.contains("adj.indef")) {
                                    typeInfo += "[不定形容詞]";
                                } else if (item.contains("algo.")) {
                                    typeInfo += "[演算法]";
                                } else if (item.contains("adj.subst.")) {
                                    typeInfo += "[名詞]";
                                } else {
                                    typeInfo += "[" + item.substring(1, item.length() - 1) + "]";
                                }
                                break;
                            case 'c':
                                if (item.contains("conj.")) {
                                    typeInfo += "[conjonction]";
                                } else {
                                    typeInfo += "[" + item.substring(1, item.length() - 1) + "]";
                                }
                                break;
                            case 'f':
                                if (item.contains("fem.")) {
                                    typeInfo += "[陰性]";
                                } else if (item.contains("fem:")) {
                                    start = item.indexOf(':');
                                    if (start < item.length() - 2) start++;
                                    toPutInInfo.add("陰性爲=" + item.substring(start, item.length() - 1) + ";");
                                } else {
                                    typeInfo += "[" + item.substring(1, item.length() - 1) + "]";
                                }
                                break;
                            case 'p':
                                if (item.contains("pron.pers.")) {
                                    typeInfo += "[人稱代名詞]";
                                } else if (item.contains("polit.")) {
                                    typeInfo += "[政治]";
                                } else if (item.contains("prep.")) {
                                    typeInfo += "[介系詞]";
                                } else if (item.contains("temp.")) {
                                    typeInfo += "[時間]";
                                } else {
                                    typeInfo += "[" + item.substring(1, item.length() - 1) + "]";
                                }
                                break;
                            case 's':
                                if (item.contains("subst.masc.")) {
                                    typeInfo += "[陽性名詞]";
                                } else {
                                    typeInfo += "[" + item.substring(1, item.length() - 1) + "]";
                                }
                                break;

                            case 'r':
                                if (item.contains("relig.")) {
                                    typeInfo += "[宗教]";
                                } else {
                                    typeInfo += "[" + item.substring(1, item.length() - 1) + "]";
                                }
                                break;
                            case 'l':
                                if (item.contains("loca.")) {
                                    typeInfo += "[地方]";
                                } else {
                                    typeInfo += "[" + item.substring(1, item.length() - 1) + "]";
                                }
                                break;
                            case 'm':
                                if (item.contains("math.")) {
                                    typeInfo += "[數學]";
                                } else if (item.contains("masc:")) {
                                    start = item.indexOf(':');
                                    if (start < item.length() - 2) start++;
                                    toPutInInfo.add("陽性爲=" + item.substring(start, item.length() - 1) + ";");
                                } else {
                                    typeInfo += "[" + item.substring(1, item.length() - 1) + "]";
                                }
                                break;
                            case 'i':
                                if (item.contains("intego.")) {
                                    typeInfo += "[疑問]";
                                } else {
                                    typeInfo += "[" + item.substring(1, item.length() - 1) + "]";
                                }
                                break;
                            default:
                                typeInfo += "[" + item.substring(1, item.length() - 1) + "]";
                        }
                    } else {
                        if (!item.trim().equals("")) {
                            toPutInInfo.add(item + ";");
                        }
                    }
                }
                setSpec(i, typeInfo);
                TextView infoView = (TextView) findViewById(mapInfosLayout[i]);
                String res = "";
                for (int k = 0; k < toPutInInfo.size(); k++) {
                    if (!toPutInInfo.get(k).equals("") && !toPutInInfo.get(k).trim().equals("")) {
                        hasInfoToDisplay = true;
                        hasInfo[i] = true;
                        res += toPutInInfo.get(k);
                    }
                }
                if (hasInfo[i]) {
                    infoView.setText(res);
                }
            }
        }
        if (!hasInfoToDisplay) {
            Button btnInfos = (Button) findViewById(R.id.dic_entry_info_button);
            btnInfos.setVisibility(GONE);
        }

    }

    public void setSpec(int index, String str) {
        if (index < 0 || index > 2 || str.equals("")) return;
        switch (index) {
            case 0:
                this.tvTrans1Spec.setText(str);
                break;
            case 1:
                this.tvTrans2Spec.setText(str);
                break;
            case 2:
                this.tvTrans3Spec.setText(str);
                break;
            default:
        }

    }

    private ArrayList<String> cutInfo(String rawInfoUnit) {
        ArrayList<String> res = new ArrayList<>();
        boolean isClosed = true;
        char neededChar = '*';
        int start = -1;
        String outsideString = "";
        for (int i = 0; i < rawInfoUnit.length(); i++) {
            char c = rawInfoUnit.charAt(i);
            if (isClosed) {
                if (c == '{' || c == '(') {
                    isClosed = false;
                    start = i;
                    if (c == '{') neededChar = '}';
                    else neededChar = ')';
                } else {
                    outsideString += c;
                }
            } else {
                if (c == neededChar) {
                    res.add(rawInfoUnit.substring(start, i + 1));
                    isClosed = true;
                }
            }
        }
        if (!outsideString.equals("")) {
            res.add(outsideString);
        }
        return res;
    }

    public void appendSpec(int index, String str) {
        if (index < 0 || index > 2 || str.equals("")) return;
        switch (index) {
            case 0:
                this.tvTrans1Spec.setText(this.tvTrans1Spec.getText() + "[" + str + "]");
                break;
            case 1:
                this.tvTrans2Spec.setText(this.tvTrans2Spec.getText() + "[" + str + "]");
                break;
            case 2:
                this.tvTrans3Spec.setText(this.tvTrans3Spec.getText() + "[" + str + "]");
                break;
            default:
        }
    }


    public void appendTrans(String altTrans) {
        int index = 1;
        String res = "";
        if (altTrans != null && altTrans != "") {
            String t = this.getTrans();
            while (t.contains("(" + index + ")")) {
                index += 1;
            }
            if (index == 1) {
                if (t.equals("") == false) res += "(" + index + ") " + t;
                else index -= 1;
            }
            res += "; (" + (index + 1) + ") " + altTrans;
            this.setTrans(res);
        }
    }


    public String getTrans() {
        return this.tvTrans1.getText().toString();
    }

    public String getWord() {
        return this.tvWord.getText().toString();
    }
}
