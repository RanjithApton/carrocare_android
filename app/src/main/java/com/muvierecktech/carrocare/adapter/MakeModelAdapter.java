package com.muvierecktech.carrocare.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.AddVehicleActivity;
import com.muvierecktech.carrocare.activity.MapAddVechileActivity;
import com.muvierecktech.carrocare.activity.MyVechiclesAddActivity;
import com.muvierecktech.carrocare.model.MakeModelList;

import java.util.ArrayList;
import java.util.List;

public class MakeModelAdapter extends RecyclerView.Adapter {
    public Context context;
    List<MakeModelList.Vehicle> makemodel;
    String type;
    private int lastSelectedPosition = -1;
    public MakeModelAdapter(Context context, List<MakeModelList.Vehicle> apartment,String s) {
        this.context = context;
        this.makemodel = apartment;
        this.type = s;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apartments, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
         final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.makemodel.setText(makemodel.get(position).vehicle_make+"-"+makemodel.get(position).vehicle_model);
        viewHolder.makemodel.setTag(viewHolder);
        viewHolder.makemodel.setChecked(lastSelectedPosition==position);
        viewHolder.makemodel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equalsIgnoreCase("1")) {
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((AddVehicleActivity) context).binding.makeModelEdt.setText(makemodel.get(pos).vehicle_make + "-" + makemodel.get(pos).vehicle_model);
                    ((AddVehicleActivity) context).binding.makemodelRc.setVisibility(View.GONE);
                    ((AddVehicleActivity) context).makeStr = makemodel.get(pos).vehicle_make;
                    ((AddVehicleActivity) context).modelStr = makemodel.get(pos).vehicle_model;
                    ((AddVehicleActivity) context).binding.apartRl.setVisibility(View.GONE);

                }else if (type.equalsIgnoreCase("2")) {
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((MyVechiclesAddActivity) context).binding.makeModelEdt.setText(makemodel.get(pos).vehicle_make + "-" + makemodel.get(pos).vehicle_model);
                    ((MyVechiclesAddActivity) context).binding.makemodelRc.setVisibility(View.GONE);
                    ((MyVechiclesAddActivity) context).makeStr = makemodel.get(pos).vehicle_make;
                    ((MyVechiclesAddActivity) context).modelStr = makemodel.get(pos).vehicle_model;
                    ((MyVechiclesAddActivity) context).binding.apartRl.setVisibility(View.GONE);
                }else if (type.equalsIgnoreCase("3")) {
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((MapAddVechileActivity) context).binding.makeModelEdt.setText(makemodel.get(pos).vehicle_make + "-" + makemodel.get(pos).vehicle_model);
                    ((MapAddVechileActivity) context).binding.makemodelRc.setVisibility(View.GONE);
                    ((MapAddVechileActivity) context).makeStr = makemodel.get(pos).vehicle_make;
                    ((MapAddVechileActivity) context).modelStr = makemodel.get(pos).vehicle_model;
                    ((MapAddVechileActivity) context).binding.apartRl.setVisibility(View.GONE);
                }

            }
        });
    }

    public void updateList(List<MakeModelList.Vehicle> list) {
        makemodel = new ArrayList<>();
        makemodel.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return makemodel.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RadioButton makemodel;
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            makemodel = (RadioButton) itemLayoutView.findViewById(R.id.apart_name);
        }
    }

}
