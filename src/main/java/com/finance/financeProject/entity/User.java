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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isFirstTimeLogin() {
        return firstTimeLogin;
    }

    public void setFirstTimeLogin(boolean firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}