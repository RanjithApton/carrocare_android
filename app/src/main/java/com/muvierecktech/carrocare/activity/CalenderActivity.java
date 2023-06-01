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
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.PreferredTimeAdapter;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityCalenderBinding;

import com.muvierecktech.carrocare.model.VehicleExtraList;
import com.muvierecktech.carrocare.model.VehicleWashList;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalenderActivity extends AppCompatActivity {
    public static final String RESULT = "result";
    public static final String EVENT = "event";
    private static final int ADD_NOTE = 44;
    public ActivityCalenderBinding binding;
    List<EventDay> mEventDays = new ArrayList<>();
    List<EventDay> mEventDayslist = new ArrayList<>();
    List<VehicleWashList.WashDetails> washDetails;
    List<VehicleWashList.InternalDetails> extraDetails;
    List<VehicleExtraList.ExtraInterior> extraInteriors;
    SessionManager sessionManager;
    DatePickerDialog picker;
    String preTime[] = {Constant.ANYTIME, "9:00 AM - 10:00 AM", "10:00 AM - 11:00 AM", "11:00 AM - 12:00 PM", "12:00 PM - 1:00 PM", "6:00 PM - 7:00 PM", "7:00 PM - 8:00 PM"};
    String token, customerid, vehicleid, orderid, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_calender);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calender);
        sessionManager = new SessionManager(this);
        HashMap<String, String> hashMap = sessionManager.getUserDetails();
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);
//        Calendar calendar = Calendar.getInstance();
//        Date datestart,dateend;

        Intent intent = getIntent();
        vehicleid = intent.getStringExtra("vecid");
        orderid = intent.getStringExtra("orderid");
        type = intent.getStringExtra("type");

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        String[] str = {"2020-8-15","2020-8-16","2020-8-17"};
//
//        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            for (String s : str) {
//                Calendar calendar = Calendar.getInstance();
//                Date datestart = formatter.parse(s);
//                calendar.setTime(datestart);
//                binding.calendarView.setDate(calendar);
//                mEventDays.add(new EventDay(calendar, R.drawable.location));
////                mEventDayslist.addAll(mEventDays);
//            }
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        binding.calendarView.setOnDayClickListener(new OnDayClickListener() {
//            @Override
//            public void onDayClick(EventDay eventDay) {
//                Calendar clickedDayCalendar = eventDay.getCalendar();
//                /*      clickedDayCalendar.set( clickedDayCalendar.get(Calendar.DAY_OF_MONTH + 4) ,  clickedDayCalendar.get(Calendar.MONTH) , clickedDayCalendar.get(Calendar.YEAR));*/
////                mEventDays.add(new EventDay(clickedDayCalendar, R.drawable.email));
//
//
////            Toast.makeText(CalenderActivity.this, "Selected Date:\n" + "Day = " + clickedDayCalendar.get(Calendar.DAY_OF_MONTH) +
////                    "\n" + "Month = " + clickedDayCalendar.get(Calendar.MONTH) + "\n" + "Year = " + clickedDayCalendar.get(Calendar.YEAR), Toast.LENGTH_LONG).show();
//
//            previewPopup();
//            }
//        });
        binding.popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.popup.setVisibility(View.GONE);
            }
        });


