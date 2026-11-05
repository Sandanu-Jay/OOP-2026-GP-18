package com.faculty.management;

import com.faculty.management.model.*;
import com.faculty.management.util.GradeCalculator;
import com.faculty.management.util.PasswordUtil;
import com.faculty.management.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated Verification Test Suite.
 * Validates UGC Circular No. 12-2024 grading, attendance rules, CA eligibility,
 * password hashing, validation rules, and OOP polymorphism.
 */
public class SystemVerificationTest {

    @Test
    @DisplayName("UGC Commission Circular No. 12-2024: Verify all 12 grade boundaries and GPVs")
    public void testUgcCircular12_2024Grading() {
        // A+ (>= 85, GPV 4.00)
        assertEquals("A+", GradeCalculator.getGrade(95.0));
        assertEquals(4.00, GradeCalculator.getGpv(95.0), 0.001);
        assertEquals("A+", GradeCalculator.getGrade(85.0));
        assertEquals(4.00, GradeCalculator.getGpv(85.0), 0.001);

        // A (75 - 84, GPV 4.00)
        assertEquals("A", GradeCalculator.getGrade(84.0));
        assertEquals(4.00, GradeCalculator.getGpv(84.0), 0.001);
        assertEquals("A", GradeCalculator.getGrade(75.0));
        assertEquals(4.00, GradeCalculator.getGpv(75.0), 0.001);

        // A- (70 - 74, GPV 3.70)
        assertEquals("A-", GradeCalculator.getGrade(74.0));
        assertEquals(3.70, GradeCalculator.getGpv(74.0), 0.001);
        assertEquals("A-", GradeCalculator.getGrade(70.0));
        assertEquals(3.70, GradeCalculator.getGpv(70.0), 0.001);

        // B+ (65 - 69, GPV 3.30)
        assertEquals("B+", GradeCalculator.getGrade(69.0));
        assertEquals(3.30, GradeCalculator.getGpv(69.0), 0.001);
        assertEquals("B+", GradeCalculator.getGrade(65.0));
        assertEquals(3.30, GradeCalculator.getGpv(65.0), 0.001);

        // B (60 - 64, GPV 3.00)
        assertEquals("B", GradeCalculator.getGrade(64.0));
        assertEquals(3.00, GradeCalculator.getGpv(64.0), 0.001);
        assertEquals("B", GradeCalculator.getGrade(60.0));
        assertEquals(3.00, GradeCalculator.getGpv(60.0), 0.001);

        // B- (55 - 59, GPV 2.70)
        assertEquals("B-", GradeCalculator.getGrade(59.0));
        assertEquals(2.70, GradeCalculator.getGpv(59.0), 0.001);
        assertEquals("B-", GradeCalculator.getGrade(55.0));
        assertEquals(2.70, GradeCalculator.getGpv(55.0), 0.001);

        // C+ (50 - 54, GPV 2.30)
        assertEquals("C+", GradeCalculator.getGrade(54.0));
        assertEquals(2.30, GradeCalculator.getGpv(54.0), 0.001);
        assertEquals("C+", GradeCalculator.getGrade(50.0));
        assertEquals(2.30, GradeCalculator.getGpv(50.0), 0.001);

        // C (45 - 49, GPV 2.00)
        assertEquals("C", GradeCalculator.getGrade(49.0));
        assertEquals(2.00, GradeCalculator.getGpv(49.0), 0.001);
        assertEquals("C", GradeCalculator.getGrade(45.0));
        assertEquals(2.00, GradeCalculator.getGpv(45.0), 0.001);

        // C- (40 - 44, GPV 1.70)
        assertEquals("C-", GradeCalculator.getGrade(44.0));
        assertEquals(1.70, GradeCalculator.getGpv(44.0), 0.001);
        assertEquals("C-", GradeCalculator.getGrade(40.0));
        assertEquals(1.70, GradeCalculator.getGpv(40.0), 0.001);

        // D+ (35 - 39, GPV 1.30)
        assertEquals("D+", GradeCalculator.getGrade(39.0));
        assertEquals(1.30, GradeCalculator.getGpv(39.0), 0.001);
        assertEquals("D+", GradeCalculator.getGrade(35.0));
        assertEquals(1.30, GradeCalculator.getGpv(35.0), 0.001);

        // D (30 - 34, GPV 1.00)
        assertEquals("D", GradeCalculator.getGrade(34.0));
        assertEquals(1.00, GradeCalculator.getGpv(34.0), 0.001);
        assertEquals("D", GradeCalculator.getGrade(30.0));
        assertEquals(1.00, GradeCalculator.getGpv(30.0), 0.001);

        // E (< 30, GPV 0.00)
        assertEquals("E", GradeCalculator.getGrade(29.0));
        assertEquals(0.00, GradeCalculator.getGpv(29.0), 0.001);
        assertEquals("E", GradeCalculator.getGrade(0.0));
        assertEquals(0.00, GradeCalculator.getGpv(0.0), 0.001);
    }

