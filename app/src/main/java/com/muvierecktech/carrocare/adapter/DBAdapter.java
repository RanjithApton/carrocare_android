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
import com.muvierecktech.carrocare.activity.CarDetailsActivity;
import com.muvierecktech.carrocare.activity.CartActivity;
import com.muvierecktech.carrocare.activity.MainActivity;
import com.muvierecktech.carrocare.activity.PaymentOptionActivity;
import com.muvierecktech.carrocare.common.DatabaseHelper;
import com.muvierecktech.carrocare.common.MyDatabaseHelper;
import com.muvierecktech.carrocare.model.DBModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class DBAdapter extends RecyclerView.Adapter<DBAdapter.viewHolder> {
    Context context;
    Activity activity;
    ArrayList<DBModel> arrayList;
    MyDatabaseHelper databaseHelper;
    public static int cart_count;

    public DBAdapter(Context context,Activity activity, ArrayList<DBModel> arrayList) {
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
        final DBModel dbm = arrayList.get(position);

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
        holder.total.setText("₹ " +dbm.getTotal());

        holder.del_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Remove");
                builder.setMessage("Are you remove to ( " + arrayList.get(position).getModel() + " )?");;
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

//                        int result = databaseHelper.deleteItem(dbm.getSno());
//
//                        if(result > 0){
//                            Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
//                            arrayList.remove(dbm);
//                            notifyDataSetChanged();
//                            Intent intent = new Intent(context,CartActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(intent);
//                        }else{
//                            Toast.makeText(context, "Error on Removeing, Check once", Toast.LENGTH_SHORT).show();
//                        }
                        databaseHelper.DeleteOrderData(dbm.getType(), dbm.getCarid());
                        Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                        arrayList.remove(dbm);
                        notifyDataSetChanged();
                        Intent intent = new Intent(context,CartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

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
        Button del_img;
        TextView car_name,car_no,action,type,month,total;
        public viewHolder(View itemView) {
            super(itemView);
            car_img = (ImageView) itemView.findViewById(R.id.cart_car_img);
            del_img = (Button) itemView.findViewById(R.id.remove);
            car_name = (TextView) itemView.findViewById(R.id.cart_model);
            car_no = (TextView) itemView.findViewById(R.id.cart_veno);
            action = (TextView) itemView.findViewById(R.id.cart_action);
            type = (TextView) itemView.findViewById(R.id.cart_serice);
            month = (TextView) itemView.findViewById(R.id.cart_sub_type);
            total = (TextView) itemView.findViewById(R.id.car_price);
        }
    }
}
