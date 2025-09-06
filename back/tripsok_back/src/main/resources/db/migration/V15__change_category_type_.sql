------------------------------------------------------------
-- 1) 신규 FK 컬럼 추가 (이미 있으면 무시)
------------------------------------------------------------
BEGIN
BEGIN
EXECUTE IMMEDIATE 'ALTER TABLE ACCOMMODATION ADD (PLACE_LCLS_CATEGORY_ID NUMBER(10))';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE != -1430 THEN RAISE; END IF; -- ORA-01430: column already exists
END;

BEGIN
EXECUTE IMMEDIATE 'ALTER TABLE TOUR ADD (PLACE_LCLS_CATEGORY_ID NUMBER(10))';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE != -1430 THEN RAISE; END IF;
END;

BEGIN
EXECUTE IMMEDIATE 'ALTER TABLE RESTAURANT ADD (PLACE_LCLS_CATEGORY_ID NUMBER(10))';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE != -1430 THEN RAISE; END IF;
END;
END;
/
------------------------------------------------------------
-- 2) 3Depth 이름 인덱스 (이미 있으면 무시)
------------------------------------------------------------
BEGIN
BEGIN
EXECUTE IMMEDIATE 'CREATE INDEX IDX_PLC_L3NAME ON PLACE_LCLS_CATEGORY (LCLS_SYSTM3_NAME)';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE != -955 THEN RAISE; END IF; -- ORA-00955: name is already used
END;
END;
/
------------------------------------------------------------
-- 3) 데이터 이관 (*_TYPE -> PLACE_LCLS_CATEGORY_ID)
------------------------------------------------------------
MERGE INTO ACCOMMODATION a
USING (
  SELECT a.ID AID, c.ID CID
  FROM ACCOMMODATION a
  JOIN PLACE_LCLS_CATEGORY c
    ON UPPER(TRIM(c.LCLS_SYSTM3_NAME)) = UPPER(TRIM(a.ACCOMMODATION_TYPE))
  WHERE a.ACCOMMODATION_TYPE IS NOT NULL
) m
ON (a.ID = m.AID)
WHEN MATCHED THEN UPDATE SET a.PLACE_LCLS_CATEGORY_ID = m.CID;

MERGE INTO TOUR t
USING (
  SELECT t.ID TID, c.ID CID
  FROM TOUR t
  JOIN PLACE_LCLS_CATEGORY c
    ON UPPER(TRIM(c.LCLS_SYSTM3_NAME)) = UPPER(TRIM(t.TOUR_TYPE))
  WHERE t.TOUR_TYPE IS NOT NULL
) m
ON (t.ID = m.TID)
WHEN MATCHED THEN UPDATE SET t.PLACE_LCLS_CATEGORY_ID = m.CID;

MERGE INTO RESTAURANT r
USING (
  SELECT r.ID RID, c.ID CID
  FROM RESTAURANT r
  JOIN PLACE_LCLS_CATEGORY c
    ON UPPER(TRIM(c.LCLS_SYSTM3_NAME)) = UPPER(TRIM(r.RESTAURANT_TYPE))
  WHERE r.RESTAURANT_TYPE IS NOT NULL
) m
ON (r.ID = m.RID)
WHEN MATCHED THEN UPDATE SET r.PLACE_LCLS_CATEGORY_ID = m.CID;

------------------------------------------------------------
-- 4) 이관 검증: 매칭 실패 남아있으면 실패
------------------------------------------------------------
DECLARE
  v_acc NUMBER; v_tour NUMBER; v_res NUMBER;
BEGIN
SELECT COUNT(*) INTO v_acc FROM ACCOMMODATION WHERE ACCOMMODATION_TYPE IS NOT NULL AND PLACE_LCLS_CATEGORY_ID IS NULL;
SELECT COUNT(*) INTO v_tour FROM TOUR          WHERE TOUR_TYPE          IS NOT NULL AND PLACE_LCLS_CATEGORY_ID IS NULL;
SELECT COUNT(*) INTO v_res FROM RESTAURANT     WHERE RESTAURANT_TYPE    IS NOT NULL AND PLACE_LCLS_CATEGORY_ID IS NULL;

IF (v_acc + v_tour + v_res) > 0 THEN
    RAISE_APPLICATION_ERROR(-20001,
      'Category FK migration mismatch remains: ACC='||v_acc||', TOUR='||v_tour||', RES='||v_res);
