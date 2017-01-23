package com.librefra.daoliangshu.librefra.activity_manager;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.lf_lesson.WaveSet;
import com.librefra.daoliangshu.librefra.tools.Retriever;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by daoliangshu on 2016/10/20.
 * Interface for choosing a activity from a lesson
 * Should be given the name of the file in which to search
 */
public class ActivityChooser extends Activity {
    ArrayList<WaveSet> activityList;
    ScrollView mainScroll;
    LinearLayout scrollInnerLayout;
    String[] sortWay;
    int currentSortWay = 0;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_selection);
        mainScroll = (ScrollView) findViewById(R.id.acti_selection_scrolview);
        activityList = new ArrayList<>();
        InputStream is;
        String[] list = null;
        scrollInnerLayout = (LinearLayout) findViewById(R.id.scrollInnerLayout);


        try {
            list = getAssets().list("lessons");
        } catch (IOException io) {
            io.printStackTrace();
        }
        if (list == null) return;
        for (String fn : list) {
            String myContent = "";
            try {
                is = getAssets().open("lessons/" + fn);
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String str2;

                while ((str2 = br.readLine()) != null) {
                    myContent += str2;
                }
                br.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            new InTheBackground().execute(myContent, fn);
        }
        sortWay = getResources().getStringArray(R.array.lesson_search_way);
        final Spinner spinSearch = (Spinner) findViewById(R.id.
                act_choose_sort_spinner);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.lesson_search_way,
                        R.layout.spinner_item_type1);
        adapter.setDropDownViewResource(R.layout.spinner_item_type1);
        spinSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = spinSearch.getSelectedItem().toString();
                for (int i = 0; i < sortWay.length; i++) {
                    if (str.equals(sortWay[i])) {
                        final Spinner subSearch = (Spinner) findViewById(R.id.act_choose_sub_sort_spinner);
                        switch (i) {
                            case 0:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        subSearch.setEmptyView(null);
                                        subSearch.setEnabled(false);

                                        ArrayList<WaveSet> res = new ArrayList<>();
                                        for (int i = 0; i < activityList.size(); i++) {
                                            res.add(activityList.get(i));
                                        }
                                        set(res);
                                    }
                                });
                                break;
                            case 1:
                                        /*----------Sort by Level-------------*/
                                ArrayAdapter<String> gameKindArray =
                                        new ArrayAdapter<>(getBaseContext(),
                                                R.layout.spinner_item_type1,
                                                getResources().getStringArray(R.array.level_list));
                                gameKindArray.setDropDownViewResource(R.layout.spinner_item_type1);
                                subSearch.setAdapter(gameKindArray);
                                subSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ArrayList<WaveSet> res = new ArrayList<WaveSet>();
                                                subSearch.setEnabled(true);
                                                String selectedLevel = subSearch.
                                                        getSelectedItem().toString();
                                                for (int i = 0; i < activityList.size(); i++) {
                                                    String[] formated = activityList.get(i).getLessonChooserFormatedData().split(",");
                                                    if (formated[3].equals(selectedLevel)) {
                                                        res.add(activityList.get(i));
                                                    }
                                                }
                                                LinearLayout courseSelectionView2 = (LinearLayout) findViewById(R.id.scrollInnerLayout);
                                                if (res.size() <= 0) {
                                                    courseSelectionView2.removeAllViews();
                                                }
                                                set(res);
                                            }
                                        });


                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ArrayList<WaveSet> res = new ArrayList<WaveSet>();
                                                subSearch.setEnabled(false);
                                                for (int i = 0; i < activityList.size(); i++) {
                                                    res.add(activityList.get(i));
                                                }
                                                LinearLayout courseSelectionView2 = (LinearLayout) findViewById(R.id.scrollInnerLayout);
                                                if (res.size() <= 0) {
                                                    courseSelectionView2.removeAllViews();
                                                }
                                                set(res);
                                            }
                                        });
                                    }
                                });
                                break;
                            case 2:
                                        /*----------Sort by Custom Thematic-------------*/

                                /*ArrayAdapter<String> gameKindArray2=
                                        new ArrayAdapter<String>(getBaseContext(),
                                                R.layout.spinner_item_type1,
                                                listThematics.toArray(
                                                        new String [listThematics.size()]));
                                gameKindArray2.setDropDownViewResource(R.layout.spinner_item_type1);
                                subSearch.setAdapter(gameKindArray2);
                                subSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ArrayList<WaveSet> res = new ArrayList<WaveSet>();
                                                subSearch.setEnabled(true);
                                                String selectedThematic = subSearch.
                                                        getSelectedItem().toString();
                                                for(int i=0; i<activityList.size(); i++){
                                                    String[] formated = activityList.get(i).getLessonChooserFormatedData().split(",");
                                                    if(formated[4].equals(selectedThematic)){
                                                        res.add(activityList.get(i));
                                                    }
                                                }
                                                ListView courseSelectionView2 = (ListView)findViewById(R.id.listView);
                                                if(res.size() <= 0){
                                                    courseSelectionView2.setEmptyView(null);
                                                }
                                            }
                                        });


                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        return;
                                    }
                                });*/
                                break;

                        }
                        currentSortWay = i;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentSortWay = 0;
                return;
            }
        });
        spinSearch.setAdapter(adapter);


    }


    /**
     * Display activity information frames
     *
     * @param wsList list of wave sets
     */
    public void set(ArrayList<WaveSet> wsList) {
        if (wsList == null) return;
        //this.scrollInnerLayout.removeAllViews();
        for (int i = 0; i < wsList.size(); i++) {
            ActivityDisplayedEntry entry = new ActivityDisplayedEntry(getBaseContext());
            entry.set(wsList.get(i));
            this.scrollInnerLayout.addView(entry);
        }
    }


    private class InTheBackground extends AsyncTask<String, Void, ArrayList<WaveSet>> {
        @Override
        protected ArrayList<WaveSet> doInBackground(String... params) {
            return Retriever.getActivitiesListFromInputStream(getBaseContext(), params[0], params[1]);
        }

        @Override
        protected void onPostExecute(ArrayList<WaveSet> result) {
            if (result != null) activityList.addAll(result);
            set(result);
        }
    }
}




