package com.finance.financeProject.config;

import com.finance.financeProject.entity.User;
import com.finance.financeProject.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        HttpSession session = request.getSession();

        if (user.isFirstTimeLogin()) {
            session.setAttribute("pendingEmail", email);
            response.sendRedirect(request.getContextPath() + "/setup-authenticator");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/home");
    }
}