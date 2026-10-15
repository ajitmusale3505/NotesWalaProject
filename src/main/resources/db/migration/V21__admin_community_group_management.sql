-- Add admin-managed university/branch/semester community group metadata.
DO $$
BEGIN
    IF to_regclass('public.community_channels') IS NOT NULL THEN
        ALTER TABLE public.community_channels
            ADD COLUMN IF NOT EXISTS semester_id BIGINT;
        ALTER TABLE public.community_channels
            ADD COLUMN IF NOT EXISTS name VARCHAR(120);
        ALTER TABLE public.community_channels
            ADD COLUMN IF NOT EXISTS description VARCHAR(500);
        ALTER TABLE public.community_channels
            ADD COLUMN IF NOT EXISTS icon_url VARCHAR(1000);
        ALTER TABLE public.community_channels
            ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
        ALTER TABLE public.community_channels
            ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_community_channel_semester') THEN
            ALTER TABLE public.community_channels
                ADD CONSTRAINT fk_community_channel_semester
                FOREIGN KEY (semester_id) REFERENCES public.semesters(id);
        END IF;

        -- University branch groups are unique per semester, not just per branch.
        DROP INDEX IF EXISTS public.uk_community_university_branch_group;
        CREATE UNIQUE INDEX IF NOT EXISTS uk_community_university_branch_semester_group
            ON public.community_channels (university_id, branch_id, semester_id)
            WHERE channel_type = 'UNIVERSITY_BRANCH'
              AND university_id IS NOT NULL AND branch_id IS NOT NULL AND semester_id IS NOT NULL;

        DROP INDEX IF EXISTS public.uk_community_college_branch_group;
        CREATE UNIQUE INDEX IF NOT EXISTS uk_community_college_branch_semester_group
            ON public.community_channels (college_id, branch_id, semester_id)
            WHERE channel_type = 'BRANCH'
              AND college_id IS NOT NULL AND branch_id IS NOT NULL AND semester_id IS NOT NULL;

        CREATE INDEX IF NOT EXISTS idx_community_channel_university_branch_semester
            ON public.community_channels (university_id, branch_id, semester_id);
    END IF;
END $$;
