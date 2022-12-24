package com.muvierecktech.carrocare.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.CartListAdapter;
import com.muvierecktech.carrocare.adapter.IntWashOrderAdapter;
import com.muvierecktech.carrocare.adapter.SliderAdapter;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.MyDatabaseHelper;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityMainBinding;
import com.muvierecktech.carrocare.model.LoginDetails;
import com.muvierecktech.carrocare.model.OrdersList;
import com.muvierecktech.carrocare.model.SettingsList;
import com.muvierecktech.carrocare.model.SliderList;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;
    int[] cars = {R.drawable.slide_1, R.drawable.slide_2, R.drawable.slide_3};
    SessionManager sessionManager;
    String username, apartname, token, customerid;
    boolean doubleBackToExitPressedOnce = false;
    String currentVersion;
    List<OrdersList.Orders> order;
    ArrayList<String> orderId;
    List<String> ser_type_wash;
    List<String> ser_type_addon;
    MyDatabaseHelper databaseHelper;
    CartListAdapter dbAdapter;
    String firebase_id;

    public static void OpenBottomDialog(final Activity activity) {
        View sheetView = activity.getLayoutInflater().inflate(R.layout.lyt_update, null);
        ViewGroup parentViewGroup = (ViewGroup) sheetView.getParent();
        if (parentViewGroup != null) {
            parentViewGroup.removeAllViews();
        }

        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        ImageView imgclose = sheetView.findViewById(R.id.imgclose);
        Button btnNotNow = sheetView.findViewById(R.id.btnNotNow);
        Button btnUpadateNow = sheetView.findViewById(R.id.btnUpdateNow);
        if (Constant.VERSION_STATUS.equals("0")) {
            btnNotNow.setVisibility(View.VISIBLE);
            imgclose.setVisibility(View.VISIBLE);
            mBottomSheetDialog.setCancelable(true);
        } else
            mBottomSheetDialog.setCancelable(false);

        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetDialog.isShowing())
                    mBottomSheetDialog.dismiss();
            }
        });
        btnNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetDialog.isShowing())
                    mBottomSheetDialog.dismiss();
            }
        });
        btnUpadateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "play store link is " + Constant.PLAY_STORE_LINK + activity.getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.PLAY_STORE_LINK + activity.getPackageName()));
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }

    public static int compareVersion(String version1, String version2) {
        String[] arr1 = version1.split("\\.");
        String[] arr2 = version2.split("\\.");

        int i = 0;
        while (i < arr1.length || i < arr2.length) {
            if (i < arr1.length && i < arr2.length) {
                if (Integer.parseInt(arr1[i]) < Integer.parseInt(arr2[i])) {
                    return -1;
                } else if (Integer.parseInt(arr1[i]) > Integer.parseInt(arr2[i])) {
                    return 1;
                }
            } else if (i < arr1.length) {
                if (Integer.parseInt(arr1[i]) != 0) {
                    return 1;
                }
            } else {
                if (Integer.parseInt(arr2[i]) != 0) {
                    return -1;
                }
            }

            i++;
        }

        return 0;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        sessionManager = new SessionManager(this);
        HashMap<String, String> hashMap = sessionManager.getUserDetails();
        username = hashMap.get(SessionManager.KEY_USERNAME);
        apartname = hashMap.get(SessionManager.KEY_APARTNAME);
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);
        binding.username.setText("Hi " + username + " !");

        databaseHelper = new MyDatabaseHelper(this);

        ApiConfig.displayLocationSettingsRequest(MainActivity.this);
        ApiConfig.getLocation(MainActivity.this);

        //int totalItemOfCart = databaseHelper.getTotalItemOfCart();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("TAG", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String newToken = task.getResult();
                    Log.e("newToken", newToken);
                    firebase_id = newToken;
                });

        showCartCount();

        binding.checkCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });

        binding.apartmentService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.setData(SessionManager.USER_WANTS, "apartment");
                startActivity(new Intent(MainActivity.this, ApartmentServiceActivity.class));
            }
        });

        binding.doorstepService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.setData(SessionManager.USER_WANTS, "doorstep");
                startActivity(new Intent(MainActivity.this, DoorStepServiceActivity.class));
            }
        });

        binding.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.internal_car_wash) {
                    binding.popupinternal.setVisibility(View.VISIBLE);
                    return true;
                } else if (itemId == R.id.my_orders) {
                    startActivity(new Intent(MainActivity.this, MyOrdersActivity.class));
                    return true;
                } else if (itemId != R.id.profile) {
                    return false;
                } else {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
                }
            }
        });

        binding.orderrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.orderrl.setVisibility(View.GONE);
            }
        });

        binding.orderImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                workextra();
            }
        });
        binding.orderEdt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                workextra();
            }
        });
        binding.detailBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                binding.detailrl.setVisibility(View.GONE);
            }
        });
        binding.noorderBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });

        binding.popupCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                binding.popupinternal.setVisibility(View.GONE);
            }
        });
        binding.popupinternal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                binding.popupinternal.setVisibility(View.GONE);
            }
        });
        binding.buttonProcessed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (binding.orderEdt.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Please Select your Order Id", Toast.LENGTH_SHORT).show();
                }
            }
        });
        work();


        // Doorstep service 23-07-2022
        binding.myVechicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyVehiclesActivity.class);
                startActivity(intent);
            }
        });
        binding.carWash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(apartname) || apartname == null || apartname.equalsIgnoreCase("null")) {
                    Constant.LOAD_FROM = "main";
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, CarWashActivity.class);
                    //intent.putExtra("headername", Constant.WASH);
                    startActivity(intent);
                }
            }
        });
        binding.carDisinfection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(apartname) || apartname == null || apartname.equalsIgnoreCase("null")) {
                    Constant.LOAD_FROM = "main";
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, DisinfectionActivity.class);
                    //intent.putExtra("headername", Constant.WASH);
                    startActivity(intent);
                }
            }
        });
        binding.bikeWash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(apartname) || apartname == null || apartname.equalsIgnoreCase("null")) {
                    Constant.LOAD_FROM = "main";
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, BikeWashActivity.class);
                    //intent.putExtra("headername", Constant.BWASH);
                    startActivity(intent);
                }
            }
        });
        binding.carService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(apartname) || apartname == null || apartname.equalsIgnoreCase("null")) {
                    Constant.LOAD_FROM = "main";
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, AddOnActivity.class);
                    //intent.putExtra("headername",Constant.ADDON);
                    startActivity(intent);
                }
            }
        });
        binding.extraInterior.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(apartname) || apartname == null || apartname.equalsIgnoreCase("null")) {
                    Constant.LOAD_FROM = "main";
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, ExtraInteriorActivity.class);
                    //intent.putExtra("headername",Constant.EXTRAINT);
                    startActivity(intent);
                }
            }
        });

    }

    private void work() {
        if (isNetworkAvailable()) {
            getSettings();
            Sliders();
            profile();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
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

    private void workextra() {
        if (isNetworkAvailable()) {
            internalWash();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection.");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Action for "Ok".
                            workextra();
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

    private void internalWash() {
        ser_type_wash = new ArrayList<>();
        ser_type_addon = new ArrayList<>();
        binding.orderrl.setVisibility(View.VISIBLE);
        order = new ArrayList();
        orderId = new ArrayList<>();
        final KProgressHUD hud = KProgressHUD.create(MainActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<OrdersList> call = apiInterface.orderlistInternal(token, customerid);
        call.enqueue(new Callback<OrdersList>() {
            @Override
            public void onResponse(Call<OrdersList> call, Response<OrdersList> response) {
                try {
                    if (response.isSuccessful()) {
                        OrdersList body = response.body();
                        if (body.code.equalsIgnoreCase("200")) {
                            hud.dismiss();
                            /*order = body.orders;
                            orderId.add(0, Constant.ORDERID);

                            for (int i = 0; i < order.size(); i++) {
                                orderId.add(order.get(i).order_id);
                                if (order.get(i).service_type.equalsIgnoreCase("Wash")){
                                    if(order.get(i).status.equalsIgnoreCase("Active") ||
                                            order.get(i).status.equalsIgnoreCase("Over Due")){
                                        ser_type_wash.add(order.get(i).service_type);
                                    }
                                }
                                else {
                                    ser_type_addon.add(order.get(i).service_type);
                                }
                            }

                            if (!ser_type_wash.isEmpty()) {
                                binding.orderRc.setVisibility(View.VISIBLE);
                                binding.noorders.setVisibility(View.GONE);
                            }else{
                                binding.orderRc.setVisibility(View.GONE);
                                binding.noorders.setVisibility(View.VISIBLE);
                            }*/

                            if (!body.orders.isEmpty()) {
                                binding.orderRc.setVisibility(View.VISIBLE);
                                binding.noorders.setVisibility(View.GONE);
                            } else {
                                binding.orderRc.setVisibility(View.GONE);
                                binding.noorders.setVisibility(View.VISIBLE);
                            }

                            binding.detailrl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    binding.detailrl.setVisibility(View.GONE);
                                }
                            });

                            IntWashOrderAdapter intWashOrderAdapter = new IntWashOrderAdapter(MainActivity.this, body.orders);
                            binding.orderRc.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
                            binding.orderRc.setAdapter(intWashOrderAdapter);

                        } else if (body.code.equalsIgnoreCase("201")) {
                            hud.dismiss();
                            binding.noorders.setVisibility(View.VISIBLE);
                            binding.orderRc.setVisibility(View.GONE);
                        }
                    } else {
                        ApiConfig.responseToast(MainActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(Call<OrdersList> call, Throwable th) {
                hud.dismiss();
                binding.orderRc.setVisibility(View.GONE);
                binding.orderrl.setVisibility(View.GONE);
                binding.popupinternal.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void profile() {
        final KProgressHUD hud = KProgressHUD.create(MainActivity.this)
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
                            sessionManager.UserMobile(loginDetails.mobile);
                            sessionManager.UserStatus(loginDetails.status);
                            sessionManager.UserApartBuilding(loginDetails.apartment_building);
                            sessionManager.UserApartName(loginDetails.apartment_name);
                            apartname = loginDetails.apartment_name;
                            sessionManager.UserFlatno(loginDetails.flat_no);
                        } else if (loginDetails.code.equalsIgnoreCase("201")) {
                            sessionManager.logoutUsers();
                        }
                    } else {
                        ApiConfig.responseToast(MainActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginDetails> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(MainActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
                Log.e("Profile", "" + t.toString());
            }
        });
    }

    private void Sliders() {
        final KProgressHUD hud = KProgressHUD.create(MainActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<SliderList> call = apiInterface.slider();
        call.enqueue(new Callback<SliderList>() {
            @Override
            public void onResponse(Call<SliderList> call, Response<SliderList> response) {
                hud.dismiss();
                try {
                    if (response.isSuccessful()) {
                        final SliderList sliderList = response.body();
                        if (sliderList.code.equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            String json = gson.toJson(sliderList);
                            SliderAdapter offerAdapter = new SliderAdapter(MainActivity.this, sliderList.slider);
                            addBannerImages(sliderList.slider);
                        }
                    } else {
                        ApiConfig.responseToast(MainActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SliderList> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(MainActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
                Log.e("Slider", "" + t.toString());
            }
        });
    }

    private void addBannerImages(List<SliderList.Slider> slider) {
        try {
            List<SlideModel> slideModelList = new ArrayList<>();

            for (int i = 0; i < slider.size(); i++) {
                slideModelList.add(new SlideModel(slider.get(i).simage));
            }

            binding.imageSlider.setImageList(slideModelList, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCartCount();

    }

    @SuppressLint("LongLogTag")
    public void showCartCount() {
        //int totalItemOfCart = dbAdapter.cart_count;
        int totalItemOfCart = databaseHelper.getTotalItemOfCart();

        if (totalItemOfCart == 0) {
            binding.cartCount.setVisibility(View.GONE);
        } else {
            binding.cartCount.setText(String.valueOf(totalItemOfCart));
        }

        Log.e("Total item of cart--->   ", "" + totalItemOfCart);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.exit_msg), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void getSettings() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<SettingsList> call = apiInterface.getSettings("");
        call.enqueue(new Callback<SettingsList>() {
            @Override
            public void onResponse(Call<SettingsList> call, Response<SettingsList> response) {
                try {
                    if (response.isSuccessful()) {
                        final SettingsList settingsList = response.body();
                        if (settingsList.code.equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            String json = gson.toJson(settingsList);
                            Constant.GST_PERCENTAGE = settingsList.res.gst;
                            sessionManager.setData(SessionManager.GST_PERCENTAGE, String.valueOf(settingsList.res.gst));
                            Constant.VERSION_CODE = settingsList.res.current_version;
                            Constant.REQUIRED_VERSION = settingsList.res.minimum_version;
                            String versionName = "";
                            try {
                                PackageInfo packageInfo = MainActivity.this.getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0);
                                versionName = packageInfo.versionName;
                            } catch (PackageManager.NameNotFoundException e) {
                                Log.e("TAG", "version name exception is " + e);
                            }
                            Log.e("VERSION NAME", versionName);
                            if (compareVersion(versionName, Constant.REQUIRED_VERSION) < 0) {
                                Constant.VERSION_STATUS = "1";
                                OpenBottomDialog(MainActivity.this);
                            } else if (compareVersion(versionName, Constant.VERSION_CODE) < 0) {
                                Constant.VERSION_STATUS = "0";
                                OpenBottomDialog(MainActivity.this);
                            }
                        }
                    } else {
                        ApiConfig.responseToast(MainActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SettingsList> loginDetails, Throwable t) {
//                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
                Log.e("Settings", "" + t.toString());
            }
        });
    }
}
