@Skip
Feature: UCM5 Find My Cards
  Background: Register a default user
    Given There are 7 cards in the "ForSale" section of the marketplace
    And There are 1 cards in the "Wanted" section of the marketplace
    And There are 0 cards in the "Exchange" section of the marketplace
    And 2 of the cards are inactive
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

  Scenario: AC1 - A logged in user can view their active cards on their home page
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user tries to view their active cards on their home page
    Then The user can view {int} active cards

  Scenario: AC1 - A not logged in user cannot view their active cards on their home page
    Given A user is not logged in
    When The user tries to view their active cards on their home page
    Then The server responds with a 401 unauthorized response

  Scenario: AC2 - A logged in user can view the active cards of another user on the other users profile page
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user tries to view the active cards of another user with id 3 on the other users profile page
    Then The user can view {int} active cards

  Scenario: AC2 - A not logged in user cannot view their active cards on the other users profile page
    Given A user is not logged in
    When The user tries to view the active cards of another user on the other users profile page
    Then The server responds with a 401 unauthorized response

  Scenario: AC2 - A logged in user cannot view the active cards of a user who does not exist
    Given A user is not logged in
    When The user tries to view the active cards of another user with id 18 on the other users profile page
    Then The server responds with a 406 response

