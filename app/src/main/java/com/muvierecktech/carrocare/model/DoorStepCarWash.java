package com.muvierecktech.carrocare.model;

import java.util.List;

public class DoorStepCarWash {
    public  String status;
    public  String code;
    public  String description;
    public Exterior_Wash Exterior_Wash;

    public class Exterior_Wash {
        public List<Service> service;
        public  String description;
        public class Service{
            public  String image;
            public  String type;
            public  String prices;
        }
    }

    public Interior_Cleaning Interior_Cleaning;

    public class Interior_Cleaning {
        public List<Service> service;
        public  String description;
        public class Service{
            public  String image;
            public  String type;
            public  String prices;
        }
    }

    public Engine_Polishing Engine_Polishing;

    public class Engine_Polishing {
        public List<Service> service;
        public  String description;
        public class Service{
            public  String image;
            public  String type;
            public  String prices;
        }
    }

    public Quick_Wax Quick_Wax;

    public class Quick_Wax {
        public List<Service> service;
        public  String description;
        public class Service{
            public  String image;
            public  String type;
            public  String prices;
        }
    }

    public Car_Sanitisation Car_Sanitisation;

    public class Car_Sanitisation {
        public List<Service> service;
        public  String description;
        public class Service{
            public  String image;
            public  String type;
            public  String prices;
        }

    }

    public Car_Polishing Car_Polishing;

    public class Car_Polishing {
        public List<Service> service;
        public  String description;
        public class Service{
            public  String image;
            public  String type;
            public  String prices;
        }

    }

    public Interior_Detailing Interior_Detailing;

    public class Interior_Detailing {
        public List<Service> service;
        public  String description;
        public class Service{
            public  String image;
            public  String type;
            public  String prices;
        }

    }

    public window_tinting window_tinting;

    public class window_tinting {
        public List<Service> service;
        public  String description;
        public class Service{
            public  String image;
            public  String type;
            public  String prices;
        }

    }


}
