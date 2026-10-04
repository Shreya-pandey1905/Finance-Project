package com.finance.financeProject.controller;

import com.finance.financeProject.JwtAccessToken.JwtService;
import com.finance.financeProject.entity.User;
import com.finance.financeProject.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RefreshTokenController {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/auth/refresh")
    public ResponseEntity<String> refreshToken(@RequestParam String refreshToken) {

        try {

            String email = jwtService.extraSubject(refreshToken);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRefreshToken() == null) {
                return ResponseEntity.status(401)
                        .body("Refresh token expired. Please login again.");
            }

            if (!user.getRefreshToken().equals(refreshToken)) {
                return ResponseEntity.status(401)
                        .body("Invalid refresh token.");
            }

            String newAccessToken = jwtService.generateToken(
                    user.getEmail(),
                    user.getRole()
            );

            return ResponseEntity.ok(newAccessToken);

        } catch (ExpiredJwtException e) {

            String email = e.getClaims().getSubject();

            userRepository.findByEmail(email).ifPresent(user -> {
                user.setRefreshToken(null);
                userRepository.save(user);
            });

            return ResponseEntity.status(401)
                    .body("Refresh token expired. Please login again.");

        } catch (JwtException | IllegalArgumentException e) {

            return ResponseEntity.status(401)
                    .body("Invalid refresh token.");

        }
    }
}