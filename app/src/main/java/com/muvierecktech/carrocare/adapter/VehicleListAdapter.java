package com.muvierecktech.carrocare.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.CartActivity;
import com.muvierecktech.carrocare.activity.PaymentOptionActivity;
import com.muvierecktech.carrocare.activity.VehicleListActivity;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.DatabaseHelper;
import com.muvierecktech.carrocare.common.MyDatabaseHelper;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.model.VehicleDetails;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class VehicleListAdapter extends RecyclerView.Adapter {
    public Context context;
    List<VehicleDetails.VecDetails> vecDetails;
    String header,price,carname;
    MyDatabaseHelper databaseHelper;
    SessionManager sessionManager;
    String customerid,token,action,type,carimage,carmakemodel,carno,onetimecarprice,carid,paidMonths,fineAmount,tottal_amt;

    public VehicleListAdapter(Context context, List<VehicleDetails.VecDetails> apartment, String carprice, String carname, String header) {
        this.context = context;
        this.vecDetails = apartment;
        this.price = carprice;
        this.header = header;
        this.carname = carname;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehicle, null);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
         final MyViewHolder viewHolder = (MyViewHolder) holder;

        sessionManager = new SessionManager(context);
        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        databaseHelper = new MyDatabaseHelper(context);
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);

        type = header;

        if (type.equalsIgnoreCase(Constant.ADDON)){
            action = Constant.ACTIONEXTRAONE;
        } else if (type.equalsIgnoreCase(Constant.EXTRAINT)){
            action = Constant.ACTIONONE;
        } else if (type.equalsIgnoreCase(Constant.DISINSFECTION)){
            action = Constant.ACTIONDISONE;
        } else {
            action = Constant.ACTIONWASHONE;
        }

//        if(header.equalsIgnoreCase(Constant.EXTRAINT)){
//            viewHolder.add_smart_checkout.setVisibility(View.GONE);
//        }else{
//            viewHolder.add_smart_checkout.setVisibility(View.VISIBLE);
//        }

//        carimage = vecDetails.get(position).vehicle_image;
//        carmakemodel = vecDetails.get(position).vehicle_make+"-"+vecDetails.get(position).vehicle_model;
//        carno = vecDetails.get(position).vehicle_no;
//        carid = vecDetails.get(position).vehicle_id;
        onetimecarprice = price;
        paidMonths = "1";
        fineAmount = "0";
        tottal_amt = price;

//        viewHolder.vec_type.setText(vecDetails.get(position).vehicle_Type);
        viewHolder.vec_makemodel.setText(vecDetails.get(position).vehicle_make+"-"+vecDetails.get(position).vehicle_model);
