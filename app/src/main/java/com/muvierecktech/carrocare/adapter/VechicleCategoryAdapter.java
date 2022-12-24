package com.muvierecktech.carrocare.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.MapAddVechileActivity;
import com.muvierecktech.carrocare.activity.MyVechiclesAddActivity;
import com.muvierecktech.carrocare.activity.ConfirmFormActivity;
import com.muvierecktech.carrocare.activity.MyVechiclesAddActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VechicleCategoryAdapter extends RecyclerView.Adapter {
    public Context context;
    String[] prefer;
    String type;
    private int lastSelectedPosition = -1;

    public VechicleCategoryAdapter(Context context, String[] predferred, String s) {
        this.context = context;
        this.prefer = predferred;
        this.type = s;
        Log.e("Time", String.valueOf(predferred));
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
                if (type.equalsIgnoreCase("2")) {
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((MyVechiclesAddActivity) context).binding.vecCategoryEdt.setText(prefer[pos]);
//                        ((MyVechiclesAddActivity)context).binding.vecCategoryRc.setVisibility(View.GONE);
//                        ((MyVechiclesAddActivity)context).binding.apartRl.setVisibility(View.GONE);

                    String vecCat = prefer[pos];
                    ((MyVechiclesAddActivity) context).additionalwork(vecCat);

                    if (prefer[pos].equalsIgnoreCase("bike")) {
                        ((MyVechiclesAddActivity) context).binding.makeModelEdt.setText("");
                        ((MyVechiclesAddActivity) context).binding.makemodelRl.setVisibility(View.GONE);
                        ((MyVechiclesAddActivity) context).binding.makeEdt.setVisibility(View.VISIBLE);
                        ((MyVechiclesAddActivity) context).binding.modelEdt.setVisibility(View.VISIBLE);
                    } else {
                        ((MyVechiclesAddActivity) context).binding.makeModelEdt.setText("");
                        ((MyVechiclesAddActivity) context).binding.makemodelRl.setVisibility(View.VISIBLE);
                        ((MyVechiclesAddActivity) context).binding.makeEdt.setVisibility(View.GONE);
                        ((MyVechiclesAddActivity) context).binding.modelEdt.setVisibility(View.GONE);

                    }

                } else if (type.equalsIgnoreCase("3")) {
                    MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
                    ((MapAddVechileActivity) context).binding.vecCategoryEdt.setText(prefer[pos]);
//                         ((MapAddVechileActivity)context).binding.vecCategoryRc.setVisibility(View.GONE);
//                         ((MapAddVechileActivity)context).binding.apartRl.setVisibility(View.GONE);

                    String vecCat = prefer[pos];
                    ((MapAddVechileActivity) context).additionalwork(vecCat);

//                         if (prefer[pos].equalsIgnoreCase("bike")){
//                             ((MapAddVechileActivity)context).binding.makeModelEdt.setText("");
//                             ((MapAddVechileActivity)context).binding.makemodelRl.setVisibility(View.GONE);
//                             ((MapAddVechileActivity)context).binding.makeEdt.setVisibility(View.VISIBLE);
//                             ((MapAddVechileActivity)context).binding.modelEdt.setVisibility(View.VISIBLE);
//                         }else{
//                             ((MapAddVechileActivity)context).binding.makeModelEdt.setText("");
//                             ((MapAddVechileActivity)context).binding.makemodelRl.setVisibility(View.VISIBLE);
//                             ((MapAddVechileActivity)context).binding.makeEdt.setVisibility(View.GONE);
//                             ((MapAddVechileActivity)context).binding.modelEdt.setVisibility(View.GONE);
//
//                         }

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