    @Test
    @DisplayName("Attendance Requirements: Verify the 5 required conditions (Theory 15 + Practical 15 = 30)")
    public void testAttendanceScenarios() {
        // Condition 1: > 80% attendance
        AttendanceSummary s1 = new AttendanceSummary(1, "TG/2022/1001", "Chamindu", 1, "ICT2101", "OOP", 14, 14, 0);
        assertEquals(28, s1.getTotalAttended());
        assertEquals(93.33, s1.getEffectivePercentage(), 0.1);
        assertTrue(s1.isEligible(), "Student with >80% should be eligible");
        assertEquals("> 80% attendance", s1.getSummaryDescription());

        // Condition 2: Exactly 80% attendance (24 out of 30)
        AttendanceSummary s2 = new AttendanceSummary(2, "TG/2022/1002", "Kavindi", 1, "ICT2101", "OOP", 12, 12, 0);
        assertEquals(24, s2.getTotalAttended());
        assertEquals(80.0, s2.getEffectivePercentage(), 0.001);
        assertTrue(s2.isEligible(), "Student with exactly 80% should be eligible");
        assertEquals("Exactly 80% attendance", s2.getSummaryDescription());

        // Condition 3: < 80% attendance without medicals (20 out of 30 = 66.7%)
        AttendanceSummary s3 = new AttendanceSummary(3, "TG/2022/1003", "Kasun", 1, "ICT2101", "OOP", 10, 10, 0);
        assertEquals(20, s3.getTotalAttended());
        assertEquals(66.67, s3.getEffectivePercentage(), 0.1);
        assertFalse(s3.isEligible(), "Student with <80% without medicals should NOT be eligible");
        assertEquals("< 80% without medicals", s3.getSummaryDescription());

        // Condition 4: > 80% attendance WITH medicals (Raw 22/30 = 73.3%, + 3 approved medicals = 25/30 = 83.3%)
        AttendanceSummary s4 = new AttendanceSummary(4, "TG/2022/1004", "Nethmi", 1, "ICT2101", "OOP", 11, 11, 3);
        assertEquals(22, s4.getTotalAttended());
        assertEquals(25, s4.getEffectiveAttended());
        assertEquals(73.33, s4.getRawPercentage(), 0.1);
        assertEquals(83.33, s4.getEffectivePercentage(), 0.1);
        assertTrue(s4.isEligible(), "Student with >80% with medicals should be eligible");
        assertEquals("> 80% with medicals", s4.getSummaryDescription());

        // Condition 5: < 80% attendance WITH medicals (Raw 18/30 = 60.0%, + 2 approved medicals = 20/30 = 66.7%)
        AttendanceSummary s5 = new AttendanceSummary(5, "TG/2022/1005", "Sandun", 1, "ICT2101", "OOP", 9, 9, 2);
        assertEquals(18, s5.getTotalAttended());
        assertEquals(20, s5.getEffectiveAttended());
        assertEquals(66.67, s5.getEffectivePercentage(), 0.1);
        assertFalse(s5.isEligible(), "Student with <80% even with medicals should NOT be eligible");
        assertEquals("< 80% with medicals", s5.getSummaryDescription());
    }

    @Test
    @DisplayName("CA Eligibility: Threshold is strictly >= 40.0%")
    public void testCaEligibility() {
        Marks m1 = new Marks();
        m1.setCaMark(40.0);
        assertTrue(m1.isCaEligible(), "CA = 40.0% must be eligible");

        Marks m2 = new Marks();
        m2.setCaMark(39.99);
        assertFalse(m2.isCaEligible(), "CA < 40.0% must NOT be eligible");

        Marks m3 = new Marks();
        m3.setCaMark(82.5);
        assertTrue(m3.isCaEligible(), "CA = 82.5% must be eligible");
    }

