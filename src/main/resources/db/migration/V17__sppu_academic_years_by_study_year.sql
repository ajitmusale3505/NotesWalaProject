-- Correct SPPU academic-year master.
-- Academic year here means the student's study year in the 2026-27 cycle.
-- Exam Pattern remains a separate master (for example, SPPU 2024 Pattern).
DO $$
DECLARE
    v_sppu_id BIGINT;
    v_fe_id BIGINT;
    v_se_id BIGINT;
    v_te_id BIGINT;
    v_be_id BIGINT;
    v_program_id VARCHAR(20);
BEGIN
    SELECT id INTO v_sppu_id
      FROM universities
     WHERE short_code = 'SPPU'
     LIMIT 1;

    IF v_sppu_id IS NULL THEN
        RAISE NOTICE 'SPPU not present yet; V17 will be completed by application bootstrap';
        RETURN;
    END IF;

    INSERT INTO academic_years(name, code, start_year, end_year, active, university_id)
    VALUES ('1st Year 2026-27', 'SPPU-FE-2026-27', NULL, NULL, TRUE, v_sppu_id)
    ON CONFLICT (name) DO UPDATE SET code=EXCLUDED.code, start_year=NULL, end_year=NULL,
        active=TRUE, university_id=EXCLUDED.university_id;

    INSERT INTO academic_years(name, code, start_year, end_year, active, university_id)
    VALUES ('2nd Year 2026-27', 'SPPU-SE-2026-27', NULL, NULL, TRUE, v_sppu_id)
    ON CONFLICT (name) DO UPDATE SET code=EXCLUDED.code, start_year=NULL, end_year=NULL,
        active=TRUE, university_id=EXCLUDED.university_id;

    INSERT INTO academic_years(name, code, start_year, end_year, active, university_id)
    VALUES ('3rd Year 2026-27', 'SPPU-TE-2026-27', NULL, NULL, TRUE, v_sppu_id)
    ON CONFLICT (name) DO UPDATE SET code=EXCLUDED.code, start_year=NULL, end_year=NULL,
        active=TRUE, university_id=EXCLUDED.university_id;

    INSERT INTO academic_years(name, code, start_year, end_year, active, university_id)
    VALUES ('Last Year (BE) 2026-27', 'SPPU-BE-2026-27', NULL, NULL, TRUE, v_sppu_id)
    ON CONFLICT (name) DO UPDATE SET code=EXCLUDED.code, start_year=NULL, end_year=NULL,
        active=TRUE, university_id=EXCLUDED.university_id;

    SELECT id INTO v_fe_id FROM academic_years WHERE code='SPPU-FE-2026-27';
    SELECT id INTO v_se_id FROM academic_years WHERE code='SPPU-SE-2026-27';
    SELECT id INTO v_te_id FROM academic_years WHERE code='SPPU-TE-2026-27';
    SELECT id INTO v_be_id FROM academic_years WHERE code='SPPU-BE-2026-27';

    UPDATE semesters s
       SET academic_year_id = CASE
           WHEN s.number IN (1,2) THEN v_fe_id
           WHEN s.number IN (3,4) THEN v_se_id
           WHEN s.number IN (5,6) THEN v_te_id
           WHEN s.number IN (7,8) THEN v_be_id
           ELSE s.academic_year_id END
     WHERE s.academic_year_id IN (
         SELECT id FROM academic_years
          WHERE university_id=v_sppu_id
            AND code IN ('SPPU-2019','SPPU-2024','AY-2026-27')
     );

    UPDATE subjects sub
       SET academic_year_id=sem.academic_year_id
      FROM semesters sem
     WHERE sub.semester_id=sem.id
       AND sem.academic_year_id IN (v_fe_id,v_se_id,v_te_id,v_be_id);

    UPDATE branches SET academic_year_id=NULL
     WHERE university_id=v_sppu_id;

    UPDATE user_academic_profiles p
       SET academic_year_id=sem.academic_year_id
      FROM semesters sem
     WHERE p.current_semester_id=sem.id
       AND p.university_id=v_sppu_id
       AND sem.academic_year_id IN (v_fe_id,v_se_id,v_te_id,v_be_id);

    SELECT id INTO v_program_id
      FROM programs
     WHERE university_id=v_sppu_id AND upper(code)='BE' AND active=TRUE
     ORDER BY id LIMIT 1;

    IF v_program_id IS NOT NULL THEN
        UPDATE user_academic_profiles p
           SET program_id=v_program_id
         WHERE p.program_id IS NULL
           AND p.university_id=v_sppu_id
           AND p.branch_id IN (
               SELECT b.id FROM branches b
                WHERE b.university_id=v_sppu_id AND upper(b.code)='COMP'
           );
    END IF;

    DELETE FROM academic_years
     WHERE university_id=v_sppu_id
       AND code IN ('SPPU-2019','SPPU-2024','AY-2026-27');
END $$;
