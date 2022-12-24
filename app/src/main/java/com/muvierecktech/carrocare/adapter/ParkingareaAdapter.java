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
import com.muvierecktech.carrocare.model.ApartmentList;
import com.muvierecktech.carrocare.model.ParkingareaList;

import java.util.ArrayList;
import java.util.List;

public class ParkingareaAdapter extends RecyclerView.Adapter {
    public Context context;
    public int lastSelectedPosition = -1;
    boolean isVisibility;
    /* access modifiers changed from: private */
    String check;
    List<ParkingareaList.data> parkingarea;

    public ParkingareaAdapter(Context context2, List<ParkingareaList.data> list, String s) {
        this.context = context2;
        this.parkingarea = list;
        this.check = s;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_apartments, (ViewGroup) null));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
        myViewHolder.parkingarea_name.setText(parkingarea.get(position).name);
        myViewHolder.parkingarea_name.setTag(myViewHolder);
        myViewHolder.parkingarea_name.setChecked(lastSelectedPosition == position);
        myViewHolder.parkingarea_name.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (check.equalsIgnoreCase("1")) {
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    notifyDataSetChanged();
                    ((AddVehicleActivity) context).binding.parkingAreaEdt.setText(parkingarea.get(pos).name);
                    //((AddVehicleActivity) context).binding.apartRl.setVisibility(View.GONE);
                } else if (check.equalsIgnoreCase("2")) {
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    notifyDataSetChanged();
                    ((MyVechiclesAddActivity) context).binding.parkingAreaEdt.setText(parkingarea.get(pos).name);
                    //((MyVechiclesAddActivity) context).binding.apartRl.setVisibility(View.GONE);
                } else if (check.equalsIgnoreCase("3")) {
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    notifyDataSetChanged();
                    //((MapAddVechileActivity) context).binding.parkingAreaEdt.setText(parkingarea.get(pos).name);
                    //((MapAddVechileActivity) context).binding.apartRl.setVisibility(View.GONE);
                }
            }
        });
    }

    public void updateList(List<ParkingareaList.data> list) {
        parkingarea = new ArrayList<>();
        parkingarea.addAll(list);
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return parkingarea.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RadioButton parkingarea_name;

        public MyViewHolder(View view) {
            super(view);
            parkingarea_name = (RadioButton) view.findViewById(R.id.apart_name);
        }
    }
}
