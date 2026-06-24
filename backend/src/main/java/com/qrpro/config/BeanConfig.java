package com.qrpro.config;

import com.qrpro.domain.service.QrCodeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {

    @Bean

    @Bean
    public QrCodeValidator qrCodeValidator() {
        return new QrCodeValidator();
    }
}
