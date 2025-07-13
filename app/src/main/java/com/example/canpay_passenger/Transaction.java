package com.example.canpay_passenger; // this is for demo purpose

public class Transaction {
    private String name;
    private String amount;
    private String date;
    private int iconResId; // for the arrow icon (optional)

    public Transaction(String name, String amount, String date, int iconResId) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.iconResId = iconResId;
    }

    public String getName() { return name; }
    public String getAmount() { return amount; }
    public String getDate() { return date; }
    public int getIconResId() { return iconResId; }
}

