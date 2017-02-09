package com.librefra.daoliangshu.librefra.activity_manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.librefra.daoliangshu.librefra.R;
import com.librefra.daoliangshu.librefra.basic_dic.BasicDictionaryActivity;
import com.librefra.daoliangshu.librefra.lf_lesson.LessonChooserActivity;
import com.librefra.daoliangshu.librefra.lf_reader.FrenchReader;
import com.librefra.daoliangshu.librefra.tools.GlobalSettingActivity;
import com.librefra.daoliangshu.librefra.verb.ConjugaisonActivity;
import com.librefra.daoliangshu.librefra.vocab.VocabularyActivity;

/**
 * Created by daoliangshu on 2/9/17.
 */

public class MenuButtonFragment extends Fragment {
    public static final int PAGE_COUNT = 7;
    ViewGroup rootView;
    int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.menu_button_fragment, container, false);
        Button mButton = (Button) rootView.findViewById(R.id.menu_button);
        int stringId[] = {
                R.string.menu_vocabulary,
                R.string.menu_multiplechoices,
                R.string.menu_dictionary,
                R.string.menu_lesson,
                R.string.menu_conjugaison,
                R.string.menu_reader,
                R.string.menu_settings,

                R.string.menu_quit
        };
        mButton.setText(getResources().getString(stringId[position]));
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToStart = null;
                switch (position) {
                    case 0:
                        intentToStart = new Intent(getActivity().getApplicationContext(),
                                VocabularyActivity.class);
                        break;
                    case 1:
                        intentToStart = new Intent(getActivity().getApplicationContext(),
                                ActivityChooser.class);
                        break;
                    case 2:
                        intentToStart = new Intent(getActivity().getApplicationContext(),
                                BasicDictionaryActivity.class);
                        break;
                    case 3:
                        intentToStart = new Intent(getActivity().getApplicationContext(),
                                LessonChooserActivity.class);
                        break;
                    case 4:
                        intentToStart = new Intent(getActivity().getApplicationContext(),
                                ConjugaisonActivity.class);
                        break;
                    case 5:
                        intentToStart = new Intent(getActivity().getApplicationContext(),
                                FrenchReader.class);
                        break;
                    case 6:
                        intentToStart = new Intent(getActivity().getApplicationContext(),
                                GlobalSettingActivity.class);
                        break;
                    default:
                        getActivity().finish();
                        break;
                }
                getActivity().startActivity(intentToStart);
            }
        });


        return rootView;
    }

    public void scaleImage(float scaleX) {
        rootView.setScaleY(scaleX);
        rootView.invalidate();
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
