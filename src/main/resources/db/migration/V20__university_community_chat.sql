-- Extend community channels so each profile receives two precise group scopes.
DO $$
BEGIN
    IF to_regclass('public.community_channels') IS NOT NULL THEN
        ALTER TABLE public.community_channels
            ADD COLUMN IF NOT EXISTS university_id BIGINT;

        -- College id is null for the university + branch group.
        ALTER TABLE public.community_channels
            ALTER COLUMN college_id DROP NOT NULL;

        IF NOT EXISTS (
            SELECT 1 FROM pg_constraint WHERE conname = 'fk_community_channel_university'
        ) THEN
            ALTER TABLE public.community_channels
                ADD CONSTRAINT fk_community_channel_university
                FOREIGN KEY (university_id) REFERENCES public.universities(id);
        END IF;

        CREATE UNIQUE INDEX IF NOT EXISTS uk_community_university_branch_group
            ON public.community_channels (university_id, branch_id)
            WHERE channel_type = 'UNIVERSITY_BRANCH'
              AND university_id IS NOT NULL AND branch_id IS NOT NULL;

        CREATE UNIQUE INDEX IF NOT EXISTS uk_community_college_branch_group
            ON public.community_channels (college_id, branch_id)
            WHERE channel_type = 'BRANCH'
              AND college_id IS NOT NULL AND branch_id IS NOT NULL;
    END IF;

    IF to_regclass('public.community_messages') IS NOT NULL THEN
        ALTER TABLE public.community_messages
            ADD COLUMN IF NOT EXISTS client_message_id VARCHAR(64);

        CREATE UNIQUE INDEX IF NOT EXISTS uk_community_message_client_id
            ON public.community_messages (channel_id, author_id, client_message_id)
            WHERE client_message_id IS NOT NULL;

        CREATE INDEX IF NOT EXISTS idx_community_message_channel_id
            ON public.community_messages (channel_id, id DESC);
    END IF;
END $$;
