package com.muvierecktech.carrocare.model;

import java.io.Serializable;
import java.util.List;

public class OrdersList {
    public  String status;
    public  String code;
    public  String message;
    public List<Orders> orders;

    public class Orders {
        public  String date_and_time;
        public  String order_id;
        public  String service_type;
        public  String payment_type;
        public  String package_type;
        public  String vehicle_make;
        public  String vehicle_model;
        public  String vehicle_no;
        public  String vehicle_id;
        public  String package_value;
        public  String total_amount;
        public  String gst;
        public  String gst_amount;
        public  String sub_total_amount;
        public  String discount_amount;
        public  String payment_mode;
        public  String valid;
        public  String paid_count;
        public  String next_due;
        public  String status;
        public  String reason;
        public  String wash_details;
        public  String extra_interior;
        public  String cancel_subscription;
        public  String payment_history;
        public String schedule_date;
        public String schedule_time;
        public  String vehicle_image;
        public String image_date_and_time;
        public String work_done;
        public List<PaymentDetails> payment_details;

    }

    public static class PaymentDetails implements Serializable {
        public  String payment_date;
        public  String razorpay_payment_id;
        public  String amount;
        public  String status;
    }
}
