-- Correct the SPPU revised 2024 Pattern seed.
--
-- "Exam Pattern" and "Academic Year" are different academic concepts.
-- The revised S.E. Computer Engineering 2024 Pattern curriculum seeded in V11
-- is effective from Academic Year 2026-27. V11 previously used the value
-- "SPPU 2024 Pattern" for the academic_years master, which caused the UI to
-- show the same values (2019 / 2024 Pattern) in both dropdowns.
--
-- Keep the existing academic_year primary key so existing semester/profile
-- foreign keys remain valid. Only the academic-year master attributes are
-- corrected.

DO $$
DECLARE
    v_university_id BIGINT;
    v_academic_year_id BIGINT;
BEGIN
    SELECT id
      INTO v_university_id
      FROM universities
     WHERE lower(name) LIKE '%savitribai phule pune%'
     ORDER BY id
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
            OR name = 'SPPU 2024 Pattern'
       )
     ORDER BY id DESC
     LIMIT 1;

    IF v_academic_year_id IS NULL THEN
        RAISE NOTICE 'No incorrectly seeded SPPU 2024 academic-year record found; nothing to correct';
        RETURN;
    END IF;

    -- Avoid creating a duplicate if a correct 2026-27 record already exists.
    IF EXISTS (
        SELECT 1
          FROM academic_years
         WHERE university_id = v_university_id
           AND id <> v_academic_year_id
           AND (
                code = 'AY-2026-27'
                OR (start_year = 2026 AND end_year = 2027)
           )
    ) THEN
        -- Move semesters to the already-correct academic year before removing
        -- the duplicate master row. This branch is only for databases that
        -- already contain the correct 2026-27 master.
        SELECT id
          INTO v_academic_year_id
          FROM academic_years
         WHERE university_id = v_university_id
           AND id <> v_academic_year_id
           AND (
                code = 'AY-2026-27'
                OR (start_year = 2026 AND end_year = 2027)
           )
         ORDER BY CASE WHEN code = 'AY-2026-27' THEN 0 ELSE 1 END, id
         LIMIT 1;
    ELSE
        UPDATE academic_years
           SET name = 'Academic Year 2026-27',
               code = 'AY-2026-27',
               start_year = 2026,
               end_year = 2027,
               active = TRUE
         WHERE id = v_academic_year_id;
    END IF;
END $$;

-- The revised 2024 Pattern remains the exam pattern:
--   exam_patterns.name = 'SPPU 2024 Pattern'
--   exam_patterns.code = 'SPPU-2024'
--
-- The academic year is now:
--   academic_years.name = 'Academic Year 2026-27'
--   academic_years.code = 'AY-2026-27'
--
-- This intentionally does not modify exam_patterns.
