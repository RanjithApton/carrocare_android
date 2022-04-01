package com.muvierecktech.carrocare.adapter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.AddVehicleActivity;
import com.muvierecktech.carrocare.activity.ConfirmFormActivity;
import com.muvierecktech.carrocare.activity.MapAddVechileActivity;
import com.muvierecktech.carrocare.activity.MyVechiclesAddActivity;
import com.muvierecktech.carrocare.activity.PaymentOptionActivity;
import com.muvierecktech.carrocare.activity.RenewActivity;
import com.muvierecktech.carrocare.activity.VehicleListActivity;

public class PreferredAdapter extends RecyclerView.Adapter {
    public Context context;
    String[] prefer;
    String type;
    private int lastSelectedPosition = -1;
    public PreferredAdapter(Context context, String[] predferred, String s) {
        this.context = context;
        this.prefer = predferred;
        this.type = s;
        Log.e("Time", String.valueOf(predferred));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apartments, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
         final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder._name.setText(prefer[position]);
        viewHolder._name.setTag(viewHolder);
        viewHolder._name.setChecked(lastSelectedPosition==position);
        viewHolder._name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equalsIgnoreCase("1")){
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((AddVehicleActivity)context).binding.preferredscheduleEdt.setText(prefer[pos]);
                    ((AddVehicleActivity)context).binding.preferredtimeEdt.setText(null);
//                    ((AddVehicleActivity)context).binding.preferredscheduleRc.setVisibility(View.GONE);
//                    ((AddVehicleActivity)context).binding.apartRl.setVisibility(View.GONE);
                }else if (type.equalsIgnoreCase("2")){
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((AddVehicleActivity)context).binding.preferredtimeEdt.setText(prefer[pos]);
//                    ((AddVehicleActivity)context).binding.preferredtimeRc.setVisibility(View.GONE);
//                    ((AddVehicleActivity)context).binding.apartRl.setVisibility(View.GONE);
                }else if (type.equalsIgnoreCase("3")){
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((AddVehicleActivity)context).binding.parkingAreaEdt.setText(prefer[pos]);
//                    ((AddVehicleActivity)context).binding.parkingAreaRc.setVisibility(View.GONE);
//                    ((AddVehicleActivity)context).binding.apartRl.setVisibility(View.GONE);
                }else if (type.equalsIgnoreCase("type")){
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((ConfirmFormActivity)context).binding.vecTypeEdt.setText(prefer[pos]);
                    ((ConfirmFormActivity)context).binding.typelistRc.setVisibility(View.GONE);
                    if (prefer[pos].equalsIgnoreCase("Bike")){
                        ((ConfirmFormActivity)context).binding.vecCategoryEdt.setText("Bike");
                    }else {
                        ((ConfirmFormActivity)context).binding.vecCategoryEdt.setText(null);
                    }
                    ((ConfirmFormActivity)context).binding.apartRl.setVisibility(View.GONE);

                }else if (type.equalsIgnoreCase("catcar")){
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((ConfirmFormActivity)context).binding.vecCategoryEdt.setText(prefer[pos]);
                    ((ConfirmFormActivity)context).binding.catlistRc.setVisibility(View.GONE);
                    ((ConfirmFormActivity)context).binding.apartRl.setVisibility(View.GONE);
                }else if (type.equalsIgnoreCase("pay")){
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((PaymentOptionActivity)context).binding.preferredtimeEdt.setText(prefer[pos]);
                    ((PaymentOptionActivity)context).time = prefer[pos];
                    //((PaymentOptionActivity)context).binding.timeLl.setVisibility(View.GONE);
                }else if (type.equalsIgnoreCase("11")){
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((MyVechiclesAddActivity)context).binding.preferredscheduleEdt.setText(prefer[pos]);
                    ((MyVechiclesAddActivity)context).binding.preferredtimeEdt.setText(null);
//                    ((MyVechiclesAddActivity)context).binding.preferredscheduleRc.setVisibility(View.GONE);
//                    ((MyVechiclesAddActivity)context).binding.apartRl.setVisibility(View.GONE);
                }else if (type.equalsIgnoreCase("22")){
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((MyVechiclesAddActivity)context).binding.preferredtimeEdt.setText(prefer[pos]);
//                    ((MyVechiclesAddActivity)context).binding.preferredtimeRc.setVisibility(View.GONE);
//                    ((MyVechiclesAddActivity)context).binding.apartRl.setVisibility(View.GONE);
                }else if (type.equalsIgnoreCase("renew_wax")){
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((RenewActivity)context).binding.preferredtimeEdt.setText(prefer[pos]);
                    ((RenewActivity)context).time = prefer[pos];
                    ((RenewActivity)context).binding.timeLl.setVisibility(View.GONE);
                }else if (type.equalsIgnoreCase("renew_ext")){
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((RenewActivity)context).binding.preferredtimeEdt1.setText(prefer[pos]);
                    ((RenewActivity)context).time = prefer[pos];
                    ((RenewActivity)context).binding.timeLl.setVisibility(View.GONE);
                }else if (type.equalsIgnoreCase("smart_wax")){
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((VehicleListActivity)context).binding.preferredtimeEdt.setText(prefer[pos]);
                    ((VehicleListActivity)context).time = prefer[pos];
                    ((VehicleListActivity)context).binding.timeLl.setVisibility(View.GONE);
                }else if (type.equalsIgnoreCase("smart_ext")){
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((VehicleListActivity)context).binding.preferredtimeEdt1.setText(prefer[pos]);
                    ((VehicleListActivity)context).time = prefer[pos];
                    ((VehicleListActivity)context).binding.timeLl.setVisibility(View.GONE);
                }

                else if (type.equalsIgnoreCase("12")){
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    //((MapAddVechileActivity)context).binding.preferredscheduleEdt.setText(prefer[pos]);
                    //((MapAddVechileActivity)context).binding.preferredtimeEdt.setText(null);
//                    ((MapAddVechileActivity)context).binding.preferredscheduleRc.setVisibility(View.GONE);
//                    ((MapAddVechileActivity)context).binding.apartRl.setVisibility(View.GONE);
                }else if (type.equalsIgnoreCase("23")){
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    //((MapAddVechileActivity)context).binding.preferredtimeEdt.setText(prefer[pos]);
//                    ((MapAddVechileActivity)context).binding.preferredtimeRc.setVisibility(View.GONE);
//                    ((MapAddVechileActivity)context).binding.apartRl.setVisibility(View.GONE);
                }


            }
        });

    }
    @Override
    public int getItemCount() {
        return prefer.length;
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RadioButton _name;
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
             _name = (RadioButton)itemLayoutView.findViewById(R.id.apart_name);
        }
    }

}
