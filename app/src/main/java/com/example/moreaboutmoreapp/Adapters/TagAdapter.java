package com.example.moreaboutmoreapp.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TagAdapter extends ArrayAdapter<String> {
    private Typeface font;
    private Context context;

    public TagAdapter(Context context, int resource, List<String> objects, Typeface font) {
        super(context, resource, objects);
        this.font = font;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        if (v instanceof TextView) {
            ((TextView) v).setTypeface(font);
        }
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        if (v instanceof TextView) {
            ((TextView) v).setTypeface(font);
        }
        return v;
    }
}
