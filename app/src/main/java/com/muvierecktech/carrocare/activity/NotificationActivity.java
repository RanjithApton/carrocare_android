package com.muvierecktech.carrocare.activity;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.databinding.ActivityNotificationBinding;


public class NotificationActivity extends AppCompatActivity {
    ActivityNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_notification);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
//        work();
    }

//    private void work() {
//        if (isNetworkAvailable()){
//            Notifications();
//        }else {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(NotificationActivity.this);
//            dialog.setCancelable(false);
//            dialog.setTitle("Alert!");
//            dialog.setMessage("No internet.Please check your connection." );
//            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int id) {
//                    //Action for "Ok".
//                    work();
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

//    private void Notifications() {
//        final KProgressHUD hud = KProgressHUD.create(NotificationActivity.this)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setCancellable(true)
//                .setAnimationSpeed(2)
//                .setDimAmount(0.5f)
//                .show();
//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<NotificationList> call = apiInterface.notifications("","");
//        call.enqueue(new Callback<NotificationList>() {
//            @Override
//            public void onResponse(Call<NotificationList> call, Response<NotificationList> response) {
//                final NotificationList notificationList = response.body();
//                hud.dismiss();
//                if (notificationList.code.equalsIgnoreCase("200")) {
//                    Gson gson = new Gson();
//                    String json = gson.toJson(notificationList);
//
//                    NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationActivity.this,notificationList.notifications);
//                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NotificationActivity.this,LinearLayoutManager.VERTICAL,false);
//                    binding.notificationRc.setLayoutManager(linearLayoutManager);
//                    binding.notificationRc.setAdapter(notificationAdapter);
//
//                }
//            }
//            @Override
//            public void onFailure(Call<NotificationList> call, Throwable t) {
//                hud.dismiss();
//                Toast.makeText(NotificationActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}