package com.muvierecktech.carrocare.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.AddVehicleActivity;
import com.muvierecktech.carrocare.activity.InternalwashActivity;
import com.muvierecktech.carrocare.activity.MainActivity;
import com.muvierecktech.carrocare.activity.MyOrdersDetailActivity;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.model.OrdersList;

import java.io.Serializable;
import java.util.List;

public class IntWashOrderAdapter extends RecyclerView.Adapter {
    public Context context;
    List<OrdersList.Orders> ordersList;
    private int lastSelectedPosition = -1;
    public IntWashOrderAdapter(Context context, List<OrdersList.Orders> orders) {
        this.context = context;
        this.ordersList = orders;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pick_order, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.order_lay.setVisibility(View.GONE);
        //viewHolder.payment_id.setText(""+ordersList.get(position).order_id);
        final OrdersList.Orders payid = ordersList.get(position);


        if(ordersList.get(position).package_type.equalsIgnoreCase("Bike")){
            viewHolder.order_lay.setVisibility(View.GONE);
        }else if(ordersList.get(position).service_type.equalsIgnoreCase("AddOn")){
            viewHolder.order_lay.setVisibility(View.GONE);
        }else if(ordersList.get(position).service_type.equalsIgnoreCase("Wash")){
            viewHolder.order_lay.setVisibility(View.VISIBLE);
            viewHolder.payment_id.setText(payid.order_id);
            viewHolder.payment_id.setTag(viewHolder);
            viewHolder.payment_id.setChecked(lastSelectedPosition == position);
            viewHolder.payment_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyViewHolder viewHolder1 = (MyViewHolder) v.getTag();
                    int pos = viewHolder1.getAdapterPosition();
                    lastSelectedPosition = pos;
                    notifyDataSetChanged();
//                        Intent intent = new Intent(context, InternalwashActivity.class);
//                        intent.putExtra(Constant.DE_ORDERID ,ordersList.get(pos).order_id);
//                        intent.putExtra(Constant.DE_VECID,ordersList.get(pos).vehicle_id);
                    ((MainActivity)context).binding.orderEdt.setText(ordersList.get(pos).order_id);
                    //((InternalwashActivity)context).binding.vehicleId.setText(ordersList.get(pos).vehicle_id);
                    //InternalwashActivity.order_id=(ordersList.get(pos).order_id);
                    ((MainActivity)context).binding.orderrl.setVisibility(View.GONE);


                    Intent intent = new Intent(context, InternalwashActivity.class);
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
                    intent.putExtra(Constant.DE_PAYMENTDETAILS,(Serializable) ordersList.get(pos).payment_details);
                    context.startActivity(intent);

                    ((MainActivity)context).binding.detailrl.setVisibility(View.GONE);
                    ((MainActivity)context).binding.orderrl.setVisibility(View.GONE);
                    ((MainActivity)context).binding.popupinternal.setVisibility(View.GONE);


                }
            });
        }

        viewHolder.view_details.setTag(viewHolder);
        viewHolder.view_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyViewHolder viewHolder1 = (MyViewHolder) v.getTag();
                int pos = viewHolder1.getAdapterPosition();

                ((MainActivity)context).binding.date.setText(ordersList.get(pos).date_and_time);
                ((MainActivity)context).binding.paymentId.setText(ordersList.get(pos).order_id);
                ((MainActivity)context).binding.serviceType.setText(ordersList.get(pos).service_type);
                ((MainActivity)context).binding.paymentType.setText(ordersList.get(pos).payment_type);
                ((MainActivity)context).binding.packageType.setText(ordersList.get(pos).package_type);
                ((MainActivity)context).binding.vecMake.setText(ordersList.get(pos).vehicle_make);
                ((MainActivity)context).binding.vecModel.setText(ordersList.get(pos).vehicle_model);
                ((MainActivity)context).binding.vecNo.setText(ordersList.get(pos).vehicle_no);
                ((MainActivity)context).binding.packageValue.setText("₹ "+ordersList.get(pos).package_value);
                ((MainActivity)context).binding.totalAmount.setText("₹ "+ordersList.get(pos).total_amount);
                ((MainActivity)context).binding.paymentMode.setText(ordersList.get(pos).payment_mode);
                ((MainActivity)context).binding.paidCount.setText(ordersList.get(pos).paid_count);
                ((MainActivity)context).binding.status.setText(ordersList.get(pos).status);

                if(ordersList.get(pos).discount_amount.equalsIgnoreCase("0")){
                    ((MainActivity)context).binding.discountField.setVisibility(View.GONE);
                }else {
                    ((MainActivity)context).binding.discountAmount.setText(ordersList.get(pos).discount_amount);
                    ((MainActivity)context).binding.discountField.setVisibility(View.VISIBLE);
                }

                if(ordersList.get(pos).wash_details.equalsIgnoreCase("0")){
                    ((MainActivity)context).binding.valid.setText(ordersList.get(pos).valid);
                }else if(ordersList.get(pos).wash_details.equalsIgnoreCase("1")){
                    ((MainActivity)context).binding.valid.setText(ordersList.get(pos).next_due);
                }

                ((MainActivity)context).binding.detailrl.setVisibility(View.VISIBLE);
            }
        });


        /*((MainActivity)context).binding.buttonProcessed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyViewHolder viewHolder1 = (MyViewHolder) v.getTag();
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
                intent.putExtra(Constant.DE_PAYMENTDETAILS,(Serializable) ordersList.get(pos).payment_details);
                context.startActivity(intent);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RadioButton payment_id;
        Button view_details;
        RelativeLayout order_lay;
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            payment_id = (RadioButton) itemLayoutView.findViewById(R.id.payment_id);
            view_details = (Button) itemLayoutView.findViewById(R.id.view_details);
            order_lay = (RelativeLayout) itemLayoutView.findViewById(R.id.order_lay);
        }
    }
}
