package com.faculty.management.dao;

import com.faculty.management.model.MedicalRecord;
import java.util.List;


/**
 * OOP CONCEPT: ABSTRACTION & INTERFACE DESIGN
 * Data Access Object Interface for Student Medical Certificates.
 */
public interface IMedicalDAO {

    List<MedicalRecord> getAllMedicalRecords();

    List<MedicalRecord> getMedicalRecordsByStudent(int studentId);

    boolean addMedicalRecord(MedicalRecord record);

    boolean updateMedicalRecord(MedicalRecord record);

    boolean updateApprovalStatus(int medicalId, String status, String comments);

    boolean deleteMedicalRecord(int medicalId);

    int countPendingMedicals();
}
