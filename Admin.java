package com.faculty.management.model;

/**
 * Admin extends User, inheriting all attributes and behaviors of User.
 */
public class Admin extends User {

    public Admin() {
        super();
        setRole("ADMIN");
    }

    public Admin(int userId, String username, String passwordHash, String fullName, 
                 String email, String phone, String profileImage) {
        super(userId, username, passwordHash, fullName, email, phone, "ADMIN", profileImage);
    }

    // OOP CONCEPT: POLYMORPHISM (Method Overriding)
    @Override
    public String getRoleDisplayName() {
        return "Administrator";
    }

    @Override
    public boolean canEditFullProfile() {
        return true; // Admin has full permission to edit profiles
    }
}
