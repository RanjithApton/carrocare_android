package com.muvierecktech.carrocare.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.adapter.PreferredAdapter;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.DatabaseHelper;
import com.muvierecktech.carrocare.common.MyDatabaseHelper;
import com.muvierecktech.carrocare.databinding.ActivityPaymentOptionBinding;
import com.muvierecktech.carrocare.model.OneTimeWashCheckout;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;

import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.icu.text.MessagePattern.ArgType.SELECT;
import static com.google.firebase.messaging.Constants.MessagePayloadKeys.FROM;
import static com.muvierecktech.carrocare.common.DatabaseHelper.TABLE_NAME;

public class PaymentOptionActivity extends AppCompatActivity implements PaymentResultListener {
    public static String time;
    public ActivityPaymentOptionBinding binding;
    String cartype, carid, carimage, carcolor, parkingarea, parkinglotno, carno, carmakemodel, apartname, prefersch, prefertime, carprice, onetimecarprice, carcat, servicetype, carname;
    String planId, subscriptionId, razorpayid, custname, custmob, custemail, customerid, token, action, onetimeService, paidMonths, discountAmount;
    String fineAmount = "0";
    SessionManager sessionManager;
    MyDatabaseHelper databaseHelper;
    String[] subsMonths = {"1 Month", "2 Months", "3 Months", "4 Months", "5 Months", "6 Months", "7 Months", "8 Months", "9 Months", "10 Months", "11 Months", "12 Months", "13 Months", "14 Months", "15 Months", "16 Months", "17 Months", "18 Months", "19 Months", "20 Months", "21 Months", "22 Months", "23 Months", "24 Months"};
    String preTime[] = {Constant.ANYTIME, "9:00 AM - 10:00 AM", "10:00 AM - 11:00 AM", "11:00 AM - 12:00 PM", "12:00 PM - 1:00 PM", "6:00 PM - 7:00 PM", "7:00 PM - 8:00 PM"};
    int totalAmountStr;
    DatePickerDialog picker;
    List<OneTimeWashCheckout.getResult> result;
    ArrayList<String> spinner_item;

    @SuppressLint({"LongLogTag", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_payment_option);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_option);

        sessionManager = new SessionManager(this);
        HashMap<String, String> hashMap = sessionManager.getUserDetails();
        customerid = hashMap.get(SessionManager.KEY_USERID);
        token = hashMap.get(SessionManager.KEY_TOKEN);

        databaseHelper = new MyDatabaseHelper(this);
        spinner_item = new ArrayList<>();

        showCartCount();

