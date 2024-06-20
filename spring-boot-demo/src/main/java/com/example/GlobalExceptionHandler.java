package com.example;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(QuoteIndexOutOfBoundsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleExceptions(QuoteIndexOutOfBoundsException ex) {
        logger.error("Error Message");
        logger.warn("Warning Message");
        logger.info("Info Message");
        logger.debug("Debug Message");
        logger.trace("Trace Message");

        return "RestControllerAdvice";
    }
}
