package com.muvierecktech.carrocare.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.DoorstepServiceAdapter;
import com.muvierecktech.carrocare.adapter.MapVehileAdapter;
import com.muvierecktech.carrocare.adapter.PagerAdapter;
import com.muvierecktech.carrocare.adapter.ViewpagerAdapter;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
    ViewPager2 viewPager2;
    ViewpagerAdapter viewpagerAdapter;
    List<PagerModel> pagerModels;
    PagerAdapter pagerAdapter;
    @SuppressLint("RestrictedApi")
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    Integer color[] = null;

    final int paddingPx = 200;
    final float MIN_SCALE = 0.8f;
    final float MAX_SCALE = 1f;

    String token,customerid;
    SessionManager sessionManager;
    private String plan_date_time_schedule = "";
    private String comparedate = "";
    private String today_date = "";
    private Date defaultDate;
    String custmob,custemail,razorpayid;
    public boolean carCheck = false ;

    public ArrayList<String> service_type;

    public String price_exterior, price_interior, price_polish, price_wax, price_sanitize, total_amount = "0";
    String price_carpolish, price_interiordetail, price_windowtint, detail_total_amount = "0";

    Activity activity;
    Context context;
    String pay_type = "";
    String gstamount;

    DatePickerDialog picker;

    public LinearLayout botAmount;
    public TextView totalAmount;
    public Button button_processed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_door_step_service);

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
        custmob = hashMap.get(SessionManager.KEY_USERMOBILE);
        custemail = hashMap.get(SessionManager.KEY_USEREMAIL);

        work();

        binding.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoorStepServiceActivity.this,MainProfileActivity.class);
                startActivity(intent);
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

        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.LOAD_FROM = "add";
                startActivity(new Intent(DoorStepServiceActivity.this,MapAddVechileActivity.class));
                finish();
            }
        });



        pagerModels = new ArrayList<>();
        pagerModels.add(new PagerModel(R.drawable.ds_carwash,"Door step car wash"));
        pagerModels.add(new PagerModel(R.drawable.ds_detailing,"Detailing"));
        pagerModels.add(new PagerModel(R.drawable.ds_paint,"Painting & Denting"));
        // pagerModels.add(new PagerModel(R.drawable.home_extra_int,"Door step extra interior"));
        // pagerModels.add(new PagerModel(R.drawable.home_machine_polish,"Door step car polish"));
        pagerModels.add(new PagerModel(R.drawable.ds_insurance,"Door step insurance"));
        pagerModels.add(new PagerModel(R.drawable.ds_batterychange,"Battery change"));

        pagerAdapter = new PagerAdapter(pagerModels,this);


        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setClipToPadding(false);
        //viewPager.setPageMargin((int) (getResources().getDisplayMetrics().widthPixels * -0.33));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPadding(150, 0, 150, 0);
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                /*float pagerWidthPx = ((ViewPager) page.getParent()).getWidth();
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
                page.setScaleY(scale);*/
                page.setScaleX(0.7f - Math.abs(position * 0.4f));
                page.setScaleY(0.8f - Math.abs(position * 0.6f));
                page.setAlpha(1.0f - Math.abs(position * 0.5f));
            }
        });

        viewPager2 = findViewById(R.id.viewpager2);
        viewpagerAdapter = new ViewpagerAdapter(pagerModels,this, viewPager2);
        viewPager2.setAdapter(viewpagerAdapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setPadding(250, 0, 250, 0);
        ///viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer transformer = new CompositePageTransformer();
        //transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float a = 1 - Math.abs(position);
                page.setScaleY(0.85f + a * 0.15f);
            }
        });

        viewPager2.setPageTransformer(transformer);

        /*Places.initialize(this, getResources().getString(R.string.google_maps_key));*/


        binding.searchPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize place field
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent serach_intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(DoorStepServiceActivity.this);
                startActivityForResult(serach_intent,100);
            }
        });

        getTodayDate();
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
            }
        }

        @Override
        protected void onPreExecute() {
        }
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
        saveLocation(latitude, longitude);
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
                Log.e("cartype", "" + Constant.ONETIME_CAR_TYPE);
                //carWash();
                getDoorstepService("carwash");
            }if(what.equalsIgnoreCase("detailing")) {
                Log.e("cartype", "" + Constant.ONETIME_CAR_TYPE);
                //detailing();
                getDoorstepService("detailing");
            }if(what.equalsIgnoreCase("painting")) {
                painting();
            }if(what.equalsIgnoreCase("battery")) {
                battery();
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
                hud.dismiss();
                try{
                    if(response.isSuccessful()){
                        final VehicleDetails vehicleDetails = response.body();
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
                    } else{
                        ApiConfig.responseToast(DoorStepServiceActivity.this, response.code());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<VehicleDetails> call, Throwable t) {
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
                hud.dismiss();
                try {
                    if(response.isSuccessful()){
                        JsonElement jsonElement = response.body();
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                            Gson gson = new Gson();

                            Constant.RAZOR_PAY_KEY_SECRET = jsonObject.optString("secretkey");
                            Constant.RAZOR_PAY_KEY_VALUE = jsonObject.optString("keyid");

                            createOrderId();


                            //startwashonetimepayment();

                        }
                    } else{
                        ApiConfig.responseToast(DoorStepServiceActivity.this, response.code());
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
        Call<JsonObject> call = apiInterface.createOrderId("create_orderid",Constant.ONETIME_CAR_FINAL_PRICE +"");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                hud.dismiss();
                try {
                    if(response.isSuccessful()){
                        JsonElement jsonElement = response.body();
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                            Gson gson = new Gson();

                            Constant.RAZOR_PAY_ORDER_ID = jsonObject.optString("rzp_order_id");

                            createTempOrder();
                            //startwashonetimepayment();

                        }
                    } else{
                        ApiConfig.responseToast(DoorStepServiceActivity.this, response.code());
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
                Constant.ONETIME_CAR_SUB_TOTAL +"",
                sessionManager.getData(SessionManager.GST_PERCENTAGE) +"",
                Constant.ONETIME_CAR_GST_AMOUNT +"",
                Constant.ONETIME_CAR_FINAL_PRICE +"",
                Constant.ONETIME_DATE +"",
                Constant.ONETIME_TIME +"",
                "onetime_payment",
                sessionManager.getData(SessionManager.MAP_ADDRESS) +"",
                sessionManager.getData(SessionManager.MAP_LATITUDE) +"",
                sessionManager.getData(SessionManager.MAP_LONGITUDE) +"");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                hud.dismiss();
                try {
                    if(response.isSuccessful()){
                        JsonElement jsonElement = response.body();
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            tempOrderSuccess();
                        }else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                            Toast.makeText(DoorStepServiceActivity.this,jsonObject.optString("result"),Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        ApiConfig.responseToast(DoorStepServiceActivity.this, response.code());
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
            int i = Integer.parseInt(Constant.ONETIME_CAR_FINAL_PRICE);
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
//            Intent intent = new Intent(DoorStepServiceActivity.this,PaymentSucessActivity.class);
//            intent.putExtra("status","failure");
//            intent.putExtra("type","online");
//            intent.putExtra("amount",Constant.ONETIME_CAR_PRICE);
//            startActivity(intent);
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
                    Constant.ONETIME_CAR_FINAL_PRICE +"",
                    sessionManager.getData(SessionManager.GST_PERCENTAGE) +"",
                    Constant.ONETIME_CAR_GST_AMOUNT +"",
                    Constant.ONETIME_CAR_SUB_TOTAL +"",
                    Constant.ONETIME_DATE +"",
                    Constant.ONETIME_TIME +"",
                    sessionManager.getData(SessionManager.MAP_ADDRESS) +"",
                    sessionManager.getData(SessionManager.MAP_LATITUDE) +"",
                    sessionManager.getData(SessionManager.MAP_LONGITUDE) +"");
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    hud.dismiss();
                    try {
                        if(response.isSuccessful()){
                            JsonElement jsonElement = response.body();
                            JSONObject jsonObject = new JSONObject(jsonElement.toString());
                            if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                                Gson gson = new Gson();
                                Toast.makeText(DoorStepServiceActivity.this,jsonObject.optString("result"),Toast.LENGTH_SHORT).show();
                                Log.e("payresponse",""+jsonObject.optString("result"));
                                Intent intent = new Intent(DoorStepServiceActivity.this,PaymentSucessActivity.class);
                                intent.putExtra("status","success");
                                intent.putExtra("type","online");
                                intent.putExtra("amount",Constant.ONETIME_CAR_FINAL_PRICE);
                                startActivity(intent);
                                finish();
                            }else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                                Toast.makeText(DoorStepServiceActivity.this,jsonObject.optString("result"),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DoorStepServiceActivity.this,PaymentSucessActivity.class);
                                intent.putExtra("status","failure");
                                intent.putExtra("type","online");
                                intent.putExtra("amount",Constant.ONETIME_CAR_FINAL_PRICE);
                                startActivity(intent);
                            }
                        } else{
                            ApiConfig.responseToast(DoorStepServiceActivity.this, response.code());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    hud.dismiss();
                    Log.e("123456",""+t.getMessage());
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

    private void workPlaceOrderCOD() {
        if (isNetworkAvailable()) {
            final KProgressHUD hud = KProgressHUD.create(DoorStepServiceActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.saveOrderDoorStepCOD("doorstep_payment",
                    customerid+"",
                    token+"",
                    Constant.ONETIME_PACK_TYPE +"",
                    Constant.ONETIME_CAR_PRICE +"",
                    Constant.ONETIME_CAR_ID +"",
                    Constant.ONETIME_SERVICE_TYPE +"",
                    Constant.ONETIME_CAR_SUB_TOTAL +"",
                    sessionManager.getData(SessionManager.GST_PERCENTAGE) +"",
                    Constant.ONETIME_CAR_GST_AMOUNT +"",
                    Constant.ONETIME_CAR_FINAL_PRICE +"",
                    Constant.ONETIME_DATE +"",
                    Constant.ONETIME_TIME +"",
                    sessionManager.getData(SessionManager.MAP_ADDRESS) +"",
                    sessionManager.getData(SessionManager.MAP_LATITUDE) +"",
                    sessionManager.getData(SessionManager.MAP_LONGITUDE) +"");
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    hud.dismiss();
                    try {
                        if(response.isSuccessful()){
                            JsonElement jsonElement = response.body();
                            JSONObject jsonObject = new JSONObject(jsonElement.toString());
                            if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                                Gson gson = new Gson();
                                Toast.makeText(DoorStepServiceActivity.this,jsonObject.optString("result"),Toast.LENGTH_SHORT).show();
                                Log.e("payresponse",""+jsonObject.optString("result"));
                                Intent intent = new Intent(DoorStepServiceActivity.this,PaymentSucessActivity.class);
                                intent.putExtra("status","success");
                                intent.putExtra("type","cod");
                                intent.putExtra("amount",Constant.ONETIME_CAR_FINAL_PRICE);
                                startActivity(intent);
                                finish();
                            }else if (jsonObject.optString("code").equalsIgnoreCase("201")) {
                                Toast.makeText(DoorStepServiceActivity.this,jsonObject.optString("result"),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DoorStepServiceActivity.this,PaymentSucessActivity.class);
                                intent.putExtra("status","failure");
                                intent.putExtra("type","cod");
                                intent.putExtra("amount",Constant.ONETIME_CAR_FINAL_PRICE);
                                startActivity(intent);
                            }
                        } else{
                            ApiConfig.responseToast(DoorStepServiceActivity.this, response.code());
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

    private void getTodayDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date cal = calendar.getTime();
        today_date = df.format(cal);
        comparedate = df.format(cal);
    }

    public void getDoorstepService(String action){
        if (isNetworkAvailable()) {
            final KProgressHUD hud = KProgressHUD.create(DoorStepServiceActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<DoorStepCarWash> call = apiInterface.doorStepService(action +"", Constant.ONETIME_CAR_TYPE+"");
            call.enqueue(new Callback<DoorStepCarWash>() {
                @Override
                public void onResponse(Call<DoorStepCarWash> call, Response<DoorStepCarWash> response) {
                    hud.dismiss();
                    try{
                        if(response.isSuccessful()){
                            final DoorStepCarWash doorStepCarWash = response.body();
                            if (doorStepCarWash.code.equalsIgnoreCase("200")) {
                                Gson gson = new Gson();
                                String json = gson.toJson(doorStepCarWash);

                                DoorstepServiceAdapter doorstepServiceAdapter = new DoorstepServiceAdapter(DoorStepServiceActivity.this,doorStepCarWash.services,action);

                                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DoorStepServiceActivity.this, R.style.BottomSheetDialogTheme);
                                bottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                View bottomview = LayoutInflater.from(getApplicationContext())
                                        .inflate(R.layout.layout_bottom_carwash,
                                                (RelativeLayout)findViewById(R.id.carwashSheet));
                                //bottomSheetDialog.setCancelable(false);
                                bottomview.findViewById(R.id.close_popup).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        bottomSheetDialog.dismiss();
                                    }
                                });

                                botAmount = bottomview.findViewById(R.id.bot_amount);
                                totalAmount = bottomview.findViewById(R.id.totalAmount);
                                button_processed = bottomview.findViewById(R.id.button_processed);
                                RecyclerView servive_rv = bottomview.findViewById(R.id.servive_rv);

                                servive_rv.setAdapter(doorstepServiceAdapter);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(DoorStepServiceActivity.this,2);
                                servive_rv.setLayoutManager(gridLayoutManager);


                                bottomSheetDialog.setContentView(bottomview);
                                bottomSheetDialog.show();


                            }
                        } else{
                            ApiConfig.responseToast(DoorStepServiceActivity.this, response.code());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<DoorStepCarWash> call, Throwable t) {
                    hud.dismiss();
                    Toast.makeText(DoorStepServiceActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
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
                    getDoorstepService(action);
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

    private void painting() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DoorStepServiceActivity.this, R.style.BottomSheetDialogTheme);
        View bottomview = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.layout_bottom_painting,
                        (RelativeLayout) findViewById(R.id.paintingsheet));
        //bottomSheetDialog.setCancelable(false);
        bottomview.findViewById(R.id.close_paint_popup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomview.findViewById(R.id.btn_paint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                Constant.ONETIME_ADDITIONAL = "Painting";
                //apicall();

                try{

                    String whatsapp_number = "+917904015630";
                    String message = "Hi Carrocare team.\n I want more information about Doorstep painting";
                    PackageManager packageManager = getApplicationContext().getPackageManager();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    String url = "https://api.whatsapp.com/send?phone="+ whatsapp_number +"&text=" + URLEncoder.encode(message, "UTF-8");
                    i.setPackage(isAppInstalled());
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i);
                    }else {
                        Toast.makeText(DoorStepServiceActivity.this, "Whatsapp not installed.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                String phoneNumberWithCountryCode = "+917904015630";
//
//
//                boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
//                if (isWhatsappInstalled) {
//                    startActivity(
//                            new Intent(Intent.ACTION_VIEW,
//                                    Uri.parse(
//                                            String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message)
//                                    )
//                            )
//                    );
//                }else {
//                    Toast.makeText(DoorStepServiceActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
////                    Uri uri = Uri.parse("market://details?id=com.whatsapp");
////                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
////                    startActivity(goToMarket);
//                }
            }
        });



        bottomSheetDialog.setContentView(bottomview);
        bottomSheetDialog.show();
    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private void battery() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DoorStepServiceActivity.this, R.style.BottomSheetDialogTheme);
        View bottomview = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.layout_bottom_battery,
                        (RelativeLayout) findViewById(R.id.batterysheet));
        //bottomSheetDialog.setCancelable(false);
        bottomview.findViewById(R.id.close_battery_popup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomview.findViewById(R.id.btn_battery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.ONETIME_ADDITIONAL = "battery";
                //apicall();
                bottomSheetDialog.dismiss();

                try{

                    String whatsapp_number = "+917904015630";
                    String message = "Hi Carrocare team.\n I want more information about Doorstep battery changing";
                    PackageManager packageManager = getApplicationContext().getPackageManager();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    String url = "https://api.whatsapp.com/send?phone="+ whatsapp_number +"&text=" + URLEncoder.encode(message, "UTF-8");
                    i.setPackage(isAppInstalled());
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i);
                    }else {
                        Toast.makeText(DoorStepServiceActivity.this, "Whatsapp not installed.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                String phoneNumberWithCountryCode = "+917904015630";
//                String message = "Hi Carrocare team.\n I want more information about Doorstep battery changing";
//
//                boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
//                if (isWhatsappInstalled) {
//                    startActivity(
//                            new Intent(Intent.ACTION_VIEW,
//                                    Uri.parse(
//                                            String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message)
//                                    )
//                            )
//                    );
//                }else {
//                    Toast.makeText(DoorStepServiceActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
////                    Uri uri = Uri.parse("market://details?id=com.whatsapp");
////                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
////                    startActivity(goToMarket);
//                }
            }
        });

        bottomSheetDialog.setContentView(bottomview);
        bottomSheetDialog.show();
    }

    private String isAppInstalled() {
        String app_installed = null;
        PackageManager packageManager = getApplicationContext().getPackageManager();
        for (PackageInfo packageInfo : packageManager.getInstalledPackages(0)) {
            if(packageInfo.packageName.equals("com.whatsapp.w4b")){
                app_installed ="com.whatsapp.w4b";
            } else if (packageInfo.packageName.equals("com.whatsapp")) {
                app_installed ="com.whatsapp";
            }
            return app_installed;
        }
        return app_installed;
    }

    public void pickDateTime() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DoorStepServiceActivity.this, R.style.BottomSheetDialogTheme);
        View bottomview = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.layout_bottom_datepicker,
                        (RelativeLayout)findViewById(R.id.timerSheet));
        //bottomSheetDialog.setCancelable(false);
        bottomview.findViewById(R.id.close_popup_schedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        SingleDateAndTimePicker dateAndTimePicker;
        dateAndTimePicker = bottomview.findViewById(R.id.single_day_picker_driver);
        SingleDateAndTimePicker.OnDateChangedListener changedList = (displayed, date) -> displays(displayed);
        dateAndTimePicker.addOnDateChangedListener(changedList);

        new SingleDateAndTimePickerDialog.Builder(context)
                .defaultDate(defaultDate)
                .display();

        Button checkout;
        checkout = bottomview.findViewById(R.id.add_checkout_btn);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (plan_date_time_schedule.isEmpty()){
                    Toast.makeText(activity, "Select Date", Toast.LENGTH_SHORT).show();
                }else if(comparedate.equals(today_date)){
                    Toast.makeText(activity, "Select different date", Toast.LENGTH_SHORT).show();
                }else{
                    if(!TextUtils.isEmpty(sessionManager.getData(SessionManager.MAP_ADDRESS))){
                        payType();
                    }else{
                        Toast.makeText(activity, "Please select service location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        bottomSheetDialog.setContentView(bottomview);
        bottomSheetDialog.show();

    }

    public void showDateAndTime(){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(DoorStepServiceActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Constant.ONETIME_DATE = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(DoorStepServiceActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(android.widget.TimePicker timePicker, int selectedHour, int selectedMinute) {

                                Time time = new Time(selectedHour, selectedMinute, 0);
                                //little h uses 12 hour format and big H uses 24 hour format
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                //format takes in a Date, and Time is a sublcass of Date
                                String choosetime = simpleDateFormat.format(time);

                                Constant.ONETIME_TIME = selectedHour + ":" + selectedMinute + ":" + "00";

                                if(!TextUtils.isEmpty(sessionManager.getData(SessionManager.MAP_ADDRESS))){
                                    payType();
                                }else{
                                    Toast.makeText(activity, "Please select service location", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, hour, minute, false);
                        timePickerDialog.show();


                    }
                }, year, month, day);
        //picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        picker.getDatePicker().setMinDate(System.currentTimeMillis()+24*60*60*1000);
        picker.show();
    }

    private void payType() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DoorStepServiceActivity.this, R.style.BottomSheetDialogTheme);
        View bottomview = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.layout_bottom_checkout,
                        (RelativeLayout)findViewById(R.id.paysheet));
        //bottomSheetDialog.setCancelable(false);
        bottomview.findViewById(R.id.close_popup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        TextView subTotal, Percentage, Tax, Total;
        subTotal = bottomview.findViewById(R.id.sub_total);
        Total = bottomview.findViewById(R.id.total_amount);
        Percentage = bottomview.findViewById(R.id.tax_percentage_edt);
        Tax = bottomview.findViewById(R.id.taxtext);

        Constant.ONETIME_CAR_PRICE = total_amount;
        subTotal.setText("₹ "+Constant.ONETIME_CAR_PRICE);
        Percentage.setText("Taxes ("+sessionManager.getData(SessionManager.GST_PERCENTAGE)+"%)");
        int before_tax = Integer.parseInt(Constant.ONETIME_CAR_PRICE);

        int taxAmt = ((Integer.parseInt(sessionManager.getData(SessionManager.GST_PERCENTAGE)) * before_tax) / 100);
        gstamount = String.valueOf(taxAmt);
        int finalAmt = taxAmt + before_tax;
        Tax.setText("₹ "+taxAmt);
        Total.setText("₹ "+finalAmt);
        Constant.ONETIME_CAR_SUB_TOTAL = String.valueOf(before_tax);
        Constant.ONETIME_CAR_GST_AMOUNT = String.valueOf(taxAmt);
        Constant.ONETIME_CAR_FINAL_PRICE = String.valueOf(finalAmt);

        RadioButton cod, online;
        cod = bottomview.findViewById(R.id.cod_check);
        online = bottomview.findViewById(R.id.online_check);

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cod.isSelected()){
                    cod.setSelected(false);
                    cod.setChecked(false);
                    pay_type = "";
                }else{
                    cod.setSelected(true);
                    cod.setChecked(true);
                    online.setChecked(false);
                    pay_type = "cod";
                }
            }
        });

        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(online.isSelected()){
                    online.setSelected(false);
                    online.setChecked(false);
                    pay_type = "";
                }else{
                    online.setSelected(true);
                    online.setChecked(true);
                    cod.setChecked(false);
                    pay_type = "online";
                }
            }
        });



        Button checkout;
        checkout = bottomview.findViewById(R.id.btn_confirm);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pay_type.equalsIgnoreCase("")){
                    Toast.makeText(activity, "Please select payment method..", Toast.LENGTH_SHORT).show();
                }else if (pay_type.equalsIgnoreCase("cod")){
                    workPlaceOrderCOD();
                }else if (pay_type.equalsIgnoreCase("online")){
                    workmode();
                }
            }
        });


        bottomSheetDialog.setContentView(bottomview);
        bottomSheetDialog.show();

    }

    private void displays(String displayed) {
        //Log.e("c_response=>>", displayed);
        plan_date_time_schedule = displayed;

        String planed_time = ApiConfig.parseDateToddMMyyyyHHMMAMPM(plan_date_time_schedule);
        //Log.e("c_response=>>123456", planed_time);

        String dateOnly = ApiConfig.spiltDate(plan_date_time_schedule);
        Log.e("c_response=>>789456",""+ dateOnly);
        Constant.ONETIME_DATE = dateOnly;

        String timeOnly = ApiConfig.spiltTime(plan_date_time_schedule);
        Log.e("c_response=>>789456",""+ timeOnly);
        Constant.ONETIME_TIME = timeOnly;
        comparedate = dateOnly;

    }

    public void showPopupDialog(String description, String image) {
        android.app.AlertDialog.Builder dialogbuilder = new android.app.AlertDialog.Builder(DoorStepServiceActivity.this);
        dialogbuilder.setCancelable(true);
        View dialogView = LayoutInflater.from(DoorStepServiceActivity.this).inflate(R.layout.layout_popup_info, null);
        TextView catDescription=dialogView.findViewById(R.id.cat_description);
        ImageView catImage =dialogView.findViewById(R.id.cat_image);
        ImageView close =dialogView.findViewById(R.id.cat_close_imgae);

        //catImage.setImageResource(R.drawable.exterior_wash);
        Picasso.get()
                .load(image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(catImage);
        catDescription.setText(Html.fromHtml(description));

        dialogbuilder.setView(dialogView);
        android.app.AlertDialog dialog = dialogbuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}