CREATE TABLE IF NOT EXISTS business_id_sequences (
    prefix VARCHAR(16) PRIMARY KEY,
    next_value BIGINT NOT NULL,
    CONSTRAINT ck_business_id_sequences_next_value CHECK (next_value >= 10001)
);

INSERT INTO business_id_sequences (prefix, next_value) VALUES ('UNI', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('PRG', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('BRN', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('PAT', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('CUR', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('SEM', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('CAT', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('SUB', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('SOF', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('ELG', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('ASM', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('UNT', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('TOP', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('STP', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('RES', 10001);
INSERT INTO business_id_sequences (prefix, next_value) VALUES ('CSEM', 10001);
