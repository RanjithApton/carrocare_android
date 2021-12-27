package com.muvierecktech.carrocare.activity;

import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.Constant;

import com.muvierecktech.carrocare.databinding.ActivityForgetBinding;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetActivity extends AppCompatActivity {
    ActivityForgetBinding binding;
    String otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_forget);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Back();
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Back();
            }
        });
        binding.sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.sendOtp.getText().toString().equalsIgnoreCase("Resend OTP")){
                    if (binding.mobileEdt.getText().toString().length()>0){
                        if (binding.mobileEdt.getText().toString().length()==10){
                            worksendotp();
                            binding.otpEdt.setText(null);
                        }else
                            Toast.makeText(ForgetActivity.this, Constant.VALIDMOBILE,Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(ForgetActivity.this, Constant.DETAILS,Toast.LENGTH_SHORT).show();
                }else if (binding.mobileEdt.getText().toString().length()>0){
                    if (binding.mobileEdt.getText().toString().length()==10){
                        worksendotp();
                    }else
                        Toast.makeText(ForgetActivity.this, Constant.VALIDMOBILE,Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(ForgetActivity.this, Constant.DETAILS,Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void worksendotp() {
        if (isNetworkAvailable()){
            SendOTP();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ForgetActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection." );
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Ok".
                    worksendotp();
                }
            }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
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

    private void SendOTP() {
        final KProgressHUD hud = KProgressHUD.create(ForgetActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.forgot(binding.mobileEdt.getText().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body();
                hud.dismiss();
                binding.sendOtp.setText("Resend OTP");
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                        Gson gson = new Gson();
                        otp = jsonObject.optString("OTP");
                        binding.otpRl.setVisibility(View.VISIBLE);
                        binding.submitBtn.setVisibility(View.VISIBLE);
                        binding.sendOtp.setText("Resend OTP");
                        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (binding.submitBtn.getText().toString().equalsIgnoreCase("Verify")) {
                                    if (binding.otpEdt.getText().toString().length() > 0) {
                                        if (binding.otpEdt.getText().toString().equalsIgnoreCase(otp)) {
                                            binding.submitBtn.setText("Submit");
                                            binding.conpassRl.setVisibility(View.VISIBLE);
                                            binding.passRl.setVisibility(View.VISIBLE);
                                        } else
                                            Toast.makeText(ForgetActivity.this, Constant.VALIDOTP, Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(ForgetActivity.this, Constant.DETAILS, Toast.LENGTH_SHORT).show();
                                }else {
                                    if (binding.passEdt.getText().toString().length() > 0 && binding.conpassEdt.getText().toString().length() > 0) {
                                        if (binding.passEdt.getText().toString().equalsIgnoreCase(binding.conpassEdt.getText().toString())) {
                                            workForgot();
                                        } else
                                            Toast.makeText(ForgetActivity.this, Constant.PASSNOTMATCH, Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(ForgetActivity.this, Constant.DETAILS, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
//                    startActivity(intent);
//                    finish();

//                    sessionManager.UserName(loginDetails.username);
//                    sessionManager.userEmail(loginDetails.useremail);
//                    sessionManager.userAccess(loginDetails.accesstoken);
//                    sessionManager.userId(loginDetails.userid);
//                    sessionManager.userCode(loginDetails.usercode);
//                    sessionManager.userMobile(loginDetails.usermobile);
//                    sessionManager.userStatus(loginDetails.status);



            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(ForgetActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void workForgot() {
        if (isNetworkAvailable()){
            Forgot();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ForgetActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection." );
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Ok".
                    workForgot();
                }
            }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
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

    private void Forgot() {
        final KProgressHUD hud = KProgressHUD.create(ForgetActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.forgotupdate(binding.mobileEdt.getText().toString(),binding.passEdt.getText().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body();
                hud.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                        Gson gson = new Gson();
                        Toast.makeText(ForgetActivity.this,jsonObject.optString("result"),Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgetActivity.this,LoginActivity.class));
                        finish();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(ForgetActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void Back() {
        finish();
    }

}