package com.muvierecktech.carrocare.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.DBAdapter;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.DatabaseHelper;
import com.muvierecktech.carrocare.databinding.ActivityCartBinding;
import com.muvierecktech.carrocare.model.DBModel;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.muvierecktech.carrocare.common.DatabaseHelper.TABLE_NAME;

public class CartActivity extends AppCompatActivity implements PaymentResultListener {
    public ActivityCartBinding binding;

    DatabaseHelper databaseHelper;
    String Sum;
    ArrayList<DBModel> arrayList;
    String custmob,custemail,razorpayid;
    public static int total;

    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    String type,customerid,token,carprice,carid,paidMonths,fineAmount,totalAmountStr,date,time;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_cart);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_cart);

        Checkout.preload(getApplicationContext());

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CartActivity.this,MainActivity.class));
                finish();
            }
        });

        databaseHelper = new DatabaseHelper(this);

        int totalItemOfCart = databaseHelper.getTotalItemOfCart();

        Log.e("Total item of cart--->   ",""+totalItemOfCart);

        showTotal();

        showCartItem();

        binding.tvCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workmode();
            }
        });


    }

    public void showTotal(){

        SQLiteDatabase sQLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor rawQuery = sQLiteDatabase.rawQuery(" SELECT SUM (" + databaseHelper.TOTAL + ") FROM " + TABLE_NAME, null);
        rawQuery.moveToFirst();

        total = Integer.parseInt(String.valueOf(rawQuery.getInt(0)));

        binding.tvTotals.setText("₹ " + total);

        if(total == 0){
            binding.novehicle.setVisibility(View.VISIBLE);
            binding.footer.setVisibility(View.GONE);
            binding.footTop.setVisibility(View.GONE);
        }else{
            binding.novehicle.setVisibility(View.GONE);
            binding.footer.setVisibility(View.VISIBLE);
            binding.footTop.setVisibility(View.VISIBLE);
        }

    }


    private void workmode() {
        if (isNetworkAvailable()){
            getPayMode();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection." );
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Ok".
                    getPayMode();
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

    private void getPayMode() {
        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getMode();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body();
                hud.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                        Gson gson = new Gson();

                        Constant.RAZOR_PAY_KEY_SECRET = jsonObject.optString("secretkey");
                        Constant.RAZOR_PAY_KEY_VALUE = jsonObject.optString("keyid");

                        createOrderId();


                        //startwashonetimepayment();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(CartActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createOrderId(){
        //int amount = total * 100;
        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.createOrderId("create_orderid",total +"");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body();
                hud.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                        Gson gson = new Gson();

                        Constant.RAZOR_PAY_ORDER_ID = jsonObject.optString("rzp_order_id");

                        createTempOrder();
                        //startwashonetimepayment();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(CartActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createTempOrder() {
        if (isNetworkAvailable()){

            sqLiteDatabase = databaseHelper.getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery(" SELECT * FROM " + TABLE_NAME,null);

            if (cursor.moveToFirst()) {
                do {

                    String type = cursor.getString(3);
                    String customerid = cursor.getString(1);
                    String token = cursor.getString(2);
                    String carprice = cursor.getString(7);
                    String carid = cursor.getString(8);
                    String paidMonths = cursor.getString(9);
                    String fineAmount = cursor.getString(10);
                    String totalAmountStr = cursor.getString(11);
                    String date = cursor.getString(12);
                    String time = cursor.getString(13);


                    if (type.equalsIgnoreCase("onetime_wash_payment")) {
                        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setCancellable(true)
                                .setAnimationSpeed(2)
                                .setDimAmount(0.5f)
                                .show();
                        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                        Call<JsonObject> call = apiInterface.tempWashOrderOneTime("temp_order",
                                Constant.RAZOR_PAY_ORDER_ID+"",
                                customerid+"",
                                token+"",
                                carprice+"",
                                carid+"",
                                paidMonths + "",
                                fineAmount + "",
                                totalAmountStr + "",
                                "Wash",
                                "onetime_wash_payment");
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                JsonElement body = response.body();
                                hud.dismiss();
                                try {
                                    JSONObject jSONObject = new JSONObject(body.toString());
                                    if (jSONObject.optString("code").equalsIgnoreCase("200")) {
                                        new Gson();
                                        //Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                                        startwashonetimepayment();
                                        //paymentSuccess();
                                        //startActivity(new Intent(CartActivity.this, CongratsActivity.class));
                                        //finish();
                                    } else if (jSONObject.optString("code").equalsIgnoreCase("201")) {
                                        Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                                    }else if (jSONObject.optString("message").equalsIgnoreCase("success")){
                                        Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                                        //paymentSuccess();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            public void onFailure(Call<JsonObject> call, Throwable th) {
                                hud.dismiss();
                                Toast.makeText(CartActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else if(type.equalsIgnoreCase("onetime_wax_payment")){
                        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setCancellable(true)
                                .setAnimationSpeed(2)
                                .setDimAmount(0.5f)
                                .show();
                        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                        Call<JsonObject> call = apiInterface.tempAddOnOrderOneTime("temp_order",
                                Constant.RAZOR_PAY_ORDER_ID+"",
                                customerid+"",
                                token+"",
                                carprice+"",
                                carid+"",
                                paidMonths + "",
                                fineAmount + "",
                                totalAmountStr + "",
                                "AddOn",
                                date +"",
                                time +"",
                                "onetime_wash_payment");
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                JsonElement body = response.body();
                                hud.dismiss();
                                try {
                                    JSONObject jSONObject = new JSONObject(body.toString());
                                    if (jSONObject.optString("code").equalsIgnoreCase("200")) {
                                        new Gson();
                                        //Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                                        startwashonetimepayment();
                                        //paymentSuccess();
                                        //startActivity(new Intent(CartActivity.this, CongratsActivity.class));
                                        //finish();
                                    } else if (jSONObject.optString("code").equalsIgnoreCase("201")) {
                                        Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            public void onFailure(Call<JsonObject> call, Throwable th) {
                                hud.dismiss();
                                Toast.makeText(CartActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else if(type.equalsIgnoreCase("onetime_payment")){
                        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setCancellable(true)
                                .setAnimationSpeed(2)
                                .setDimAmount(0.5f)
                                .show();
                        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                        Call<JsonObject> call = apiInterface.tempOrderOneTime("temp_order",
                                Constant.RAZOR_PAY_ORDER_ID+"",
                                customerid+"",
                                token+"",
                                "ExtraInterior",
                                carprice+"",
                                carid+"",
                                "AddOn",
                                carprice+"",
                                date +"",
                                time +"",
                                "onetime_payment");
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                JsonElement jsonElement = response.body();
                                hud.dismiss();
                                try {
                                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                                        Gson gson = new Gson();
                                        //Toast.makeText(CartActivity.this,jsonObject.optString("result"),Toast.LENGTH_SHORT).show();
                                        startwashonetimepayment();
                                        //paymentSuccess();
//                        Intent intent = new Intent(CartActivity.this,CongratsActivity.class);
//                        startActivity(intent);
//                        finish();
                                    }else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                                        Toast.makeText(CartActivity.this,jsonObject.optString("result"),Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                hud.dismiss();
                                Toast.makeText(CartActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }


                }while (cursor.moveToNext());
            }


        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection." );
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Ok".
                    workPlaceOrder(razorpayid);
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


    public void startwashonetimepayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID(Constant.RAZOR_PAY_KEY_VALUE);
        checkout.setImage(R.drawable.logo5234);
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("name", "Carro Care");
            jSONObject.put("description", "");
            jSONObject.put("order_id", Constant.RAZOR_PAY_ORDER_ID);
            jSONObject.put("currency", "INR");
            int i = total;
            Log.e("AMOUNTRZP", String.valueOf(i));
            jSONObject.put("amount", i);
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("email", custemail);
            jSONObject2.put("contact", custmob);
            jSONObject.put("prefill", jSONObject2);
            checkout.open(this, jSONObject);
            Log.e("OPTIONS", jSONObject.toString());
        } catch (Exception e) {
            Log.d("PaymentOptionActivity", "Error in starting Razorpay Checkout", e);
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            razorpayid = razorpayPaymentID;
            Log.e("razorpayPaymentID>>>>>>>>>>>>>>>>>>>>>>>>>  :  ",""+razorpayid);
            Log.e("razorpaytempID>>>>>>>>>>>>>>>>>>>>>>>>>  :  ",""+Constant.RAZOR_PAY_ORDER_ID);
            workPlaceOrder(razorpayid);
        } catch (Exception e) {
            Log.e("TAG onPaymentSuccess  ", e.getMessage());
        }
    }

    @Override
    public void onPaymentError(int i, String response) {
        try {
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
            Log.e("TAG onPaymentError  ", response);
        } catch (Exception e) {
            Log.e("TAG onPaymentError  ", e.getMessage());
        }
    }

    private void workPlaceOrder(final String razorpayid) {
        if (isNetworkAvailable()){

            sqLiteDatabase = databaseHelper.getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery(" SELECT * FROM " + TABLE_NAME,null);

            if (cursor.moveToFirst()) {
                do {

                    type = cursor.getString(3);
                    customerid = cursor.getString(1);
                    token = cursor.getString(2);
                    carprice = cursor.getString(7);
                    carid = cursor.getString(8);
                    paidMonths = cursor.getString(9);
                    fineAmount = cursor.getString(10);
                    totalAmountStr = cursor.getString(11);
                    date = cursor.getString(12);
                    time = cursor.getString(13);


                    if (type.equalsIgnoreCase("onetime_wash_payment")) {
                        PlaceOrderOneTimeWash(razorpayid);
                    }else if(type.equalsIgnoreCase("onetime_wax_payment")){
                        PlaceOrderOneTimeAddOn(razorpayid);
                    }else if(type.equalsIgnoreCase("onetime_payment")){
                        PlaceOrderOneTime(razorpayid);
                    }


                }while (cursor.moveToNext());
            }


        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection." );
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Ok".
                    workPlaceOrder(razorpayid);
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


    private void PlaceOrderOneTimeWash(String razorpayid) {
        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.saveWashOrderOneTime("onetime_wash_payment",
                razorpayid+"",
                Constant.RAZOR_PAY_ORDER_ID +"",
                customerid+"",
                token+"",
                carprice+"",
                carid+"",
                paidMonths + "",
                fineAmount + "",
                totalAmountStr + "",
                "Wash");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement body = response.body();
                hud.dismiss();
                try {
                    JSONObject jSONObject = new JSONObject(body.toString());
                    if (jSONObject.optString("code").equalsIgnoreCase("200")) {
                        new Gson();
                        Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                        paymentSuccess();
                        Log.e("payresponse",""+jSONObject.optString("result"));
                        //startActivity(new Intent(CartActivity.this, CongratsActivity.class));
                        //finish();
                    } else if (jSONObject.optString("code").equalsIgnoreCase("201")) {
                        Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                    }else if (jSONObject.optString("message").equalsIgnoreCase("success")){
                        Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                        paymentSuccess();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(Call<JsonObject> call, Throwable th) {
                hud.dismiss();
                Toast.makeText(CartActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PlaceOrderOneTimeAddOn(String razorpayid) {
        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.saveAddOnOrderOneTime("onetime_wash_payment",
                razorpayid+"",
                Constant.RAZOR_PAY_ORDER_ID +"",
                customerid+"",
                token+"",
                carprice+"",
                carid+"",
                paidMonths + "",
                fineAmount + "",
                totalAmountStr + "",
                "AddOn",
                date +"",
                time +"");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement body = response.body();
                hud.dismiss();
                try {
                    JSONObject jSONObject = new JSONObject(body.toString());
                    if (jSONObject.optString("code").equalsIgnoreCase("200")) {
                        new Gson();
                        Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                        paymentSuccess();
                        Log.e("payresponse",""+jSONObject.optString("result"));
                        //startActivity(new Intent(CartActivity.this, CongratsActivity.class));
                        //finish();
                    } else if (jSONObject.optString("code").equalsIgnoreCase("201")) {
                        Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(Call<JsonObject> call, Throwable th) {
                hud.dismiss();
                Log.e("123456",""+th.getMessage());
                Toast.makeText(CartActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PlaceOrderOneTime(String razorpayid) {

//        Date date = Calendar.getInstance().getTime();
//        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
//        String formattedDate = df.format(date);

        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.saveOrderOneTime("onetime_payment",
                razorpayid+"",
                Constant.RAZOR_PAY_ORDER_ID +"",
                customerid+"",
                token+"",
                "ExtraInterior",
                carprice+"",
                carid+"",
                "AddOn",
                carprice+"",
                date +"",
                time +"");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body();
                hud.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                        Gson gson = new Gson();
                        Toast.makeText(CartActivity.this,jsonObject.optString("result"),Toast.LENGTH_SHORT).show();
                        paymentSuccess();
                        Log.e("payresponse",""+jsonObject.optString("result"));
//                        Intent intent = new Intent(CartActivity.this,CongratsActivity.class);
//                        startActivity(intent);
//                        finish();
                    }else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                        Toast.makeText(CartActivity.this,jsonObject.optString("result"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Log.e("123456",""+t.getMessage());
                Toast.makeText(CartActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void paymentSuccess(){
        startActivity(new Intent(CartActivity.this, CongratsActivity.class));
        finish();
        databaseHelper.DeleteAllCartData();
    }

    public void showCartItem(){
        arrayList = new ArrayList<>(databaseHelper.getItems());
        binding.rvCartitem.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvCartitem.setItemAnimator(new DefaultItemAnimator());
        DBAdapter adapter = new DBAdapter(getApplicationContext(),this,arrayList);
        binding.rvCartitem.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CartActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}