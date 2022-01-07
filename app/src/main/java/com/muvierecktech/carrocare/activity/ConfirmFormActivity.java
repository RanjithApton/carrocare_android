package com.muvierecktech.carrocare.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.PreferredAdapter;
import com.muvierecktech.carrocare.common.BaseActivity;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityConfirmFormBinding;
import com.muvierecktech.carrocare.model.ServicePriceList;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmFormActivity extends BaseActivity implements View.OnClickListener {
    public ActivityConfirmFormBinding binding;
    SessionManager sessionManager;
    String name,mobile,email,form,header,description,vectype,veccat;
    String type[] = {
//            Constant.VECTYPE,
            Constant.CAR,Constant.BIKE};
    String categoryCar[] = {
//            Constant.VECCAT,
            Constant.HATCHBACK,Constant.SEDAN,Constant.SUV};
    String[] categoryBike = {Constant.BIKE};
    ArrayList<String> spinner_item;
    ArrayList<String> type_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_confirm_form);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_form);
        attachKeyboardListeners();
        sessionManager = new SessionManager(this);
        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        name = hashMap.get(SessionManager.KEY_USERNAME);
        email = hashMap.get(SessionManager.KEY_USEREMAIL);
        mobile = hashMap.get(SessionManager.KEY_USERMOBILE);

        Intent intent = getIntent();
        header = intent.getStringExtra("headername");

        spinner_item = new ArrayList<>();
        type_item = new ArrayList<>();
        Collections.addAll(type_item, type);

        if (intent.hasExtra("headername")){
            if (header.equalsIgnoreCase("Doorstep Car Insurance")){
                binding.info.setVisibility(View.VISIBLE);
                binding.headerName.setText(header);
                form = "car_insurance";
                workPrice();
            }
        }else {
            binding.info.setVisibility(View.GONE);
            binding.headerName.setText("Confirm Form");
            form = "car_machine_polish";
        }

        binding.backBtn.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.vecTypeEdt.setOnClickListener(this);
        binding.vecCategoryEdt.setOnClickListener(this);

        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (binding.nameEdt.getText().toString().length()>0 &&binding.emailEdt.getText().toString().length()>0 &&
                            binding.mobileEdt.getText().toString().length()>0 &&binding.addressEdt.getText().toString().length()>0 &&
                            binding.landmarkEdt.getText().toString().length()>0 &&binding.cityEdt.getText().toString().length()>0 &&
                            binding.stateEdt.getText().toString().length()>0 &&binding.countryEdt.getText().toString().length()>0 &&
                            binding.pincodeEdt.getText().toString().length()>0 && binding.vecTypeEdt.getText().toString().length()>0&&
                            binding.vecMakeEdt.getText().toString().length()>0 &&binding.vecModelEdt.getText().toString().length()>0 &&
                            binding.vecCategoryEdt.getText().toString().length()>0 ){
                        if (binding.mobileEdt.getText().toString().length()==10){
                            if (binding.pincodeEdt.getText().toString().length()==6){
                                if (emailValidator(binding.emailEdt.getText().toString())){
                                    work();
                                }else Toast.makeText(ConfirmFormActivity.this,Constant.VALIDEMAIL,Toast.LENGTH_SHORT).show();

                            }else Toast.makeText(ConfirmFormActivity.this,Constant.ENTERPINCODE,Toast.LENGTH_SHORT).show();

                        }else Toast.makeText(ConfirmFormActivity.this,Constant.VALIDMOBILE,Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(ConfirmFormActivity.this,Constant.DETAILS,Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }



    private void workPrice() {
        if (isNetworkAvailable()){
            ServicePrice();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ConfirmFormActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Alert!");
            dialog.setMessage("No internet.Please check your connection." );
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Ok".
                    workPrice();
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
        final KProgressHUD hud = KProgressHUD.create(ConfirmFormActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ServicePriceList> call = apiInterface.Service(Constant.CARINSURANCE);
        call.enqueue(new Callback<ServicePriceList>() {
            @Override
            public void onResponse(Call<ServicePriceList> call, Response<ServicePriceList> response) {
                final ServicePriceList servicePriceList = response.body();
                hud.dismiss();
                if (servicePriceList.code.equalsIgnoreCase("200")) {
                    Gson gson = new Gson();
                    String json = gson.toJson(servicePriceList);

//                    binding.description.setText(HtmlCompat.fromHtml(servicePriceList.description+"",0));
                    description = servicePriceList.description;
                    binding.info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ConfirmFormActivity.this,InfoActivity.class);
                            intent.putExtra("headername",Constant.CARINSURANCE);
                            intent.putExtra("description",description);
                            startActivity(intent);
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ServicePriceList> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(ConfirmFormActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void work() {
        if (isNetworkAvailable()){
            FormSubmit();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ConfirmFormActivity.this);
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

    private void FormSubmit() {
        final KProgressHUD hud = KProgressHUD.create(ConfirmFormActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.serviceForm(name,mobile,email,binding.addressEdt.getText().toString(),binding.landmarkEdt.getText().toString()
                ,binding.cityEdt.getText().toString(),binding.stateEdt.getText().toString(),binding.countryEdt.getText().toString(),binding.pincodeEdt.getText().toString(),
                binding.vecTypeEdt.getText().toString(),binding.vecMakeEdt.getText().toString(),binding.vecModelEdt.getText().toString(),
                binding.vecCategoryEdt.getText().toString(),form);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body();
                hud.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                        Gson gson = new Gson();
                        Toast.makeText(ConfirmFormActivity.this,jsonObject.optString("message"),Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(ConfirmFormActivity.this,jsonObject.optString("message"),Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(ConfirmFormActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void closeKeyboard(Activity activity, TextView vecTypeEdt) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        vecTypeEdt.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onShowKeyboard(int keyboardHeight) {
        super.onShowKeyboard(keyboardHeight);
//        binding.typelistLl.setVisibility(View.GONE);
//        binding.catlistLl.setVisibility(View.GONE);
    }

    @Override
    protected void onHideKeyboard() {
        super.onHideKeyboard();

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
    @Override
    public void onClick (View view){
        switch (view.getId()) {
            case R.id.vec_type_edt:
                binding.apartRl.setVisibility(View.VISIBLE);
                binding.typelistRc.setVisibility(View.VISIBLE);
                binding.catlistRc.setVisibility(View.GONE);
                PreferredAdapter preferredAdapter = new PreferredAdapter(ConfirmFormActivity.this, type, "type");
                LinearLayoutManager linearLayoutManage = new LinearLayoutManager(ConfirmFormActivity.this, LinearLayoutManager.VERTICAL, false);
                binding.typelistRc.setLayoutManager(linearLayoutManage);
                binding.typelistRc.setAdapter(preferredAdapter);
                break;
            case R.id.vec_category_edt:
                if (binding.vecTypeEdt.getText().toString().equalsIgnoreCase(Constant.CAR)) {
                    binding.apartRl.setVisibility(View.VISIBLE);
                    binding.catlistRc.setVisibility(View.VISIBLE);
                    binding.typelistRc.setVisibility(View.GONE);
                    PreferredAdapter preferredAdapter1 = new PreferredAdapter(ConfirmFormActivity.this, categoryCar, "catcar");
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ConfirmFormActivity.this, LinearLayoutManager.VERTICAL, false);
                    binding.catlistRc.setLayoutManager(linearLayoutManager);
                    binding.catlistRc.setAdapter(preferredAdapter1);
                }else  if (binding.vecTypeEdt.getText().toString().equalsIgnoreCase(Constant.BIKE)){
                    binding.vecCategoryEdt.setText(Constant.BIKE);
                }else {
                    Toast.makeText(ConfirmFormActivity.this,"Choose Vehicle type",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.apart_rl:
                binding.apartRl.setVisibility(View.GONE);
                binding.catlistRc.setVisibility(View.GONE);
                binding.typelistRc.setVisibility(View.GONE);
                break;

            case R.id.back:
                finish();
                break;
            case R.id.back_btn:
                finish();
                break;
//            case R.id.vec_type_edt:
//                binding.typelistLl.setVisibility(View.VISIBLE);
//                closeKeyboard(ConfirmFormActivity.this,binding.vecTypeEdt);
//                break;
//            case R.id.vec_category_edt:
//                if (!TextUtils.isEmpty(binding.vecTypeEdt.getText().toString())){
//                    binding.catlistLl.setVisibility(View.VISIBLE);
//                }else {
//                    Toast.makeText(ConfirmFormActivity.this, Constant.CHOOSEVECTYPE,Toast.LENGTH_SHORT).show();
//                }
//                closeKeyboard(ConfirmFormActivity.this, binding.vecCategoryEdt);
//                break;
//            case R.id.type_car:
//                binding.typelistLl.setVisibility(View.GONE);
//                binding.vecTypeEdt.setText(Constant.CAR);
//                binding.catBike.setVisibility(View.GONE);
//                binding.vecCategoryEdt.setText(null);
//                binding.catHatchback.setVisibility(View.VISIBLE);
//                binding.catSedan.setVisibility(View.VISIBLE);
//                binding.catSuv.setVisibility(View.VISIBLE);
//                break;
//            case R.id.type_bike:
//                binding.typelistLl.setVisibility(View.GONE);
//                binding.catBike.setVisibility(View.VISIBLE);
//                binding.vecTypeEdt.setText(Constant.BIKE);
//                binding.vecCategoryEdt.setText(Constant.BIKE);
//                binding.catHatchback.setVisibility(View.GONE);
//                binding.catSedan.setVisibility(View.GONE);
//                binding.catSuv.setVisibility(View.GONE);
//                break;
//            case R.id.cat_hatchback:
//                binding.catlistLl.setVisibility(View.GONE);
//                binding.vecCategoryEdt.setText(Constant.HATCHBACK);
//                break;
//            case R.id.cat_sedan:
//                binding.catlistLl.setVisibility(View.GONE);
//                binding.vecCategoryEdt.setText(Constant.SEDAN);
//                break;
//            case R.id.cat_suv:
//                binding.catlistLl.setVisibility(View.GONE);
//                binding.vecCategoryEdt.setText(Constant.SUV);
//                break;
//            case R.id.cat_bike:
//                binding.catlistLl.setVisibility(View.GONE);
//                binding.vecCategoryEdt.setText(Constant.BIKE);
//                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ConfirmFormActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}