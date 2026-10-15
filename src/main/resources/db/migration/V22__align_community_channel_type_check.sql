-- Align PostgreSQL's persisted channel_type check with CommunityChannelType.java.
-- Existing databases may have created this constraint before UNIVERSITY_BRANCH was added.
-- Guard this migration for fresh installs where Hibernate creates the table after Flyway runs.
DO $$
BEGIN
    IF to_regclass('public.community_channels') IS NOT NULL THEN
        ALTER TABLE public.community_channels
            DROP CONSTRAINT IF EXISTS community_channels_channel_type_check;

        ALTER TABLE public.community_channels
            ADD CONSTRAINT community_channels_channel_type_check
            CHECK (
                channel_type IN (
                    'COLLEGE',
                    'BRANCH',
                    'UNIVERSITY',
                    'UNIVERSITY_BRANCH'
                )
            );
    END IF;
END $$;
