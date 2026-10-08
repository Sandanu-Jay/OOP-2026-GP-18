package com.faculty.management.model;

import java.time.LocalDateTime;

/**
 * - Abstract Class: User cannot be instantiated directly; it defines common state and behavior
 *   for Admin, Lecturer, TechnicalOfficer, and Student.
 */
public abstract class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private String address = "";
    private String role; // 'ADMIN', 'LECTURER', 'TECHNICAL_OFFICER', 'STUDENT'
    private String profileImage;
    private LocalDateTime createdAt;

    // Default Constructor
    public User() {
    }

    // Parameterized Constructor
    public User(int userId, String username, String passwordHash, String fullName, 
                String email, String phone, String role, String profileImage) {
        this(userId, username, passwordHash, fullName, email, phone, "", role, profileImage);
    }

    public User(int userId, String username, String passwordHash, String fullName, 
                String email, String phone, String address, String role, String profileImage) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = (address != null) ? address : "";
        this.role = role;
        this.profileImage = (profileImage != null && !profileImage.isEmpty()) ? profileImage : "default_avatar.png";
    }

    // OOP CONCEPT: ABSTRACTION
    // Abstract method that every subclass must implement polymorphically
    public abstract String getRoleDisplayName();

    // OOP CONCEPT: POLYMORPHISM
    // Subclasses can override this method to define their profile edit permissions
    public boolean canEditFullProfile() {
        return false; // Default: restricted profile editing
    }

    // Getters and Setters (Encapsulation)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return fullName + " (" + getRoleDisplayName() + ")";
    }
}
