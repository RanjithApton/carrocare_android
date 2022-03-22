package com.muvierecktech.carrocare.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.DatabaseHelper;
import com.muvierecktech.carrocare.common.MyDatabaseHelper;
import com.muvierecktech.carrocare.common.PostServiceChooseDialog;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityMainProfileBinding;


import java.util.HashMap;

public class MainProfileActivity extends AppCompatActivity implements PostServiceChooseDialog.AlertPositiveListener {
    ActivityMainProfileBinding binding;
    SessionManager sessionManager;
    String name,mobile;
    MyDatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main_profile);
        sessionManager = new SessionManager(this);
        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        name = hashMap.get(SessionManager.KEY_USERNAME);
        mobile = hashMap.get(SessionManager.KEY_USERMOBILE);

        databaseHelper = new MyDatabaseHelper(this);

        String versionName = "";
        try {
            PackageInfo packageInfo = MainProfileActivity.this.getPackageManager().getPackageInfo(MainProfileActivity.this.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        binding.versionEdt.setText("Version "+versionName+"\n"+"© Muviereck Technologies Pvt Ltd.");

        binding.profilename.setText(name);
        binding.profileNum.setText(mobile);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                onBackPressed();
            }
        });
        binding.homeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        binding.serviceTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PostServiceChooseDialog().show(getSupportFragmentManager(), "alert_dialog_category_choose");
            }
        });
        binding.profileTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });
        binding.myvehicleTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this,MyVehiclesActivity.class);
                startActivity(intent);
            }
        });
        binding.logoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainProfileActivity.this);
                builder.setTitle("Logout");
                builder.setMessage("Are you want to exit ?");;
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.DeleteAllOrderData();
                        sessionManager.logoutUsers();
                    }
                });
                builder.setNegativeButton("No",null);
                builder.show();
            }
        });
        binding.myordersTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this,MyOrdersActivity.class);
                startActivity(intent);
            }
        });
        binding.privacyTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this,WebviewActivity.class);
                intent.putExtra("headername","Privacy Policy");
                intent.putExtra("link", Constant.PRIVACYPOLICY);
                startActivity(intent);
            }
        });
        binding.tandconTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this,WebviewActivity.class);
                intent.putExtra("headername","Terms And Conditions");
                intent.putExtra("link", Constant.TERMSANDCONDITIONS);
                startActivity(intent);
            }
        });
        binding.faqTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this,WebviewActivity.class);
                intent.putExtra("headername","FAQ");
                intent.putExtra("link", Constant.FAQ);
                startActivity(intent);
            }
        });
        binding.aboutusTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this,WebviewActivity.class);
                intent.putExtra("headername","About Us");
                intent.putExtra("link", Constant.ABOUTUS);
                startActivity(intent);
            }
        });
        binding.contactusTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this,ContactUsActivity.class);
                startActivity(intent);
            }
        });
        binding.helpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainProfileActivity.this,HelpAndSupportActivity.class);
                startActivity(intent);
            }
        });
        binding.shareappTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApp();
            }
        });
        binding.rateourappTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateApp();

            }
        });
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void shareApp() {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, Download this awesome app! \n " +
                    "https://play.google.com/store/apps/details?id=com.muvierecktech.carrocare");
            shareIntent.setType("text/plain");
            try {
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
            }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MainProfileActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void apartmentSevice() {
        sessionManager.setData(SessionManager.USER_WANTS,"apartment");
        Intent intent = new Intent(MainProfileActivity.this,ApartmentServiceActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void doorstepService() {
        sessionManager.setData(SessionManager.USER_WANTS,"doorstep");
        Intent intent = new Intent(MainProfileActivity.this,DoorStepServiceActivity.class);
        startActivity(intent);
        finish();

    }
}