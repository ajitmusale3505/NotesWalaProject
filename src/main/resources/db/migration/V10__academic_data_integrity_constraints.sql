-- Academic integrity hardening.
-- These constraints prevent orphaned/inactive academic records from being used by
-- newly seeded subject offerings and assessment structures.

ALTER TABLE subject_offerings
    ADD CONSTRAINT ck_subject_offering_code_not_blank
    CHECK (length(trim(code)) > 0);

ALTER TABLE assessment_components
    ADD CONSTRAINT ck_assessment_component_key_not_blank
    CHECK (length(trim(component_key)) > 0);

ALTER TABLE elective_groups
    ADD CONSTRAINT ck_elective_group_code_not_blank
    CHECK (length(trim(code)) > 0);

ALTER TABLE curriculum_semesters
    ADD CONSTRAINT ck_curriculum_semester_study_year_positive
    CHECK (study_year BETWEEN 1 AND 4);

ALTER TABLE syllabus_units
    ADD CONSTRAINT ck_syllabus_unit_name_not_blank
    CHECK (length(trim(chapter_name)) > 0);

ALTER TABLE syllabus_topics
    ADD CONSTRAINT ck_syllabus_topic_name_not_blank
    CHECK (length(trim(name)) > 0);

ALTER TABLE syllabus_subtopics
    ADD CONSTRAINT ck_syllabus_subtopic_name_not_blank
    CHECK (length(trim(name)) > 0);

CREATE INDEX IF NOT EXISTS idx_curriculum_program_pattern_branch
    ON curriculums(university_id, program_id, branch_id, exam_pattern_id, active);

CREATE INDEX IF NOT EXISTS idx_subject_offering_semester_subject
    ON subject_offerings(curriculum_semester_id, subject_id, active);

CREATE INDEX IF NOT EXISTS idx_syllabus_units_offering_coverage
    ON syllabus_units(subject_offering_id, coverage, active);

CREATE INDEX IF NOT EXISTS idx_assessment_component_type_marks
    ON assessment_components(subject_offering_id, type_id, max_marks, active);

-- Seed/verification jobs must populate these tables only after the parent
-- academic context exists. Cross-context validation is enforced by the
-- application services before writes.
