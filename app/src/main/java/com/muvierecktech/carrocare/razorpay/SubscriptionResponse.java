package com.muvierecktech.carrocare.razorpay;

public class SubscriptionResponse {
    // Define fields based on the response from Razorpay's subscription API
    private String id;
    private String status;

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
