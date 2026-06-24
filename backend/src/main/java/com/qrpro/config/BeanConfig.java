package com.qrpro.config;

import com.qrpro.domain.service.QrCodeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public QrCodeValidator qrCodeValidator() {
        return new QrCodeValidator();
    }
}
