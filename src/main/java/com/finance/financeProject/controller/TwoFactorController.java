package com.finance.financeProject.controller;

import com.finance.financeProject.JwtAccessToken.JwtService;
import com.finance.financeProject.entity.User;
import com.finance.financeProject.repository.UserRepository;
import com.finance.financeProject.service.EmailOtpService;
import com.finance.financeProject.service.QRService;
import com.finance.financeProject.service.TOTPService;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.exceptions.QrGenerationException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class TwoFactorController {
    private final EmailOtpService emailOtpService;
    private final UserRepository userRepository;
    private final TOTPService totpService;
    private final QRService qrService;
    private final JwtService jwtService;

    @GetMapping("/setup-authenticator")
    public String setupAuthenticator(HttpSession session, Model model) throws QrGenerationException {

        String email = (String) session.getAttribute("pendingEmail");

        if (email == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String secret = (String) session.getAttribute("pendingSecret");

        if (secret == null) {
            secret = totpService.generateSecret();
            session.setAttribute("pendingSecret", secret);
        }

        String qrCode = qrService.generateQRCode(
                user.getEmail(),
                secret
        );

        model.addAttribute("qrCode", qrCode);
        model.addAttribute("email", user.getEmail());

        return "setup-authenticator";
    }

    @GetMapping("/verify-otp")
    public String showVerifyOtp( HttpSession session, Model model) {

        String email = (String) session.getAttribute("pendingEmail");

        if (email == null) {
            return "redirect:/login";
        }

        model.addAttribute("email", email);

        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String code,
            HttpSession session,
            Model model) throws QrGenerationException, CodeGenerationException {

        String email = (String) session.getAttribute("pendingEmail");
        if (email == null) {
           return "redirect:/login";
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String secret;

        if (user.isFirstTimeLogin()) {
            secret = (String) session.getAttribute("pendingSecret");

            if (secret == null) {
                return "redirect:/login";
            }
        } else {
            secret = user.getSecretKey();
        }

        System.out.println("EMAIL = " + email);
        System.out.println("SECRET = " + secret);
        System.out.println("OTP = " + code);

        boolean valid = totpService.verifyCode(secret, code);

        if (!valid) {
            model.addAttribute("error", "Invalid OTP. Please try again.");

            if (user.isFirstTimeLogin()) {
                String qrCode = qrService.generateQRCode(user.getEmail(), secret);
                model.addAttribute("qrCode", qrCode);
                model.addAttribute("email", user.getEmail());

                return "setup-authenticator";
            }

            model.addAttribute("email", user.getEmail());
            return "verify-otp";
        }

        if (user.isFirstTimeLogin()) {
            user.setSecretKey(secret);
            user.setFirstTimeLogin(false);
            userRepository.save(user);

            session.removeAttribute("pendingSecret");
        }


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

        return "redirect:/home";
    }


}