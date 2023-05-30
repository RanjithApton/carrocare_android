package com.muvierecktech.carrocare.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.PaymentDetailsAdapter;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.PermissionUtils;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityMyOrdersDetailBinding;
import com.muvierecktech.carrocare.model.OrdersList;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersDetailActivity extends AppCompatActivity {
    ActivityMyOrdersDetailBinding binding;
    String date_and_time, order_id, service_type, payment_type, package_type, vehicle_make, vehicle_model, vehicle_no, vehicle_id, package_value,
            total_amount, gst, gst_amount, subtotal_amount, discount_amount, payment_mode, package_months, valid, status, wash_details, extra_interior, cancel_subscription, payment_history,
            schedule_date, schedule_time, work_done, next_due, work_image, work_image_date, invoice;
    List<OrdersList.PaymentDetails> paymentDetails;
    SessionManager sessionManager;
    String token, customerid;

    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQ_CODE = 3000;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_orders_detail);
        sessionManager = new SessionManager(this);
        HashMap<String, String> hashMap = sessionManager.getUserDetails();
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);

        Intent intent = getIntent();
        date_and_time = intent.getStringExtra(Constant.DE_DATE);
        order_id = intent.getStringExtra(Constant.DE_ORDERID);
        service_type = intent.getStringExtra(Constant.DE_SERVTYPE);
        payment_type = intent.getStringExtra(Constant.DE_PAYTYPE);
        package_type = intent.getStringExtra(Constant.DE_PACKTYPE);
        vehicle_make = intent.getStringExtra(Constant.DE_VECMAKE);
        vehicle_model = intent.getStringExtra(Constant.DE_VECMODEL);
        vehicle_no = intent.getStringExtra(Constant.DE_VECNO);
        vehicle_id = intent.getStringExtra(Constant.DE_VECID);
        package_value = intent.getStringExtra(Constant.DE_PACKVALUE);
        total_amount = intent.getStringExtra(Constant.DE_TOTAMOUNT);
        gst = intent.getStringExtra(Constant.DE_GST);
        gst_amount = intent.getStringExtra(Constant.DE_GSTAMOUNT);
        subtotal_amount = intent.getStringExtra(Constant.DE_SUB_TOTAMOUNT);
        discount_amount = intent.getStringExtra(Constant.DE_DISCOUNTMOUNT);
        payment_mode = intent.getStringExtra(Constant.DE_PAYMODE);
        package_months = intent.getStringExtra(Constant.DE_PACKMONTH);
        valid = intent.getStringExtra(Constant.DE_VALID);
        status = intent.getStringExtra(Constant.DE_STATUS);
        wash_details = intent.getStringExtra(Constant.DE_WASHDET);
        extra_interior = intent.getStringExtra(Constant.DE_EXTRADET);
        cancel_subscription = intent.getStringExtra(Constant.DE_CANCEL);
        payment_history = intent.getStringExtra(Constant.DE_PAYMENT_HISTORY);
        paymentDetails = (ArrayList<OrdersList.PaymentDetails>) intent.getSerializableExtra(Constant.DE_PAYMENTDETAILS);
        schedule_date = intent.getStringExtra(Constant.DE_SCHEDULE_DATE);
        schedule_time = intent.getStringExtra(Constant.DE_SCHEDULE_TIME);
        work_done = intent.getStringExtra(Constant.DE_WORK_DONE);
        next_due = intent.getStringExtra(Constant.DE_NEXTDUE);
        //work_image = intent.getStringExtra(Constant.DE_IMAGE);
        work_image_date = intent.getStringExtra(Constant.DE_IMAGE_DATE);
        invoice = intent.getStringExtra(Constant.DE_INVOICE);

        binding.date.setText(date_and_time);
        binding.paymentId.setText(order_id);
        binding.serviceType.setText(service_type);
        binding.paymentType.setText(payment_type);
        binding.packageType.setText(package_type);
        binding.vecMake.setText(vehicle_make);
        binding.vecModel.setText(vehicle_model);
        binding.vecNo.setText(vehicle_no);
        binding.vecId.setText(vehicle_id);
        binding.valid.setText(valid);

        binding.scheduleDate.setText(schedule_date);
        binding.workDone.setText(work_done);
        binding.paymentMode.setText(payment_mode);
        binding.paidCount.setText(package_months);
        binding.status.setText(status);
        binding.imageDate.setText(work_image_date);

        binding.packageValue.setText("₹ " + package_value);
        binding.discountAmount.setText("₹ " + discount_amount);
        if (!TextUtils.isEmpty(subtotal_amount))
            binding.subtotalAmount.setText("₹ " + subtotal_amount);
        else {
            if (!TextUtils.isEmpty(discount_amount))
                binding.subtotalAmount.setText("₹ " + discount_amount);
            else
                binding.subtotalAmount.setText("₹ " + package_value);
        }
        if (!TextUtils.isEmpty(gst))
            binding.taxPercentage.setText(gst + "%");
        else
            binding.taxPercentage.setText("0 %");
        if (!TextUtils.isEmpty(gst_amount))
            binding.taxAmount.setText("₹ " + gst_amount);
        else
            binding.taxAmount.setText("₹ 0");
        binding.totalAmount.setText("₹ " + total_amount);


        if (service_type.equalsIgnoreCase("Addon") && payment_type.equalsIgnoreCase("One Time")) {

            String get_img;

            get_img = intent.getStringExtra(Constant.DE_IMAGE);

            if (get_img != null && !get_img.isEmpty()) {
                work_image = get_img;
                //work_image = "0";
            } else {
                work_image = "0";
            }


            Picasso.get()
                    .load(work_image)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(binding.vecImage);

            binding.viewWashdetails.setVisibility(View.GONE);
            binding.viewhistory.setVisibility(View.GONE);
            binding.viewExtrainterior.setVisibility(View.GONE);
            binding.cancel.setVisibility(View.GONE);
            binding.viewhistory.setVisibility(View.GONE);
            binding.downloadInvoice.setVisibility(View.VISIBLE);
            binding.vehiclemakeField.setVisibility(View.VISIBLE);
            binding.vehiclemodelField.setVisibility(View.VISIBLE);
            binding.vehiclenoField.setVisibility(View.VISIBLE);
            binding.paymentmodeField.setVisibility(View.VISIBLE);
            binding.paidcountField.setVisibility(View.GONE);
            binding.validField.setVisibility(View.GONE);
            binding.statusField.setVisibility(View.VISIBLE);
            //binding.imageField.setVisibility(View.VISIBLE);
            binding.vehicleidField.setVisibility(View.VISIBLE);
            binding.scheduleField.setVisibility(View.VISIBLE);
            binding.workdoneField.setVisibility(View.VISIBLE);

//            if(!package_value.equals("100")){
//                binding.imageField.setVisibility(View.VISIBLE);
//            }else{
//                binding.imageField.setVisibility(View.GONE);
//            }

            if (work_done.equalsIgnoreCase("No")) {
                binding.imageField.setVisibility(View.GONE);
            } else {
                binding.imageField.setVisibility(View.VISIBLE);
            }

            if (discount_amount.equalsIgnoreCase("0")) {
                binding.discountField.setVisibility(View.GONE);
            } else {
                binding.discountField.setVisibility(View.VISIBLE);
            }
        }
        if (service_type.equalsIgnoreCase("Addon") && payment_type.equalsIgnoreCase("Monthly")) {
            if (discount_amount.equalsIgnoreCase("0")) {
                binding.discountField.setVisibility(View.GONE);
            } else {
                binding.discountField.setVisibility(View.VISIBLE);
            }
            if (wash_details.equalsIgnoreCase("0")) {
                binding.viewWashdetails.setVisibility(View.VISIBLE);
                binding.valid.setText(valid);
            } else if (wash_details.equalsIgnoreCase("1")) {
                binding.viewWashdetails.setVisibility(View.VISIBLE);
                binding.valid.setText(next_due);
            } else {
                binding.viewWashdetails.setVisibility(View.GONE);
            }
            if (extra_interior.equalsIgnoreCase("0")) {
                binding.viewExtrainterior.setVisibility(View.GONE);
            } else if (extra_interior.equalsIgnoreCase("1")) {
                binding.viewExtrainterior.setVisibility(View.VISIBLE);
            } else {
                binding.viewExtrainterior.setVisibility(View.GONE);
            }
            if (cancel_subscription.equalsIgnoreCase("0")) {
                binding.cancel.setVisibility(View.GONE);
            } else if (cancel_subscription.equalsIgnoreCase("1")) {
                binding.cancel.setVisibility(View.VISIBLE);
            } else {
                binding.cancel.setVisibility(View.GONE);
            }
            if (payment_history.equalsIgnoreCase("0")) {
                binding.viewhistory.setVisibility(View.GONE);
                binding.downloadInvoice.setVisibility(View.VISIBLE);
            } else if (payment_history.equalsIgnoreCase("1")) {
                binding.viewhistory.setVisibility(View.VISIBLE);
                binding.downloadInvoice.setVisibility(View.GONE);
            } else {
                binding.viewhistory.setVisibility(View.GONE);
                binding.downloadInvoice.setVisibility(View.VISIBLE);
            }
        }
        /*if (service_type.equalsIgnoreCase("Addon") && payment_type.equalsIgnoreCase("One Time") && payment_history.equalsIgnoreCase("1")){
            binding.viewhistory.setVisibility(View.GONE);
            binding.viewExtrainterior.setVisibility(View.GONE);
            binding.cancel.setVisibility(View.GONE);

            if (discount_amount.equalsIgnoreCase("0")) {
                binding.discountField.setVisibility(View.GONE);
            } else {
                binding.discountField.setVisibility(View.VISIBLE);
            }

        }*/

        if (service_type.equalsIgnoreCase("Wash")) {
            if (discount_amount.equalsIgnoreCase("0")) {
                binding.discountField.setVisibility(View.GONE);
            } else {
                binding.discountField.setVisibility(View.VISIBLE);
            }
            if (wash_details.equalsIgnoreCase("0")) {
                binding.viewWashdetails.setVisibility(View.VISIBLE);
                binding.valid.setText(valid);
            } else if (wash_details.equalsIgnoreCase("1")) {
                binding.viewWashdetails.setVisibility(View.VISIBLE);
                binding.valid.setText(next_due);
            } else {
                binding.viewWashdetails.setVisibility(View.GONE);
            }
            if (extra_interior.equalsIgnoreCase("0")) {
                binding.viewExtrainterior.setVisibility(View.GONE);
            } else if (extra_interior.equalsIgnoreCase("1")) {
                binding.viewExtrainterior.setVisibility(View.VISIBLE);
            } else {
                binding.viewExtrainterior.setVisibility(View.GONE);
            }
            if (cancel_subscription.equalsIgnoreCase("0")) {
                binding.cancel.setVisibility(View.GONE);
            } else if (cancel_subscription.equalsIgnoreCase("1")) {
                binding.cancel.setVisibility(View.VISIBLE);
            } else {
                binding.cancel.setVisibility(View.GONE);
            }
            if (payment_history.equalsIgnoreCase("0")) {
                binding.viewhistory.setVisibility(View.GONE);
                binding.downloadInvoice.setVisibility(View.VISIBLE);
            } else if (payment_history.equalsIgnoreCase("1")) {
                binding.viewhistory.setVisibility(View.VISIBLE);
                binding.downloadInvoice.setVisibility(View.GONE);
            } else {
                binding.viewhistory.setVisibility(View.GONE);
                binding.downloadInvoice.setVisibility(View.VISIBLE);
            }
            if (package_type.equalsIgnoreCase("Bike")) {
                binding.viewWashdetails.setVisibility(View.GONE);
            } else {
                binding.viewWashdetails.setVisibility(View.VISIBLE);
            }
        }

        if (service_type.equalsIgnoreCase("Door step Wash") ||
                service_type.equalsIgnoreCase("Door step Detailing") ||
                service_type.equalsIgnoreCase("Door step AddOn")) {

            binding.viewWashdetails.setVisibility(View.GONE);
            binding.viewhistory.setVisibility(View.GONE);
            binding.viewExtrainterior.setVisibility(View.GONE);
            binding.cancel.setVisibility(View.GONE);
            binding.viewhistory.setVisibility(View.GONE);
            binding.downloadInvoice.setVisibility(View.VISIBLE);
            binding.vehiclemakeField.setVisibility(View.VISIBLE);
            binding.vehiclemodelField.setVisibility(View.VISIBLE);
            binding.vehiclenoField.setVisibility(View.VISIBLE);
            binding.paymentmodeField.setVisibility(View.VISIBLE);
            binding.paidcountField.setVisibility(View.GONE);
            binding.validField.setVisibility(View.GONE);
            binding.statusField.setVisibility(View.VISIBLE);
            //binding.imageField.setVisibility(View.VISIBLE);
            binding.vehicleidField.setVisibility(View.VISIBLE);
            binding.scheduleField.setVisibility(View.VISIBLE);
            binding.workdoneField.setVisibility(View.GONE);
            binding.cancelOnetime.setVisibility(View.VISIBLE);

            if (status.equals("Cancel Requested")) {
                binding.cancelOnetime.setVisibility(View.GONE);
            } else {
                binding.cancelOnetime.setVisibility(View.VISIBLE);
            }

            binding.imageField.setVisibility(View.GONE);
//            if(work_done.equalsIgnoreCase("No")){
//                binding.imageField.setVisibility(View.GONE);
//            }else{
//                binding.imageField.setVisibility(View.VISIBLE);
//            }

            if (discount_amount.equalsIgnoreCase("0")) {
                binding.discountField.setVisibility(View.GONE);
            } else {
                binding.discountField.setVisibility(View.VISIBLE);
            }
        }


        if (service_type.equalsIgnoreCase("Disinsfection") || service_type.equalsIgnoreCase("Disinfection")) {

            binding.viewWashdetails.setVisibility(View.GONE);
            binding.viewhistory.setVisibility(View.GONE);
            binding.viewExtrainterior.setVisibility(View.GONE);
            binding.cancel.setVisibility(View.GONE);
            binding.viewhistory.setVisibility(View.GONE);
            binding.downloadInvoice.setVisibility(View.VISIBLE);
            binding.vehiclemakeField.setVisibility(View.VISIBLE);
            binding.vehiclemodelField.setVisibility(View.VISIBLE);
            binding.vehiclenoField.setVisibility(View.VISIBLE);
            binding.paymentmodeField.setVisibility(View.VISIBLE);
            binding.paidcountField.setVisibility(View.GONE);
            binding.validField.setVisibility(View.GONE);
            binding.statusField.setVisibility(View.VISIBLE);
            //binding.imageField.setVisibility(View.VISIBLE);
            binding.vehicleidField.setVisibility(View.VISIBLE);
            binding.scheduleField.setVisibility(View.VISIBLE);
            binding.workdoneField.setVisibility(View.GONE);
            //binding.cancelOnetime.setVisibility(View.VISIBLE);
            binding.viewWashdetails.setVisibility(View.GONE);

//            if(!package_value.equals("100")){
//                binding.imageField.setVisibility(View.VISIBLE);
//            }else{
//                binding.imageField.setVisibility(View.GONE);
//            }
            binding.imageField.setVisibility(View.GONE);
//            if(work_done.equalsIgnoreCase("No")){
//                binding.imageField.setVisibility(View.GONE);
//            }else{
//                binding.imageField.setVisibility(View.VISIBLE);
//            }

            if (discount_amount.equalsIgnoreCase("0")) {
                binding.discountField.setVisibility(View.GONE);
            } else {
                binding.discountField.setVisibility(View.VISIBLE);
            }
        }

        binding.cancelOnetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MyOrdersDetailActivity.this);
                dialog.setCancelable(true);
                dialog.setTitle("Alert!");
                dialog.setMessage("Are you sure want to cancel this order?");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //Action for "Ok".
                                if (isNetworkAvailable()) {
                                    cancelOrder();
                                } else {
                                    AlertDialog.Builder dialog1 = new AlertDialog.Builder(MyOrdersDetailActivity.this);
                                    dialog1.setCancelable(false);
                                    dialog1.setTitle("Alert!");
                                    dialog1.setMessage("No internet.Please check your connection.");
                                    dialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //Action for "Ok".
                                                    cancelOrder();
                                                }
                                            })
                                            .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //Action for "Cancel".
                                                    finish();
                                                }
                                            });

                                    final AlertDialog alert = dialog1.create();
                                    alert.show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Action for "Cancel".
                                //finish();
                                dialog.dismiss();
                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();
            }
        });

        binding.downloadInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String s = invoice.split("=")[1];
                    if(s.equalsIgnoreCase("0")){
                        Toast.makeText(MyOrdersDetailActivity.this, "Invoice Download Failed. Please Contact Admin", Toast.LENGTH_SHORT).show();
                    } else {
                        downloadInvoice(invoice,"Carrocare_Invoice_"+order_id+".pdf");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MyOrdersDetailActivity.this, "Invoice Download Failed. Please Contact Admin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.viewhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.paymrntDetailRl.setVisibility(View.VISIBLE);
            }
        });
        binding.paymrntDetailRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.paymrntDetailRl.setVisibility(View.GONE);
            }
        });
        PaymentDetailsAdapter paymentDetailsAdapter = new PaymentDetailsAdapter(MyOrdersDetailActivity.this, paymentDetails);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyOrdersDetailActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.paymentDetailsRc.setLayoutManager(linearLayoutManager);
        binding.paymentDetailsRc.setAdapter(paymentDetailsAdapter);

        binding.viewWashdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyOrdersDetailActivity.this, CalenderActivity.class);
                intent.putExtra("vecid", vehicle_id);
                intent.putExtra("orderid", order_id);
                intent.putExtra("type", "wash");
                startActivity(intent);
            }
        });
        binding.viewExtrainterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyOrdersDetailActivity.this, CalenderActivity.class);
                intent.putExtra("vecid", vehicle_id);
                intent.putExtra("type", "extra");
                startActivity(intent);
            }
        });
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MyOrdersDetailActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("Confirm!");
                dialog.setMessage("Do you want to cancel subscription?");
                dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
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
                                dialog.dismiss();
                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private void cancelOrder() {

        android.app.AlertDialog.Builder dialogbuilder = new android.app.AlertDialog.Builder(MyOrdersDetailActivity.this);
        dialogbuilder.setCancelable(true);
        View dialogView = LayoutInflater.from(MyOrdersDetailActivity.this).inflate(R.layout.lyt_cancel, null);

        EditText comments = dialogView.findViewById(R.id.et_comments);
        Button submit = dialogView.findViewById(R.id.btn_submit_rate);

        dialogbuilder.setView(dialogView);
        android.app.AlertDialog dialog = dialogbuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comments.getText().toString().equals("")) {
                    Toast.makeText(MyOrdersDetailActivity.this, "Please type reason", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    final KProgressHUD hud = KProgressHUD.create(MyOrdersDetailActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setCancellable(true)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f)
                            .show();
                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    Call<JsonObject> call = apiInterface.cancelCodOrder(order_id + "",
                            customerid + "",
                            comments.getText().toString() + "");
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
                                        Toast.makeText(MyOrdersDetailActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MyOrdersDetailActivity.this, MyOrdersActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                                        Toast.makeText(MyOrdersDetailActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MyOrdersDetailActivity.this, MyOrdersActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    ApiConfig.responseToast(MyOrdersDetailActivity.this, response.code());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            hud.dismiss();
                            Toast.makeText(MyOrdersDetailActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        dialog.show();

    }

    private void work() {
        if (isNetworkAvailable()) {
            CancelSubscription();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MyOrdersDetailActivity.this);
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

    private void CancelSubscription() {
        final KProgressHUD hud = KProgressHUD.create(MyOrdersDetailActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(View.VISIBLE)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.cancelSubs(token + "", "" + vehicle_id, "" + order_id);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        JsonElement jsonElement = response.body();
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        if (jsonObject.optString("code").equalsIgnoreCase("2View.VISIBLEView.VISIBLE")) {
                            Gson gson = new Gson();
                            Toast.makeText(MyOrdersDetailActivity.this, jsonObject.optString("result"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MyOrdersDetailActivity.this, MyOrdersActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MyOrdersDetailActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(MyOrdersDetailActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(MyOrdersDetailActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(MyOrdersDetailActivity.this, MyOrdersActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission ) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (PermissionUtils.neverAskAgainSelected(this, permission)) {
                        displayNeverAskAgainDialog();
                    } else {
                        ActivityCompat.requestPermissions(this, PERMISSIONS,REQ_CODE);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQ_CODE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("REQ_CODE ::: ", "Permission granted successfully");
                //Toast.makeText(this, "Permission granted successfully", Toast.LENGTH_LONG).show();
            } else {
                for (String permission : PERMISSIONS) {
                    PermissionUtils.setShouldShowStatus(this, permission);
                }
            }
        }
    }

    private void displayNeverAskAgainDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("We need to permission for performing necessary task. Please permit the permission through "
                + "Settings screen.\n\nSelect Permissions -> Enable permission");
        builder.setCancelable(false);
        builder.setPositiveButton("Permit Manually", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        //builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void downloadInvoice(String download_invoice, String s) {
        try{
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(download_invoice));
            String title = s;
            request.setTitle(title);
            request.setDescription("Downloading file please wait...");
            String cookie = CookieManager.getInstance().getCookie(download_invoice);
            request.addRequestHeader("cookie",cookie);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title);

            DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);

            PermissionUtils.successToast(this,title);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invoice Download Failed.", Toast.LENGTH_SHORT).show();
        }
    }
}