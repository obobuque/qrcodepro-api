package com.qrpro.unit.domain;

import com.qrpro.domain.service.QrCodeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class QrCodeValidatorTest {

    private QrCodeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new QrCodeValidator();
    }

    @Test
    void validateContent_should_pass_for_valid_url() {
        assertThatNoException()
                .isThrownBy(() -> validator.validateContent("https://www.google.com"));
    }

    @Test
    void validateContent_should_throw_for_null() {
        assertThatThrownBy(() -> validator.validateContent(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be empty");
    }

    @Test
    void validateContent_should_throw_for_blank() {
        assertThatThrownBy(() -> validator.validateContent("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be empty");
    }

    @Test
    void validateContent_should_throw_when_exceeds_max_capacity() {
        String tooLong = "a".repeat(7090);
        assertThatThrownBy(() -> validator.validateContent(tooLong))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maximum QR code capacity");
    }

    @Test
    void validateContent_should_accept_exactly_max_length() {
        String maxLength = "a".repeat(7089);
        assertThatNoException()
                .isThrownBy(() -> validator.validateContent(maxLength));
    }
}
