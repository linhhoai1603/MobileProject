package com.mobile.fe_bankproject.dto; // Đảm bảo package đúng với cấu trúc frontend của bạn

public class FundTransferConfirmRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private String otp;

    // Constructor mặc định (thường cần cho Gson)
    public FundTransferConfirmRequest() {
    }

    // Constructor với tất cả các trường
    public FundTransferConfirmRequest(String fromAccountNumber, String toAccountNumber, double amount, String otp) {
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.otp = otp;
    }

    // Getters và Setters (Gson cần public getters/setters hoặc trường public)
    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    // Optional: toString() method for logging/debugging
    @Override
    public String toString() {
        return "FundTransferConfirmRequest{" +
               "fromAccountNumber='" + fromAccountNumber + '\'' +
               ", toAccountNumber='" + toAccountNumber + '\'' +
               ", amount=" + amount +
               ", otp='***'" + // Avoid logging sensitive OTP
               '}';
    }
}