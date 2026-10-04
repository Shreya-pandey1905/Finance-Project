package com.finance.financeProject.serviceImpl;

import com.finance.financeProject.service.TOTPService;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TOTPServiceImpl implements TOTPService {

    private final SecretGenerator secretGenerator;
    private final CodeVerifier codeVerifier;
    private final CodeGenerator codeGenerator;

    @Override
    public String generateSecret() {
        return secretGenerator.generate();
    }

    @Override
    public boolean verifyCode(String secret,String code) {
        return codeVerifier.isValidCode(secret,code);
    }

    @Override
    public String generateCode(String secret) throws CodeGenerationException {
        long time=new SystemTimeProvider().getTime(); // give current time in milliseconds
        long counter=time/30; // e.g  600s so thwe counter will be 600/30= 20, The counter changes every 30 seconds.

        return codeGenerator.generate(secret,counter);
    }
}