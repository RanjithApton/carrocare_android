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
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.applandeo.materialcalendarview.EventDay;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.IntWashPrefTimeAdapter;
import com.muvierecktech.carrocare.adapter.PreferredTimeAdapter;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityInternalwashBinding;
import com.muvierecktech.carrocare.model.OrdersList;
import com.muvierecktech.carrocare.model.VehicleWashList;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InternalwashActivity extends AppCompatActivity {
    public ActivityInternalwashBinding binding;
    String customerid;
    List<VehicleWashList.InternalDetails> extraDetails;
    List<EventDay> mEventDays = new ArrayList();
    List<OrdersList.Orders> order;
    ArrayList<String> orderId;
    String order_id;
    DatePickerDialog picker;
    String[] preTime = {Constant.ANYTIME, "9.00 AM - 10.00 AM", "10.00 AM - 11.00 AM", "11.00 AM - 12.00 PM", "12.00 PM - 1.00 PM", "6.00 PM - 7.00 PM", "7.00 PM - 8.00 PM"};
    SessionManager sessionManager;
    String token;
    String vehicle_id;
    String vehicle_make;
    String vehicle_model;
    List<VehicleWashList.WashDetails> washDetails;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = (ActivityInternalwashBinding) DataBindingUtil.setContentView(InternalwashActivity.this, R.layout.activity_internalwash);
        SessionManager sessionManager2 = new SessionManager(InternalwashActivity.this);
        sessionManager = sessionManager2;
        HashMap<String, String> userDetails = sessionManager2.getUserDetails();
        token = userDetails.get(SessionManager.KEY_TOKEN);
        customerid = userDetails.get(SessionManager.KEY_USERID);
        Intent intent = getIntent();
        order_id = intent.getStringExtra(Constant.DE_ORDERID);
        vehicle_id = intent.getStringExtra(Constant.DE_VECID);
        vehicle_make = intent.getStringExtra(Constant.DE_VECMAKE);
        vehicle_model = intent.getStringExtra(Constant.DE_VECMODEL);
        work();
        TextView textView = binding.orderEdt;
        textView.setText("Order Id : " + order_id + "\n\nVehicle Id : " + vehicle_id);
        TextView textView2 = binding.vehicleMake;
        textView2.setText("Vehicle Make : " + vehicle_make + "\n\nVechicle Model : " + vehicle_model);
        binding.back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(InternalwashActivity.this,MainActivity.class));
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
                picker = new DatePickerDialog(InternalwashActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                binding.preferDate.setText(year + "-" +(monthOfYear + 1) + "-" +dayOfMonth);
                            }
                        }, year, month, day);
                picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                picker.show();
            }
        });
        binding.preferredtimeEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.preferDate.getText().toString())){
                    Toast.makeText(InternalwashActivity.this,"Choose Preferred Schedule",Toast.LENGTH_SHORT).show();
                }else {
                    binding.timerl.setVisibility(View.VISIBLE);
                    IntWashPrefTimeAdapter intWashPrefTimeAdapter = new IntWashPrefTimeAdapter(InternalwashActivity.this, preTime);
                    LinearLayoutManager linearLayoutManage = new LinearLayoutManager(InternalwashActivity.this, LinearLayoutManager.VERTICAL, false);
                    binding.timeRc.setLayoutManager(linearLayoutManage);
                    binding.timeRc.setAdapter(intWashPrefTimeAdapter);
                }
            }
        });
        binding.prefertimeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.preferDate.getText().toString())) {
                    Toast.makeText(InternalwashActivity.this, "Choose Preferred Schedule", Toast.LENGTH_SHORT).show();
                } else {
                    binding.timerl.setVisibility(View.VISIBLE);
                    IntWashPrefTimeAdapter intWashPrefTimeAdapter = new IntWashPrefTimeAdapter(InternalwashActivity.this, preTime);
                    LinearLayoutManager linearLayoutManage = new LinearLayoutManager(InternalwashActivity.this, LinearLayoutManager.VERTICAL, false);
                    binding.timeRc.setLayoutManager(linearLayoutManage);
                    binding.timeRc.setAdapter(intWashPrefTimeAdapter);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    private void work() {
        if (isNetworkAvailable()){
            Wash();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(InternalwashActivity.this);
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

    /* access modifiers changed from: private */
    public void workinternal(final String type, final String veid) {
        if (isNetworkAvailable()) {
            IntenalSchedule(type, veid);
        }
        else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(InternalwashActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection." );
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Ok".
                    workinternal(type, veid);
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


    private void IntenalSchedule(String type, String veid) {

        final KProgressHUD hud = KProgressHUD.create(InternalwashActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.interSchedule(customerid+"",token+"",vehicle_id+"",""+order_id,""+binding.preferDate.getText().toString()
                ,""+binding.preferredtimeEdt.getText().toString(),""+binding.commentTxt.getText().toString(),""+type,veid+"");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body();
                hud.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                        Gson gson = new Gson();
                        Toast.makeText(InternalwashActivity.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        finish();
                    }else  if (jsonObject.optString("code").equalsIgnoreCase("203")) {
                        sessionManager.logoutUsers();
                    }else  if (jsonObject.optString("code").equalsIgnoreCase("201")){
                        Toast.makeText(InternalwashActivity.this,jsonObject.optString("message"),Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(InternalwashActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void Wash() {
        washDetails = new ArrayList<>();
        extraDetails = new ArrayList<>();
        final KProgressHUD hud = KProgressHUD.create(InternalwashActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<VehicleWashList> call = apiInterface.washDetails(""+customerid,""+token,vehicle_id+"",""+order_id);
        call.enqueue(new Callback<VehicleWashList>() {
            @Override
            public void onResponse(Call<VehicleWashList> call, Response<VehicleWashList> response) {
                VehicleWashList body = response.body();
                hud.dismiss();
                if (body.code.equalsIgnoreCase("200")) {
                    new Gson().toJson((Object) body);
                    washDetails = body.wash_details;
                    try {
                        for (int i = 0 ; i < washDetails.size(); i++) {
                            @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Calendar calendar = Calendar.getInstance();
                            Date datestart;
                            datestart = formatter.parse(washDetails.get(i).date);
                            calendar.setTime(datestart);
                            mEventDays.add(new EventDay(calendar, R.drawable.car_wash));
                        }
                        Log.e("Event", String.valueOf(mEventDays));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    extraDetails = body.internal_details;

                    try {
                        for (int i = 0 ; i < extraDetails.size(); i++) {
                            @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Calendar calendar = Calendar.getInstance();
                            Calendar calendar1 = Calendar.getInstance();
                            if (!TextUtils.isEmpty(extraDetails.get(i).schedule_date1)){
                                calendar.setTime(formatter.parse(extraDetails.get(i).schedule_date1));
                                if (!extraDetails.get(i).schedule_date1.equalsIgnoreCase("null")) {
                                    binding.date1.setText(extraDetails.get(i).schedule_date1);
                                }
                            }
                            if (!TextUtils.isEmpty(extraDetails.get(i).schedule_date2)){
                                calendar1.setTime(formatter.parse(extraDetails.get(i).schedule_date1));
                                if (!extraDetails.get(i).schedule_date1.equalsIgnoreCase("null")) {
                                    binding.date1.setText(extraDetails.get(i).schedule_date1);
                                }
                            }
                            if (!TextUtils.isEmpty(extraDetails.get(i).schedule_date1) && !TextUtils.isEmpty(extraDetails.get(i).schedule_date2)){
                                binding.date1.setText("Internal  wash 1 : "+extraDetails.get(i).schedule_date1);
                                binding.date2.setText("Internal  wash 2 : "+extraDetails.get(i).schedule_date2);
                                binding.date1.setVisibility(View.VISIBLE);
                                binding.date2.setVisibility(View.VISIBLE);
                                binding.note.setVisibility(View.VISIBLE);
                                binding.internalClean.setVisibility(View.GONE);
                            }else{
                                final int finalI = i;
                                binding.date1.setVisibility(View.GONE);
                                binding.date2.setVisibility(View.GONE);
                                binding.note.setVisibility(View.GONE);
                                binding.internalClean.setVisibility(View.VISIBLE);
                                binding.submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (binding.preferDate.getText().toString().length()>0 && binding.preferredtimeEdt.getText().toString().length()>0){
                                            if(TextUtils.isEmpty(extraDetails.get(finalI).schedule_date1) && TextUtils.isEmpty(extraDetails.get(finalI).schedule_date2)){
                                                workinternal("1",extraDetails.get(finalI).id);
                                            }else if(!TextUtils.isEmpty(extraDetails.get(finalI).schedule_date1) && TextUtils.isEmpty(extraDetails.get(finalI).schedule_date2)){
                                                workinternal("2",extraDetails.get(finalI).id);
                                            }
                                        }else {
                                            Toast.makeText(InternalwashActivity.this,Constant.CHOOSEDATETIME,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (body.code.equalsIgnoreCase("203")) {
                    sessionManager.logoutUsers();
                } else if (body.code.equalsIgnoreCase("201")) {
                    Toast.makeText(InternalwashActivity.this, body.message, Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(Call<VehicleWashList> call, Throwable th) {
                hud.dismiss();
                Toast.makeText(InternalwashActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(InternalwashActivity.this, MainActivity.class));
        finish();
    }
}
