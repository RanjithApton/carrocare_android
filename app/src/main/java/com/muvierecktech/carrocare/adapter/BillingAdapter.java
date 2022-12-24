package com.muvierecktech.carrocare.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.MyBillingActivity;
import com.muvierecktech.carrocare.activity.MyOrdersDetailActivity;
import com.muvierecktech.carrocare.model.BillingList;
import com.muvierecktech.carrocare.model.OrdersList;

import java.util.List;

public class BillingAdapter extends RecyclerView.Adapter {
    public Context context;
    List<BillingList.Res> billingList;
    String header;

    public BillingAdapter(Context context, List<BillingList.Res> billingList) {
        this.context = context;
        this.billingList = billingList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_biling, null);
        return new MyViewHolder(itemLayoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;

        viewHolder.invoice_no.setText("" + billingList.get(position).invoice);
        viewHolder.date.setText("" + billingList.get(position).date);

        viewHolder.imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String s = billingList.get(position).download_invoice.split("=")[1];
                    if(s.equalsIgnoreCase("0")){
                        Toast.makeText(context, "Invoice Download Failed. Please Contact Admin.", Toast.LENGTH_SHORT).show();
                    } else {
                        ((MyBillingActivity)context).downloadInvoice(billingList.get(position).download_invoice,"Carrocare_Invoice_"+billingList.get(position).razorpay_payment_id+".pdf");
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
        return billingList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView invoice_no, date;
        ImageView imgDownload;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            invoice_no = itemLayoutView.findViewById(R.id.invoice_no);
            date = itemLayoutView.findViewById(R.id.date);
            imgDownload = itemLayoutView.findViewById(R.id.imgDownload);
        }
    }
}
