package com.faculty.management.dao;

import com.faculty.management.model.User;

/**
 * OOP CONCEPT: ABSTRACTION & INTERFACE DESIGN
 * Contract for User authentication and user account operations.
 * Defines the public behaviors without exposing implementation details.
 */
public interface IUserDAO {

    /**
     * Authenticates a user by username and plain text password.
     * Demonstrates Polymorphism by returning the concrete User subclass (Admin, Lecturer, etc.)
     * referenced via the User abstraction.
     *
     * @param username user login handle
     * @param plainPassword plain-text password to verify against stored hash
     * @return concrete User object or null if credentials fail
     */
    User authenticate(String username, String plainPassword);

    /**
     * Updates contact info and profile image.
     */
    boolean updateContactAndProfilePicture(int userId, String email, String phone, String profileImage);

    /**
     * Updates contact info, address, and profile image.
     */
    boolean updateContactAndProfilePicture(int userId, String email, String phone, String address, String profileImage);

    /**
     * Updates profile picture filename.
     */
    boolean updateProfileImage(int userId, String profileImage);

    /**
     * Updates user details by Administrator.
     */
    boolean updateUser(int userId, String fullName, String email, String phone, String role);

    /**
     * Updates user password with SHA-256 hash.
     */
    boolean updatePassword(int userId, String newPassword);

    /**
     * Deletes a user by ID.
     */
    boolean deleteUser(int userId);

    /**
     * Counts users belonging to a specific role.
     */
    int countUsersByRole(String role);

    /**
     * Retrieves total count of all users in the system.
     */
    int getTotalUsersCount();
}
