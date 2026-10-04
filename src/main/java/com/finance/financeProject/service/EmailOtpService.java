package com.finance.financeProject.service;

public interface EmailOtpService {

    String generateOtp();

    void sendOtp(String email, String otp);
}