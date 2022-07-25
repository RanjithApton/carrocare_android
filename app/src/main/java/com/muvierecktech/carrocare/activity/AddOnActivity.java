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
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.MainAdapter;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;

import com.muvierecktech.carrocare.databinding.ActivityAddOnBinding;
import com.muvierecktech.carrocare.model.ServicePriceList;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOnActivity extends AppCompatActivity {
    ActivityAddOnBinding binding;
    String headername;
    SessionManager sessionManager;
    String description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_on);

        headername = Constant.ADDON;
        binding.headerName.setText(headername);

//        Intent intent = getIntent();
//        headername = intent.getStringExtra("headername");
//        binding.headerName.setText(headername);

        work();
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void work() {
        if (isNetworkAvailable()){
            ServicePrice();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(AddOnActivity.this);
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
        final KProgressHUD hud = KProgressHUD.create(AddOnActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ServicePriceList> call = apiInterface.Service(Constant.ADDONSERVICE);
        call.enqueue(new Callback<ServicePriceList>() {
            @Override
            public void onResponse(Call<ServicePriceList> call, Response<ServicePriceList> response) {
                hud.dismiss();
                try{
                    if(response.isSuccessful()){
                        final ServicePriceList servicePriceList = response.body();
                        if (servicePriceList.code.equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            String json = gson.toJson(servicePriceList);

                            binding.description.setText(HtmlCompat.fromHtml(servicePriceList.description+"",0));
                            description = servicePriceList.description;
                            MainAdapter mainAdapter = new MainAdapter(AddOnActivity.this,servicePriceList.services, headername);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddOnActivity.this,LinearLayoutManager.VERTICAL,false);
                            binding.carslistRc.setLayoutManager(linearLayoutManager);
                            binding.carslistRc.setAdapter(mainAdapter);

                            binding.info.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(AddOnActivity.this,InfoActivity.class);
                                    intent.putExtra("headername",Constant.ADDONSERVICE);
                                    intent.putExtra("description",description);
                                    startActivity(intent);
                                }
                            });
                        }
                    } else{
                        ApiConfig.responseToast(AddOnActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ServicePriceList> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(AddOnActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
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
//        Intent intent = new Intent(AddOnActivity.this,MainActivity.class);
//        startActivity(intent);
        finish();
    }
}