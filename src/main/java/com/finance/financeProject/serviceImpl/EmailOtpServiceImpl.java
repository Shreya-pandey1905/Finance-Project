package com.finance.financeProject.serviceImpl;

import com.finance.financeProject.service.EmailOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class EmailOtpServiceImpl implements EmailOtpService {

    private final JavaMailSender mailSender;

    @Override
    public String generateOtp() {

        SecureRandom random = new SecureRandom();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    @Override
    public void sendOtp(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("ForgeHub - Authenticator Reset OTP");
        message.setText(
                "Your OTP for resetting Google Authenticator is: "
                        + otp
                        + "\n\nThis OTP is valid for 5 minutes."
        );

        mailSender.send(message);
    }
}