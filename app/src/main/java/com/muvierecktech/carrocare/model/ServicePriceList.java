package com.muvierecktech.carrocare.model;

import java.util.List;

public class ServicePriceList {
    public  String status;
    public  String code;
    public  String description;
    public List<Services> services;

    public class Services {
        public  String id;
        public  String image;
        public  String type;
        public  String prices;
        public  String description;
    }
}
