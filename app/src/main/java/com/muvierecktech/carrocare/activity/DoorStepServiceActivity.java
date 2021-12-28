package com.muvierecktech.carrocare.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.MapVehileAdapter;
import com.muvierecktech.carrocare.adapter.PagerAdapter;
import com.muvierecktech.carrocare.common.ApiConfig;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityDoorStepServiceBinding;
import com.muvierecktech.carrocare.model.DoorStepCarWash;
import com.muvierecktech.carrocare.model.PagerModel;
import com.muvierecktech.carrocare.model.VehicleDetails;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoorStepServiceActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener,
        PaymentResultListener {

    public ActivityDoorStepServiceBinding binding;
    private GoogleApiClient googleApiClient;
    private double longitude, c_longitude, c_latitude;
    private double latitude;
    private GoogleMap mMap;
    //PlaceAutocompleteFragment placeAutoComplete;
    Toolbar toolbar;
    ImageButton fabSatellite, fabStreet, fabCurrent;
    int mapType = GoogleMap.MAP_TYPE_NORMAL;
    SupportMapFragment mapFragment;
    public boolean isCurrent;

    ViewPager viewPager;
    List<PagerModel> pagerModels;
    PagerAdapter pagerAdapter;
    @SuppressLint("RestrictedApi")
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    Integer color[] = null;

    final int paddingPx = 300;
    final float MIN_SCALE = 0.8f;
    final float MAX_SCALE = 1f;

    String token,customerid;
    SessionManager sessionManager;
    private String plan_date_time_schedule = "";
    SingleDateAndTimePicker dateAndTimePicker, singleDateAndTimePicker;
    private Date defaultDate;
    String custmob,custemail,razorpayid;
    public boolean carCheck = false ;

    public ArrayList<String> service_type;

    String price_exterior, price_interior, price_polish, price_wax, price_sanitize, total_amount = "0";
    String price_carpolish, price_interiordetail, price_windowtint, detail_total_amount = "0";

    Activity activity;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_door_step_service);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_door_step_service);

        sessionManager = new SessionManager(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        fabCurrent = findViewById(R.id.fabCurrent);



        fabCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mapType = GoogleMap.MAP_TYPE_NORMAL;
                LatLng latLng = new LatLng(c_latitude, c_longitude);
                latitude = c_latitude;
                longitude = c_longitude;
                saveLocation(c_latitude, c_longitude);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title(getString(R.string.current_location)));

                //Moving the map
                mMap.clear();
                moveMap(false);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

                //text.setText("Latitude - " + latitude + "\nLongitude - " + longitude);
                binding.placeTxt.setText(ApiConfig.getAddress(latitude, longitude, DoorStepServiceActivity.this));
            }
        });
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mapFragment.getMapAsync(DoorStepServiceActivity.this);
            }
        }, 1000);


        activity = DoorStepServiceActivity.this;
        context = getApplicationContext();

        Constant.ONETIME_CAR_TYPE = null;

        sessionManager = new SessionManager(this);
        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);

        work();

        binding.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoorStepServiceActivity.this,MainProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.btnBattery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.ONETIME_ADDITIONAL = "battery";
                apicall();
            }
        });

        binding.btnPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.ONETIME_ADDITIONAL = "Painting";
                apicall();
            }
        });

        binding.bottomWashPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.schedulePopup.getVisibility() == View.VISIBLE){
                    binding.schedulePopup.setVisibility(View.GONE);
                    binding.bottomWashPopup.setVisibility(View.GONE);
                } else if (binding.carwashPopup.getVisibility() == View.VISIBLE) {
                    binding.carwashPopup.setVisibility(View.GONE);
                    binding.bottomWashPopup.setVisibility(View.GONE);
                    binding.extreriorCheck.setChecked(false);
                    binding.interiorCheck.setChecked(false);
                    binding.polisingCheck.setChecked(false);
                    binding.waxCheck.setChecked(false);
                    binding.sanitizeCheck.setChecked(false);
                    //service_type.clear();
                } else if (binding.detailingPopup.getVisibility() == View.VISIBLE) {
                    binding.detailingPopup.setVisibility(View.GONE);
                    binding.bottomWashPopup.setVisibility(View.GONE);
                    binding.interiorCheck.setChecked(false);
                    binding.carpolishngCheck.setChecked(false);
                    binding.windowtintingCheck.setChecked(false);
                    //service_type.clear();
                } else {
                    binding.bottomWashPopup.setVisibility(View.GONE);
                    //service_type.clear();
                }
            }
        });

        binding.closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.carwashPopup.setVisibility(View.GONE);
                binding.bottomWashPopup.setVisibility(View.GONE);
                //service_type.clear();
                binding.extreriorCheck.setChecked(false);
                binding.interiorCheck.setChecked(false);
                binding.polisingCheck.setChecked(false);
                binding.waxCheck.setChecked(false);
                binding.sanitizeCheck.setChecked(false);
                binding.carpolishngCheck.setChecked(false);
                binding.intertiordetailingCheck.setChecked(false);
                binding.windowtintingCheck.setChecked(false);
                total_amount = "0";
                binding.botAmount.setVisibility(View.GONE);
                detail_total_amount = "0";
                binding.detailAmount.setVisibility(View.GONE);
            }
        });

        binding.closePopup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.detailingPopup.setVisibility(View.GONE);
                binding.bottomWashPopup.setVisibility(View.GONE);
                //service_type.clear();
                binding.extreriorCheck.setChecked(false);
                binding.interiorCheck.setChecked(false);
                binding.polisingCheck.setChecked(false);
                binding.waxCheck.setChecked(false);
                binding.sanitizeCheck.setChecked(false);
                binding.carpolishngCheck.setChecked(false);
                binding.intertiordetailingCheck.setChecked(false);
                binding.windowtintingCheck.setChecked(false);
                total_amount = "0";
                binding.botAmount.setVisibility(View.GONE);
                detail_total_amount = "0";
                binding.detailAmount.setVisibility(View.GONE);
            }
        });

        binding.bottomPaintPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomPaintPopup.setVisibility(View.GONE);
            }
        });

        binding.closePaintPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomPaintPopup.setVisibility(View.GONE);
            }
        });

        binding.bottomBatteryPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomBatteryPopup.setVisibility(View.GONE);
            }
        });

        binding.closeBatteryPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomBatteryPopup.setVisibility(View.GONE);
            }
        });

        binding.closePopupSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.schedulePopup.setVisibility(View.GONE);
            }
        });

        binding.addVehcileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.LOAD_FROM = "add";
                startActivity(new Intent(DoorStepServiceActivity.this,MapAddVechileActivity.class));
                finish();
            }
        });

        viewPager = findViewById(R.id.bottom_vp);

        pagerModels = new ArrayList<>();
        pagerModels.add(new PagerModel(R.drawable.ds_carwash,"Door step car wash"));
        pagerModels.add(new PagerModel(R.drawable.ds_detailing,"Detailing"));
        pagerModels.add(new PagerModel(R.drawable.ds_paint,"Painting & Denting"));
        // pagerModels.add(new PagerModel(R.drawable.home_extra_int,"Door step extra interior"));
        // pagerModels.add(new PagerModel(R.drawable.home_machine_polish,"Door step car polish"));
        pagerModels.add(new PagerModel(R.drawable.ds_insurance,"Door step insurance"));
        pagerModels.add(new PagerModel(R.drawable.ds_batterychange,"Battery change"));

        pagerAdapter = new PagerAdapter(pagerModels,this);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(paddingPx, 0, paddingPx, 0);

        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                float pagerWidthPx = ((ViewPager) page.getParent()).getWidth();
                float pageWidthPx = pagerWidthPx - 2 * paddingPx;

                float maxVisiblePages = pagerWidthPx / pageWidthPx;
                float center = maxVisiblePages / 2f;

                float scale;
                if (position + 0.5f < center - 0.5f || position > center) {
                    scale = MIN_SCALE;
                } else {
                    float coef;
                    if (position + 0.5f < center) {
                        coef = (position + 1 - center) / 0.5f;
                    } else {
                        coef = (center - position) / 0.5f;
                    }
                    scale = coef * (MAX_SCALE - MIN_SCALE) + MIN_SCALE;
                }
                page.setScaleX(scale);
                page.setScaleY(scale);
            }
        });




        binding.catCloseImgae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.infoPopup.setVisibility(View.GONE);
            }
        });

        binding.infoPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.infoPopup.setVisibility(View.GONE);
            }
        });

        dateAndTimePicker = findViewById(R.id.single_day_picker_driver);
        SingleDateAndTimePicker.OnDateChangedListener changedList = (displayed, date) -> displays(displayed);
        dateAndTimePicker.addOnDateChangedListener(changedList);

        new SingleDateAndTimePickerDialog.Builder(context)
                .defaultDate(defaultDate)
                .display();


