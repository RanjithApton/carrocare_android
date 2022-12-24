package com.muvierecktech.carrocare.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.AddVehicleActivity;
import com.muvierecktech.carrocare.activity.MyRemainderActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReminderAdapter extends RecyclerView.Adapter {
    public Context context;
    String[] prefer;
    String type;
    private int lastSelectedPosition = -1;

    public ReminderAdapter(Context context, String[] predferred, String s) {
        this.context = context;
        this.prefer = predferred;
        this.type = s;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apartments, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder._name.setText(prefer[position]);
        viewHolder._name.setTag(viewHolder);
        viewHolder._name.setChecked(lastSelectedPosition == position);
        viewHolder._name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equalsIgnoreCase("remainderType")) {
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((MyRemainderActivity) context).binding.reminderEdt.setText(prefer[pos]);
                    ((MyRemainderActivity) context).binding.reminTypeRc.setVisibility(View.GONE);
                    ((MyRemainderActivity) context).binding.apartRl.setVisibility(View.GONE);

                    if (prefer[pos].equalsIgnoreCase("Services Remainder")) {
                        ((MyRemainderActivity) context).binding.reminderService.setVisibility(View.VISIBLE);
                        ((MyRemainderActivity) context).binding.reminderInsurance.setVisibility(View.GONE);
                    } else if (prefer[pos].equalsIgnoreCase("Bike/Car Insurance Remainder")) {
                        ((MyRemainderActivity) context).binding.reminderInsurance.setVisibility(View.VISIBLE);
                        ((MyRemainderActivity) context).binding.reminderService.setVisibility(View.GONE);
                    }

                } else if (type.equalsIgnoreCase("serviceCount")) {
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((MyRemainderActivity) context).binding.serviceCountEdt.setText(prefer[pos]);
                    ((MyRemainderActivity) context).binding.serCountRc.setVisibility(View.GONE);
                    ((MyRemainderActivity) context).binding.apartRl.setVisibility(View.GONE);

                } else if (type.equalsIgnoreCase("setRemainder")) {
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((MyRemainderActivity) context).binding.setSerReminder.setText(prefer[pos]);
                    ((MyRemainderActivity) context).binding.setRemainderRc.setVisibility(View.GONE);
                    ((MyRemainderActivity) context).binding.apartRl.setVisibility(View.GONE);

                } else if (type.equalsIgnoreCase("insuranceBrand")) {
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((MyRemainderActivity) context).binding.insBrndEdt.setText(prefer[pos]);
                    ((MyRemainderActivity) context).binding.insBrndRc.setVisibility(View.GONE);
                    ((MyRemainderActivity) context).binding.apartRl.setVisibility(View.GONE);

                } else if (type.equalsIgnoreCase("setRemainderInsurance")) {
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((MyRemainderActivity) context).binding.setInsReminder.setText(prefer[pos]);
                    ((MyRemainderActivity) context).binding.setRemainderRc.setVisibility(View.GONE);
                    ((MyRemainderActivity) context).binding.apartRl.setVisibility(View.GONE);

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
            _name = (RadioButton) itemLayoutView.findViewById(R.id.apart_name);
        }
    }
}
