package com.parentalcontrol.exception;

public class AuthorisationFailureException extends RuntimeException {
    public AuthorisationFailureException(String message) {
        super(message);
    }
}
