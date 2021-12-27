package com.muvierecktech.carrocare.model;

import java.util.List;

public class OneTimeWashCheckout {
    public String code;
    public List<getResult> result;
    public String staus;

    public static class getResult {
        public String discount_amount;
        public String fine_amount;
        public String gst_amount;
        public String order_exists;
        public String show_onetime;
        public String total_amount;
    }
}
