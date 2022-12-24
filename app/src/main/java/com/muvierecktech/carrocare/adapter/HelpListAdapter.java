package com.muvierecktech.carrocare.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.HelpAndSupportActivity;

public class HelpListAdapter extends RecyclerView.Adapter {
    public Context context;
    String[] messagelist;
    boolean isVisibility;

    public HelpListAdapter(Context context, String[] messageList) {
        this.context = context;
        this.messagelist = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_helplist, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.title.setText(messagelist[position]);
        viewHolder.title.setTag(viewHolder);
        viewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                int pos = viewHolder1.getAdapterPosition();
                if (isVisibility) {
                    viewHolder1.meassge_ll.setVisibility(View.GONE);
                    isVisibility = false;
                } else {
                    viewHolder1.meassge_ll.setVisibility(View.VISIBLE);
                    isVisibility = true;
                }
            }
        });
        viewHolder.submit.setTag(viewHolder);
        viewHolder.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                int pos = viewHolder1.getAdapterPosition();
                ((HelpAndSupportActivity) context).work(viewHolder1.title.getText().toString(), viewHolder1.msg.getText().toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        return messagelist.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView msg, title;
        Button submit;
        LinearLayout meassge_ll;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            title = (TextView) itemLayoutView.findViewById(R.id.tvTitle);
            msg = (TextView) itemLayoutView.findViewById(R.id.edtmessage);
            submit = (Button) itemLayoutView.findViewById(R.id.submit);
            meassge_ll = (LinearLayout) itemLayoutView.findViewById(R.id.meassge_ll);
        }
    }

}
