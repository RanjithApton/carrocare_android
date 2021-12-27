package com.muvierecktech.carrocare.activity;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.SessionManager;

import java.util.HashMap;

public class IntroActivity extends AppCompatActivity {
    SharedPreferences prefs = null;
    private static int SPLASH_TIME_OUT = 2000;
    private Thread startThread;
    SessionManager sessionManager;
    String accesstoken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        // Perhaps set content view here
        prefs = getSharedPreferences("com.muvierecktech.carrocare", MODE_PRIVATE);
        sessionManager = new SessionManager(this);

        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        accesstoken = hashMap.get(SessionManager.KEY_TOKEN);

        /*started thread*/
        Runnable threadJob = new MyRunnable();
        startThread = new Thread(threadJob);
        startThread.start();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (prefs.getBoolean("firstrun", true)) {
//            // Do first run stuff here then set 'firstrun' as false
//            // using the following line to edit/commit prefs
//            prefs.edit().putBoolean("firstrun", false).commit();
//            Intent intent = new Intent(IntroActivity.this,SplashActivity.class);
//            startActivity(intent);
//            finish();
//        }else {
//            Intent intent = new Intent(IntroActivity.this,LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            try {
                startThread.sleep(SPLASH_TIME_OUT);
                if (prefs.getBoolean("firstrun", true)) {
                    // Do first run stuff here then set 'firstrun' as false
                    // using the following line to edit/commit prefs
                    prefs.edit().putBoolean("firstrun", false).commit();
                    Intent intent = new Intent(IntroActivity.this,SplashActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(IntroActivity.this,IntroductionActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
