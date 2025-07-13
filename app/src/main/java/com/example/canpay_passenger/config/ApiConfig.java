package com.example.canpay_passenger.config;

public class ApiConfig {
    public static final String USER_ROLE = "PASSENGER";


    public static String getBaseUrl() {
//       return "http://10.0.2.2:8081"; // Emulator
        return "https://api-v1-canpay.sehanw.com";
//        return "http://10.189.23.158:8081";

    }
}