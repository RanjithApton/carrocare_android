package com.muvierecktech.carrocare.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.ApartmentsAdapter;
import com.muvierecktech.carrocare.adapter.MakeModelAdapter;
import com.muvierecktech.carrocare.adapter.ParkingareaAdapter;
import com.muvierecktech.carrocare.adapter.PreferredAdapter;
import com.muvierecktech.carrocare.adapter.VechicleCategoryAdapter;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityMyVechiclesAddBinding;
import com.muvierecktech.carrocare.model.ApartmentList;
import com.muvierecktech.carrocare.model.MakeModelList;
import com.muvierecktech.carrocare.model.ParkingareaList;
import com.muvierecktech.carrocare.restapi.ApiClient;
import com.muvierecktech.carrocare.restapi.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyVechiclesAddActivity extends AppCompatActivity implements View.OnClickListener{
    public ActivityMyVechiclesAddBinding binding;
    SessionManager sessionManager;
    String customerid,token,vecType,vecCategory;
    String vechicleCategory[] = {
//            Constant.PREFERTIME,
            "hatchback","sedan","suv","bike"};
    String preSchedule[] = {
//            Constant.PREFERSCH,
            Constant.MORNING,Constant.EVENING};
    String preMorTime[] = {
//            Constant.PREFERTIME,
            "5.00 AM - 6.00 AM","6.00 AM - 7.00 AM","7.00 AM - 8.00 AM","8.00 AM - 9.00 AM","9.00 AM - 10.00 AM"};
    String preEveTime[] = {
//            Constant.PREFERTIME,
            "5.00 PM - 6.00 PM","6.00 PM - 7.00 PM","7.00 PM - 8.00 PM","8.00 PM - 9.00 PM"};
    String parkingArea[] = {
//            Constant.PARKAREA,
            "Basement 1","Basement 2","Open Area","Visitor Parking"};
    List<ParkingareaList.data> parkingarea;
    ArrayList<String> parkingareaname;
    public static String makeStr,modelStr;
    List<MakeModelList.Vehicle> vehicleList ;
    List<ApartmentList.Apartment> apartments;
    ArrayList<String> makemodel ;
    ArrayList<String> apartmentname;
    String make,model,apartname,preferdScd,preferTime,parkArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_vechicles_add);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_vechicles_add);
        sessionManager = new SessionManager(this);
        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        customerid = hashMap.get(SessionManager.KEY_USERID);
        token = hashMap.get(SessionManager.KEY_TOKEN);


        binding.vecCategoryEdt.setOnClickListener(this);
        binding.makeModelEdt.setOnClickListener(this);
        binding.apartmentEdt.setOnClickListener(this);
        binding.parkingAreaEdt.setOnClickListener(this);
        binding.preferredscheduleEdt.setOnClickListener(this);
        binding.preferredtimeEdt.setOnClickListener(this);
        binding.apartRl.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.cancelBtn.setOnClickListener(this);

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.vecCategoryEdt.getText().toString().isEmpty()) {
                    Toast.makeText(MyVechiclesAddActivity.this,"Select Vehicle Category", Toast.LENGTH_SHORT).show();
                } else {

                    if (vecCategory.equalsIgnoreCase("Bike")) {
                        if (binding.makeEdt.getText().toString().length() > 0 && binding.modelEdt.getText().toString().length() > 0 && binding.vecNoEdt.getText().toString().length() > 0 &&
                                binding.vecColorEdt.getText().toString().length() > 0 && binding.apartmentEdt.toString().length() > 0 &&
                                binding.parkingLotEdt.getText().toString().length() > 0 && binding.parkingAreaEdt.getText().toString().length() > 0 &&
                                binding.preferredscheduleEdt.getText().toString().length() > 0 && binding.preferredtimeEdt.getText().toString().length() > 0) {
                            makeStr = binding.makeEdt.getText().toString();
                            modelStr = binding.modelEdt.getText().toString();
                            apartname = binding.apartmentEdt.getText().toString();
                            preferdScd = binding.preferredscheduleEdt.getText().toString();
                            preferTime = binding.preferredtimeEdt.getText().toString();
                            parkArea = binding.parkingAreaEdt.getText().toString();
                            workAdd();
                        } else
                            Toast.makeText(MyVechiclesAddActivity.this, Constant.DETAILS, Toast.LENGTH_SHORT).show();
                    } else {
                        if (binding.makeModelEdt.getText().toString().length() > 0 && binding.apartmentEdt.getText().toString().length() > 0
                                && binding.vecNoEdt.getText().toString().length() > 0 && binding.vecColorEdt.getText().toString().length() > 0 &&
                                binding.parkingLotEdt.getText().toString().length() > 0 && binding.parkingAreaEdt.getText().toString().length() > 0 &&
                                binding.preferredscheduleEdt.getText().toString().length() > 0 && binding.preferredtimeEdt.getText().toString().length() > 0) {

                            apartname = binding.apartmentEdt.getText().toString();
                            preferdScd = binding.preferredscheduleEdt.getText().toString();
                            preferTime = binding.preferredtimeEdt.getText().toString();
                            parkArea = binding.parkingAreaEdt.getText().toString();
                            workAdd();
                        } else
                            Toast.makeText(MyVechiclesAddActivity.this, Constant.DETAILS, Toast.LENGTH_SHORT).show();
                    }
               /* if (vecCategory.equalsIgnoreCase("Bike")){
                    if (binding.makeEdt.getText().toString().length()>0 && binding.modelEdt.getText().toString().length()>0 && binding.vecNoEdt.getText().toString().length()>0 &&
                            binding.vecColorEdt.getText().toString().length()>0 && !binding.apartnameEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.APARTMENTNAME)&&
                            binding.parkingLotEdt.getText().toString().length()>0 &&!binding.parkingAreaEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.PARKAREA) &&
                            !binding.preferredscheduleEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.PREFERSCH)&& !binding.preferredtimeEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.PREFERTIME)){
                        makeStr = binding.makeEdt.getText().toString();
                        modelStr = binding.modelEdt.getText().toString();
                        workAdd();
                    }else Toast.makeText(MyVechiclesAddActivity.this,Constant.DETAILS,Toast.LENGTH_SHORT).show();
                }else {
                    if (!binding.makeModelEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.MAKEMODEL)  &&!binding.apartnameEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.APARTMENTNAME)
                            && binding.vecNoEdt.getText().toString().length()>0 && binding.vecColorEdt.getText().toString().length()>0 &&
                            binding.parkingLotEdt.getText().toString().length()>0 &&!binding.parkingAreaEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.PARKAREA) &&
                            !binding.preferredscheduleEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.PREFERSCH)&& !binding.preferredtimeEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.PREFERTIME)){
                        makeStr = make;
                        modelStr = model;
                        workAdd();
                    }else Toast.makeText(MyVechiclesAddActivity.this,Constant.DETAILS,Toast.LENGTH_SHORT).show();
                }*/

                }
            }

        });
        work();
    /*    workSchedule();
        workArea();*/
    }

    /* private void workArea() {
         final int pos = 0;
 //        for(int i = 0; i < preSchedule.length; i++){
 //            //Storing names to string array
 //            String items = preSchedule[i];
 //            apartmentname.add(items);
 ////                        pos = i;
 //        }
 
         //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
         ArrayAdapter<String> adapter;
         adapter = new ArrayAdapter<String>(MyVechiclesAddActivity.this, R.layout.spinner_item, parkingArea);
         //setting adapter to spinner
         binding.parkingAreaEdt.setAdapter(adapter);
         binding.parkingAreaEdt.setSelection(pos);
 
         binding.parkingAreaEdt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 parkArea = parkingArea[i];
                 Log.e("parkarea",parkArea);
             }
 
             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {
 
             }
         });
     }
 
     private void workTime() {
         final int pos = 0;
 //        for(int i = 0; i < preSchedule.length; i++){
 //            //Storing names to string array
 //            String items = preSchedule[i];
 //            apartmentname.add(items);
 ////                        pos = i;
 //        }
 
         //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
 
         if (!binding.preferredscheduleEdt.equals("0")){
             ArrayAdapter<String> adapter;
             binding.prefertimeRl.setVisibility(View.VISIBLE);
             if (binding.preferredscheduleEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.MORNING)){
                 adapter = new ArrayAdapter<String>(MyVechiclesAddActivity.this, R.layout.spinner_item, preMorTime);
                 binding.preferredtimeEdt.setAdapter(adapter);
                 binding.preferredtimeEdt.setSelection(pos);
             } else if (binding.preferredscheduleEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.EVENING)) {
                 adapter = new ArrayAdapter<String>(MyVechiclesAddActivity.this, R.layout.spinner_item, preEveTime);
                 binding.preferredtimeEdt.setAdapter(adapter);
                 binding.preferredtimeEdt.setSelection(pos);
             }
             //setting adapter to spinner
 
             binding.preferredtimeEdt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                 @Override
                 public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                     if (binding.preferredscheduleEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.MORNING)){
                         preferTime = preMorTime[i];
                         Log.e("preTime",preferTime);
                     }else {
                         preferTime = preEveTime[i];
                         Log.e("preTime",preferTime);
                     }
                 }
 
                 @Override
                 public void onNothingSelected(AdapterView<?> adapterView) {
 
                 }
             });
         }else {
             binding.prefertimeRl.setVisibility(View.GONE);
             Toast.makeText(MyVechiclesAddActivity.this,"Select Preferred Schedule",Toast.LENGTH_SHORT).show();
         }
 
     }
 
     private void workSchedule() {
 
         final int pos = 0;
 //        for(int i = 0; i < preSchedule.length; i++){
 //            //Storing names to string array
 //            String items = preSchedule[i];
 //            apartmentname.add(items);
 ////                        pos = i;
 //        }
 
         //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
         ArrayAdapter<String> adapter;
         adapter = new ArrayAdapter<String>(MyVechiclesAddActivity.this, R.layout.spinner_item, preSchedule);
         //setting adapter to spinner
         binding.preferredscheduleEdt.setAdapter(adapter);
         binding.preferredscheduleEdt.setSelection(pos);
 
         binding.preferredscheduleEdt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 preferdScd = preSchedule[i];
                 Log.e("preSchdule",preferdScd);
                 workTime();
             }
 
             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {
 
             }
         });
     }
 */
    private void AddVechile() {
        final KProgressHUD hud = KProgressHUD.create(MyVechiclesAddActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.vechileAdd(vecType,vecCategory,makeStr,
                modelStr,binding.vecNoEdt.getText().toString(),binding.vecColorEdt.getText().toString(),
                apartname,binding.parkingLotEdt.getText().toString(),parkArea,
                preferdScd,preferTime,customerid,token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonElement jsonElement = response.body();
                hud.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                        Gson gson = new Gson();
                        Toast.makeText(MyVechiclesAddActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(MyVechiclesAddActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(MyVechiclesAddActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void workAdd() {
        if (isNetworkAvailable()){
            AddVechile();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MyVechiclesAddActivity.this);
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
    private void work() {
        if (isNetworkAvailable()){
            /*if (vecCategory.equalsIgnoreCase("Bike")){
            }else {
                makeModelList();
            }*/
            apartmentList();
            parkingList();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MyVechiclesAddActivity.this);
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

    public void additionalwork(String vecCat){
        vecCategory = vecCat;

        if (vecCat.equalsIgnoreCase("bike")){
            vecType = Constant.BIKE;
        }else {
            vecType = Constant.CAR;

            if (isNetworkAvailable()){
                makeModelList();
            }else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MyVechiclesAddActivity.this);
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
    }

    private void apartmentList() {
        apartments = new ArrayList<>();
        apartmentname = new ArrayList<>();
        final KProgressHUD hud = KProgressHUD.create(MyVechiclesAddActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ApartmentList> call = apiInterface.apartmentList();
        call.enqueue(new Callback<ApartmentList>() {
            @Override
            public void onResponse(Call<ApartmentList> call, Response<ApartmentList> response) {
                ApartmentList apartmentList = response.body();
                hud.dismiss();
                if (apartmentList.code.equalsIgnoreCase("200")){
                    apartments = apartmentList.Apartment;
                    int pos = 0;
                    apartmentname.add(0,Constant.APARTMENTNAME);
                    for(int i = 0; i < apartments.size(); i++){
                        //Storing names to string array
                        String items = apartments.get(i).name;
                        apartmentname.add(items);
//                        pos = i;
                    }

             /*       //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
                    ArrayAdapter<String> adapter;
                    adapter = new ArrayAdapter<String>(MyVechiclesAddActivity.this, R.layout.spinner_item, apartmentname);
                    adapter.setDropDownViewResource(R.layout.spinner_item);
                    //setting adapter to spinner
                    binding.apartnameEdt.setAdapter(adapter);
                    binding.apartnameEdt.setSelection(pos);
*/
                    ApartmentsAdapter apartmentsAdapter = new ApartmentsAdapter(MyVechiclesAddActivity.this,apartmentList.Apartment,"3");
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyVechiclesAddActivity.this,LinearLayoutManager.VERTICAL,false);
                    binding.apartlistRc.setLayoutManager(linearLayoutManager);
                    binding.apartlistRc.setAdapter(apartmentsAdapter);
                }
            }
            @Override
            public void onFailure(Call<ApartmentList> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(MyVechiclesAddActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
/*
        binding.apartnameEdt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                apartname = apartmentname.get(position);
                Log.e("Apartmentname",apartname);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
    }


    private void parkingList() {
        parkingarea = new ArrayList();
        parkingareaname = new ArrayList<>();

        final KProgressHUD hud = KProgressHUD.create(MyVechiclesAddActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ParkingareaList> call = apiInterface.parkingareaList();
        call.enqueue(new Callback<ParkingareaList>() {
            @Override
            public void onResponse(Call<ParkingareaList> call, Response<ParkingareaList> response) {
                ParkingareaList body = response.body();
                hud.dismiss();
                if (body.code.equalsIgnoreCase("200")) {
                    parkingarea = body.data;
                    parkingareaname.add(0, Constant.PARKINGNAME);
                    for (int i = 0; i < parkingarea.size(); i++) {
                        parkingareaname.add(parkingarea.get(i).name);
                    }
                    ParkingareaAdapter parkingareaAdapter = new ParkingareaAdapter(MyVechiclesAddActivity.this, body.data,"2");
                    binding.parkingAreaRc.setLayoutManager(new LinearLayoutManager(MyVechiclesAddActivity.this, LinearLayoutManager.VERTICAL, false));
                    binding.parkingAreaRc.setAdapter(parkingareaAdapter);
                }
            }

            public void onFailure(Call<ParkingareaList> call, Throwable th) {
                hud.dismiss();
                Toast.makeText(MyVechiclesAddActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void makeModelList() {
        //Log.e("VecCat",vecCategory);

//        vehicleList.clear();
        makemodel = new ArrayList<>();
        vehicleList = new ArrayList<>();
        final KProgressHUD hud = KProgressHUD.create(MyVechiclesAddActivity.this)
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
                final MakeModelList makeModelList = response.body();
                hud.dismiss();
                if (makeModelList.code.equalsIgnoreCase("200")) {
                    Gson gson = new Gson();
                    String json = gson.toJson(makeModelList);
                    vehicleList = makeModelList.vehicle;
             /*       int pos = 0;

                    makemodel.add(0,Constant.MAKEMODEL);
                    //String array to store all the book names
//                    String[] items = new String[vehicleList.size()];
//                    items[0] = new MakeModelList.Vehicle("0","Select City");

                    //Traversing through the whole list to get all the names
                    for(int i = 0; i < vehicleList.size(); i++){
                        //Storing names to string array
                      String items = vehicleList.get(i).vehicle_make+"-"+vehicleList.get(i).vehicle_model;
                      makemodel.add(items);
//                        pos = i;
                    }

                    //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
                    ArrayAdapter<String> adapter;
                    adapter = new ArrayAdapter<String>(MyVechiclesAddActivity.this, R.layout.spinner_item, makemodel);
                    //setting adapter to spinner
                    binding.makeModelEdt.setAdapter(adapter);
                    binding.makeModelEdt.setSelection(pos);
                    //Creating an array adapter for list view
*/
//                    vehicleList.add(0,new MakeModelList.Vehicle().vehicle_make,new MakeModelList.Vehicle().vehicle_model);
                    MakeModelAdapter modelAdapter = new MakeModelAdapter(MyVechiclesAddActivity.this,makeModelList.vehicle,"2");
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyVechiclesAddActivity.this,LinearLayoutManager.VERTICAL,false);
                    binding.makemodelRc.setLayoutManager(linearLayoutManager);
                    binding.makemodelRc.setAdapter(modelAdapter);
                }else if (makeModelList.code.equalsIgnoreCase("201")){
                    Toast.makeText(MyVechiclesAddActivity.this,makeModelList.message,Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<MakeModelList> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(MyVechiclesAddActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
   /* binding.makeModelEdt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position!=0) {
                    String[] separated = makemodel.get(position).split("-");
                    make = separated[0].trim();
                    model = separated[1].trim();
                    Log.e("Make & Model", make + "," + model);
                }
//                city = binding.makeModelEdt.getSelectedItem().toString();
//                SetAreaSpinnerData(cityId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.vec_category_edt:
                binding.apartRl.setVisibility(View.VISIBLE);
                binding.vecCategoryRc.setVisibility(View.VISIBLE);
                binding.makemodelRc.setVisibility(View.GONE);
                binding.apartlistRc.setVisibility(View.GONE);
                binding.preferredscheduleRc.setVisibility(View.GONE);
                binding.preferredtimeRc.setVisibility(View.GONE);
                binding.parkingAreaRc.setVisibility(View.GONE);
                VechicleCategoryAdapter vechicleCategoryAdapter = new VechicleCategoryAdapter(MyVechiclesAddActivity.this, vechicleCategory, "2");
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyVechiclesAddActivity.this, LinearLayoutManager.VERTICAL, false);
                binding.vecCategoryRc.setLayoutManager(linearLayoutManager);
                binding.vecCategoryRc.setAdapter(vechicleCategoryAdapter);
                break;
            case R.id.make_model_edt:
                if (TextUtils.isEmpty(binding.vecCategoryEdt.getText().toString())){
                    Toast.makeText(MyVechiclesAddActivity.this,"Choose Vechicle Category",Toast.LENGTH_SHORT).show();
                } else {
                    binding.vecCategoryRc.setVisibility(View.GONE);
                    binding.apartRl.setVisibility(View.VISIBLE);
                    binding.makemodelRc.setVisibility(View.VISIBLE);
                    binding.apartlistRc.setVisibility(View.GONE);
                    binding.preferredscheduleRc.setVisibility(View.GONE);
                    binding.preferredtimeRc.setVisibility(View.GONE);
                    binding.parkingAreaRc.setVisibility(View.GONE);
                }
                break;
            case R.id.apartment_edt:
                binding.vecCategoryRc.setVisibility(View.GONE);
                binding.apartRl.setVisibility(View.VISIBLE);
                binding.apartlistRc.setVisibility(View.VISIBLE);
                binding.makemodelRc.setVisibility(View.GONE);
                binding.preferredscheduleRc.setVisibility(View.GONE);
                binding.preferredtimeRc.setVisibility(View.GONE);
                binding.parkingAreaRc.setVisibility(View.GONE);
                break;
            case R.id.parking_area_edt:
                binding.vecCategoryRc.setVisibility(View.GONE);
                binding.apartRl.setVisibility(View.VISIBLE);
                binding.parkingAreaRc.setVisibility(View.VISIBLE);
                binding.makemodelRc.setVisibility(View.GONE);
                binding.apartlistRc.setVisibility(View.GONE);
                binding.preferredscheduleRc.setVisibility(View.GONE);
                binding.preferredtimeRc.setVisibility(View.GONE);
                //PreferredAdapter preferredAdapter1 = new PreferredAdapter(MyVechiclesAddActivity.this,parkingArea,"3");
                //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyVechiclesAddActivity.this,LinearLayoutManager.VERTICAL,false);
                //binding.parkingAreaRc.setLayoutManager(linearLayoutManager);
                //binding.parkingAreaRc.setAdapter(preferredAdapter1);
                break;
            case R.id.preferredschedule_edt:
                binding.vecCategoryRc.setVisibility(View.GONE);
                binding.apartRl.setVisibility(View.VISIBLE);
                binding.preferredscheduleRc.setVisibility(View.VISIBLE);
                binding.makemodelRc.setVisibility(View.GONE);
                binding.apartlistRc.setVisibility(View.GONE);
                binding.preferredtimeRc.setVisibility(View.GONE);
                binding.parkingAreaRc.setVisibility(View.GONE);
                PreferredAdapter modelAdapter = new PreferredAdapter(MyVechiclesAddActivity.this,preSchedule,"11");
                LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(MyVechiclesAddActivity.this,LinearLayoutManager.VERTICAL,false);
                binding.preferredscheduleRc.setLayoutManager(linearLayoutManager1);
                binding.preferredscheduleRc.setAdapter(modelAdapter);
                break;
            case R.id.preferredtime_edt:
//                    if (!binding.preferredscheduleEdt.equals("0")){
//                        Toast.makeText(MyVechiclesAddActivity.this,"Choose Preferred Schedule",Toast.LENGTH_SHORT).show();
//                    }
                if (TextUtils.isEmpty(binding.preferredscheduleEdt.getText().toString())){
                    Toast.makeText(MyVechiclesAddActivity.this,"Choose Preferred Schedule",Toast.LENGTH_SHORT).show();
                }else {
                    binding.vecCategoryRc.setVisibility(View.GONE);
                    binding.apartRl.setVisibility(View.VISIBLE);
                    binding.preferredtimeRc.setVisibility(View.VISIBLE);
                    binding.makemodelRc.setVisibility(View.GONE);
                    binding.apartlistRc.setVisibility(View.GONE);
                    binding.parkingAreaRc.setVisibility(View.GONE);
                    binding.preferredscheduleRc.setVisibility(View.GONE);
                    if (binding.preferredscheduleEdt.getText().toString().equalsIgnoreCase(Constant.MORNING)) {
                        Log.e("PreTIme", Constant.MORNING);
                        PreferredAdapter preferredAdapter = new PreferredAdapter(MyVechiclesAddActivity.this, preMorTime, "22");
                        LinearLayoutManager linearLayoutManage = new LinearLayoutManager(MyVechiclesAddActivity.this, LinearLayoutManager.VERTICAL, false);
                        binding.preferredtimeRc.setLayoutManager(linearLayoutManage);
                        binding.preferredtimeRc.setAdapter(preferredAdapter);
                    } else if (binding.preferredscheduleEdt.getText().toString().equalsIgnoreCase(Constant.EVENING)) {
                        Log.e("PreTIme", Constant.EVENING);
                        PreferredAdapter preferredAdapter = new PreferredAdapter(MyVechiclesAddActivity.this, preEveTime, "22");
                        LinearLayoutManager linearLayoutManage = new LinearLayoutManager(MyVechiclesAddActivity.this, LinearLayoutManager.VERTICAL, false);
                        binding.preferredtimeRc.setLayoutManager(linearLayoutManage);
                        binding.preferredtimeRc.setAdapter(preferredAdapter);
                    }
                       /* if (binding.preferredscheduleEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.MORNING)) {
                            Log.e("PreTIme", Constant.MORNING);
                            PreferredAdapter preferredAdapter = new PreferredAdapter(MyVechiclesAddActivity.this, preMorTime, "2");
                            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(MyVechiclesAddActivity.this, LinearLayoutManager.VERTICAL, false);
                            binding.preferredtimeRc.setLayoutManager(linearLayoutManager1);
                            binding.preferredtimeRc.setAdapter(preferredAdapter);
                        } else if (binding.preferredscheduleEdt.getSelectedItem().toString().equalsIgnoreCase(Constant.EVENING)) {
                            Log.e("PreTIme", Constant.EVENING);
                            PreferredAdapter preferredAdapter = new PreferredAdapter(MyVechiclesAddActivity.this, preEveTime, "2");
                            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(MyVechiclesAddActivity.this, LinearLayoutManager.VERTICAL, false);
                            binding.preferredtimeRc.setLayoutManager(linearLayoutManager1);
                            binding.preferredtimeRc.setAdapter(preferredAdapter);
                        }*/
                }

                break;
            case R.id.apart_rl:
                binding.vecCategoryRc.setVisibility(View.GONE);
                binding.apartRl.setVisibility(View.GONE);
                binding.preferredtimeRc.setVisibility(View.GONE);
                binding.makemodelRc.setVisibility(View.GONE);
                binding.apartlistRc.setVisibility(View.GONE);
                binding.preferredscheduleRc.setVisibility(View.GONE);
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