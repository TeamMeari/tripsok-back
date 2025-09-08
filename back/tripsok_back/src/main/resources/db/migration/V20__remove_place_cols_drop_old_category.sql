

DECLARE
  v_cnt NUMBER := 0;
BEGIN
  SELECT COUNT(*) INTO v_cnt
  FROM ALL_TABLES
  WHERE OWNER = USER AND TABLE_NAME = 'PLACE';
  IF v_cnt = 0 THEN
    RAISE_APPLICATION_ERROR(-20990, 'PLACE table not found. Abort V20.');
  END IF;
END;
/

DECLARE
  PROCEDURE drop_col(p_col IN VARCHAR2) IS
    v_has NUMBER := 0;
  BEGIN
    SELECT COUNT(*) INTO v_has
    FROM USER_TAB_COLUMNS
    WHERE TABLE_NAME = 'PLACE' AND COLUMN_NAME = p_col;

    IF v_has > 0 THEN
      EXECUTE IMMEDIATE 'ALTER TABLE PLACE DROP COLUMN '||p_col;
      DBMS_OUTPUT.PUT_LINE('Dropped PLACE.'||p_col);
    ELSE
      DBMS_OUTPUT.PUT_LINE('PLACE.'||p_col||' not found. Skip.');
    END IF;
  END;
BEGIN
  drop_col('ADDRESS');
  drop_col('INFORMATION');
  drop_col('SUMMARY');
END;
/

-- 3) Drop old backup table PLACE_LCLS_CATEGORY_OLD if exists
DECLARE
  v_cnt NUMBER := 0;
BEGIN
  SELECT COUNT(*) INTO v_cnt
  FROM ALL_TABLES
  WHERE OWNER = USER AND TABLE_NAME = 'PLACE_LCLS_CATEGORY_OLD';

  IF v_cnt > 0 THEN
    EXECUTE IMMEDIATE 'DROP TABLE PLACE_LCLS_CATEGORY_OLD CASCADE CONSTRAINTS';
    DBMS_OUTPUT.PUT_LINE('Dropped table PLACE_LCLS_CATEGORY_OLD');
  ELSE
    DBMS_OUTPUT.PUT_LINE('PLACE_LCLS_CATEGORY_OLD not found. Skip.');
  END IF;
END;
/

-------------------------------------------------------------------------------
-- End of V20
-------------------------------------------------------------------------------

