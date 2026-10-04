package com.finance.financeProject.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
}