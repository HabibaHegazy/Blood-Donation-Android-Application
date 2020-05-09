package com.example.blood_donation_project.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.blood_donation_project.R;

public class DonorMenuImagesAdapter extends PagerAdapter {

    private int[] images_resources = {R.drawable.project_logo, R.drawable.project_logo};
    private Context ctx;
    private LayoutInflater layoutInflater;

    public DonorMenuImagesAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return images_resources.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view==(LinearLayout)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.donor_menu_image_layout,container,false);
        ImageView imageView = item_view.findViewById(R.id.image_view);
        imageView.setImageResource(images_resources[position]);
        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
