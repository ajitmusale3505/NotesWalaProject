CREATE TABLE assessment_component_types (
    id VARCHAR(20) PRIMARY KEY,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(300),
    practical BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    CONSTRAINT uk_assessment_type_code UNIQUE (code)
);

INSERT INTO assessment_component_types
    (id, code, name, description, practical, active)
VALUES
    ('ASM10001', 'INSEM', 'In-Semester Examination',
        'Internal/in-semester theory assessment', FALSE, TRUE),
    ('ASM10002', 'ENDSEM', 'End-Semester Examination',
        'University end-semester theory examination', FALSE, TRUE),
    ('ASM10003', 'TERM_WORK', 'Term Work',
        'Term work/internal continuous assessment', FALSE, TRUE),
    ('ASM10004', 'PRACTICAL', 'Practical',
        'Practical assessment component', TRUE, TRUE),
    ('ASM10005', 'PRACTICAL_MANUAL', 'Practical Manual',
        'Manual/record component of practical work', TRUE, TRUE),
    ('ASM10006', 'PRACTICAL_CODE', 'Practical Code',
        'Coding/programming component of practical work', TRUE, TRUE),
    ('ASM10007', 'VIVA', 'Viva / Oral',
        'Viva voce or oral examination', TRUE, TRUE),
    ('ASM10008', 'PROJECT', 'Project',
        'Project evaluation', FALSE, TRUE),
    ('ASM10009', 'INTERNSHIP', 'Internship',
        'Internship evaluation', FALSE, TRUE)
ON CONFLICT (code) DO NOTHING;

CREATE TABLE assessment_components (
    id VARCHAR(20) PRIMARY KEY,
    subject_offering_id VARCHAR(20) NOT NULL,
    type_id VARCHAR(20) NOT NULL,
    component_key VARCHAR(50) NOT NULL,
    max_marks INTEGER NOT NULL,
    passing_marks INTEGER,
    display_order INTEGER NOT NULL,
    included_in_total BOOLEAN NOT NULL DEFAULT TRUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    CONSTRAINT uk_assessment_offering_type
        UNIQUE (subject_offering_id, type_id, component_key),

    CONSTRAINT ck_assessment_max_marks
        CHECK (max_marks >= 0),

    CONSTRAINT ck_assessment_passing_marks
        CHECK (passing_marks IS NULL OR (passing_marks >= 0 AND passing_marks <= max_marks)),

    CONSTRAINT ck_assessment_display_order
        CHECK (display_order > 0),

    CONSTRAINT fk_assessment_offering
        FOREIGN KEY (subject_offering_id)
        REFERENCES subject_offerings(id),

    CONSTRAINT fk_assessment_type
        FOREIGN KEY (type_id)
        REFERENCES assessment_component_types(id)
);

CREATE INDEX idx_assessment_offering_order
    ON assessment_components(subject_offering_id, display_order);

CREATE INDEX idx_assessment_offering_active
    ON assessment_components(subject_offering_id, active);
