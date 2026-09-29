ALTER TABLE syllabus_units
    ADD COLUMN IF NOT EXISTS business_id VARCHAR(20),
    ADD COLUMN IF NOT EXISTS subject_offering_id VARCHAR(20),
    ADD COLUMN IF NOT EXISTS coverage VARCHAR(10) NOT NULL DEFAULT 'BOTH';

UPDATE syllabus_units
   SET business_id = 'UNT' || (10000 + id)
 WHERE business_id IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_syllabus_units_business_id
    ON syllabus_units(business_id);

ALTER TABLE syllabus_topics
    ADD COLUMN IF NOT EXISTS business_id VARCHAR(20);

UPDATE syllabus_topics
   SET business_id = 'TOP' || (10000 + id)
 WHERE business_id IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_syllabus_topics_business_id
    ON syllabus_topics(business_id);

ALTER TABLE syllabus_units
    ADD CONSTRAINT fk_syllabus_units_subject_offering
    FOREIGN KEY (subject_offering_id)
    REFERENCES subject_offerings(id);

CREATE INDEX idx_syllabus_units_subject_offering
    ON syllabus_units(subject_offering_id, active);

CREATE TABLE syllabus_subtopics (
    id VARCHAR(20) PRIMARY KEY,
    subtopic_number INTEGER NOT NULL,
    name VARCHAR(400) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    topic_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    CONSTRAINT uk_syllabus_subtopics_topic_number
        UNIQUE (topic_id, subtopic_number),

    CONSTRAINT ck_syllabus_subtopics_number
        CHECK (subtopic_number > 0),

    CONSTRAINT fk_syllabus_subtopics_topic
        FOREIGN KEY (topic_id)
        REFERENCES syllabus_topics(id)
);

CREATE INDEX idx_syllabus_subtopics_topic_active
    ON syllabus_subtopics(topic_id, active);

ALTER TABLE syllabus_units
    ADD CONSTRAINT ck_syllabus_units_coverage
    CHECK (coverage IN ('INSEM', 'ENDSEM', 'BOTH'));

UPDATE business_id_sequences
   SET next_value = GREATEST(
       next_value,
       COALESCE((SELECT MAX(id) + 10001 FROM syllabus_units), 10001)
   )
 WHERE prefix = 'UNT';

UPDATE business_id_sequences
   SET next_value = GREATEST(
       next_value,
       COALESCE((SELECT MAX(id) + 10001 FROM syllabus_topics), 10001)
   )
 WHERE prefix = 'TOP';

