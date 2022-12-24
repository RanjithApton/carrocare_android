package com.muvierecktech.carrocare.model;

import java.util.List;

public class DoorStepCarWash {
    public String status;
    public String code;
    public String description;
    public List<Service> services;

    public class Service {
        public String service;
        public String image;
        public String type;
        public String prices;
        public String description;
        public String status;
    }

}
