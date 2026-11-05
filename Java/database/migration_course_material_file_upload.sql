-- Run this once on an existing Faculty Management System database.
-- Adds database storage for real uploaded files (not external URLs).
USE faculty_management_db;

ALTER TABLE course_materials
    ADD COLUMN file_name VARCHAR(255) NULL AFTER file_path_or_url,
    ADD COLUMN file_data LONGBLOB NULL AFTER file_name;

-- Existing URL-only records are retained, but only newly uploaded file records
-- can be opened through the new file-upload interface.
