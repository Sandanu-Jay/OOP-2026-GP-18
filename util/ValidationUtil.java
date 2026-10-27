package com.faculty.management.util;

import java.util.regex.Pattern;

/**
 * Validates user inputs before database storage or calculation.
 * Prevents invalid states and provides user-friendly feedback.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[+]?[0-9\\s-]{7,20}$");

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) return true; // Phone is optional in some contexts
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidMark(double mark) {
        return mark >= 0.0 && mark <= 100.0;
    }

    public static boolean isValidCredit(int credit) {
        return credit >= 1 && credit <= 10;
    }

    public static boolean isValidSessionNumber(int session) {
        return session >= 1 && session <= 15;
    }
}
