ALTER TABLE tour_image
    MODIFY (attraction_id NULL);

ALTER TABLE restaurant_image
    MODIFY (menu_id NULL);

ALTER TABLE accommodation_image
    MODIFY (room_id NULL);
