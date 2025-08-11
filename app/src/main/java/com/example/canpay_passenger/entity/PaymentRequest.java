package com.example.canpay_passenger.entity;

public class PaymentRequest {
    private String busId;
    private String operatorId;
    private String amount;

    public PaymentRequest(String busId, String amount) {
        this.busId = busId;
        this.amount = amount;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}