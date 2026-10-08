package com.faculty.management.model;

/**
 * TechnicalOfficer extends User with department-specific responsibility.
 */
public class TechnicalOfficer extends User {
    private String department;

    public TechnicalOfficer() {
        super();
        setRole("TECHNICAL_OFFICER");
    }

    public TechnicalOfficer(int userId, String username, String passwordHash, String fullName, 
                            String email, String phone, String profileImage, String department) {
        super(userId, username, passwordHash, fullName, email, phone, "TECHNICAL_OFFICER", profileImage);
        this.department = department;
    }

    // OOP CONCEPT: POLYMORPHISM (Method Overriding)
    @Override
    public String getRoleDisplayName() {
        return "Technical Officer";
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
