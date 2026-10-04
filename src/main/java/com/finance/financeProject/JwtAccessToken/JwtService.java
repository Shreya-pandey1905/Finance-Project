package com.finance.financeProject.JwtAccessToken;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service

public class JwtService {

   private final SecretKey secretKey;
    private final Long expirationMs;
    private final Long refreshExpirationMs;

    public JwtService(@Value("${jwt.secret}") String secretKey,
                      @Value("${jwt.expiration-ms}") Long expirationMs,
                      @Value("${jwt.refresh-expiration-ms}") Long refreshExpirationMs) {

        this.secretKey = Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );

        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    /*
        payload:
        {
        "subject":"email",
        "role":"role",
        "issuedAt(iat)":"dateTime",
        "exp":expiryTime
        }
        * */
    public String generateToken(String  email, String role){
        Date now= new Date();
        return Jwts.builder()
                .subject(email)
                .claim("role",role)
                .issuedAt(now)
                .expiration(new Date(now.getTime()+expirationMs))
                .signWith(secretKey,Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(String email) {
        Date now = new Date();

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpirationMs))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String extraSubject(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

    }
}
