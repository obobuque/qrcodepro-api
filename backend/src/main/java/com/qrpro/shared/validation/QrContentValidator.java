package com.qrpro.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class QrContentValidator implements ConstraintValidator<ValidQrContent, String> {

    @Override
    public void initialize(ValidQrContent constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return value.length() <= 7089;
    }
}
