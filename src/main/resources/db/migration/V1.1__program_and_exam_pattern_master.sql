CREATE TABLE IF NOT EXISTS programs (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(30) NOT NULL,
    degree_level VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    university_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    CONSTRAINT uk_program_university_code
        UNIQUE (university_id, code),

    CONSTRAINT fk_program_university
        FOREIGN KEY (university_id) REFERENCES universities(id)
);

CREATE INDEX IF NOT EXISTS idx_program_university_active
    ON programs(university_id, active);

CREATE TABLE IF NOT EXISTS exam_patterns (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(40) NOT NULL,
    description VARCHAR(500),
    effective_from_year INTEGER,
    effective_to_year INTEGER,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    university_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    CONSTRAINT uk_exam_pattern_university_code
        UNIQUE (university_id, code),

    CONSTRAINT fk_exam_pattern_university
        FOREIGN KEY (university_id) REFERENCES universities(id)
);

CREATE INDEX IF NOT EXISTS idx_exam_pattern_university_active
    ON exam_patterns(university_id, active);
