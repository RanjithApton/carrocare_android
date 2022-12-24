package com.muvierecktech.carrocare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.BillingAdapter;
import com.muvierecktech.carrocare.adapter.MyOrdersAdapter;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.PermissionUtils;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityMyBillingBinding;
import com.muvierecktech.carrocare.model.BillingList;
import com.muvierecktech.carrocare.model.OrdersList;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBillingActivity extends AppCompatActivity {
    ActivityMyBillingBinding binding;
    SessionManager sessionManager;
    String token, customerid;

    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQ_CODE = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_billing);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_billing);
        sessionManager = new SessionManager(this);
        HashMap<String, String> hashMap = sessionManager.getUserDetails();
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);
        work();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void work() {
        if (isNetworkAvailable()) {
            ServicePrice();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MyBillingActivity.this);
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

    private void ServicePrice() {
        final KProgressHUD hud = KProgressHUD.create(MyBillingActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<BillingList> call = apiInterface.billinglist(token, customerid);
        call.enqueue(new Callback<BillingList>() {
            @Override
            public void onResponse(Call<BillingList> call, Response<BillingList> response) {
                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        final BillingList billingList = response.body();
                        if (billingList.code.equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            String json = gson.toJson(billingList);
                            binding.noorders.setVisibility(View.GONE);
                            binding.ordersRc.setVisibility(View.VISIBLE);

                            BillingAdapter billingAdapter = new BillingAdapter(MyBillingActivity.this, billingList.res);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyBillingActivity.this, LinearLayoutManager.VERTICAL, false);
                            binding.ordersRc.setLayoutManager(linearLayoutManager);
                            binding.ordersRc.setAdapter(billingAdapter);

                        } else if (billingList.code.equalsIgnoreCase("203")) {
                            sessionManager.logoutUsers();
                        } else if (billingList.code.equalsIgnoreCase("201")) {
                            binding.noorders.setVisibility(View.VISIBLE);
                            binding.ordersRc.setVisibility(View.GONE);
                        }
                    } else {
                        ApiConfig.responseToast(MyBillingActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BillingList> call, Throwable t) {
                hud.dismiss();
                binding.noorders.setVisibility(View.VISIBLE);
                binding.ordersRc.setVisibility(View.GONE);
                Toast.makeText(MyBillingActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyBillingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission ) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (PermissionUtils.neverAskAgainSelected(this, permission)) {
                        displayNeverAskAgainDialog();
                    } else {
                        ActivityCompat.requestPermissions(this, PERMISSIONS,REQ_CODE);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQ_CODE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("REQ_CODE ::: ", "Permission granted successfully");
                //Toast.makeText(this, "Permission granted successfully", Toast.LENGTH_LONG).show();
            } else {
                for (String permission : PERMISSIONS) {
                    PermissionUtils.setShouldShowStatus(this, permission);
                }
            }
        }
    }

    private void displayNeverAskAgainDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("We need to permission for performing necessary task. Please permit the permission through "
                + "Settings screen.\n\nSelect Permissions -> Enable permission");
        builder.setCancelable(false);
        builder.setPositiveButton("Permit Manually", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        //builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void downloadInvoice(String download_invoice, String s) {
        try{
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(download_invoice));
            String title = s;
            request.setTitle(title);
            request.setDescription("Downloading file please wait...");
            String cookie = CookieManager.getInstance().getCookie(download_invoice);
            request.addRequestHeader("cookie",cookie);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title);

            DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);

            PermissionUtils.successToast(this,title);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invoice Download Failed.", Toast.LENGTH_SHORT).show();
        }
    }

}