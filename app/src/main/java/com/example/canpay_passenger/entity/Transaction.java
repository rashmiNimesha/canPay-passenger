package com.example.canpay_passenger.entity; // this is for demo purpose

public class Transaction {
    private String name;
    private String amount;
    private String date;
    private int iconResId; // for the arrow icon (optional)
    public String note;


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
}

