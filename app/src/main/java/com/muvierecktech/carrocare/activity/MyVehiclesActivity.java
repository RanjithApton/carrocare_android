package com.muvierecktech.carrocare.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.MyVehicleAdapter;
import com.muvierecktech.carrocare.adapter.VehicleListAdapter;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityMyVehiclesBinding;
import com.muvierecktech.carrocare.model.VehicleDetails;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import java.util.HashMap;

public class MyVehiclesActivity extends AppCompatActivity {
    ActivityMyVehiclesBinding binding;
    SessionManager sessionManager;
    String token, customerid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_vehicles);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_vehicles);

        sessionManager = new SessionManager(this);
        HashMap<String, String> hashMap = sessionManager.getUserDetails();
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);

        work();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startActivity(new Intent(MyVehiclesActivity.this, MainActivity.class));
                finish();
            }
        });

        binding.addvehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyVehiclesActivity.this, MyVechiclesAddActivity.class));
                finish();
            }
        });

    }

    private void work() {
        if (isNetworkAvailable()) {
            VehicleDeatils();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MyVehiclesActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection.");
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

    private void VehicleDeatils() {
        final KProgressHUD hud = KProgressHUD.create(MyVehiclesActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<VehicleDetails> call = apiInterface.myvehicledetails(customerid, token);

        call.enqueue(new Callback<VehicleDetails>() {
            @Override
            public void onResponse(Call<VehicleDetails> call, Response<VehicleDetails> response) {
                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        final VehicleDetails vehicleDetails = response.body();
                        if (vehicleDetails.code.equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            String json = gson.toJson(vehicleDetails);
                            binding.novehicle.setVisibility(View.GONE);
                            binding.vehiclelistRc.setVisibility(View.VISIBLE);

                            MyVehicleAdapter myVehicleAdapter = new MyVehicleAdapter(MyVehiclesActivity.this, vehicleDetails.details);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyVehiclesActivity.this, LinearLayoutManager.VERTICAL, false);
                            binding.vehiclelistRc.setLayoutManager(linearLayoutManager);
                            binding.vehiclelistRc.setAdapter(myVehicleAdapter);
                        } else if (vehicleDetails.code.equalsIgnoreCase("201")) {
                            binding.novehicle.setVisibility(View.VISIBLE);
                            binding.vehiclelistRc.setVisibility(View.GONE);
                        } else if (vehicleDetails.code.equalsIgnoreCase("203")) {
                            sessionManager.logoutUsers();
                        }
                    } else {
                        ApiConfig.responseToast(MyVehiclesActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<VehicleDetails> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(MyVehiclesActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
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
    protected void onRestart() {
        super.onRestart();
        work();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}