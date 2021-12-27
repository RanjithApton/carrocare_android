package com.muvierecktech.carrocare.model;

import java.util.List;

public class VehicleExtraList {
    public String code;
    public String status;
    public String message;
    public List<ExtraInterior> extra_interior;


    public class ExtraInterior {
        public String date_and_time;
        public String order_id;
        public String vehicle_id;
        public String schedule_date;
        public String schedule_time;
        public String comment_box;
        public String schedule_work_status;
        public String vehicle_image;
        public String vehicle_image_dateandtime;
        public String updated_date;
        public String updated_time;
    }
}
