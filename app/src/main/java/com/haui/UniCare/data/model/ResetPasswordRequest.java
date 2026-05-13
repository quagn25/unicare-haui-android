package com.haui.UniCare.data.model;

public class ResetPasswordRequest {
    private String username;
    private String email;
    private String otp;
    private String newPassword;

    public ResetPasswordRequest(String username, String email, String otp, String newPassword) {
        this.username = username;
        this.email = email;
        this.otp = otp;
        this.newPassword = newPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
