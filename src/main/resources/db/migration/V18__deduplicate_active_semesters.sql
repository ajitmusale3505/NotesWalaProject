-- Normalize duplicate active semesters created by the legacy academic-year seeds.
-- Keep historical rows for FK safety, but allow only one active semester per
-- academic year/semester number.

DO $$
BEGIN
    -- Move direct academic-profile and subject references to the deterministic
    -- canonical semester (lowest id) before retiring duplicate rows.
    WITH ranked AS (
        SELECT id,
               MIN(id) OVER (PARTITION BY academic_year_id, number) AS canonical_id
        FROM semesters
        WHERE active = TRUE
    )
    UPDATE user_academic_profiles p
       SET semester_id = r.canonical_id
      FROM ranked r
     WHERE p.semester_id = r.id
       AND r.id <> r.canonical_id;

    WITH ranked AS (
        SELECT id,
               MIN(id) OVER (PARTITION BY academic_year_id, number) AS canonical_id
        FROM semesters
        WHERE active = TRUE
    )
    UPDATE subjects s
       SET semester_id = r.canonical_id
      FROM ranked r
     WHERE s.semester_id = r.id
       AND r.id <> r.canonical_id;

    -- Curriculum-semester rows can already be unique for the same curriculum
    -- and canonical semester. Update only rows that cannot create a duplicate.
    WITH ranked AS (
        SELECT id,
               MIN(id) OVER (PARTITION BY academic_year_id, number) AS canonical_id
        FROM semesters
        WHERE active = TRUE
    )
    UPDATE curriculum_semesters cs
       SET semester_id = r.canonical_id
      FROM ranked r
     WHERE cs.semester_id = r.id
       AND r.id <> r.canonical_id
       AND NOT EXISTS (
           SELECT 1
             FROM curriculum_semesters existing
            WHERE existing.curriculum_id = cs.curriculum_id
              AND existing.semester_id = r.canonical_id
              AND existing.id <> cs.id
       );

    -- Retire remaining duplicate active rows instead of deleting them. This
    -- preserves all historical foreign-key references.
    WITH ranked AS (
        SELECT id,
               MIN(id) OVER (PARTITION BY academic_year_id, number) AS canonical_id
        FROM semesters
        WHERE active = TRUE
    )
    UPDATE semesters s
       SET active = FALSE
      FROM ranked r
     WHERE s.id = r.id
       AND r.id <> r.canonical_id;

    CREATE UNIQUE INDEX IF NOT EXISTS uk_semesters_active_year_number
        ON semesters(academic_year_id, number)
        WHERE active = TRUE;
END $$;