        binding.checkCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentOptionActivity.this, CartActivity.class));
            }
        });

        Intent intent = getIntent();
        cartype = intent.getStringExtra(Constant.CARTYPE);
        carid = intent.getStringExtra(Constant.CARID);
        carimage = intent.getStringExtra(Constant.CARIMAGE);
        carcolor = intent.getStringExtra(Constant.CARCOLOR);
        parkingarea = intent.getStringExtra(Constant.PARKINGAREA);
        parkinglotno = intent.getStringExtra(Constant.PARKINGLOT);
        carno = intent.getStringExtra(Constant.CARNO);
        carmakemodel = intent.getStringExtra(Constant.CARMAKEMODEL);
        apartname = intent.getStringExtra(Constant.APARTNAME);
        prefersch = intent.getStringExtra(Constant.PREFERREDSCHEDULE);
        prefertime = intent.getStringExtra(Constant.PREFERREDTIME);
        carprice = intent.getStringExtra(Constant.CARPRICE);
        carcat = intent.getStringExtra(Constant.CARCATEGORY);
        servicetype = intent.getStringExtra(Constant.SERVICETYPE);
        carname = intent.getStringExtra(Constant.CARNAME);

        onetimecarprice = carprice;


        binding.vecMakemodel.setText(carmakemodel);
        binding.vecColor.setText(carcolor);
        binding.vecNo.setText(carno);
        binding.parkingArea.setText(parkingarea);
        binding.parkingLot.setText(parkinglotno);
        binding.addressTxt.setText(apartname);
        binding.datetimeTxt.setText(prefersch + "\n" + prefertime);

        if (Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) != 0) {
            binding.taxField.setVisibility(View.VISIBLE);
            binding.taxField1.setVisibility(View.VISIBLE);
            binding.taxPercentage.setText("GST (" + Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) + "%)");
            binding.taxPercentage1.setText("GST (" + Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) + "%)");
        } else {
            binding.taxField.setVisibility(View.GONE);
            binding.taxField1.setVisibility(View.GONE);
        }

        if (servicetype.equalsIgnoreCase(Constant.WASH)) {
            this.binding.subsCard.setVisibility(View.GONE);
            binding.serviceType.setText("Wash");
            binding.serviceType1.setText("Wash");
            onetimeService = "Wash";
            workPayment();
            Log.e("-----------------CAR ID---------->", onetimeService);
            Log.e("PlaceOrderOneTimeWash", token);
        } else if (this.servicetype.equalsIgnoreCase(Constant.BWASH)) {
            this.binding.subsCard.setVisibility(View.GONE);
            binding.serviceType.setText("Wash");
            binding.serviceType1.setText("Wash");
            this.onetimeService = "Wash";
            workPayment();
            Log.e("-----------------CAR ID---------->", onetimeService);
            Log.e("PlaceOrderOneTimeWash", token);
        } else if (servicetype.equalsIgnoreCase(Constant.ADDON)) {
            binding.serviceType.setText("Add On");
            binding.serviceType1.setText("Add On");
            this.onetimeService = "AddOn";
            workAddOn();
            this.binding.subsCard1.setVisibility(View.VISIBLE);
            this.binding.subsCard.setVisibility(View.GONE);
        } else if (servicetype.equalsIgnoreCase(Constant.EXTRAINT)) {
            this.binding.serviceType.setText("Add On");
            this.binding.subsCard1.setVisibility(View.GONE);
        } else if (servicetype.equalsIgnoreCase(Constant.DISINSFECTION)) {
            this.binding.serviceType.setText("Disinfection");
            this.binding.subsCard1.setVisibility(View.GONE);
        }
        if (carname.startsWith("extra") || carname.startsWith("Extra") || carname.startsWith("EXTRA")) {
            binding.subscriptionType.setText(Constant.ONETIME);
            binding.subsHead.setText("One Time Subscription");
            action = Constant.ACTIONONE;
            binding.extraLl.setVisibility(View.VISIBLE);
        } else if (servicetype.equalsIgnoreCase(Constant.DISINSFECTION)) {
            binding.subscriptionType.setText(Constant.ONETIME);
            binding.subsHead.setText("One Time Subscription");
            action = Constant.ACTIONONE;
            binding.extraLl.setVisibility(View.VISIBLE);
        } else if (this.servicetype.equalsIgnoreCase(Constant.WASH) || this.servicetype.equalsIgnoreCase(Constant.BWASH) || servicetype.equalsIgnoreCase(Constant.ADDON)) {

            if (servicetype.equalsIgnoreCase(Constant.ADDON)) {
                this.binding.extraLl.setVisibility(View.VISIBLE);
            } else {
                this.binding.extraLl.setVisibility(View.GONE);
            }

            this.binding.subsCard1.setVisibility(View.VISIBLE);

            binding.packageType1.setText(carcat);
            binding.packageMrp1.setText("₹ " + carprice);

            if (servicetype.equalsIgnoreCase(Constant.ADDON)) {
                this.binding.subsHead1.setText("One Time Subscription");
                binding.subscriptionType1.setVisibility(View.GONE);
                binding.subscriptionType01.setVisibility(View.VISIBLE);
                binding.subscriptionType01.setText(Constant.ONETIME);
                paidMonths = "1";
                totalAmountStr = Integer.parseInt(onetimecarprice);
                binding.total1.setText("₹ " + onetimecarprice);
                int taxAmt = ((Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) * Integer.parseInt(onetimecarprice)) / 100);
                int finalAmt = taxAmt + Integer.parseInt(onetimecarprice);
                binding.taxTotal1.setText("₹ " + taxAmt);
                binding.totalAmount1.setText("₹ " + finalAmt);
                //binding.totalAmount1.setText("₹ " + onetimecarprice);
            } else {
                this.binding.subsHead1.setText("One Time Wash Subscription");
            }


            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subsMonths);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            this.binding.subscriptionType1.setAdapter(arrayAdapter);
            this.binding.subscriptionType1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                    int position = i + 1;
                    double parseDouble = Double.parseDouble(onetimecarprice) * position;
                    totalAmountStr = (int) parseDouble;
                    paidMonths = String.valueOf(position);
                    binding.total1.setText("₹ " + totalAmountStr);
                    int taxAmt = ((Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) * totalAmountStr) / 100);
                    int finalAmt = taxAmt + totalAmountStr;
                    binding.taxTotal1.setText("₹ " + taxAmt);
                    binding.totalAmount1.setText("₹ " + finalAmt);
                    Log.e("AMOUNTRZP", String.valueOf(parseDouble));
                }
            });
            this.binding.subscriptionType.setText(Constant.MONTHLY);
            this.binding.subsHead.setText("Monthly Subscription");
        } else {
            binding.subsCard1.setVisibility(View.GONE);
            binding.subscriptionType.setText(Constant.MONTHLY);
            binding.subsHead.setText("Monthly Subscription");
            action = Constant.ACTIONMONTH;
            binding.extraLl.setVisibility(View.GONE);
        }
        binding.preferredtimeEdt.setText(Constant.ANYTIME);
        time = Constant.ANYTIME;

        binding.packageType.setText(carcat);
        binding.packageMrp.setText("₹ " + carprice);
        binding.total.setText("₹ " + carprice);
        int taxAmt = ((Constant.GST_PERCENTAGE * Integer.parseInt(carprice)) / 100);
        int finalAmt = taxAmt + Integer.parseInt(carprice);
        binding.taxTotal.setText("₹ " + taxAmt);
        binding.totalAmount.setText("₹ " + finalAmt);
        //binding.totalAmount.setText("₹ "+carprice);
        Picasso.get().
                load(carimage)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.carImg);
        Collections.addAll(spinner_item, preTime);

        binding.spinner.setAdapter(new ArrayAdapter<String>(PaymentOptionActivity.this, android.R.layout.simple_list_item_1, spinner_item));

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                binding.preferredtimeEdt.setText(spinner_item.get(i));
                time = spinner_item.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        binding.preferredtimeEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.preferDate.getText().toString())) {
                    Toast.makeText(PaymentOptionActivity.this, "Choose Preferred Schedule", Toast.LENGTH_SHORT).show();
                } else {
                    binding.spinner.performClick();
                }
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                picker = new DatePickerDialog(PaymentOptionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                binding.preferDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                //picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                picker.getDatePicker().setMinDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                picker.show();
            }
        });
