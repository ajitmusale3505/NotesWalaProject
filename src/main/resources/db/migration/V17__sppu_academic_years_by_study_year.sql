-- SPPU academic-year master correction.
--
-- Academic Year is the student's study year within the 2026-27 cycle.
-- Exam Pattern remains a separate master (SPPU 2024 Pattern).
--
-- Required SPPU options:
--   1st Year 2026-27
--   2nd Year 2026-27
--   3rd Year 2026-27
--   Last Year (BE) 2026-27
--
-- start_year/end_year are intentionally NULL. The display name/code carry
-- the academic cycle and study-year meaning.

DO $$
DECLARE
    v_sppu_id BIGINT;
    v_fe_id BIGINT;
    v_se_id BIGINT;
    v_te_id BIGINT;
    v_be_id BIGINT;
    v_program_id VARCHAR(20);
BEGIN
    SELECT id
      INTO v_sppu_id
      FROM universities
     WHERE short_code = 'SPPU'
     LIMIT 1;

    IF v_sppu_id IS NULL THEN
        RAISE EXCEPTION 'SPPU university master record is required before V17';
    END IF;

    -- Create the four canonical academic-year records.
    INSERT INTO academic_years(name, code, start_year, end_year, active, university_id)
    VALUES ('1st Year 2026-27', 'SPPU-FE-2026-27', NULL, NULL, TRUE, v_sppu_id)
    ON CONFLICT (name) DO UPDATE
        SET code = EXCLUDED.code,
            start_year = NULL,
            end_year = NULL,
            active = TRUE,
            university_id = EXCLUDED.university_id;

    INSERT INTO academic_years(name, code, start_year, end_year, active, university_id)
    VALUES ('2nd Year 2026-27', 'SPPU-SE-2026-27', NULL, NULL, TRUE, v_sppu_id)
    ON CONFLICT (name) DO UPDATE
        SET code = EXCLUDED.code,
            start_year = NULL,
            end_year = NULL,
            active = TRUE,
            university_id = EXCLUDED.university_id;

    INSERT INTO academic_years(name, code, start_year, end_year, active, university_id)
    VALUES ('3rd Year 2026-27', 'SPPU-TE-2026-27', NULL, NULL, TRUE, v_sppu_id)
    ON CONFLICT (name) DO UPDATE
        SET code = EXCLUDED.code,
            start_year = NULL,
            end_year = NULL,
            active = TRUE,
            university_id = EXCLUDED.university_id;

    INSERT INTO academic_years(name, code, start_year, end_year, active, university_id)
    VALUES ('Last Year (BE) 2026-27', 'SPPU-BE-2026-27', NULL, NULL, TRUE, v_sppu_id)
    ON CONFLICT (name) DO UPDATE
        SET code = EXCLUDED.code,
            start_year = NULL,
            end_year = NULL,
            active = TRUE,
            university_id = EXCLUDED.university_id;

    SELECT id INTO v_fe_id FROM academic_years WHERE code = 'SPPU-FE-2026-27';
    SELECT id INTO v_se_id FROM academic_years WHERE code = 'SPPU-SE-2026-27';
    SELECT id INTO v_te_id FROM academic_years WHERE code = 'SPPU-TE-2026-27';
    SELECT id INTO v_be_id FROM academic_years WHERE code = 'SPPU-BE-2026-27';

    -- Semesters belong to the study-year academic record:
    -- FE = Sem 1-2, SE = Sem 3-4, TE = Sem 5-6, BE = Sem 7-8.
    UPDATE semesters s
       SET academic_year_id = CASE
           WHEN s.number IN (1, 2) THEN v_fe_id
           WHEN s.number IN (3, 4) THEN v_se_id
           WHEN s.number IN (5, 6) THEN v_te_id
           WHEN s.number IN (7, 8) THEN v_be_id
           ELSE s.academic_year_id
       END
     WHERE s.academic_year_id IN (
         SELECT ay.id
           FROM academic_years ay
          WHERE ay.university_id = v_sppu_id
            AND ay.code IN ('SPPU-2019', 'SPPU-2024', 'AY-2026-27')
     );

    -- Keep legacy subject records aligned with their semester study year.
    UPDATE subjects sub
       SET academic_year_id = sem.academic_year_id
      FROM semesters sem
     WHERE sub.semester_id = sem.id
       AND sub.branch_id IN (
           SELECT b.id FROM branches b WHERE b.university_id = v_sppu_id
       )
       AND sem.academic_year_id IN (v_fe_id, v_se_id, v_te_id, v_be_id);

    -- The legacy branch->academic-year field is not used for hierarchy
    -- validation. Make it nullable and clear the stale 2019 assignment.
    ALTER TABLE branches ALTER COLUMN academic_year_id DROP NOT NULL;
    UPDATE branches
       SET academic_year_id = NULL
     WHERE university_id = v_sppu_id;

    -- Repair existing SPPU user profiles from their selected semester.
    UPDATE user_academic_profiles p
       SET academic_year_id = sem.academic_year_id
      FROM semesters sem
     WHERE p.current_semester_id = sem.id
       AND p.university_id = v_sppu_id
       AND sem.academic_year_id IN (v_fe_id, v_se_id, v_te_id, v_be_id);

    -- Existing SPPU Computer Engineering profiles should use the BE program.
    -- This also removes the "Academic program is required" startup/profile
    -- error for legacy profiles created before program_id was introduced.
    SELECT id
      INTO v_program_id
      FROM programs
     WHERE university_id = v_sppu_id
       AND upper(code) = 'BE'
       AND active = TRUE
     ORDER BY id
     LIMIT 1;

    IF v_program_id IS NOT NULL THEN
        UPDATE user_academic_profiles p
           SET program_id = v_program_id
         WHERE p.program_id IS NULL
           AND p.university_id = v_sppu_id
           AND p.branch_id IN (
               SELECT b.id
                 FROM branches b
                WHERE b.university_id = v_sppu_id
                  AND upper(b.code) = 'COMP'
           );
    END IF;

    -- Remove obsolete SPPU academic-year records after all known references
    -- have been migrated. The exam pattern record is NOT deleted.
    DELETE FROM academic_years
     WHERE university_id = v_sppu_id
       AND code IN ('SPPU-2019', 'SPPU-2024', 'AY-2026-27');
END $$;
