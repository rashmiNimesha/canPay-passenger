//
//package com.example.canpay_passenger.entity;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//public class Transaction implements Parcelable {
//    private String transactionId;
//    private double amount;
//    private String happenedAt;
//    private String status;
//    private String note;
//    private String passengerId;
//    private String passengerName;
//    private String passengerEmail;
//    private String fromBankAccountId; // For recharges
//    private String fromBankName;
//    private String fromAccountNumber;
//    private String toWalletId;
//    private String toWalletNumber;
//    private double toWalletBalance;
//    private String operatorId; // For payments
//    private String operatorName;
//    private String operatorEmail;
//    private String ownerId;
//    private String ownerName;
//    private String ownerEmail;
//    private String busId;
//    private String busNumber;
//    private String busRoute;
//    private String busType;
//    private String province;
//    private String fromWalletId; // For payments
//    private String fromWalletNumber;
//    private double fromWalletBalance;
//    private String transactionType; // To distinguish between recharge and payment
//
//    public Transaction(String transactionId, double amount, String happenedAt, String status, String note,
//                       String passengerId, String passengerName, String passengerEmail, String transactionType) {
//        this.transactionId = transactionId;
//        this.amount = amount;
//        this.happenedAt = happenedAt;
//        this.status = status;
//        this.note = note;
//        this.passengerId = passengerId;
//        this.passengerName = passengerName;
//        this.passengerEmail = passengerEmail;
//        this.transactionType = transactionType;
//    }
//
//    public Transaction(String conductor, String s, String happenedAt, String status) {
//        this.transactionId = conductor; // Assuming conductor is the transaction ID
//        this.amount = Double.parseDouble(s); // Assuming s is a string representation of the amount
//        this.happenedAt = happenedAt;
//        this.status = status;
//        this.note = ""; // Default note
//        this.passengerId = ""; // Default passenger ID
//        this.passengerName = ""; // Default passenger name
//        this.passengerEmail = ""; // Default passenger email
//        this.transactionType = "payment"; // Default transaction type
//    }
//
//    // Add setters and getters for all fields
//    public String getTransactionId() { return transactionId; }
//    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
//    public double getAmount() { return amount; }
//    public void setAmount(double amount) { this.amount = amount; }
//    public String getHappenedAt() { return happenedAt; }
//    public void setHappenedAt(String happenedAt) { this.happenedAt = happenedAt; }
//    public String getStatus() { return status; }
//    public void setStatus(String status) { this.status = status; }
//    public String getNote() { return note; }
//    public void setNote(String note) { this.note = note; }
//    public String getPassengerId() { return passengerId; }
//    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
//    public String getPassengerName() { return passengerName; }
//    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
//    public String getPassengerEmail() { return passengerEmail; }
//    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }
//    public String getTransactionType() { return transactionType; }
//    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
//    // Add setters and getters for other fields as needed
//
//    // Parcelable implementation
//    protected Transaction(Parcel in) {
//        transactionId = in.readString();
//        amount = in.readDouble();
//        happenedAt = in.readString();
//        status = in.readString();
//        note = in.readString();
//        passengerId = in.readString();
//        passengerName = in.readString();
//        passengerEmail = in.readString();
//        transactionType = in.readString();
//        // Add other fields as needed
//    }
//
//    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
//        @Override
//        public Transaction createFromParcel(Parcel in) {
//            return new Transaction(in);
//        }
//
//        @Override
//        public Transaction[] newArray(int size) {
//            return new Transaction[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//
//    }
//
//    public void setFromBankAccountId(String fromBankAccountId) {
//    }
//}


