package com.faculty.management.exception;

/**
 * Custom exception for input validation failures.
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}
