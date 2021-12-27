package com.muvierecktech.carrocare.model;

import java.util.List;

public class VehicleWashList {
    public String code;
    public String status;
    public String message;
    public List<WashDetails> wash_details;
    public List<InternalDetails> internal_details;
    public class WashDetails {
        public String date;
        public String vehicle_wash_id;
        public String employee_id;
        public String wash_status;
        public String note;
        public String vehicle_image;
        public String vehicle_image_dateandtime;
        public String updated_date;
        public String updated_time;
    }
    public class InternalDetails {
        public String id;
        public String vehicle_id;
        public String schedule_date1;
        public String schedule_time1;
        public String schedule_work_status1;
        public String vehicle_image1;
        public String vehicle_image1_dateandtime;
        public String schedule_date2;
        public String schedule_time2;
        public String schedule_work_status2;
        public String vehicle_image2;
        public String vehicle_image2_dateandtime;
        public String updated_date;
        public String updated_time;
    }
}
