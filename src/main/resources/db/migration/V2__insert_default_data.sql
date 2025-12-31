-- Category (level 1)
INSERT INTO categories (name, level, parent_id, created_at, updated_at)
VALUES
    ('상의', 1, NULL, NOW(), NOW()),
    ('하의', 1, NULL, NOW(), NOW()),
    ('한벌 옷', 1, NULL, NOW(), NOW()),
    ('아우터', 1, NULL, NOW(), NOW()),
    ('신발', 1, NULL, NOW(), NOW()),
    ('악세서리', 1, NULL, NOW(), NOW());

-- Category (level 2)
INSERT INTO categories (name, level, parent_id, created_at, updated_at)
VALUES
    ('반팔티', 2, (SELECT id FROM categories WHERE name = '상의'), NOW(), NOW()),
    ('긴팔티', 2, (SELECT id FROM categories WHERE name = '상의'), NOW(), NOW()),
    ('셔츠', 2, (SELECT id FROM categories WHERE name = '상의'), NOW(), NOW()),
    ('니트', 2, (SELECT id FROM categories WHERE name = '상의'), NOW(), NOW()),
    ('맨투맨', 2, (SELECT id FROM categories WHERE name = '상의'), NOW(), NOW()),
    ('후드티', 2, (SELECT id FROM categories WHERE name = '상의'), NOW(), NOW()),
    ('민소매티', 2, (SELECT id FROM categories WHERE name = '상의'), NOW(), NOW()),
    ('트레이닝', 2, (SELECT id FROM categories WHERE name = '상의'), NOW(), NOW()),
    ('기타', 2, (SELECT id FROM categories WHERE name = '상의'), NOW(), NOW()),

    ('청바지', 2, (SELECT id FROM categories WHERE name = '하의'), NOW(), NOW()),
    ('면바지', 2, (SELECT id FROM categories WHERE name = '하의'), NOW(), NOW()),
    ('슬랙스', 2, (SELECT id FROM categories WHERE name = '하의'), NOW(), NOW()),
    ('반바지', 2, (SELECT id FROM categories WHERE name = '하의'), NOW(), NOW()),
    ('레깅스', 2, (SELECT id FROM categories WHERE name = '하의'), NOW(), NOW()),
    ('트레이닝', 2, (SELECT id FROM categories WHERE name = '하의'), NOW(), NOW()),
    ('치마', 2, (SELECT id FROM categories WHERE name = '하의'), NOW(), NOW()),
    ('기타', 2, (SELECT id FROM categories WHERE name = '하의'), NOW(), NOW()),

    ('원피스', 2, (SELECT id FROM categories WHERE name = '한벌 옷'), NOW(), NOW()),
    ('점프슈트', 2, (SELECT id FROM categories WHERE name = '한벌 옷'), NOW(), NOW()),
    ('기타', 2, (SELECT id FROM categories WHERE name = '한벌 옷'), NOW(), NOW()),

    ('자켓', 2, (SELECT id FROM categories WHERE name = '아우터'), NOW(), NOW()),
    ('코트', 2, (SELECT id FROM categories WHERE name = '아우터'), NOW(), NOW()),
    ('점퍼', 2, (SELECT id FROM categories WHERE name = '아우터'), NOW(), NOW()),
    ('가디건', 2, (SELECT id FROM categories WHERE name = '아우터'), NOW(), NOW()),
    ('집업', 2, (SELECT id FROM categories WHERE name = '아우터'), NOW(), NOW()),
    ('패딩', 2, (SELECT id FROM categories WHERE name = '아우터'), NOW(), NOW()),
    ('기타', 2, (SELECT id FROM categories WHERE name = '아우터'), NOW(), NOW()),

    ('운동화', 2, (SELECT id FROM categories WHERE name = '신발'), NOW(), NOW()),
    ('구두', 2, (SELECT id FROM categories WHERE name = '신발'), NOW(), NOW()),
    ('샌들', 2, (SELECT id FROM categories WHERE name = '신발'), NOW(), NOW()),
    ('슬리퍼', 2, (SELECT id FROM categories WHERE name = '신발'), NOW(), NOW()),
    ('부츠', 2, (SELECT id FROM categories WHERE name = '신발'), NOW(), NOW()),
    ('하이힐', 2, (SELECT id FROM categories WHERE name = '신발'), NOW(), NOW()),
    ('뮬', 2, (SELECT id FROM categories WHERE name = '신발'), NOW(), NOW()),
    ('기타', 2, (SELECT id FROM categories WHERE name = '신발'), NOW(), NOW()),

    ('목걸이', 2, (SELECT id FROM categories WHERE name = '악세서리'), NOW(), NOW()),
    ('반지', 2, (SELECT id FROM categories WHERE name = '악세서리'), NOW(), NOW()),
    ('팔찌', 2, (SELECT id FROM categories WHERE name = '악세서리'), NOW(), NOW()),
    ('손목시계', 2, (SELECT id FROM categories WHERE name = '악세서리'), NOW(), NOW()),
    ('귀걸이', 2, (SELECT id FROM categories WHERE name = '악세서리'), NOW(), NOW()),
    ('벨트', 2, (SELECT id FROM categories WHERE name = '악세서리'), NOW(), NOW()),
    ('넥타이', 2, (SELECT id FROM categories WHERE name = '악세서리'), NOW(), NOW()),
    ('모자', 2, (SELECT id FROM categories WHERE name = '악세서리'), NOW(), NOW()),
    ('가방', 2, (SELECT id FROM categories WHERE name = '악세서리'), NOW(), NOW()),
    ('양말', 2, (SELECT id FROM categories WHERE name = '악세서리'), NOW(), NOW()),
    ('장갑', 2, (SELECT id FROM categories WHERE name = '악세서리'), NOW(), NOW()),
    ('머플러', 2, (SELECT id FROM categories WHERE name = '악세서리'), NOW(), NOW()),
    ('기타', 2, (SELECT id FROM categories WHERE name = '악세서리'), NOW(), NOW());

-- Material
INSERT INTO materials (name, created_at, updated_at)
VALUES
    ('데님', NOW(), NOW()),
    ('면', NOW(), NOW()),
    ('가죽', NOW(), NOW()),
    ('울', NOW(), NOW()),
    ('실크', NOW(), NOW()),
    ('기타', NOW(), NOW());

-- Pattern
INSERT INTO patterns (name, created_at, updated_at)
VALUES
    ('무지', NOW(), NOW()),
    ('스트라이프', NOW(), NOW()),
    ('체크', NOW(), NOW()),
    ('도트', NOW(), NOW()),
    ('프린트', NOW(), NOW()),
    ('기타', NOW(), NOW());

-- Style
INSERT INTO styles (name, created_at, updated_at)
VALUES
    ('캐쥬얼', NOW(), NOW()),
    ('클래식', NOW(), NOW()),
    ('빈티지', NOW(), NOW()),
    ('스트릿', NOW(), NOW()),
    ('스포티', NOW(), NOW()),
    ('힙합', NOW(), NOW()),
    ('기타', NOW(), NOW());

-- TPO
INSERT INTO tpos (name, created_at, updated_at)
VALUES
    ('데일리', NOW(), NOW()),
    ('포멀', NOW(), NOW()),
    ('데이트', NOW(), NOW()),
    ('여행', NOW(), NOW()),
    ('레저', NOW(), NOW()),
    ('파티', NOW(), NOW()),
    ('하객룩', NOW(), NOW()),
    ('기타', NOW(), NOW());
