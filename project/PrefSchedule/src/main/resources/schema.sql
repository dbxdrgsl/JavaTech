-- Schema for PrefSchedule
-- Tables: students, instructors, packs, courses, student_preferences
-- Uses IDENTITY columns for portability between H2 and PostgreSQL (Postgres 10+ supports IDENTITY)

CREATE TABLE IF NOT EXISTS students (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    student_year INT NOT NULL
);

CREATE TABLE IF NOT EXISTS instructors (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS packs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pack_year INT NOT NULL,
    semester INT NOT NULL,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS courses (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type VARCHAR(50) NOT NULL, -- 'COMPULSORY' or 'OPTIONAL'
    code VARCHAR(50) NOT NULL,
    abbr VARCHAR(50),
    name VARCHAR(255) NOT NULL,
    instructor_id BIGINT,
    pack_id BIGINT,
    group_count INT,
    description TEXT,
    CONSTRAINT fk_course_instructor FOREIGN KEY (instructor_id) REFERENCES instructors(id),
    CONSTRAINT fk_course_pack FOREIGN KEY (pack_id) REFERENCES packs(id)
);

CREATE TABLE IF NOT EXISTS student_preferences (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    rank_order INT NOT NULL,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_preference_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_preference_course FOREIGN KEY (course_id) REFERENCES courses(id),
    CONSTRAINT uk_student_course UNIQUE (student_id, course_id)
);

-- Notes:
-- - Optional courses can be identified by type = 'OPTIONAL' and are grouped via pack_id.
-- - Students express preferences for courses via the student_preferences table.
-- - The rank_order indicates preference (1 = most preferred). Ties are allowed.
-- - The version field supports optimistic locking for concurrent updates.
