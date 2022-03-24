package com.muvierecktech.carrocare.model;

public class CartList {
    public String type;
    public String imge;
    public String model;
    public String number;
    public String carprice;
    public String carid;
    public String paidmonth;
    public String fine;
    public String sub_total;
    public String gst;
    public String gstamount;
    public String total;
    public String date;
    public String time;

    public CartList() {

    }

    public CartList(String type, String imge, String model, String number, String carprice, String carid, String paidmonth, String fine, String sub_total, String gst, String gstamount, String total, String date, String time) {
        this.type = type;
        this.imge = imge;
        this.model = model;
        this.number = number;
        this.carprice = carprice;
        this.carid = carid;
        this.paidmonth = paidmonth;
        this.fine = fine;
        this.sub_total = sub_total;
        this.gst = gst;
        this.gstamount = gstamount;
        this.total = total;
        this.date = date;
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImge() {
        return imge;
    }

    public void setImge(String imge) {
        this.imge = imge;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCarprice() {
        return carprice;
    }

    public void setCarprice(String carprice) {
        this.carprice = carprice;
    }

    public String getCarid() {
        return carid;
    }

    public void setCarid(String carid) {
        this.carid = carid;
    }

    public String getPaidmonth() {
        return paidmonth;
    }

    public void setPaidmonth(String paidmonth) {
        this.paidmonth = paidmonth;
    }

    public String getFine() {
        return fine;
    }

    public void setFine(String fine) {
        this.fine = fine;
    }

    public String getSub_total() {
        return sub_total;
    }

    public void setSub_total(String sub_total) {
        this.sub_total = sub_total;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getGstamount() {
        return gstamount;
    }

    public void setGstamount(String gstamount) {
        this.gstamount = gstamount;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
