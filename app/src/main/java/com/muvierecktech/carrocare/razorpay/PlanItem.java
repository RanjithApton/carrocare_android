package com.muvierecktech.carrocare.razorpay;

public class PlanItem {
    private String name;
    private int amount;
    private String currency;

    public PlanItem(String name, int amount, String currency) {
        this.name = name;
        this.amount = amount;
        this.currency = currency;
    }

    // Getters and Setters (if needed)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}