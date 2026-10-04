package com.finance.financeProject.controller;

import com.finance.financeProject.JwtAccessToken.JwtService;
import com.finance.financeProject.entity.User;
import com.finance.financeProject.repository.UserRepository;
//import com.finance.financeProject.service.EmailOtpService;
import com.finance.financeProject.service.EmailOtpService;
import com.finance.financeProject.service.QRService;
import com.finance.financeProject.service.TOTPService;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.exceptions.QrGenerationException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TwoFactorResetController {

    private final UserRepository userRepository;
    private final EmailOtpService emailOtpService;
    private final TOTPService totpService;
    private final QRService qrService;
    private final JwtService jwtService;

    @GetMapping("/forgot-authenticator")
    public String forgotAuthenticator() {
        return "forgot-authenticator";
    }


    @PostMapping("/forgot-authenticator/send-otp")
    public String sendEmailOtp(
            @RequestParam String email,
            HttpSession session,
            Model model) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            model.addAttribute("error", "User not found");
            return "forgot-authenticator";
        }

        String otp = emailOtpService.generateOtp();

        emailOtpService.sendOtp(email, otp);

        session.setAttribute("resetEmail", email);
        session.setAttribute("emailOtp", otp);

        long expiryTime = System.currentTimeMillis() + (5 * 60 * 1000);

        session.setAttribute("emailOtpExpiry", expiryTime);

        return "redirect:/verify-email-otp";
    }

    @GetMapping("/verify-email-otp")
    public String showEmailOtpPage(
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute("resetEmail");

        if (email == null) {
            return "redirect:/forgot-authenticator";
        }

        model.addAttribute("email", email);

        return "verify-email-otp";
    }

    @PostMapping("/verify-email-otp")
    public String verifyEmailOtp(
            @RequestParam String otp,
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute("resetEmail");
        String savedOtp = (String) session.getAttribute("emailOtp");
        Long expiryTime = (Long) session.getAttribute("emailOtpExpiry");

        if (email == null || savedOtp == null || expiryTime == null) {
            return "redirect:/forgot-authenticator";
        }

        if (System.currentTimeMillis() > expiryTime) {
            session.removeAttribute("emailOtp");
            session.removeAttribute("emailOtpExpiry");

            model.addAttribute("error", "OTP has expired. Please request a new OTP.");
            model.addAttribute("email", email);

            return "verify-email-otp";
        }

        if (!savedOtp.equals(otp)) {
            model.addAttribute("error", "Invalid OTP. Please try again.");
            model.addAttribute("email", email);

            return "verify-email-otp";
        }

        session.removeAttribute("emailOtp");
        session.removeAttribute("emailOtpExpiry");

        session.setAttribute("emailVerifiedForReset", true);

        return "redirect:/reset-authenticator";
    }


    @GetMapping("/reset-authenticator")
    public String resetAuthenticator(
            HttpSession session,
            Model model) throws QrGenerationException {

        Boolean emailVerified =
                (Boolean) session.getAttribute("emailVerifiedForReset");

        if (!Boolean.TRUE.equals(emailVerified)) {
            return "redirect:/forgot-authenticator";
        }

        String email = (String) session.getAttribute("resetEmail");

        if (email == null) {
            return "redirect:/forgot-authenticator";
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String resetSecret =
                (String) session.getAttribute("resetSecret");

        if (resetSecret == null) {

            resetSecret = totpService.generateSecret();

            session.setAttribute("resetSecret", resetSecret);
        }

        String qrCode = qrService.generateQRCode(
                user.getEmail(),
                resetSecret
        );

        model.addAttribute("qrCode", qrCode);
        model.addAttribute("email", user.getEmail());

        return "reset-authenticator";
    }

    @PostMapping("/reset-authenticator/verify")
    public String verifyResetAuthenticator(
            @RequestParam String code,
            HttpSession session,
            Model model) throws CodeGenerationException, QrGenerationException {

        String email = (String) session.getAttribute("resetEmail");
        String resetSecret = (String) session.getAttribute("resetSecret");

        Boolean emailVerified =
                (Boolean) session.getAttribute("emailVerifiedForReset");

        if (email == null || resetSecret == null
                || !Boolean.TRUE.equals(emailVerified)) {

            return "redirect:/forgot-authenticator";
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean valid = totpService.verifyCode(resetSecret, code);

        if (!valid) {

            model.addAttribute("error",
                    "Invalid authenticator OTP. Please try again.");

            model.addAttribute("email", email);

            String qrCode = qrService.generateQRCode(
                    user.getEmail(),
                    resetSecret
            );

            model.addAttribute("qrCode", qrCode);

            return "reset-authenticator";
        }

        user.setSecretKey(resetSecret);
        user.setFirstTimeLogin(false);

        String accessToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getEmail()
        );

        user.setRefreshToken(refreshToken);

        userRepository.save(user);

        System.out.println("ACCESS TOKEN = " + accessToken);
        System.out.println("REFRESH TOKEN = " + refreshToken);

        session.removeAttribute("resetEmail");
        session.removeAttribute("emailOtp");
        session.removeAttribute("emailOtpExpiry");
        session.removeAttribute("emailVerifiedForReset");
        session.removeAttribute("resetSecret");

        return "redirect:/home";
    }
}