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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.muvierecktech.carrocare.model.OneTimeWashCheckout;
import com.muvierecktech.carrocare.model.VehicleDetails;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleListActivity extends AppCompatActivity {
    public ActivityVehicleListBinding binding;
    String carname,carprice,cardesc,carimage,carid,header,token,customerid,servicetype;
    SessionManager sessionManager;
    MyDatabaseHelper databaseHelper;

    String action,onetimeService,paidMonths,fineAmount,discountAmount,onetimecarprice;
    List<OneTimeWashCheckout.getResult> result;
    int totalAmountStr;
    String[] subsMonths = {"1 Month", "2 Months", "3 Months", "4 Months", "5 Months", "6 Months", "7 Months", "8 Months", "9 Months", "10 Months", "11 Months", "12 Months", "13 Months", "14 Months", "15 Months", "16 Months", "17 Months", "18 Months", "19 Months", "20 Months", "21 Months", "22 Months", "23 Months", "24 Months"};
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


        if(Constant.GST_PERCENTAGE != 0){
            binding.taxField.setVisibility(View.VISIBLE);
            binding.taxField1.setVisibility(View.VISIBLE);
            binding.taxPercentage.setText("Taxes ("+Constant.GST_PERCENTAGE+"%)");
            binding.taxPercentage1.setText("Taxes ("+Constant.GST_PERCENTAGE+"%)");
        }else{
            binding.taxField.setVisibility(View.GONE);
            binding.taxField1.setVisibility(View.GONE);
        }

        binding.popupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.popupCard.setVisibility(View.GONE);
                work();
            }
        });

        binding.popupCancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.popupCard1.setVisibility(View.GONE);
                work();
            }
        });

        onetimecarprice = "0";

        binding.preferredtimeEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.preferDate.getText().toString())){
                    Toast.makeText(VehicleListActivity.this,"Choose Preferred Schedule",Toast.LENGTH_SHORT).show();
                }else {
                    binding.timeLl.setVisibility(View.VISIBLE);
                    PreferredAdapter preferredAdapter = new PreferredAdapter(VehicleListActivity.this, preTime, "smart_wax");
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

        binding.preferDate.setOnClickListener(new View.OnClickListener() {
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
                                binding.preferDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                //picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                picker.getDatePicker().setMinDate(System.currentTimeMillis()+24*60*60*1000);
                picker.show();
            }
        });

        binding.preferredtimeEdt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.preferDate1.getText().toString())){
                    Toast.makeText(VehicleListActivity.this,"Choose Preferred Schedule",Toast.LENGTH_SHORT).show();
                }else {
                    binding.timeLl.setVisibility(View.VISIBLE);
                    PreferredAdapter preferredAdapter = new PreferredAdapter(VehicleListActivity.this, preTime, "smart_ext");
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

    public void checkOnetime(String carprice, String carid, String onetimeService) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<OneTimeWashCheckout> call = apiInterface.onetime_wash(customerid,carprice,carid,onetimeService);
        call.enqueue(new Callback<OneTimeWashCheckout>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<OneTimeWashCheckout> call, Response<OneTimeWashCheckout> response) {
                OneTimeWashCheckout body = response.body();
                if (body.code.equalsIgnoreCase("200")) {
                    showDropdown();
                    result = body.result;
                    for (int i = 0; i < result.size(); i++) {
                        if (result.get(i).order_exists.equalsIgnoreCase("0")) {
                            fineAmount = result.get(i).fine_amount;
                            onetimecarprice = result.get(i).total_amount;
                            paidMonths = "1";
                            totalAmountStr = Integer.parseInt(result.get(i).total_amount);
                            binding.total1.setText("₹ " + onetimecarprice);
                            int taxAmt = ((Constant.GST_PERCENTAGE * Integer.parseInt(onetimecarprice)) / 100);
                            int finalAmt = taxAmt + Integer.parseInt(onetimecarprice);
                            binding.taxTotal1.setText("₹ " + taxAmt);
                            binding.totalAmount1.setText("₹ " +finalAmt);
                            //binding.totalAmount1.setText("₹ " + onetimecarprice);

                            //showing Discount filed
                            if (result.get(i).discount_amount.equalsIgnoreCase("0")) {
                                discountAmount = result.get(i).discount_amount;
                                binding.discountField.setVisibility(View.GONE);
                                binding.discountAmount1.setText(result.get(i).discount_amount);
                            } else {
                                binding.discountField.setVisibility(View.VISIBLE);
                                discountAmount = result.get(i).discount_amount;
                                binding.discountAmount1.setText(result.get(i).discount_amount);
                            }

                            if (result.get(i).total_amount.equalsIgnoreCase("0")) {
                                //onetimecarprice.equals(carprice);
                                //Log.e("is 0","Price"+onetimecarprice);
                            }else{
                                onetimecarprice = result.get(i).total_amount;
                                binding.total1.setText("₹ " +result.get(i).total_amount);
                                int taxAmt1 = ((Constant.GST_PERCENTAGE * Integer.parseInt(result.get(i).total_amount)) / 100);
                                int finalAmt1 = taxAmt1 + Integer.parseInt(result.get(i).total_amount);
                                binding.taxTotal1.setText("₹ " + taxAmt1);
                                binding.totalAmount1.setText("₹ " +finalAmt1);
                                //binding.totalAmount1.setText("₹ " +result.get(i).total_amount);
                                //Log.e("not 0","Price"+onetimecarprice);
                            }

                        } else {
                            binding.fineField.setVisibility(View.VISIBLE);
                            if (result.get(i).fine_amount.equalsIgnoreCase("0")) {
                                fineAmount = result.get(i).fine_amount;
                                binding.fineField.setVisibility(View.GONE);
                                binding.fineAmount1.setText(result.get(i).fine_amount);
                            } else {
                                binding.fineField.setVisibility(View.VISIBLE);
                                fineAmount = result.get(i).fine_amount;
                                binding.fineAmount1.setText(result.get(i).fine_amount);
                            }

                            //showing Discount filed
                            if (result.get(i).discount_amount.equalsIgnoreCase("0")) {
                                discountAmount = result.get(i).discount_amount;
                                binding.discountField.setVisibility(View.GONE);
                                binding.discountAmount1.setText("₹ "+result.get(i).discount_amount);
                            } else {
                                binding.discountField.setVisibility(View.VISIBLE);
                                discountAmount = result.get(i).discount_amount;
                                binding.discountAmount1.setText("₹ "+result.get(i).discount_amount);
                            }

                            if (result.get(i).total_amount.equalsIgnoreCase("0")) {
                                //onetimecarprice.equals(carprice);
                                //Log.e("is 0","Price"+onetimecarprice);
                            }else{
                                onetimecarprice = result.get(i).total_amount;
                                binding.total1.setText("₹ " +result.get(i).total_amount);
                                int taxAmt2 = ((Constant.GST_PERCENTAGE * Integer.parseInt(result.get(i).total_amount)) / 100);
                                int finalAmt2 = taxAmt2 + Integer.parseInt(result.get(i).total_amount);
                                binding.taxTotal1.setText("₹ " + taxAmt2);
                                binding.totalAmount1.setText("₹ " +finalAmt2);
                                //binding.totalAmount1.setText("₹ " +result.get(i).total_amount);
                                //Log.e("not 0","Price"+onetimecarprice);
                            }

                        }

                    }
                } else if (body.code.equalsIgnoreCase("201")) {
                    Toast.makeText(VehicleListActivity.this,body.staus,Toast.LENGTH_SHORT).show();
                }
            }

            @SuppressLint("LongLogTag")
            public void onFailure(Call<OneTimeWashCheckout> call, Throwable th) {
                Toast.makeText(VehicleListActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDropdown(){
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subsMonths);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        this.binding.subscriptionType1.setAdapter(arrayAdapter);
        this.binding.subscriptionType1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (i == 0) {
                    double parseDouble = Double.parseDouble(onetimecarprice) * 1;
                    totalAmountStr = (int) parseDouble;
                    paidMonths = "1";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble));
                } else if (i == 1) {
                    double parseDouble2 = Double.parseDouble(onetimecarprice) * 2;
                    totalAmountStr = (int) parseDouble2;
                    paidMonths = "2";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble2));
                } else if (i == 2) {
                    double parseDouble3 = Double.parseDouble(onetimecarprice) * 3;
                    totalAmountStr = (int) parseDouble3;
                    paidMonths = "3";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble3));
                } else if (i == 3) {
                    double parseDouble4 = Double.parseDouble(onetimecarprice) * 4;
                    totalAmountStr = (int) parseDouble4;
                    paidMonths = "4";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble4));
                } else if (i == 4) {
                    double parseDouble5 = Double.parseDouble(onetimecarprice) * 5;
                    totalAmountStr = (int) parseDouble5;
                    paidMonths = "5";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble5));
                } else if (i == 5) {
                    double parseDouble6 = Double.parseDouble(onetimecarprice) * 6;
                    totalAmountStr = (int) parseDouble6;
                    paidMonths = "6";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble6));
                } else if (i == 6) {
                    double parseDouble7 = Double.parseDouble(onetimecarprice) * 7;
                    totalAmountStr = (int) parseDouble7;
                    paidMonths = "7";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble7));
                } else if (i == 7) {
                    double parseDouble8 = Double.parseDouble(onetimecarprice) * 8;
                    totalAmountStr = (int) parseDouble8;
                    paidMonths = "8";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble8));
                } else if (i == 8) {
                    double parseDouble9 = Double.parseDouble(onetimecarprice) * 9;
                    totalAmountStr = (int) parseDouble9;
                    paidMonths = "9";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble9));
                } else if (i == 9) {
                    double parseDouble10 = Double.parseDouble(onetimecarprice) * 10;
                    totalAmountStr = (int) parseDouble10;
                    paidMonths = "10";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble10));
                } else if (i == 10) {
                    double parseDouble11 = Double.parseDouble(onetimecarprice) * 11;
                    totalAmountStr = (int) parseDouble11;
                    paidMonths = "11";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble11));
                } else if (i == 11) {
                    double parseDouble12 = Double.parseDouble(onetimecarprice) * 12;
                    totalAmountStr = (int) parseDouble12;
                    paidMonths = "12";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble12));
                } else if (i == 12) {
                    double parseDouble13 = Double.parseDouble(onetimecarprice) * 13;
                    totalAmountStr = (int) parseDouble13;
                    paidMonths = "13";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble13));
                } else if (i == 13) {
                    double parseDouble14 = Double.parseDouble(onetimecarprice) * 14;
                    totalAmountStr = (int) parseDouble14;
                    paidMonths = "14";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble14));
                } else if (i == 14) {
                    double parseDouble15 = Double.parseDouble(onetimecarprice) * 15;
                    totalAmountStr = (int) parseDouble15;
                    paidMonths = "15";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble15));
                } else if (i == 15) {
                    double parseDouble16 = Double.parseDouble(onetimecarprice) * 16;
                    totalAmountStr = (int) parseDouble16;
                    paidMonths = "16";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble16));
                } else if (i == 16) {
                    double parseDouble17 = Double.parseDouble(onetimecarprice) * 17;
                    totalAmountStr = (int) parseDouble17;
                    paidMonths = "17";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble17));
                } else if (i == 17) {
                    double parseDouble18 = Double.parseDouble(onetimecarprice) * 18;
                    totalAmountStr = (int) parseDouble18;
                    paidMonths = "18";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble18));
                } else if (i == 18) {
                    double parseDouble19 = Double.parseDouble(onetimecarprice) * 19;
                    totalAmountStr = (int) parseDouble19;
                    paidMonths = "19";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble19));
                } else if (i == 19) {
                    double parseDouble20 = Double.parseDouble(onetimecarprice) * 20;
                    totalAmountStr = (int) parseDouble20;
                    paidMonths = "20";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble20));
                } else if (i == 20) {
                    double parseDouble21 = Double.parseDouble(onetimecarprice) * 21;
                    totalAmountStr = (int) parseDouble21;
                    paidMonths = "21";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble21));
                } else if (i == 21) {
                    double parseDouble22 = Double.parseDouble(onetimecarprice) * 22;
                    totalAmountStr = (int) parseDouble22;
                    paidMonths = "22";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble22));
                } else if (i == 22) {
                    double parseDouble23 = Double.parseDouble(onetimecarprice) * 23;
                    totalAmountStr = (int) parseDouble23;
                    paidMonths = "23";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble23));
                } else if (i == 23) {
                    double parseDouble24 = Double.parseDouble(onetimecarprice) * 24;
                    totalAmountStr = (int) parseDouble24;
                    paidMonths = "24";
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Constant.GST_PERCENTAGE * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " +finalAmt);
                    //binding.totalAmount1.setText("₹ " + totalAmountStr);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble24));
                }
            }
        });
    }


    public void checkIfExists(String servicetype,String  finalCarid,String carmakemodel,String carno){

        if (servicetype.equalsIgnoreCase("AddOn")){
            action = Constant.ACTIONEXTRAONE;
        }else if (servicetype.equalsIgnoreCase("Wash")){
            action = Constant.ACTIONWASHONE;
        }

        String carid = finalCarid;

        databaseHelper.CheckOrderExists(action,carid);

        String tottal_amt = String.valueOf(totalAmountStr);

        int before_tax = Integer.parseInt(tottal_amt);
        int taxAmt = ((Constant.GST_PERCENTAGE * before_tax) / 100);
        int finalAmt = taxAmt + before_tax;

        String result = databaseHelper.AddUpdateOrder(action+"",
                "1",
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
                binding.preferDate.getText().toString()+"",
                time+"");

        if(result.equalsIgnoreCase("1")){
            binding.popupCard.setVisibility(View.GONE);
            Toast.makeText(VehicleListActivity.this, "Added. ", Toast.LENGTH_SHORT).show();
            showCartCount();
            work();
            binding.preferDate.setText("");
        }else{
            Toast.makeText(VehicleListActivity.this, "Added Failed. ", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkIfExists1(String servicetype, String finalCarid, String carmakemodel, String carno, String carprice){
        Log.e("123466",""+servicetype);
        if(servicetype.equals("AddOn")){
            action = Constant.ACTIONONE;
        }else if(servicetype.equals("Disinsfection")){
            action = Constant.ACTIONDISONE;
        }

        String carid = finalCarid;

        int before_tax = Integer.parseInt(carprice);
        int taxAmt = ((Constant.GST_PERCENTAGE * before_tax) / 100);
        int finalAmt = taxAmt + before_tax;

        String result = databaseHelper.AddUpdateOrder(action+"",
                "1",
                carmakemodel+"",
                carno+"",
                carprice+"",
                carid+"",
                "1",
                "0",
                carprice+"",
                Constant.GST_PERCENTAGE+"",
                taxAmt+"",
                String.valueOf(finalAmt),
                binding.preferDate1.getText().toString()+"",
                time+"");

        if(result.equalsIgnoreCase("1")){
            binding.popupCard1.setVisibility(View.GONE);
            Toast.makeText(VehicleListActivity.this, "Added. ", Toast.LENGTH_SHORT).show();
            showCartCount();
            work();
            binding.preferDate1.setText("");
        }else{
            Toast.makeText(VehicleListActivity.this, "Added Failed. ", Toast.LENGTH_SHORT).show();
        }


    }

}