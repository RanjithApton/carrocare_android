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

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                System.out.println("=====n_response " + json.toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void sendPushNotification(JSONObject json) {
        try {

            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            String imageUrl = data.getString("image");

            String type = data.getString("type");
            String id = data.getString("id");

            Intent intent = null;
            if (type != null) {
                if (type.equals("carwash")){
                    intent = new Intent(getApplicationContext(), CarWashActivity.class);
//                        intent.putExtra("id", id);
                } else if (type.equals("bikewash")){
                    intent = new Intent(getApplicationContext(), BikeWashActivity.class);
//                        intent.putExtra("id", id);
                } else if (type.equals("waxpolish")){
                    intent = new Intent(getApplicationContext(), AddOnActivity.class);
//                        intent.putExtra("id", id);
                }else if (type.equals("extra")){
                    intent = new Intent(getApplicationContext(), ExtraInteriorActivity.class);
//                        intent.putExtra("id", id);
                } else if (type.equals("carpolish")){
                    intent = new Intent(getApplicationContext(), CarPolishActivity.class);
//                        intent.putExtra("id", id);
                } else if (type.equals("insurance")){
                    intent = new Intent(getApplicationContext(), ConfirmFormActivity.class);
//                        intent.putExtra("id", id);
                } else if (type.equals("orders")){
                    intent = new Intent(getApplicationContext(), MyOrdersActivity.class);
//                        intent.putExtra("id", id);
                } else if (type.equals("cart")){
                    intent = new Intent(getApplicationContext(), CartActivity.class);
//                        intent.putExtra("id", id);
                }else {
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                }
            }
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
            //Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            if (imageUrl.equals("null") || imageUrl.equals("")) {
                mNotificationManager.showSmallNotification(title, message, intent);
            } else {
                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }


        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
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
