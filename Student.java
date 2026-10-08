package com.faculty.management.model;

/**
 * Student extends User with student-specific academic information.
 */
public class Student extends User {
    private String regNo;
    private String department;
    private String batch;
    private String studentStatus; // 'NORMAL', 'REPEAT', 'BATCH_MISSED'

    public Student() {
        super();
        setRole("STUDENT");
        this.studentStatus = "NORMAL";
    }

    public Student(int userId, String username, String passwordHash, String fullName, 
                   String email, String phone, String profileImage, 
                   String regNo, String department, String batch, String studentStatus) {
        super(userId, username, passwordHash, fullName, email, phone, "STUDENT", profileImage);
        this.regNo = regNo;
        this.department = department;
        this.batch = batch;
        this.studentStatus = (studentStatus != null) ? studentStatus : "NORMAL";
    }

    // OOP CONCEPT: POLYMORPHISM (Method Overriding)
    @Override
    public String getRoleDisplayName() {
        return "Undergraduate (" + regNo + ")";
    }

    @Override
    public boolean canEditFullProfile() {
        return false; // Students can only update contact details and profile picture
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getStudentStatus() {
        return studentStatus;
    }

    public void setStudentStatus(String studentStatus) {
        this.studentStatus = studentStatus;
    }
}
