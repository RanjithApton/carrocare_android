package com.muvierecktech.carrocare.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.MyOrdersDetailActivity;
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.date.setText(paymentDetails.get(position).payment_date);
        viewHolder.paymentid.setText(paymentDetails.get(position).razorpay_payment_id);
        viewHolder.amount.setText("Rs." + paymentDetails.get(position).amount);
        viewHolder.status.setText(paymentDetails.get(position).status);

        viewHolder.imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String s = paymentDetails.get(position).invoice.split("=")[1];
                    if(s.equalsIgnoreCase("0")){
                        Toast.makeText(context, "Invoice Download Failed. Please Contact Admin.", Toast.LENGTH_SHORT).show();
                    } else {
                        ((MyOrdersDetailActivity)context).downloadInvoice(paymentDetails.get(position).invoice,"Carrocare_Invoice_"+paymentDetails.get(position).razorpay_payment_id+".pdf");
                    }
                    //Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "IInvoice Download Failed. Please Contact Admin.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentDetails.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, amount, paymentid, status;
        ImageView imgDownload;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            date = (TextView) itemLayoutView.findViewById(R.id.date);
            paymentid = (TextView) itemLayoutView.findViewById(R.id.paymentid);
            amount = (TextView) itemLayoutView.findViewById(R.id.amount);
            status = (TextView) itemLayoutView.findViewById(R.id.status);
            imgDownload = itemLayoutView.findViewById(R.id.imgDownload);
        }
    }

}