END IF;
END;
/
------------------------------------------------------------
-- 5) NOT NULL 강제 (이미 NOT NULL이면 -1442 무시)
------------------------------------------------------------
DECLARE
  v_acc NUMBER; v_tour NUMBER; v_res NUMBER;
BEGIN
SELECT COUNT(*) INTO v_acc FROM ACCOMMODATION WHERE PLACE_LCLS_CATEGORY_ID IS NULL;
SELECT COUNT(*) INTO v_tour FROM TOUR          WHERE PLACE_LCLS_CATEGORY_ID IS NULL;
SELECT COUNT(*) INTO v_res FROM RESTAURANT     WHERE PLACE_LCLS_CATEGORY_ID IS NULL;

IF v_acc > 0 OR v_tour > 0 OR v_res > 0 THEN
    RAISE_APPLICATION_ERROR(-20002,
      'NULL FK exists: ACC='||v_acc||', TOUR='||v_tour||', RES='||v_res);
END IF;

BEGIN
EXECUTE IMMEDIATE 'ALTER TABLE ACCOMMODATION MODIFY PLACE_LCLS_CATEGORY_ID NOT NULL';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE != -1442 THEN RAISE; END IF; -- ORA-01442 already NOT NULL
END;

BEGIN
EXECUTE IMMEDIATE 'ALTER TABLE TOUR MODIFY PLACE_LCLS_CATEGORY_ID NOT NULL';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE != -1442 THEN RAISE; END IF;
END;

BEGIN
EXECUTE IMMEDIATE 'ALTER TABLE RESTAURANT MODIFY PLACE_LCLS_CATEGORY_ID NOT NULL';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE != -1442 THEN RAISE; END IF;
END;
END;
/
------------------------------------------------------------
-- 6) PK/UNIQUE 검사 (FK 추가 "바로 앞")  -- 스키마 자동 탐지
------------------------------------------------------------
DECLARE
  v_owner VARCHAR2(128);
v_nulls NUMBER := 0;
v_total NUMBER := 0;
v_dist  NUMBER := 0;
v_dup   NUMBER := 0;
v_cnt   NUMBER := 0;   -- enabled PK or UNIQUE on (ID)
BEGIN
-- 6-0) 이 테이블의 실제 소유자(OWNER) 탐지 (내 스키마 우선)
SELECT owner
INTO v_owner
FROM (
         SELECT owner
         FROM   all_tab_columns
         WHERE  table_name = 'PLACE_LCLS_CATEGORY'
           AND column_name = 'ID'
         ORDER BY CASE WHEN owner = USER THEN 0 ELSE 1 END
     )
WHERE ROWNUM = 1;

-- 6-1) NULL 존재 여부
EXECUTE IMMEDIATE
    'SELECT COUNT(*) FROM '||v_owner||'.PLACE_LCLS_CATEGORY WHERE ID IS NULL'
    INTO v_nulls;
IF v_nulls > 0 THEN
    RAISE_APPLICATION_ERROR(-20011,
      'PLACE_LCLS_CATEGORY.ID contains NULLs ('||v_nulls||') in '||v_owner);
END IF;

  -- 6-2) 중복 존재 여부 (UNIQUE/PK 생성 가능성 확인용)
EXECUTE IMMEDIATE
    'SELECT COUNT(*) FROM '||v_owner||'.PLACE_LCLS_CATEGORY'
    INTO v_total;
EXECUTE IMMEDIATE
    'SELECT COUNT(DISTINCT ID) FROM '||v_owner||'.PLACE_LCLS_CATEGORY'
    INTO v_dist;
v_dup := v_total - v_dist;
IF v_dup > 0 THEN
    RAISE_APPLICATION_ERROR(-20012,
      'PLACE_LCLS_CATEGORY.ID has duplicates ('||v_dup||') in '||v_owner);
END IF;

  -- 6-3) ENABLED 상태의 PK/UNIQUE 제약이 (ID)에 있는지 검사
SELECT COUNT(*)
INTO v_cnt
FROM all_constraints c
         JOIN all_cons_columns cc
              ON c.owner = cc.owner
                  AND c.constraint_name = cc.constraint_name
WHERE c.owner = v_owner
  AND c.table_name = 'PLACE_LCLS_CATEGORY'
  AND cc.column_name = 'ID'
  AND c.constraint_type IN ('P','U')
  AND c.status = 'ENABLED';

