INSERT INTO ADDRESS (ADDRESS_ID, CITY, COUNTRY, POSTCODE, REGION, STREET_NAME, STREET_NUMBER, SUBURB)
VALUES  (2, 'CHRISTCHURCH', 'Canada', '70001', 'Louisiana', 'Paul Wayne Haggerty Road', '2176', 'A Suburb'),
        (3, 'CHRISTCHURCH', 'Russia', '38117', 'Tennessee', 'Burton Avenue', '3231', 'B Suburb'),
        (4, 'CHRISTCHURCH', 'Italy', '70001', 'Louisiana', 'Paul Wayne Haggerty Road', '2176', 'C Suburb'),
        (5, 'CHRISTCHURCH', 'Malaysia', '70001', 'Canterbury', 'Paul Wayne Haggerty Road', '2176', 'D Suburb');

INSERT INTO USER (USER_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, NICKNAME, BIO, EMAIL, DATE_OF_BIRTH, PHONE_NUMBER, ADDRESS_ID,
                  PASSWORD, ROLE, CREATED)
VALUES (2, 'David', 'adama', 'Traore', 'DAT', 'Definitely a real person', 'test@test.com', '2001-01-01', '0800 838383',
        2, 'alsdjflaskdf', 'user', sysdate());

INSERT INTO BUSINESS(BUSINESS_ID, BUSINESS_TYPE, DESCRIPTION, NAME, PRIMARY_ADMIN_ID,
                         REGISTRATION_DATE, ADDRESS_ID)
VALUES (1, 'Accommodation and Food Services', 'UNIVERSITY', 'UC', 1, '2020-01-01', 3),
    (2, 'Retail Trade', 'Fast Food', 'McRonalds', 2, '1995-5-20', 4),
    (3, 'Charitable Organisation', 'Fast Food', 'Mc aaa Ronalds', 2, '1995-5-20', 5),
    (4, 'Non-Profit Organisation', 'breakfast', 'Sanitiserium', 2, '2020-01-01', 2);

INSERT INTO ADMINS(USER_ID, BUSINESS_ID)
VALUES (2, 1),
       (2, 2),
       (2, 3),
       (2, 4);


INSERT INTO PRODUCT (ROW_ID, PRODUCT_ID, CREATED, DESCRIPTION, MANUFACTURER, NAME, RECOMMENDED_RETAIL_PRICE,
                         BUSINESS_ID)
VALUES (1, 'watt-222-222', '2020-01-01', 'Baked Beans as they should be.', 'this is MANUFACTURER',
    'Watties Baked Beans - 420g can', 2.2, 1), (2, 'matt-222-222', '2020-01-01', 'Maked Means as they should be.',
    'this is MANUFACTURER', 'Matties Maked Means - 420g can', 2.2, 2), (3, 'chip43', '2020-01-01',
    'You know you cant grab them', 'this is MANUFACTURER', 'Ghost Chips 43 ounces', 2.2, 3), (4, 'wetBix', '2020-01-01',
    'Never eat them soggy', 'Sanitiserium', 'Weeto-Bix', 4, 4);

INSERT INTO INVENTORY_ITEM (INVENTORY_ITEM_ID, BEST_BEFORE, SELL_BY, EXPIRES, MANUFACTURED, PRICE_PER_ITEM, QUANTITY, TOTAL_PRICE,
                            ROW_ID, PRODUCT_ID, BUSINESS_ID)
VALUES (1, sysdate() + interval 1 year, sysdate() + interval 2 year, sysdate() + interval 3 year,
        sysdate() - interval 1 year, 12.00, 100, 1200.00, 1, 'watt-222-222', 1), (2, sysdate() + interval 1 year,
        sysdate() + interval 2 year, sysdate() + interval 3 year, sysdate() - interval 1 year, 11.00, 100, 1100.00, 2,
        'matt-222-222', 2), (3, sysdate() + interval 1 year, sysdate() + interval 2 year, sysdate() + interval 3 year,
        sysdate() - interval 1 year, 91.00, 100, 910.00, 3, 'chip43', 3), (4, sysdate() + interval 1 year,
        sysdate() + interval 2 year, sysdate() + interval 3 year, sysdate() - interval 1 year, 12.00, 100, 1200.00, 4,
        'wetBix', 4);

INSERT INTO SALE_ITEM (SALE_ITEM_ID, INVENTORY_ITEM_ID, SOLD, QUANTITY, PRICE, MORE_INFO, CLOSES, CREATED)
VALUES (1, 1, FALSE, 5, 100, 'Seller may be willing to consider near offers', sysdate(), sysdate()),
        (2, 2, FALSE, 5, 20, 'Seller may be willing to consider near offers', sysdate(), sysdate()),
        (3, 3, FALSE, 5, 40, 'Seller may be willing to consider near offers', sysdate(), sysdate()),
        (4, 1, FALSE, 5, 80, 'Seller may be willing to consider near offers', sysdate(), sysdate()),
        (5, 2, FALSE, 5, 60, 'Seller may be willing to consider near offers', sysdate(), sysdate()),
        (6, 3, FALSE, 5, 20, 'Seller may be willing to consider near offers', sysdate(), sysdate()),
        (7, 1, FALSE, 5, 40, 'Seller may be willing to consider near offers', sysdate(), sysdate()),
        (8, 4, FALSE, 10, 10, 'Seller may be willing to consider near offers', sysdate() + interval 1 month, sysdate());