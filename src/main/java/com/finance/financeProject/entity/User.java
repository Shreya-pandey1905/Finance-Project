package com.finance.financeProject.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.ToString;
import jakarta.persistence.*;

@Entity
@Data
@ToString
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(name = "FullName", nullable = false)
    private String fullName;

    @Column(name = "Email", nullable = false)
    private String email;

    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

    @Column(name = "Role", nullable = false)
    private String role;

    @Column(name = "IsFirstTimeLogin", nullable = false)
    private boolean firstTimeLogin;

    @Column(name = "SecretKey")
    private String secretKey;

    @Column(name = "refresh_token")
    private String refreshToken;


}