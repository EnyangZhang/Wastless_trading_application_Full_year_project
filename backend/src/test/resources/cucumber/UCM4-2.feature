Feature: UCM4-2 Card Expiry
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

  Scenario: AC1 - If a user cards been the marketplace for the maximum display period then they are notified
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When A card created by "jdo@gmail.com" has exceeded the maximum display period
    And The user has not yet been notified for that card
    Then The user receives a notification that the card has expired

  Scenario: AC1 - If a user has already been notified for a specific card, they won't be notified multiple times
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When A card created by "jdo@gmail.com" has exceeded the maximum display period
    And The user had been previously notified about that card
    Then The user receives no additional notification that the card has expired

  Scenario: AC1 - A user can extend their expired card
    Given The user is logged in with the email "jdo@gmail.com" and password "passwordabc123"
    And The user has created a marketplace card with ID 2
    When The user has received a notification that the card has expired
    Then The user can extend their expired card

  Scenario: AC1 - When a user extends their expired card then expiry date is extended
    Given The user is logged in with the email "jdo@gmail.com" and password "passwordabc123"
    And The user has created a marketplace card with ID 2
    And The user has received a notification that the card has expired
    When The user chooses to extend the card
    Then The closing date is extended by 2 weeks


  Scenario: AC1 - A user can delete their expired card
    Given The user is logged in with the email "jdo@gmail.com" and password "passwordabc123"
    And The user has created a marketplace card with ID 2
    When The user has received a notification that the card has expired
    Then The user can delete their expired card


  Scenario: AC2 - If a user does not take any action on an expired card within 24 hours then it is deleted
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When A card created by "jdo@gmail.com" has exceeded the maximum display period by more than 24 hours
    Then The card is deleted
    And The user is notified that the card has been deleted
