package com.librefra.daoliangshu.librefra.lf_lesson;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.librefra.daoliangshu.librefra.R;

/**
 * Created by daoliangshu on 12/5/16.
 * Graphical representation of a lesson
 */
class LessonItemAdapter extends BaseAdapter {

    Context context;
    String[] data;
    private static LayoutInflater inflater = null;

    public LessonItemAdapter(Context context, String[] data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.lesson_chooser_list_row, null);

        final ViewFlipper vf = (ViewFlipper) vi.findViewById(R.id.leschoos_row_title_flipper);
        final TextView text = (TextView) vi.findViewById(R.id.leschoos_row_title_fr);
        final String[] itemData = data[position].split(",");
        text.setText(itemData[0]);

        TextView textZh = (TextView) vi.findViewById(R.id.leschoos_row_title_zh);
        textZh.setText(itemData[1]);
        vf.setDisplayedChild(0);
        vf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*--- Flip between title language display when click on title ---*/
                int vid = vf.getDisplayedChild();
                vf.setDisplayedChild((vid + 1) % 2);
            }
        });
        TextView textViewFilename = (TextView) vi.findViewById(R.id.leschoos_row_filename);
        textViewFilename.setText(itemData[2]);


        Button btnStart = (Button) vi.findViewById(R.id.leschoos_row_btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), LessonActivity.class);
                Log.i("filename", itemData[2]);
                intent.putExtra("lesson", itemData[2]);
                v.getContext().startActivity(intent);
            }
        });
        return vi;
    }
}