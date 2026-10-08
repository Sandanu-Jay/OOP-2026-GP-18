package com.faculty.management.model;

/**
 * Lecturer extends User with lecturer-specific attributes (department, designation).
 */
public class Lecturer extends User {
    private String department;
    private String designation;

    public Lecturer() {
        super();
        setRole("LECTURER");
    }

    public Lecturer(int userId, String username, String passwordHash, String fullName, 
                    String email, String phone, String profileImage, 
                    String department, String designation) {
        super(userId, username, passwordHash, fullName, email, phone, "LECTURER", profileImage);
        this.department = department;
        this.designation = designation;
    }

    // OOP CONCEPT: POLYMORPHISM (Method Overriding)
    @Override
    public String getRoleDisplayName() {
        return "Lecturer (" + (designation != null ? designation : "Academic Staff") + ")";
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