    @Test
    @DisplayName("Overall Final-Exam Eligibility: Requires BOTH CA >= 40% AND Attendance >= 80%")
    public void testOverallExamEligibility() {
        // Both satisfied
        EligibilityStatus e1 = new EligibilityStatus(1, "TG/2022/1001", "Student 1", 1, "ICT2101", "OOP", 70.0, 85.0);
        assertTrue(e1.isCaEligible());
        assertTrue(e1.isAttendanceEligible());
        assertTrue(e1.isFinalExamEligible(), "Must be Final Exam Eligible when both CA >= 40% and Attendance >= 80%");

        // CA fails (< 40%)
        EligibilityStatus e2 = new EligibilityStatus(2, "TG/2022/1002", "Student 2", 1, "ICT2101", "OOP", 35.0, 90.0);
        assertFalse(e2.isCaEligible());
        assertTrue(e2.isAttendanceEligible());
        assertFalse(e2.isFinalExamEligible(), "Must NOT be Final Exam Eligible when CA < 40%");

        // Attendance fails (< 80%)
        EligibilityStatus e3 = new EligibilityStatus(3, "TG/2022/1003", "Student 3", 1, "ICT2101", "OOP", 65.0, 75.0);
        assertTrue(e3.isCaEligible());
        assertFalse(e3.isAttendanceEligible());
        assertFalse(e3.isFinalExamEligible(), "Must NOT be Final Exam Eligible when Attendance < 80%");

        // Both fail
        EligibilityStatus e4 = new EligibilityStatus(4, "TG/2022/1004", "Student 4", 1, "ICT2101", "OOP", 25.0, 60.0);
        assertFalse(e4.isCaEligible());
        assertFalse(e4.isAttendanceEligible());
        assertFalse(e4.isFinalExamEligible(), "Must NOT be Final Exam Eligible when both fail");
    }

    @Test
    @DisplayName("Password Hashing: SHA-256 integrity and matching")
    public void testPasswordHashing() {
        String adminHash = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9";
        String lecHash = "e09a46489b39fc28b3813b1101afc8300b59322474104431216f91cf774c306b";
        String toHash = "ab27b9a100edf52a1e939157af0180c4e0d5c936567a6269a0687db670af6542";
        String stuHash = "703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b";

        assertEquals(adminHash, PasswordUtil.hashPassword("admin123"));
        assertEquals(lecHash, PasswordUtil.hashPassword("lec123"));
        assertEquals(toHash, PasswordUtil.hashPassword("to123"));
        assertEquals(stuHash, PasswordUtil.hashPassword("student123"));

        assertTrue(PasswordUtil.verifyPassword("admin123", adminHash));
        assertFalse(PasswordUtil.verifyPassword("wrongpass", adminHash));
    }

    @Test
    @DisplayName("Validation Utilities: Email, Phone, and Mark bounds")
    public void testValidationUtil() {
        assertTrue(ValidationUtil.isValidEmail("chamindu@student.ac.lk"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail(""));

        assertTrue(ValidationUtil.isValidMark(0.0));
        assertTrue(ValidationUtil.isValidMark(100.0));
        assertTrue(ValidationUtil.isValidMark(75.5));
        assertFalse(ValidationUtil.isValidMark(-1.0));
        assertFalse(ValidationUtil.isValidMark(100.1));
    }

    @Test
    @DisplayName("OOP Concept: Inheritance, Polymorphism, and Encapsulation")
    public void testPolymorphicUserHierarchy() {
        // Polymorphic assignment
        User admin = new Admin(1, "admin", "hash", "Admin User", "admin@faculty.lk", "111", null);
        User lecturer = new Lecturer(2, "lec1", "hash", "Dr. Silva", "silva@faculty.lk", "222", null, "CS", "Senior Lecturer");
        User to = new TechnicalOfficer(3, "to1", "hash", "Saman W.", "to@faculty.lk", "333", null, "CS");
        User student = new Student(4, "stu1", "hash", "Chamindu", "c@student.lk", "444", null, "TG/2022/1001", "CS", "2021/2022", "NORMAL");

        // Polymorphic method calls (Dynamic Method Dispatch)
        assertEquals("Administrator", admin.getRoleDisplayName());
        assertEquals("Lecturer (Senior Lecturer)", lecturer.getRoleDisplayName());
        assertEquals("Technical Officer", to.getRoleDisplayName());
        assertEquals("Undergraduate (TG/2022/1001)", student.getRoleDisplayName());

        // Subclass-specific permission polymorphism
        assertTrue(admin.canEditFullProfile());
        assertFalse(student.canEditFullProfile());

        // Encapsulation verification
        assertEquals("TG/2022/1001", ((Student) student).getRegNo());
        assertEquals("CS", ((Lecturer) lecturer).getDepartment());
    }
}
