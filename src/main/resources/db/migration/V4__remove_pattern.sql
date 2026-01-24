-- Remove pattern_id foreign key constraint from items table
ALTER TABLE IF EXISTS items
    DROP CONSTRAINT IF EXISTS FKoy4vj6xiykaqkbwkxxsymhoeq;

-- Remove pattern_id column from items table
ALTER TABLE IF EXISTS items
    DROP COLUMN IF EXISTS pattern_id;

-- Drop patterns table
DROP TABLE IF EXISTS patterns;
