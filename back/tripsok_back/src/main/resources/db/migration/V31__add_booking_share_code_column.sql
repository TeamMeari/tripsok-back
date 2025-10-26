ALTER TABLE booking
    ADD (SHARE_CODE VARCHAR2(40 CHAR));

ALTER TABLE booking
    ADD CONSTRAINT uk_booking_share_code UNIQUE (share_code);
