-- Repair community channel_type constraint without assuming the table is in public.
-- V22 used public.community_channels and could silently skip deployments whose
-- active PostgreSQL schema/search_path resolves the table elsewhere.
DO $$
BEGIN
    IF to_regclass('community_channels') IS NOT NULL THEN
        ALTER TABLE community_channels
            DROP CONSTRAINT IF EXISTS community_channels_channel_type_check;

        ALTER TABLE community_channels
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
