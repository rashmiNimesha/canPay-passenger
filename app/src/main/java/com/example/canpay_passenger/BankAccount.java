package com.example.canpay_passenger;

public class BankAccount {
//    private String name;

    private String bank;
    private String accountName;
//    public BankAccount(String name) { this.name = name; }
//    public String getName() { return name; }


    public BankAccount(String bank, String accountName) {
        this.bank = bank;
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
}
