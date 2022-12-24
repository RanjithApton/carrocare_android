package com.muvierecktech.carrocare.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.model.NotificationList;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter {
    public Context context;
    List<NotificationList.Notifications> notificationsList;
    String check;

    public NotificationAdapter(Context context, List<NotificationList.Notifications> notifications) {
        this.context = context;
        this.notificationsList = notifications;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notifications, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.title.setText(notificationsList.get(position).title);
        viewHolder.vecno.setText(notificationsList.get(position).description);

    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, vecno;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            title = (TextView) itemLayoutView.findViewById(R.id.title);
            vecno = (TextView) itemLayoutView.findViewById(R.id.vec_no);
        }
    }

}
