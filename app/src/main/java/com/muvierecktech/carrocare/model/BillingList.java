package com.muvierecktech.carrocare.model;

import java.util.List;

public class BillingList {

    public List<Res> res;
    public String message;
    public String code;
    public String status;

    public static class Res {
        public String download_invoice;
        public String amount;
        public String razorpay_payment_id;
        public String invoice;
        public String date;
    }
}
