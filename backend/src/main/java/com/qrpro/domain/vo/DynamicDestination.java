package com.qrpro.domain.vo;

public record DynamicDestination(String url) {
    public DynamicDestination {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Destination URL cannot be empty");
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("Destination URL must start with http:// or https://");
        }
        if (url.length() > 2048) {
            throw new IllegalArgumentException("Destination URL exceeds maximum length of 2048 characters");
        }
    }
}
