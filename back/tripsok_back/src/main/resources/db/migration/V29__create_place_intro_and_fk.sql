-- Create PLACE_INTRO table
CREATE TABLE place_intro (
    id           NUMBER(10)     NOT NULL,
    open_date    VARCHAR2(255)  NULL,
    rest_date    VARCHAR2(255)  NULL,
    use_time     VARCHAR2(255)  NULL,
    raw_json     CLOB           NULL,
    created_at   TIMESTAMP      NOT NULL,
    updated_at   TIMESTAMP      NOT NULL
);

ALTER TABLE place_intro
    ADD CONSTRAINT PK_PLACE_INTRO PRIMARY KEY (id);

ALTER TABLE place_intro
    ADD CONSTRAINT FK_PLACE_INTRO_PLACE
        FOREIGN KEY (id)
            REFERENCES place (id);
