package com.muvierecktech.carrocare.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.MyOrdersAdapter;
import com.muvierecktech.carrocare.adapter.PreferredAdapter;
import com.muvierecktech.carrocare.adapter.RenewOrderAdapter;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.DatabaseHelper;
import com.muvierecktech.carrocare.common.MyDatabaseHelper;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityRenewBinding;
import com.muvierecktech.carrocare.model.OneTimeWashCheckout;
import com.muvierecktech.carrocare.model.OrdersList;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class RenewActivity extends AppCompatActivity {
    public ActivityRenewBinding binding;
    SessionManager sessionManager;
    String token,customerid,action,onetimeService,paidMonths,fineAmount,discountAmount,onetimecarprice;
    RenewOrderAdapter renewOrderAdapter;
    MyDatabaseHelper databaseHelper;
    List<OneTimeWashCheckout.getResult> result;
    int totalAmountStr;
    String[] subsMonths = {"1 Month", "2 Months", "3 Months", "4 Months", "5 Months", "6 Months", "7 Months", "8 Months", "9 Months", "10 Months", "11 Months", "12 Months", "13 Months", "14 Months", "15 Months", "16 Months", "17 Months", "18 Months", "19 Months", "20 Months", "21 Months", "22 Months", "23 Months", "24 Months"};
    String preTime[] = {Constant.ANYTIME, "9.00 AM - 10.00 AM","10.00 AM - 11.00 AM","11.00 AM - 12.00 PM","12.00 PM - 1.00 PM","6.00 PM - 7.00 PM","7.00 PM - 8.00 PM"};
    public static String time;
    DatePickerDialog picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_renew);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_renew);

        databaseHelper = new MyDatabaseHelper(this);

        showCartCount();

        binding.checkCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RenewActivity.this,CartActivity.class));
            }
        });

        sessionManager = new SessionManager(this);
        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);
        work();
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RenewActivity.this,MyOrdersActivity.class));
                finish();
            }
        });

        binding.makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RenewActivity.this,CartActivity.class));
                finish();
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
                    Toast.makeText(RenewActivity.this,"Choose Preferred Schedule",Toast.LENGTH_SHORT).show();
                }else {
                    binding.timeLl.setVisibility(View.VISIBLE);
                    PreferredAdapter preferredAdapter = new PreferredAdapter(RenewActivity.this, preTime, "renew_wax");
                    LinearLayoutManager linearLayoutManage = new LinearLayoutManager(RenewActivity.this, LinearLayoutManager.VERTICAL, false);
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
                picker = new DatePickerDialog(RenewActivity.this,
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
                    Toast.makeText(RenewActivity.this,"Choose Preferred Schedule",Toast.LENGTH_SHORT).show();
                }else {
                    binding.timeLl.setVisibility(View.VISIBLE);
                    PreferredAdapter preferredAdapter = new PreferredAdapter(RenewActivity.this, preTime, "renew_ext");
                    LinearLayoutManager linearLayoutManage = new LinearLayoutManager(RenewActivity.this, LinearLayoutManager.VERTICAL, false);
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
                picker = new DatePickerDialog(RenewActivity.this,
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





//        binding.addToCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkIfExists();
//
//            }
//        });

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
            Toast.makeText(RenewActivity.this, "Added. ", Toast.LENGTH_SHORT).show();
            showCartCount();
            work();
            binding.preferDate.setText("");
        }else{
            Toast.makeText(RenewActivity.this, "Added Failed. ", Toast.LENGTH_SHORT).show();
        }


    }

    public void checkIfExists1(String servicetype, String finalCarid, String carmakemodel, String carno, String carprice){

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
            Toast.makeText(RenewActivity.this, "Added. ", Toast.LENGTH_SHORT).show();
            showCartCount();
            work();
            binding.preferDate1.setText("");
        }else{
            Toast.makeText(RenewActivity.this, "Added Failed. ", Toast.LENGTH_SHORT).show();
        }


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
                    Toast.makeText(RenewActivity.this,body.staus,Toast.LENGTH_SHORT).show();
                }
            }

            @SuppressLint("LongLogTag")
            public void onFailure(Call<OneTimeWashCheckout> call, Throwable th) {
                Toast.makeText(RenewActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void work() {
        if (isNetworkAvailable()){
            ServicePrice();
            binding.preferredtimeEdt.setText("");
            binding.preferredtimeEdt1.setText("");
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(RenewActivity.this);
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
        final KProgressHUD hud = KProgressHUD.create(RenewActivity.this)
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
                final OrdersList ordersList = response.body();
                hud.dismiss();
                if (ordersList.code.equalsIgnoreCase("200")) {
                    Gson gson = new Gson();
                    String json = gson.toJson(ordersList);
                    binding.noorders.setVisibility(View.GONE);
                    binding.ordersRc.setVisibility(View.VISIBLE);

                    renewOrderAdapter = new RenewOrderAdapter(RenewActivity.this,ordersList.orders);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RenewActivity.this,LinearLayoutManager.VERTICAL,false);
                    binding.ordersRc.setLayoutManager(linearLayoutManager);
                    binding.ordersRc.setAdapter(renewOrderAdapter);

                }else  if (ordersList.code.equalsIgnoreCase("203")) {
                    sessionManager.logoutUsers();
                }else  if (ordersList.code.equalsIgnoreCase("201")){
                    binding.noorders.setVisibility(View.VISIBLE);
                    binding.bottomLl.setVisibility(View.GONE);
                    binding.ordersRc.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<OrdersList> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(RenewActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
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


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RenewActivity.this,MyOrdersActivity.class);
        startActivity(intent);
        finish();
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