package com.faculty.management.exception;

/**
 * Custom exception for failed login or unauthorized access.
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }
}
