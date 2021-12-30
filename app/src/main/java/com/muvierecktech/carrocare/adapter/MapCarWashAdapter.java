package com.muvierecktech.carrocare.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.CarDetailsActivity;
import com.muvierecktech.carrocare.activity.DoorStepServiceActivity;
import com.muvierecktech.carrocare.activity.DoorStepServiceActivity;
import com.muvierecktech.carrocare.activity.VehicleListActivity;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.model.ServicePriceList;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MapCarWashAdapter extends RecyclerView.Adapter {
    public Context context;
    List<ServicePriceList.Services> services;
    String header;

    public MapCarWashAdapter(Context context, List<ServicePriceList.Services> services, String headername) {
        this.context = context;
        this.services = services;
        this.header = headername;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lyt_category, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;


        if(header.equalsIgnoreCase("carwash")){
            if(services.get(position).id.equalsIgnoreCase("4")){
                viewHolder.linearLayout.setVisibility(View.GONE);
                viewHolder.card.setVisibility(View.GONE);
            }else{
                viewHolder.linearLayout.setVisibility(View.VISIBLE);
                viewHolder.card.setVisibility(View.VISIBLE);
            }
        }else if(header.equalsIgnoreCase("bikewash")){
            if(services.get(position).type.equalsIgnoreCase("bike")){
                viewHolder.linearLayout.setVisibility(View.VISIBLE);
                viewHolder.card.setVisibility(View.VISIBLE);
            }else{
                viewHolder.card.setVisibility(View.GONE);
                viewHolder.linearLayout.setVisibility(View.GONE);
            }
        }

        Picasso.get()
                .load(services.get(position).image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(viewHolder.image);

        viewHolder.title.setText(services.get(position).type);


        viewHolder.info_btn.setTag(viewHolder);
        viewHolder.info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyViewHolder myViewHolder = (MyViewHolder)v.getTag();
                int pos = myViewHolder.getAdapterPosition();
                //((DoorStepServiceActivity)context).binding.infoPopup.clearAnimation();
            }
        });

        viewHolder.card.setTag(viewHolder);
        viewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyViewHolder myViewHolder = (MyViewHolder)v.getTag();
                int pos = myViewHolder.getAdapterPosition();
                Constant.ONETIME_CAR_PRICE = services.get(pos).prices;
                String car_name = services.get(pos).type;
                //((DoorStepServiceActivity)context).showVechile(car_name);
            }
        });

    }
    @Override
    public int getItemCount() {
        return services.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        Button info_btn;
        TextView title;
        CardView card;
        LinearLayout linearLayout;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            image = (ImageView)itemLayoutView.findViewById(R.id.image);
            title = (TextView)itemLayoutView.findViewById(R.id.title);
            info_btn = (Button) itemLayoutView.findViewById(R.id.info_btn);
            card = (CardView) itemLayoutView.findViewById(R.id.card);
            linearLayout = (LinearLayout) itemLayoutView.findViewById(R.id.linearLayout);

        }
    }

}
