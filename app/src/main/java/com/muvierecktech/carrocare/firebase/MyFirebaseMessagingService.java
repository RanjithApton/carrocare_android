package com.muvierecktech.carrocare.firebase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.muvierecktech.carrocare.activity.AddOnActivity;
import com.muvierecktech.carrocare.activity.BikeWashActivity;
import com.muvierecktech.carrocare.activity.CarPolishActivity;
import com.muvierecktech.carrocare.activity.CarWashActivity;
import com.muvierecktech.carrocare.activity.CartActivity;
import com.muvierecktech.carrocare.activity.ConfirmFormActivity;
import com.muvierecktech.carrocare.activity.ExtraInteriorActivity;
import com.muvierecktech.carrocare.activity.MainActivity;
import com.muvierecktech.carrocare.activity.MyOrdersActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            try {
                RemoteMessage.Notification notification = remoteMessage.getNotification();
                Map<String, String> data = remoteMessage.getData();

//                JSONObject json = new JSONObject(remoteMessage.getData().toString());
//                System.out.println("=====n_response " + json.toString());
                Log.e(TAG, data.toString());
                Log.e(TAG, "onMessageReceived: DATA" + remoteMessage.getData().get("message"));
                sendPushNotification(data);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        } else {
            Log.e(TAG, remoteMessage.getFrom());
        }
    }

    private void sendPushNotification(Map<String, String> data) {
        String title = "";
        String message = "";
        String type = "";
        String id = "";
        String imageUrl = "";
        try {

            title = data.get("title");
            message = data.get("message");
            imageUrl = data.get("image");

            type = data.get("type");
            id = data.get("id");

            Intent intent = null;
            if (type.equals("carwash")) {
                intent = new Intent(getApplicationContext(), CarWashActivity.class);
//                        intent.putExtra("id", id);
            } else if (type.equals("bikewash")) {
                intent = new Intent(getApplicationContext(), BikeWashActivity.class);
//                        intent.putExtra("id", id);
            } else if (type.equals("waxpolish")) {
                intent = new Intent(getApplicationContext(), AddOnActivity.class);
//                        intent.putExtra("id", id);
            } else if (type.equals("extra")) {
                intent = new Intent(getApplicationContext(), ExtraInteriorActivity.class);
//                        intent.putExtra("id", id);
            } else if (type.equals("carpolish")) {
                intent = new Intent(getApplicationContext(), CarPolishActivity.class);
//                        intent.putExtra("id", id);
            } else if (type.equals("insurance")) {
                intent = new Intent(getApplicationContext(), ConfirmFormActivity.class);
//                        intent.putExtra("id", id);
            } else if (type.equals("orders")) {
                intent = new Intent(getApplicationContext(), MyOrdersActivity.class);
//                        intent.putExtra("id", id);
            } else if (type.equals("cart")) {
                intent = new Intent(getApplicationContext(), CartActivity.class);
//                        intent.putExtra("id", id);
            } else {
                intent = new Intent(getApplicationContext(), MainActivity.class);
            }
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
            //intent = new Intent(getApplicationContext(), MainActivity.class);

            if (imageUrl.equals("null") || imageUrl.equals("")) {
                mNotificationManager.showSmallNotification(title, message, intent);
            } else {
                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }


        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
//        AppController.getInstance().setDeviceToken(s);
        //MainActivity.UpdateToken(s);
    }

}