IF v_cnt = 0 THEN
    -- 다른 스키마면 보통 제약 추가 권한이 없음 → 명시적으로 실패
    RAISE_APPLICATION_ERROR(-20013,
      'No ENABLED PK/UNIQUE on '||v_owner||'.PLACE_LCLS_CATEGORY(ID). Ask DBA of '||v_owner||' to add PK/UNIQUE.');
END IF;

  -- OWNER를 FK 블록에서 쓸 수 있게 출력 (디버깅용; 제거 가능)
DBMS_OUTPUT.PUT_LINE('Resolved owner for PLACE_LCLS_CATEGORY = '||v_owner);
END;
/

------------------------------------------------------------
-- 7) FK 제약 (이미 있으면 무시)  -- 스키마 자동 탐지 사용
------------------------------------------------------------
DECLARE
  v_owner VARCHAR2(128);
BEGIN
-- FK 대상 테이블 OWNER 재탐지 (내 스키마 우선)
SELECT owner
INTO v_owner
FROM (
         SELECT owner
         FROM   all_tab_columns
         WHERE  table_name = 'PLACE_LCLS_CATEGORY'
           AND column_name = 'ID'
         ORDER BY CASE WHEN owner = USER THEN 0 ELSE 1 END
     )
WHERE ROWNUM = 1;

BEGIN
EXECUTE IMMEDIATE
    'ALTER TABLE ACCOMMODATION '||
      'ADD CONSTRAINT FK_ACCOMMODATION_CATEGORY '||
      'FOREIGN KEY (PLACE_LCLS_CATEGORY_ID) '||
      'REFERENCES '||v_owner||'.PLACE_LCLS_CATEGORY (ID)';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE NOT IN (-2260, -02260) THEN RAISE; END IF; -- already exists
END;

BEGIN
EXECUTE IMMEDIATE
    'ALTER TABLE TOUR '||
      'ADD CONSTRAINT FK_TOUR_CATEGORY '||
      'FOREIGN KEY (PLACE_LCLS_CATEGORY_ID) '||
      'REFERENCES '||v_owner||'.PLACE_LCLS_CATEGORY (ID)';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE NOT IN (-2260, -02260) THEN RAISE; END IF;
END;

BEGIN
EXECUTE IMMEDIATE
    'ALTER TABLE RESTAURANT '||
      'ADD CONSTRAINT FK_RESTAURANT_CATEGORY '||
      'FOREIGN KEY (PLACE_LCLS_CATEGORY_ID) '||
      'REFERENCES '||v_owner||'.PLACE_LCLS_CATEGORY (ID)';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE NOT IN (-2260, -02260) THEN RAISE; END IF;
END;
END;
/

------------------------------------------------------------
-- 8) FK 컬럼 인덱스 (이미 있으면 무시)
------------------------------------------------------------
BEGIN
BEGIN
EXECUTE IMMEDIATE 'CREATE INDEX IDX_ACC_CATEGORY_ID ON ACCOMMODATION (PLACE_LCLS_CATEGORY_ID)';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE != -955 THEN RAISE; END IF;
END;

BEGIN
EXECUTE IMMEDIATE 'CREATE INDEX IDX_TOUR_CATEGORY_ID ON TOUR (PLACE_LCLS_CATEGORY_ID)';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE != -955 THEN RAISE; END IF;
END;

BEGIN
EXECUTE IMMEDIATE 'CREATE INDEX IDX_RES_CATEGORY_ID ON RESTAURANT (PLACE_LCLS_CATEGORY_ID)';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE != -955 THEN RAISE; END IF;
END;
END;
/
------------------------------------------------------------
-- 9) 기존 문자열 컬럼 삭제 (없으면 무시)
------------------------------------------------------------
BEGIN
BEGIN
EXECUTE IMMEDIATE 'ALTER TABLE ACCOMMODATION DROP COLUMN ACCOMMODATION_TYPE';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE NOT IN (-904, -1418, -1430) THEN RAISE; END IF; -- absent variants
END;

BEGIN
EXECUTE IMMEDIATE 'ALTER TABLE TOUR DROP COLUMN TOUR_TYPE';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE NOT IN (-904, -1418, -1430) THEN RAISE; END IF;
END;

BEGIN
EXECUTE IMMEDIATE 'ALTER TABLE RESTAURANT DROP COLUMN RESTAURANT_TYPE';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE NOT IN (-904, -1418, -1430) THEN RAISE; END IF;
END;
END;
/
