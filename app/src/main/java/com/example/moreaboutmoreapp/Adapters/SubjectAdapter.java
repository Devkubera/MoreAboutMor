package com.example.moreaboutmoreapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moreaboutmoreapp.Models.SubjectModel;
import com.example.moreaboutmoreapp.R;

import java.util.ArrayList;
import java.util.List;

public class SubjectAdapter extends ArrayAdapter<SubjectModel> {

    public SubjectAdapter(Context context, ArrayList<SubjectModel> subject) {
        super(context, 0, subject);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        SubjectModel subjectModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_sample_view, parent, false);
        }

        TextView tvCode = (TextView) convertView.findViewById(R.id.tvSubjectCode);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvSubjectNane);

        tvCode.setText(subjectModel.getPasscode() + " : " + subjectModel.getName());
        //tvName.setText(subjectModel.getName());

        //return super.getView(position, convertView, parent);
        return convertView;
    }

    public SubjectAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public SubjectAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public SubjectAdapter(@NonNull Context context, int resource, @NonNull SubjectModel[] objects) {
        super(context, resource, objects);
    }

    public SubjectAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull SubjectModel[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public SubjectAdapter(@NonNull Context context, int resource, @NonNull List<SubjectModel> objects) {
        super(context, resource, objects);
    }

    public SubjectAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<SubjectModel> objects) {
        super(context, resource, textViewResourceId, objects);
    }

}
