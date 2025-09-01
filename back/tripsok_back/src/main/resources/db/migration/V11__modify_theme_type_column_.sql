ALTER TABLE theme
    MODIFY (type VARCHAR2(100) NOT NULL);

ALTER TABLE theme
    ADD CONSTRAINT uc_theme_type UNIQUE (type);
