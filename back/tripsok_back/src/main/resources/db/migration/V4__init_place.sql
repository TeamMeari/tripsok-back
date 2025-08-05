CREATE TABLE restaurant_review (
                                     id	NUMBER(10)	NOT NULL,
                                     restaurant_id	NUMBER(10)	NOT NULL,
                                     user_id	VARCHAR2(255)	NOT NULL,
                                     restaurant_review	VARCHAR2(255)	NULL,
                                     created_at	TIMESTAMP	NOT NULL,
                                     updated_at	TIMESTAMP	NOT NULL
);

CREATE TABLE tour_image (
                              id	NUMBER(10)	NOT NULL,
                              tour_id	NUMBER(10)	NOT NULL,
                              attraction_id	NUMBER(10)	NOT NULL,
                              created_at	TIMESTAMP	NOT NULL
);


CREATE TABLE tour (
                        id	NUMBER(10)	NOT NULL,
                        tour_type	VARCHAR2(255)	NULL,
                        created_at	TIMESTAMP	NOT NULL
);

CREATE TABLE place (
                         id	NUMBER(10)	NOT NULL,
                         content_id NUMBER NOT NULL,
                         place_name	VARCHAR2(255)	NULL,
                         address	VARCHAR2(255)	NULL,
                         contact	VARCHAR2(255)	NULL,
                         email	VARCHAR2(255)	NULL,
                         information	CLOB	NULL,
                         "view"	NUMBER(10)	NULL,
                         "like"	NUMBER(10)	NULL,
                         tour_id	NUMBER(10)	NULL,
                         restaurant_id	NUMBER(10)	NULL,
                         accommodation_id	NUMBER(10)	NULL,
                         map_x NUMBER NULL,
                         map_y NUMBER NULL,
                         created_at	TIMESTAMP	NOT NULL,
                         updated_at	TIMESTAMP	NOT NULL
);

CREATE TABLE attraction (
                              id	NUMBER(10)	NOT NULL,
                              tour_id	NUMBER(10)	NOT NULL,
                              attraction_name	VARCHAR2(255)	NULL,
                              attraction_price	VARCHAR2(255)	NULL,
                              attraction_information	VARCHAR2(255)	NULL,
                              created_at	TIMESTAMP	NOT NULL
);

CREATE TABLE room (
                        id	NUMBER(10)	NOT NULL,
                        accommodation_id	NUMBER(10)	NOT NULL,
                        room_name	VARCHAR2(255)	NULL,
                        season_type	VARCHAR2(255)	NULL,
                        room_price	VARCHAR2(255)	NULL,
                        room_information	VARCHAR2(255)	NULL,
                        created_at	TIMESTAMP	NOT NULL
);

CREATE TABLE restaurant_image (
                                    id	NUMBER(10)	NOT NULL,
                                    restaurant_id	NUMBER(10)	NOT NULL,
                                    menu_id	NUMBER(10)	NOT NULL,
                                    created_at	TIMESTAMP	NOT NULL
);


CREATE TABLE accommodation_image (
                                      id	NUMBER(10)	NOT NULL,
                                      accommodation_id	NUMBER(10)	NOT NULL,
                                      room_id	NUMBER(10)	NOT NULL,
                                      created_at	TIMESTAMP	NOT NULL
);

CREATE TABLE restaurant (
                              id	NUMBER(10)	NOT NULL,
                              restaurant_type	VARCHAR2(255)	NULL
);

CREATE TABLE accommodation_review (
                                       id	NUMBER(10)	NOT NULL,
                                       accommodation_id	NUMBER(10)	NOT NULL,
                                       room_id	NUMBER(10)	NOT NULL,
                                       user_id	VARCHAR2(255)	NOT NULL,
                                       accommodation_review	VARCHAR2(255)	NULL,
                                       created_at	TIMESTAMP	NOT NULL,
                                       updated_at	TIMESTAMP	NOT NULL
);

CREATE TABLE menu (
                        id	NUMBER(10)	NOT NULL,
                        restaurant_id	NUMBER(10)	NOT NULL,
                        menu_name	VARCHAR2(255)	NULL,
                        menu_price	VARCHAR2(255)	NULL,
                        menu_information	VARCHAR2(255)	NULL,
                        created_at	TIMESTAMP	NOT NULL
);


CREATE TABLE accommodation (
                                id	NUMBER(10)	NOT NULL,
                                accommodation_type	VARCHAR2(255)	NULL,
                                created_at	TIMESTAMP	NOT NULL
);

CREATE TABLE tour_review (
                               id	NUMBER(10)	NOT NULL,
                               tour_id	NUMBER(10)	NOT NULL,
                               user_id	VARCHAR2(255)	NOT NULL,
                               tour_review	VARCHAR2(255)	NULL,
                               created_at	TIMESTAMP	NOT NULL,
                               updated_at	TIMESTAMP	NOT NULL
);

ALTER TABLE restaurant_review ADD CONSTRAINT  PK_RESTAURANT_REVIEW PRIMARY KEY (
                                                                                id
    );


ALTER TABLE tour_image ADD CONSTRAINT  PK_TOUR_IMAGE PRIMARY KEY (
                                                                  id
    );


ALTER TABLE tour ADD CONSTRAINT  PK_TOUR PRIMARY KEY (
                                                      id
    );

ALTER TABLE place ADD CONSTRAINT  PK_PLACE PRIMARY KEY (
                                                        id
    );

ALTER TABLE attraction ADD CONSTRAINT  PK_ATTRACTION PRIMARY KEY (
                                                                  id
    );

ALTER TABLE room ADD CONSTRAINT  PK_ROOM PRIMARY KEY (
                                                      id
    );

ALTER TABLE restaurant_image ADD CONSTRAINT  PK_RESTAURANT_IMAGE PRIMARY KEY (
                                                                              id
    );




ALTER TABLE accommodation_image ADD CONSTRAINT  PK_accommodation_IMAGE PRIMARY KEY (
                                                                                    id
    );

ALTER TABLE restaurant ADD CONSTRAINT  PK_RESTAURANT PRIMARY KEY (
                                                                  id
    );

ALTER TABLE accommodation_review ADD CONSTRAINT  PK_accommodation_REVIEW PRIMARY KEY (
                                                                                      id
    );

ALTER TABLE menu ADD CONSTRAINT  PK_MENU PRIMARY KEY (
                                                      id
    );


ALTER TABLE accommodation ADD CONSTRAINT  PK_accommodation PRIMARY KEY (
                                                                        id
    );


ALTER TABLE tour_review ADD CONSTRAINT  PK_TOUR_REVIEW PRIMARY KEY (
                                                                    id
    );