//        workExtra();

        if (type.equalsIgnoreCase("extra")) {
            binding.addinternal.setVisibility(View.GONE);
            workextra();
        } else {
            work();
        }


        binding.addinternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.popupinternal.setVisibility(View.VISIBLE);

            }
        });
        binding.popupinternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.popupinternal.setVisibility(View.GONE);
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
                picker = new DatePickerDialog(CalenderActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                binding.preferDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                //picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                picker.getDatePicker().setMinDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                picker.show();
            }
        });
        binding.preferredtimeEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.preferDate.getText().toString())) {
                    Toast.makeText(CalenderActivity.this, "Choose Preferred Schedule", Toast.LENGTH_SHORT).show();
                } else {
                    binding.timerl.setVisibility(View.VISIBLE);
                    PreferredTimeAdapter preferredAdapter = new PreferredTimeAdapter(CalenderActivity.this, preTime);
                    LinearLayoutManager linearLayoutManage = new LinearLayoutManager(CalenderActivity.this, LinearLayoutManager.VERTICAL, false);
                    binding.timeRc.setLayoutManager(linearLayoutManage);
                    binding.timeRc.setAdapter(preferredAdapter);
                }
            }
        });
        binding.prefertimeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.preferDate.getText().toString())) {
                    Toast.makeText(CalenderActivity.this, "Choose Preferred Schedule", Toast.LENGTH_SHORT).show();
                } else {
                    binding.timerl.setVisibility(View.VISIBLE);
                    PreferredTimeAdapter preferredAdapter = new PreferredTimeAdapter(CalenderActivity.this, preTime);
                    LinearLayoutManager linearLayoutManage = new LinearLayoutManager(CalenderActivity.this, LinearLayoutManager.VERTICAL, false);
                    binding.timeRc.setLayoutManager(linearLayoutManage);
                    binding.timeRc.setAdapter(preferredAdapter);
                }
            }
        });
    }

    private void workextra() {
        if (isNetworkAvailable()) {
            ExtraInterior();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CalenderActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection.");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Action for "Ok".
                            workextra();
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

    private void ExtraInterior() {
        extraInteriors = new ArrayList<>();
        final KProgressHUD hud = KProgressHUD.create(CalenderActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<VehicleExtraList> call = apiInterface.extraDetails("" + customerid, "" + token, vehicleid + "");
        call.enqueue(new Callback<VehicleExtraList>() {
            @Override
            public void onResponse(Call<VehicleExtraList> call, Response<VehicleExtraList> response) {
                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        final VehicleExtraList vehicleExtraList = response.body();
                        if (vehicleExtraList.code.equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            String json = gson.toJson(vehicleExtraList);
//                    binding.noorders.setVisibility(View.GONE);
//                    binding..setVisibility(View.VISIBLE);
                            extraInteriors = vehicleExtraList.extra_interior;
                            try {
                                for (int i = 0; i < extraInteriors.size(); i++) {
                                    @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    Calendar calendar = Calendar.getInstance();
                                    Date datestart;
                                    datestart = formatter.parse(extraInteriors.get(i).schedule_date);
                                    calendar.setTime(datestart);
                                    binding.calendarView.setDate(calendar);
                                    mEventDays.add(new EventDay(calendar, R.drawable.car_wash));
                                }
                                binding.calendarView.setEvents(mEventDays);
                                Log.e("Event", String.valueOf(mEventDays));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            binding.calendarView.setOnDayClickListener(new OnDayClickListener() {
                                @Override
                                public void onDayClick(EventDay eventDay) {
                                    try {
                                        if (eventDay.getImageResource() == R.drawable.car_wash) {
                                            for (int i = 0; i < extraInteriors.size(); i++) {
                                                @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                                Calendar calendar = Calendar.getInstance();
                                                Date datestart;
                                                datestart = formatter.parse(extraInteriors.get(i).schedule_date);
                                                calendar.setTime(datestart);
                                                Log.e("CALENDAREXTRA", calendar.toString() + "****" + eventDay);
                                                if (eventDay.getCalendar().equals(calendar)) {
                                                    if (extraInteriors.get(i).schedule_date.equalsIgnoreCase("No")) {
                                                        previewPopup1("Internal Clean", "No Data Found");
                                                    } else {
                                                        CalenderActivity calenderActivity = CalenderActivity.this;
                                                        String str = extraInteriors.get(i).vehicle_image;
                                                        previewPopup(str, "Internal Clean", "Wash Date : " + extraInteriors.get(i).schedule_date, "");
                                                    }
                                                }
                                        /*if (eventDay.getCalendar().equals(calendar)) {
                                            previewPopup(extraInteriors.get(i).vehicle_image, "Internal Clean");
                                        }*/
                                            }
                                        }

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

//                    MyOrdersAdapter mainAdapter = new MyOrdersAdapter(CalenderActivity.this,ordersList.orders);
//                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CalenderActivity.this,LinearLayoutManager.VERTICAL,false);
//                    binding.ordersRc.setLayoutManager(linearLayoutManager);
//                    binding.ordersRc.setAdapter(mainAdapter);

                        } else if (vehicleExtraList.code.equalsIgnoreCase("203")) {
                            sessionManager.logoutUsers();
                        } else if (vehicleExtraList.code.equalsIgnoreCase("201")) {

                            Toast.makeText(CalenderActivity.this, vehicleExtraList.message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(CalenderActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<VehicleExtraList> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(CalenderActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void workinternal(final String type, final String veid) {
        if (isNetworkAvailable()) {
            IntenalSchedule(type, veid);
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CalenderActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection.");
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

    private void work() {
        if (isNetworkAvailable()) {
            Wash();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CalenderActivity.this);
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

    private void IntenalSchedule(String type, String veid) {
        final KProgressHUD hud = KProgressHUD.create(CalenderActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.interSchedule(customerid + "", token + "", vehicleid + "", "" + orderid, "" + binding.preferDate.getText().toString()
                , "" + binding.preferredtimeEdt.getText().toString(), "" + binding.commentTxt.getText().toString(), "" + type, veid + "");
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
                            Toast.makeText(CalenderActivity.this, jsonObject.optString("message"), Toast.LENGTH_LONG).show();
                            finish();
                        } else if (jsonObject.optString("code").equalsIgnoreCase("203")) {
                            sessionManager.logoutUsers();
                        } else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                            Toast.makeText(CalenderActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(CalenderActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(CalenderActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
//    private void workExtra() {
//        extraDetails = new ArrayList<>();
//        final KProgressHUD hud = KProgressHUD.create(CalenderActivity.this)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setCancellable(true)
//                .setAnimationSpeed(2)
//                .setDimAmount(0.5f)
//                .show();
//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<VehicleExtraList> call = apiInterface.extraDetails(""+customerid,""+token,orderid+"");
//        call.enqueue(new Callback<VehicleExtraList>() {
//            @Override
//            public void onResponse(Call<VehicleExtraList> call, Response<VehicleExtraList> response) {
//                final VehicleExtraList vehicleWashList = response.body();
//                hud.dismiss();
//                if (vehicleWashList.code.equalsIgnoreCase("200")) {
//                    Gson gson = new Gson();
//                    String json = gson.toJson(vehicleWashList);
////                    binding.noorders.setVisibility(View.GONE);
////                    binding..setVisibility(View.VISIBLE);
//                    extraDetails = vehicleWashList.details;
//                    try {
//                        for (int i = 0 ; i < extraDetails.size(); i++) {
//                            @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//                            Calendar calendar = Calendar.getInstance();
//                            Calendar calendar1 = Calendar.getInstance();
//                            Date datestart,dateend;
//                            if (!TextUtils.isEmpty(extraDetails.get(i).schedule_date1)){
//                                datestart = formatter.parse(extraDetails.get(i).schedule_date1);
//                                calendar.setTime(datestart);
//                                binding.calendarView.setDate(calendar);
//                                mEventDays.add(new EventDay(calendar, R.drawable.car_extra));
//                            }
//                            if (!TextUtils.isEmpty(extraDetails.get(i).schedule_date2)){
//                                dateend = formatter.parse(extraDetails.get(i).schedule_date2);
//                                calendar1.setTime(dateend);
//                                binding.calendarView.setDate(calendar1);
//                                mEventDays.add(new EventDay(calendar1, R.drawable.car_extra));
//                            }
//
//                        }
//                        binding.calendarView.setEvents(mEventDays);
//                        Log.e("Event", String.valueOf(mEventDays));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    binding.calendarView.setOnDayClickListener(new OnDayClickListener() {
//                        @Override
//                        public void onDayClick(EventDay eventDay) {
//                            try {
//                                for (int i = 0 ; i < extraDetails.size(); i++) {
//                                    @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//                                    Calendar calendar = Calendar.getInstance();
//                                    Calendar calendar1 = Calendar.getInstance();
//                                    Date datestart,dateend;
////                                    datestart = formatter.parse(extraDetails.get(i).updated_date);
////                                    calendar.setTime(datestart);
//                                    if (!TextUtils.isEmpty(extraDetails.get(i).schedule_date1)){
//                                        datestart = formatter.parse(extraDetails.get(i).schedule_date1);
//                                        calendar.setTime(datestart);
//                                    }
//                                    if (!TextUtils.isEmpty(extraDetails.get(i).schedule_date2)){
//                                        dateend = formatter.parse(extraDetails.get(i).schedule_date2);
//                                        calendar1.setTime(dateend);
//                                    }
//                                    if (eventDay.getCalendar().equals(calendar)){
//                                        previewPopup(extraDetails.get(i).vehicle_image1,"Internal Wash");
//                                    }else if (eventDay.getCalendar().equals(calendar1)){
//                                        previewPopup(extraDetails.get(i).vehicle_image2,"Internal Wash");
//                                    }
//                                }
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
////                    MyOrdersAdapter mainAdapter = new MyOrdersAdapter(CalenderActivity.this,ordersList.orders);
////                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CalenderActivity.this,LinearLayoutManager.VERTICAL,false);
////                    binding.ordersRc.setLayoutManager(linearLayoutManager);
////                    binding.ordersRc.setAdapter(mainAdapter);
//
//                }else  if (vehicleWashList.code.equalsIgnoreCase("203")) {
//                    sessionManager.logoutUsers();
//                }else  if (vehicleWashList.code.equalsIgnoreCase("201")){
////                    binding.noorders.setVisibility(View.VISIBLE);
////                    binding.ordersRc.setVisibility(View.GONE);
//                }
//            }
//            @Override
//            public void onFailure(Call<VehicleExtraList> call, Throwable t) {
//                hud.dismiss();
//                Toast.makeText(CalenderActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void Wash() {
        washDetails = new ArrayList<>();
        extraDetails = new ArrayList<>();
        final KProgressHUD hud = KProgressHUD.create(CalenderActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<VehicleWashList> call = apiInterface.washDetails("" + customerid, "" + token, vehicleid + "", "" + orderid);
        call.enqueue(new Callback<VehicleWashList>() {
            @Override
            public void onResponse(Call<VehicleWashList> call, Response<VehicleWashList> response) {
                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        final VehicleWashList vehicleWashList = response.body();
                        if (vehicleWashList.code.equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            String json = gson.toJson(vehicleWashList);
                            washDetails = vehicleWashList.wash_details;
                            try {
                                for (int i = 0; i < washDetails.size(); i++) {
                                    @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    Calendar calendar = Calendar.getInstance();
                                    Date datestart;
                                    datestart = formatter.parse(washDetails.get(i).date);
                                    calendar.setTime(datestart);
                                    binding.calendarView.setDate(calendar);
                                    mEventDays.add(new EventDay(calendar, R.drawable.car_wash));
                                }
                                binding.calendarView.setEvents(mEventDays);
                                Log.e("Event", String.valueOf(mEventDays));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            extraDetails = vehicleWashList.internal_details;
                            try {
                                for (int i = 0; i < extraDetails.size(); i++) {
                                    @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    Calendar calendar = Calendar.getInstance();
                                    Calendar calendar1 = Calendar.getInstance();
                                    Date datestart, dateend;
                                    if (!TextUtils.isEmpty(extraDetails.get(i).schedule_date1)) {
                                        datestart = formatter.parse(extraDetails.get(i).schedule_date1);
                                        calendar.setTime(datestart);
                                        binding.calendarView.setDate(calendar);
                                        mEventDays.add(new EventDay(calendar, R.drawable.car_extra));
                                    }
                                    if (!TextUtils.isEmpty(extraDetails.get(i).schedule_date2)) {
                                        dateend = formatter.parse(extraDetails.get(i).schedule_date2);
                                        calendar1.setTime(dateend);
                                        binding.calendarView.setDate(calendar1);
                                        mEventDays.add(new EventDay(calendar1, R.drawable.car_extra));
                                    }
                                    if (!TextUtils.isEmpty(extraDetails.get(i).schedule_date1) && !TextUtils.isEmpty(extraDetails.get(i).schedule_date2)) {
                                        binding.addinternal.setVisibility(View.GONE);
                                    } else {
                                        binding.addinternal.setVisibility(View.VISIBLE);
                                        final int finalI = i;
                                        binding.submit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (binding.preferDate.getText().toString().length() > 0 && binding.preferredtimeEdt.getText().toString().length() > 0) {
                                                    if (TextUtils.isEmpty(extraDetails.get(finalI).schedule_date1) && TextUtils.isEmpty(extraDetails.get(finalI).schedule_date2)) {
                                                        workinternal("1", extraDetails.get(finalI).id);
                                                    } else if (!TextUtils.isEmpty(extraDetails.get(finalI).schedule_date1) && TextUtils.isEmpty(extraDetails.get(finalI).schedule_date2)) {
                                                        workinternal("2", extraDetails.get(finalI).id);
                                                    }
                                                } else {
                                                    Toast.makeText(CalenderActivity.this, Constant.CHOOSEDATETIME, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                                binding.calendarView.setEvents(mEventDays);
                                Log.e("Event", String.valueOf(mEventDays));


                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            binding.calendarView.setOnDayClickListener(new OnDayClickListener() {
                                @Override
                                public void onDayClick(EventDay eventDay) {
                                    try {
                                        if (eventDay.getImageResource() == R.drawable.car_wash) {
                                            for (int i = 0; i < washDetails.size(); i++) {
                                                @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                                Calendar calendar = Calendar.getInstance();
                                                Date datestart;
                                                datestart = formatter.parse(washDetails.get(i).date);
                                                calendar.setTime(datestart);
                                                Log.e("CALENDARWASH", calendar.toString() + "****" + eventDay);
                                                if (eventDay.getCalendar().equals(calendar)) {
                                                    previewPopup(washDetails.get(i).vehicle_image, "External Wash", washDetails.get(i).date, "");
                                                }
                                            }
                                        } else if (eventDay.getImageResource() == R.drawable.car_extra) {
                                            for (int i = 0; i < extraDetails.size(); i++) {
                                                @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                                Calendar calendar = Calendar.getInstance();
                                                Calendar calendar1 = Calendar.getInstance();
                                                Date datestart, dateend;
//                                    datestart = formatter.parse(extraDetails.get(i).updated_date);
//                                    calendar.setTime(datestart);
                                                if (!TextUtils.isEmpty(extraDetails.get(i).schedule_date1)) {
                                                    datestart = formatter.parse(extraDetails.get(i).schedule_date1);
                                                    calendar.setTime(datestart);
                                                }
                                                if (!TextUtils.isEmpty(extraDetails.get(i).schedule_date2)) {
                                                    dateend = formatter.parse(extraDetails.get(i).schedule_date2);
                                                    calendar1.setTime(dateend);
                                                }
                                                if (eventDay.getCalendar().equals(calendar)) {
                                                    if (extraDetails.get(i).schedule_work_status1.equalsIgnoreCase("No")) {
                                                        previewPopup1("Inrernal Wash", "No Data Found");
                                                    } else {
                                                        String str = extraDetails.get(i).vehicle_image1;
                                                        previewPopup(str, "Internal Wash", "Wash Date : " + extraDetails.get(i).vehicle_image1_dateandtime, "Washed Status: Cleaned");
                                                    }
                                                    //previewPopup(extraDetails.get(i).vehicle_image1, "Internal Wash");
                                                } else if (eventDay.getCalendar().equals(calendar1)) {
                                                    if (extraDetails.get(i).schedule_work_status2.equalsIgnoreCase("No")) {
                                                        previewPopup1("Inrernal Wash", "No Data Found");
                                                    } else {
                                                        String str2 = extraDetails.get(i).vehicle_image2;
                                                        previewPopup(str2, "Internal Wash", "Wash Date : " + extraDetails.get(i).vehicle_image2_dateandtime, "Washed Status: Cleaned");
                                                    }
                                                    //previewPopup(extraDetails.get(i).vehicle_image2, "Internal Wash");
                                                }
                                            }
                                        }

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        } else if (vehicleWashList.code.equalsIgnoreCase("203")) {
                            sessionManager.logoutUsers();
                        } else if (vehicleWashList.code.equalsIgnoreCase("201")) {
                            Toast.makeText(CalenderActivity.this, vehicleWashList.message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(CalenderActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<VehicleWashList> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(CalenderActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void previewPopup(String vehicle_image, String Type, String Date, String Status) {
        binding.popup.setVisibility(View.VISIBLE);
        binding.name.setText(Type + "");
        binding.washDate.setText(Date + "");
        binding.washDate.setText(Date + "");
        binding.washStatus.setVisibility(View.VISIBLE);
        binding.washStatus.setText(Status + "");
        if (TextUtils.isEmpty(vehicle_image)) {
            this.binding.noimage.setVisibility(View.VISIBLE);
            this.binding.carImg.setVisibility(View.GONE);
            return;
        }
        this.binding.noimage.setVisibility(View.GONE);
        this.binding.carImg.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(vehicle_image)
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(binding.carImg);
    }


    public void previewPopup1(String Type, String Date) {
        binding.popup.setVisibility(View.VISIBLE);
        binding.name.setText(Type + "");
        binding.washDate.setText(Date + "");
        binding.washStatus.setVisibility(View.GONE);
        this.binding.carImg.setVisibility(View.GONE);
        this.binding.noimage.setVisibility(View.GONE);
    }

    /*private void previewPopup(String vehicle_image, String internal_wash) {
        binding.popup.setVisibility(View.VISIBLE);
        binding.name.setText(internal_wash+"");
        if (TextUtils.isEmpty(vehicle_image)){
            binding.noimage.setVisibility(View.VISIBLE);
            binding.carImg.setVisibility(View.GONE);
        }else {
            binding.noimage.setVisibility(View.GONE);
            binding.carImg.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(vehicle_image)
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.carImg);
        }
    }*/
}