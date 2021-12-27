package com.muvierecktech.carrocare.model;

import java.util.List;

public class VehicleDetails {
    public  String code;
    public  String status;
    public List<VecDetails> details;

    public static class VecDetails {
        public  String vehicle_id;
        public  String vehicle_Type;
        public  String vehicle_make;
        public  String vehicle_model;
        public  String vehicle_category;
        public  String vehicle_no;
        public  String vehicle_color;
        public  String vehicle_apartment_name;
        public  String vehicle_parking_lot_no;
        public  String vehicle_parking_area;
        public  String vehicle_preferred_schedule;
        public  String vehicle_preferred_time;
        public  String vehicle_image;



    }
}
