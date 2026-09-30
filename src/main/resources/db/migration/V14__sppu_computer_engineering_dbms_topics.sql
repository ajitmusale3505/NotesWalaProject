-- SPPU S.E. Computer Engineering 2024 Pattern - Revised DBMS topic seed
-- Effective AY 2026-27.
-- Source: revised SPPU PCC-251-COM course content, dated 13-Jun-2026.
-- This migration seeds topic-level structure only. Subtopics are intentionally
-- separated into a later migration so they can be verified independently.

DO $$
DECLARE
    r RECORD;
    v_unit_id BIGINT;
    v_topic_business_number INTEGER := 12001;
BEGIN
    FOR r IN
        SELECT *
        FROM (VALUES
            ('SOF50011',1,1,'Database Management System Fundamentals'),
            ('SOF50011',1,2,'Database Applications and Purpose'),
            ('SOF50011',1,3,'View of Data and Database Languages'),
            ('SOF50011',1,4,'Database System Structure and Enterprise Constraints'),
            ('SOF50011',1,5,'Data Models'),
            ('SOF50011',1,6,'Database Design and ER Model'),
            ('SOF50011',1,7,'Entity, Attributes, Relationships and Constraints'),
            ('SOF50011',1,8,'Keys and ER Design Process'),
            ('SOF50011',1,9,'ER Diagram and Design Issues'),
            ('SOF50011',1,10,'Extended ER Features and EER to Relational Tables'),

            ('SOF50011',2,1,'SQL DDL and DML'),
            ('SOF50011',2,2,'SELECT Queries and SQL Functions'),
            ('SOF50011',2,3,'Views and Indexes'),
            ('SOF50011',2,4,'GROUP BY, HAVING and JOIN Queries'),
            ('SOF50011',2,5,'Set Operations and Set Membership'),
            ('SOF50011',2,6,'Nested Queries'),
            ('SOF50011',2,7,'DCL and TCL'),
            ('SOF50011',2,8,'PL/SQL Control Statements'),
            ('SOF50011',2,9,'PL/SQL Cursors'),
            ('SOF50011',2,10,'Stored Procedures, Functions and Triggers'),

            ('SOF50011',3,1,'Relational Model Fundamentals'),
            ('SOF50011',3,2,'Attributes and Domains'),
            ('SOF50011',3,3,'Codd Rules and Relational Integrity'),
            ('SOF50011',3,4,'Features of Good Relational Designs'),
            ('SOF50011',3,5,'Normalization and Atomic Domains'),
            ('SOF50011',3,6,'Functional Dependencies and Decomposition'),
            ('SOF50011',3,7,'First Normal Form'),
            ('SOF50011',3,8,'Second Normal Form'),
            ('SOF50011',3,9,'Third Normal Form'),
            ('SOF50011',3,10,'Boyce-Codd Normal Form'),

            ('SOF50011',4,1,'Transaction Fundamentals and Management'),
            ('SOF50011',4,2,'Transaction Properties and ACID'),
            ('SOF50011',4,3,'Schedules and Serial Schedules'),
            ('SOF50011',4,4,'Conflict and View Serializability'),
            ('SOF50011',4,5,'Cascaded Aborts'),
            ('SOF50011',4,6,'Recoverable and Non-recoverable Schedules'),
            ('SOF50011',4,7,'Need for Concurrency Control'),
            ('SOF50011',4,8,'Locking Methods'),

            ('SOF50011',5,1,'NoSQL Database Fundamentals'),
            ('SOF50011',5,2,'NoSQL Data Models'),
            ('SOF50011',5,3,'CAP Theorem'),
            ('SOF50011',5,4,'BASE Properties'),
            ('SOF50011',5,5,'SQL and NoSQL Comparison'),
            ('SOF50011',5,6,'MongoDB CRUD Operations'),
            ('SOF50011',5,7,'MongoDB Indexing'),
            ('SOF50011',5,8,'MongoDB Aggregation')
        ) AS x(offering_id,unit_number,topic_number,topic_name)
    LOOP
        SELECT su.id INTO v_unit_id
        FROM syllabus_units su
        WHERE su.subject_offering_id = r.offering_id
          AND su.unit_number = r.unit_number
          AND su.active = TRUE;

        IF v_unit_id IS NULL THEN
            RAISE EXCEPTION 'Syllabus unit missing: offering %, unit %',
                r.offering_id, r.unit_number;
        END IF;

        INSERT INTO syllabus_topics
            (business_id, topic_number, name, description, active, unit_id, created_at, updated_at)
        VALUES
            (
                'TOP' || v_topic_business_number::TEXT,
                r.topic_number,
                r.topic_name,
                'SPPU revised 2024 Pattern topic for PCC-251-COM; effective AY 2026-27.',
                TRUE,
                v_unit_id,
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
        WHERE NOT EXISTS (
            SELECT 1
            FROM syllabus_topics st
            WHERE st.unit_id = v_unit_id
              AND st.topic_number = r.topic_number
        );

        v_topic_business_number := v_topic_business_number + 1;
    END LOOP;
END $$;

DO $$
DECLARE
    v_topic_count INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO v_topic_count
    FROM syllabus_topics st
    JOIN syllabus_units su ON su.id = st.unit_id
    WHERE su.subject_offering_id = 'SOF50011'
      AND st.active = TRUE;

    IF v_topic_count <> 46 THEN
        RAISE EXCEPTION 'DBMS topic seed mismatch: expected 46 active topics, found %',
            v_topic_count;
    END IF;
END $$;

UPDATE business_id_sequences
   SET next_value = GREATEST(next_value, 12047)
 WHERE prefix = 'TOP';
