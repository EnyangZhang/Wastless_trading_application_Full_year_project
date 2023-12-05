DELETE FROM ADDRESS;

INSERT INTO ADDRESS (ADDRESS_ID, CITY, COUNTRY, POSTCODE, REGION, STREET_NAME, STREET_NUMBER, SUBURB)
VALUES  (1, 'Metairie', 'United States of America', '70001', 'Louisiana', 'Paul Wayne Haggerty Road', '2176', 'Ilam');


INSERT INTO USER (USER_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, NICKNAME, BIO, EMAIL, DATE_OF_BIRTH, PHONE_NUMBER, ADDRESS_ID,
                  PASSWORD, ROLE, CREATED)
VALUES (1, 'Admin', '', 'Default Global', '', '', 'admin@defaultglobal', '2001-01-01', '',
        1, 'test1234', 'default_global_admin', sysdate()),
       (2, 'David', 'adama', 'Enyang', 'DAE', 'Definitely a real person', 'test@test.com', '1990-03-01', '0800 838383',
        1, 'test1234', 'user', sysdate());

