package com.muvierecktech.carrocare.razorpay;

public class SubscriptionRequest {
    private String plan_id;
    private int customer_notify;
    private int total_count;

    public SubscriptionRequest(String plan_id, int customer_notify, int total_count) {
        this.plan_id = plan_id;
        this.customer_notify = customer_notify;
        this.total_count = total_count;
    }

    // Getters and Setters (if needed)
}
