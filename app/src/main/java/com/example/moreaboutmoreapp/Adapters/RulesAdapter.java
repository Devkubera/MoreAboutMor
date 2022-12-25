package com.example.moreaboutmoreapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.moreaboutmoreapp.R;

public class RulesAdapter extends PagerAdapter {

    Context context;

    int images[] = {
            R.drawable.img_rule01,
            R.drawable.img_rule02,
            R.drawable.img_rule03,
            R.drawable.img_rule04,
            R.drawable.img_rule05,
            R.drawable.img_rule06,
            R.drawable.img_rule07,
    };

    public RulesAdapter (Context context) {

        this.context = context;

    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout, container, false);

        ImageView slideImage = (ImageView) view.findViewById(R.id.imgRule);
        slideImage.setImageResource(images[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((LinearLayout)object);


    }
}
