package com.krstf.newsfeed.port.inbound.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSourceRequest(
        @NotBlank String url,
        @NotBlank String name,
        String description
) {
}
