package com.librefra.daoliangshu.librefra.tools;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.librefra.daoliangshu.librefra.R;

import java.util.List;

/**
 * Created by gigitintin on 29/06/16.
 * List adapter used for drawer
 */
public class DrawerListAdapter extends ArrayAdapter {
    private Context mContext;
    private int id;
    private List<String> items;

    public DrawerListAdapter(Context context, int textViewResourceId, List<String> list) {
        super(context, textViewResourceId, list);
        mContext = context;
        id = textViewResourceId;
        items = list;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        View mView = v;
        if (mView == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        TextView text = (TextView) mView.findViewById(R.id.textView);

        if (items.get(position) != null) {
            text.setTextSize(32);
            text.setTextColor(Color.WHITE);
            text.setText(items.get(position));
            int color = Color.argb(200, 255, 64, 64);
            text.setBackgroundColor(color);

        }

        return mView;
    }

    public String getText(int index) {
        if (index < items.size()) {
            return items.get(index);
        }
        return null;
    }
}
