package com.muvierecktech.carrocare.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.CarWashActivity;
import com.muvierecktech.carrocare.activity.ConfirmFormActivity;
import com.muvierecktech.carrocare.activity.MainActivity;
import com.muvierecktech.carrocare.activity.DoorStepServiceActivity;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.model.PagerModel;

import java.util.List;
import java.util.Map;

public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {

    private List<PagerModel> pagerModelList;
    private LayoutInflater layoutInflater;
    private Context context;


    public PagerAdapter(List<PagerModel> pagerModelList, Context context) {
        this.pagerModelList = pagerModelList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return pagerModelList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_viewpager, container, false);

        ImageView imageView;
        TextView textView;
        CardView cardView;

        cardView = view.findViewById(R.id.card);
        imageView = view.findViewById(R.id.image);
        textView = view.findViewById(R.id.title);

        imageView.setImageResource(pagerModelList.get(position).getImage());
        textView.setText(pagerModelList.get(position).getTitle());


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView.getText().toString().equalsIgnoreCase("Door step car wash")) {
                    ((DoorStepServiceActivity) context).showPopup("carwash");
//                    Constant.ONETIME_PACK_TYPE = "Door step car wash";
//                    Constant.ONETIME_SERVICE_TYPE = "Door step Wash";
                } else if (textView.getText().toString().equalsIgnoreCase("Detailing")) {
                    ((DoorStepServiceActivity) context).showPopup("detailing");
//                    Constant.ONETIME_PACK_TYPE = "Door step bike wash";
//                    Constant.ONETIME_SERVICE_TYPE = "Door step Wash";
                } else if (textView.getText().toString().equalsIgnoreCase("Painting & Denting")) {
                    ((DoorStepServiceActivity) context).showPopup("painting");
//                    Constant.ONETIME_PACK_TYPE = "Door step wax polish";
//                    Constant.ONETIME_SERVICE_TYPE = "Door step AddOn";
                } else if (textView.getText().toString().equalsIgnoreCase("Battery change")) {
                    ((DoorStepServiceActivity) context).showPopup("battery");
//                    Constant.ONETIME_PACK_TYPE = "Door step wax polish";
//                    Constant.ONETIME_SERVICE_TYPE = "Door step AddOn";
                }
//                else if(textView.getText().toString().equalsIgnoreCase("Door step car polish")){
//                    context.startActivity(new Intent(context, ConfirmFormActivity.class));
//                }
                else if (textView.getText().toString().equalsIgnoreCase("Door step insurance")) {
                    context.startActivity(new Intent(context, ConfirmFormActivity.class).putExtra("headername", Constant.INSURANCE));
                }
            }
        });

        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }
}