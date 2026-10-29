package com.faculty.management.util;

/**
 * Standard Grading System based on UGC Commission Circular No. 12-2024.
 * Computes Grade and Grade Point Values (GPV) from percentage marks.
 */
public class GradeCalculator {

    public static String getGrade(double mark) {
        if (mark >= 85.0) return "A+";
        if (mark >= 75.0) return "A";
        if (mark >= 70.0) return "A-";
        if (mark >= 65.0) return "B+";
        if (mark >= 60.0) return "B";
        if (mark >= 55.0) return "B-";
        if (mark >= 50.0) return "C+";
        if (mark >= 45.0) return "C";
        if (mark >= 40.0) return "C-";
        if (mark >= 35.0) return "D+";
        if (mark >= 30.0) return "D";
        return "E";
    }

    public static double getGpv(double mark) {
        if (mark >= 75.0) return 4.00; // Both A+ and A give 4.00
        if (mark >= 70.0) return 3.70; // A-
        if (mark >= 65.0) return 3.30; // B+
        if (mark >= 60.0) return 3.00; // B
        if (mark >= 55.0) return 2.70; // B-
        if (mark >= 50.0) return 2.30; // C+
        if (mark >= 45.0) return 2.00; // C
        if (mark >= 40.0) return 1.70; // C-
        if (mark >= 35.0) return 1.30; // D+
        if (mark >= 30.0) return 1.00; // D
        return 0.00;                   // E
    }

    public static String getDescription(String grade) {
        return switch (grade) {
            case "A+", "A" -> "Excellent";
            case "A-" -> "Very Good";
            case "B+", "B" -> "Good";
            case "B-" -> "Satisfactory";
            case "C+", "C" -> "Pass";
            case "C-" -> "Conditional Pass";
            case "D+", "D" -> "Weak";
            default -> "Fail";
        };
    }
}
