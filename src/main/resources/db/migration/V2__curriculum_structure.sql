CREATE TABLE curriculums (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(40) NOT NULL,
    description VARCHAR(500),
    start_year INTEGER NOT NULL,
    end_year INTEGER,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    university_id BIGINT NOT NULL,
    program_id VARCHAR(20) NOT NULL,
    branch_id BIGINT NOT NULL,
    exam_pattern_id VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    CONSTRAINT uk_curriculum_branch_pattern_code
        UNIQUE (branch_id, exam_pattern_id, code),

    CONSTRAINT fk_curriculum_university
        FOREIGN KEY (university_id) REFERENCES universities(id),

    CONSTRAINT fk_curriculum_program
        FOREIGN KEY (program_id) REFERENCES programs(id),

    CONSTRAINT fk_curriculum_branch
        FOREIGN KEY (branch_id) REFERENCES branches(id),

    CONSTRAINT fk_curriculum_exam_pattern
        FOREIGN KEY (exam_pattern_id) REFERENCES exam_patterns(id)
);

CREATE INDEX idx_curriculum_branch_pattern_active
    ON curriculums(branch_id, exam_pattern_id, active);

CREATE INDEX idx_curriculum_university_active
    ON curriculums(university_id, active);

CREATE TABLE curriculum_semesters (
    id VARCHAR(20) PRIMARY KEY,
    curriculum_id VARCHAR(20) NOT NULL,
    semester_id BIGINT NOT NULL,
    study_year INTEGER NOT NULL,
    display_order INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    CONSTRAINT uk_curriculum_semester
        UNIQUE (curriculum_id, semester_id),

    CONSTRAINT ck_curriculum_semester_study_year
        CHECK (study_year BETWEEN 1 AND 4),

    CONSTRAINT ck_curriculum_semester_display_order
        CHECK (display_order > 0),

    CONSTRAINT fk_curriculum_semester_curriculum
        FOREIGN KEY (curriculum_id) REFERENCES curriculums(id),

    CONSTRAINT fk_curriculum_semester_semester
        FOREIGN KEY (semester_id) REFERENCES semesters(id)
);

CREATE INDEX idx_curriculum_semester_lookup
    ON curriculum_semesters(curriculum_id, study_year, display_order);
