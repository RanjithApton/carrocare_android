package com.muvierecktech.carrocare.model;

public class SettingsList {
    public String code;
    public String status;
    public String message;
    public Settingresponse res;

    public static class Settingresponse {
        public int gst;
        public String current_version;
        public String minimum_version;
    }
}
