-- Fix the SPPU 2024 revised curriculum academic-year master.
-- The 2024 value is an EXAM PATTERN, not an academic year.
-- The revised S.E. Computer Engineering curriculum seeded in V11 is effective
-- from Academic Year 2026-27, so its academic-year master must represent 2026-27.
DO $$
DECLARE
    v_university_id BIGINT;
    v_academic_year_id BIGINT;
BEGIN
    SELECT id
      INTO v_university_id
      FROM universities
     WHERE lower(name) LIKE '%savitribai phule pune%'
     LIMIT 1;

    IF v_university_id IS NULL THEN
        RAISE EXCEPTION 'SPPU university master record is required before V16';
    END IF;

    SELECT id
      INTO v_academic_year_id
      FROM academic_years
     WHERE university_id = v_university_id
       AND (
            code = 'SPPU-2024'
            OR lower(name) = 'sppu 2024 pattern'
       )
     ORDER BY id DESC
     LIMIT 1;

    IF v_academic_year_id IS NULL THEN
        RAISE NOTICE 'No SPPU-2024 academic-year misuse found; nothing to migrate';
        RETURN;
    END IF;

    -- Do not overwrite an already-correct 2026-27 record.
    IF EXISTS (
        SELECT 1
          FROM academic_years
         WHERE university_id = v_university_id
           AND code = 'AY-2026-27'
           AND id <> v_academic_year_id
    ) THEN
        -- Move dependent semester references to the existing correct year.
        UPDATE semesters
           SET academic_year_id = (
               SELECT id
                 FROM academic_years
                WHERE university_id = v_university_id
                  AND code = 'AY-2026-27'
                LIMIT 1
           )
         WHERE academic_year_id = v_academic_year_id;

        -- The old record can then be disabled without breaking existing profile
        -- references; those profiles are handled by the application on next edit.
        UPDATE academic_years
           SET active = FALSE
         WHERE id = v_academic_year_id;
    ELSE
        UPDATE academic_years
           SET name = '2026-27',
               code = 'AY-2026-27',
               start_year = 2026,
               end_year = 2027,
               active = TRUE
         WHERE id = v_academic_year_id;
    END IF;
END $$;
