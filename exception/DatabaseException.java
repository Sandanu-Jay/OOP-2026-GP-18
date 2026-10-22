package com.faculty.management.exception;


/**
 * OOP CONCEPT: EXCEPTION HANDLING
 * Custom unchecked exception for database operation failures.
 */
public class DatabaseException extends RuntimeException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
