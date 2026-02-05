-- user_id NULL인 데이터 정리
DELETE FROM closet_items
WHERE closet_id IN (SELECT id FROM closets WHERE user_id IS NULL);
DELETE FROM closets WHERE user_id IS NULL;

--  user_id NOT NULL
ALTER TABLE closets
    ALTER COLUMN user_id SET NOT NULL;

-- (user_id, name) 유니크 제약
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_closet_user_name'
          AND conrelid = 'closets'::regclass
    ) THEN
ALTER TABLE closets
    ADD CONSTRAINT uk_closet_user_name UNIQUE (user_id, name);
END IF;
END $$;
