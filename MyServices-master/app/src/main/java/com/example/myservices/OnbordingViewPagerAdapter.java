package com.example.myservices;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


public class OnbordingViewPagerAdapter extends PagerAdapter {

    Context ctx;

    public OnbordingViewPagerAdapter(Context ctx) {
        this.ctx = ctx;
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.onbording, container, false);

        ImageView logo = view.findViewById(R.id.logo);

        TextView title = view.findViewById(R.id.title);

        Button next = view.findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Onbording.viewPager.setCurrentItem(position + 1);
            }
        });

        switch (position) {
            case 0:
                logo.setImageResource(R.drawable.images);
                title.setText("Fast reservation with technicians \n                  and craftsmen");
                next.setVisibility(View.VISIBLE);
                break;
            case 1:
                logo.setImageResource(R.drawable.onboarding2);
                title.setText("Fast reservation with technicians \n                  and craftsmen");
                next.setVisibility(View.VISIBLE);
                break;
            case 2:
                logo.setImageResource(R.drawable.onboarding);
                title.setText("Fast reservation with technicians \n                  and craftsmen");
                next.setVisibility(View.VISIBLE);
                next.setText("Done");
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(ctx, Login.class);
                        ctx.startActivity(intent);
                    }
                });
                break;

        }
        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
