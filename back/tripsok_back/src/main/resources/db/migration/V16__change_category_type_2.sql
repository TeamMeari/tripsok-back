-------------------------------------------------------------------------------
-- 목적: PLACE_LCLS_CATEGORY_TR의 PK를 (CATEGORY_ID, LOCALE)로 교체
-- 전제: 마스터 테이블 PLACE_LCLS_CATEGORY 존재, FK(PLLC_TR -> PLC)는 유지
-- 설계: 재실행 안전(idempotent). 중복 (CATEGORY_ID, LOCALE) 존재 시 실패.
-------------------------------------------------------------------------------

-- 0) 필수 테이블 존재 확인
DECLARE
    v_cnt NUMBER := 0;
BEGIN
SELECT COUNT(*)
INTO v_cnt
FROM ALL_TABLES
WHERE OWNER = USER
  AND TABLE_NAME = 'PLACE_LCLS_CATEGORY_TR';
IF v_cnt = 0 THEN
    RAISE_APPLICATION_ERROR(-20960, 'PLACE_LCLS_CATEGORY_TR not found.');
END IF;

SELECT COUNT(*)
INTO v_cnt
FROM ALL_TABLES
WHERE OWNER = USER
  AND TABLE_NAME = 'PLACE_LCLS_CATEGORY';
IF v_cnt = 0 THEN
    RAISE_APPLICATION_ERROR(-20961, 'PLACE_LCLS_CATEGORY not found.');
END IF;
END;
/
-- 1) (CATEGORY_ID, LOCALE) 중복 존재 여부 검사 → 있으면 실패
DECLARE
    v_dups NUMBER := 0;
BEGIN
SELECT COUNT(*)
INTO v_dups
FROM (SELECT CATEGORY_ID, LOCALE
      FROM PLACE_LCLS_CATEGORY_TR
      GROUP BY CATEGORY_ID, LOCALE
      HAVING COUNT(*) > 1);

IF v_dups > 0 THEN
    RAISE_APPLICATION_ERROR(-20962,
      '(CATEGORY_ID, LOCALE) duplicates exist in PLACE_LCLS_CATEGORY_TR = '||v_dups||
      '. Clean data before changing PK.');
END IF;
END;
/
-- 2) 현재 PK가 이미 (CATEGORY_ID, LOCALE)인지 확인
DECLARE
    v_pk_name VARCHAR2(128);
v_pk_cols  VARCHAR2(4000);
v_target   VARCHAR2(4000) := 'CATEGORY_ID,LOCALE';
v_has_pk   NUMBER := 0;
BEGIN
BEGIN
SELECT constraint_name
INTO v_pk_name
FROM USER_CONSTRAINTS
WHERE table_name = 'PLACE_LCLS_CATEGORY_TR'
  AND constraint_type = 'P';
v_has_pk := 1;
EXCEPTION WHEN NO_DATA_FOUND THEN
    v_has_pk := 0;
END;

IF v_has_pk = 1 THEN
SELECT LISTAGG(column_name, ',') WITHIN GROUP (ORDER BY position)
INTO v_pk_cols
FROM USER_CONS_COLUMNS
WHERE table_name = 'PLACE_LCLS_CATEGORY_TR'
  AND constraint_name = v_pk_name;

IF v_pk_cols = v_target THEN
      -- 이미 원하는 PK 구성 → 종료
      DBMS_OUTPUT.PUT_LINE('PK already (CATEGORY_ID, LOCALE). No change.');
RETURN;
END IF;
END IF;
END;
/
-- 3) 기존 PK 드롭 (있으면)
DECLARE
    v_pk_name VARCHAR2(128);
v_has_pk   NUMBER := 0;
BEGIN
BEGIN
SELECT constraint_name
INTO v_pk_name
FROM USER_CONSTRAINTS
WHERE table_name = 'PLACE_LCLS_CATEGORY_TR'
  AND constraint_type = 'P';
v_has_pk := 1;
EXCEPTION WHEN NO_DATA_FOUND THEN
    v_has_pk := 0;
END;

IF v_has_pk = 1 THEN
    -- PK에 종속된 인덱스도 함께 제거
EXECUTE IMMEDIATE 'ALTER TABLE PLACE_LCLS_CATEGORY_TR DROP PRIMARY KEY DROP INDEX';
DBMS_OUTPUT.PUT_LINE('Dropped existing PK '||v_pk_name||' on PLACE_LCLS_CATEGORY_TR');
END IF;
END;
/
-- 4) (CATEGORY_ID, LOCALE)로 PK 생성
BEGIN
-- 이름은 일관성 있게 PK_PLLCTR로 사용. (이름 충돌 시 다른 이름을 쓰려면 바꾸면 됨)
EXECUTE IMMEDIATE 'ALTER TABLE PLACE_LCLS_CATEGORY_TR '||
    'ADD CONSTRAINT PK_PLLCTR PRIMARY KEY (CATEGORY_ID, LOCALE)';
EXCEPTION WHEN OTHERS THEN
  -- ORA-00955: name is already used by an existing constraint(이름 충돌) 등은 무시 가능
  IF SQLCODE NOT IN (-955) THEN RAISE;
END IF;
END;
/
-- 5) 보조 인덱스 보장 (조회 최적화)
BEGIN
-- (LOCALE) 인덱스
BEGIN
EXECUTE IMMEDIATE 'CREATE INDEX IX_PLLCTR_LOCALE ON PLACE_LCLS_CATEGORY_TR (LOCALE)';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE != -955 THEN RAISE;
END IF;
END;

-- (LOCALE, UPPER(TRIM(LCLS_SYSTM3_NAME))) 인덱스
BEGIN
EXECUTE IMMEDIATE 'CREATE INDEX IX_PLLCTR_LOCALE_L3NAME_UPPER '||
      'ON PLACE_LCLS_CATEGORY_TR (LOCALE, UPPER(TRIM(LCLS_SYSTM3_NAME)))';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE != -955 THEN RAISE;
END IF;
END;
END;
/
-- 6) 최종 점검
DECLARE
    v_pk_cols VARCHAR2(4000);
BEGIN
SELECT LISTAGG(column_name, ',') WITHIN GROUP (ORDER BY position)
INTO v_pk_cols
FROM USER_CONS_COLUMNS
WHERE table_name = 'PLACE_LCLS_CATEGORY_TR'
  AND constraint_name = (SELECT constraint_name
                         FROM USER_CONSTRAINTS
                         WHERE table_name = 'PLACE_LCLS_CATEGORY_TR'
                           AND constraint_type = 'P');

IF v_pk_cols <> 'CATEGORY_ID,LOCALE' THEN
    RAISE_APPLICATION_ERROR(-20963,
      'PK is not (CATEGORY_ID, LOCALE). Current='||v_pk_cols);
END IF;

DBMS_OUTPUT.PUT_LINE('PK changed to (CATEGORY_ID, LOCALE) successfully.');
END;
/
