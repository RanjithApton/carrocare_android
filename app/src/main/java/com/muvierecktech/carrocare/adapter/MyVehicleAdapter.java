package com.muvierecktech.carrocare.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.DatabaseHelper;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.model.VehicleDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyVehicleAdapter extends RecyclerView.Adapter {
    public Context context;
    List<VehicleDetails.VecDetails> vecDetails;

    public MyVehicleAdapter(Context context, List<VehicleDetails.VecDetails> vecDetails) {
        this.context = context;
        this.vecDetails = vecDetails;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_myvehicle, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;

//        if(TextUtils.isEmpty(vecDetails.get(position).address) || vecDetails.get(position).address == null){
//            viewHolder.card.setVisibility(View.VISIBLE);
//        }else{
//            viewHolder.card.setVisibility(View.GONE);
//        }

        viewHolder.vec_makemodel.setText(vecDetails.get(position).vehicle_make + "-" + vecDetails.get(position).vehicle_model);
//        viewHolder.category.setText(vecDetails.get(position).vehicle_category);
        viewHolder.vehicleno.setText(vecDetails.get(position).vehicle_no);
        viewHolder.color.setText(vecDetails.get(position).vehicle_color);
        viewHolder.apart_name.setText(vecDetails.get(position).vehicle_apartment_name);
        viewHolder.parkinglotno.setText(vecDetails.get(position).vehicle_parking_lot_no);
        viewHolder.parkingarea.setText(vecDetails.get(position).vehicle_parking_area);
        viewHolder.preferredschedule.setText(vecDetails.get(position).vehicle_preferred_schedule);
        viewHolder.preferredtime.setText(vecDetails.get(position).vehicle_preferred_time);

        Picasso.get()
                .load(vecDetails.get(position).vehicle_image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(viewHolder.carimage);

    }

    @Override
    public int getItemCount() {
        return vecDetails.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView carimage;
        TextView vec_type, vec_makemodel, category, vehicleno, color, apart_name, parkinglotno, parkingarea, preferredschedule, preferredtime;
        CardView card;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            apart_name = (TextView) itemLayoutView.findViewById(R.id.apart_name);
            vehicleno = (TextView) itemLayoutView.findViewById(R.id.vec_no);
            vec_makemodel = (TextView) itemLayoutView.findViewById(R.id.vec_makemodel);
            color = (TextView) itemLayoutView.findViewById(R.id.vec_color);
            parkingarea = (TextView) itemLayoutView.findViewById(R.id.parking_area);
            parkinglotno = (TextView) itemLayoutView.findViewById(R.id.parking_lot);
            preferredschedule = (TextView) itemLayoutView.findViewById(R.id.pref_schedule);
            preferredtime = (TextView) itemLayoutView.findViewById(R.id.preftime);
            carimage = (ImageView) itemLayoutView.findViewById(R.id.car_img);
            card = (CardView) itemLayoutView.findViewById(R.id.card);
        }
    }

}
