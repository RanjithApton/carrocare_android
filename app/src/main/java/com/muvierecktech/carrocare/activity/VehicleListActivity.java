package com.muvierecktech.carrocare.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.PreferredAdapter;
import com.muvierecktech.carrocare.adapter.VehicleListAdapter;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.DatabaseHelper;
import com.muvierecktech.carrocare.common.MyDatabaseHelper;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityVehicleListBinding;
import com.muvierecktech.carrocare.model.VehicleDetails;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleListActivity extends AppCompatActivity {
    public ActivityVehicleListBinding binding;
    String carname,carprice,cardesc,carimage,carid,header,token,customerid,servicetype;
    SessionManager sessionManager;
    MyDatabaseHelper databaseHelper;

    String preTime[] = {Constant.ANYTIME, "9.00 AM - 10.00 AM","10.00 AM - 11.00 AM","11.00 AM - 12.00 PM","12.00 PM - 1.00 PM","6.00 PM - 7.00 PM","7.00 PM - 8.00 PM"};
    public static String time;
    DatePickerDialog picker;

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

    public void addCartWithTime(String action, String carimage, String carmakemodel, String carno, String onetimecarprice, String carid, String paidMonths, String fineAmount, String tottal_amt) {

        binding.popupCancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.popupCard1.setVisibility(View.GONE);
                work();
            }
        });

        binding.preferredtimeEdt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.preferDate1.getText().toString())){
                    Toast.makeText(VehicleListActivity.this,"Choose Preferred Schedule",Toast.LENGTH_SHORT).show();
                }else {
                    binding.timeLl.setVisibility(View.VISIBLE);
                    PreferredAdapter preferredAdapter = new PreferredAdapter(VehicleListActivity.this, preTime, "smart_check");
                    LinearLayoutManager linearLayoutManage = new LinearLayoutManager(VehicleListActivity.this, LinearLayoutManager.VERTICAL, false);
                    binding.timeRc.setLayoutManager(linearLayoutManage);
                    binding.timeRc.setAdapter(preferredAdapter);
                }
            }
        });
        binding.timeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.timeLl.setVisibility(View.GONE);
            }
        });

        binding.preferDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(VehicleListActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                binding.preferDate1.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                //picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                picker.getDatePicker().setMinDate(System.currentTimeMillis()+24*60*60*1000);
                picker.show();
            }
        });

        binding.addToCart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.preferDate1.getText().length()>0 && !TextUtils.isEmpty(time)){
                    databaseHelper.CheckOrderExists(action,carid);

                    int before_tax = Integer.parseInt(tottal_amt);
                    int taxAmt = ((Constant.GST_PERCENTAGE * before_tax) / 100);
                    int finalAmt = taxAmt + before_tax;

                    String result = databaseHelper.AddUpdateOrder(action+"",
                            carimage+"",
                            carmakemodel+"",
                            carno+"",
                            onetimecarprice+"",
                            carid+"",
                            paidMonths+"",
                            fineAmount+"",
                            tottal_amt+"",
                            Constant.GST_PERCENTAGE+"",
                            taxAmt+"",
                            String.valueOf(finalAmt),
                            binding.preferDate1.getText().toString()+"",
                            time+"");

                    if(result.equalsIgnoreCase("1")){
                        Toast.makeText(VehicleListActivity.this, "Added to Smart Checkout ", Toast.LENGTH_SHORT).show();
                        showCartCount();
                        binding.popupCard1.setVisibility(View.GONE);
                        startActivity(new Intent(VehicleListActivity.this,CartActivity.class));
                        finish();
                    }else{
                        Toast.makeText(VehicleListActivity.this, "Failed. ", Toast.LENGTH_SHORT).show();
                        binding.popupCard1.setVisibility(View.GONE);
                    }

                }else {
                    Toast.makeText(VehicleListActivity.this,Constant.CHOOSEDATETIME,Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}