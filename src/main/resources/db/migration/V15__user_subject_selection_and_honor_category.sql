-- User-managed current-semester subject selections.
-- Regular subjects are resolved automatically from the curriculum and are never
-- stored as user selections. Elective, honor and practical offerings may be selected.
INSERT INTO subject_categories (id, code, name, description, active)
VALUES (
    'CAT10004',
    'HONOR',
    'Honor Subject',
    'Subject selected as part of an honor/specialization track.',
    TRUE
)
ON CONFLICT (code) DO NOTHING;

INSERT INTO business_id_sequences (prefix, next_value)
VALUES ('USS', 10001)
ON CONFLICT (prefix) DO NOTHING;

CREATE TABLE IF NOT EXISTS user_subject_selections (
    id VARCHAR(20) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subject_offering_id VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    CONSTRAINT uk_user_subject_selection_user_offering
        UNIQUE (user_id, subject_offering_id),

    CONSTRAINT ck_user_subject_selection_id
        CHECK (id ~ '^USS[0-9]{5,}$'),

    CONSTRAINT fk_user_subject_selection_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_user_subject_selection_offering
        FOREIGN KEY (subject_offering_id)
        REFERENCES subject_offerings(id)
);

CREATE INDEX IF NOT EXISTS idx_user_subject_selection_user_active
    ON user_subject_selections(user_id, active);

CREATE INDEX IF NOT EXISTS idx_user_subject_selection_offering
    ON user_subject_selections(subject_offering_id, active);
