package com.faculty.management.exception;

/**
 * OOP CONCEPT: EXCEPTION HANDLING
 * Custom exception for failed login or unauthorized access.
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }
}
