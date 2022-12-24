package com.muvierecktech.carrocare.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
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
import com.muvierecktech.carrocare.adapter.HelpListAdapter;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityHelpAndSupportBinding;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpAndSupportActivity extends AppCompatActivity {
    public ActivityHelpAndSupportBinding binding;
    SessionManager sessionManager;
    String customerid, token;
    String[] messageList = {Constant.NOTCLEANPROPER, Constant.NOTCLEANTIME, Constant.PACKAGE, Constant.PAYMENT, Constant.NOTCLEAN, Constant.VEHICLEDAMAGE,
            Constant.CHANGEMOBILE, Constant.NOTCLEANLONGTIME, Constant.NOTMYVEHICLE, Constant.INTERIORNOT, Constant.EARLIERPHOTO, Constant.IAMNOTAVAILE, Constant.OTHERS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_help_and_support);
        sessionManager = new SessionManager(this);
        HashMap<String, String> hashMap = sessionManager.getUserDetails();
        customerid = hashMap.get(SessionManager.KEY_USERID);
        token = hashMap.get(SessionManager.KEY_TOKEN);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        HelpListAdapter helpListAdapter = new HelpListAdapter(HelpAndSupportActivity.this, messageList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.helpRc.setLayoutManager(linearLayoutManager);
        binding.helpRc.setAdapter(helpListAdapter);
    }

    public void work(final String title, final String message) {
        if (isNetworkAvailable()) {
            SubmitForm(title, message);
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(HelpAndSupportActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection.");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Action for "Ok".
                            work(title, message);
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

    private void SubmitForm(String title, String message) {
        final KProgressHUD hud = KProgressHUD.create(HelpAndSupportActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        Log.e("****************", title + "" + message);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.helpandsupport(title, message,
                customerid + "", "" + token);
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
                            Toast.makeText(HelpAndSupportActivity.this, jsonObject.optString("result"), Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(HelpAndSupportActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ApiConfig.responseToast(HelpAndSupportActivity.this, response.code());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(HelpAndSupportActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
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
}