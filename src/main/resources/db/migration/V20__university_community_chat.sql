-- Extends the existing community module while preserving college and branch channels.
DO $$
BEGIN
    IF to_regclass('public.community_channels') IS NOT NULL THEN
        ALTER TABLE public.community_channels
            ADD COLUMN IF NOT EXISTS university_id BIGINT;
        ALTER TABLE public.community_channels
            ALTER COLUMN college_id DROP NOT NULL;

        IF NOT EXISTS (
            SELECT 1 FROM pg_constraint WHERE conname = 'fk_community_channel_university'
        ) THEN
            ALTER TABLE public.community_channels
                ADD CONSTRAINT fk_community_channel_university
                FOREIGN KEY (university_id) REFERENCES public.universities(id);
        END IF;

        CREATE UNIQUE INDEX IF NOT EXISTS uk_community_university_channel
            ON public.community_channels (university_id)
            WHERE channel_type = 'UNIVERSITY' AND university_id IS NOT NULL;
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
