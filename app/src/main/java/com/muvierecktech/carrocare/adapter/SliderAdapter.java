package com.muvierecktech.carrocare.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.model.SliderList;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends PagerAdapter {

    private Context context;
    //    private List<SliderModel.Slider> newsList;
    private LayoutInflater layoutInflater;
//    private List<Slider> image;
    List<SliderList.Slider> sliders;

    public SliderAdapter(Context context, List<SliderList.Slider> car) {
        this.context = context;
        this.sliders = car;
        layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return sliders.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.item_slider, container, false);

        ImageView carImg = (ImageView) itemView.findViewById(R.id.car_img);
       // ImageView progress = (ImageView) itemView.findViewById(R.id.progress_loader);
//        TextView newsText = (TextView) itemView.findViewById(R.id.news_txt);
//        progress.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(sliders.get(position).simage)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(carImg);

        /*Set animated progressbar*/

//        Picasso.get()
//                .load(R.drawable.progress_animation)
//                .placeholder(R.drawable.progress_animation)
//                .into(progress);
//        final ImageView progressBar = progress;
//        Picasso.get()
//                .load(image.get(position).simage)
//                .placeholder(R.drawable.defaultbanner)
//                .memoryPolicy(MemoryPolicy.NO_CACHE)
//                .networkPolicy(NetworkPolicy.NO_CACHE)
//                .into(newsImg, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        progressBar.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//
//                    }
//                });
        container.addView(itemView,0);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}