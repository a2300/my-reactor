package a2300.spring.dto;

import java.util.List;

public record ErrorResponseDto(
        List<String> errors
) {
}
