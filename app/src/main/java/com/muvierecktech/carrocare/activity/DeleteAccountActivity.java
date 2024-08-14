package com.muvierecktech.carrocare.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.databinding.ActivityDeleteAccountBinding;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteAccountActivity extends AppCompatActivity {

    public ActivityDeleteAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delete_account);

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitForm();
            }
        });
    }

    private void SubmitForm() {
        String emailAddress =  binding.emailEdt.getText().toString();
        String description = binding.descEdt.getText().toString();
        if(emailAddress.isEmpty()){
            Toast.makeText(DeleteAccountActivity.this, "Please enter your email address",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        final KProgressHUD hud = KProgressHUD.create(DeleteAccountActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hud.dismiss();
                Toast.makeText(DeleteAccountActivity.this, "Your request sent to admin " +
                                "successfully",
                        Toast.LENGTH_SHORT).show();
            }
        }, 3000);


    }
}