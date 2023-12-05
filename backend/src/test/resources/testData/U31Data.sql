--  DELETE FROM ADDRESS;

INSERT INTO ADDRESS (ADDRESS_ID, CITY, COUNTRY, POSTCODE, REGION, STREET_NAME, STREET_NUMBER, SUBURB)
VALUES  (1, 'Metairie', 'United States of America', '70001', 'Louisiana', 'Paul Wayne Haggerty Road', '2176', 'Ilam');


INSERT INTO USER (USER_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, NICKNAME, BIO, EMAIL, DATE_OF_BIRTH, PHONE_NUMBER, ADDRESS_ID,
                  PASSWORD, ROLE, CREATED)
VALUES (1, 'Admin', '', 'Default Global', '', '', 'admin@defaultglobal', '2001-01-01', '',
        1, 'test1234', 'default_global_admin', sysdate()),
       (2, 'David', 'adama', 'Enyang', 'DAE', 'Definitely a real person', 'test@test.com', '1990-03-01', '0800 838383',
        1, 'test1234', 'user', sysdate());

INSERT INTO BUSINESS(BUSINESS_ID, BUSINESS_TYPE, DESCRIPTION, NAME, PRIMARY_ADMIN_ID,
                     REGISTRATION_DATE, ADDRESS_ID)
VALUES  (1, 'Accommodation and Food Services', 'UNIVERSITY', 'UC', 2, '2020-01-01', 1),
        (2, 'Accommodation and Food Services', 'UNIVERSITY', 'UC', 2, '2020-01-01', 1);

INSERT INTO ADMINS(USER_ID, BUSINESS_ID)
VALUES (2, 1);

INSERT INTO PRODUCT (ROW_ID, PRODUCT_ID, CREATED, DESCRIPTION, MANUFACTURER, NAME, RECOMMENDED_RETAIL_PRICE,
                     BUSINESS_ID)
VALUES (1, 'testOne', '2020-01-01', 'Baked Beans as they should be.', 'this is MANUFACTURER',
        'Watties Baked Beans - 420g can', 10.0, 1),
       (2, 'testTwo', '2020-01-01', 'Baked Beans as they should be.', 'this is MANUFACTURER',
        'Watties Baked Beans - 420g can', 10.0, 1);

INSERT INTO PRODUCT_IMAGE (PRODUCT_IMAGE_ID, FILENAME, THUMBNAIL_FILENAME, IS_PRIMARY, ROW_ID)
VALUES (1, '/media/images/23987192387509-123908794328.png', '/media/images/23987192387509-123908794328_thumbnail.png', true,
        1);

INSERT INTO INVENTORY_ITEM (INVENTORY_ITEM_ID, BEST_BEFORE, SELL_BY, EXPIRES, MANUFACTURED, PRICE_PER_ITEM, QUANTITY, TOTAL_PRICE,
                            ROW_ID, PRODUCT_ID, BUSINESS_ID)
VALUES (1, sysdate() + interval 1 year, sysdate() + interval 2 year, sysdate() + interval 3 year,
        sysdate() - interval 1 year, 10.00, 10, 100.00, 1, 'testOne', 1),
       (2, sysdate() + interval 1 year, sysdate() + interval 2 year, sysdate() + interval 3 year,
        sysdate() - interval 1 year, 10.00, 10, 100.00, 1, 'testTwo', 1);

INSERT INTO SALE_ITEM (SALE_ITEM_ID, INVENTORY_ITEM_ID, SOLD, QUANTITY, PRICE, MORE_INFO, CLOSES, CREATED)
VALUES (1, 1, FALSE, 5, 10.0, 'Seller may be willing to consider near offers', sysdate(), sysdate()),
       (2, 2, FALSE , 8, 10.0, 'Seller may be willing to consider near offers', sysdate(), sysdate());
