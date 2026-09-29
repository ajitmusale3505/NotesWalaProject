ALTER TABLE resources
    ADD COLUMN IF NOT EXISTS subject_offering_id VARCHAR(20);

ALTER TABLE resources
    ADD CONSTRAINT fk_resources_subject_offering
    FOREIGN KEY (subject_offering_id)
    REFERENCES subject_offerings(id);

CREATE INDEX IF NOT EXISTS idx_resources_subject_offering
    ON resources(subject_offering_id, active, published);
