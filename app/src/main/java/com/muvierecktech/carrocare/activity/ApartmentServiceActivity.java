package com.muvierecktech.carrocare.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityApartmentServiceBinding;

import java.util.HashMap;

public class ApartmentServiceActivity extends AppCompatActivity {
    ActivityApartmentServiceBinding binding;
    SessionManager sessionManager;
    String username,apartname,token,customerid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_apartment_service);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_apartment_service);
        sessionManager = new SessionManager(this);
        binding.headerName.setText("Apartment Service");
        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        username = hashMap.get(SessionManager.KEY_USERNAME);
        apartname = hashMap.get(SessionManager.KEY_APARTNAME);
        token = hashMap.get(SessionManager.KEY_TOKEN);
        customerid = hashMap.get(SessionManager.KEY_USERID);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        binding.myVechicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApartmentServiceActivity.this,MyVehiclesActivity.class);
                startActivity(intent);
            }
        });

//        binding.myRemainder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ApartmentServiceActivity.this,MyRemainderActivity.class);
//                startActivity(intent);
//            }
//        });


        binding.carWash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(apartname) || apartname == null || apartname.equalsIgnoreCase("null")){
                    Constant.LOAD_FROM = "main";
                    Intent intent = new Intent(ApartmentServiceActivity.this,ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(ApartmentServiceActivity.this,CarWashActivity.class);
                    //intent.putExtra("headername", Constant.WASH);
                    startActivity(intent);
                }
            }
        });
        binding.bikeWash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(apartname) || apartname == null || apartname.equalsIgnoreCase("null")) {
                    Constant.LOAD_FROM = "main";
                    Intent intent = new Intent(ApartmentServiceActivity.this,ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(ApartmentServiceActivity.this,BikeWashActivity.class);
                    //intent.putExtra("headername", Constant.BWASH);
                    startActivity(intent);
                }
            }
        });
//        binding.carInsurance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(apartname) || apartname == null || apartname.equalsIgnoreCase("null")){
//                    Constant.LOAD_FROM = "main";
//                    Intent intent = new Intent(ApartmentServiceActivity.this,ProfileActivity.class);
//                    startActivity(intent);
//                    finish();
//                }else {
//                    Intent intent = new Intent(ApartmentServiceActivity.this,ConfirmFormActivity.class);
//                    intent.putExtra("headername",Constant.INSURANCE);
//                    startActivity(intent);
//                }
//            }
//        });
        binding.carService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(apartname) || apartname == null || apartname.equalsIgnoreCase("null")){
                    Constant.LOAD_FROM = "main";
                    Intent intent = new Intent(ApartmentServiceActivity.this,ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(ApartmentServiceActivity.this,AddOnActivity.class);
                    //intent.putExtra("headername",Constant.ADDON);
                    startActivity(intent);
                }
            }
        });
        binding.extraInterior.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(apartname) || apartname == null || apartname.equalsIgnoreCase("null")){
                    Constant.LOAD_FROM = "main";
                    Intent intent = new Intent(ApartmentServiceActivity.this,ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(ApartmentServiceActivity.this, ExtraInteriorActivity.class);
                    //intent.putExtra("headername",Constant.EXTRAINT);
                    startActivity(intent);
                }
            }
        });
        binding.machinePolish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(apartname) || apartname == null || apartname.equalsIgnoreCase("null")){
                    Constant.LOAD_FROM = "main";
                    Intent intent = new Intent(ApartmentServiceActivity.this,ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(ApartmentServiceActivity.this,CarPolishActivity.class);
                    //intent.putExtra("headername",Constant.CARMACHINE);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ApartmentServiceActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}