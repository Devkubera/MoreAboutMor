package com.example.moreaboutmoreapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.moreaboutmoreapp.R;

public class FirstUserAdapter extends PagerAdapter {

    Context context;

    int images[] = {
            R.drawable.info_first,
            R.drawable.info_second,
            R.drawable.info_third,
            R.drawable.info_fourth
    };

    int bg[] = {
            R.drawable.bg_first,
            R.drawable.bg_second,
            R.drawable.bg_third,
            R.drawable.bg_fourth
    };

    // Generate Constructor
    public FirstUserAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    // ins
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout_info, container, false);

        ImageView slideImage = view.findViewById(R.id.imgInfo);
        RelativeLayout layout = view.findViewById(R.id.layout_slid_first_user);

        slideImage.setImageResource(images[position]);
        layout.setBackgroundResource(bg[position]);

        container.addView(view);

        return view;
    }

    // des
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout) object);

    }


}
