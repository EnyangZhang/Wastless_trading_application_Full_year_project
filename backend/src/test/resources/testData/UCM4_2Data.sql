INSERT INTO ADDRESS (ADDRESS_ID, CITY, COUNTRY, POSTCODE, REGION, STREET_NAME, STREET_NUMBER, SUBURB)
VALUES  (2, 'Roseville', 'United States of America', '9567', 'California', 'Timber Ridge Road', '1716', 'Ilam');

INSERT INTO USER (USER_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, NICKNAME, BIO, EMAIL, DATE_OF_BIRTH, PHONE_NUMBER, ADDRESS_ID,
                  PASSWORD, ROLE, CREATED)
VALUES (2, 'John', 'Smith', 'Johnson', 'Johnson', 'Definitely a real person', 'test@test.com', '1990-12-01', '0800 123123',
        2, 'passwordabc123', 'user', sysdate());

INSERT INTO MARKETPLACE_CARD(MARKETPLACE_CARD_ID, CREATOR_ID, SECTION, CREATED, DISPLAY_PERIOD_END, TITLE, DESCRIPTION)
VALUES
    (1, 2, 'ForSale', '2019-01-01', sysdate(), 'A', '');