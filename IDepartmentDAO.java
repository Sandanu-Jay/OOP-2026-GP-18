package com.faculty.management.dao;

import com.faculty.management.model.Department;
import java.util.List;

/**
 * Data Access Object Interface for Faculty Departments.
 */
public interface IDepartmentDAO {

    List<Department> getAllDepartments();

    Department getDepartmentById(int departmentId);

    Department getDepartmentByName(String name);
}
