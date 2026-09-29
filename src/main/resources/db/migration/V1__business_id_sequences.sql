CREATE TABLE IF NOT EXISTS business_id_sequences (
    prefix VARCHAR(16) PRIMARY KEY,
    next_value BIGINT NOT NULL,
    CONSTRAINT ck_business_id_sequences_next_value CHECK (next_value >= 10001)
);

INSERT INTO business_id_sequences (prefix, next_value)
VALUES
    ('UNI', 10001),
    ('PRG', 10001),
    ('BRN', 10001),
    ('PAT', 10001),
    ('CUR', 10001),
    ('SEM', 10001),
    ('CAT', 10001),
    ('SUB', 10001),
    ('SOF', 10001),
    ('ELG', 10001),
    ('ASM', 10001),
    ('UNT', 10001),
    ('TOP', 10001),
    ('STP', 10001),
    ('RES', 10001)
ON CONFLICT (prefix) DO NOTHING;
