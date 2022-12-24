package com.muvierecktech.carrocare.adapter;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.activity.CarDetailsActivity;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.VehicleListActivity;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.model.ServicePriceList;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter {
    public Context context;
    List<ServicePriceList.Services> services;
    String header;

    public MainAdapter(Context context, List<ServicePriceList.Services> services, String headername) {
        this.context = context;
        this.services = services;
        this.header = headername;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dailycarlist, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.car_name.setText(services.get(position).type);
        viewHolder.car_price.setText("₹ " + services.get(position).prices);
        viewHolder.car_desc.setText(HtmlCompat.fromHtml(services.get(position).description, 0));
        Picasso.get()
                .load(services.get(position).image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(viewHolder.carimage);
        viewHolder.card.setTag(viewHolder);
        viewHolder.booknow.setTag(viewHolder);

        viewHolder.car_name1.setText(services.get(position).type);
        viewHolder.car_price1.setText("Total amount\n" + "₹ " + services.get(position).prices);
        viewHolder.car_desc1.setText(HtmlCompat.fromHtml(services.get(position).description, 0));
        Picasso.get()
                .load(services.get(position).image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(viewHolder.carimage1);
        viewHolder.rel_card.setTag(viewHolder);
        viewHolder.booknow1.setTag(viewHolder);

        if (header.equalsIgnoreCase(Constant.MACHINEPOLISH)) {
            viewHolder.booknow.setVisibility(View.GONE);
        }

        if (header.equalsIgnoreCase(Constant.WASH)) {
            if (services.get(position).id.equalsIgnoreCase("4")) {
                viewHolder.card.setVisibility(View.GONE);
            } else {
                viewHolder.card.setVisibility(View.VISIBLE);
            }
        }
        if (header.equalsIgnoreCase(Constant.BWASH)) {
            if (services.get(position).id.equalsIgnoreCase("4")) {
                viewHolder.card.setVisibility(View.GONE);
                viewHolder.rel_card.setVisibility(View.VISIBLE);
            } else {
                viewHolder.card.setVisibility(View.GONE);
            }
        }
        if (header.equalsIgnoreCase(Constant.ADDON)) {
            if (services.get(position).id.equalsIgnoreCase("4")) {
                viewHolder.card.setVisibility(View.GONE);
            } else {
                viewHolder.card.setVisibility(View.VISIBLE);
            }
        }
        if (header.equalsIgnoreCase(Constant.EXTRAINT)) {
            if (services.get(position).id.equalsIgnoreCase("4")) {
                viewHolder.card.setVisibility(View.GONE);
                viewHolder.rel_card.setVisibility(View.VISIBLE);
            } else {
                viewHolder.card.setVisibility(View.GONE);
            }
        }
        viewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyViewHolder myViewHolder = (MyViewHolder) view.getTag();
                int i = myViewHolder.getAdapterPosition();
                Intent intent = new Intent(context, CarDetailsActivity.class);
                intent.putExtra("carname", services.get(i).type);
                intent.putExtra("carprice", services.get(i).prices);
                intent.putExtra("cardesc", services.get(i).description);
                intent.putExtra("carimage", services.get(i).image);
                intent.putExtra("carid", services.get(i).id);
                intent.putExtra("header", header);
                context.startActivity(intent);
            }
        });
        viewHolder.booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyViewHolder myViewHolder = (MyViewHolder) view.getTag();
                int i = myViewHolder.getAdapterPosition();
                Intent intent = new Intent(context, VehicleListActivity.class);
                intent.putExtra("carname", services.get(i).type);
                intent.putExtra("carprice", services.get(i).prices);
                intent.putExtra("cardesc", services.get(i).description);
                intent.putExtra("carimage", services.get(i).image);
                intent.putExtra("carid", services.get(i).id);
                intent.putExtra("header", header);
                context.startActivity(intent);
            }
        });
        viewHolder.booknow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyViewHolder myViewHolder = (MyViewHolder) view.getTag();
                int i = myViewHolder.getAdapterPosition();
                Intent intent = new Intent(context, VehicleListActivity.class);
                intent.putExtra("carname", services.get(i).type);
                intent.putExtra("carprice", services.get(i).prices);
                intent.putExtra("cardesc", services.get(i).description);
                intent.putExtra("carimage", services.get(i).image);
                intent.putExtra("carid", services.get(i).id);
                intent.putExtra("header", header);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView carimage, carimage1;
        Button booknow, booknow1;
        TextView car_name, car_price, car_desc, car_name1, car_price1, car_desc1;
        CardView card, card1;
        ConstraintLayout rel_card;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            carimage = (ImageView) itemLayoutView.findViewById(R.id.car_img);
            car_name = (TextView) itemLayoutView.findViewById(R.id.car_name);
            car_price = (TextView) itemLayoutView.findViewById(R.id.car_price);
            car_desc = (TextView) itemLayoutView.findViewById(R.id.car_desc);
            booknow = (Button) itemLayoutView.findViewById(R.id.booknow);
            card = (CardView) itemLayoutView.findViewById(R.id.card);

            carimage1 = (ImageView) itemLayoutView.findViewById(R.id.car_img1);
            car_name1 = (TextView) itemLayoutView.findViewById(R.id.car_name1);
            car_price1 = (TextView) itemLayoutView.findViewById(R.id.car_price1);
            car_desc1 = (TextView) itemLayoutView.findViewById(R.id.car_desc1);
            booknow1 = (Button) itemLayoutView.findViewById(R.id.booknow1);
            card1 = (CardView) itemLayoutView.findViewById(R.id.card1);
            rel_card = (ConstraintLayout) itemLayoutView.findViewById(R.id.rel_card);
        }
    }

}
