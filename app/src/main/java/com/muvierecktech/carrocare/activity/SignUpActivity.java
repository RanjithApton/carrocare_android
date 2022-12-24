package com.muvierecktech.carrocare.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivitySignUpBinding;
import com.muvierecktech.carrocare.model.LoginDetails;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    String otp;
    String deviceID;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        sessionManager = new SessionManager(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("TAG", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String newToken = task.getResult();
                    Log.e("newToken", newToken);
                    deviceID = newToken;
                });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.loginBtn.getText().toString().equals("Send OTP")) {
                    if (binding.nameEdt.getText().toString().length() > 0 && binding.emailEdt.getText().toString().length() > 0
                            && binding.mobileEdt.getText().toString().length() > 0) {
                        if (binding.mobileEdt.getText().toString().length() == 10) {
                            if (emailValidator(binding.emailEdt.getText().toString())) {
                                worksendOtp();
                            } else
                                Toast.makeText(SignUpActivity.this, Constant.VALIDEMAIL, Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(SignUpActivity.this, Constant.VALIDMOBILE, Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(SignUpActivity.this, Constant.DETAILS, Toast.LENGTH_LONG).show();
                }
            }
        });


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

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

    private void worksendOtp() {
        if (isNetworkAvailable()) {
            SendOTP();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SignUpActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection.");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Ok".
                    worksendOtp();
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
        final KProgressHUD hud = KProgressHUD.create(SignUpActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.loginotp(binding.mobileEdt.getText().toString(), binding.nameEdt.getText().toString(), binding.emailEdt.getText().toString());
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
                            otp = jsonObject.optString("OTP");
                            binding.loginBtn.setText("Resend OTP");
                            binding.submitBtn.setVisibility(View.VISIBLE);
                            binding.otpRl.setVisibility(View.VISIBLE);
                            binding.otpEdt.setText(null);
                            binding.passEdt.setText(null);
                            binding.conpassEdt.setText(null);
                            binding.loginBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (binding.loginBtn.getText().toString().equals("Send OTP")) {
                                        if (binding.nameEdt.getText().toString().length() > 0 && binding.emailEdt.getText().toString().length() > 0
                                                && binding.mobileEdt.getText().toString().length() > 0) {
                                            if (binding.mobileEdt.getText().toString().length() == 10) {
                                                if (emailValidator(binding.emailEdt.getText().toString())) {
                                                    worksendOtp();
                                                }
                                            }
                                        }
                                    } else if (binding.loginBtn.getText().toString().equals("Resend OTP")) {
                                        if (binding.nameEdt.getText().toString().length() > 0 && binding.emailEdt.getText().toString().length() > 0
                                                && binding.mobileEdt.getText().toString().length() > 0 && binding.otpEdt.getText().toString().length() > 0) {
                                            if (binding.mobileEdt.getText().toString().length() == 10) {
                                                if (emailValidator(binding.emailEdt.getText().toString())) {
                                                    binding.otpEdt.setText(null);

                                                }
                                            }
                                        }
                                    }
                                }
                            });
                            binding.submitBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (binding.passRl.getVisibility() == View.GONE) {
                                        if (binding.nameEdt.getText().toString().length() > 0 && binding.emailEdt.getText().toString().length() > 0
                                                && binding.mobileEdt.getText().toString().length() > 0 && binding.otpEdt.getText().toString().length() > 0) {
                                            if (binding.mobileEdt.getText().toString().length() == 10) {
                                                if (emailValidator(binding.emailEdt.getText().toString())) {
//                                workcheckOtp();
                                                    if (binding.otpEdt.getText().toString().equals(otp)) {
                                                        binding.passRl.setVisibility(View.VISIBLE);
                                                        binding.conpassRl.setVisibility(View.VISIBLE);
                                                    } else
                                                        Toast.makeText(SignUpActivity.this, Constant.VALIDOTP, Toast.LENGTH_LONG).show();
                                                } else
                                                    Toast.makeText(SignUpActivity.this, Constant.VALIDEMAIL, Toast.LENGTH_LONG).show();
                                            } else
                                                Toast.makeText(SignUpActivity.this, Constant.VALIDMOBILE, Toast.LENGTH_LONG).show();
                                        } else
                                            Toast.makeText(SignUpActivity.this, Constant.DETAILS, Toast.LENGTH_LONG).show();
                                    } else {
                                        if (binding.nameEdt.getText().toString().length() > 0 && binding.emailEdt.getText().toString().length() > 0
                                                && binding.mobileEdt.getText().toString().length() > 0 && binding.otpEdt.getText().toString().length() > 0
                                                && binding.passEdt.getText().toString().length() > 0 && binding.conpassEdt.getText().toString().length() > 0) {
                                            if (binding.mobileEdt.getText().toString().length() == 10) {
                                                if (emailValidator(binding.emailEdt.getText().toString())) {
                                                    if (binding.passEdt.getText().toString().equalsIgnoreCase(binding.conpassEdt.getText().toString())) {
                                                        workregister();
                                                    } else
                                                        Toast.makeText(SignUpActivity.this, Constant.PASSNOTMATCH, Toast.LENGTH_LONG).show();
                                                } else
                                                    Toast.makeText(SignUpActivity.this, Constant.VALIDEMAIL, Toast.LENGTH_LONG).show();
                                            } else
                                                Toast.makeText(SignUpActivity.this, Constant.VALIDMOBILE, Toast.LENGTH_LONG).show();
                                        } else
                                            Toast.makeText(SignUpActivity.this, Constant.DETAILS, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(SignUpActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        ApiConfig.responseToast(SignUpActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(SignUpActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void workregister() {
        if (isNetworkAvailable()) {
            Register();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SignUpActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection.");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Ok".
                    workregister();
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

    private void Register() {
        final KProgressHUD hud = KProgressHUD.create(SignUpActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        String model = Build.MODEL;
        String deviceOs = Build.VERSION.RELEASE;
        String name = Build.BRAND;

//        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
//
//        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginDetails> call = apiInterface.register(binding.mobileEdt.getText().toString(), binding.passEdt.getText().toString(),
                binding.nameEdt.getText().toString(), binding.emailEdt.getText().toString(), deviceID, name, model, deviceOs);
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
//                    sessionManager.(loginDetails.usercode);
                            sessionManager.UserMobile(loginDetails.mobile);
                            sessionManager.UserStatus(loginDetails.status);
                            sessionManager.UserApartBuilding(loginDetails.apartment_building);
                            sessionManager.UserApartName(loginDetails.apartment_name);
                            sessionManager.UserFlatno(loginDetails.flat_no);

                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (loginDetails.code.equalsIgnoreCase("201")) {
                            Toast.makeText(SignUpActivity.this, loginDetails.message, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        ApiConfig.responseToast(SignUpActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginDetails> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(SignUpActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
