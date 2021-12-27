package com.muvierecktech.carrocare.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.RenewActivity;
import com.muvierecktech.carrocare.activity.VehicleListActivity;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.DatabaseHelper;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.model.OrdersList;

import java.util.HashMap;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RenewOrderAdapter extends RecyclerView.Adapter {
    public Context context;
    List<OrdersList.Orders> ordersList;
    String header;
    DatabaseHelper databaseHelper;
    SessionManager sessionManager;
    String customerid,token,action,carimage,carmakemodel,carno,onetimecarprice,carid,paidMonths,fineAmount,tottal_amt,rn_total_amount;

    public RenewOrderAdapter(Context context, List<OrdersList.Orders> orders) {
        this.context = context;
        this.ordersList = orders;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_renew, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;

        sessionManager = new SessionManager(context);
        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        databaseHelper = new DatabaseHelper(context);
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);

//        if(ordersList.get(position).service_type.equalsIgnoreCase("Wash")){
//            viewHolder.card.setVisibility(View.VISIBLE);
//        }else{
//            viewHolder.card.setVisibility(View.GONE);
//        }

        if(ordersList.get(position).service_type.equalsIgnoreCase("Wash")){
            viewHolder.top.setVisibility(View.VISIBLE);
        }else{
            viewHolder.top.setVisibility(View.GONE);
        }

        rn_total_amount = ordersList.get(position).total_amount;

        if (ordersList.get(position).payment_type.equalsIgnoreCase("Monthly")){
            viewHolder.valid.setText(ordersList.get(position).next_due);
        }else{
            viewHolder.valid.setText(ordersList.get(position).valid);
        }


        if(ordersList.get(position).service_type.equalsIgnoreCase("Wash") &&
                ordersList.get(position).status.equalsIgnoreCase("Cancelled")){
            viewHolder.add_smart_checkout.setVisibility(View.GONE);
        }else{
            viewHolder.add_smart_checkout.setVisibility(View.VISIBLE);
        }

        viewHolder.status.setText(ordersList.get(position).status);
        viewHolder.date.setText(""+ordersList.get(position).date_and_time);
        viewHolder.service_type.setText(ordersList.get(position).service_type);
        viewHolder.package_type.setText(ordersList.get(position).package_type);
        viewHolder.vec_make.setText(ordersList.get(position).vehicle_make+"-"+ordersList.get(position).vehicle_model);
        viewHolder.vehicleno.setText(ordersList.get(position).vehicle_no);
        viewHolder.pack_amount.setText("₹ "+rn_total_amount);


        viewHolder.add_smart_checkout.setTag(viewHolder);
        viewHolder.add_smart_checkout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyViewHolder viewHolder = (MyViewHolder) buttonView.getTag();
                final int pos = viewHolder.getAdapterPosition();

                final String carprice,carid,onetimeService;

                carprice = ordersList.get(pos).package_value;
                carid = ordersList.get(pos).vehicle_id;
                onetimeService = ordersList.get(pos).service_type;

                if(ordersList.get(pos).package_value.equalsIgnoreCase("100")){
                    ((RenewActivity)context).binding.popupCard1.setVisibility(View.VISIBLE);
                    ((RenewActivity)context).binding.serviceType.setText(ordersList.get(pos).service_type);
                    ((RenewActivity)context).binding.subscriptionType.setText(ordersList.get(pos).payment_type);
                    ((RenewActivity)context).binding.packageType.setText(ordersList.get(pos).package_type);
                    ((RenewActivity)context).binding.packageMrp.setText("₹ " +ordersList.get(pos).package_value);
                    ((RenewActivity)context).binding.total.setText("₹ " +ordersList.get(pos).package_value);
                    ((RenewActivity)context).binding.totalAmount.setText("₹ " +ordersList.get(pos).package_value);

                }

                else{

                    if(ordersList.get(pos).service_type.equalsIgnoreCase("Wash")) {
                        ((RenewActivity) context).binding.popupCard.setVisibility(View.VISIBLE);
                        ((RenewActivity) context).binding.extraL0.setVisibility(View.GONE);
                        ((RenewActivity) context).binding.serviceType1.setText(ordersList.get(pos).service_type);
                        ((RenewActivity) context).binding.packageType1.setText(ordersList.get(pos).package_type);
                        ((RenewActivity) context).binding.packageMrp1.setText("₹ " + ordersList.get(pos).package_value);
                        ((RenewActivity) context).binding.subscriptionType1.setVisibility(View.VISIBLE);
                        ((RenewActivity) context).binding.subscriptionType01.setVisibility(View.GONE);
                    }else if(ordersList.get(pos).service_type.equalsIgnoreCase("AddOn") &&
                            !ordersList.get(pos).package_value.equalsIgnoreCase("100")){
                        ((RenewActivity) context).binding.popupCard.setVisibility(View.VISIBLE);
                        ((RenewActivity) context).binding.extraL0.setVisibility(View.VISIBLE);
                        ((RenewActivity) context).binding.serviceType1.setText(ordersList.get(pos).service_type);
                        ((RenewActivity) context).binding.packageType1.setText(ordersList.get(pos).package_type);
                        ((RenewActivity) context).binding.packageMrp1.setText("₹ " + ordersList.get(pos).package_value);
                        ((RenewActivity) context).binding.subscriptionType1.setVisibility(View.GONE);
                        ((RenewActivity) context).binding.subscriptionType01.setVisibility(View.VISIBLE);
                        ((RenewActivity) context).binding.subscriptionType01.setText(Constant.ONETIME);
                    }
                }

                ((RenewActivity)context).checkOnetime(carprice,carid,onetimeService);

                final String servicetype,carmakemodel,carno;

                servicetype = ordersList.get(pos).service_type;
                //carid = ordersList.get(pos).vehicle_id;
                carmakemodel = ordersList.get(pos).vehicle_make+"-"+ordersList.get(pos).vehicle_model;
                carno = ordersList.get(pos).vehicle_no;

                final String finalCarid = carid;

                ((RenewActivity)context).binding.addToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ordersList.get(pos).service_type.equalsIgnoreCase("AddOn") &&
                                !ordersList.get(pos).package_value.equalsIgnoreCase("100")) {
                            if (((RenewActivity) context).binding.preferDate.getText().length() > 0 && !TextUtils.isEmpty(((RenewActivity) context).time)) {
                                ((RenewActivity) context).checkIfExists(servicetype, finalCarid, carmakemodel, carno);
                            } else {
                                Toast.makeText(context, Constant.CHOOSEDATETIME, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            ((RenewActivity) context).checkIfExists(servicetype, finalCarid, carmakemodel, carno);
                        }
                    }
                });

                ((RenewActivity)context).binding.addToCart1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((RenewActivity)context).binding.preferDate1.getText().length()>0 && !TextUtils.isEmpty(((RenewActivity)context).time)){
                            ((RenewActivity)context).checkIfExists1( finalCarid,carmakemodel,carno,carprice);
                        }else {
                            Toast.makeText(context,Constant.CHOOSEDATETIME,Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                /*carimage = "1";
                carmakemodel = ordersList.get(pos).vehicle_make+"-"+ordersList.get(pos).vehicle_model;
                carno = ordersList.get(pos).vehicle_no;
                carid = ordersList.get(pos).vehicle_id;
                fineAmount = "0";
                tottal_amt = ordersList.get(pos).total_amount;

                if (ordersList.get(pos).service_type.equalsIgnoreCase("AddOn")){
                    action = Constant.ACTIONEXTRAONE;
                }else if (ordersList.get(pos).service_type.equalsIgnoreCase("Wash")){
                    action = Constant.ACTIONWASHONE;
                }

                onetimecarprice = ordersList.get(pos).package_value;

                if(isChecked){
                    databaseHelper.CheckOrderExists(action,carid);

                    long result = databaseHelper.addCart(customerid,token,action,carimage,carmakemodel,carno,onetimecarprice,carid,paidMonths,fineAmount,tottal_amt);

                    if(result > 0){
                        Toast.makeText(context, "Added to Smart Checkout ", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "Failed. ", Toast.LENGTH_SHORT).show();
                    }

                    ((RenewActivity)context).binding.bottomLl.setVisibility(View.VISIBLE);

                }
                else if(!isChecked){
                    databaseHelper.CheckOrderExists(action,carid);
                    ((RenewActivity)context).binding.bottomLl.setVisibility(View.GONE);
                }*/
            }
        });

    }
    @Override
    public int getItemCount() {
        return ordersList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView service_type,package_type,vec_make,vehicleno,pack_amount,date,status,valid;
        CheckBox add_smart_checkout;
        CardView card;
        LinearLayout top;
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            card = (CardView)itemLayoutView.findViewById(R.id.rn_card);
            service_type = (TextView)itemLayoutView.findViewById(R.id.rn_service_type);
            package_type = (TextView)itemLayoutView.findViewById(R.id.rn_pack_type);
            vehicleno = (TextView)itemLayoutView.findViewById(R.id.rn_vec_no);
            vec_make = (TextView)itemLayoutView.findViewById(R.id.rn_vec_model);
            pack_amount = (TextView)itemLayoutView.findViewById(R.id.rn_pack_amount);
            date = (TextView)itemLayoutView.findViewById(R.id.rn_pay_date);
            status = (TextView)itemLayoutView.findViewById(R.id.rn_status);
            valid = (TextView)itemLayoutView.findViewById(R.id.rn_valid);
            add_smart_checkout = (CheckBox)itemLayoutView.findViewById(R.id.rn_check_smart);
            top = (LinearLayout)itemLayoutView.findViewById(R.id.ll_top);
        }
    }
}
