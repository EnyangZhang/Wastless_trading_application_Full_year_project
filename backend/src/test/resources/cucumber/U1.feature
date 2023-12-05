Feature: U1 User accounts

  Background: Register a default user
      Given A user with the following details has been registered:
          |firstName    |John                    |
          |middleName   |Smith                   |
          |lastName     |Johnson                 |
          |nickname     |Johnny                  |
          |bio          |Definitely a real person|
          |email        |jdo@gmail.com           |
          |DoB          |1990-12-01              |
          |phone        |0800 123123             |
          |password     |passwordabc123          |
          |streetNumber |1716                    |
          |streetName   |Timer Ridge Road        |
          |suburb       |Ilam                    |
          |city         |Roseville               |
          |postcode     |9567                    |
          |region       |California              |
          |country      |United States of America|

  Scenario: Creating a user profile - all fields valid should succeed
    Given No user exists with email "test_abc@test.com"
    When A user with the following details is registered:
      |firstName    |Test                    |
      |middleName   |Test                    |
      |lastName     |Test                    |
      |nickname     |Tester                  |
      |bio          |Definitely a real person|
      |email        |test@test.com           |
      |DoB          |1990-12-01              |
      |phone        |0800 123123             |
      |password     |passwordabc123          |
      |streetNumber |1716                    |
      |streetName   |Timer Ridge Road        |
      |suburb       |Ilam                    |
      |city         |Roseville               |
      |postcode     |9567                    |
      |region       |California              |
      |country      |United States of America|
    Then The user will be registered with the email: "test@test.com"

  Scenario: Creating a user profile - duplicate email returns an error
    When A user with the following details is registered:
      |firstName    |John                    |
      |middleName   |Smith                   |
      |lastName     |Johnson                 |
      |nickname     |Johnny                  |
      |bio          |Definitely a real person|
      |email        |jdo@gmail.com           |
      |DoB          |1990-12-01              |
      |phone        |0800 123123             |
      |password     |passwordabc123          |
      |streetNumber |1716                    |
      |streetName   |Timer Ridge Road        |
      |suburb       |Ilam                    |
      |city         |Roseville               |
      |postcode     |9567                    |
      |region       |California              |
      |country      |United States of America|
    Then The user will receive an error message of "E-mail is already taken"

  Scenario: Creating a user profile - invalid details returns an error
    When A user with the following details is registered:
      |firstName    |John                    |
      |middleName   |Smith                   |
      |lastName     |Johnson                 |
      |nickname     |Johnny                  |
      |bio          |Definitely a real person|
      |email        |noatsymbol              |
      |DoB          |1990-12-01              |
      |phone        |0800 123123             |
      |password     |passwordabc123          |
      |streetNumber |1716                    |
      |streetName   |Timer Ridge Road        |
      |suburb       |Ilam                    |
      |city         |Roseville               |
      |postcode     |9567                    |
      |region       |California              |
      |country      |United States of America|
    Then The user will receive a "bad request" error

  Scenario: Logging in - valid credentials should successfully login
    Given The user with email "jdo@gmail.com" exists
    When The user logs in with credentials "jdo@gmail.com" and password "passwordabc123"
    Then The user will be logged in

  Scenario: Logging in - non-existent user will return an error
    Given No user exists with email "random@gmail.com"
    When The user logs in with credentials "random@gmail.com" and password "testPassword"
    Then The user will receive a "unauthorized" error

  Scenario: Logging in - incorrect password will return error
    Given The user with email "jdo@gmail.com" exists
    When The user logs in with credentials "jdo@gmail.com" and password "wrongPassword"
    Then The user will receive a "unauthorized" error