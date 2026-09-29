-- SPPU S.E. Computer Engineering 2024 Pattern - Revised (effective AY 2026-27)
-- Source-checked against the revised SPPU curriculum material dated 13-Jun-2026.
-- This migration seeds only subject/offering metadata. Unit/topic seed data is kept
-- separate so syllabus content can be independently verified before insertion.

DO $$
DECLARE
    v_university_id BIGINT;
    v_branch_id BIGINT;
    v_program_id VARCHAR(20);
    v_pattern_id VARCHAR(20);
    v_academic_year_id BIGINT;
    v_sem3_id BIGINT;
    v_sem4_id BIGINT;
    v_cur_id VARCHAR(20);
    v_csem3_id VARCHAR(20);
    v_csem4_id VARCHAR(20);
BEGIN
    SELECT id INTO v_university_id FROM universities
      WHERE lower(name) LIKE '%savitribai phule pune%' LIMIT 1;
    IF v_university_id IS NULL THEN RAISE EXCEPTION 'SPPU university master record is required before seed'; END IF;

    SELECT id INTO v_branch_id FROM branches
      WHERE university_id = v_university_id
        AND lower(name) = 'computer engineering' LIMIT 1;
    IF v_branch_id IS NULL THEN RAISE EXCEPTION 'Computer Engineering branch master record is required before seed'; END IF;

    SELECT id INTO v_program_id FROM programs
      WHERE university_id = v_university_id
        AND (upper(code) IN ('BE','B.E.','BE-COMPUTER')
             OR lower(name) LIKE '%b.e.%'
             OR lower(name) LIKE '%bachelor of engineering%')
      ORDER BY CASE WHEN upper(code) = 'BE' THEN 0 ELSE 1 END, id
      LIMIT 1;

    IF v_program_id IS NULL THEN
        UPDATE business_id_sequences
           SET next_value = next_value + 1
         WHERE prefix = 'PRG'
        RETURNING 'PRG' || (next_value - 1)::TEXT INTO v_program_id;

        INSERT INTO programs(id,name,code,degree_level,active,university_id)
        VALUES (
            v_program_id,
            'Bachelor of Engineering',
            'BE',
            'UNDERGRADUATE',
            TRUE,
            v_university_id
        );
    END IF;

    SELECT id INTO v_pattern_id FROM exam_patterns
      WHERE university_id = v_university_id
        AND (upper(code) IN ('2024','SPPU-2024','2024-PATTERN')
             OR lower(name) LIKE '%2024%')
      ORDER BY CASE WHEN upper(code) = '2024' THEN 0 ELSE 1 END, id
      LIMIT 1;

    IF v_pattern_id IS NULL THEN
        UPDATE business_id_sequences
           SET next_value = next_value + 1
         WHERE prefix = 'PAT'
        RETURNING 'PAT' || (next_value - 1)::TEXT INTO v_pattern_id;

        INSERT INTO exam_patterns(
            id,name,code,description,effective_from_year,effective_to_year,active,university_id
        )
        VALUES (
            v_pattern_id,
            'SPPU 2024 Pattern',
            'SPPU-2024',
            'Savitribai Phule Pune University 2024 Pattern.',
            2024,
            NULL,
            TRUE,
            v_university_id
        );
    END IF;

    SELECT id INTO v_academic_year_id FROM academic_years
      WHERE university_id = v_university_id
        AND (code ILIKE '%2024%' OR name ILIKE '%2024%')
      ORDER BY CASE WHEN upper(code) = 'SPPU-2024' THEN 0 ELSE 1 END, id DESC
      LIMIT 1;

    IF v_academic_year_id IS NULL THEN
        INSERT INTO academic_years(name,code,start_year,end_year,active,university_id)
        VALUES (
            'SPPU 2024 Pattern',
            'SPPU-2024',
            2024,
            NULL,
            TRUE,
            v_university_id
        )
        RETURNING id INTO v_academic_year_id;
    END IF;

    SELECT id INTO v_sem3_id
    FROM semesters
    WHERE number = 3 AND academic_year_id = v_academic_year_id
    LIMIT 1;

    IF v_sem3_id IS NULL THEN
        INSERT INTO semesters(number,name,active,academic_year_id)
        VALUES (3,'Semester 3',TRUE,v_academic_year_id)
        RETURNING id INTO v_sem3_id;
    END IF;

    SELECT id INTO v_sem4_id
    FROM semesters
    WHERE number = 4 AND academic_year_id = v_academic_year_id
    LIMIT 1;

    IF v_sem4_id IS NULL THEN
        INSERT INTO semesters(number,name,active,academic_year_id)
        VALUES (4,'Semester 4',TRUE,v_academic_year_id)
        RETURNING id INTO v_sem4_id;
    END IF;

    SELECT id INTO v_cur_id FROM curriculums
      WHERE university_id=v_university_id AND program_id=v_program_id
        AND branch_id=v_branch_id AND exam_pattern_id=v_pattern_id
        AND code='BE-COM-2024-REV-2026' LIMIT 1;

    IF v_cur_id IS NULL THEN
        INSERT INTO curriculums(id,name,code,description,start_year,end_year,active,university_id,program_id,branch_id,exam_pattern_id)
        VALUES ('CUR50001','B.E. Computer Engineering - SPPU 2024 Pattern Revised','BE-COM-2024-REV-2026',
                'Revised S.E. Computer Engineering curriculum effective from Academic Year 2026-27.',
                2026,NULL,TRUE,v_university_id,v_program_id,v_branch_id,v_pattern_id)
        RETURNING id INTO v_cur_id;
    END IF;

    SELECT id INTO v_csem3_id FROM curriculum_semesters WHERE curriculum_id=v_cur_id AND semester_id=v_sem3_id LIMIT 1;
    IF v_csem3_id IS NULL THEN
        INSERT INTO curriculum_semesters(id,curriculum_id,semester_id,study_year,display_order,active)
        VALUES ('CSEM50003',v_cur_id,v_sem3_id,2,3,TRUE) RETURNING id INTO v_csem3_id;
    END IF;

    SELECT id INTO v_csem4_id FROM curriculum_semesters WHERE curriculum_id=v_cur_id AND semester_id=v_sem4_id LIMIT 1;
    IF v_csem4_id IS NULL THEN
        INSERT INTO curriculum_semesters(id,curriculum_id,semester_id,study_year,display_order,active)
        VALUES ('CSEM50004',v_cur_id,v_sem4_id,2,4,TRUE) RETURNING id INTO v_csem4_id;
    END IF;

    -- Reusable helper pattern: insert legacy Subject once, then bind it to this curriculum semester.
    -- Existing subjects with the same university/branch/code are reused.
    INSERT INTO subjects(business_id,name,code,active,subject_category,exam_type,credits,elective,honors,minor,branch_id,semester_id,academic_year_id)
    SELECT x.business_id,x.name,x.code,TRUE,x.category,x.exam_type,x.credits,x.elective,FALSE,FALSE,
           v_branch_id,x.v_semester_id,v_academic_year_id
    FROM (VALUES
      ('SUB50001','Data Structures','PCC-201-COM','CORE','THEORY_ONLY',3,FALSE,v_sem3_id),
      ('SUB50002','Object Oriented Programming and Computer Graphics','PCC-202-COM','CORE','THEORY_ONLY',3,FALSE,v_sem3_id),
      ('SUB50003','Operating Systems','PCC-203-COM','CORE','THEORY_ONLY',3,FALSE,v_sem3_id),
      ('SUB50004','Data Structures Laboratory','PCC-204-COM','LAB','PRACTICAL_ONLY',2,FALSE,v_sem3_id),
      ('SUB50005','Object Oriented Programming and Computer Graphics Laboratory','PCC-205-COM','LAB','PRACTICAL_ONLY',1,FALSE,v_sem3_id),
      ('SUB50006','Open Elective - I','OE-203-COM','ELECTIVE','THEORY_ONLY',2,TRUE,v_sem3_id),
      ('SUB50007','Digital Electronics and Logic Design','MDM-221-COM','CORE','THEORY_ONLY',2,FALSE,v_sem3_id),
      ('SUB50008','Entrepreneurship Development','EEM-231-COM','CORE','PRACTICAL_ONLY',2,FALSE,v_sem3_id),
      ('SUB50009','Universal Human Values and Professional Ethics','VEC-232-COM','CORE','THEORY_ONLY',2,FALSE,v_sem3_id),
      ('SUB50010','Community Engagement Project','CEP-241-COM','LAB','PRACTICAL_ONLY',2,FALSE,v_sem3_id),
      ('SUB50011','Database Management Systems','PCC-251-COM','CORE','THEORY_ONLY',3,FALSE,v_sem4_id),
      ('SUB50012','Discrete Mathematics','PCC-252-COM','CORE','THEORY_ONLY',3,FALSE,v_sem4_id),
      ('SUB50013','Computer Organization and Microprocessor','PCC-253-COM','CORE','THEORY_ONLY',2,FALSE,v_sem4_id),
      ('SUB50014','Database Management Laboratory','PCC-254-COM','LAB','PRACTICAL_ONLY',1,FALSE,v_sem4_id),
      ('SUB50015','Microprocessor Laboratory','PCC-255-COM','LAB','PRACTICAL_ONLY',1,FALSE,v_sem4_id),
      ('SUB50016','Open Elective - II','OE-253-COM','ELECTIVE','THEORY_ONLY',2,TRUE,v_sem4_id),
      ('SUB50017','Internet of Things','MDM-271-COM','CORE','THEORY_ONLY',2,FALSE,v_sem4_id),
      ('SUB50018','Web Development','VSE-281-COM','LAB','PRACTICAL_ONLY',2,FALSE,v_sem4_id),
      ('SUB50019','Modern Indian Language - Marathi','AEC-282-COM','CORE','THEORY_ONLY',2,FALSE,v_sem4_id),
      ('SUB50020','Engineering Product Design','EEM-283-COM','CORE','PRACTICAL_ONLY',2,FALSE,v_sem4_id),
      ('SUB50021','Environmental Studies','VEC-284-COM','CORE','THEORY_ONLY',2,FALSE,v_sem4_id)
    ) AS x(business_id,name,code,category,exam_type,credits,elective,v_semester_id)
    WHERE NOT EXISTS (
      SELECT 1 FROM subjects s WHERE s.code=x.code
    );

    -- Bind offerings. Category codes are the normalized offering categories.
    INSERT INTO subject_offerings(id,subject_id,curriculum_semester_id,code,credits,category_id,mandatory,active)
    SELECT x.offering_id,s.id,x.csem_id,s.code,s.credits,
           CASE WHEN s.elective THEN 'CAT10002' WHEN s.subject_category='LAB' THEN 'CAT10003' WHEN s.subject_category='ELECTIVE' THEN 'CAT10002' ELSE 'CAT10001' END,
           NOT s.elective,TRUE
    FROM (VALUES
      ('SOF50001','PCC-201-COM','CSEM50003'),('SOF50002','PCC-202-COM','CSEM50003'),
      ('SOF50003','PCC-203-COM','CSEM50003'),('SOF50004','PCC-204-COM','CSEM50003'),
      ('SOF50005','PCC-205-COM','CSEM50003'),('SOF50006','OE-203-COM','CSEM50003'),
      ('SOF50007','MDM-221-COM','CSEM50003'),('SOF50008','EEM-231-COM','CSEM50003'),
      ('SOF50009','VEC-232-COM','CSEM50003'),('SOF50010','CEP-241-COM','CSEM50003'),
      ('SOF50011','PCC-251-COM','CSEM50004'),('SOF50012','PCC-252-COM','CSEM50004'),
      ('SOF50013','PCC-253-COM','CSEM50004'),('SOF50014','PCC-254-COM','CSEM50004'),
      ('SOF50015','PCC-255-COM','CSEM50004'),('SOF50016','OE-253-COM','CSEM50004'),
      ('SOF50017','MDM-271-COM','CSEM50004'),('SOF50018','VSE-281-COM','CSEM50004'),
      ('SOF50019','AEC-282-COM','CSEM50004'),('SOF50020','EEM-283-COM','CSEM50004'),
      ('SOF50021','VEC-284-COM','CSEM50004')
    ) AS x(offering_id,code,csem_id)
    JOIN subjects s ON s.code=x.code
    WHERE NOT EXISTS (
      SELECT 1 FROM subject_offerings so
      WHERE so.curriculum_semester_id=x.csem_id AND so.subject_id=s.id AND so.code=s.code
    );
    -- Keep shared business-id allocators ahead of the seeded range so future API-created
    -- records cannot collide with this deterministic curriculum seed.
    UPDATE business_id_sequences
       SET next_value = GREATEST(next_value, 50022)
     WHERE prefix IN ('SUB','SOF');

    UPDATE business_id_sequences
       SET next_value = GREATEST(next_value, 50002)
     WHERE prefix = 'CUR';

    UPDATE business_id_sequences
       SET next_value = GREATEST(next_value, 50005)
     WHERE prefix = 'CSEM';
END $;