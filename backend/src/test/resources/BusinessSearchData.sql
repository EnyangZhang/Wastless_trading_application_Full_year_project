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

INSERT INTO BUSINESS(BUSINESS_ID, BUSINESS_TYPE, DESCRIPTION, NAME, PRIMARY_ADMIN_ID,
                     REGISTRATION_DATE, ADDRESS_ID)
VALUES (1, 'Accommodation and Food Services', 'UNIVERSITY', 'UC', 1, '2020-01-01', 11),
       (2, 'Accommodation and Food Services', 'Fast Food', 'McRonalds', 7, '1995-5-20', 12),
       (3, 'Accommodation and Food Services', 'Fast Food', 'Mc aaa Ronalds', 7, '1995-5-20', 12),
       (4, 'Accommodation and Food Services', 'Fast Food', 'book', 7, '1995-5-20', 12),
       (5, 'Accommodation and Food Services', 'Fast Food', 'Mc Ronalds', 7, '1995-5-20', 12);