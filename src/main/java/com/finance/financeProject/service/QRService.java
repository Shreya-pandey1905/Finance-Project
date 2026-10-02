package com.finance.financeProject.service;

import dev.samstevens.totp.exceptions.QrGenerationException;

public interface QRService {

    String generateQRCode(String email, String secret) throws QrGenerationException;
}