package com.muvierecktech.carrocare.common;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApiConfig {

    public static String getAddress(double lat, double lng, Activity activity) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() != 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                address = add;
            }
        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }

    public static String parseDateToddMMyyyyHHMMAMPM(String time) {
        String inputPattern = "EEE d MMM h:mm a";
        String outputPattern = "MM-dd HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = ApiConfig.yearcurrent() + "-" + outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String spiltDate(String time) {
        String inputPattern = "EEE d MMM h:mm a";
        String outputPattern = "MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = ApiConfig.yearcurrent() + "-" + outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String spiltTime(String time) {
        String inputPattern = "EEE d MMM h:mm a";
        String outputPattern = "HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str =  outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String yearcurrent() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }





}
