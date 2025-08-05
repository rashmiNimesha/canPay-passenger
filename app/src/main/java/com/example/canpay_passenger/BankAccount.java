package com.example.canpay_passenger;

public class BankAccount {
    private String bank;
    private String accountName;
    private boolean isDefault = false;
    private long accountNumber;

    public BankAccount(String bank, String accountName) {
        this.bank = bank;
        this.accountName = accountName;
    }

    public BankAccount(String bank, String accountName, long accountNumber, boolean isDefault) {
        this.bank = bank;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.isDefault = isDefault;
    }

    public BankAccount(String accountName) {
        this.accountName = accountName;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }
}
