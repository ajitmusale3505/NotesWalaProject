-- Keep PostgreSQL's document_type check aligned with DocumentType.java.
-- DocumentType now describes the uploaded file format (PDF, WORD, ZIP, etc.);
-- the resource category remains responsible for content classification (Syllabus, Notes, PYQ, ...).
ALTER TABLE resources
    DROP CONSTRAINT IF EXISTS resources_document_type_check;

ALTER TABLE resources
    ADD CONSTRAINT resources_document_type_check
    CHECK (
        document_type IS NULL
        OR document_type IN (
            'PDF',
            'WORD',
            'ZIP',
            'RAR',
            'SEVEN_ZIP',
            'POWERPOINT',
            'EXCEL',
            'TEXT',
            'CSV',
            'OTHER',
            -- Legacy values retained for existing resources.
            'QUESTION_PAPER',
            'SOLVED_PAPER',
            'BOOK',
            'NOTE',
            'IMPORTANT_QUESTION',
            'STUDY_MATERIAL'
        )
    );
