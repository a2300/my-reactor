package a2300.spring.client.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record ExchangeRate(String base, Map<String, Double> rates) {
}
