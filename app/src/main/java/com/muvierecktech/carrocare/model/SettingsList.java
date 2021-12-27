package com.muvierecktech.carrocare.model;

public class SettingsList {
    public String code;
    public String status;
    public String message;
    public Settingresponse res;

    public class Settingresponse {
        public String current_version;
        public String minimum_version;
    }
}
