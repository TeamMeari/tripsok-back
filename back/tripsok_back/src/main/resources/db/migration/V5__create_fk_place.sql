ALTER TABLE place
    ADD CONSTRAINT FK_PLACE_TOUR
        FOREIGN KEY (tour_id)
            REFERENCES tour (id);

ALTER TABLE place
    ADD CONSTRAINT FK_PLACE_RESTAURANT
        FOREIGN KEY (restaurant_id)
            REFERENCES restaurant (id);

ALTER TABLE place
    ADD CONSTRAINT FK_PLACE_ACCOMMODATION
        FOREIGN KEY (accommodation_id)
            REFERENCES accommodation (id);

ALTER TABLE restaurant_review
    ADD CONSTRAINT FK_RESTAURANT_REVIEW_RESTAURANT
        FOREIGN KEY (restaurant_id)
            REFERENCES restaurant (id);

ALTER TABLE tour_image
    ADD CONSTRAINT FK_TOUR_IMAGE_TOUR
        FOREIGN KEY (tour_id)
            REFERENCES tour (id);

ALTER TABLE tour_image
    ADD CONSTRAINT FK_TOUR_IMAGE_ATTRACTION
        FOREIGN KEY (attraction_id)
            REFERENCES attraction (id);

ALTER TABLE attraction
    ADD CONSTRAINT FK_ATTRACTION_TOUR
        FOREIGN KEY (tour_id)
            REFERENCES tour (id);

ALTER TABLE room
    ADD CONSTRAINT FK_ROOM_ACCOMMODATION
        FOREIGN KEY (accommodation_id)
            REFERENCES accommodation (id);

ALTER TABLE restaurant_image
    ADD CONSTRAINT FK_RESTAURANT_IMAGE_RESTAURANT
        FOREIGN KEY (restaurant_id)
            REFERENCES restaurant (id);

ALTER TABLE restaurant_image
    ADD CONSTRAINT FK_RESTAURANT_IMAGE_MENU
        FOREIGN KEY (menu_id)
            REFERENCES menu (id);

ALTER TABLE accommodation_image
    ADD CONSTRAINT FK_ACCOMMODATION_IMAGE_ACCOMMODATION
        FOREIGN KEY (accommodation_id)
            REFERENCES accommodation (id);

ALTER TABLE accommodation_image
    ADD CONSTRAINT FK_ACCOMMODATION_IMAGE_ROOM
        FOREIGN KEY (room_id)
            REFERENCES room (id);

ALTER TABLE accommodation_review
    ADD CONSTRAINT FK_ACCOMMODATION_REVIEW_ACCOMMODATION
        FOREIGN KEY (accommodation_id)
            REFERENCES accommodation (id);

ALTER TABLE accommodation_review
    ADD CONSTRAINT FK_ACCOMMODATION_REVIEW_ROOM
        FOREIGN KEY (room_id)
            REFERENCES room (id);

ALTER TABLE menu
    ADD CONSTRAINT FK_MENU_RESTAURANT
        FOREIGN KEY (restaurant_id)
            REFERENCES restaurant (id);

ALTER TABLE tour_review
    ADD CONSTRAINT FK_TOUR_REVIEW_TOUR
        FOREIGN KEY (tour_id)
            REFERENCES tour (id);