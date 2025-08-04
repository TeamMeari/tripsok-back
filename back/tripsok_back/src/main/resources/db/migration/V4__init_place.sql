CREATE TABLE restaurant_review (
                                     restaurant_review_id	NUMBER	NOT NULL,
                                     restaurant_id	NUMBER	NOT NULL,
                                     user_id	VARCHAR2(255)	NOT NULL,
                                     restaurant_review	VARCHAR2(255)	NULL,
                                     created_at	TIMESTAMP	NULL,
                                     updated_at	TIMESTAMP	NULL
);

CREATE TABLE tour_image (
                              tour_image_id	NUMBER	NOT NULL,
                              tour_id	NUMBER	NOT NULL,
                              attraction_id	NUMBER	NOT NULL,
                              created_at	TIMESTAMP	NULL
);


CREATE TABLE tour (
                        tour_id	NUMBER	NOT NULL,
                        tour_type	VARCHAR2(255)	NULL,
                        created_at	TIMESTAMP	NULL
);

CREATE TABLE place (
                         place_id	Number	NOT NULL,
                         content_id NUMBER NOT NULL,
                         place_name	VARCHAR2(255)	NULL,
                         address	VARCHAR2(255)	NULL,
                         contact	VARCHAR2(255)	NULL,
                         email	VARCHAR2(255)	NULL,
                         information	CLOB	NULL,
                         "view"	VARCHAR2(255)	NULL,
                         "like"	VARCHAR2(255)	NULL,
                         tour_id	NUMBER	NOT NULL,
                         restaurant_id	NUMBER	NOT NULL,
                         accomodation_id	NUMBER	NOT NULL,
                         created_at	TIMESTAMP	NULL
);

CREATE TABLE attraction (
                              attraction_id	NUMBER	NOT NULL,
                              tour_id	NUMBER	NOT NULL,
                              attraction_name	VARCHAR2(255)	NULL,
                              attraction_price	VARCHAR2(255)	NULL,
                              attraction_information	VARCHAR2(255)	NULL,
                              created_at	TIMESTAMP	NULL
);

CREATE TABLE room (
                        room_id	NUMBER	NOT NULL,
                        accomodation_id	NUMBER	NOT NULL,
                        room_name	VARCHAR2(255)	NULL,
                        season_type	VARCHAR2(255)	NULL,
                        room_price	VARCHAR2(255)	NULL,
                        room_information	VARCHAR2(255)	NULL,
                        created_at	TIMESTAMP	NULL
);

CREATE TABLE restaurant_image (
                                    restaurant_image_id	NUMBER	NOT NULL,
                                    restaurant_id	NUMBER	NOT NULL,
                                    menu_id	NUMBER	NOT NULL,
                                    created_at	TIMESTAMP	NULL
);


CREATE TABLE accomodation_image (
                                      accomodation_image_id	NUMBER	NOT NULL,
                                      accomodation_id	NUMBER	NOT NULL,
                                      room_id	NUMBER	NOT NULL,
                                      created_at	TIMESTAMP	NULL
);

CREATE TABLE restaurant (
                              restaurant_id	NUMBER	NOT NULL,
                              restaurant_type	VARCHAR2(255)	NULL
);

CREATE TABLE accomodation_review (
                                       accomodation_review_id	NUMBER	NOT NULL,
                                       accomodation_id	NUMBER	NOT NULL,
                                       room_id	NUMBER	NOT NULL,
                                       user_id	VARCHAR2(255)	NOT NULL,
                                       accomodation_review	VARCHAR2(255)	NULL,
                                       created_at	TIMESTAMP	NULL,
                                       updated_at	TIMESTAMP	NULL
);

CREATE TABLE menu (
                        menu_id	NUMBER	NOT NULL,
                        restaurant_id	NUMBER	NOT NULL,
                        menu_name	VARCHAR2(255)	NULL,
                        menu_price	VARCHAR2(255)	NULL,
                        menu_information	VARCHAR2(255)	NULL,
                        created_at	TIMESTAMP	NULL
);


CREATE TABLE accomodation (
                                accomodation_id	NUMBER	NOT NULL,
                                accomodation_type	VARCHAR2(255)	NULL,
                                created_at	TIMESTAMP	NULL
);

CREATE TABLE tour_review (
                               tour_review_id	NUMBER	NOT NULL,
                               tour_id	NUMBER	NOT NULL,
                               user_id	VARCHAR2(255)	NOT NULL,
                               tour_review	VARCHAR2(255)	NULL,
                               created_at	TIMESTAMP	NULL,
                               updated_at	TIMESTAMP	NULL
);

ALTER TABLE restaurant_review ADD CONSTRAINT  PK_RESTAURANT_REVIEW PRIMARY KEY (
                                                                                   restaurant_review_id
    );


ALTER TABLE tour_image ADD CONSTRAINT  PK_TOUR_IMAGE PRIMARY KEY (
                                                                     tour_image_id
    );


ALTER TABLE tour ADD CONSTRAINT  PK_TOUR PRIMARY KEY (
                                                         tour_id
    );

ALTER TABLE place ADD CONSTRAINT  PK_PLACE PRIMARY KEY (
                                                           place_id
    );

ALTER TABLE attraction ADD CONSTRAINT  PK_ATTRACTION PRIMARY KEY (
                                                                     attraction_id
    );

ALTER TABLE room ADD CONSTRAINT  PK_ROOM PRIMARY KEY (
                                                         room_id
    );

ALTER TABLE restaurant_image ADD CONSTRAINT  PK_RESTAURANT_IMAGE PRIMARY KEY (
                                                                                 restaurant_image_id
    );




ALTER TABLE accomodation_image ADD CONSTRAINT  PK_ACCOMODATION_IMAGE PRIMARY KEY (
                                                                                     accomodation_image_id
    );

ALTER TABLE restaurant ADD CONSTRAINT  PK_RESTAURANT PRIMARY KEY (
                                                                     restaurant_id
    );

ALTER TABLE accomodation_review ADD CONSTRAINT  PK_ACCOMODATION_REVIEW PRIMARY KEY (
                                                                                       accomodation_review_id
    );

ALTER TABLE menu ADD CONSTRAINT  PK_MENU PRIMARY KEY (
                                                         menu_id
    );


ALTER TABLE accomodation ADD CONSTRAINT  PK_ACCOMODATION PRIMARY KEY (
                                                                         accomodation_id
    );


ALTER TABLE tour_review ADD CONSTRAINT  PK_TOUR_REVIEW PRIMARY KEY (
                                                                       tour_review_id
    );