package com.example.canpay_passenger.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaction implements Parcelable {
    private String transactionId;
    private double amount;
    private String happenedAt;
    private String status;
    private String note;
    private String passengerId;
    private String passengerName;
    private String passengerEmail;
    private String fromBankAccountId; // For recharges
    private String fromBankName;
    private String fromAccountNumber;
    private String toWalletId;
    private String toWalletNumber;
    private double toWalletBalance;
    private String operatorId; // For payments
    private String operatorName;
    private String operatorEmail;
    private String ownerId;
    private String ownerName;
    private String ownerEmail;
    private String busId;
    private String busNumber;
    private String busRoute;
    private String busType;
    private String province;
    private String fromWalletId; // For payments
    private String fromWalletNumber;
    private double fromWalletBalance;
    private String transactionType; // To distinguish between recharge and payment

    public Transaction(String transactionId, double amount, String happenedAt, String status, String note,
                       String passengerId, String passengerName, String passengerEmail, String transactionType) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.happenedAt = happenedAt;
        this.status = status;
        this.note = note;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.transactionType = transactionType;
    }

    public Transaction(String operatorname, String amount, String happenedAt, String busNumber) {
        this.operatorName = operatorname;
        this.amount = Double.parseDouble(amount);
        this.happenedAt = happenedAt;
        this.busNumber = busNumber;
    }

    // Getters and setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getHappenedAt() { return happenedAt; }
    public void setHappenedAt(String happenedAt) { this.happenedAt = happenedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getPassengerId() { return passengerId; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getPassengerEmail() { return passengerEmail; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public String getFromBankAccountId() { return fromBankAccountId; }
    public void setFromBankAccountId(String fromBankAccountId) { this.fromBankAccountId = fromBankAccountId; }
    public String getFromBankName() { return fromBankName; }
    public void setFromBankName(String fromBankName) { this.fromBankName = fromBankName; }
    public String getFromAccountNumber() { return fromAccountNumber; }
    public void setFromAccountNumber(String fromAccountNumber) { this.fromAccountNumber = fromAccountNumber; }
    public String getToWalletId() { return toWalletId; }
    public void setToWalletId(String toWalletId) { this.toWalletId = toWalletId; }
    public String getToWalletNumber() { return toWalletNumber; }
    public void setToWalletNumber(String toWalletNumber) { this.toWalletNumber = toWalletNumber; }
    public double getToWalletBalance() { return toWalletBalance; }
    public void setToWalletBalance(double toWalletBalance) { this.toWalletBalance = toWalletBalance; }
    public String getOperatorId() { return operatorId; }
    public void setOperatorId(String operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getOperatorEmail() { return operatorEmail; }
    public void setOperatorEmail(String operatorEmail) { this.operatorEmail = operatorEmail; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    public String getBusId() { return busId; }
    public void setBusId(String busId) { this.busId = busId; }
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    public String getBusRoute() { return busRoute; }
    public void setBusRoute(String busRoute) { this.busRoute = busRoute; }
    public String getBusType() { return busType; }
    public void setBusType(String busType) { this.busType = busType; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getFromWalletId() { return fromWalletId; }
    public void setFromWalletId(String fromWalletId) { this.fromWalletId = fromWalletId; }
    public String getFromWalletNumber() { return fromWalletNumber; }
    public void setFromWalletNumber(String fromWalletNumber) { this.fromWalletNumber = fromWalletNumber; }
    public double getFromWalletBalance() { return fromWalletBalance; }
    public void setFromWalletBalance(double fromWalletBalance) { this.fromWalletBalance = fromWalletBalance; }

    // Parcelable implementation
    protected Transaction(Parcel in) {
        transactionId = in.readString();
        amount = in.readDouble();
        happenedAt = in.readString();
        status = in.readString();
        note = in.readString();
        passengerId = in.readString();
        passengerName = in.readString();
        passengerEmail = in.readString();
        transactionType = in.readString();
        fromBankAccountId = in.readString();
        fromBankName = in.readString();
        fromAccountNumber = in.readString();
        toWalletId = in.readString();
        toWalletNumber = in.readString();
        toWalletBalance = in.readDouble();
        operatorId = in.readString();
        operatorName = in.readString();
        operatorEmail = in.readString();
        ownerId = in.readString();
        ownerName = in.readString();
        ownerEmail = in.readString();
        busId = in.readString();
        busNumber = in.readString();
        busRoute = in.readString();
        busType = in.readString();
        province = in.readString();
        fromWalletId = in.readString();
        fromWalletNumber = in.readString();
        fromWalletBalance = in.readDouble();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionId);
        dest.writeDouble(amount);
        dest.writeString(happenedAt);
        dest.writeString(status);
        dest.writeString(note);
        dest.writeString(passengerId);
        dest.writeString(passengerName);
        dest.writeString(passengerEmail);
        dest.writeString(transactionType);
        dest.writeString(fromBankAccountId);
        dest.writeString(fromBankName);
        dest.writeString(fromAccountNumber);
        dest.writeString(toWalletId);
        dest.writeString(toWalletNumber);
        dest.writeDouble(toWalletBalance);
        dest.writeString(operatorId);
        dest.writeString(operatorName);
        dest.writeString(operatorEmail);
        dest.writeString(ownerId);
        dest.writeString(ownerName);
        dest.writeString(ownerEmail);
        dest.writeString(busId);
        dest.writeString(busNumber);
        dest.writeString(busRoute);
        dest.writeString(busType);
        dest.writeString(province);
        dest.writeString(fromWalletId);
        dest.writeString(fromWalletNumber);
        dest.writeDouble(fromWalletBalance);
    }
}
// No changes needed here if you use the main constructor and set fields as in HomeFragment.
