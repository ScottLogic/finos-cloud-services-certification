package com.scottlogic.compliance.event;

public class ComplianceEventParseException extends RuntimeException {
    public ComplianceEventParseException() {
    }

    public ComplianceEventParseException(String message) {
        super(message);
    }

    public ComplianceEventParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComplianceEventParseException(Throwable cause) {
        super(cause);
    }

    public ComplianceEventParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
