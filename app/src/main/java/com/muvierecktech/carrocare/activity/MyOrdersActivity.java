package com.muvierecktech.carrocare.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.MyOrdersAdapter;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.SessionManager;

import com.muvierecktech.carrocare.databinding.ActivityMyOrdersBinding;
import com.muvierecktech.carrocare.model.OrdersList;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersActivity extends AppCompatActivity {
    ActivityMyOrdersBinding binding;
     SessionManager sessionManager;
     String token,customerid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my_orders);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_orders);
        sessionManager = new SessionManager(this);
        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);
        work();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyOrdersActivity.this,RenewActivity.class));
                finish();
            }
        });

    }

    private void work() {
        if (isNetworkAvailable()){
            ServicePrice();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MyOrdersActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection." );
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Ok".
                    work();
                }
            })
                    .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Action for "Cancel".
                            finish();
                        }
                    });

            final AlertDialog alert = dialog.create();
            alert.show();
        }
    }

    private void ServicePrice() {
        final KProgressHUD hud = KProgressHUD.create(MyOrdersActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<OrdersList> call = apiInterface.orderlist(token,customerid);
        call.enqueue(new Callback<OrdersList>() {
            @Override
            public void onResponse(Call<OrdersList> call, Response<OrdersList> response) {
                hud.dismiss();
                try{
                    if(response.isSuccessful()){
                        final OrdersList ordersList = response.body();
                        if (ordersList.code.equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            String json = gson.toJson(ordersList);
                            binding.noorders.setVisibility(View.GONE);
                            binding.ordersRc.setVisibility(View.VISIBLE);

                            MyOrdersAdapter mainAdapter = new MyOrdersAdapter(MyOrdersActivity.this,ordersList.orders);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyOrdersActivity.this,LinearLayoutManager.VERTICAL,false);
                            binding.ordersRc.setLayoutManager(linearLayoutManager);
                            binding.ordersRc.setAdapter(mainAdapter);

                        }else  if (ordersList.code.equalsIgnoreCase("203")) {
                            sessionManager.logoutUsers();
                        }else  if (ordersList.code.equalsIgnoreCase("201")){
                            binding.noorders.setVisibility(View.VISIBLE);
                            binding.bottomLl.setVisibility(View.GONE);
                            binding.ordersRc.setVisibility(View.GONE);
                        }
                    } else{
                        ApiConfig.responseToast(MyOrdersActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<OrdersList> call, Throwable t) {
                hud.dismiss();
                binding.noorders.setVisibility(View.VISIBLE);
                binding.bottomLl.setVisibility(View.GONE);
                binding.ordersRc.setVisibility(View.GONE);
                Toast.makeText(MyOrdersActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyOrdersActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}