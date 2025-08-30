ALTER TABLE theme
    MODIFY (type VARCHAR2(100) NOT NULL);

MERGE INTO theme t
USING (
    SELECT '바다' AS type FROM dual UNION ALL
    SELECT '자연' FROM dual UNION ALL
    SELECT '힐링여행' FROM dual UNION ALL
    SELECT '온천' FROM dual UNION ALL
    SELECT '등산' FROM dual UNION ALL
    SELECT 'K-POP' FROM dual UNION ALL
    SELECT '미술관' FROM dual UNION ALL
    SELECT '역사유적' FROM dual UNION ALL
    SELECT '한옥' FROM dual UNION ALL
    SELECT '축제' FROM dual UNION ALL
    SELECT '맛집탐방' FROM dual UNION ALL
    SELECT '전통시장' FROM dual UNION ALL
    SELECT '카페투어' FROM dual UNION ALL
    SELECT '쇼핑' FROM dual UNION ALL
    SELECT '야경' FROM dual
) s -- 위 SELECT 구문을 통해 결과셋 생성
ON (t.type = s.type)
WHEN NOT MATCHED THEN
    INSERT (created_at, type)
    VALUES (SYSTIMESTAMP, s.type);

ALTER TABLE theme
    ADD CONSTRAINT uc_theme_type UNIQUE (type);
