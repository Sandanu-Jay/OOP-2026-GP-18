-- Run this once on an existing faculty_management_db database.
ALTER TABLE courses ADD COLUMN level INT NOT NULL DEFAULT 1 AFTER department;
-- Existing courses remain Level 1. Update them if required, for example:
-- UPDATE courses SET level = 2 WHERE course_code LIKE 'ICT2%';
