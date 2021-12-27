package com.muvierecktech.carrocare.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.AddVehicleActivity;

import com.muvierecktech.carrocare.activity.MyVechiclesAddActivity;
import com.muvierecktech.carrocare.activity.ProfileActivity;
import com.muvierecktech.carrocare.model.ApartmentList;

import java.util.List;

public class ApartmentsAdapter extends RecyclerView.Adapter {
    public Context context;
    List<ApartmentList.Apartment> apartments;
    boolean isVisibility;
    String check;
    private int lastSelectedPosition = -1;
    public ApartmentsAdapter(Context context, List<ApartmentList.Apartment> apartmentsl, String s) {
        this.context = context;
        this.apartments = apartmentsl;
        this.check = s;
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
        final ApartmentList.Apartment apartment = apartments.get(position);
        viewHolder.apart_name.setText(apartment.name);
        viewHolder.apart_name.setTag(viewHolder);
        viewHolder.apart_name.setChecked(lastSelectedPosition == position);
        viewHolder.apart_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                int pos = viewHolder1.getAdapterPosition();
                if (check.equalsIgnoreCase("2")){
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                ((AddVehicleActivity)context).binding.apartmentEdt.setText(apartments.get(pos).name);
                ((AddVehicleActivity)context).binding.apartRl.setVisibility(View.GONE);
                }else if (check.equalsIgnoreCase("1")){
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                ((ProfileActivity)context).binding.apartnameEdt.setText(apartments.get(pos).name);
                ((ProfileActivity)context).binding.apartRl.setVisibility(View.GONE);
                }else if (check.equalsIgnoreCase("3")){
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((MyVechiclesAddActivity)context).binding.apartmentEdt.setText(apartments.get(pos).name);
                    ((MyVechiclesAddActivity)context).binding.apartRl.setVisibility(View.GONE);
                }

            }
        });

    }
    @Override
    public int getItemCount() {
        return apartments.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RadioButton apart_name;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            apart_name = (RadioButton) itemLayoutView.findViewById(R.id.apart_name);
        }
    }

}
