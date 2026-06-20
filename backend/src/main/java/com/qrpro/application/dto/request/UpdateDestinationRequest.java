package com.qrpro.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateDestinationRequest(
    @NotBlank(message = "New URL is required")
    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    String newUrl
) {}
