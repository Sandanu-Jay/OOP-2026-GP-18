package com.faculty.management.model;

import java.time.LocalDateTime;

/**
 * OOP CONCEPT: ENCAPSULATION
 * Represents learning materials (lecture slides, lab sheets, notes) uploaded for courses.
 */
public class CourseMaterial {
    private int materialId;
    private int courseId;
    private String courseCode;
    private String title;
    private String description;
    private String fileType;
    private String filePathOrUrl;
    private LocalDateTime uploadedDate;

    public CourseMaterial() {
    }

    public CourseMaterial(int materialId, int courseId, String title, String description, 
                          String fileType, String filePathOrUrl, LocalDateTime uploadedDate) {
        this.materialId = materialId;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.fileType = fileType;
        this.filePathOrUrl = filePathOrUrl;
        this.uploadedDate = uploadedDate;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePathOrUrl() {
        return filePathOrUrl;
    }

    public void setFilePathOrUrl(String filePathOrUrl) {
        this.filePathOrUrl = filePathOrUrl;
    }

    public LocalDateTime getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(LocalDateTime uploadedDate) {
        this.uploadedDate = uploadedDate;
    }
}
