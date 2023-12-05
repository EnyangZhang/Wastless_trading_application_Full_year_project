DELETE FROM ADDRESS;

INSERT INTO ADDRESS (ADDRESS_ID, CITY, COUNTRY, POSTCODE, REGION, STREET_NAME, STREET_NUMBER, SUBURB)
VALUES  (1, 'Metairie', 'United States of America', '70001', 'Louisiana', 'Paul Wayne Haggerty Road', '2176', 'Ilam'),
        (2, 'Memphis', 'United States of America', '38117', 'Tennessee', 'Burton Avenue', '3231', 'Ilam'),
        (3, 'Metairie', 'United States of America', '70001', 'Louisiana', 'Paul Wayne Haggerty Road', '2176', 'Ilam'),
        (4, 'Metairie', 'United States of America', '70001', 'Louisiana', 'Paul Wayne Haggerty Road', '2176', 'Ilam'),
        (5, 'Petrolina', 'Brazil', '56310-320', 'Pernambuco', 'Rua Planalto', '560', 'Ilam'),
        (6, 'Pine Bluff', 'United States of America', '71601', 'Arkansas', 'Cedar Street', '1401', 'Ilam'),
        (7, 'Wittenoom', 'Australia', '6751', 'Western Australia', 'Darwinia Loop', '38', 'Ilam'),
        (11, 'CHRISTCHURCH', 'NZ', '8011', 'CANTABURY', 'ENGINEER STREET', '10', 'Ilam'),
        (12, 'CHRISTCHURCH', 'NZ', '8011', 'CANTABURY', 'ENGINEER STREET', '10', 'Ilam');

INSERT INTO USER (USER_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, NICKNAME, BIO, EMAIL, DATE_OF_BIRTH, PHONE_NUMBER, ADDRESS_ID,
                  PASSWORD, ROLE, CREATED)
VALUES (1, 'David', 'adama', 'Enyang', 'DAE', 'Definitely a real person', 'test@test.com', '2001-01-01', '0800 838383',
        1, 'alsdjflaskdf', 'user', sysdate()),
       (2, 'David', 'adama', 'Enyang', 'DAE', 'Definitely a real person', 'david@test.com', '2001-01-01', '0800 838383',
        2, 'alsdjflaskdf', 'user', sysdate()),
       (3, 'David', 'adama', 'Enyang', 'DAE', 'Definitely a real person', 'test1@test.com',
        sysdate() - interval 15 year, '0800 838383', 3, 'alsdjflaskdf', 'global_admin', sysdate()),
       (4, 'David', 'adama', 'Enyang', 'DAE', 'Definitely a real person', 'admin@defaultglobal', '2001-01-01',
        '0800 838383', 4, 'alsdjflaskdf', 'default_global_admin', sysdate()),
       (5, 'Heron', 'Ashley', 'Waller', 'Wally', '', 'heronwaller@test.com', '1997-02-12', '0800 123456', 5,
        'alsdjflaskdf', 'user', sysdate()),
       (6, 'Michelle', 'George', 'Jarvis', '', '', 'MichelleGJarvis@dayrep.com', sysdate() - interval 16 year, '', 6,
        'agh1AeMae', 'user', sysdate()),
       (7, 'Michael', 'Clover', 'Black', '', '', 'michaelcblack@rhyta.com', sysdate() - interval 13 year, '', 7,
        'Kua6aicu2boh', 'user', sysdate());

INSERT INTO BUSINESS(BUSINESS_ID, BUSINESS_TYPE, DESCRIPTION, NAME, PRIMARY_ADMIN_ID, REGISTRATION_DATE, ADDRESS_ID)
VALUES (1, 'Accommodation and Food Services', 'UNIVERSITY', 'UC', 1, '2020-01-01', 11),
       (2, 'Accommodation and Food Services', 'Fast Food', 'McRonalds', 7, '1995-5-20', 12),
       (3, 'Accommodation and Food Services', 'Fast Food', 'Mc aaa Ronalds', 7, '1995-5-20', 12),
       (4, 'Accommodation and Food Services', 'Fast Food', 'Mc Ronalds', 7, '1995-5-20', 12),
       (5, 'Accommodation and Food Services', 'Fast Food', 'Mc Donalds', 7, '1995-5-20', 12);


INSERT INTO ADMINS(USER_ID, BUSINESS_ID)
VALUES (1, 1),
       (1, 2),
       (5, 2);

INSERT INTO PRODUCT (ROW_ID, PRODUCT_ID, CREATED, DESCRIPTION, MANUFACTURER, NAME, RECOMMENDED_RETAIL_PRICE,
                     BUSINESS_ID)
VALUES (1, 'watt-222-222', '2020-01-01', 'Baked Beans as they should be.', 'this is MANUFACTURER',
        'Watties Baked Beans - 420g can', 2.2, 1),
       (2, 'watt-222-111', '2020-01-01', 'Baked Beans as they should be.', 'this is MANUFACTURER',
        'Watties Baked Beans - 420g can', 2.2, 2);

INSERT INTO PRODUCT_IMAGE (PRODUCT_IMAGE_ID, FILENAME, THUMBNAIL_FILENAME, IS_PRIMARY, ROW_ID)
VALUES (1, '/media/images/23987192387509-123908794328.png', '/media/images/23987192387509-123908794328_thumbnail.png', true,
        1);

INSERT INTO INVENTORY_ITEM (INVENTORY_ITEM_ID, BEST_BEFORE, SELL_BY, EXPIRES, MANUFACTURED, PRICE_PER_ITEM, QUANTITY, TOTAL_PRICE,
                            ROW_ID, PRODUCT_ID, BUSINESS_ID)
VALUES (1, sysdate() + interval 1 year, sysdate() + interval 2 year, sysdate() + interval 3 year,
        sysdate() - interval 1 year, 12.00, 10, 120.00, 1, 'watt-222-222', 1),
       (2, sysdate() + interval 1 year, sysdate() + interval 2 year, sysdate() + interval 3 year,
        sysdate() - interval 1 year, 12.00, 10, 120.00, 1, 'watt-222-111', 2);

INSERT INTO SALE_ITEM (SALE_ITEM_ID, INVENTORY_ITEM_ID, SOLD, QUANTITY, PRICE, MORE_INFO, CLOSES, CREATED)
VALUES (1, 1, FALSE, 5, 4.0, 'Seller may be willing to consider near offers', sysdate(), sysdate()),
       (2, 1, TRUE, 0, 4.0, 'Seller may be willing to consider near offers', sysdate(), sysdate()),
       (3, 1, FALSE, 0, 4.0, 'Seller may be willing to consider near offers', sysdate(), sysdate()),
       (4, 2, FALSE, 0, 4.0, 'Seller may be willing to consider near offers', sysdate(), sysdate());
