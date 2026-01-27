-- user_id NULL인 데이터 정리
DELETE FROM closets WHERE user_id IS NULL;

--  user_id NOT NULL
ALTER TABLE closets
    ALTER COLUMN user_id SET NOT NULL;

-- (user_id, name) 유니크 제약
ALTER TABLE closets
    ADD CONSTRAINT uk_closet_user_name UNIQUE (user_id, name);
