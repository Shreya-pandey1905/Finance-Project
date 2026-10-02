package com.finance.financeProject.config;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TOTPConfig {

    @Bean
    public SecretGenerator secretGenerator() {
        return new DefaultSecretGenerator();
    }

    @Bean
    public CodeGenerator codeGenerator() {
        return new DefaultCodeGenerator();
    }

    @Bean
    public CodeVerifier codeVerifier(CodeGenerator codeGenerator) {
        TimeProvider timeProvider = new SystemTimeProvider();
        return new DefaultCodeVerifier(codeGenerator, timeProvider);
    }
}