package a2300.spring.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AccountDto(
        Long id,
        @NotEmpty
        @Email
        String email,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotNull
        @Past
        LocalDate dateOfBirth,
        @NotNull
        @Pattern(regexp = "USD|EUR|GBP")
        String currency,
        @NotNull
        @PositiveOrZero
        Integer moneyAmount
) {
}
