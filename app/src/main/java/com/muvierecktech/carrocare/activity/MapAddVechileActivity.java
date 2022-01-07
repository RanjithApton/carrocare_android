package com.muvierecktech.carrocare.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.MakeModelAdapter;
import com.muvierecktech.carrocare.adapter.VechicleCategoryAdapter;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityMapAddVechileBinding;
import com.muvierecktech.carrocare.model.MakeModelList;
import com.muvierecktech.carrocare.model.ParkingareaList;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapAddVechileActivity extends AppCompatActivity implements View.OnClickListener{
    public ActivityMapAddVechileBinding binding;
    SessionManager sessionManager;
    String customerid,token,vecType,vecCategory;
    MakeModelList makeModelList;
    String vechicleCategory[] = {"hatchback","sedan","suv"};
    public static String makeStr,modelStr;
    List<MakeModelList.Vehicle> vehicleList ;
    ArrayList<String> makemodel ;
    List<ParkingareaList.data> parkingarea;
    ArrayList<String> parkingareaname;
    String make,model,apartname,preferdScd,preferTime,parkArea;
    String address, latitude, longitude;
    ArrayList<String> spinner_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_map_add_vechile);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_map_add_vechile);
        sessionManager = new SessionManager(this);
        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        customerid = hashMap.get(SessionManager.KEY_USERID);
        token = hashMap.get(SessionManager.KEY_TOKEN);

        spinner_item = new ArrayList<>();
        Collections.addAll(spinner_item, vechicleCategory);

        binding.vecCategoryEdt.setOnClickListener(this);
        binding.makeModelEdt.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.cancelBtn.setOnClickListener(this);

        address = sessionManager.getData(SessionManager.MAP_ADDRESS);
        latitude = sessionManager.getData(SessionManager.MAP_LATITUDE);
        longitude = sessionManager.getData(SessionManager.MAP_LONGITUDE);


        binding.addressEdt.setText(address);

        binding.addressEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_loc = new Intent(MapAddVechileActivity.this, LocateOnMapActivity.class);
                startActivityForResult(intent_loc, 100);
            }
        });

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.makeModelEdt.getText().toString().length() > 0 && binding.addressEdt.getText().toString().length() > 0
                        && binding.vecNoEdt.getText().toString().length() > 0 && binding.vecColorEdt.getText().toString().length() > 0 ) {
                    workAdd();
                } else
                    Toast.makeText(MapAddVechileActivity.this, Constant.DETAILS, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void workAdd() {
        if (isNetworkAvailable()){
            AddVechile();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MapAddVechileActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection." );
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Ok".
                    workAdd();
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

    private void AddVechile() {
        final KProgressHUD hud = KProgressHUD.create(MapAddVechileActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.vechileAddDoorstep(vecType +"",
                vecCategory +"",
                makeStr +"",
                modelStr +"",
                binding.vecNoEdt.getText().toString() +"",
                binding.vecColorEdt.getText().toString() +"",
//                binding.addressEdt.getText().toString() +"",
//                latitude +"",
//                longitude +"",
                customerid +"",
                token +"");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body();
                hud.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                        Gson gson = new Gson();
                        Toast.makeText(MapAddVechileActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MapAddVechileActivity.this,DoorStepServiceActivity.class));
                        finish();
                    } else {
                        Toast.makeText(MapAddVechileActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(MapAddVechileActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void additionalwork(String vecCat) {
        vecCategory = vecCat;

//        if (vecCat.equalsIgnoreCase("bike")){
//            vecType = Constant.BIKE;
//        }else
//        }
        vecType = Constant.CAR;

        if (isNetworkAvailable()){
            makeModelList();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MapAddVechileActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection." );
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    additionalwork(vecCat);
                    //Action for "Ok".

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

    private void makeModelList() {
        makemodel = new ArrayList<>();
        vehicleList = new ArrayList<>();
        final KProgressHUD hud = KProgressHUD.create(MapAddVechileActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<MakeModelList> call = apiInterface.Makemodel(vecCategory);
        call.enqueue(new Callback<MakeModelList>() {
            @Override
            public void onResponse(Call<MakeModelList> call, Response<MakeModelList> response) {
                makeModelList = response.body();
                hud.dismiss();
                if (makeModelList.code.equalsIgnoreCase("200")) {
                    Gson gson = new Gson();
                    String json = gson.toJson(makeModelList);
                    vehicleList = makeModelList.vehicle;
                    for (int i = 0; i < vehicleList.size(); i++) {
                        makemodel.addAll(Collections.singleton(vehicleList.get(i).vehicle_make + "-" + vehicleList.get(i).vehicle_model));
                    }

                }else if (makeModelList.code.equalsIgnoreCase("201")){
                    Toast.makeText(MapAddVechileActivity.this,makeModelList.message,Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<MakeModelList> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(MapAddVechileActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.LOAD_FROM.equalsIgnoreCase("map")) {
            if (!TextUtils.isEmpty(sessionManager.getData(SessionManager.KEY_ADDRESS)) || sessionManager.getData(SessionManager.KEY_ADDRESS) != null) {
                binding.addressEdt.setText(sessionManager.getData(SessionManager.KEY_ADDRESS));
                latitude = sessionManager.getData(SessionManager.KEY_LATITUDE);
                longitude = sessionManager.getData(SessionManager.KEY_LONGITUDE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MapAddVechileActivity.this, DoorStepServiceActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.vec_category_edt:

                binding.spinner.setAdapter(new ArrayAdapter<String>(MapAddVechileActivity.this, android.R.layout.simple_list_item_1,spinner_item));

                binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        binding.vecCategoryEdt.setText(spinner_item.get(i));
                        additionalwork(spinner_item.get(i));
                        binding.makeModelEdt.setText("");
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                binding.spinner.performClick();
                break;
            case R.id.make_model_edt:
                if (TextUtils.isEmpty(binding.vecCategoryEdt.getText().toString())){
                    Toast.makeText(MapAddVechileActivity.this,"Choose Vechicle Category",Toast.LENGTH_SHORT).show();
                } else {

                    binding.spinner.setAdapter(new ArrayAdapter<String>(MapAddVechileActivity.this, android.R.layout.simple_list_item_1,makemodel));

                    binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            binding.makeModelEdt.setText(makemodel.get(i));
                            String currentString = makemodel.get(i);
                            String[] separated = currentString.split("-");
                            makeStr = separated[0];
                            modelStr = separated[1];
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                    binding.spinner.performClick();
                }
                break;
            case R.id.back:
                finish();
                break;
            case R.id.cancel_btn:
                finish();
                break;
        }
    }
}