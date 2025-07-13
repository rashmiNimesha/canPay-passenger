package com.example.canpay_passenger.utils;

public class Endpoints {
    public static final String SEND_OTP = "/api/v1/auth/send-otp";
    public static final String VERIFY_OTP = "/api/v1/auth/verify-otp";
    public static final String CREATE_PROFILE = "/api/v1/auth/create-profile";

    public static final String LOAD_BANK_ACCS = "/api/v1/bank-account/by-email";

    public static final String LOAD_BANK_LIST = "/api/v1/bank-account/list";
public static final String ADD_BANK_ACCOUNT = "/api/v1/bank-account/add";

    public  static final String  CHECK_USER = "/api/v1/auth/check-user";
    public static final String PASSENGER_WALLET_RECHARGE = "/api/v1/wallet/recharge";

}