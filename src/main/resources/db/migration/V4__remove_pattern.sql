-- Remove pattern_id foreign key constraint from items table
ALTER TABLE IF EXISTS items
    DROP CONSTRAINT IF EXISTS FKoy4vj6xiykaqkbwkxxsymhoeq;

-- Remove pattern_id column from items table
ALTER TABLE IF EXISTS items
    DROP COLUMN IF EXISTS pattern_id;

-- Drop patterns table
DROP TABLE IF EXISTS patterns;

-- Add missing materials
INSERT INTO materials (name, created_at, updated_at)
VALUES
    ('나일론', NOW(), NOW()),
    ('폴리에스터', NOW(), NOW()),
    ('캐시미어', NOW(), NOW()),
    ('스웨이드', NOW(), NOW()),
    ('코듀로이', NOW(), NOW()),
    ('아크릴', NOW(), NOW()),
    ('레이온', NOW(), NOW()),
    ('린넨', NOW(), NOW()),
    ('알파카', NOW(), NOW()),
    ('기타', NOW(), NOW());
