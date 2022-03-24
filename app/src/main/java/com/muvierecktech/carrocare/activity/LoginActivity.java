package com.muvierecktech.carrocare.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.DatabaseHelper;
import com.muvierecktech.carrocare.databinding.ActivityLoginBinding;
import com.muvierecktech.carrocare.model.LoginDetails;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;

import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    SessionManager sessionManager;
    String firebase_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        sessionManager = new SessionManager(this);
        ApiConfig.displayLocationSettingsRequest(LoginActivity.this);
        ApiConfig.getLocation(LoginActivity.this);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken",newToken);
                firebase_id = newToken;
            }
        });


        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.emailEdt.getText().toString().length()>0 && binding.passEdt.getText().toString().length()>0){
                    if (emailValidator(binding.emailEdt.getText().toString())){
                        work();
                    }else
                        Toast.makeText(LoginActivity.this, Constant.VALIDEMAIL,Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(LoginActivity.this,Constant.DETAILS,Toast.LENGTH_SHORT).show();
            }
        });
        binding.forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(LoginActivity.this,ForgetActivity.class);
                startActivity(intent);
            }
        });
    }

    private void work() {
        if (isNetworkAvailable()){
            Login();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
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

    private void Login() {
        final KProgressHUD hud = KProgressHUD.create(LoginActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginDetails> call = apiInterface.login(binding.emailEdt.getText().toString(),binding.passEdt.getText().toString());
        call.enqueue(new Callback<LoginDetails>() {
            @Override
            public void onResponse(Call<LoginDetails> call, Response<LoginDetails> response) {
                LoginDetails loginDetails = response.body();
                hud.dismiss();
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
                    /*Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();*/
                    workmode();
                } else  if (loginDetails.code.equalsIgnoreCase("201")) {
                    Toast.makeText(LoginActivity.this, loginDetails.message, Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onFailure(Call<LoginDetails> loginDetails, Throwable t) {
                Toast.makeText(LoginActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
                hud.dismiss();
            }
        });

    }

    private void workmode() {
        if (isNetworkAvailable()){
            loginverify();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection." );
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Ok".
                    workmode();
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

    public void loginverify() {
        String device_model = Build.MODEL;
        Log.e("Device Model", device_model);
        String os_version = Build.VERSION.RELEASE;
        Log.e("Device OS", os_version);
        String deice_name = Build.BRAND;
        Log.e("Device Name", deice_name);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.loginverify(firebase_id,deice_name,device_model,os_version,binding.emailEdt.getText().toString());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body();
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                        Gson gson = new Gson();
                        sessionManager.UserToken(jsonObject.optString(SessionManager.KEY_TOKEN));
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                        Toast.makeText(LoginActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(LoginActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void ShowHidePass(View view) {
        if (view.getId() != R.id.passhide) {
            return;
        }
        if (this.binding.passEdt.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            ((ImageView) view).setImageResource(R.drawable.ic_visible);
            this.binding.passEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            return;
        }
        ((ImageView) view).setImageResource(R.drawable.ic_invisible);
        this.binding.passEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }
}
