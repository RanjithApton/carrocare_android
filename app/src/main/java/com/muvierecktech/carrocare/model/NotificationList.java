package com.muvierecktech.carrocare.model;

import java.util.List;

public class NotificationList {
    public String code;
    public String message;
    public String status;
    public List<Notifications> notifications;

    public class Notifications {
        public String id;
        public String title;
        public String description;

    }
}
