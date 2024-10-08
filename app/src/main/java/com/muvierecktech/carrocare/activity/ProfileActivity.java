package com.muvierecktech.carrocare.activity;

import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.ApartmentsAdapter;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityProfileBinding;
import com.muvierecktech.carrocare.model.ApartmentList;
import com.muvierecktech.carrocare.model.LoginDetails;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    public ActivityProfileBinding binding;
    SessionManager sessionManager;
    List<ApartmentList.Apartment> apartments;
    ArrayList<String> apartmentname;
    String token, customerid, apartname;
    String type, latitude, longitude;
    ArrayList<String> spinner_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        sessionManager = new SessionManager(this);
        HashMap<String, String> hashMap = sessionManager.getUserDetails();
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);

        spinner_item = new ArrayList<>();

        type = sessionManager.getData(SessionManager.USER_WANTS);

        if (type.equalsIgnoreCase("apartment")) {
            binding.apartmentFields.setVisibility(View.VISIBLE);
            binding.addressEdt.setVisibility(View.GONE);
        } else if (type.equalsIgnoreCase("doorstep")) {
            binding.addressEdt.setVisibility(View.VISIBLE);
            binding.apartmentFields.setVisibility(View.GONE);
        } else {
            binding.apartmentFields.setVisibility(View.VISIBLE);
            binding.addressEdt.setVisibility(View.GONE);
        }

        binding.addressEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_loc = new Intent(ProfileActivity.this, LocateOnMapActivity.class);
                startActivityForResult(intent_loc, 100);
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equalsIgnoreCase("apartment")) {
                    if (binding.nameEdt.getText().toString().length() > 0 && binding.emailEdt.getText().toString().length() > 0 &&
                            binding.mobileEdt.getText().toString().length() > 0 && binding.apartbuildingEdt.getText().toString().length() > 0 &&
//                        binding.apartnameEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.APARTMENTNAME)
                            binding.apartnameEdt.getText().toString().length() > 0 && binding.flatnoEdt.getText().toString().length() > 0
                            //binding.gstEdt.getText().toString().length() > 0
                    ) {
                        if (binding.mobileEdt.getText().length() == 10) {
                            if (emailValidator(binding.emailEdt.getText().toString())) {
                                workUpdate();
                            }
                        } else
                            Toast.makeText(ProfileActivity.this, Constant.VALIDMOBILE, Toast.LENGTH_SHORT).show();

                    } else
                        Toast.makeText(ProfileActivity.this, Constant.DETAILS, Toast.LENGTH_SHORT).show();
                } else if (type.equalsIgnoreCase("doorstep")) {
                    if (binding.nameEdt.getText().toString().length() > 0 && binding.emailEdt.getText().toString().length() > 0 &&
                            binding.mobileEdt.getText().toString().length() > 0 && binding.addressEdt.getText().toString().length() > 0) {
                        if (binding.mobileEdt.getText().length() == 10) {
                            if (emailValidator(binding.emailEdt.getText().toString())) {
                                workUpdate();
                            }
                        } else
                            Toast.makeText(ProfileActivity.this, Constant.VALIDMOBILE, Toast.LENGTH_SHORT).show();

                    } else
                        Toast.makeText(ProfileActivity.this, Constant.DETAILS, Toast.LENGTH_SHORT).show();
                } else {
                    if (binding.nameEdt.getText().toString().length() > 0 && binding.emailEdt.getText().toString().length() > 0 &&
                            binding.mobileEdt.getText().toString().length() > 0 && binding.apartbuildingEdt.getText().toString().length() > 0 &&
//                        binding.apartnameEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.APARTMENTNAME)
                            binding.apartnameEdt.getText().toString().length() > 0 && binding.flatnoEdt.getText().toString().length() > 0) {
                        if (binding.mobileEdt.getText().length() == 10) {
                            if (emailValidator(binding.emailEdt.getText().toString())) {
                                workUpdate();
                            }
                        } else
                            Toast.makeText(ProfileActivity.this, Constant.VALIDMOBILE, Toast.LENGTH_SHORT).show();

                    } else
                        Toast.makeText(ProfileActivity.this, Constant.DETAILS, Toast.LENGTH_SHORT).show();
                }

            }
        });
        work();
        binding.apartnameEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.spinner.performClick();
            }
        });

    }

    private void work() {
        if (isNetworkAvailable()) {
            profile();
            apartmentList();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileActivity.this);
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

    private void apartmentList() {
        apartments = new ArrayList<>();
        apartmentname = new ArrayList<>();
        final KProgressHUD hud = KProgressHUD.create(ProfileActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ApartmentList> call = apiInterface.apartmentList();
        call.enqueue(new Callback<ApartmentList>() {
            @Override
            public void onResponse(Call<ApartmentList> call, Response<ApartmentList> response) {
                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        ApartmentList apartmentList = response.body();
                        if (apartmentList.code.equalsIgnoreCase("200")) {
                            apartments = apartmentList.Apartment;
                            int pos = 0;
                            for (int i = 0; i < apartments.size(); i++) {
                                String items = apartments.get(i).name;
                                apartmentname.add(items);
                            }

                            spinner_item.clear();
                            spinner_item.addAll(apartmentname);
                            //spinner_item.clear();

                            binding.spinner.setAdapter(new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_list_item_1, spinner_item));

                            binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    binding.apartnameEdt.setText(spinner_item.get(i));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                        }
                    } else {
                        ApiConfig.responseToast(ProfileActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApartmentList> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(ProfileActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void profile() {
        final KProgressHUD hud = KProgressHUD.create(ProfileActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginDetails> call = apiInterface.profile(token, customerid);
        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        final LoginDetails loginDetails = response.body();
                        if (loginDetails.code.equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            String json = gson.toJson(loginDetails);
                            sessionManager.UserName(loginDetails.name);
                            sessionManager.UserEmail(loginDetails.email);
                            sessionManager.UserToken(loginDetails.token);
                            sessionManager.UserId(loginDetails.customer_id);
//                    sessionManager.userCode(loginDetails.usercode);
                            sessionManager.UserMobile(loginDetails.mobile);
                            sessionManager.UserStatus(loginDetails.status);
                            sessionManager.UserApartBuilding(loginDetails.apartment_building);
                            sessionManager.UserApartName(loginDetails.apartment_name);
                            sessionManager.UserFlatno(loginDetails.flat_no);

                            binding.nameEdt.setText(loginDetails.name);
                            binding.emailEdt.setText(loginDetails.email);
                            binding.mobileEdt.setText(loginDetails.mobile);
                            binding.apartnameEdt.setText(loginDetails.apartment_name);
                            apartname = loginDetails.apartment_name;
                            binding.apartbuildingEdt.setText(loginDetails.apartment_building);
                            binding.flatnoEdt.setText(loginDetails.flat_no);
                            binding.addressEdt.setText(loginDetails.address);
                            binding.gstEdt.setText(loginDetails.gst);
                            latitude = loginDetails.latitude;
                            longitude = loginDetails.longitude;
                        } else if (loginDetails.code.equalsIgnoreCase("201")) {
                            sessionManager.logoutUsers();
                        }
                    } else {
                        ApiConfig.responseToast(ProfileActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginDetails> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(ProfileActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void workUpdate() {
        if (isNetworkAvailable()) {
            profileUpdate();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection.");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Action for "Ok".
                            workUpdate();
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

    private void profileUpdate() {
        final KProgressHUD hud = KProgressHUD.create(ProfileActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.profileupdate(token, customerid, binding.apartnameEdt.getText().toString(),
                binding.apartbuildingEdt.getText().toString(), binding.flatnoEdt.getText().toString(), binding.addressEdt.getText().toString(),
                latitude, longitude, binding.gstEdt.getText().toString().trim());
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
                            Toast.makeText(ProfileActivity.this, jsonObject.optString("result"), Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(i);
                        } else {
                            Toast.makeText(ProfileActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(ProfileActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(ProfileActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*Email validating*/
    private boolean emailValidator(String s) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(s);
        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.LOAD_FROM.equalsIgnoreCase("map")) {
            if (!TextUtils.isEmpty(sessionManager.getData(SessionManager.KEY_ADDRESS)) || sessionManager.getData(SessionManager.KEY_ADDRESS) != null) {
                binding.addressEdt.setText(sessionManager.getData(SessionManager.KEY_ADDRESS));
                latitude = sessionManager.getData(SessionManager.KEY_LATITUDE);
                longitude = sessionManager.getData(SessionManager.KEY_LONGITUDE);
            }
        }
    }
}