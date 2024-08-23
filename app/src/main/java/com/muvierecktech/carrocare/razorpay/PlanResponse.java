package com.muvierecktech.carrocare.razorpay;

public class PlanResponse {
    private String id;
    private String entity;
    private String period;
    private int interval;

    // Define more fields based on Razorpay's API response
    private PlanItem item;

    // Getters and Setters (if needed)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public PlanItem getItem() {
        return item;
    }

    public void setItem(PlanItem item) {
        this.item = item;
    }
}
