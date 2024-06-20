package com.example;

public class QuoteIndexOutOfBoundsException extends RuntimeException {
    public QuoteIndexOutOfBoundsException(String message) {
        super(message);
    }

    public QuoteIndexOutOfBoundsException() {
        this("Quote does not exist at give index.");
    }
}
