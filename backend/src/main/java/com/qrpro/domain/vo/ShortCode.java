package com.qrpro.domain.vo;

import java.security.SecureRandom;
import java.util.Objects;

public final class ShortCode {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 7;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String BASE_URL = "http://localhost:80/r/";

    private final String value;

    public ShortCode(String value) {
        if (value == null || !value.matches("^[a-zA-Z0-9]{" + LENGTH + "}$")) {
            throw new IllegalArgumentException(
                "Invalid short code format. Must be exactly " + LENGTH + " alphanumeric characters."
            );
        }
        this.value = value;
    }

    public static ShortCode generate() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new ShortCode(sb.toString());
    }

    public String value() {
        return value;
    }

    public String toRedirectUrl() {
        return BASE_URL + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortCode shortCode = (ShortCode) o;
        return Objects.equals(value, shortCode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