//        viewHolder.category.setText(vecDetails.get(position).vehicle_category);
        viewHolder.vehicleno.setText(vecDetails.get(position).vehicle_no);
        viewHolder.color.setText(vecDetails.get(position).vehicle_color);
        viewHolder.apart_name.setText(vecDetails.get(position).vehicle_apartment_name);
        viewHolder.parkinglotno.setText(vecDetails.get(position).vehicle_parking_lot_no);
        viewHolder.parkingarea.setText(vecDetails.get(position).vehicle_parking_area);
        viewHolder.preferredschedule.setText(vecDetails.get(position).vehicle_preferred_schedule);
        viewHolder.preferredtime.setText(vecDetails.get(position).vehicle_preferred_time);
        viewHolder.subscribe.setTag(viewHolder);
        viewHolder.subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyViewHolder viewHolder1 = (MyViewHolder) view.getTag();
                int pos = viewHolder1.getAdapterPosition();
                Intent intent = new Intent(context, PaymentOptionActivity.class);
                intent.putExtra(Constant.CARID,vecDetails.get(pos).vehicle_id);
                intent.putExtra(Constant.CARTYPE,vecDetails.get(pos).vehicle_Type);
                intent.putExtra(Constant.CARIMAGE,vecDetails.get(pos).vehicle_image);
                intent.putExtra(Constant.CARNO,vecDetails.get(pos).vehicle_no);
                intent.putExtra(Constant.CARMAKEMODEL,vecDetails.get(pos).vehicle_make+"-"+vecDetails.get(pos).vehicle_model);
                intent.putExtra(Constant.PARKINGLOT,vecDetails.get(pos).vehicle_parking_lot_no);
                intent.putExtra(Constant.CARCOLOR,vecDetails.get(pos).vehicle_color);
                intent.putExtra(Constant.PARKINGAREA,vecDetails.get(pos).vehicle_parking_area);
                intent.putExtra(Constant.APARTNAME,vecDetails.get(pos).vehicle_apartment_name);
                intent.putExtra(Constant.PREFERREDSCHEDULE,vecDetails.get(pos).vehicle_preferred_schedule);
                intent.putExtra(Constant.PREFERREDTIME,vecDetails.get(pos).vehicle_preferred_time);
                intent.putExtra(Constant.CARPRICE,price);
                intent.putExtra(Constant.CARCATEGORY,vecDetails.get(pos).vehicle_category);
                intent.putExtra(Constant.SERVICETYPE,header);
                if (carname.isEmpty()){
                    intent.putExtra(Constant.CARNAME,"ExtraInterior");
                }else {
                    intent.putExtra(Constant.CARNAME,carname);
                }

                context.startActivity(intent);
            }
        });
        Picasso.get()
                .load(vecDetails.get(position).vehicle_image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(viewHolder.carimage);
//        viewHolder.card.setBackground(vecDetails.get(position).vehicle_image);

        viewHolder.add_smart_checkout.setTag(viewHolder);
        viewHolder.add_smart_checkout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyViewHolder viewHolder = (MyViewHolder) buttonView.getTag();
                int pos = viewHolder.getAdapterPosition();

                carimage = vecDetails.get(pos).vehicle_image;
                carmakemodel = vecDetails.get(pos).vehicle_make+"-"+vecDetails.get(pos).vehicle_model;
                carno = vecDetails.get(pos).vehicle_no;
                carid = vecDetails.get(pos).vehicle_id;

                    if(isChecked){
                        databaseHelper.CheckOrderExists(action,carid);

                        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                        Date todayDate = new Date();
                        String thisDate = currentDate.format(todayDate);

                        int before_tax = Integer.parseInt(tottal_amt);
                        int taxAmt = ((18 * before_tax) / 100);
                        int finalAmt = taxAmt + before_tax;

                        String result = databaseHelper.AddUpdateOrder(action+"",
                                carimage+"",
                                carmakemodel+"",
                                carno+"",
                                onetimecarprice+"",
                                carid+"",
                                paidMonths+"",
                                fineAmount+"",
                                taxAmt+"",
                                tottal_amt+"",
                                String.valueOf(finalAmt),
                                thisDate+"",
                                "Any Time");
//
                        if(result.equalsIgnoreCase("1")){
                            Toast.makeText(context, "Added to Smart Checkout ", Toast.LENGTH_SHORT).show();
                            ((VehicleListActivity)context).showCartCount();
                        }else{
                            Toast.makeText(context, "Failed. ", Toast.LENGTH_SHORT).show();
                        }

                        ((VehicleListActivity)context).binding.addvehicle.setVisibility(View.GONE);
                        ((VehicleListActivity)context).binding.makepayment.setVisibility(View.VISIBLE);

                    }
                    else if(!isChecked){
                        databaseHelper.CheckOrderExists(action,carid);
                        ((VehicleListActivity)context).showCartCount();
                        ((VehicleListActivity)context).binding.addvehicle.setVisibility(View.VISIBLE);
                        ((VehicleListActivity)context).binding.makepayment.setVisibility(View.GONE);
                    }
            }
        });


    }
    @Override
    public int getItemCount() {
        return vecDetails.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView carimage;
        Button subscribe;
        TextView vec_type,vec_makemodel,category,vehicleno,color,apart_name,parkinglotno,parkingarea,preferredschedule,preferredtime;
        CardView card;
        CheckBox add_smart_checkout;
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            apart_name = (TextView)itemLayoutView.findViewById(R.id.apart_name);
            vehicleno = (TextView)itemLayoutView.findViewById(R.id.vec_no);
            vec_makemodel = (TextView)itemLayoutView.findViewById(R.id.vec_makemodel);
            color = (TextView)itemLayoutView.findViewById(R.id.vec_color);
            parkingarea = (TextView)itemLayoutView.findViewById(R.id.parking_area);
            parkinglotno = (TextView)itemLayoutView.findViewById(R.id.parking_lot);
            preferredschedule = (TextView)itemLayoutView.findViewById(R.id.pref_schedule);
            preferredtime = (TextView)itemLayoutView.findViewById(R.id.preftime);
            carimage = (ImageView) itemLayoutView.findViewById(R.id.car_img);
            subscribe = (Button) itemLayoutView.findViewById(R.id.subscribe);
            card  = (CardView)itemLayoutView.findViewById(R.id.card);
            add_smart_checkout = (CheckBox)itemLayoutView.findViewById(R.id.check_smart);
        }
    }

}
