package com.example.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServiceUnavailableException extends RuntimeException {
    private final String serviceName;
    private final int statusCode;

    public ServiceUnavailableException(String serviceName, int statusCode, String msg) {
        super(msg);
        this.serviceName = serviceName;
        this.statusCode = statusCode;
    }
}
