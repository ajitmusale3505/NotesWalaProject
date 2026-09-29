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
      WHERE university_id = v_university_id AND upper(code) IN ('BE','B.E.','BE-COMPUTER') LIMIT 1;
    IF v_program_id IS NULL THEN RAISE EXCEPTION 'BE program master record is required before seed'; END IF;

    SELECT id INTO v_pattern_id FROM exam_patterns
      WHERE university_id = v_university_id AND upper(code) IN ('2024','SPPU-2024','2024-PATTERN') LIMIT 1;
    IF v_pattern_id IS NULL THEN RAISE EXCEPTION 'SPPU 2024 exam pattern master record is required before seed'; END IF;

    SELECT id INTO v_academic_year_id FROM academic_years
      WHERE university_id = v_university_id AND (code ILIKE '%2024%' OR name ILIKE '%2024%')
      ORDER BY id DESC LIMIT 1;
    IF v_academic_year_id IS NULL THEN RAISE EXCEPTION 'SPPU 2024 academic-year master record is required before seed'; END IF;

    SELECT id INTO v_sem3_id FROM semesters WHERE number = 3 AND academic_year_id = v_academic_year_id LIMIT 1;
    SELECT id INTO v_sem4_id FROM semesters WHERE number = 4 AND academic_year_id = v_academic_year_id LIMIT 1;
    IF v_sem3_id IS NULL OR v_sem4_id IS NULL THEN RAISE EXCEPTION 'Semester 3 and 4 master records are required before seed'; END IF;

    SELECT id INTO v_cur_id FROM curriculums
      WHERE university_id=v_university_id AND program_id=v_program_id
        AND branch_id=v_branch_id AND exam_pattern_id=v_pattern_id
        AND code='BE-COM-2024-REV-2026' LIMIT 1;

    IF v_cur_id IS NULL THEN
        INSERT INTO curriculums(id,name,code,description,start_year,end_year,active,university_id,program_id,branch_id,exam_pattern_id)
        VALUES ('CUR10001','B.E. Computer Engineering - SPPU 2024 Pattern Revised','BE-COM-2024-REV-2026',
                'Revised S.E. Computer Engineering curriculum effective from Academic Year 2026-27.',
                2026,NULL,TRUE,v_university_id,v_program_id,v_branch_id,v_pattern_id)
        RETURNING id INTO v_cur_id;
    END IF;

    SELECT id INTO v_csem3_id FROM curriculum_semesters WHERE curriculum_id=v_cur_id AND semester_id=v_sem3_id LIMIT 1;
    IF v_csem3_id IS NULL THEN
        INSERT INTO curriculum_semesters(id,curriculum_id,semester_id,study_year,display_order,active)
        VALUES ('CSEM10003',v_cur_id,v_sem3_id,2,3,TRUE) RETURNING id INTO v_csem3_id;
    END IF;

    SELECT id INTO v_csem4_id FROM curriculum_semesters WHERE curriculum_id=v_cur_id AND semester_id=v_sem4_id LIMIT 1;
    IF v_csem4_id IS NULL THEN
        INSERT INTO curriculum_semesters(id,curriculum_id,semester_id,study_year,display_order,active)
        VALUES ('CSEM10004',v_cur_id,v_sem4_id,2,4,TRUE) RETURNING id INTO v_csem4_id;
    END IF;

    -- Reusable helper pattern: insert legacy Subject once, then bind it to this curriculum semester.
    -- Existing subjects with the same university/branch/code are reused.
    INSERT INTO subjects(business_id,name,code,active,subject_category,exam_type,credits,elective,honors,minor,branch_id,semester_id,academic_year_id)
    SELECT x.business_id,x.name,x.code,TRUE,x.category,x.exam_type,x.credits,x.elective,FALSE,FALSE,
           v_branch_id,x.v_semester_id,v_academic_year_id
    FROM (VALUES
      ('SUB10001','Data Structures','PCC-201-COM','CORE','THEORY_ONLY',3,FALSE,v_sem3_id),
      ('SUB10002','Object Oriented Programming and Computer Graphics','PCC-202-COM','CORE','THEORY_ONLY',3,FALSE,v_sem3_id),
      ('SUB10003','Operating Systems','PCC-203-COM','CORE','THEORY_ONLY',3,FALSE,v_sem3_id),
      ('SUB10004','Data Structures Laboratory','PCC-204-COM','LAB','PRACTICAL_ONLY',2,FALSE,v_sem3_id),
      ('SUB10005','Object Oriented Programming and Computer Graphics Laboratory','PCC-205-COM','LAB','PRACTICAL_ONLY',1,FALSE,v_sem3_id),
      ('SUB10006','Open Elective - I','OE-203-COM','ELECTIVE','THEORY_ONLY',2,TRUE,v_sem3_id),
      ('SUB10007','Digital Electronics and Logic Design','MDM-221-COM','CORE','THEORY_ONLY',2,FALSE,v_sem3_id),
      ('SUB10008','Entrepreneurship Development','EEM-231-COM','CORE','PRACTICAL_ONLY',2,FALSE,v_sem3_id),
      ('SUB10009','Universal Human Values and Professional Ethics','VEC-232-COM','CORE','THEORY_ONLY',2,FALSE,v_sem3_id),
      ('SUB10010','Community Engagement Project','CEP-241-COM','LAB','PRACTICAL_ONLY',2,FALSE,v_sem3_id),
      ('SUB10011','Database Management Systems','PCC-251-COM','CORE','THEORY_ONLY',3,FALSE,v_sem4_id),
      ('SUB10012','Discrete Mathematics','PCC-252-COM','CORE','THEORY_ONLY',3,FALSE,v_sem4_id),
      ('SUB10013','Computer Organization and Microprocessor','PCC-253-COM','CORE','THEORY_ONLY',2,FALSE,v_sem4_id),
      ('SUB10014','Database Management Laboratory','PCC-254-COM','LAB','PRACTICAL_ONLY',1,FALSE,v_sem4_id),
      ('SUB10015','Microprocessor Laboratory','PCC-255-COM','LAB','PRACTICAL_ONLY',1,FALSE,v_sem4_id),
      ('SUB10016','Open Elective - II','OE-253-COM','ELECTIVE','THEORY_ONLY',2,TRUE,v_sem4_id),
      ('SUB10017','Internet of Things','MDM-271-COM','CORE','THEORY_ONLY',2,FALSE,v_sem4_id),
      ('SUB10018','Web Development','VSE-281-COM','LAB','PRACTICAL_ONLY',2,FALSE,v_sem4_id),
      ('SUB10019','Modern Indian Language - Marathi','AEC-282-COM','CORE','THEORY_ONLY',2,FALSE,v_sem4_id),
      ('SUB10020','Engineering Product Design','EEM-283-COM','CORE','PRACTICAL_ONLY',2,FALSE,v_sem4_id),
      ('SUB10021','Environmental Studies','VEC-284-COM','CORE','THEORY_ONLY',2,FALSE,v_sem4_id)
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
      ('SOF10001','PCC-201-COM','CSEM10003'),('SOF10002','PCC-202-COM','CSEM10003'),
      ('SOF10003','PCC-203-COM','CSEM10003'),('SOF10004','PCC-204-COM','CSEM10003'),
      ('SOF10005','PCC-205-COM','CSEM10003'),('SOF10006','OE-203-COM','CSEM10003'),
      ('SOF10007','MDM-221-COM','CSEM10003'),('SOF10008','EEM-231-COM','CSEM10003'),
      ('SOF10009','VEC-232-COM','CSEM10003'),('SOF10010','CEP-241-COM','CSEM10003'),
      ('SOF10011','PCC-251-COM','CSEM10004'),('SOF10012','PCC-252-COM','CSEM10004'),
      ('SOF10013','PCC-253-COM','CSEM10004'),('SOF10014','PCC-254-COM','CSEM10004'),
      ('SOF10015','PCC-255-COM','CSEM10004'),('SOF10016','OE-253-COM','CSEM10004'),
      ('SOF10017','MDM-271-COM','CSEM10004'),('SOF10018','VSE-281-COM','CSEM10004'),
      ('SOF10019','AEC-282-COM','CSEM10004'),('SOF10020','EEM-283-COM','CSEM10004'),
      ('SOF10021','VEC-284-COM','CSEM10004')
    ) AS x(offering_id,code,csem_id)
    JOIN subjects s ON s.code=x.code
    WHERE NOT EXISTS (
      SELECT 1 FROM subject_offerings so
      WHERE so.curriculum_semester_id=x.csem_id AND so.subject_id=s.id AND so.code=s.code
    );
END $$;