//        Date d = new Date();
//        CharSequence s  = DateFormat.format("MMMM d, yyyy ", d.getTime());
//        binding.datePicker.setText(s);
//        CharSequence time  = DateFormat.format("h:mm:ss", d.getTime());
//        binding.timePicker.setText(time);

        binding.addCheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!plan_date_time_schedule.isEmpty()) {
                    workmode();
                }else{
                    Toast.makeText(activity, "Select Date", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.extreriorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.extreriorCheck.setChecked(true);
            }
        });

        binding.interiorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.interiorCheck.setChecked(true);
            }
        });

        binding.polishingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.polisingCheck.setChecked(true);
            }
        });

        binding.waxCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.waxCheck.setChecked(true);
            }
        });

        binding.sanitizeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.sanitizeCheck.setChecked(true);
            }
        });

        binding.extreriorCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //service_type.add("ExteriorWash");
                    binding.botAmount.setVisibility(View.VISIBLE);
                    total_amount = price_exterior;
                    binding.totalAmount.setText("INR\n"+total_amount);
                }else if(!isChecked){
                    //service_type.clear();
                    binding.interiorCheck.setChecked(false);
                    binding.polisingCheck.setChecked(false);
                    binding.waxCheck.setChecked(false);
                    binding.sanitizeCheck.setChecked(false);
                    total_amount = "0";
                    binding.botAmount.setVisibility(View.GONE);
                }
            }
        });

        binding.interiorCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //service_type.add("InteriorCleaning");
                    binding.extreriorCheck.setChecked(true);
                    binding.botAmount.setVisibility(View.VISIBLE);
                    if(total_amount.equals("0")){
                        int num1 = (int) Double.parseDouble(price_exterior);
                        int num2 = (int) Double.parseDouble(price_interior);
                        int sum = num1 + num2;
                        total_amount = String.valueOf(sum);
                    }else{
                        int num1 = (int) Double.parseDouble(total_amount);
                        int num2 = (int) Double.parseDouble(price_interior);
                        int sum = num1 + num2;
                        total_amount = String.valueOf(sum);
                    }



                    binding.totalAmount.setText("INR\n"+total_amount);
                }else if(!isChecked){
                    //service_type.remove("InteriorCleaning");
                    int num1 = (int) Double.parseDouble(total_amount);
                    int num2 = (int) Double.parseDouble(price_interior);
                    int sum = num1 - num2;
                    total_amount = String.valueOf(sum);
                    binding.totalAmount.setText("INR\n"+total_amount);
                }
            }
        });

        binding.polisingCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //service_type.add("EnginePolishing");
                    binding.extreriorCheck.setChecked(true);
                    binding.botAmount.setVisibility(View.VISIBLE);
                    if(total_amount.equals("0")){
                        int num1 = (int) Double.parseDouble(price_exterior);
                        int num2 = (int) Double.parseDouble(price_polish);
                        int sum = num1 + num2;
                        total_amount = String.valueOf(sum);
                    }else{
                        int num1 = (int) Double.parseDouble(total_amount);
                        int num2 = (int) Double.parseDouble(price_polish);
                        int sum = num1 + num2;
                        total_amount = String.valueOf(sum);
                    }

                    binding.totalAmount.setText("INR\n"+total_amount);
                }else if(!isChecked){
                    //service_type.remove("EnginePolishing");
                    int num1 = (int) Double.parseDouble(total_amount);
                    int num2 = (int) Double.parseDouble(price_polish);
                    int sum = num1 - num2;
                    total_amount = String.valueOf(sum);
                    binding.totalAmount.setText("INR\n"+total_amount);
                }
            }
        });

        binding.waxCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //service_type.add("QuickWax");
                    binding.extreriorCheck.setChecked(true);
                    binding.botAmount.setVisibility(View.VISIBLE);
                    if(total_amount.equals("0")){
                        int num1 = (int) Double.parseDouble(price_exterior);
                        int num2 = (int) Double.parseDouble(price_wax);
                        int sum = num1 + num2;
                        total_amount = String.valueOf(sum);
                    }else{
                        int num1 = (int) Double.parseDouble(total_amount);
                        int num2 = (int) Double.parseDouble(price_wax);
                        int sum = num1 + num2;
                        total_amount = String.valueOf(sum);
                    }

                    binding.totalAmount.setText("INR\n"+total_amount);
                }else if(!isChecked){
                    //service_type.remove("QuickWax");
                    int num1 = (int) Double.parseDouble(total_amount);
                    int num2 = (int) Double.parseDouble(price_wax);
                    int sum = num1 - num2;
                    total_amount = String.valueOf(sum);
                    binding.totalAmount.setText("INR\n"+total_amount);
                }
            }
        });

        binding.sanitizeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //service_type.add("CarSanitisation");
                    binding.extreriorCheck.setChecked(true);
                    binding.botAmount.setVisibility(View.VISIBLE);
                    if(total_amount.equals("0")){
                        int num1 = (int) Double.parseDouble(price_exterior);
                        int num2 = (int) Double.parseDouble(price_sanitize);
                        int sum = num1 + num2;
                        total_amount = String.valueOf(sum);
                    }else{
                        int num1 = (int) Double.parseDouble(total_amount);
                        int num2 = (int) Double.parseDouble(price_sanitize);
                        int sum = num1 + num2;
                        total_amount = String.valueOf(sum);
                    }

                    binding.totalAmount.setText("INR\n"+total_amount);
                }else if(!isChecked){
                    //service_type.remove("CarSanitisation");
                    int num1 = (int) Double.parseDouble(total_amount);
                    int num2 = (int) Double.parseDouble(price_sanitize);
                    int sum = num1 - num2;
                    total_amount = String.valueOf(sum);
                    binding.totalAmount.setText("INR\n"+total_amount);
                }
            }
        });

        binding.buttonProcessed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("servicetype",""+//service_type.toString());
                Constant.ONETIME_CAR_PRICE = total_amount;
                Constant.ONETIME_SERVICE_TYPE = "Door step Wash";
                binding.schedulePopup.setVisibility(View.VISIBLE);

            }
        });

        binding.detailButtonProcessed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.ONETIME_CAR_PRICE = detail_total_amount;
                Constant.ONETIME_SERVICE_TYPE = "Door step Detailing";
                binding.schedulePopup.setVisibility(View.VISIBLE);
            }
        });

        binding.carpolishngCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.carpolishngCheck.setChecked(true);
            }
        });

        binding.interiordetalingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.intertiordetailingCheck.setChecked(true);
            }
        });

        binding.windowtintingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.windowtintingCheck.setChecked(true);
            }
        });

        binding.carpolishngCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.detailAmount.setVisibility(View.VISIBLE);
                    if(detail_total_amount.equals("0")){
                        detail_total_amount = price_carpolish;
                    }else{
                        int num1 = (int) Double.parseDouble(detail_total_amount);
                        int num2 = (int) Double.parseDouble(price_carpolish);
                        int sum = num1 + num2;
                        detail_total_amount = String.valueOf(sum);
                    }

                    binding.detailTotalAmount.setText("INR\n"+detail_total_amount);
                }else if(!isChecked){
                    int num1 = (int) Double.parseDouble(detail_total_amount);
                    int num2 = (int) Double.parseDouble(price_carpolish);
                    int sum = num1 - num2;
                    detail_total_amount = String.valueOf(sum);
                    binding.detailTotalAmount.setText("INR\n"+detail_total_amount);

                    if(detail_total_amount.equals("0") || detail_total_amount.equals("0.0")){
                        binding.detailAmount.setVisibility(View.GONE);
                    }else{
                        binding.detailAmount.setVisibility(View.VISIBLE);
                    }


                }
            }
        });

        binding.intertiordetailingCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.detailAmount.setVisibility(View.VISIBLE);
                    if(detail_total_amount.equals("0")){
                        detail_total_amount = price_interiordetail;
                    }else{
                        int num1 = (int) Double.parseDouble(detail_total_amount);
                        int num2 = (int) Double.parseDouble(price_interiordetail);
                        int sum = num1 + num2;
                        detail_total_amount = String.valueOf(sum);
                    }

                    binding.detailTotalAmount.setText("INR\n"+detail_total_amount);
                }else if(!isChecked){
                    int num1 = (int) Double.parseDouble(detail_total_amount);
                    int num2 = (int) Double.parseDouble(price_interiordetail);
                    int sum = num1 - num2;
                    detail_total_amount = String.valueOf(sum);
                    binding.detailTotalAmount.setText("INR\n"+detail_total_amount);

                    if(detail_total_amount.equals("0") || detail_total_amount.equals("0.0")){
                        binding.detailAmount.setVisibility(View.GONE);
                    }else{
                        binding.detailAmount.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        binding.windowtintingCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.detailAmount.setVisibility(View.VISIBLE);
                    if(detail_total_amount.equals("0")){
                        detail_total_amount = price_windowtint;
                    }else{
                        int num1 = (int) Double.parseDouble(detail_total_amount);
                        int num2 = (int) Double.parseDouble(price_windowtint);
                        int sum = num1 + num2;
                        detail_total_amount = String.valueOf(sum);
                    }

                    binding.detailTotalAmount.setText("INR\n"+detail_total_amount);
                }else if(!isChecked){
                    int num1 = (int) Double.parseDouble(detail_total_amount);
                    int num2 = (int) Double.parseDouble(price_windowtint);
                    int sum = num1 - num2;
                    detail_total_amount = String.valueOf(sum);
                    binding.detailTotalAmount.setText("INR\n"+detail_total_amount);

                    if(detail_total_amount.equals("0") || detail_total_amount.equals("0.0")){
                        binding.detailAmount.setVisibility(View.GONE);
                    }else{
                        binding.detailAmount.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        Places.initialize(this, getResources().getString(R.string.google_maps_key));


        binding.searchPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize place field
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent serach_intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(DoorStepServiceActivity.this);
                startActivityForResult(serach_intent,100);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(activity, "Result", Toast.LENGTH_SHORT).show();

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);

            LatLng latLng = place.getLatLng();

            latitude = latLng.latitude;
            longitude = latLng.longitude;

            //Moving the map
            mMap.clear();
            moveMap(false);

            String addressList= ApiConfig.getAddress(latitude, longitude, DoorStepServiceActivity.this);
            binding.placeTxt.setText(addressList);
            sessionManager.setData(SessionManager.MAP_ADDRESS,addressList);
            sessionManager.setData(SessionManager.MAP_LATITUDE,String.valueOf(latLng.latitude));
            sessionManager.setData(SessionManager.MAP_LONGITUDE,String.valueOf(latLng.longitude));
        }else if(resultCode == AutocompleteActivity.RESULT_ERROR ) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(activity, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void apicall() {
        new makeChat().execute();
    }

    private class makeChat extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            return null;
        }
        @SuppressLint("WrongThread")
        @Override
        protected void onPostExecute(String result) {
            try{

                String whatsapp_number = "918012981059";
                //String whatsapp_number = productsPost.get(pos).whatsappno;
                String message = "Content ";

                PackageManager packageManager = getPackageManager();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                String urlcopy = "https://api.whatsapp.com/send?phone="+ whatsapp_number +"&text=" + URLEncoder.encode(message, "UTF-8");
                intent.setPackage("com.whatsapp");
                intent.setData(Uri.parse(urlcopy));

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
                }else {
                    Thread thread = new Thread(){
                        public void run(){
                            Looper.prepare();//Call looper.prepare()

                            @SuppressLint("HandlerLeak") Handler mHandler = new Handler() {
                                public void handleMessage(Message msg) {
                                    Toast.makeText(activity, "Whatsapp not installed.", Toast.LENGTH_SHORT).show();
                                }
                            };

                            Looper.loop();
                        }
                    };
                    thread.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, "error da "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private void displays(String displayed) {
        Log.e("c_response=>>", displayed);
        plan_date_time_schedule = displayed;

        String planed_time = ApiConfig.parseDateToddMMyyyyHHMMAMPM(plan_date_time_schedule);
        Log.e("c_response=>>123456", planed_time);

        String dateOnly = ApiConfig.spiltDate(plan_date_time_schedule);
        Log.e("c_response=>>789456",""+ dateOnly);
        Constant.ONETIME_DATE = dateOnly;

        String timeOnly = ApiConfig.spiltTime(plan_date_time_schedule);
        Log.e("c_response=>>789456",""+ timeOnly);
        Constant.ONETIME_TIME = timeOnly;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.clear();
        LatLng latLng;
        latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        mMap.setMapType(mapType);
        mMap.setOnMarkerDragListener(this);

        mMap.setOnMapLongClickListener(this);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                latitude = latLng.latitude;
                longitude = latLng.longitude;

                //Moving the map
                mMap.clear();
                moveMap(false);
            }
        });
        // text.setText("Latitude - " + latitude + "\nLongitude - " + longitude);
        binding.placeTxt.setText(ApiConfig.getAddress(latitude, longitude, DoorStepServiceActivity.this));
    }


    private void getCurrentLocation() {

        if (ContextCompat.checkSelfPermission(DoorStepServiceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.location_permission))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(DoorStepServiceActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        0);

                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);

            }
        }

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations, this can be null.
                if (location != null) {
                    c_longitude = location.getLongitude();
                    c_latitude = location.getLatitude();
                    if (sessionManager.getCoordinates(SessionManager.KEY_LATITUDE).equals("0") || sessionManager.getCoordinates(SessionManager.KEY_LONGITUDE).equals("0")) {
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                    } else {
                        longitude = Double.parseDouble(sessionManager.getCoordinates(SessionManager.KEY_LONGITUDE));
                        latitude = Double.parseDouble(sessionManager.getCoordinates(SessionManager.KEY_LATITUDE));
                    }
                    moveMap(true);
                }
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void cLocation(){
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations, this can be null.
                if (location != null) {
                    c_longitude = location.getLongitude();
                    c_latitude = location.getLatitude();

                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    moveMap(true);
                }
            }
        });
    }

    private void moveMap(boolean isfirst) {


        LatLng latLng = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(getString(R.string.set_location)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        if (isfirst)
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        binding.placeTxt.setText(ApiConfig.getAddress(latitude, longitude, DoorStepServiceActivity.this));
        //  text.setText("Latitude - " + latitude + "\nLongitude - " + longitude);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        mMap.clear();
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        saveLocation(latitude, longitude);
        //Moving the map
        moveMap(false);

    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        mMap.clear();
        moveMap(false);
    }

    public void saveLocation(double latitude, double longitude) {
        String address_map = ApiConfig.getAddress(latitude, longitude, DoorStepServiceActivity.this);
        sessionManager.setData(SessionManager.MAP_ADDRESS,address_map);
        sessionManager.setData(SessionManager.MAP_LATITUDE,String.valueOf(latitude));
        sessionManager.setData(SessionManager.MAP_LONGITUDE,String.valueOf(longitude));
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //  saveLocation(latitude, longitude);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.getMapAsync(this);
    }

    public void OnLocationClick(View view) {
        onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showPopup(String what){
        if(!TextUtils.isEmpty(Constant.ONETIME_CAR_TYPE)){
            //Toast.makeText(activity, Constant.ONETIME_CAR_TYPE, Toast.LENGTH_SHORT).show();
            if(what.equalsIgnoreCase("carwash")) {
                binding.bottomWashPopup.setVisibility(View.VISIBLE);
                binding.carwashPopup.setVisibility(View.VISIBLE);
                Log.e("cartype", "" + Constant.ONETIME_CAR_TYPE);
                showCategoryCarWash(what);
            }if(what.equalsIgnoreCase("detailing")) {
                binding.bottomWashPopup.setVisibility(View.VISIBLE);
                binding.detailingPopup.setVisibility(View.VISIBLE);
                Log.e("cartype", "" + Constant.ONETIME_CAR_TYPE);
                showCategoryDetailing(what);
            }if(what.equalsIgnoreCase("painting")) {
                binding.bottomPaintPopup.setVisibility(View.VISIBLE);
            }if(what.equalsIgnoreCase("battery")) {
                binding.bottomBatteryPopup.setVisibility(View.VISIBLE);
            }
        }else{
            Toast.makeText(activity, "Select vehicle first", Toast.LENGTH_SHORT).show();
        }

    }


    private void work() {
        if (isNetworkAvailable()){
            showVechile();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(DoorStepServiceActivity.this);
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

    public void showVechile(){
        //binding.vehiclePopup.setVisibility(View.VISIBLE);
        final KProgressHUD hud = KProgressHUD.create(DoorStepServiceActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<VehicleDetails> call = apiInterface.myvehicledetails(customerid,token);

        call.enqueue(new Callback<VehicleDetails>() {
            @Override
            public void onResponse(Call<VehicleDetails> call, Response<VehicleDetails> response) {
                final VehicleDetails vehicleDetails = response.body();
                hud.dismiss();
                if (vehicleDetails.code.equalsIgnoreCase("200")) {
                    Gson gson = new Gson();
                    String json = gson.toJson(vehicleDetails);
                    binding.vehicleRv.setVisibility(View.VISIBLE);

                    MapVehileAdapter mainAdapter = new MapVehileAdapter(DoorStepServiceActivity.this,vehicleDetails.details);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DoorStepServiceActivity.this,LinearLayoutManager.HORIZONTAL,false);
                    binding.vehicleRv.setLayoutManager(linearLayoutManager);
                    binding.vehicleRv.setAdapter(mainAdapter);
                }else if (vehicleDetails.code.equalsIgnoreCase("201")) {
                    binding.vehicleRv.setVisibility(View.GONE);
                }else if (vehicleDetails.code.equalsIgnoreCase("203")) {
                    sessionManager.logoutUsers();
                }
            }
            @Override
            public void onFailure(Call<VehicleDetails> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(DoorStepServiceActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showCategoryCarWash(String action) {
        final KProgressHUD hud = KProgressHUD.create(DoorStepServiceActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<DoorStepCarWash> call = apiInterface.doorStepService(action +"");
        call.enqueue(new Callback<DoorStepCarWash>() {
            @Override
            public void onResponse(Call<DoorStepCarWash> call, Response<DoorStepCarWash> response) {
                final DoorStepCarWash doorStepCarWash = response.body();
                hud.dismiss();
                if (doorStepCarWash.code.equalsIgnoreCase("200")) {
                    Gson gson = new Gson();
                    String json = gson.toJson(doorStepCarWash);

                    binding.extreriorInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.infoPopup.setVisibility(View.VISIBLE);
                            binding.catImage.setImageResource(R.drawable.exterior_wash);
                            binding.catDescription.setText(Html.fromHtml(doorStepCarWash.Exterior_Wash.description));
                        }
                    });

                    binding.interiorInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.infoPopup.setVisibility(View.VISIBLE);
                            binding.catDescription.setText(Html.fromHtml(doorStepCarWash.Interior_Cleaning.description));
                        }
                    });

                    binding.polishingInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.infoPopup.setVisibility(View.VISIBLE);
                            binding.catDescription.setText(Html.fromHtml(doorStepCarWash.Engine_Polishing.description));
                        }
                    });

                    binding.waxInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.infoPopup.setVisibility(View.VISIBLE);
                            binding.catDescription.setText(Html.fromHtml(doorStepCarWash.Quick_Wax.description));
                        }
                    });

                    binding.sanitizeInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.infoPopup.setVisibility(View.VISIBLE);
                            binding.catDescription.setText(Html.fromHtml(doorStepCarWash.Car_Sanitisation.description));
                        }
                    });

                    List<DoorStepCarWash.Exterior_Wash.Service> list = new ArrayList<>();
                    list = doorStepCarWash.Exterior_Wash.service;

                    List<DoorStepCarWash.Interior_Cleaning.Service> list1 = new ArrayList<>();
                    list1 = doorStepCarWash.Interior_Cleaning.service;

                    List<DoorStepCarWash.Engine_Polishing.Service> list2 = new ArrayList<>();
                    list2 = doorStepCarWash.Engine_Polishing.service;

                    List<DoorStepCarWash.Quick_Wax.Service> list3 = new ArrayList<>();
                    list3 = doorStepCarWash.Quick_Wax.service;

                    List<DoorStepCarWash.Car_Sanitisation.Service> list4 = new ArrayList<>();
                    list4 = doorStepCarWash.Car_Sanitisation.service;



                    for(int i = 0 ; i < list.size() ; i++) {
                        if (Constant.ONETIME_CAR_TYPE.equalsIgnoreCase(list.get(i).type)){
                            binding.extreriorAmt.setText(list.get(i).prices);
                            price_exterior = list.get(i).prices;
                        }
                    }

                    for(int i = 0 ; i < list1.size() ; i++) {
                        if (Constant.ONETIME_CAR_TYPE.equalsIgnoreCase(list1.get(i).type)){
                            binding.interiorAmt.setText(list1.get(i).prices);
                            price_interior = list1.get(i).prices;
                        }
                    }

                    for(int i = 0 ; i < list2.size() ; i++) {
                        if (Constant.ONETIME_CAR_TYPE.equalsIgnoreCase(list2.get(i).type)){
                            binding.polishingAmt.setText(list2.get(i).prices);
                            price_polish = list2.get(i).prices;
                        }
                    }

                    for(int i = 0 ; i < list3.size() ; i++) {
                        if (Constant.ONETIME_CAR_TYPE.equalsIgnoreCase(list3.get(i).type)){
                            binding.waxAmt.setText(list3.get(i).prices);
                            price_wax = list3.get(i).prices;
                        }
                    }

                    for(int i = 0 ; i < list4.size() ; i++) {
                        if (Constant.ONETIME_CAR_TYPE.equalsIgnoreCase(list4.get(i).type)){
                            binding.sanitizeAmt.setText(list4.get(i).prices);
                            price_sanitize = list4.get(i).prices;
                        }
                    }




                }
            }
            @Override
            public void onFailure(Call<DoorStepCarWash> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(DoorStepServiceActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showCategoryDetailing(String action) {
        final KProgressHUD hud = KProgressHUD.create(DoorStepServiceActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<DoorStepCarWash> call = apiInterface.doorStepService(action +"");
        call.enqueue(new Callback<DoorStepCarWash>() {
            @Override
            public void onResponse(Call<DoorStepCarWash> call, Response<DoorStepCarWash> response) {
                final DoorStepCarWash doorStepCarWash = response.body();
                hud.dismiss();
                if (doorStepCarWash.code.equalsIgnoreCase("200")) {
                    Gson gson = new Gson();
                    String json = gson.toJson(doorStepCarWash);

                    binding.carpolisingInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.infoPopup.setVisibility(View.VISIBLE);
                            binding.catDescription.setText(Html.fromHtml(doorStepCarWash.Car_Polishing.description));
                        }
                    });

                    binding.interiordetalingInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.infoPopup.setVisibility(View.VISIBLE);
                            binding.catDescription.setText(Html.fromHtml(doorStepCarWash.Interior_Detailing.description));
                        }
                    });

                    binding.wingowtintingInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.infoPopup.setVisibility(View.VISIBLE);
                            binding.catDescription.setText(Html.fromHtml(doorStepCarWash.window_tinting.description));
                        }
                    });


                    List<DoorStepCarWash.Car_Polishing.Service> list = new ArrayList<>();
                    list = doorStepCarWash.Car_Polishing.service;

                    List<DoorStepCarWash.Interior_Detailing.Service> list1 = new ArrayList<>();
                    list1 = doorStepCarWash.Interior_Detailing.service;

                    List<DoorStepCarWash.window_tinting.Service> list2 = new ArrayList<>();
                    list2 = doorStepCarWash.window_tinting.service;



                    for(int i = 0 ; i < list.size() ; i++) {
                        if (Constant.ONETIME_CAR_TYPE.equalsIgnoreCase(list.get(i).type)){
                            binding.carpolisingAmt.setText(list.get(i).prices);
                            price_carpolish = list.get(i).prices;
                        }
                    }

                    for(int i = 0 ; i < list1.size() ; i++) {
                        if (Constant.ONETIME_CAR_TYPE.equalsIgnoreCase(list1.get(i).type)){
                            binding.interiordetalingAmt.setText(list1.get(i).prices);
                            price_interiordetail = list1.get(i).prices;
                        }
                    }

                    for(int i = 0 ; i < list2.size() ; i++) {
                        if (Constant.ONETIME_CAR_TYPE.equalsIgnoreCase(list2.get(i).type)){
                            binding.wingowtintingAmt.setText(list2.get(i).prices);
                            price_windowtint = list2.get(i).prices;
                        }
                    }


                }
            }
            @Override
            public void onFailure(Call<DoorStepCarWash> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(DoorStepServiceActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void workmode() {
        if (isNetworkAvailable()){
            getPayMode();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(DoorStepServiceActivity.this);
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

    private void getPayMode() {
        final KProgressHUD hud = KProgressHUD.create(DoorStepServiceActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getMode();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body();
                hud.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                        Gson gson = new Gson();

                        Constant.RAZOR_PAY_KEY_SECRET = jsonObject.optString("secretkey");
                        Constant.RAZOR_PAY_KEY_VALUE = jsonObject.optString("keyid");

                        createOrderId();


                        //startwashonetimepayment();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(DoorStepServiceActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createOrderId(){
        //int amount = total * 100;
        final KProgressHUD hud = KProgressHUD.create(DoorStepServiceActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.createOrderId("create_orderid",Constant.ONETIME_CAR_PRICE +"");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body();
                hud.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                        Gson gson = new Gson();

                        Constant.RAZOR_PAY_ORDER_ID = jsonObject.optString("rzp_order_id");

                        createTempOrder();
                        //startwashonetimepayment();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(DoorStepServiceActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createTempOrder() {
        final KProgressHUD hud = KProgressHUD.create(DoorStepServiceActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.tempOrderDoorStep("temp_order",
                Constant.RAZOR_PAY_ORDER_ID+"",
                customerid+"",
                token+"",
                Constant.ONETIME_PACK_TYPE +"",
                Constant.ONETIME_CAR_PRICE +"",
                Constant.ONETIME_CAR_ID +"",
                Constant.ONETIME_SERVICE_TYPE +"",
                Constant.ONETIME_CAR_PRICE +"",
                Constant.ONETIME_DATE +"",
                Constant.ONETIME_TIME +"",
                "onetime_payment",
                sessionManager.getData(SessionManager.MAP_ADDRESS) +"",
                sessionManager.getData(SessionManager.MAP_LATITUDE) +"",
                sessionManager.getData(SessionManager.MAP_LONGITUDE) +"");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body();
                hud.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                        Gson gson = new Gson();
                        tempOrderSuccess();
                    }else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                        Toast.makeText(DoorStepServiceActivity.this,jsonObject.optString("result"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(DoorStepServiceActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tempOrderSuccess() {
        startwashonetimepayment();
    }

    public void startwashonetimepayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID(Constant.RAZOR_PAY_KEY_VALUE);
        checkout.setImage(R.drawable.logo5234);
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("name", "Carro Care");
            jSONObject.put("description", "");
            jSONObject.put("order_id", Constant.RAZOR_PAY_ORDER_ID);
            jSONObject.put("currency", "INR");
            int i = Integer.parseInt(Constant.ONETIME_CAR_PRICE);
            Log.e("AMOUNTRZP", String.valueOf(i));
            jSONObject.put("amount", i);
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("email", custemail);
            jSONObject2.put("contact", custmob);
            jSONObject.put("prefill", jSONObject2);
            checkout.open(this, jSONObject);
            Log.e("OPTIONS", jSONObject.toString());
        } catch (Exception e) {
            Log.d("PaymentOptionActivity", "Error in starting Razorpay Checkout", e);
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @SuppressLint("LongLogTag")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            razorpayid = razorpayPaymentID;
            Log.e("razorpayPaymentID>>>>>>>>>>>>>>>>>>>>>>>>>  :  ",""+razorpayid);
            Log.e("razorpaytempID>>>>>>>>>>>>>>>>>>>>>>>>>  :  ",""+Constant.RAZOR_PAY_ORDER_ID);
            workPlaceOrder(razorpayid);
        } catch (Exception e) {
            Log.e("TAG onPaymentSuccess  ", e.getMessage());
        }
    }

    @Override
    public void onPaymentError(int i, String response) {
        try {
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
            Log.e("TAG onPaymentError  ", response);
        } catch (Exception e) {
            Log.e("TAG onPaymentError  ", e.getMessage());
        }
    }

    private void workPlaceOrder(final String razorpayid) {
        if (isNetworkAvailable()) {
            final KProgressHUD hud = KProgressHUD.create(DoorStepServiceActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.saveOrderDoorStep("onetime_payment",
                    razorpayid+"",
                    Constant.RAZOR_PAY_ORDER_ID +"",
                    customerid+"",
                    token+"",
                    Constant.ONETIME_PACK_TYPE +"",
                    Constant.ONETIME_CAR_PRICE +"",
                    Constant.ONETIME_CAR_ID +"",
                    Constant.ONETIME_SERVICE_TYPE +"",
                    Constant.ONETIME_CAR_PRICE +"",
                    Constant.ONETIME_DATE +"",
                    Constant.ONETIME_TIME +"",
                    sessionManager.getData(SessionManager.MAP_ADDRESS) +"",
                    sessionManager.getData(SessionManager.MAP_LATITUDE) +"",
                    sessionManager.getData(SessionManager.MAP_LONGITUDE) +"");
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonElement jsonElement = response.body();
                    hud.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            Toast.makeText(DoorStepServiceActivity.this,jsonObject.optString("result"),Toast.LENGTH_SHORT).show();
                            Log.e("payresponse",""+jsonObject.optString("result"));
                            Intent intent = new Intent(DoorStepServiceActivity.this,CongratsActivity.class);
                            startActivity(intent);
                            finish();
                        }else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                            Toast.makeText(DoorStepServiceActivity.this,jsonObject.optString("result"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    hud.dismiss();
                    Toast.makeText(DoorStepServiceActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(DoorStepServiceActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection." );
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Ok".
                    workPlaceOrder(razorpayid);
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


}