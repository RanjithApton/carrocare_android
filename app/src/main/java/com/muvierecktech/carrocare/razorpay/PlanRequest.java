package com.muvierecktech.carrocare.razorpay;

public class PlanRequest {
    private String period;
    private int interval;
    private PlanItem item;

    public PlanRequest(String period, int interval, PlanItem item) {
        this.period = period;
        this.interval = interval;
        this.item = item;
    }

    // Getters and Setters (if needed)

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
