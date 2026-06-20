package com.qrpro.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = QrContentValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidQrContent {
    String message() default "Invalid QR code content";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
