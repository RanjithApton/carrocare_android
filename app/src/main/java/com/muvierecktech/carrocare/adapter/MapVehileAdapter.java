package com.muvierecktech.carrocare.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.model.VehicleDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MapVehileAdapter extends RecyclerView.Adapter {
    public Context context;
    List<VehicleDetails.VecDetails> vecDetails;
    String header, price, carname;
    private int selectedPosition = 0;
    private ArrayList<Integer> selectCheck = new ArrayList<>();

    public MapVehileAdapter(Context context, List<VehicleDetails.VecDetails> vecDetails) {
        this.context = context;
        this.vecDetails = vecDetails;

        for (int i = 0; i < vecDetails.size(); i++) {
            selectCheck.add(0);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lyt_vehicle, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;

        if (vecDetails.get(position).vehicle_Type.equalsIgnoreCase("Bike")) {
            viewHolder.card.setVisibility(View.GONE);
        } else {
            viewHolder.card.setVisibility(View.VISIBLE);
        }

//        if(TextUtils.isEmpty(vecDetails.get(position).address) || vecDetails.get(position).address == null){
//            viewHolder.card.setVisibility(View.GONE);
//        }else{
//            if(vecDetails.get(position).vehicle_Type.equalsIgnoreCase("Bike")){
//                viewHolder.card.setVisibility(View.GONE);
//            }else{
//                viewHolder.card.setVisibility(View.VISIBLE);
//            }
//        }


        //viewHolder.vec_makemodel.setText(vecDetails.get(position).vehicle_make+"-"+vecDetails.get(position).vehicle_model);
        viewHolder.vehicleno.setText(vecDetails.get(position).vehicle_no);
        //viewHolder.color.setText(vecDetails.get(position).vehicle_color);
        Picasso.get()
                .load(vecDetails.get(position).vehicle_image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(viewHolder.carimage);

//        viewHolder.card.setTag(viewHolder);
//        viewHolder.card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewHolder.card.setCardBackgroundColor(context.getColor(R.color.colorPrimary));
//            }
//        });

        if (selectCheck.get(position) == 1) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < selectCheck.size(); i++) {
                    if (i == position) {
                        selectCheck.set(i, 1);
                    } else {
                        selectCheck.set(i, 0);
                    }
                }
                notifyDataSetChanged();
            }
        });

        viewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.checkBox.setChecked(true);
                for (int i = 0; i < selectCheck.size(); i++) {
                    if (i == position) {
                        selectCheck.set(i, 1);
                    } else {
                        selectCheck.set(i, 0);
                    }
                }
                notifyDataSetChanged();
            }
        });

        viewHolder.checkBox.setTag(viewHolder);
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    MyViewHolder viewHolder1 = (MyViewHolder) buttonView.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    Constant.ONETIME_CAR_TYPE = vecDetails.get(pos).vehicle_category;
                    Constant.ONETIME_CAR_ID = vecDetails.get(pos).vehicle_id;
                    Constant.ONETIME_PACK_TYPE = vecDetails.get(pos).vehicle_category;
                    //Toast.makeText(context, Constant.ONETIME_CAR_TYPE, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return vecDetails.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView carimage;
        TextView vec_makemodel, vehicleno, color;
        CardView card;
        CheckBox checkBox;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            vehicleno = (TextView) itemLayoutView.findViewById(R.id.vec_no);
            //vec_makemodel = (TextView)itemLayoutView.findViewById(R.id.vec_makemodel);
            //color = (TextView)itemLayoutView.findViewById(R.id.vec_color);
            carimage = (ImageView) itemLayoutView.findViewById(R.id.car_img);
            card = (CardView) itemLayoutView.findViewById(R.id.card);
            checkBox = (CheckBox) itemLayoutView.findViewById(R.id.car_check);
        }

    }

}

