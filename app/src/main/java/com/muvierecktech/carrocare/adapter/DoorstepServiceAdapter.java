package com.muvierecktech.carrocare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.DoorStepServiceActivity;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.MyDatabaseHelper;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.model.DoorStepCarWash;
import com.muvierecktech.carrocare.model.VehicleDetails;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class DoorstepServiceAdapter extends RecyclerView.Adapter {
    public Context context;
    List<DoorStepCarWash.Service> services;
    String action;
    String total_amount = "0";

    public DoorstepServiceAdapter(Context context, List<DoorStepCarWash.Service> services, String action) {
        this.context = context;
        this.services = services;
        this.action = action;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lyt_doorstep, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;

        Picasso.get()
                .load(services.get(position).image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(viewHolder.service_image);
        viewHolder.service_name.setText(services.get(position).service);
        viewHolder.extrerior_amt.setText(services.get(position).prices);

        if (services.get(position).status.equalsIgnoreCase("Active")) {
            viewHolder.extrerior_card.setVisibility(View.VISIBLE);
        } else {
            viewHolder.extrerior_card.setVisibility(View.GONE);
        }

        viewHolder.extrerior_info.setTag(viewHolder);
        viewHolder.extrerior_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                int pos = viewHolder1.getAdapterPosition();
                ((DoorStepServiceActivity) context).showPopupDialog(services.get(pos).description, services.get(pos).image);
            }
        });

//        if(action.equalsIgnoreCase("carwash")){
//
//        }else{
//
//        }

        viewHolder.extrerior_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.extrerior_check.isChecked()) {
                    viewHolder.extrerior_check.setChecked(false);
                } else
                    viewHolder.extrerior_check.setChecked(true);
            }
        });

        viewHolder.extrerior_check.setTag(viewHolder);
        viewHolder.extrerior_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyViewHolder viewHolder1 = (MyViewHolder) buttonView.getTag();
                int pos = viewHolder1.getAdapterPosition();

                if (isChecked) {
                    ((DoorStepServiceActivity) context).botAmount.setVisibility(View.VISIBLE);
                    int num1 = (int) Double.parseDouble(total_amount);
                    int num2 = (int) Double.parseDouble(services.get(pos).prices);
                    int sum = num1 + num2;
                    total_amount = String.valueOf(sum);
                    ((DoorStepServiceActivity) context).total_amount = total_amount;
                    ((DoorStepServiceActivity) context).totalAmount.setText("INR " + total_amount);
                } else if (!isChecked) {
                    int num1 = (int) Double.parseDouble(total_amount);
                    int num2 = (int) Double.parseDouble(services.get(pos).prices);
                    int sum = num1 - num2;
                    total_amount = String.valueOf(sum);
                    ((DoorStepServiceActivity) context).total_amount = total_amount;
                    ((DoorStepServiceActivity) context).totalAmount.setText("INR " + total_amount);
                    if (sum == 0) {
                        ((DoorStepServiceActivity) context).botAmount.setVisibility(View.GONE);
                    } else {
                        ((DoorStepServiceActivity) context).botAmount.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        ((DoorStepServiceActivity) context).button_processed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.ONETIME_CAR_PRICE = total_amount;
                if (action.equalsIgnoreCase("carwash")) {
                    Constant.ONETIME_SERVICE_TYPE = "Door step Wash";
                } else {
                    Constant.ONETIME_SERVICE_TYPE = "Door step Detailing";
                }

                ((DoorStepServiceActivity) context).showDateAndTime();
            }
        });

    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout extrerior_card;
        ImageView service_image;
        TextView service_name, extrerior_amt;
        Button extrerior_info;
        CheckBox extrerior_check;


        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            extrerior_card = itemLayoutView.findViewById(R.id.extrerior_card);
            service_image = itemLayoutView.findViewById(R.id.service_image);
            service_name = itemLayoutView.findViewById(R.id.service_name);
            extrerior_amt = itemLayoutView.findViewById(R.id.extrerior_amt);
            extrerior_info = itemLayoutView.findViewById(R.id.extrerior_info);
            extrerior_check = itemLayoutView.findViewById(R.id.extrerior_check);
        }
    }
}
