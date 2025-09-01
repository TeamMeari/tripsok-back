CREATE INDEX idx_place_view_desc ON place ("view" DESC);
CREATE INDEX idx_place_like_desc ON place ("like" DESC);

ALTER TABLE restaurant_image
    ADD (url VARCHAR2(2000) NULL);

ALTER TABLE accommodation_image
    ADD (url VARCHAR2(2000) NULL);

ALTER TABLE tour_image
    ADD (url VARCHAR2(2000) NULL);