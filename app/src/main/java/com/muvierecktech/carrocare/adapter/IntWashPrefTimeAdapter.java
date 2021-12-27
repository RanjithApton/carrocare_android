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
import com.muvierecktech.carrocare.activity.CalenderActivity;
import com.muvierecktech.carrocare.activity.InternalwashActivity;

public class IntWashPrefTimeAdapter extends RecyclerView.Adapter{
    public Context context;
    String[] prefer;
    String type;
    private int lastSelectedPosition = -1;
    public IntWashPrefTimeAdapter(Context context, String[] predferred) {
        this.context = context;
        this.prefer = predferred;
        Log.e("Time", String.valueOf(predferred));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apartments, null);
        return new PreferredTimeAdapter.MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final PreferredTimeAdapter.MyViewHolder viewHolder = (PreferredTimeAdapter.MyViewHolder) holder;
        viewHolder._name.setText(prefer[position]);
        viewHolder._name.setTag(viewHolder);
        viewHolder._name.setChecked(lastSelectedPosition==position);
        viewHolder._name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferredTimeAdapter.MyViewHolder viewHolder1 = (PreferredTimeAdapter.MyViewHolder) view.getTag();
                int pos = viewHolder1.getAdapterPosition();
                lastSelectedPosition = pos;
                notifyDataSetChanged();
                ((InternalwashActivity)context).binding.preferredtimeEdt.setText(prefer[pos]);
                ((InternalwashActivity)context).binding.timerl.setVisibility(View.GONE);
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
