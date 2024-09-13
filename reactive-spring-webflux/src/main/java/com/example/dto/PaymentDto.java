package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PaymentDto(
        String id,
        @NotBlank
        String user,
        @NotNull
        @PositiveOrZero
        Integer price
) {
}
