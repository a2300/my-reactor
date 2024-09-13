package com.example.exception;

import java.text.MessageFormat;

public class RetryExhaustedException extends RuntimeException {

    private static final String ERROR_MESSAGE_TEMPLATE = "Retry exhausted after {0} attempts";

    public RetryExhaustedException(long retriesCount, Throwable cause) {
        super(MessageFormat.format(ERROR_MESSAGE_TEMPLATE, retriesCount), cause);
    }

    public RetryExhaustedException(long retriesCount) {
        super(MessageFormat.format(ERROR_MESSAGE_TEMPLATE, retriesCount));
    }
}
