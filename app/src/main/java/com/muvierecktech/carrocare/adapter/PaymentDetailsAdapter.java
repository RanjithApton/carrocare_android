package com.muvierecktech.carrocare.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.model.OrdersList;

import java.util.List;

public class PaymentDetailsAdapter extends RecyclerView.Adapter {
    public Context context;
    List<OrdersList.PaymentDetails> paymentDetails;
    public PaymentDetailsAdapter(Context context, List<OrdersList.PaymentDetails> paymentDetailsList) {
        this.context = context;
        this.paymentDetails = paymentDetailsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_result_payment_details, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
         final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.date.setText(paymentDetails.get(position).payment_date);
        viewHolder.paymentid.setText(paymentDetails.get(position).razorpay_payment_id);
        viewHolder.amount.setText("Rs."+paymentDetails.get(position).amount);
        viewHolder.status.setText(paymentDetails.get(position).status);
    }
    @Override
    public int getItemCount() {
        return paymentDetails.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date,amount,paymentid,status;
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            date = (TextView)itemLayoutView.findViewById(R.id.date);
            paymentid = (TextView)itemLayoutView.findViewById(R.id.paymentid);
            amount = (TextView)itemLayoutView.findViewById(R.id.amount);
            status = (TextView)itemLayoutView.findViewById(R.id.status);
        }
    }

}
