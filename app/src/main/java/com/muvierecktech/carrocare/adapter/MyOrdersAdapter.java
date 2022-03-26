package com.muvierecktech.carrocare.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.MyOrdersDetailActivity;
import com.muvierecktech.carrocare.activity.RenewActivity;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.model.OrdersList;
import com.razorpay.PaymentResultListener;

import java.io.Serializable;
import java.util.List;

public class MyOrdersAdapter extends RecyclerView.Adapter  {
    public Context context;
    List<OrdersList.Orders> ordersList;
    String header;

    public MyOrdersAdapter(Context context, List<OrdersList.Orders> orders) {
        this.context = context;
        this.ordersList = orders;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_orders, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
         final MyViewHolder viewHolder = (MyViewHolder) holder;

        viewHolder.date.setText(""+ordersList.get(position).date_and_time);
        viewHolder.payment_id.setText(""+ordersList.get(position).order_id);
        viewHolder.service_type.setText(ordersList.get(position).service_type);
        viewHolder.package_type.setText(ordersList.get(position).package_type);
        viewHolder.status.setText(ordersList.get(position).status);
        viewHolder.vehicleno.setText(ordersList.get(position).vehicle_no);
        viewHolder.card.setTag(viewHolder);
        viewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                int pos = viewHolder1.getAdapterPosition();
                Intent intent = new Intent(context, MyOrdersDetailActivity.class);
                intent.putExtra(Constant.DE_DATE,ordersList.get(pos).date_and_time);
                intent.putExtra(Constant.DE_ORDERID ,ordersList.get(pos).order_id);
                intent.putExtra(Constant.DE_SERVTYPE,ordersList.get(pos).service_type);
                intent.putExtra(Constant.DE_PAYTYPE ,ordersList.get(pos).payment_type);
                intent.putExtra(Constant.DE_PACKTYPE,ordersList.get(pos).package_type);
                intent.putExtra(Constant.DE_VECMAKE ,ordersList.get(pos).vehicle_make);
                intent.putExtra(Constant.DE_VECMODEL,ordersList.get(pos).vehicle_model);
                intent.putExtra(Constant.DE_VECNO ,ordersList.get(pos).vehicle_no);
                intent.putExtra(Constant.DE_VECID ,ordersList.get(pos).vehicle_id);
                intent.putExtra(Constant.DE_PACKVALUE,ordersList.get(pos).package_value);
                intent.putExtra(Constant.DE_TOTAMOUNT,ordersList.get(pos).total_amount);
                intent.putExtra(Constant.DE_GST,ordersList.get(pos).gst);
                intent.putExtra(Constant.DE_GSTAMOUNT,ordersList.get(pos).gst_amount);
                intent.putExtra(Constant.DE_SUB_TOTAMOUNT,ordersList.get(pos).sub_total_amount);
                intent.putExtra(Constant.DE_DISCOUNTMOUNT,ordersList.get(pos).discount_amount);
                intent.putExtra(Constant.DE_PAYMODE ,ordersList.get(pos).payment_mode);
                intent.putExtra(Constant.DE_PACKMONTH,ordersList.get(pos).paid_count);
                intent.putExtra(Constant.DE_VALID,ordersList.get(pos).valid);
                intent.putExtra(Constant.DE_NEXTDUE,ordersList.get(pos).next_due);
                intent.putExtra(Constant.DE_STATUS,ordersList.get(pos).status);
                intent.putExtra(Constant.DE_WASHDET,ordersList.get(pos).wash_details);
                intent.putExtra(Constant.DE_EXTRADET,ordersList.get(pos).extra_interior);
                intent.putExtra(Constant.DE_CANCEL,ordersList.get(pos).cancel_subscription);
                intent.putExtra(Constant.DE_PAYMENT_HISTORY,ordersList.get(pos).payment_history);
                intent.putExtra(Constant.DE_SCHEDULE_DATE, ordersList.get(pos).schedule_date);
                intent.putExtra(Constant.DE_SCHEDULE_TIME, ordersList.get(pos).schedule_time);
                intent.putExtra(Constant.DE_WORK_DONE, ordersList.get(pos).work_done);
                intent.putExtra(Constant.DE_IMAGE, ordersList.get(pos).vehicle_image);
                intent.putExtra(Constant.DE_IMAGE_DATE, ordersList.get(pos).image_date_and_time);
                intent.putExtra(Constant.DE_PAYMENTDETAILS,(Serializable) ordersList.get(pos).payment_details);
                context.startActivity(intent);
            }
        });
        viewHolder.viewmore.setTag(viewHolder);
        viewHolder.viewmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                int pos = viewHolder1.getAdapterPosition();
                Intent intent = new Intent(context, MyOrdersDetailActivity.class);
                intent.putExtra(Constant.DE_DATE,ordersList.get(pos).date_and_time);
                intent.putExtra(Constant.DE_ORDERID ,ordersList.get(pos).order_id);
                intent.putExtra(Constant.DE_SERVTYPE,ordersList.get(pos).service_type);
                intent.putExtra(Constant.DE_PAYTYPE ,ordersList.get(pos).payment_type);
                intent.putExtra(Constant.DE_PACKTYPE,ordersList.get(pos).package_type);
                intent.putExtra(Constant.DE_VECMAKE ,ordersList.get(pos).vehicle_make);
                intent.putExtra(Constant.DE_VECMODEL,ordersList.get(pos).vehicle_model);
                intent.putExtra(Constant.DE_VECNO ,ordersList.get(pos).vehicle_no);
                intent.putExtra(Constant.DE_VECID ,ordersList.get(pos).vehicle_id);
                intent.putExtra(Constant.DE_PACKVALUE,ordersList.get(pos).package_value);
                intent.putExtra(Constant.DE_TOTAMOUNT,ordersList.get(pos).total_amount);
                intent.putExtra(Constant.DE_GST,ordersList.get(pos).gst);
                intent.putExtra(Constant.DE_GSTAMOUNT,ordersList.get(pos).gst_amount);
                intent.putExtra(Constant.DE_SUB_TOTAMOUNT,ordersList.get(pos).sub_total_amount);
                intent.putExtra(Constant.DE_DISCOUNTMOUNT,ordersList.get(pos).discount_amount);
                intent.putExtra(Constant.DE_PAYMODE ,ordersList.get(pos).payment_mode);
                intent.putExtra(Constant.DE_PACKMONTH,ordersList.get(pos).paid_count);
                intent.putExtra(Constant.DE_VALID,ordersList.get(pos).valid);
                intent.putExtra(Constant.DE_NEXTDUE,ordersList.get(pos).next_due);
                intent.putExtra(Constant.DE_STATUS,ordersList.get(pos).status);
                intent.putExtra(Constant.DE_WASHDET,ordersList.get(pos).wash_details);
                intent.putExtra(Constant.DE_EXTRADET,ordersList.get(pos).extra_interior);
                intent.putExtra(Constant.DE_CANCEL,ordersList.get(pos).cancel_subscription);
                intent.putExtra(Constant.DE_SCHEDULE_DATE, ordersList.get(pos).schedule_date);
                intent.putExtra(Constant.DE_SCHEDULE_TIME, ordersList.get(pos).schedule_time);
                intent.putExtra(Constant.DE_WORK_DONE, ordersList.get(pos).work_done);
                intent.putExtra(Constant.DE_PAYMENT_HISTORY,ordersList.get(pos).payment_history);
                intent.putExtra(Constant.DE_IMAGE, ordersList.get(pos).vehicle_image);
                intent.putExtra(Constant.DE_IMAGE_DATE, ordersList.get(pos).image_date_and_time);
                intent.putExtra(Constant.DE_PAYMENTDETAILS,(Serializable)ordersList.get(pos).payment_details);
                context.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return ordersList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView service_type,package_type,status,vehicleno,payment_id,date;
        CardView card;
        Button viewmore;
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            card = (CardView) itemLayoutView.findViewById(R.id.cardorder);
            service_type = (TextView)itemLayoutView.findViewById(R.id.service_type);
            package_type = (TextView)itemLayoutView.findViewById(R.id.package_type);
            vehicleno = (TextView)itemLayoutView.findViewById(R.id.vec_no);
            status = (TextView)itemLayoutView.findViewById(R.id.status);
            payment_id = (TextView)itemLayoutView.findViewById(R.id.payment_id);
            date = (TextView)itemLayoutView.findViewById(R.id.date);
            viewmore = (Button) itemLayoutView.findViewById(R.id.view_more);
        }
    }

}
