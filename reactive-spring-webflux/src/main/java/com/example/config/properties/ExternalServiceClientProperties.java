package com.example.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "application.provider.rest.external-service")
public record ExternalServiceClientProperties(@NotBlank String url,
                                              WebClientProperties webClient) {

    public record WebClientProperties(int connectTimeoutMillis,
                                       int readTimeoutMillis,
                                       int writeTimeoutMillis,
                                       int maxRetries,
                                       int minBackoffMillis) {

    }
}
