ALTER TABLE user_academic_profiles
    ADD COLUMN IF NOT EXISTS program_id VARCHAR(20),
    ADD COLUMN IF NOT EXISTS exam_pattern_id VARCHAR(20);

CREATE INDEX IF NOT EXISTS idx_user_academic_profiles_program
    ON user_academic_profiles(program_id);

CREATE INDEX IF NOT EXISTS idx_user_academic_profiles_exam_pattern
    ON user_academic_profiles(exam_pattern_id);

ALTER TABLE user_academic_profiles
    ADD CONSTRAINT fk_user_academic_profile_program
    FOREIGN KEY (program_id) REFERENCES programs(id);

ALTER TABLE user_academic_profiles
    ADD CONSTRAINT fk_user_academic_profile_exam_pattern
    FOREIGN KEY (exam_pattern_id) REFERENCES exam_patterns(id);
