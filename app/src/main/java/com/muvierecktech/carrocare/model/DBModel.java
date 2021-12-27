package com.muvierecktech.carrocare.model;

public class DBModel {
    int sno;
    String cusid;
    String token;
    String type;
    String imge;
    String model;
    String number;
    String carprice;
    String carid;
    String paidmonth;
    String fine;
    String total;
    String date;
    String time;


    public DBModel(int sno, String cusid, String token, String type, String imge, String model, String number, String carprice, String carid, String paidmonth, String fine, String total, String date, String time) {
        this.sno = sno;
        this.cusid = cusid;
        this.token = token;
        this.type = type;
        this.imge = imge;
        this.model = model;
        this.number = number;
        this.carprice = carprice;
        this.carid = carid;
        this.paidmonth = paidmonth;
        this.fine = fine;
        this.total = total;
        this.date = date;
        this.time = time;
    }

    public DBModel() {

    }

    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    public String getCusid() {
        return cusid;
    }

    public void setCusid(String cusid) {
        this.cusid = cusid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
