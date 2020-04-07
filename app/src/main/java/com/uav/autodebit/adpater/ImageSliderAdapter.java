package com.uav.autodebit.adpater;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.uav.autodebit.R;

import java.util.List;

public class ImageSliderAdapter extends PagerAdapter {

    private Context context;
    private List<String> banners;


    public ImageSliderAdapter(Context context, List<String> banners) {
        this.context = context;
        this.banners = banners;
    }

    @Override
    public int getCount() {
        return banners.size();
    }



    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String img = banners.get(position);

        View view = inflater.inflate(R.layout.item_slider, null);

        ImageView imgView = (ImageView) view.findViewById(R.id.bannerImage);

        ImageView loadimage = (ImageView) view.findViewById(R.id.loadimage);
        loadimage.setVisibility(View.VISIBLE);
        imgView.setVisibility(View.GONE);

        Picasso.with(context)
                .load(img)
                .into(imgView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        imgView.setVisibility(View.VISIBLE);
                        loadimage.setVisibility(View.GONE);
                        //do smth when picture is loaded successfully
                    }
                    @Override
                    public void onError() {
                        imgView.setVisibility(View.GONE);
                        loadimage.setVisibility(View.VISIBLE);
                    }
                });

        container.addView(view);

        /*ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);*/

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        /*ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);*/
        container.removeView((View) object);
    }

}
