/*DROP TABLE IF EXISTS USER;

CREATE TABLE USER (
                      USER_ID LONG AUTO_INCREMENT  PRIMARY KEY,
                      FIRST_NAME VARCHAR(250) NOT NULL,
                      MIDDLE_NAME VARCHAR(250),
                      LAST_NAME VARCHAR(250) NOT NULL,
                      NICK_NAME VARCHAR(250),
                      BIO VARCHAR(2000),
                      EMAIL VARCHAR(250) NOT NULL,
                      DATE_OF_BIRTH DATE NOT NULL,
                      PHONE_NUMBER VARCHAR(25) ,
                      HOME_ADDRESS VARCHAR(250) NOT NULL,
                      PASSWORD VARCHAR(250),
                      REGISTRATION_DATE DATE.
                      ROLE VARCHAR(15) NOT NULL DEFAULT 'user'
);

INSERT INTO USER (FIRST_NAME, MIDDLE_NAME, LAST_NAME, NICK_NAME, BIO, EMAIL, DATE_OF_BIRTH, PHONE_NUMBER, HOME_ADDRESS, PASSWORD, REGISTRATION_DATE) VALUES
('John','Doe','Smith','Mr. Test', 'Definitely a real person','test@test.com', sysdate, '0800 838383', 'UC Computer Labs', 'alsdjflaskdf', TO_DATE('16/01/2021', 'DD/MM/YYYY'));*/