//        workTime();

        binding.paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (carname.startsWith("extra") || carname.startsWith("Extra") || carname.startsWith("EXTRA")) {
                    action = Constant.ACTIONONE;
                } else {
                    action = Constant.ACTIONMONTH;
                }
                workmode();
            }
        });
        binding.paynow1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (servicetype.equalsIgnoreCase(Constant.ADDON)) {
                    action = Constant.ACTIONEXTRAONE;
                } else {
                    action = Constant.ACTIONWASHONE;
                }

                workmode();
            }
        });

        binding.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (servicetype.equalsIgnoreCase(Constant.ADDON)) {
                    if (binding.preferDate.getText().length() > 0 && !TextUtils.isEmpty(time)) {
                        checkIfExists();
                    } else {
                        Toast.makeText(PaymentOptionActivity.this, Constant.CHOOSEDATETIME, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    checkIfExists();
                }
            }
        });

        binding.addToCart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action.equalsIgnoreCase(Constant.ACTIONONE)) {
                    if (binding.preferDate.getText().length() > 0 && !TextUtils.isEmpty(time)) {
                        checkIfExists1();
                    } else {
                        Toast.makeText(PaymentOptionActivity.this, Constant.CHOOSEDATETIME, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    checkIfExists1();
                }
            }
        });

    }

    public void checkIfExists() {

        /*if (servicetype.equalsIgnoreCase(Constant.ADDON)) {
            action = Constant.ACTIONEXTRAONE;
        } else {
            action = Constant.ACTIONWASHONE;
        }*/


        String type = servicetype;

        if (type.equalsIgnoreCase(Constant.ADDON)) {
            action = Constant.ACTIONEXTRAONE;
        } else if (type.equalsIgnoreCase(Constant.EXTRAINT)) {
            action = Constant.ACTIONONE;
        } else if (type.equalsIgnoreCase(Constant.DISINSFECTION)) {
            action = Constant.ACTIONDISONE;
        } else {
            action = Constant.ACTIONWASHONE;
        }

        String tottal_amt = String.valueOf(totalAmountStr);

        int before_tax = Integer.parseInt(tottal_amt);
        int taxAmt = ((Constant.GST_PERCENTAGE * before_tax) / 100);
        int finalAmt = taxAmt + before_tax;

        String dBType = action +"="+ type;
        databaseHelper.CheckOrderExists(action, carid);
        databaseHelper.CheckOrderExists(dBType, carid);


        String result = databaseHelper.AddUpdateOrder(dBType + "",
                carimage + "",
                carmakemodel + "",
                carno + "",
                onetimecarprice + "",
                carid + "",
                paidMonths + "",
                fineAmount + "",
                tottal_amt + "",
                Constant.GST_PERCENTAGE + "",
                taxAmt + "",
                String.valueOf(finalAmt),
                binding.preferDate.getText().toString() + "",
                time + "");

        Log.e("123456", "" + result);

        if (result.equalsIgnoreCase("1")) {
            Toast.makeText(PaymentOptionActivity.this, "Added. ", Toast.LENGTH_SHORT).show();
            showCartCount();
            startActivity(new Intent(PaymentOptionActivity.this, CartActivity.class));
            finish();
        } else {
            Toast.makeText(PaymentOptionActivity.this, "Added Failed. ", Toast.LENGTH_SHORT).show();
        }

    }

    public void checkIfExists1() {

//        if (servicetype.equalsIgnoreCase(Constant.EXTRAINT)) {
//            action = Constant.ACTIONONE;
//        } else {
//            action = Constant.ACTIONDISONE;
//        }

        String type = servicetype;

        if (type.equalsIgnoreCase(Constant.ADDON)) {
            action = Constant.ACTIONEXTRAONE;
        } else if (type.equalsIgnoreCase(Constant.EXTRAINT)) {
            action = Constant.ACTIONONE;
        } else if (type.equalsIgnoreCase(Constant.DISINSFECTION)) {
            action = Constant.ACTIONDISONE;
        } else {
            action = Constant.ACTIONWASHONE;
        }

        int before_tax = Integer.parseInt(carprice);
        int taxAmt = ((Constant.GST_PERCENTAGE * before_tax) / 100);
        int finalAmt = taxAmt + before_tax;

        String dBType = action +"="+ type;
        databaseHelper.CheckOrderExists(action, carid);
        databaseHelper.CheckOrderExists(dBType, carid);

        String result = databaseHelper.AddUpdateOrder(dBType + "",
                carimage + "",
                carmakemodel + "",
                carno + "",
                carprice + "",
                carid + "",
                "1",
                "0",
                carprice + "",
                Constant.GST_PERCENTAGE + "",
                taxAmt + "",
                String.valueOf(finalAmt),
                binding.preferDate.getText().toString() + "",
                time + "");

        if (result.equalsIgnoreCase("1")) {
            Toast.makeText(PaymentOptionActivity.this, "Added. ", Toast.LENGTH_SHORT).show();
            showCartCount();
            startActivity(new Intent(PaymentOptionActivity.this, CartActivity.class));
            finish();
        } else {
            Toast.makeText(PaymentOptionActivity.this, "Added Failed. ", Toast.LENGTH_SHORT).show();
        }


    }

    public void workPayment() {
        if (isNetworkAvailable()) {
            checkOnetime();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle((CharSequence) "Alert!");
        builder.setMessage((CharSequence) "No internet.Please check your connection.");
        builder.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                workPayment();
            }
        }).setNegativeButton((CharSequence) "Cancel ", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
    }

    private void checkOnetime() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<OneTimeWashCheckout> call = apiInterface.onetime_wash(customerid, carprice, carid, onetimeService);
        call.enqueue(new Callback<OneTimeWashCheckout>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<OneTimeWashCheckout> call, Response<OneTimeWashCheckout> response) {
                try {
                    if (response.isSuccessful()) {
                        OneTimeWashCheckout body = response.body();
                        if (body.code.equalsIgnoreCase("200")) {
                            Log.e("Reponse---------------------", body.code);
                            result = body.result;
                            for (int i = 0; i < result.size(); i++) {
                                totalAmountStr = Integer.parseInt(result.get(i).total_amount);
                                if (result.get(i).order_exists.equalsIgnoreCase("0")) {
                                    fineAmount = result.get(i).fine_amount;

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
                                        binding.discountAmount1.setText("₹ " + result.get(i).discount_amount);
                                    } else {
                                        binding.discountField.setVisibility(View.VISIBLE);
                                        discountAmount = result.get(i).discount_amount;
                                        binding.discountAmount1.setText("₹ " + result.get(i).discount_amount);
                                    }

                                    if (result.get(i).total_amount.equalsIgnoreCase("0")) {
                                        onetimecarprice.equals(carprice);
                                        //Log.e("is 0","Price"+onetimecarprice);
                                    } else {
                                        onetimecarprice = result.get(i).total_amount;
                                        binding.total1.setText("₹ " + result.get(i).total_amount);
                                        int taxAmt = ((Constant.GST_PERCENTAGE * Integer.parseInt(result.get(i).total_amount)) / 100);
                                        int finalAmt = taxAmt + Integer.parseInt(result.get(i).total_amount);
                                        binding.taxTotal1.setText("₹ " + taxAmt);
                                        binding.totalAmount1.setText("₹ " + finalAmt);
                                        //Log.e("not 0","Price"+onetimecarprice);
                                    }

                                }

                            }
                        } else if (body.code.equalsIgnoreCase("201")) {
                            Toast.makeText(PaymentOptionActivity.this, body.staus, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(PaymentOptionActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("LongLogTag")
            public void onFailure(Call<OneTimeWashCheckout> call, Throwable th) {
                Toast.makeText(PaymentOptionActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void workAddOn() {
        if (isNetworkAvailable()) {
            checkOnetimeAddOn();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle((CharSequence) "Alert!");
        builder.setMessage((CharSequence) "No internet.Please check your connection.");
        builder.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                checkOnetimeAddOn();
            }
        }).setNegativeButton((CharSequence) "Cancel ", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
    }

    private void checkOnetimeAddOn() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<OneTimeWashCheckout> call = apiInterface.onetime_wash(customerid, carprice, carid, onetimeService);
        call.enqueue(new Callback<OneTimeWashCheckout>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<OneTimeWashCheckout> call, Response<OneTimeWashCheckout> response) {
                try {
                    if (response.isSuccessful()) {
                        OneTimeWashCheckout body = response.body();
                        if (body.code.equalsIgnoreCase("200")) {
                            Log.e("Reponse---------------------", body.code);
                            result = body.result;
                            for (int i = 0; i < result.size(); i++) {
                                totalAmountStr = Integer.parseInt(result.get(i).total_amount);
                                if (result.get(i).order_exists.equalsIgnoreCase("0")) {
                                    fineAmount = result.get(i).fine_amount;

                                    if (result.get(i).total_amount.equalsIgnoreCase("0")) {
                                        onetimecarprice.equals(carprice);
                                        //Log.e("is 0","Price"+onetimecarprice);
                                    } else {
                                        onetimecarprice = result.get(i).total_amount;
                                        binding.total1.setText("₹ " + result.get(i).total_amount);
                                        int taxAmt = ((Constant.GST_PERCENTAGE * Integer.parseInt(result.get(i).total_amount)) / 100);
                                        int finalAmt = taxAmt + Integer.parseInt(result.get(i).total_amount);
                                        binding.taxTotal1.setText("₹ " + taxAmt);
                                        binding.totalAmount1.setText("₹ " + finalAmt);
                                        //binding.totalAmount1.setText("₹ " +result.get(i).total_amount);
                                        //Log.e("not 0","Price"+onetimecarprice);
                                    }

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

                                    if (result.get(i).discount_amount.equalsIgnoreCase("0")) {
                                        discountAmount = result.get(i).discount_amount;
                                        binding.discountField.setVisibility(View.GONE);
                                        binding.discountAmount1.setText("₹ " + result.get(i).discount_amount);
                                    } else {
                                        binding.discountField.setVisibility(View.VISIBLE);
                                        discountAmount = result.get(i).discount_amount;
                                        binding.discountAmount1.setText("₹ " + result.get(i).discount_amount);
                                    }

                                    if (result.get(i).total_amount.equalsIgnoreCase("0")) {
                                        onetimecarprice.equals(carprice);
                                    } else {
                                        onetimecarprice = result.get(i).total_amount;
                                        binding.total1.setText("₹ " + result.get(i).total_amount);
                                        int taxAmt = ((Constant.GST_PERCENTAGE * Integer.parseInt(result.get(i).total_amount)) / 100);
                                        int finalAmt = taxAmt + Integer.parseInt(result.get(i).total_amount);
                                        binding.taxTotal1.setText("₹ " + taxAmt);
                                        binding.totalAmount1.setText("₹ " + finalAmt);
                                    }

                                }
                            }
                        } else if (body.code.equalsIgnoreCase("201")) {
                            Toast.makeText(PaymentOptionActivity.this, body.staus, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(PaymentOptionActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("LongLogTag")
            public void onFailure(Call<OneTimeWashCheckout> call, Throwable th) {
                Toast.makeText(PaymentOptionActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void workmode() {
        if (isNetworkAvailable()) {
            getPayMode();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(PaymentOptionActivity.this);
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
        final KProgressHUD hud = KProgressHUD.create(PaymentOptionActivity.this)
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

                            if (action.equalsIgnoreCase(Constant.ACTIONMONTH)) {
                                work();
//                    startPaymentMonth();
                            } else if (action.equalsIgnoreCase(Constant.ACTIONONE)) {
                                if (binding.preferDate.getText().length() > 0 && !TextUtils.isEmpty(time)) {
                                    startPayment();
                                } else {
                                    Toast.makeText(PaymentOptionActivity.this, Constant.CHOOSEDATETIME, Toast.LENGTH_SHORT).show();
                                }
                            } else if (action.equalsIgnoreCase(Constant.ACTIONWASHONE)) {
                                startwashonetimepayment();
                            } else if (action.equalsIgnoreCase(Constant.ACTIONEXTRAONE)) {
                                if (binding.preferDate.getText().length() > 0 && !TextUtils.isEmpty(time)) {
                                    startwashonetimepayment();
                                } else {
                                    Toast.makeText(PaymentOptionActivity.this, Constant.CHOOSEDATETIME, Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    } else {
                        ApiConfig.responseToast(PaymentOptionActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(PaymentOptionActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void work() {
        if (isNetworkAvailable()) {
            getOrdercheck();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(PaymentOptionActivity.this);
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

    private void getOrdercheck() {
        final KProgressHUD hud = KProgressHUD.create(PaymentOptionActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.checkValidation(customerid + "", carid + "", servicetype + "");
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
                            Intent intent = new Intent(PaymentOptionActivity.this, PaymentWebActivity.class);
                            intent.putExtra("package_type", carcat);
                            intent.putExtra("vehicle_type", cartype);
                            intent.putExtra("subscription_type", "Monthly");
                            intent.putExtra("service_type", binding.serviceType.getText().toString());
                            intent.putExtra("vehicle_id", carid);
                            intent.putExtra("customer_id", customerid);
                            intent.putExtra("amount", carprice);
                            startActivity(intent);
                            finish();
                        } else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                            Toast.makeText(PaymentOptionActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(PaymentOptionActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(PaymentOptionActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void workTime() {
//        final int pos = 0;
//            ArrayAdapter<String> adapter;
//                adapter = new ArrayAdapter<String>(PaymentOptionActivity.this, R.layout.spinner_item, preTime);
//                binding.preferTime.setAdapter(adapter);
//                binding.preferTime.setSelection(pos);
//            //setting adapter to spinner
//
//            binding.preferTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                         time = preTime[i];
//                        Log.e("preTime",time);
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> adapterView) {
//
//                }
//            });
//    }

//        private void workgetPlan() {
//        if (isNetworkAvailable()){
//            getPlan();
//        }else {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(PaymentOptionActivity.this);
//            dialog.setCancelable(false);
//            dialog.setTitle("Alert!");
//            dialog.setMessage("No internet.Please check your connection." );
//            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int id) {
//                    //Action for "Ok".
//                    workgetPlan();
//                }
//            })
//                    .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //Action for "Cancel".
//                            finish();
//                        }
//                    });
//
//            final AlertDialog alert = dialog.create();
//            alert.show();
//        }
//
//    }


//
//    private void workSubscription() {
//        if (isNetworkAvailable()){
//            getSubscription();
//        }else {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(PaymentOptionActivity.this);
//            dialog.setCancelable(false);
//            dialog.setTitle("Alert!");
//            dialog.setMessage("No internet.Please check your connection." );
//            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int id) {
//                    //Action for "Ok".
//                    workSubscription();
//                }
//            })
//                    .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //Action for "Cancel".
//                            finish();
//                        }
//                    });
//
//            final AlertDialog alert = dialog.create();
//            alert.show();
//        }
//    }
//
//    private void getSubscription() {
//        final KProgressHUD hud = KProgressHUD.create(PaymentOptionActivity.this)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setCancellable(true)
//                .setAnimationSpeed(2)
//                .setDimAmount(0.5f)
//                .show();
//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<JsonObject> call = apiInterface.getSubcription(planId+"",customerid+"",carid+"",token+"","60");
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                JsonElement jsonElement = response.body();
//                hud.dismiss();
//                try {
//                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
//                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
//                        Gson gson = new Gson();
//                        planId = jsonObject.optString("plan_id");
//                        subscriptionId = jsonObject.optString("subscription_id");
//                        Constant.RAZOR_PAY_KEY_VALUE = jsonObject.optString("razorpay_keyid");
//                        custname = jsonObject.optString("customer_name");
//                        custmob = jsonObject.optString("customer_mobile");
//                        custemail = jsonObject.optString("customer_email");
//                        if (action.equalsIgnoreCase(Constant.ACTIONONE)){
//                            startPayment("1");
//                        }else if (action.equalsIgnoreCase(Constant.ACTIONMONTH)){
//                            startPayment("0");
//                        }
//                    }else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
//                        Toast.makeText(PaymentOptionActivity.this,jsonObject.optString("message"),Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                hud.dismiss();
//                Toast.makeText(PaymentOptionActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID(Constant.RAZOR_PAY_KEY_VALUE);
//        checkout.setKeyID("rzp_test_tcKV4hRzb6g7Z5");
        checkout.setImage(R.drawable.logo5234);

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Carro Care");
            options.put("description", binding.serviceType.getText().toString());
            options.put("currency", "INR");
//            if (s.equalsIgnoreCase("0")){
//                options.put("subscription_id", subscriptionId);
//            }
            double amount = Double.parseDouble(carprice);
            amount = amount * 100;
            Log.e("AMOUNTRZP", String.valueOf(amount));
            options.put("amount", amount);

            JSONObject preFill = new JSONObject();
            preFill.put("email", custemail);
            preFill.put("contact", custmob);
            options.put("prefill", preFill);
            checkout.open(PaymentOptionActivity.this, options);
            Log.e("OPTIONS", options.toString());
        } catch (Exception e) {
            Log.d("PaymentOptionActivity", "Error in starting Razorpay Checkout", e);
        }
    }

    public void startwashonetimepayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID(Constant.RAZOR_PAY_KEY_VALUE);
        checkout.setImage(R.drawable.logo5234);
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("name", "Carro Care");
            jSONObject.put("description", this.binding.serviceType1.getText().toString());
            jSONObject.put("currency", "INR");
            int i = this.totalAmountStr * 100;
            Log.e("AMOUNTRZP", String.valueOf(i));
            jSONObject.put("amount", i);
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("email", this.custemail);
            jSONObject2.put("contact", this.custmob);
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

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            razorpayid = razorpayPaymentID;
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
        if (isNetworkAvailable()) {
            if (action.equalsIgnoreCase(Constant.ACTIONONE)) {
                PlaceOrderOneTime(razorpayid);
            } else if (action.equalsIgnoreCase(Constant.ACTIONMONTH)) {
                PlaceOrder(razorpayid);
            } else if (action.equalsIgnoreCase(Constant.ACTIONWASHONE)) {
                PlaceOrderOneTimeWash(razorpayid);
            } else if (action.equalsIgnoreCase(Constant.ACTIONEXTRAONE)) {
                PlaceOrderOneTimeAddOn(razorpayid);
            }

        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(PaymentOptionActivity.this);
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

    private void PlaceOrderOneTime(String razorpayid) {
        final KProgressHUD hud = KProgressHUD.create(PaymentOptionActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.saveOrderOneTime(action + "",
                razorpayid + "",
                "",
                customerid + "",
                token + "",
                "ExtraInterior",
                carprice + "",
                carid + "",
                "AddOn",
                carprice + "",
                Constant.GST_PERCENTAGE + "",
                "0",
                carprice + "",
                binding.preferDate.getText().toString(), time);
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
                            Toast.makeText(PaymentOptionActivity.this, jsonObject.optString("result"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PaymentOptionActivity.this, CongratsActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                            Toast.makeText(PaymentOptionActivity.this, jsonObject.optString("result"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(PaymentOptionActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(PaymentOptionActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void PlaceOrderOneTimeWash(String razorpayid) {
        final KProgressHUD hud = KProgressHUD.create(PaymentOptionActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.saveWashOrderOneTime("onetime_wash_payment",
                razorpayid + "",
                "",
                customerid + "",
                token + "",
                carprice + "",
                carid + "",
                paidMonths + "",
                fineAmount + "",
                carprice + "",
                Constant.GST_PERCENTAGE + "",
                "0",
                totalAmountStr + "",
                "Wash","");
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
                            Toast.makeText(PaymentOptionActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PaymentOptionActivity.this, CongratsActivity.class));
                            finish();
                        } else if (jSONObject.optString("code").equalsIgnoreCase("201")) {
                            Toast.makeText(PaymentOptionActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                        } else if (jSONObject.optString("message").equalsIgnoreCase("success")) {
                            Toast.makeText(PaymentOptionActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PaymentOptionActivity.this, CongratsActivity.class));
                            finish();
                        }
                    } else {
                        ApiConfig.responseToast(PaymentOptionActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(Call<JsonObject> call, Throwable th) {
                hud.dismiss();
                Toast.makeText(PaymentOptionActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PlaceOrderOneTimeAddOn(String razorpayid) {
        final KProgressHUD hud = KProgressHUD.create(PaymentOptionActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.saveAddOnOrderOneTime("onetime_wash_payment",
                razorpayid + "",
                "",
                customerid + "",
                token + "",
                carprice + "",
                carid + "",
                paidMonths + "",
                fineAmount + "",
                carprice + "",
                Constant.GST_PERCENTAGE + "",
                "0",
                totalAmountStr + "",
                "AddOn","",
                binding.preferDate.getText().toString(),
                time);
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
                            Toast.makeText(PaymentOptionActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PaymentOptionActivity.this, CongratsActivity.class));
                            finish();
                        } else if (jSONObject.optString("code").equalsIgnoreCase("201")) {
                            Toast.makeText(PaymentOptionActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                        } else if (jSONObject.optString("message").equalsIgnoreCase("success")) {
                            Toast.makeText(PaymentOptionActivity.this, jSONObject.optString("result"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PaymentOptionActivity.this, CongratsActivity.class));
                            finish();
                        }
                    } else {
                        ApiConfig.responseToast(PaymentOptionActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(Call<JsonObject> call, Throwable th) {
                hud.dismiss();
                Toast.makeText(PaymentOptionActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void PlaceOrder(String razorpayid) {
        final KProgressHUD hud = KProgressHUD.create(PaymentOptionActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.saveOrder(action + "", razorpayid + "", customerid + "", token + "", carid + "", binding.serviceType.getText().toString() + "",
                carprice + "", planId + "", subscriptionId + "");
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
                            Toast.makeText(PaymentOptionActivity.this, jsonObject.optString("result"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PaymentOptionActivity.this, CongratsActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                            Toast.makeText(PaymentOptionActivity.this, jsonObject.optString("result"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(PaymentOptionActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(PaymentOptionActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("LongLogTag")
    public void showCartCount() {
        //int totalItemOfCart = dbAdapter.cart_count;
        int totalItemOfCart = databaseHelper.getTotalItemOfCart();

        binding.cartCount.setText(String.valueOf(totalItemOfCart));

//        if(totalItemOfCart == 0){
//            binding.cartCount.setVisibility(View.INVISIBLE);
//        }else{
//           binding.cartCount.setText(String.valueOf(totalItemOfCart));
//        }

        Log.e("Total item of cart--->   ", "" + totalItemOfCart);
    }


}