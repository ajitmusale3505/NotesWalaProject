-- SPPU S.E. Computer Engineering 2024 Pattern - Revised syllabus unit seed
-- Effective AY 2026-27.
-- Unit coverage is BOTH because the revised theory guidance states CCE is based
-- on all units and ESE covers all five units.
-- Topic/subtopic content is intentionally kept in the next migration so the
-- syllabus hierarchy can be reviewed independently from unit master data.

DO $$
DECLARE
    r RECORD;
    v_subject_offering_id VARCHAR(20);
    v_subject_id BIGINT;
    v_existing_count INTEGER;
    v_unit_business_number INTEGER := 11001;
BEGIN
    FOR r IN
        SELECT *
        FROM (VALUES
            ('SOF50001',1,'Introduction to Data Structures and Algorithms'),
            ('SOF50001',2,'Linear Data Structures, Searching and Sorting'),
            ('SOF50001',3,'Stacks, Queues and Linked Lists'),
            ('SOF50001',4,'Hashing'),
            ('SOF50001',5,'Graphs and Trees'),

            ('SOF50002',1,'Introduction to OOP Concepts and Control Structure'),
            ('SOF50002',2,'Introduction to Classes and Objects and Arrays'),
            ('SOF50002',3,'Inheritance and Polymorphism, Exception Handling and Multithreading'),
            ('SOF50002',4,'Graphics Primitives, Scan Conversion, Windowing and Clipping'),
            ('SOF50002',5,'2D, 3D Transformations and Projections'),

            ('SOF50003',1,'Introduction to Operating System'),
            ('SOF50003',2,'Process and Thread Management'),
            ('SOF50003',3,'Interprocess Communication and Deadlock'),
            ('SOF50003',4,'Memory Management'),
            ('SOF50003',5,'File and Disk Management'),

            ('SOF50011',1,'Introduction to Database Management System'),
            ('SOF50011',2,'SQL and PL/SQL'),
            ('SOF50011',3,'Relational Database Design'),
            ('SOF50011',4,'Database Transactions'),
            ('SOF50011',5,'NoSQL Database'),

            ('SOF50012',1,'Set and Propositions'),
            ('SOF50012',2,'Relations and Functions'),
            ('SOF50012',3,'Trees and Network Flow'),
            ('SOF50012',4,'Graph Theory'),
            ('SOF50012',5,'Counting Principles and Algebraic Structures')
        ) AS x(offering_id,unit_number,unit_name)
    LOOP
        SELECT id, subject_id
          INTO v_subject_offering_id, v_subject_id
        FROM subject_offerings
        WHERE id = r.offering_id
          AND active = TRUE;

        IF v_subject_offering_id IS NULL THEN
            RAISE EXCEPTION 'Subject offering % is missing/inactive', r.offering_id;
        END IF;

        SELECT COUNT(*) INTO v_existing_count
        FROM syllabus_units
        WHERE subject_offering_id = r.offering_id
          AND unit_number = r.unit_number;

        IF v_existing_count = 0 THEN
            INSERT INTO syllabus_units
                (business_id, unit_number, chapter_name, description, active,
                 subject_id, subject_offering_id, coverage, created_at, updated_at)
            VALUES
                (
                    'UNT' || LPAD(v_unit_business_number::TEXT, 5, '0'),
                    r.unit_number,
                    r.unit_name,
                    'SPPU revised 2024 Pattern syllabus unit; effective AY 2026-27.',
                    TRUE,
                    v_subject_id,
                    v_subject_offering_id,
                    'BOTH',
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                );
        ELSE
            UPDATE syllabus_units
               SET chapter_name = r.unit_name,
                   active = TRUE,
                   coverage = 'BOTH'
             WHERE subject_offering_id = r.offering_id
               AND unit_number = r.unit_number;
        END IF;

        v_unit_business_number := v_unit_business_number + 1;
    END LOOP;
END $$;

-- Integrity checks: all seeded theory offerings must have exactly five units.
DO $$
DECLARE
    r RECORD;
    v_unit_count INTEGER;
BEGIN
    FOR r IN
        SELECT offering_id
        FROM (VALUES
            ('SOF50001'),('SOF50002'),('SOF50003'),
            ('SOF50011'),('SOF50012')
        ) AS x(offering_id)
    LOOP
        SELECT COUNT(*)
        INTO v_unit_count
        FROM syllabus_units
        WHERE subject_offering_id = r.offering_id
          AND active = TRUE;

        IF v_unit_count <> 5 THEN
            RAISE EXCEPTION 'Syllabus unit seed mismatch for %: expected 5 units, found %',
                r.offering_id, v_unit_count;
        END IF;
    END LOOP;
END $$;

-- Keep the custom-ID allocator ahead of the seeded IDs.
UPDATE business_id_sequences
   SET next_value = GREATEST(next_value, 11026)
 WHERE prefix = 'UNT';
