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
    v_existing_count INTEGER;
BEGIN
    FOR r IN
        SELECT *
        FROM (VALUES
            ('SOF10001',1,'Introduction to Data Structures and Algorithms'),
            ('SOF10001',2,'Linear Data Structures, Searching and Sorting'),
            ('SOF10001',3,'Stacks, Queues and Linked Lists'),
            ('SOF10001',4,'Hashing'),
            ('SOF10001',5,'Graphs and Trees'),

            ('SOF10002',1,'Introduction to OOP Concepts and Control Structure'),
            ('SOF10002',2,'Introduction to Classes and Objects and Arrays'),
            ('SOF10002',3,'Inheritance and Polymorphism, Exception Handling and Multithreading'),
            ('SOF10002',4,'Graphics Primitives, Scan Conversion, Windowing and Clipping'),
            ('SOF10002',5,'2D, 3D Transformations and Projections'),

            ('SOF10003',1,'Introduction to Operating System'),
            ('SOF10003',2,'Process and Thread Management'),
            ('SOF10003',3,'Interprocess Communication and Deadlock'),
            ('SOF10003',4,'Memory Management'),
            ('SOF10003',5,'File and Disk Management'),

            ('SOF10011',1,'Introduction to Database Management System'),
            ('SOF10011',2,'SQL and PL/SQL'),
            ('SOF10011',3,'Relational Database Design'),
            ('SOF10011',4,'Database Transactions'),
            ('SOF10011',5,'NoSQL Database'),

            ('SOF10012',1,'Set and Propositions'),
            ('SOF10012',2,'Relations and Functions'),
            ('SOF10012',3,'Trees and Network Flow'),
            ('SOF10012',4,'Graph Theory'),
            ('SOF10012',5,'Counting Principles and Algebraic Structures')
        ) AS x(offering_id,unit_number,unit_name)
    LOOP
        SELECT id INTO v_subject_offering_id
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
                (business_id, unit_number, name, description, active,
                 subject_offering_id, coverage)
            VALUES
                (
                    'UNT' || LPAD((11000 + (
                        ROW_NUMBER() OVER (
                            ORDER BY r.offering_id, r.unit_number
                        )
                    ))::TEXT, 5, '0'),
                    r.unit_number,
                    r.unit_name,
                    'SPPU revised 2024 Pattern syllabus unit; effective AY 2026-27.',
                    TRUE,
                    v_subject_offering_id,
                    'BOTH'
                );
        ELSE
            UPDATE syllabus_units
               SET name = r.unit_name,
                   active = TRUE,
                   coverage = 'BOTH'
             WHERE subject_offering_id = r.offering_id
               AND unit_number = r.unit_number;
        END IF;
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
            ('SOF10001'),('SOF10002'),('SOF10003'),
            ('SOF10011'),('SOF10012')
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
