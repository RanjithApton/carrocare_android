package com.muvierecktech.carrocare.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CouponCodeModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("coupon_data")
    @Expose
    private List<CouponDatum> couponData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<CouponDatum> getCouponData() {
        return couponData;
    }

    public void setCouponData(List<CouponDatum> couponData) {
        this.couponData = couponData;
    }

    public class CouponDatum {

        @SerializedName("c_id")
        @Expose
        private String cId;
        @SerializedName("coupon_discount")
        @Expose
        private String couponDiscount;
        @SerializedName("message")
        @Expose
        private String message;

        public String getcId() {
            return cId;
        }

        public void setcId(String cId) {
            this.cId = cId;
        }

        public String getCouponDiscount() {
            return couponDiscount;
        }

        public void setCouponDiscount(String couponDiscount) {
            this.couponDiscount = couponDiscount;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}