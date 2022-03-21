package com.muvierecktech.carrocare.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.util.Log;
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
import com.muvierecktech.carrocare.adapter.VehicleListAdapter;
import com.muvierecktech.carrocare.common.DatabaseHelper;
import com.muvierecktech.carrocare.common.MyDatabaseHelper;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityVehicleListBinding;
import com.muvierecktech.carrocare.model.VehicleDetails;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleListActivity extends AppCompatActivity {
    public ActivityVehicleListBinding binding;
    String carname,carprice,cardesc,carimage,carid,header,token,customerid,servicetype;
    SessionManager sessionManager;
    MyDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_vehicle_list);
        sessionManager = new SessionManager(this);
        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);

        databaseHelper = new MyDatabaseHelper(this);

        showCartCount();

        binding.checkCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VehicleListActivity.this,CartActivity.class));
            }
        });

        Intent intent = getIntent();
        carname = intent.getStringExtra("carname");
        carprice = intent.getStringExtra("carprice");
        cardesc = intent.getStringExtra("cardesc");
        carimage = intent.getStringExtra("carimage");
        carid = intent.getStringExtra("carid");
        header = intent.getStringExtra("header");
        binding.carDetailsHeading.setText(header+" Details");
        binding.headerName.setText(carname);
        if (carname.startsWith("extra")||carname.startsWith("Extra") || carname.startsWith("EXTRA")){
            carname = "";
            binding.bottomLl.setVisibility(View.GONE);
        }

//        binding.carDetailsHeading.setPaintFlags(binding.carDetailsHeading.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.carDetails.setText(HtmlCompat.fromHtml(cardesc,0)+"");
        binding.subPriceHeading.setText("Subscription Price");
//        binding.subPriceHeading.setPaintFlags(binding.subPriceHeading.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.subPrice.setText("₹ "+carprice);
        binding.selectVehicle.setText("Select Vehicle");
//        binding.selectVehicle.setPaintFlags(binding.selectVehicle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        work();

        binding.addvehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VehicleListActivity.this,AddVehicleActivity.class);
                intent.putExtra("carname",carname);
                startActivity(intent);
//                finish();
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.makepayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VehicleListActivity.this,CartActivity.class));
            }
        });


    }

    private void work() {
        if (isNetworkAvailable()){
            VehicleDeatils();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(VehicleListActivity.this);
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

    private void VehicleDeatils() {
        final KProgressHUD hud = KProgressHUD.create(VehicleListActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<VehicleDetails> call = apiInterface.vehicledetails(customerid,token,carname);

        call.enqueue(new Callback<VehicleDetails>() {
            @Override
            public void onResponse(Call<VehicleDetails> call, Response<VehicleDetails> response) {
                final VehicleDetails vehicleDetails = response.body();
                hud.dismiss();
                if (vehicleDetails.code.equalsIgnoreCase("200")) {
                    Gson gson = new Gson();
                    String json = gson.toJson(vehicleDetails);
                    binding.novehicle.setVisibility(View.GONE);
                    binding.carslistRc.setVisibility(View.VISIBLE);

                    VehicleListAdapter mainAdapter = new VehicleListAdapter(VehicleListActivity.this,vehicleDetails.details,carprice,carname,header);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(VehicleListActivity.this,LinearLayoutManager.VERTICAL,false);
                    binding.carslistRc.setLayoutManager(linearLayoutManager);
                    binding.carslistRc.setAdapter(mainAdapter);
                }else if (vehicleDetails.code.equalsIgnoreCase("201")) {
                    binding.novehicle.setVisibility(View.VISIBLE);
                    binding.carslistRc.setVisibility(View.GONE);
                }else if (vehicleDetails.code.equalsIgnoreCase("203")) {
                    sessionManager.logoutUsers();
                }
            }
            @Override
            public void onFailure(Call<VehicleDetails> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(VehicleListActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
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

    @SuppressLint("LongLogTag")
    public void showCartCount(){
        //int totalItemOfCart = dbAdapter.cart_count;
        int totalItemOfCart = databaseHelper.getTotalItemOfCart();

        binding.cartCount.setText(String.valueOf(totalItemOfCart));

//        if(totalItemOfCart == 0){
//            binding.cartCount.setVisibility(View.INVISIBLE);
//        }else{
//           binding.cartCount.setText(String.valueOf(totalItemOfCart));
//        }

        Log.e("Total item of cart--->   ",""+totalItemOfCart);
    }
}