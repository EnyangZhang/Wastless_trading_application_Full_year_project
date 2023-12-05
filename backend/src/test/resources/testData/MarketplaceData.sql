INSERT INTO ADDRESS (ADDRESS_ID, CITY, COUNTRY, POSTCODE, REGION, STREET_NAME, STREET_NUMBER, SUBURB)
VALUES  (2, 'D City', 'Canada', '70001', 'Louisiana', 'Paul Wayne Haggerty Road', '2176', 'A Suburb'),
        (3, 'C City', 'Russia', '38117', 'Tennessee', 'Burton Avenue', '3231', 'B Suburb'),
        (4, 'B City', 'Italy', '70001', 'Louisiana', 'Paul Wayne Haggerty Road', '2176', 'C Suburb'),
        (5, 'E City', 'Malaysia', '70001', 'Louisiana', 'Paul Wayne Haggerty Road', '2176', 'D Suburb'),
        (6, 'A City', 'Brazil', '56310-320', 'Pernambuco', 'Rua Planalto', '560', 'E Suburb'),
        (7, 'F City', 'United States of America', '71601', 'Arkansas', 'Cedar Street', '1401', 'F Suburb'),
        (8, 'Wittenoom', 'Australia', '6751', 'Western Australia', 'Darwinia Loop', '38', 'G Suburb'),
        (9, 'CHRISTCHURCH', 'NZ', '8011', 'CANTABURY', 'ENGINEER STREET', '10', 'H Suburb'),
        (10, 'CHRISTCHURCH', 'NZ', '8011', 'CANTABURY', 'ENGINEER STREET', '10', 'Ilam');

INSERT INTO USER (USER_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, NICKNAME, BIO, EMAIL, DATE_OF_BIRTH, PHONE_NUMBER, ADDRESS_ID,
                  PASSWORD, ROLE, CREATED)
VALUES (1, 'David', 'adama', 'Enyang', 'DAE', 'Definitely a real person', 'admin@defaultglobal', '2001-01-01',
        '0800 838383', 4, 'alsdjflaskdf', 'default_global_admin', sysdate()),
       (2, 'David', 'adama', 'Enyang', 'DAE', 'Definitely a real person', 'test@test.com', '2001-01-01', '0800 838383',
        2, 'password', 'user', sysdate()),
       (3, 'David', 'adama', 'Enyang', 'DAE', 'Definitely a real person', 'david@test.com', '2001-01-01', '0800 838383',
        3, 'alsdjflaskdf', 'user', sysdate()),
       (4, 'David', 'adama', 'Enyang', 'DAE', 'Definitely a real person', 'test1@test.com',
        sysdate() - interval 15 year, '0800 838383', 4, 'alsdjflaskdf', 'global_admin', sysdate()),
       (5, 'David', 'adama', 'Enyang', 'DAE', 'Definitely a real person', 'a@gmail.com', '2001-01-01',
        '0800 838383', 5, 'alsdjflaskdf', 'user', sysdate()),
       (6, 'Heron', 'Ashley', 'Waller', 'Wally', '', 'heronwaller@test.com', '1997-02-12', '0800 123456', 6,
        'alsdjflaskdf', 'user', sysdate()),
       (7, 'Michelle', 'George', 'Jarvis', '', '', 'MichelleGJarvis@dayrep.com', sysdate() - interval 16 year, '', 7,
        'agh1AeMae', 'user', sysdate()),
       (8, 'Michael', 'Clover', 'Black', '', '', 'michaelcblack@rhyta.com', sysdate() - interval 13 year, '', 8,
        'Kua6aicu2boh', 'user', sysdate());

INSERT INTO MARKETPLACE_CARD(MARKETPLACE_CARD_ID, CREATOR_ID, SECTION, CREATED, DISPLAY_PERIOD_END, TITLE, DESCRIPTION,
                             NOTIFIED_EXPIRING)
VALUES
(1, 7, 'ForSale', '2019-01-01', sysdate(), 'A', '', false),
(2, 3, 'ForSale', '2001-01-03', sysdate(), 'B', '', false),
(3, 6, 'ForSale', '2018-05-10', sysdate(), 'C', '', false),
(4, 8, 'ForSale', '2001-01-02', sysdate(), 'D', '', false),
(5, 4, 'ForSale', '2018-03-30', sysdate(), 'E', '', false),
(6, 2, 'ForSale', '2001-02-01', sysdate(), 'F', '', false),
(7, 5, 'ForSale', sysdate(), sysdate(), 'G', '', false);

INSERT INTO MARKETPLACE_CARD(MARKETPLACE_CARD_ID, CREATOR_ID, SECTION, CREATED, DISPLAY_PERIOD_END, TITLE, DESCRIPTION,
                             NOTIFIED_EXPIRING)
VALUES
(40, 2, 'Wanted', sysdate(), sysdate(), 'Card1', '', false);