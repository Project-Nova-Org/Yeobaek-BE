ALTER TABLE closet_items
    ADD COLUMN IF NOT EXISTS pos_x DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS pos_y DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS scale DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS rotation DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS z_index INTEGER;

-- 기존 데이터 NULL 방지 기본값 채우기
UPDATE closet_items
SET
    pos_x = COALESCE(pos_x, 0),
    pos_y = COALESCE(pos_y, 0),
    scale = COALESCE(scale, 1),
    rotation = COALESCE(rotation, 0),
    z_index = COALESCE(z_index, 0)
WHERE
    pos_x IS NULL OR pos_y IS NULL OR scale IS NULL OR rotation IS NULL OR z_index IS NULL;

-- 엔티티가 not null로 매핑되어 있다면 DB도 not null로 맞춤
ALTER TABLE closet_items
    ALTER COLUMN pos_x SET NOT NULL,
ALTER COLUMN pos_y SET NOT NULL,
    ALTER COLUMN scale SET NOT NULL,
    ALTER COLUMN rotation SET NOT NULL,
    ALTER COLUMN z_index SET NOT NULL;
