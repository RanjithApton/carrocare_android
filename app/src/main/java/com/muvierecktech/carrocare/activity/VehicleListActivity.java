package com.muvierecktech.carrocare.activity;

import static com.muvierecktech.carrocare.common.Constant.OFFER_PRICE_50;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.PreferredAdapter;
import com.muvierecktech.carrocare.adapter.VehicleListAdapter;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.Constant;
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
    public static String time;
    public ActivityVehicleListBinding binding;
    String carname, carprice, cardesc, carimage, carid, header, token, customerid, servicetype,displayPrice;
    SessionManager sessionManager;
    MyDatabaseHelper databaseHelper;
    String action, onetimeService, paidMonths, fineAmount, discountAmount, onetimecarprice;
    List<OneTimeWashCheckout.getResult> result;
    int totalAmountStr;
    String[] subsMonths = {"1 Month", "2 Months", "3 Months", "4 Months", "5 Months", "6 Months", "7 Months", "8 Months", "9 Months", "10 Months", "11 Months", "12 Months", "13 Months", "14 Months", "15 Months", "16 Months", "17 Months", "18 Months", "19 Months", "20 Months", "21 Months", "22 Months", "23 Months", "24 Months"};
    String preTime[] = {Constant.ANYTIME, "9:00 AM - 10:00 AM", "10:00 AM - 11:00 AM", "6:00 PM - 7:00 PM", "7:00 PM - 8:00 PM"};
    DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vehicle_list);
        sessionManager = new SessionManager(this);
        HashMap<String, String> hashMap = sessionManager.getUserDetails();
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);

        databaseHelper = new MyDatabaseHelper(this);

        showCartCount();

        binding.checkCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VehicleListActivity.this, CartActivity.class));
            }
        });

        Intent intent = getIntent();
        carname = intent.getStringExtra("carname");
        carprice = intent.getStringExtra("carprice");
        cardesc = intent.getStringExtra("cardesc");
        carimage = intent.getStringExtra("carimage");
        carid = intent.getStringExtra("carid");
        header = intent.getStringExtra("header");
        displayPrice = intent.getStringExtra("displayprice");
        binding.carDetailsHeading.setText(header + " Details");
        binding.headerName.setText(carname);
        if (carname.startsWith("extra") || carname.startsWith("Extra") || carname.startsWith("EXTRA")) {
            carname = "";
            binding.bottomLl.setVisibility(View.GONE);
        }

//        binding.carDetailsHeading.setPaintFlags(binding.carDetailsHeading.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.carDetails.setText(HtmlCompat.fromHtml(cardesc, 0) + "");
        binding.subPriceHeading.setText("Subscription Price");
//        binding.subPriceHeading.setPaintFlags(binding.subPriceHeading.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.subPrice.setText("₹ " + displayPrice);
        binding.selectVehicle.setText("Select Vehicle");
