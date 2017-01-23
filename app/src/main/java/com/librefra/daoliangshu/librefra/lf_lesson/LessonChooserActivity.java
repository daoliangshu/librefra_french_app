package com.librefra.daoliangshu.librefra.lf_lesson;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.librefra.daoliangshu.librefra.R;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by daoliangshu on 2016/9/21.
 * Activity for choosing lessons
 */
public class LessonChooserActivity extends Activity implements AdapterView.OnItemClickListener {
    private String[] sortWay;
    private int currentSortWay = 0;
    ArrayList<String> lessonList = null;
    ArrayList<String> listThematics = null;
    /* maps index to the correct file :
        Note that one lesson file cannot have two units with
        the same name.
        <lesson>
            <unit>
                <title>...</title> <---unique for a file, but can be repeated outside
                ...
            </unit>
            ...
        </lesson>
     */
    ArrayList<String> choiceMapping = null;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.lesson_chooser_layout);
        final ListView courseSelectionView = (ListView) findViewById(R.id.listView);
        courseSelectionView.setOnItemClickListener(this);
        lessonList = new ArrayList<>();
        choiceMapping = new ArrayList<>();

        try {
            listThematics = new ArrayList<>();
            String[] list = getAssets().list("lessons");
            for (String fn : list) {
                LessonSet cc = new LessonSet(getAssets().open("lessons/" + fn), fn, getBaseContext());
                ArrayList<String> tmpList = cc.getLessonChooserFormatedData();
                for (String tle : tmpList) {
                    choiceMapping.add(fn);
                    lessonList.add(tle);
                    /*Retrieve all possible custom distinct thematics*/
                    if (!listThematics.contains(tle.split(",")[4])) {
                        listThematics.add(tle.split(",")[4]);
                    }
                }
                Log.e("LESSON", fn);
            }
            sortWay = getResources().getStringArray(R.array.lesson_search_way);
            LessonItemAdapter lia = new LessonItemAdapter(this,
                    this.lessonList.toArray(new String[this.lessonList.size()]));

            courseSelectionView.setAdapter(lia);

            final Spinner spinSearch = (Spinner) findViewById(R.id.
                    spin_lessonSearch);
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
                            final Spinner subSearch = (Spinner) findViewById(R.id.spin_lessonSearch2);
                            switch (i) {
                                case 0:
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            subSearch.setEmptyView(null);
                                            subSearch.setEnabled(false);

                                            ArrayList<String> res = new ArrayList<>();
                                            for (int i = 0; i < lessonList.size(); i++) {
                                                res.add(lessonList.get(i));
                                            }
                                            ListView courseSelectionView2 = (ListView) findViewById(R.id.listView);

                                            if (res.size() <= 0) {
                                                courseSelectionView2.setEmptyView(null);
                                            }
                                            LessonItemAdapter lia = new LessonItemAdapter(getBaseContext(),
                                                    res.toArray(new String[res.size()]));
                                            courseSelectionView2.setAdapter(lia);
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
                                                    ArrayList<String> res = new ArrayList<String>();
                                                    subSearch.setEnabled(true);
                                                    String selectedLevel = subSearch.
                                                            getSelectedItem().toString();
                                                    for (int i = 0; i < lessonList.size(); i++) {
                                                        String[] formated = lessonList.get(i).split(",");
                                                        if (formated[3].equals(selectedLevel)) {
                                                            res.add(lessonList.get(i));
                                                        }
                                                    }
                                                    ListView courseSelectionView2 = (ListView) findViewById(R.id.listView);
                                                    if (res.size() <= 0) {
                                                        courseSelectionView2.setEmptyView(null);
                                                    }
                                                    LessonItemAdapter lia = new LessonItemAdapter(getBaseContext(),
                                                            res.toArray(new String[res.size()]));
                                                    courseSelectionView2.setAdapter(lia);
                                                }
                                            });


                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                            return;
                                        }
                                    });
                                    break;
                                case 2:
                                        /*----------Sort by Custom Thematic-------------*/

                                    ArrayAdapter<String> gameKindArray2 =
                                            new ArrayAdapter<>(getBaseContext(),
                                                    R.layout.spinner_item_type1,
                                                    listThematics.toArray(
                                                            new String[listThematics.size()]));
                                    gameKindArray2.setDropDownViewResource(R.layout.spinner_item_type1);
                                    subSearch.setAdapter(gameKindArray2);
                                    subSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ArrayList<String> res = new ArrayList<>();
                                                    subSearch.setEnabled(true);
                                                    String selectedThematic = subSearch.
                                                            getSelectedItem().toString();
                                                    for (int i = 0; i < lessonList.size(); i++) {
                                                        String[] formated = lessonList.get(i).split(",");
                                                        if (formated[4].equals(selectedThematic)) {
                                                            res.add(lessonList.get(i));
                                                        }
                                                    }
                                                    ListView courseSelectionView2 = (ListView) findViewById(R.id.listView);
                                                    if (res.size() <= 0) {
                                                        courseSelectionView2.setEmptyView(null);
                                                    }
                                                    LessonItemAdapter lia = new LessonItemAdapter(getBaseContext(),
                                                            res.toArray(new String[res.size()]));
                                                    courseSelectionView2.setAdapter(lia);
                                                }
                                            });


                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                            return;
                                        }
                                    });
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

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Log.i("ITEM_ID", "" + parent.getItemIdAtPosition(position));
        int item_id = (int) parent.getItemIdAtPosition(position);

        /*if(item_id >= 0 && item_id < this.choiceMapping.size()){
            Intent intent = new Intent(this , LessonActivity.class);
            String filename = this.choiceMapping.get(item_id);
            Log.i("filename", filename);
            intent.putExtra("lesson", filename.toString());
            startActivity(intent);
        }*/

    }


}
