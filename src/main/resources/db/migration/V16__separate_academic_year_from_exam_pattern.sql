-- Correct the SPPU revised 2024 Pattern seed.
--
-- Exam Pattern and Academic Year are different concepts.
-- V11 seeded the revised 2024 Pattern curriculum (effective AY 2026-27)
-- but incorrectly used "SPPU 2024 Pattern" as the academic-year master.
-- Keep the existing academic_year PK so existing semester/profile FKs remain valid.

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
       AND (code = 'SPPU-2024' OR name = 'SPPU 2024 Pattern')
     ORDER BY id DESC
     LIMIT 1;

    IF v_academic_year_id IS NULL THEN
        RAISE NOTICE 'No incorrectly seeded SPPU 2024 academic-year record found; nothing to correct';
        RETURN;
    END IF;

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
        RAISE EXCEPTION
            'A separate SPPU Academic Year 2026-27 already exists. Resolve the duplicate academic-year master before V16.';
    END IF;

    UPDATE academic_years
       SET name = 'Academic Year 2026-27',
           code = 'AY-2026-27',
           start_year = 2026,
           end_year = 2027,
           active = TRUE
     WHERE id = v_academic_year_id;

    -- The legacy SPPU 2019 record is also an academic-cycle master, not an
    -- exam-pattern label. Keep its code for backward compatibility but make
    -- its display name unambiguous.
    UPDATE academic_years
       SET name = 'Academic Year 2019-2024'
     WHERE university_id = v_university_id
       AND code = 'SPPU-2019'
       AND name = 'SPPU 2019 Pattern';
END $$;

-- The revised exam pattern remains unchanged:
--   exam_patterns.name = 'SPPU 2024 Pattern'
--   exam_patterns.code = 'SPPU-2024'
--
-- The academic year is now:
--   academic_years.name = 'Academic Year 2026-27'
--   academic_years.code = 'AY-2026-27'