//        binding.selectVehicle.setPaintFlags(binding.selectVehicle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        work();

        binding.addvehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VehicleListActivity.this, AddVehicleActivity.class);
                intent.putExtra("carname", carname);
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
                startActivity(new Intent(VehicleListActivity.this, CartActivity.class));
            }
        });

        onetimecarprice = "0";

    }

    private void work() {
        if (isNetworkAvailable()) {

            if(header.equalsIgnoreCase(Constant.EXTRAINT)){
                VehicleDeatilsExtra();
                binding.bottomLl.setVisibility(View.GONE);
            } else {
                VehicleDeatils();
                binding.bottomLl.setVisibility(View.VISIBLE);
            }

        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(VehicleListActivity.this);
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

    private void VehicleDeatils() {
        final KProgressHUD hud = KProgressHUD.create(VehicleListActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<VehicleDetails> call = apiInterface.vehicledetails(customerid, token, carname);

        call.enqueue(new Callback<VehicleDetails>() {
            @Override
            public void onResponse(Call<VehicleDetails> call, Response<VehicleDetails> response) {
                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        final VehicleDetails vehicleDetails = response.body();
                        if (vehicleDetails.code.equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            String json = gson.toJson(vehicleDetails);
                            binding.novehicle.setVisibility(View.GONE);
                            binding.carslistRc.setVisibility(View.VISIBLE);

                            VehicleListAdapter mainAdapter = new VehicleListAdapter(VehicleListActivity.this, vehicleDetails.details, carprice, carname, header);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(VehicleListActivity.this, LinearLayoutManager.VERTICAL, false);
                            binding.carslistRc.setLayoutManager(linearLayoutManager);
                            binding.carslistRc.setAdapter(mainAdapter);
                        } else if (vehicleDetails.code.equalsIgnoreCase("201")) {
                            binding.novehicle.setVisibility(View.VISIBLE);
                            binding.carslistRc.setVisibility(View.GONE);
                        } else if (vehicleDetails.code.equalsIgnoreCase("203")) {
                            sessionManager.logoutUsers();
                        }
                    } else {
                        ApiConfig.responseToast(VehicleListActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<VehicleDetails> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(VehicleListActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void VehicleDeatilsExtra() {
        final KProgressHUD hud = KProgressHUD.create(VehicleListActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<VehicleDetails> call = apiInterface.vehicledetailsExtra(customerid, token, carname,"");

        call.enqueue(new Callback<VehicleDetails>() {
            @Override
            public void onResponse(Call<VehicleDetails> call, Response<VehicleDetails> response) {
                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        final VehicleDetails vehicleDetails = response.body();
                        if (vehicleDetails.code.equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            String json = gson.toJson(vehicleDetails);
                            binding.novehicle.setVisibility(View.GONE);
                            binding.carslistRc.setVisibility(View.VISIBLE);

                            VehicleListAdapter mainAdapter = new VehicleListAdapter(VehicleListActivity.this, vehicleDetails.details, carprice, carname, header);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(VehicleListActivity.this, LinearLayoutManager.VERTICAL, false);
                            binding.carslistRc.setLayoutManager(linearLayoutManager);
                            binding.carslistRc.setAdapter(mainAdapter);
                        } else if (vehicleDetails.code.equalsIgnoreCase("201")) {
                            binding.novehicle.setVisibility(View.VISIBLE);
                            binding.carslistRc.setVisibility(View.GONE);
                        } else if (vehicleDetails.code.equalsIgnoreCase("203")) {
                            sessionManager.logoutUsers();
                        }
                    } else {
                        ApiConfig.responseToast(VehicleListActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<VehicleDetails> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(VehicleListActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
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
    public void showCartCount() {
        //int totalItemOfCart = dbAdapter.cart_count;
        int totalItemOfCart = databaseHelper.getTotalItemOfCart();

        binding.cartCount.setText(String.valueOf(totalItemOfCart));
    }

    public void showCheckoutPopupWash(String type, String action, VehicleDetails.VecDetails vecDetails, String price) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<OneTimeWashCheckout> call = apiInterface.onetime_wash(
                customerid+"",
                price+"",
                vecDetails.vehicle_id+"",
                "Wash"
        );
        call.enqueue(new Callback<OneTimeWashCheckout>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(Call<OneTimeWashCheckout> call, Response<OneTimeWashCheckout> response) {
                try {
                    if (response.isSuccessful()) {
                        OneTimeWashCheckout body = response.body();
                        if (body.code.equalsIgnoreCase("200")) {

                            result = body.result;


                            android.app.AlertDialog.Builder dialogbuilder = new android.app.AlertDialog.Builder(VehicleListActivity.this);
                            View dialogView = LayoutInflater.from(VehicleListActivity.this).inflate(R.layout.dialog_add_to_smart_checkout_wash, null);

                            ImageView imgClose = dialogView.findViewById(R.id.popup_cancel);
                            TextView service_type = dialogView.findViewById(R.id.service_type);
                            TextView package_type = dialogView.findViewById(R.id.package_type);
                            TextView package_mrp = dialogView.findViewById(R.id.package_mrp);
                            LinearLayout discount_field = dialogView.findViewById(R.id.discount_field);
                            TextView discount_amount = dialogView.findViewById(R.id.discount_amount);
                            LinearLayout fine_field = dialogView.findViewById(R.id.fine_field);
                            TextView fine_amount = dialogView.findViewById(R.id.fine_amount);
                            AppCompatSpinner month_spinner = dialogView.findViewById(R.id.month_spinner);
                            TextView subscription_type = dialogView.findViewById(R.id.subscription_type);
                            TextView total = dialogView.findViewById(R.id.total);
                            LinearLayout tax_field = dialogView.findViewById(R.id.tax_field);
                            TextView tax_percentage = dialogView.findViewById(R.id.tax_percentage);
                            TextView tax_total = dialogView.findViewById(R.id.tax_total);
                            TextView tax_deal = dialogView.findViewById(R.id.tax_deal);
                            TextView total_amount = dialogView.findViewById(R.id.total_amount);
                            Button add_toCart = dialogView.findViewById(R.id.add_toCart);

                            dialogbuilder.setView(dialogView);
                            android.app.AlertDialog dialog = dialogbuilder.create();
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                            imgClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            service_type.setText("Wash");

                            if(type.equalsIgnoreCase(Constant.WASH)) {
                                package_type.setText("Car Wash");
                            } else if(type.equalsIgnoreCase(Constant.BWASH)){
                                package_type.setText("Bike Wash");
                            }

                            package_mrp.setText("₹ " + price);

                            if (Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) != 0) {
                                tax_field.setVisibility(View.VISIBLE);
//                                tax_percentage.setText("GST (" + Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) + "%)");
                                tax_percentage.setText("Offer Price");
                            } else {
                                tax_field.setVisibility(View.GONE);
                            }

                            if (result.get(0).order_exists.equalsIgnoreCase("0")) {
                                fineAmount = result.get(0).fine_amount;
                                onetimecarprice = result.get(0).total_amount;
                                paidMonths = "1";
                                totalAmountStr = Integer.parseInt(result.get(0).total_amount);
                                int taxAmt = ((Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) * Integer.parseInt(onetimecarprice)) / 100);
                                int finalAmt = taxAmt + Integer.parseInt(onetimecarprice);
                                int offerAmount =
                                        taxAmt + Integer.parseInt(onetimecarprice) + OFFER_PRICE_50;
                                tax_deal.setText(
                                        Html.fromHtml( "<sup><small><small>MRP</small></small" +
                                                "></sup>"+"<b>"+ "<strike>"+"₹ " + offerAmount
                                                + "</strike></b>" ) );
                                tax_total.setText(
                                        Html.fromHtml( "<sup><small><small>DEAL</small></small>" +
                                                "<b>"+ "₹ "+finalAmt +"</b>"  ) );
                                total.setText("₹ " + finalAmt);
                                total_amount.setText("₹ " + finalAmt);
                                package_mrp.setText("₹ " + offerAmount);

                                //showing Discount filed
                                if (result.get(0).discount_amount.equalsIgnoreCase("0")) {
                                    discountAmount = result.get(0).discount_amount;
                                    discount_field.setVisibility(View.GONE);
                                    discount_amount.setText(result.get(0).discount_amount);
                                } else {
                                    discount_field.setVisibility(View.VISIBLE);
                                    discountAmount = result.get(0).discount_amount;
                                    discount_amount.setText(result.get(0).discount_amount);
                                }

                                if (result.get(0).total_amount.equalsIgnoreCase("0")) {

                                } else {
                                    onetimecarprice = result.get(0).total_amount;
                                    int taxAmt1 = ((Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) * Integer.parseInt(result.get(0).total_amount)) / 100);
                                    int finalAmt1 = taxAmt1 + Integer.parseInt(result.get(0).total_amount);
                                    int offerAmount1 =
                                            taxAmt1 + Integer.parseInt(result.get(0).total_amount) + OFFER_PRICE_50;
                                    tax_deal.setText(
                                            Html.fromHtml( "<sup><small><small>MRP</small></small></sup>"+"<b>"+
                                                    "<strike>"+"₹ " + offerAmount1
                                                    + "</strike></b>" ) );
                                    tax_total.setText(
                                            Html.fromHtml("<sup><small><small>DEAL</small></small>" +"<b>"+ "₹ "+finalAmt1 +"</b>"  ) );
                                    total.setText("₹ " + finalAmt1);
                                    total_amount.setText("₹ " + finalAmt1);
                                    package_mrp.setText("₹ " + offerAmount1);
                                }

                            } else {
                                fine_field.setVisibility(View.VISIBLE);
                                if (result.get(0).fine_amount.equalsIgnoreCase("0")) {
                                    fineAmount = result.get(0).fine_amount;
                                    fine_field.setVisibility(View.GONE);
                                    fine_amount.setText(result.get(0).fine_amount);
                                } else {
                                    fine_field.setVisibility(View.VISIBLE);
                                    fineAmount = result.get(0).fine_amount;
                                    fine_amount.setText(result.get(0).fine_amount);
                                }

                                //showing Discount filed
                                if (result.get(0).discount_amount.equalsIgnoreCase("0")) {
                                    discountAmount = result.get(0).discount_amount;
                                    discount_field.setVisibility(View.GONE);
                                    discount_amount.setText("₹ " + result.get(0).discount_amount);
                                } else {
                                    discount_field.setVisibility(View.VISIBLE);
                                    discountAmount = result.get(0).discount_amount;
                                    discount_amount.setText("₹ " + result.get(0).discount_amount);
                                }

                                if (result.get(0).total_amount.equalsIgnoreCase("0")) {

                                } else {
                                    onetimecarprice = result.get(0).total_amount;
                                    int taxAmt2 = ((Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) * Integer.parseInt(result.get(0).total_amount)) / 100);
                                    int finalAmt2 = taxAmt2 + Integer.parseInt(result.get(0).total_amount);
                                    int offerAmount2 =
                                            taxAmt2 + Integer.parseInt(result.get(0).total_amount) + OFFER_PRICE_50;
                                    tax_deal.setText(
                                            Html.fromHtml( "<sup><small><small>MRP</small></small></sup>"+"<b>"+
                                                    "<strike>"+"₹ " + offerAmount2
                                                    + "</strike></b>" ) );
                                    tax_total.setText(
                                            Html.fromHtml("<sup><small><small>DEAL</small></small>" +"<b>"+ "₹ "+finalAmt2 +"</b>"  ) );
                                    total.setText("₹ " + finalAmt2);
                                    total_amount.setText("₹ " + finalAmt2);
                                    package_mrp.setText("₹ " + offerAmount2);
                                }
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(VehicleListActivity.this, android.R.layout.simple_spinner_dropdown_item, subsMonths);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            month_spinner.setAdapter(arrayAdapter);

                            month_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    int position = i + 1;
                                    double parseDouble = Double.parseDouble(onetimecarprice) * position;
                                    totalAmountStr = (int) parseDouble;
                                    paidMonths = String.valueOf(position);
                                    int taxAmt = ((Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) * totalAmountStr) / 100);
                                    int finalAmt = taxAmt + totalAmountStr;
                                    int offerAmount =
                                            taxAmt + totalAmountStr + (position * OFFER_PRICE_50);
                                    tax_deal.setText(
                                            Html.fromHtml( "<sup><small><small>MRP</small></small></sup>"+"<b>"+ "<strike>"+"₹ " + offerAmount
                                                    + "</strike></b>" ) );
                                    tax_total.setText(
                                            Html.fromHtml( "<sup><small><small>DEAL</small></small>" +"<b>"+ "₹ "+finalAmt +"</b>"  ) );
                                    total.setText("₹ " + finalAmt);
                                    total_amount.setText("₹ " + finalAmt);
                                    package_mrp.setText("₹ " + offerAmount);
                                    Log.e("AMOUNTRZP", String.valueOf(parseDouble));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });

                            add_toCart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String dBType = action +"="+ type;
                                    databaseHelper.CheckOrderExists(action, vecDetails.vehicle_id);
                                    databaseHelper.CheckOrderExists(dBType, vecDetails.vehicle_id);

                                    String tottal_amt = String.valueOf(totalAmountStr);
                                    int before_tax = Integer.parseInt(tottal_amt);
                                    int taxAmt = ((Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) * before_tax) / 100);
                                    int finalAmt = taxAmt + before_tax;

                                    String result = databaseHelper.AddUpdateOrder(dBType + "",
                                            vecDetails.vehicle_image,
                                            vecDetails.vehicle_make + "-" + vecDetails.vehicle_model + "",
                                            vecDetails.vehicle_no + "",
                                            onetimecarprice + "",
                                            vecDetails.vehicle_id + "",
                                            paidMonths + "",
                                            fineAmount + "",
                                            tottal_amt + "",
                                            Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) + "",
                                            taxAmt + "",
                                            String.valueOf(finalAmt),
                                            binding.preferDate.getText().toString() + "",
                                            time + "");

                                    if (result.equalsIgnoreCase("1")) {
                                        Toast.makeText(VehicleListActivity.this, "Added. ", Toast.LENGTH_SHORT).show();
                                        showCartCount();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(VehicleListActivity.this, "Added Failed. ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            dialog.show();

                        } else if (body.code.equalsIgnoreCase("201")) {
                            Toast.makeText(VehicleListActivity.this, body.staus, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(VehicleListActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("LongLogTag")
            public void onFailure(Call<OneTimeWashCheckout> call, Throwable th) {
                Toast.makeText(VehicleListActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showCheckoutPopupAddon(String type, String action, VehicleDetails.VecDetails vecDetails, String price) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<OneTimeWashCheckout> call = apiInterface.onetime_wash(
                customerid+"",
                price+"",
                vecDetails.vehicle_id+"",
                "AddOn"
        );
        call.enqueue(new Callback<OneTimeWashCheckout>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(Call<OneTimeWashCheckout> call, Response<OneTimeWashCheckout> response) {
                try {
                    if (response.isSuccessful()) {
                        OneTimeWashCheckout body = response.body();
                        if (body.code.equalsIgnoreCase("200")) {

                            result = body.result;


                            android.app.AlertDialog.Builder dialogbuilder = new android.app.AlertDialog.Builder(VehicleListActivity.this);
                            View dialogView = LayoutInflater.from(VehicleListActivity.this).inflate(R.layout.dialog_add_to_smart_checkout_addon, null);

                            ImageView imgClose = dialogView.findViewById(R.id.popup_cancel);
                            TextView prefer_date = dialogView.findViewById(R.id.prefer_date);
                            TextView preferredtime_edt = dialogView.findViewById(R.id.preferredtime_edt);
                            AppCompatSpinner time_spinner = dialogView.findViewById(R.id.time_spinner);
                            TextView service_type = dialogView.findViewById(R.id.service_type);
                            TextView package_type = dialogView.findViewById(R.id.package_type);
                            TextView package_mrp = dialogView.findViewById(R.id.package_mrp);
                            TextView subscription_type = dialogView.findViewById(R.id.subscription_type);
                            TextView total = dialogView.findViewById(R.id.total);
                            LinearLayout tax_field = dialogView.findViewById(R.id.tax_field);
                            TextView tax_percentage = dialogView.findViewById(R.id.tax_percentage);
                            TextView tax_total = dialogView.findViewById(R.id.tax_total);
                            TextView tax_deal = dialogView.findViewById(R.id.tax_deal);
                            TextView total_amount = dialogView.findViewById(R.id.total_amount);
                            Button add_toCart = dialogView.findViewById(R.id.add_toCart);

                            dialogbuilder.setView(dialogView);
                            android.app.AlertDialog dialog = dialogbuilder.create();
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                            imgClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            service_type.setText("Addon");

                            if(type.equalsIgnoreCase(Constant.ADDON)) {
                                package_type.setText("Wax Polish");
                            } else if(type.equalsIgnoreCase(Constant.EXTRAINT)){
                                package_type.setText("ExtraInterior");
                            } else if(type.equalsIgnoreCase(Constant.DISINSFECTION)){
                                package_type.setText("Disinfection");
                            }

//                            package_mrp.setText("₹ " + price);

                            if (Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) != 0) {
                                tax_field.setVisibility(View.VISIBLE);
//                                tax_percentage.setText("GST (" + Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) + "%)");
                                tax_percentage.setText("Offer Price");
                            } else {
                                tax_field.setVisibility(View.GONE);
                            }

                            fineAmount = result.get(0).fine_amount;
                            onetimecarprice = result.get(0).total_amount;
                            paidMonths = "1";
                            totalAmountStr = Integer.parseInt(result.get(0).total_amount);
                            int taxAmt = ((Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) * Integer.parseInt(onetimecarprice)) / 100);
                            int finalAmt = taxAmt + Integer.parseInt(onetimecarprice);
                            int offerAmount = taxAmt + Integer.parseInt(onetimecarprice) + OFFER_PRICE_50;
                            tax_deal.setText(
                                    Html.fromHtml( "<sup><small><small>MRP</small></small></sup>"+"<b>"+ "<strike>"+"₹ " + offerAmount
                                            + "</strike></b>" ) );
                            tax_total.setText(
                                    Html.fromHtml("<sup><small><small>DEAL</small></small>" +"<b>"+ "₹ "+finalAmt +"</b>" ) );
                            total.setText("₹ " + finalAmt);
                            total_amount.setText("₹ " + finalAmt);
                            package_mrp.setText("₹ " + offerAmount);

                            if (result.get(0).total_amount.equalsIgnoreCase("0")) {

                            } else {
                                onetimecarprice = result.get(0).total_amount;
                                int taxAmt1 = ((Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) * Integer.parseInt(result.get(0).total_amount)) / 100);
                                int finalAmt1 = taxAmt1 + Integer.parseInt(result.get(0).total_amount);
                                int offerAmount1 =
                                        taxAmt1 + Integer.parseInt(result.get(0).total_amount) + OFFER_PRICE_50;
                                tax_deal.setText(
                                        Html.fromHtml( "<sup><small><small>MRP</small></small></sup>"+"<b>"+ "<strike>"+
                                                "₹ " + offerAmount1
                                                + "</strike></b>" ) );
                                tax_total.setText(
                                        Html.fromHtml( "<sup><small><small>DEAL</small></small>" +"<b>"+ "₹ "+finalAmt1 +"</b></font>"  ) );
                                total.setText("₹ " + finalAmt1);
                                total_amount.setText("₹ " + finalAmt1);
                                package_mrp.setText("₹ " + offerAmount1);
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(VehicleListActivity.this, android.R.layout.simple_spinner_dropdown_item, preTime);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            time_spinner.setAdapter(arrayAdapter);

                            preferredtime_edt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (TextUtils.isEmpty(prefer_date.getText().toString())) {
                                        Toast.makeText(VehicleListActivity.this, "Choose Preferred Schedule", Toast.LENGTH_SHORT).show();
                                    } else {
                                        time_spinner.performClick();
                                    }
                                }
                            });

                            prefer_date.setOnClickListener(new View.OnClickListener() {
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
                                                    prefer_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                                }
                                            }, year, month, day);
                                    picker.getDatePicker().setMinDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                                    picker.show();
                                }
                            });

                            time_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    preferredtime_edt.setText(time_spinner.getSelectedItem().toString());
                                    time = time_spinner.getSelectedItem().toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });

                            add_toCart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (prefer_date.getText().length() > 0 && !TextUtils.isEmpty(time)) {
                                        String dBType = action + "=" + type;
                                        databaseHelper.CheckOrderExists(action, vecDetails.vehicle_id);
                                        databaseHelper.CheckOrderExists(dBType, vecDetails.vehicle_id);

                                        String tottal_amt = String.valueOf(totalAmountStr);
                                        int before_tax = Integer.parseInt(tottal_amt);
                                        int taxAmt = ((Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) * before_tax) / 100);
                                        int finalAmt = taxAmt + before_tax;

                                        String result = databaseHelper.AddUpdateOrder(dBType + "",
                                                vecDetails.vehicle_image,
                                                vecDetails.vehicle_make + "-" + vecDetails.vehicle_model + "",
                                                vecDetails.vehicle_no + "",
                                                onetimecarprice + "",
                                                vecDetails.vehicle_id + "",
                                                "1" + "",
                                                "0" + "",
                                                tottal_amt + "",
                                                Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) + "",
                                                taxAmt + "",
                                                String.valueOf(finalAmt),
                                                prefer_date.getText().toString() + "",
                                                time + "");

                                        if (result.equalsIgnoreCase("1")) {
                                            Toast.makeText(VehicleListActivity.this, "Added. ", Toast.LENGTH_SHORT).show();
                                            showCartCount();
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(VehicleListActivity.this, "Added Failed. ", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(VehicleListActivity.this, Constant.CHOOSEDATETIME, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            dialog.show();

                        } else if (body.code.equalsIgnoreCase("201")) {
                            Toast.makeText(VehicleListActivity.this, body.staus, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(VehicleListActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("LongLogTag")
            public void onFailure(Call<OneTimeWashCheckout> call, Throwable th) {
                Toast.makeText(VehicleListActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }
}