CREATE TABLE elective_groups (
    id VARCHAR(20) PRIMARY KEY,
    curriculum_semester_id VARCHAR(20) NOT NULL,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    required_selections INTEGER NOT NULL,
    display_order INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    CONSTRAINT uk_elective_group_semester_code
        UNIQUE (curriculum_semester_id, code),

    CONSTRAINT ck_elective_group_required_selections
        CHECK (required_selections > 0),

    CONSTRAINT ck_elective_group_display_order
        CHECK (display_order > 0),

    CONSTRAINT fk_elective_group_semester
        FOREIGN KEY (curriculum_semester_id)
        REFERENCES curriculum_semesters(id)
);

CREATE INDEX idx_elective_group_semester_active
    ON elective_groups(curriculum_semester_id, active);

CREATE TABLE elective_group_subjects (
    id VARCHAR(20) PRIMARY KEY,
    elective_group_id VARCHAR(20) NOT NULL,
    subject_offering_id VARCHAR(20) NOT NULL,
    display_order INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    CONSTRAINT uk_elective_group_subject
        UNIQUE (elective_group_id, subject_offering_id),

    CONSTRAINT ck_elective_group_subject_display_order
        CHECK (display_order > 0),

    CONSTRAINT fk_elective_group_subject_group
        FOREIGN KEY (elective_group_id)
        REFERENCES elective_groups(id),

    CONSTRAINT fk_elective_group_subject_offering
        FOREIGN KEY (subject_offering_id)
        REFERENCES subject_offerings(id)
);

CREATE INDEX idx_elective_group_subject_order
    ON elective_group_subjects(elective_group_id, display_order);

CREATE INDEX idx_elective_group_subject_offering
    ON elective_group_subjects(subject_offering_id);
