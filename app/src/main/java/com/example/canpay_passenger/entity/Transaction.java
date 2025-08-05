package com.example.canpay_passenger.entity; // this is for demo purpose

public class Transaction {
    private String name;
    private String amount;
    private String date;
    private String time;

    private int iconResId; // for the arrow icon (optional)
    public String note;

    private String transactionType;

    public Transaction(String name, String amount, String date, int iconResId) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.iconResId = iconResId;
    }

    public Transaction(String name, String amount, String date, String note) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.note = note;
    }

    public Transaction(String name, String amount, String date, String time, String transactionType, String note) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.transactionType = transactionType;
        this.note = note;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getName() { return name; }
    public String getAmount() { return amount; }
    public String getDate() { return date; }
    public int getIconResId() { return iconResId; }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
}

