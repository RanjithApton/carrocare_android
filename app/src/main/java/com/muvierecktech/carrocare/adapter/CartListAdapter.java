package com.muvierecktech.carrocare.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.activity.CartActivity;
import com.muvierecktech.carrocare.common.MyDatabaseHelper;
import com.muvierecktech.carrocare.model.CartList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.viewHolder> {
    Context context;
    Activity activity;
    ArrayList<CartList> arrayList;
    MyDatabaseHelper databaseHelper;
    public static int cart_count;

    public CartListAdapter(Context context, Activity activity, ArrayList<CartList> arrayList) {
        this.activity = activity;
        this.context = context;
        this.arrayList = arrayList;
        cart_count = arrayList.size();
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final CartList dbm = arrayList.get(position);

        databaseHelper = new MyDatabaseHelper(context);

        Picasso.get()
                .load(dbm.getImge())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.car_img);

        holder.car_name.setText(dbm.getModel());
        holder.car_no.setText(dbm.getNumber());
        holder.action.setText(dbm.getType());
        if(holder.action.getText().equals("onetime_wash_payment")){
            holder.type.setText("Wash");
        }else if(holder.action.getText().equals("onetime_wax_payment")){
            holder.type.setText("Add On");
        } else if(holder.action.getText().equals("onetime_payment")){
            holder.type.setText("Extra Interior");
        } else if(holder.action.getText().equals("onetime_disinsfection_payment")){
            holder.type.setText("Disinsfection");
        }
        holder.month.setText(dbm.getPaidmonth());
        if(holder.month.getText().equals("1")){
            holder.month.setText(dbm.getPaidmonth()+ " Month");
        }else if(holder.month.getText().equals("0")){
            holder.month.setText("1 Month");
        }else{
            holder.month.setText(dbm.getPaidmonth()+ " Months");
        }
        holder.pack_amount.setText("₹ " +dbm.getCarprice());
        holder.before_total_amount.setText("₹ " +dbm.getTotal());
        holder.tax_percentage.setText(dbm.getGst()+"%");
        holder.tax_total_amount.setText("₹ " +dbm.getGstamount());
        holder.total.setText("₹ " +dbm.getSub_total());

        holder.del_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Remove");
                builder.setMessage("Are you remove to ( " + arrayList.get(position).getModel() + " )?");;
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.DeleteOrderData(dbm.getType(), dbm.getCarid());
                        Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                        arrayList.remove(dbm);
                        ((CartActivity)activity).getData();
                        activity.invalidateOptionsMenu();
                        notifyDataSetChanged();
                        if (getItemCount() == 0) {
                            ((CartActivity)activity).binding.novehicle.setVisibility(View.VISIBLE);
                            ((CartActivity)activity).binding.lyttotal.setVisibility(View.GONE);
                        }
                    }
                });
                builder.setNegativeButton("No",null);
                builder.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }



    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView car_img;
        ImageView del_img;
        TextView car_name,car_no,action,type,month,pack_amount, before_total_amount, tax_percentage, tax_total_amount, total;
        public viewHolder(View itemView) {
            super(itemView);
            car_img = (ImageView) itemView.findViewById(R.id.cart_car_img);
            del_img = (ImageView) itemView.findViewById(R.id.delete_img);
            car_name = (TextView) itemView.findViewById(R.id.cart_model);
            car_no = (TextView) itemView.findViewById(R.id.cart_veno);
            action = (TextView) itemView.findViewById(R.id.cart_action);
            type = (TextView) itemView.findViewById(R.id.cart_serice);
            month = (TextView) itemView.findViewById(R.id.cart_sub_type);
            total = (TextView) itemView.findViewById(R.id.car_price);
            pack_amount = (TextView) itemView.findViewById(R.id.pack_amount);
            before_total_amount = (TextView) itemView.findViewById(R.id.before_total_amount);
            tax_total_amount = (TextView) itemView.findViewById(R.id.tax_total_amount);
            tax_percentage = (TextView) itemView.findViewById(R.id.tax_percentage);
        }
    }
}
