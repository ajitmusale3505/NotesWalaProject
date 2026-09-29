ALTER TABLE subjects
    ADD COLUMN IF NOT EXISTS business_id VARCHAR(20);

UPDATE subjects SET business_id = 'SUB' || (10000 + id) WHERE business_id IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_subjects_business_id
    ON subjects(business_id);

CREATE TABLE subject_categories (
    id VARCHAR(20) PRIMARY KEY,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(300),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    CONSTRAINT uk_subject_category_code UNIQUE (code)
);

INSERT INTO subject_categories (id, code, name, description, active)
VALUES
    ('CAT10001', 'REGULAR', 'Regular Subject', 'Mandatory/core academic subject', TRUE),
    ('CAT10002', 'ELECTIVE', 'Elective Subject', 'Subject selected from an elective group', TRUE),
    ('CAT10003', 'PRACTICAL', 'Practical Subject', 'Practical/laboratory subject', TRUE)
ON CONFLICT (code) DO NOTHING;

CREATE TABLE subject_offerings (
    id VARCHAR(20) PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    curriculum_semester_id VARCHAR(20) NOT NULL,
    code VARCHAR(40) NOT NULL,
    credits INTEGER NOT NULL,
    category_id VARCHAR(20) NOT NULL,
    mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    CONSTRAINT uk_subject_offering_curriculum_semester_code
        UNIQUE (curriculum_semester_id, subject_id, code),

    CONSTRAINT ck_subject_offering_credits
        CHECK (credits >= 0),

    CONSTRAINT fk_subject_offering_category
        FOREIGN KEY (category_id) REFERENCES subject_categories(id),

    CONSTRAINT fk_subject_offering_subject
        FOREIGN KEY (subject_id) REFERENCES subjects(id),

    CONSTRAINT fk_subject_offering_curriculum_semester
        FOREIGN KEY (curriculum_semester_id)
        REFERENCES curriculum_semesters(id)
);

CREATE INDEX idx_subject_offering_curriculum_semester_active
    ON subject_offerings(curriculum_semester_id, active);

CREATE INDEX idx_subject_offering_subject_active
    ON subject_offerings(subject_id, active);

UPDATE business_id_sequences
   SET next_value = GREATEST(
       next_value,
       COALESCE((SELECT MAX(id) + 10001 FROM subjects), 10001)
   )
 WHERE prefix = 'SUB';
