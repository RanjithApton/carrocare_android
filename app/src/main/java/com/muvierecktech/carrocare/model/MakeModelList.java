package com.muvierecktech.carrocare.model;

import java.util.List;

public class MakeModelList {
    public String status;
    public String code;
    public String message;
    public List<Vehicle> vehicle;

    public static class Vehicle {
        public String vehicle_make;
        public String vehicle_model;
    }
}
