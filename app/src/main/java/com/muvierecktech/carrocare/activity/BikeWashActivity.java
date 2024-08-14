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
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityBikeWashBinding;
import com.muvierecktech.carrocare.model.ServicePriceList;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BikeWashActivity extends AppCompatActivity {
    ActivityBikeWashBinding binding;
    String cardesc, carid, carimg, carname, carprice, description, headername,displayPrice;
    List<ServicePriceList.Services> service;
    SessionManager sessionManager;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = (ActivityBikeWashBinding) DataBindingUtil.setContentView(this, R.layout.activity_bike_wash);

        headername = Constant.BWASH;
        binding.headerName.setText(headername);

//        headername = getIntent().getStringExtra("headername");
//        binding.headerName.setText(headername);

        work();
        binding.back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }

    /* access modifiers changed from: private */
    public void work() {
        if (isNetworkAvailable()) {
            ServicePrice();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle((CharSequence) "Alert!");
        builder.setMessage((CharSequence) "No internet.Please check your connection.");
        builder.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                work();
            }
        }).setNegativeButton((CharSequence) "Cancel ", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
    }

    private void ServicePrice() {
        final KProgressHUD show = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setCancellable(true).setAnimationSpeed(2).setDimAmount(0.5f).show();
        ((ApiInterface) ApiClient.getClient().create(ApiInterface.class)).Service(Constant.DAILYWASH).enqueue(new Callback<ServicePriceList>() {
            public void onResponse(Call<ServicePriceList> call, Response<ServicePriceList> response) {
                show.dismiss();
                try {
                    if (response.isSuccessful()) {
                        ServicePriceList body = response.body();
                        if (body.code.equalsIgnoreCase("200")) {
                            new Gson().toJson((Object) body);
                            service = body.services;
                            for (int i = 0; i < service.size(); i++) {
                                if (service.get(i).type.equalsIgnoreCase("bike")) {
                                    binding.carName.setText(service.get(i).type);
                                    carname = service.get(i).type;
                                    carprice = service.get(i).prices;
                                    int taxAmt =
                                            ((Constant.GST_PERCENTAGE * Integer.parseInt(service.get(i).prices)) / 100);
                                    int finalAmount = taxAmt + Integer.parseInt(service.get(i).prices);
                                    displayPrice = finalAmount+"";
                                    binding.carPrice.setText("Total amount\n₹ " + displayPrice);
                                    binding.carDesc.setText(HtmlCompat.fromHtml(service.get(i).description, 0));
                                    cardesc = service.get(i).description;
                                    Picasso.get().load(service.get(i).image)
                                            .placeholder((int) R.drawable.placeholder)
                                            .error((int) R.drawable.placeholder)
                                            .into(binding.carImg);
                                    carimg = service.get(i).image;

                                    carid = service.get(i).id;

                                    binding.booknow.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View view) {
                                            Intent intent = new Intent(BikeWashActivity.this, VehicleListActivity.class);
                                            intent.putExtra("carname", carname);
                                            intent.putExtra("carprice", carprice);
                                            intent.putExtra("cardesc", cardesc);
                                            intent.putExtra("carimage", carimg);
                                            intent.putExtra("carid", carid);
                                            intent.putExtra("header", headername);
                                            intent.putExtra("displayprice", displayPrice);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        }
                    } else {
                        ApiConfig.responseToast(BikeWashActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(Call<ServicePriceList> call, Throwable th) {
                show.dismiss();
                Toast.makeText(BikeWashActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
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
//        Intent intent = new Intent(BikeWashActivity.this,MainActivity.class);
//        startActivity(intent);
        finish();
    }
}
