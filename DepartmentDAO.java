package com.faculty.management.dao;

import com.faculty.management.database.DatabaseConnection;
import com.faculty.management.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data Access Object for Faculty Departments.
 */
public class DepartmentDAO implements IDepartmentDAO {

    public static final List<String> STANDARD_DEPARTMENTS = Arrays.asList(
        "Engineering Technology",
        "Information and communication Technology",
        "Bio System Technology",
        "Multidisciplinary Studies"
    );

    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT department_id, department_name FROM departments ORDER BY department_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Department(
                    rs.getInt("department_id"),
                    rs.getString("department_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Could not fetch departments from DB, using defaults: " + e.getMessage());
        }

        // Fallback to standard 4 departments if database table not yet populated
        if (list.isEmpty()) {
            int id = 1;
            for (String deptName : STANDARD_DEPARTMENTS) {
                list.add(new Department(id++, deptName));
            }
        }

        return list;
    }

    public Department getDepartmentById(int departmentId) {
        String sql = "SELECT department_id, department_name FROM departments WHERE department_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Department(
                        rs.getInt("department_id"),
                        rs.getString("department_name")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching department by ID: " + e.getMessage());
        }
        return null;
    }

    public Department getDepartmentByName(String name) {
        String sql = "SELECT department_id, department_name FROM departments WHERE department_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Department(
                        rs.getInt("department_id"),
                        rs.getString("department_name")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching department by name: " + e.getMessage());
        }
        return null;
    }
}
