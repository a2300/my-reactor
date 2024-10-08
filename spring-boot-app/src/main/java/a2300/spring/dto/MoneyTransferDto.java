package a2300.spring.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record MoneyTransferDto(
        @Min(1)
        @NotNull
        Long senderId,
        @Min(1)
        @NotNull
        Long recipientId,
        @PositiveOrZero
        @NotNull
        Integer moneyAmount
) {
}
