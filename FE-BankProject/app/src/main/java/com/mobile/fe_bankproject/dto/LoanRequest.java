package com.mobile.fe_bankproject.dto;
public class LoanRequest {
    private String accountNumber;
    private double amount;
    private int term;
    private String note;
    //getters and setters
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public int getTerm() {
        return term;
    }
    public void setTerm(int term) {
        this.term = term;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
}