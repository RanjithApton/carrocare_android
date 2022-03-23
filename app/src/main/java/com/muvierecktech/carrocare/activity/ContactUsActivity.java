package com.muvierecktech.carrocare.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;

import com.muvierecktech.carrocare.databinding.ActivityContactUsBinding;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUsActivity extends AppCompatActivity
//        implements OnMapReadyCallback
{
    ActivityContactUsBinding binding;
    SessionManager sessionManager;
    String token,customerid;
//    GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_contact_us);
        sessionManager = new SessionManager(this);
        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.nameEdt.getText().toString().length()>0 && binding.emailEdt.getText().toString().length()>0 &&
                binding.mobnoEdt.getText().toString().length()>0 && binding.subjectEdt.getText().toString().length() >0 &&
                        binding.subjectEdt.getText().toString().length()>0){
                    if (binding.mobnoEdt.getText().toString().length()==10){
                        if (emailValidator(binding.emailEdt.getText().toString())){
                            work();
                        }else   Toast.makeText(ContactUsActivity.this,Constant.VALIDEMAIL,Toast.LENGTH_SHORT).show();
                    }else   Toast.makeText(ContactUsActivity.this,Constant.VALIDMOBILE,Toast.LENGTH_SHORT).show();
                } else   Toast.makeText(ContactUsActivity.this, Constant.DETAILS,Toast.LENGTH_SHORT).show();
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.devCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.muvierecktech.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        binding.whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumberWithCountryCode = "+917418712862";
                String message = "Hi Carrocare team...";

                boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
                if (isWhatsappInstalled) {
                    startActivity(
                            new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(
                                            String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message)
                                    )
                            )
                    );
                }else {
                    Toast.makeText(ContactUsActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
//                    Uri uri = Uri.parse("market://details?id=com.whatsapp");
//                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(goToMarket);
                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
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

    private void work() {
        if (isNetworkAvailable()){
            SubmitForm();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ContactUsActivity.this);
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

    private void SubmitForm() {
        final KProgressHUD hud = KProgressHUD.create(ContactUsActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.conatctFrom(binding.subjectEdt.getText().toString()+"",""+binding.messageEdt.getText().toString(),
                binding.nameEdt.getText().toString()+"",binding.emailEdt.getText().toString()+"",binding.mobnoEdt.getText().toString()+"");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body();
                hud.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                        Gson gson = new Gson();
                        Toast.makeText(ContactUsActivity.this,jsonObject.optString("result"),Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(ContactUsActivity.this,jsonObject.optString("message"),Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(ContactUsActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
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
        @SuppressLint("MissingPermission")
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

//    @Override
//    public void onMapReady(GoogleMap gMap) {
//        googleMap = gMap;
//        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//        //Edit the following as per you needs
//        googleMap.setTrafficEnabled(true);
//        googleMap.setIndoorEnabled(true);
//        googleMap.setBuildingsEnabled(true);
//        googleMap.getUiSettings().setZoomControlsEnabled(true);
//        //
//
//        LatLng placeLocation = new LatLng(Double.parseDouble(Constant.LAT),Double.parseDouble(Constant.LNG)); //Make them global
//        Marker placeMarker = googleMap.addMarker(new MarkerOptions().position(placeLocation)
//                .title("Regal car wash\n" +
//                        "No 3pc1, kambar salai, Mogappair West, Chennai, Tamil Nadu 600036"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 1000, null);
//    }
}