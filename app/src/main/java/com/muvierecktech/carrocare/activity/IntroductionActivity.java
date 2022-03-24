package com.muvierecktech.carrocare.activity;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.SessionManager;

import java.util.HashMap;

public class IntroductionActivity extends AppCompatActivity {
    SharedPreferences prefs = null;
    private static int SPLASH_TIME_OUT = 2000;
    private Thread startThread;
    SessionManager sessionManager;
    String accesstoken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        // Perhaps set content view here
        prefs = getSharedPreferences("com.muvierecktech.carrocare.activity", MODE_PRIVATE);
        sessionManager = new SessionManager(this);

        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        accesstoken = hashMap.get(SessionManager.KEY_TOKEN);

        /*started thread*/
        Runnable threadJob = new MyRunnable();
        startThread = new Thread(threadJob);
        startThread.start();

    }
    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            try {
                startThread.sleep(SPLASH_TIME_OUT);
               if (accesstoken!=null){
                    Intent intent = new Intent(IntroductionActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(IntroductionActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}