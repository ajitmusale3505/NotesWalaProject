-- SPPU S.E. Computer Engineering 2024 Pattern - Revised assessment seed
-- Effective AY 2026-27.
-- Source-checked against the revised curriculum dated 13-Jun-2026.
-- Assessment components are intentionally separate from syllabus/unit data.
-- Manual/Code components are NOT invented where the official scheme only specifies
-- a generic Practical, Term Work, or Oral component.

DO $$
DECLARE
    r RECORD;
    v_type_id VARCHAR(20);
    v_component_number INTEGER := 10010;
BEGIN
    FOR r IN
        SELECT *
        FROM (VALUES
            ('SOF50001','INSEM',40,1),('SOF50001','ENDSEM',60,2),
            ('SOF50002','INSEM',40,1),('SOF50002','ENDSEM',60,2),
            ('SOF50003','INSEM',40,1),('SOF50003','ENDSEM',60,2),
            ('SOF50004','TERM_WORK',50,1),('SOF50004','PRACTICAL',25,2),
            ('SOF50005','TERM_WORK',25,1),('SOF50005','VIVA',25,2),
            ('SOF50006','INSEM',20,1),('SOF50006','ENDSEM',30,2),
            ('SOF50007','INSEM',40,1),('SOF50007','ENDSEM',60,2),
            ('SOF50008','TERM_WORK',25,1),
            ('SOF50009','INSEM',20,1),('SOF50009','ENDSEM',30,2),
            ('SOF50010','TERM_WORK',25,1),('SOF50010','PRACTICAL',25,2),

            ('SOF50011','INSEM',40,1),('SOF50011','ENDSEM',60,2),
            ('SOF50012','INSEM',40,1),('SOF50012','ENDSEM',60,2),
            ('SOF50013','INSEM',40,1),('SOF50013','ENDSEM',60,2),
            ('SOF50014','TERM_WORK',25,1),('SOF50014','PRACTICAL',25,2),
            ('SOF50015','VIVA',25,1),
            ('SOF50016','INSEM',20,1),('SOF50016','ENDSEM',30,2),
            ('SOF50017','INSEM',40,1),('SOF50017','ENDSEM',60,2),
            ('SOF50018','TERM_WORK',25,1),('SOF50018','PRACTICAL',25,2),
            ('SOF50019','TERM_WORK',50,1),
            ('SOF50020','TERM_WORK',25,1),
            ('SOF50021','INSEM',15,1),('SOF50021','ENDSEM',35,2)
        ) AS x(offering_id,type_code,max_marks,display_order)
    LOOP
        SELECT id INTO v_type_id
        FROM assessment_component_types
        WHERE code = r.type_code AND active = TRUE;

        IF v_type_id IS NULL THEN
            RAISE EXCEPTION 'Assessment component type % is missing/inactive', r.type_code;
        END IF;

        INSERT INTO assessment_components
            (id, subject_offering_id, type_id, component_key, max_marks,
             passing_marks, display_order, included_in_total,
             counts_toward_final_total, active)
        VALUES
            (
                'ASM' || v_component_number::TEXT,
                r.offering_id,
                v_type_id,
                r.type_code,
                r.max_marks,
                NULL,
                r.display_order,
                TRUE,
                TRUE,
                TRUE
            )
        ON CONFLICT (subject_offering_id, type_id, component_key)
        DO UPDATE SET
            max_marks = EXCLUDED.max_marks,
            display_order = EXCLUDED.display_order,
            included_in_total = TRUE,
            counts_toward_final_total = TRUE,
            active = TRUE,
            updated_at = CURRENT_TIMESTAMP;

        v_component_number := v_component_number + 1;
    END LOOP;
END $$;

DO $$
DECLARE
    v_missing_count INTEGER;
    v_sem3_total INTEGER;
    v_sem4_total INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO v_missing_count
    FROM subject_offerings so
    WHERE so.id BETWEEN 'SOF50001' AND 'SOF50021'
      AND so.active = TRUE
      AND so.curriculum_semester_id IN (
          SELECT id FROM curriculum_semesters
          WHERE id IN ('CSEM50003','CSEM50004') AND active = TRUE
      )
      AND NOT EXISTS (
          SELECT 1
          FROM assessment_components ac
          WHERE ac.subject_offering_id = so.id
            AND ac.active = TRUE
            AND ac.counts_toward_final_total = TRUE
      );

    IF v_missing_count > 0 THEN
        RAISE EXCEPTION 'Assessment seed incomplete: % subject offerings have no active assessment component', v_missing_count;
    END IF;

    SELECT COALESCE(SUM(ac.max_marks), 0)
    INTO v_sem3_total
    FROM assessment_components ac
    WHERE ac.subject_offering_id BETWEEN 'SOF50001' AND 'SOF50010'
      AND ac.active = TRUE
      AND ac.counts_toward_final_total = TRUE;

    SELECT COALESCE(SUM(ac.max_marks), 0)
    INTO v_sem4_total
    FROM assessment_components ac
    WHERE ac.subject_offering_id BETWEEN 'SOF50011' AND 'SOF50021'
      AND ac.active = TRUE
      AND ac.counts_toward_final_total = TRUE;

    IF v_sem3_total <> 700 THEN
        RAISE EXCEPTION 'SPPU Sem III assessment total mismatch: expected 700, got %', v_sem3_total;
    END IF;

    IF v_sem4_total <> 700 THEN
        RAISE EXCEPTION 'SPPU Sem IV assessment total mismatch: expected 700, got %', v_sem4_total;
    END IF;
END $$;
