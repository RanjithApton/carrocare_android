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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.Collections;
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
    ArrayList<String> spinner_item;
    ArrayList<String> spinner_schedule;
    ArrayList<String> spinner_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_vechicles_add);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_vechicles_add);
        sessionManager = new SessionManager(this);
        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        customerid = hashMap.get(SessionManager.KEY_USERID);
        token = hashMap.get(SessionManager.KEY_TOKEN);

        spinner_item = new ArrayList<>();
        spinner_schedule = new ArrayList<>();
        spinner_time = new ArrayList<>();
        Collections.addAll(spinner_item, vechicleCategory);
        Collections.addAll(spinner_schedule, preSchedule);


        binding.vecCategoryEdt.setOnClickListener(this);
        binding.makeModelEdt.setOnClickListener(this);
        binding.apartmentEdt.setOnClickListener(this);
        binding.parkingAreaEdt.setOnClickListener(this);
        binding.preferredscheduleEdt.setOnClickListener(this);
        binding.preferredtimeEdt.setOnClickListener(this);
        //binding.apartRl.setOnClickListener(this);
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
                    //apartmentname.add(0,Constant.APARTMENTNAME);
                    for(int i = 0; i < apartments.size(); i++){
                        //Storing names to string array
                        String items = apartments.get(i).name;
                        apartmentname.add(items);
//                        pos = i;
                    }
                }
            }
            @Override
            public void onFailure(Call<ApartmentList> call, Throwable t) {
                hud.dismiss();
                Toast.makeText(MyVechiclesAddActivity.this,"Timeout.Try after sometime",Toast.LENGTH_SHORT).show();
            }
        });
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
                    //parkingareaname.add(0, Constant.PARKINGNAME);
                    for (int i = 0; i < parkingarea.size(); i++) {
                        parkingareaname.add(parkingarea.get(i).name);
                    }
                }
            }

            public void onFailure(Call<ParkingareaList> call, Throwable th) {
                hud.dismiss();
                Toast.makeText(MyVechiclesAddActivity.this, "Timeout.Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void makeModelList() {
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

                    for (int i = 0; i < vehicleList.size(); i++) {
                        makemodel.addAll(Collections.singleton(vehicleList.get(i).vehicle_make + "-" + vehicleList.get(i).vehicle_model));
                    }
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


                binding.spinner.setAdapter(new ArrayAdapter<String>(MyVechiclesAddActivity.this, android.R.layout.simple_list_item_1,spinner_item));

                binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        binding.vecCategoryEdt.setText(spinner_item.get(i));
                        additionalwork(spinner_item.get(i));
                        if (spinner_item.get(i).equalsIgnoreCase("bike")){
                            binding.makeModelEdt.setText("");
                            binding.makemodelRl.setVisibility(View.GONE);
                            binding.makeEdt.setVisibility(View.VISIBLE);
                            binding.modelEdt.setVisibility(View.VISIBLE);
                        }else{
                            binding.makeModelEdt.setText("");
                            binding.makemodelRl.setVisibility(View.VISIBLE);
                            binding.makeEdt.setVisibility(View.GONE);
                            binding.modelEdt.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                binding.spinner.performClick();
                break;
            case R.id.make_model_edt:
                //spinner_item.clear();
                //spinner_item.addAll(makemodel);

                if (TextUtils.isEmpty(binding.vecCategoryEdt.getText().toString())){
                    Toast.makeText(MyVechiclesAddActivity.this,"Choose Vechicle Category",Toast.LENGTH_SHORT).show();
                }else{
                    binding.spinner.setAdapter(new ArrayAdapter<String>(MyVechiclesAddActivity.this, android.R.layout.simple_list_item_1,makemodel));

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
            case R.id.apartment_edt:
//                spinner_item.clear();
//                spinner_item.addAll();

                binding.apartmentSpinner.setAdapter(new ArrayAdapter<String>(MyVechiclesAddActivity.this, android.R.layout.simple_list_item_1,apartmentname));

                binding.apartmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        binding.apartmentEdt.setText(apartmentname.get(i));
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                binding.apartmentSpinner.performClick();
                break;
            case R.id.parking_area_edt:
//                spinner_item.clear();
//                spinner_item.addAll(parkingareaname);

                binding.parkingSpinner.setAdapter(new ArrayAdapter<String>(MyVechiclesAddActivity.this, android.R.layout.simple_list_item_1,parkingareaname));

                binding.parkingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        binding.parkingAreaEdt.setText(parkingareaname.get(i));
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                binding.parkingSpinner.performClick();
                break;
            case R.id.preferredschedule_edt:
                //spinner_item.clear();

                //spinner_item.add(String.valueOf(preSchedule));
                binding.scheduleSpinner.setAdapter(new ArrayAdapter<String>(MyVechiclesAddActivity.this, android.R.layout.simple_list_item_1,spinner_schedule));

                binding.scheduleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        binding.preferredscheduleEdt.setText(spinner_schedule.get(i));
                        binding.preferredtimeEdt.setText(null);

                        if(spinner_schedule.get(i).equalsIgnoreCase(Constant.MORNING)){
                            spinner_time.clear();
                            Collections.addAll(spinner_time, preMorTime);
                        }else if (spinner_schedule.get(i).equalsIgnoreCase(Constant.EVENING)) {
                            spinner_time.clear();
                            Collections.addAll(spinner_time, preEveTime);
                        }

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                binding.scheduleSpinner.performClick();

                break;
            case R.id.preferredtime_edt:
                if (TextUtils.isEmpty(binding.preferredscheduleEdt.getText().toString())){
                    Toast.makeText(MyVechiclesAddActivity.this,"Choose Preferred Schedule",Toast.LENGTH_SHORT).show();
                }else {
                    // spinner_item.clear();

                    if (binding.preferredscheduleEdt.getText().toString().equalsIgnoreCase(Constant.MORNING)) {
                        Log.e("PreTIme", Constant.MORNING);
                        //
                        binding.timeSpinner.setAdapter(new ArrayAdapter<String>(MyVechiclesAddActivity.this, android.R.layout.simple_list_item_1,spinner_time));

                        binding.timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                binding.preferredtimeEdt.setText(spinner_time.get(i));
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });

                        binding.timeSpinner.performClick();
                    } else if (binding.preferredscheduleEdt.getText().toString().equalsIgnoreCase(Constant.EVENING)) {
                        Log.e("PreTIme", Constant.EVENING);
                        //Collections.addAll(spinner_item, preEveTime);
                        //Collections.addAll(spinner_item, preEveTime);
                        binding.timeSpinner.setAdapter(new ArrayAdapter<String>(MyVechiclesAddActivity.this, android.R.layout.simple_list_item_1,spinner_time));

                        binding.timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                binding.preferredtimeEdt.setText(spinner_time.get(i));
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });

                        binding.timeSpinner.performClick();
                    }
                }
                break;
            case R.id.apart_rl:
                spinner_item.clear();
                spinner_item.addAll(apartmentname);
                binding.spinner.performClick();

                binding.spinner.setAdapter(new ArrayAdapter<String>(MyVechiclesAddActivity.this, android.R.layout.simple_list_item_1,spinner_item));

                binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        binding.apartmentEdt.setText(spinner_item.get(i));
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
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