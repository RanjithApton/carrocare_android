package com.muvierecktech.carrocare.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.muvierecktech.carrocare.activity.LoginActivity;

import java.util.HashMap;

public class SessionManager {
    public static final String PREF_NAME = "loginPreferenceUser";
    public static final String IS_USER_LOGIN = "IsUserLoggedInUser";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USEREMAIL = "useremail";
    public static final String KEY_USERID = "userid";
    public static final String KEY_USERMOBILE = "usermobile";
    public static final String KEY_STATUS = "status";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_APARTNAME = "apartment_name";
    public static final String KEY_APARTBUILDING = "apartment_building";
    public static final String KEY_FLATNO = "flat_no";
    public static final String MAP_ADDRESS = "map_address";
    public static final String MAP_LATITUDE = "map_latitude";
    public static final String MAP_LONGITUDE = "map_longitude";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String USER_WANTS = "user_wants";
    public static final String GST_PERCENTAGE = "gst_percentage";
    static SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    DatabaseHelper databaseHelper;
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public static void setDeviceToken(String token) {
        sharedPreferences.edit().putString("DEVICETOKEN", token).apply();
    }

    public void setData(String id, String val) {
        editor.putString(id, val);
        editor.commit();
    }

    public String getData(String id) {
        return sharedPreferences.getString(id, "");
    }

    public String getCoordinates(String id) {
        return sharedPreferences.getString(id, "0");
    }

    public void UserName(String seekbar) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_USERNAME, String.valueOf(seekbar));
        Log.e("Sessioncreated", String.valueOf(seekbar) + "");
        editor.commit();
    }

    public void UserEmail(String seekbar) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_USEREMAIL, String.valueOf(seekbar));
        Log.e("Sessioncreated", String.valueOf(seekbar) + "");
        editor.commit();
    }

    public void UserId(String seekbar) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_USERID, String.valueOf(seekbar));
        Log.e("Sessioncreated", String.valueOf(seekbar) + "");
        editor.commit();
    }

    public void UserMobile(String seekbar) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_USERMOBILE, String.valueOf(seekbar));
        Log.e("Sessioncreated", String.valueOf(seekbar) + "");
        editor.commit();
    }

    public void UserToken(String seekbar) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_TOKEN, String.valueOf(seekbar));
        Log.e("Sessioncreated", String.valueOf(seekbar) + "");
        editor.commit();
    }

    public void UserStatus(String seekbar) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_STATUS, String.valueOf(seekbar));
        Log.e("Sessioncreated", String.valueOf(seekbar) + "");
        editor.commit();
    }

    public void UserApartBuilding(String seekbar) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_APARTBUILDING, String.valueOf(seekbar));
        Log.e("Sessioncreated", String.valueOf(seekbar) + "");
        editor.commit();
    }

    public void UserApartName(String seekbar) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_APARTNAME, String.valueOf(seekbar));
        Log.e("Sessioncreated", String.valueOf(seekbar) + "");
        editor.commit();
    }

    public void UserFlatno(String seekbar) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_FLATNO, String.valueOf(seekbar));
        Log.e("Sessioncreated", String.valueOf(seekbar) + "");
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> users = new HashMap<>();
        users.put(KEY_USERNAME, sharedPreferences.getString(KEY_USERNAME, null));
        users.put(KEY_USEREMAIL, sharedPreferences.getString(KEY_USEREMAIL, null));
        users.put(KEY_USERID, sharedPreferences.getString(KEY_USERID, null));
        users.put(KEY_USERMOBILE, sharedPreferences.getString(KEY_USERMOBILE, null));
        users.put(KEY_STATUS, sharedPreferences.getString(KEY_STATUS, null));
        users.put(KEY_TOKEN, sharedPreferences.getString(KEY_TOKEN, null));
        users.put(KEY_APARTBUILDING, sharedPreferences.getString(KEY_APARTBUILDING, null));
        users.put(KEY_APARTNAME, sharedPreferences.getString(KEY_APARTNAME, null));
        users.put(KEY_FLATNO, sharedPreferences.getString(KEY_FLATNO, null));
        return users;
    }

    public void logoutUsers() {
        databaseHelper = new DatabaseHelper(context);
        databaseHelper.DeleteAllCartData();
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(i);
    }
}
