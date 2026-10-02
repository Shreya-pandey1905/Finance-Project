package com.finance.financeProject.service;

import dev.samstevens.totp.exceptions.CodeGenerationException;

public interface TOTPService {

    String generateSecret();

    boolean verifyCode(String secret, String code) throws CodeGenerationException;

    String generateCode(String secret) throws CodeGenerationException;
}