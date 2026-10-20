package com.faculty.management.model;

import java.time.LocalDateTime;

/**
 * OOP CONCEPT: ENCAPSULATION
 * Represents an announcement or official notice published within the faculty.
 */
public class Notice {
    private int noticeId;
    private String title;
    private String content;
    private String targetAudience; // 'ALL', 'STUDENTS', 'LECTURERS', 'OFFICERS'
    private int postedBy;
    private String postedByName;
    private LocalDateTime postedDate;
    private boolean isPinned;

    public Notice() {
        this.targetAudience = "ALL";
    }

    public Notice(int noticeId, String title, String content, String targetAudience, 
                  int postedBy, LocalDateTime postedDate, boolean isPinned) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.targetAudience = targetAudience;
        this.postedBy = postedBy;
        this.postedDate = postedDate;
        this.isPinned = isPinned;
    }

    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public int getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(int postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostedByName() {
        return postedByName;
    }

    public void setPostedByName(String postedByName) {
        this.postedByName = postedByName;
    }

    public LocalDateTime getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDateTime postedDate) {
        this.postedDate = postedDate;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
}
