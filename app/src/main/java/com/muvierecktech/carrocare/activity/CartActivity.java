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
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.CartListAdapter;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.MyDatabaseHelper;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityCartBinding;
import com.muvierecktech.carrocare.model.CartList;
import com.muvierecktech.carrocare.model.CouponCodeModel;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.muvierecktech.carrocare.common.DatabaseHelper.TABLE_NAME;

public class CartActivity extends AppCompatActivity implements PaymentResultListener {
    public static int total;
    public static double pay_amount;
    public ActivityCartBinding binding;
    MyDatabaseHelper databaseHelper;
    String Sum;
    ArrayList<CartList> arrayList;
    String custmob, custemail, razorpayid;
    SessionManager sessionManager;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    String action, customerid, token, carprice, carid, paidMonths, fineAmount, gst, gstAmount, totalAmountStr, subtotal, date, time;

    boolean showCoupon = false;
    boolean isCouponApplied = false;
    String applyText = "APPLY";

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_cart);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart);
        databaseHelper = new MyDatabaseHelper(this);
        sessionManager = new SessionManager(this);
        HashMap<String, String> hashMap = sessionManager.getUserDetails();
        customerid = hashMap.get(SessionManager.KEY_USERID);
        token = hashMap.get(SessionManager.KEY_TOKEN);
        custmob = hashMap.get(SessionManager.KEY_USERMOBILE);
        custemail = hashMap.get(SessionManager.KEY_USEREMAIL);

        getData();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CartActivity.this, MainActivity.class));
                finish();
            }
        });

        binding.txtcheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workmode();
            }
        });

        binding.imgcheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workmode();
            }
        });

        binding.editCoupon.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        binding.txtRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCoupon = !showCoupon;
                if(showCoupon){
                    binding.linearCoupon.setVisibility(View.VISIBLE);
                }else{
                    binding.linearCoupon.setVisibility(View.GONE);
                }

            }
        });

        binding.btnAppy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (arrayList.isEmpty() || total == 0) {
                    Toast.makeText(CartActivity.this, "Please choose at least one product", Toast.LENGTH_SHORT).show();
                    return;
                }
                applyCouponCode();
            }
        });

    }

    public void getData() {
        SetDataTotal();
        arrayList = new ArrayList<>(databaseHelper.getItems());
        binding.rvCartitem.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvCartitem.setItemAnimator(new DefaultItemAnimator());
        CartListAdapter adapter = new CartListAdapter(getApplicationContext(), this, arrayList);
        binding.rvCartitem.setAdapter(adapter);
    }

    void applyCouponCode() {
        ///Coupon code flow start here
        if (binding.editCoupon.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter coupon code", Toast.LENGTH_SHORT).show();

        } else {

            final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            if (!isCouponApplied) {
                Call<JsonObject> call =
                        apiInterface.applyCouponCode(binding.editCoupon.getText().toString(), customerid);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        hud.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                Gson gson = new Gson();
                                CouponCodeModel couponCodeModel = gson.fromJson(response.body().toString(),
                                        CouponCodeModel.class);
                                    if (couponCodeModel.getCode() == 200) {
                                        if (couponCodeModel.getStatus() == Constant.statusSuccess) {
                                            updateCouponCode(isCouponApplied,
                                                    binding.editCoupon.getText().toString(),
                                                    couponCodeModel.getCouponData().get(0).getCouponDiscount());
                                            if (!isCouponApplied) {
                                                isCouponApplied = true;
                                                applyText = "REMOVE";
                                                binding.btnAppy.setText(applyText);
                                                long discountAmount =
                                                        (total * Integer.parseInt(couponCodeModel.getCouponData().get(0).getCouponDiscount())) /100;
                                                total = Math.toIntExact(total - discountAmount);
                                            }
                                            Toast.makeText(CartActivity.this,
                                                    couponCodeModel.getCouponData().get(0).getMessage(), Toast.LENGTH_SHORT).show();

                                        } else {
                                            if (couponCodeModel.getMessage() != null && couponCodeModel.getMessage().equalsIgnoreCase(
                                                    "Coupon Already Applied")) {
                                                if (!isCouponApplied) {
                                                    isCouponApplied = false;
                                                    applyText = "APPLY";
                                                    binding.btnAppy.setText(applyText);
                                                } else {
                                                    isCouponApplied = true;
                                                    applyText = "REMOVE";

                                                    binding.btnAppy.setText(applyText);
                                                }
                                            }
                                            if (couponCodeModel.getCode() == 203) {
                                                sessionManager.logoutUsers();
                                            } else {
                                                Toast.makeText(CartActivity.this,
                                                        couponCodeModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }else{

                                        Toast.makeText(CartActivity.this,
                                                couponCodeModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                            } else {
                                ApiConfig.responseToast(CartActivity.this, response.code());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        hud.dismiss();
                        Log.e("SHAGUL", "onFailure: "+call.toString() );
                        Toast.makeText(CartActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {

                Call<JsonObject> call =
                        apiInterface.removeCouponCode(binding.editCoupon.getText().toString(), customerid);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        hud.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                Gson gson = new Gson();
                                CouponCodeModel couponCodeModel = gson.fromJson(response.body().toString(),
                                        CouponCodeModel.class);
                                if (couponCodeModel.getCode() == 200) {
                                    if (couponCodeModel.getStatus() == Constant.statusSuccess) {
                                        updateCouponCode(isCouponApplied,
                                                binding.editCoupon.getText().toString(),
                                                couponCodeModel.getCouponData().get(0).getCouponDiscount());
                                        if (isCouponApplied) {
                                            isCouponApplied = false;
                                            applyText = "APPLY";
                                            binding.btnAppy.setText(applyText);
                                            binding.editCoupon.setText("");
                                            if (!arrayList.isEmpty()) {
                                                total = 0;
                                                SQLiteDatabase sQLiteDatabase = databaseHelper.getReadableDatabase();
                                                Cursor rawQuery = sQLiteDatabase.rawQuery(" SELECT SUM (" + databaseHelper.TOTAL + ") FROM " + TABLE_NAME, null);
                                                rawQuery.moveToFirst();
                                                total = Integer.parseInt(String.valueOf(rawQuery.getInt(0)));
                                                binding.txttotal.setText("₹ " + total);
                                                binding.txtstotal.setText("₹ " + total);
                                                binding.txtfinaltotal.setText(databaseHelper.getTotalItemOfCart() + " Items  " + "₹ " + total);
                                            }
                                        }
                                        Toast.makeText(CartActivity.this,
                                                couponCodeModel.getCouponData().get(0).getMessage(), Toast.LENGTH_SHORT).show();

                                    } else {
                                        if (couponCodeModel.getMessage() != null && couponCodeModel.getMessage().equalsIgnoreCase(
                                                "Coupon Already Applied")) {
                                            if (!isCouponApplied) {
                                                isCouponApplied = false;
                                                applyText = "APPLY";
                                                binding.btnAppy.setText(applyText);
                                            } else {
                                                isCouponApplied = true;
                                                applyText = "REMOVE";

                                                binding.btnAppy.setText(applyText);
                                            }
                                        }
                                        if (couponCodeModel.getCode() == 203) {
                                            sessionManager.logoutUsers();
                                        } else {
                                            Toast.makeText(CartActivity.this,
                                                    couponCodeModel.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                else{

                                    Toast.makeText(CartActivity.this,
                                            couponCodeModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                ApiConfig.responseToast(CartActivity.this, response.code());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        hud.dismiss();
                        Toast.makeText(CartActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    void updateCouponCode(boolean isCouponApplied, String couponCode, String percentage) {
        if (!isCouponApplied) {
            sessionManager.setData(SessionManager.COUPON_CODE, couponCode);
            sessionManager.setData(SessionManager.COUPON_PERCENTAGE, percentage);
        } else {
            sessionManager.setData(SessionManager.COUPON_CODE, "");
            sessionManager.setData(SessionManager.COUPON_PERCENTAGE, "");
        }
    }

    @SuppressLint("SetTextI18n")
    public void SetDataTotal() {
        SQLiteDatabase sQLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor rawQuery1 = sQLiteDatabase.rawQuery(" SELECT SUM (" + databaseHelper.SUB_TOTAL + ") FROM " + TABLE_NAME, null);
        rawQuery1.moveToFirst();
//        binding.txtstotal.setText("₹ " + Integer.parseInt(String.valueOf(rawQuery1.getInt(0))));

        Cursor rawQuery2 = sQLiteDatabase.rawQuery(" SELECT SUM (" + databaseHelper.GST_AMOUNT + ") FROM " + TABLE_NAME, null);
        rawQuery2.moveToFirst();
//        binding.txttaxtotal.setText("₹ " + Integer.parseInt(String.valueOf(rawQuery2.getInt(0))));

        Cursor rawQuery = sQLiteDatabase.rawQuery(" SELECT SUM (" + databaseHelper.TOTAL + ") FROM " + TABLE_NAME, null);
        rawQuery.moveToFirst();
        total = Integer.parseInt(String.valueOf(rawQuery.getInt(0)));
        binding.txttotal.setText("₹ " + total);
        binding.txtstotal.setText("₹ " + total);
        binding.txtfinaltotal.setText(databaseHelper.getTotalItemOfCart() + " Items  " + "₹ " + total);

        final ArrayList<String> idslist = databaseHelper.getCartList();
        if (idslist.isEmpty()) {
            binding.novehicle.setVisibility(View.VISIBLE);
            binding.lyttotal.setVisibility(View.GONE);
        } else {
            binding.novehicle.setVisibility(View.GONE);
            binding.lyttotal.setVisibility(View.VISIBLE);
            String couponCode = sessionManager.getData(SessionManager.COUPON_CODE);
            String percentage = sessionManager.getData(SessionManager.COUPON_PERCENTAGE);
            if (couponCode != null && !TextUtils.isEmpty(couponCode) && percentage != null && !TextUtils.isEmpty(percentage) && total != 0) {
                binding.editCoupon.setText(couponCode);
                isCouponApplied = true;
                applyText = "REMOVE";
                binding.btnAppy.setText(applyText);
                long discountAmount = (total * Integer.parseInt(percentage)) / 100;
                total = Math.toIntExact(total - discountAmount);
                showCoupon = true;
                binding.linearCoupon.setVisibility(View.VISIBLE);
            }
        }

    }


    private void workmode() {
        if (isNetworkAvailable()) {
            getPayMode();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection.");
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

                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        JsonElement jsonElement = response.body();
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                            Gson gson = new Gson();

                            Constant.RAZOR_PAY_KEY_SECRET = jsonObject.optString("secretkey");
                            Constant.RAZOR_PAY_KEY_VALUE = jsonObject.optString("keyid");

                            createOrderId();
                        }
                    } else {
                        ApiConfig.responseToast(CartActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(CartActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createOrderId() {
        //int amount = total * 100;
        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.createOrderId("create_orderid", total + "");
        //Call<JsonObject> call = apiInterface.createOrderId("create_orderid","1");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        JsonElement jsonElement = response.body();
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            Constant.RAZOR_PAY_ORDER_ID = jsonObject.optString("rzp_order_id");
                            createTempOrder();
                        }
                    } else {
                        ApiConfig.responseToast(CartActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(CartActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createTempOrder() {
        if (isNetworkAvailable()) {

            sqLiteDatabase = databaseHelper.getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery(" SELECT * FROM " + TABLE_NAME, null);

            if (cursor.moveToFirst()) {
                do {

                    String action = cursor.getString(0);
                    String carprice = cursor.getString(4);
                    String carid = cursor.getString(5);
                    String paidMonths = cursor.getString(6);
                    String fineAmount = cursor.getString(7);
                    String subtotal = cursor.getString(8);
                    String gst = cursor.getString(9);
                    String gstAmount = cursor.getString(10);
                    String totalAmountStr = cursor.getString(11);
                    String date = cursor.getString(12);
                    String time = cursor.getString(13);

                    final String[] split = action.split("=");
                    String type = split[0];

                    String package_type = "";
                    if(split[1].equalsIgnoreCase(Constant.WASH)){
                        package_type = "Car Wash";
                    } else if(split[1].equalsIgnoreCase(Constant.BWASH)){
                        package_type = "Bike Wash";
                    } else if(split[1].equalsIgnoreCase(Constant.ADDON)){
                        package_type = "Wax Polish";
                    } else if(split[1].equalsIgnoreCase(Constant.EXTRAINT)){
                        package_type = "ExtraInterior";
                    } else if(split[1].equalsIgnoreCase(Constant.DISINSFECTION)){
                        package_type = "Disinfection";
                    }

                    if (type.equalsIgnoreCase(Constant.ACTIONWASHONE)) {
                        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setCancellable(true)
                                .setAnimationSpeed(2)
                                .setDimAmount(0.5f)
                                .show();
                        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                        Call<JsonObject> call = apiInterface.tempWashOrderOneTime("temp_order",
                                Constant.RAZOR_PAY_ORDER_ID + "",
                                customerid + "",
                                token + "",
                                carprice + "",
                                carid + "",
                                paidMonths + "",
                                fineAmount + "",
                                subtotal + "",
                                gst + "",
                                gstAmount + "",
                                totalAmountStr + "",
                                "Wash",
                                package_type+"",
                                "onetime_wash_payment");
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                hud.dismiss();
                                try {
                                    if (response.isSuccessful()) {
                                        JsonElement body = response.body();
                                        JSONObject jSONObject = new JSONObject(body.toString());
                                        if (jSONObject.optString("code").equalsIgnoreCase("200")) {
                                            new Gson();
                                            startwashonetimepayment();
                                        } else if (jSONObject.optString("code").equalsIgnoreCase("201")) {
                                            Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                                        } else if (jSONObject.optString("message").equalsIgnoreCase("success")) {
                                            Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                                            //paymentSuccess();
                                        }
                                    } else {
                                        ApiConfig.responseToast(CartActivity.this, response.code());
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
                    else if (type.equalsIgnoreCase(Constant.ACTIONEXTRAONE)) {
                        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setCancellable(true)
                                .setAnimationSpeed(2)
                                .setDimAmount(0.5f)
                                .show();
                        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                        Call<JsonObject> call = apiInterface.tempAddOnOrderOneTime("temp_order",
                                Constant.RAZOR_PAY_ORDER_ID + "",
                                customerid + "",
                                token + "",
                                carprice + "",
                                carid + "",
                                paidMonths + "",
                                fineAmount + "",
                                subtotal + "",
                                gst + "",
                                gstAmount + "",
                                totalAmountStr + "",
                                "AddOn",
                                package_type+"",
                                date + "",
                                time + "",
                                "onetime_wash_payment");
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                hud.dismiss();
                                try {
                                    if (response.isSuccessful()) {
                                        JsonElement body = response.body();
                                        JSONObject jSONObject = new JSONObject(body.toString());
                                        if (jSONObject.optString("code").equalsIgnoreCase("200")) {
                                            new Gson();
                                            startwashonetimepayment();
                                        } else if (jSONObject.optString("code").equalsIgnoreCase("201")) {
                                            Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        ApiConfig.responseToast(CartActivity.this, response.code());
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
                    else if (type.equalsIgnoreCase(Constant.ACTIONONE)) {
                        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setCancellable(true)
                                .setAnimationSpeed(2)
                                .setDimAmount(0.5f)
                                .show();
                        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                        Call<JsonObject> call = apiInterface.tempOrderOneTime("temp_order",
                                Constant.RAZOR_PAY_ORDER_ID + "",
                                customerid + "",
                                token + "",
                                package_type+"",
                                carprice + "",
                                carid + "",
                                "AddOn",
                                subtotal + "",
                                gst + "",
                                gstAmount + "",
                                totalAmountStr + "",
                                date + "",
                                time + "",
                                "onetime_payment");
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                                hud.dismiss();
                                try {
                                    if (response.isSuccessful()) {
                                        JsonElement jsonElement = response.body();
                                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                                        if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                                            Gson gson = new Gson();
                                            startwashonetimepayment();
                                        } else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                                            Toast.makeText(CartActivity.this, jsonObject.optString("result"), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        ApiConfig.responseToast(CartActivity.this, response.code());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                hud.dismiss();
                                Toast.makeText(CartActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    if (type.equalsIgnoreCase(Constant.ACTIONDISONE)) {
                        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setCancellable(true)
                                .setAnimationSpeed(2)
                                .setDimAmount(0.5f)
                                .show();
                        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                        Call<JsonObject> call = apiInterface.tempOrderOneTime("temp_order",
                                Constant.RAZOR_PAY_ORDER_ID + "",
                                customerid + "",
                                token + "",
                                package_type+"",
                                carprice + "",
                                carid + "",
                                "AddOn",
                                subtotal + "",
                                gst + "",
                                gstAmount + "",
                                totalAmountStr + "",
                                date + "",
                                time + "",
                                "onetime_payment");
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                                hud.dismiss();
                                try {
                                    if (response.isSuccessful()) {
                                        JsonElement jsonElement = response.body();
                                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                                        if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                                            Gson gson = new Gson();
                                            startwashonetimepayment();
                                        } else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                                            Toast.makeText(CartActivity.this, jsonObject.optString("result"), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        ApiConfig.responseToast(CartActivity.this, response.code());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                hud.dismiss();
                                Toast.makeText(CartActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }


                } while (cursor.moveToNext());
            }


        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection.");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Action for "Ok".
                            createTempOrder();
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
        final Activity activity = this;
        Checkout checkout = new Checkout();
        checkout.setKeyID(Constant.RAZOR_PAY_KEY_VALUE);
        checkout.setImage(R.drawable.logo5234);
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("name", "Carro Care");
            jSONObject.put("description", Constant.RAZOR_PAY_ORDER_ID);
            jSONObject.put("order_id", Constant.RAZOR_PAY_ORDER_ID);
            jSONObject.put("currency", "INR");
            int i = total * 100;
            //int i = 1;
            Log.e("AMOUNTRZP", String.valueOf(i));
            jSONObject.put("amount", i);
            jSONObject.put("send_sms_hash", false);
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
            Log.e("razorpayPaymentID>>>>>>>>>>>>>>>>>>>>>>>>>  :  ", "" + razorpayid);
            Log.e("razorpaytempID>>>>>>>>>>>>>>>>>>>>>>>>>  :  ", "" + Constant.RAZOR_PAY_ORDER_ID);
            //workPlaceOrder(razorpayid);
            paymentSuccess();
        } catch (Exception e) {
            Log.e("TAG onPaymentSuccess  ", e.getMessage());
        }
    }

    @Override
    public void onPaymentError(int i, String response) {
        try {
            //Toast.makeText(this, response, Toast.LENGTH_LONG).show();
            // Payment failed
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_LONG).show();
            startActivity(new Intent(CartActivity.this, MainActivity.class));
            finish();
            databaseHelper.DeleteAllOrderData();
            Log.e("TAG onPaymentError  ", response);
        } catch (Exception e) {
            Log.e("TAG onPaymentError  ", e.getMessage());
        }
    }

    private void workPlaceOrder(final String razorpayid) {
        if (isNetworkAvailable()) {

            sqLiteDatabase = databaseHelper.getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery(" SELECT * FROM " + TABLE_NAME, null);

            if (cursor.moveToFirst()) {
                do {

                    action = cursor.getString(0);
                    carprice = cursor.getString(4);
                    carid = cursor.getString(5);
                    paidMonths = cursor.getString(6);
                    fineAmount = cursor.getString(7);
                    subtotal = cursor.getString(8);
                    gst = cursor.getString(9);
                    gstAmount = cursor.getString(10);
                    totalAmountStr = cursor.getString(11);
                    date = cursor.getString(12);
                    time = cursor.getString(13);

                    final String[] split = action.split("=");
                    String type = split[0];

                    String package_type = "";
                    if(split[1].equalsIgnoreCase(Constant.WASH)){
                        package_type = "Car Wash";
                    } else if(split[1].equalsIgnoreCase(Constant.BWASH)){
                        package_type = "Bike Wash";
                    } else if(split[1].equalsIgnoreCase(Constant.ADDON)){
                        package_type = "Wax Polish";
                    } else if(split[1].equalsIgnoreCase(Constant.EXTRAINT)){
                        package_type = "ExtraInterior";
                    } else if(split[1].equalsIgnoreCase(Constant.DISINSFECTION)){
                        package_type = "Disinfection";
                    }

                    if (type.equalsIgnoreCase(Constant.ACTIONWASHONE)) {
                        PlaceOrderOneTimeWash(razorpayid, package_type);
                    } else if (type.equalsIgnoreCase(Constant.ACTIONEXTRAONE)) {
                        PlaceOrderOneTimeAddOn(razorpayid, package_type);
                    } else if (type.equalsIgnoreCase(Constant.ACTIONONE)) {
                        PlaceOrderOneTime(razorpayid, package_type);
                    } else if (type.equalsIgnoreCase(Constant.ACTIONDISONE)) {
                        PlaceOrderOneTimeDisinsfecion(razorpayid, package_type);
                    }


                } while (cursor.moveToNext());
            }


        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection.");
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


    private void PlaceOrderOneTimeWash(String razorpayid, String package_type) {
        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.saveWashOrderOneTime("onetime_wash_payment",
                razorpayid + "",
                Constant.RAZOR_PAY_ORDER_ID + "",
                customerid + "",
                token + "",
                carprice + "",
                carid + "",
                paidMonths + "",
                fineAmount + "",
                subtotal + "",
                gst + "",
                gstAmount + "",
                totalAmountStr + "",
                "Wash",
                package_type+"");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        JsonElement body = response.body();
                        JSONObject jSONObject = new JSONObject(body.toString());
                        if (jSONObject.optString("code").equalsIgnoreCase("200")) {
                            new Gson();
                            Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                            paymentSuccess();
                            Log.e("payresponse", "" + jSONObject.optString("result"));
                            //startActivity(new Intent(CartActivity.this, CongratsActivity.class));
                            //finish();
                        } else if (jSONObject.optString("code").equalsIgnoreCase("201")) {
                            Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                        } else if (jSONObject.optString("message").equalsIgnoreCase("success")) {
                            Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                            paymentSuccess();
                        }
                    } else {
                        ApiConfig.responseToast(CartActivity.this, response.code());
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

    private void PlaceOrderOneTimeAddOn(String razorpayid, String package_type) {
        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.saveAddOnOrderOneTime("onetime_wash_payment",
                razorpayid + "",
                Constant.RAZOR_PAY_ORDER_ID + "",
                customerid + "",
                token + "",
                carprice + "",
                carid + "",
                paidMonths + "",
                fineAmount + "",
                subtotal + "",
                gst + "",
                gstAmount + "",
                totalAmountStr + "",
                "AddOn",
                package_type+"",
                date + "",
                time + "");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        JsonElement body = response.body();
                        JSONObject jSONObject = new JSONObject(body.toString());
                        if (jSONObject.optString("code").equalsIgnoreCase("200")) {
                            new Gson();
                            Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                            paymentSuccess();
                            Log.e("payresponse", "" + jSONObject.optString("result"));
                            //startActivity(new Intent(CartActivity.this, CongratsActivity.class));
                            //finish();
                        } else if (jSONObject.optString("code").equalsIgnoreCase("201")) {
                            Toast.makeText(CartActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(CartActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(Call<JsonObject> call, Throwable th) {
                hud.dismiss();
                Log.e("123456", "" + th.getMessage());
                Toast.makeText(CartActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PlaceOrderOneTime(String razorpayid, String package_type) {

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
                razorpayid + "",
                Constant.RAZOR_PAY_ORDER_ID + "",
                customerid + "",
                token + "",
                package_type+"",
                carprice + "",
                carid + "",
                "AddOn",
                subtotal + "",
                gst + "",
                gstAmount + "",
                totalAmountStr + "",
                date + "",
                time + "");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        JsonElement jsonElement = response.body();
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            Toast.makeText(CartActivity.this, jsonObject.optString("result"), Toast.LENGTH_SHORT).show();
                            paymentSuccess();
                            Log.e("payresponse", "" + jsonObject.optString("result"));
//                        Intent intent = new Intent(CartActivity.this,CongratsActivity.class);
//                        startActivity(intent);
//                        finish();
                        } else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                            Toast.makeText(CartActivity.this, jsonObject.optString("result"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(CartActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Log.e("123456", "" + t.getMessage());
                Toast.makeText(CartActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void PlaceOrderOneTimeDisinsfecion(String razorpayid, String package_type) {
        final KProgressHUD hud = KProgressHUD.create(CartActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.saveOrderOneTime("onetime_payment",
                razorpayid + "",
                Constant.RAZOR_PAY_ORDER_ID + "",
                customerid + "",
                token + "",
                package_type+"",
                carprice + "",
                carid + "",
                "AddOn",
                subtotal + "",
                gst + "",
                gstAmount + "",
                totalAmountStr + "",
                date + "",
                time + "");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        JsonElement jsonElement = response.body();
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            Toast.makeText(CartActivity.this, jsonObject.optString("result"), Toast.LENGTH_SHORT).show();
                            paymentSuccess();
                            Log.e("payresponse", "" + jsonObject.optString("result"));
//                        Intent intent = new Intent(CartActivity.this,CongratsActivity.class);
//                        startActivity(intent);
//                        finish();
                        } else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                            Toast.makeText(CartActivity.this, jsonObject.optString("result"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(CartActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Log.e("123456", "" + t.getMessage());
                Toast.makeText(CartActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void paymentSuccess() {
        startActivity(new Intent(CartActivity.this, CongratsActivity.class));
        finish();
        databaseHelper.DeleteAllOrderData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CartActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}