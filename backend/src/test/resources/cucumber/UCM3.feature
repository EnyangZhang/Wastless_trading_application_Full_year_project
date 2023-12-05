Feature: UCM3 Marketplace Display
  Background: Register a default user
    Given There are 7 cards in the "ForSale" section of the marketplace
    And There are 1 cards in the "Wanted" section of the marketplace
    And There are 0 cards in the "Exchange" section of the marketplace
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

  Scenario: AC1 - Logged in user can view cards in each section
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" navigates to page 0 of the "ForSale" section of the marketplace
    Then The user only sees cards belonging to the current section

  Scenario: AC1 - The most recently created / renewed items appear first
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" navigates to page 0 of the "ForSale" section of the marketplace
    Then the ordering of the cards is changed to:
      |7|1|3|5|6|2|4|

  Scenario: AC1 - Logged in user can view cards in each section
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" navigates to page 0 of the "Wanted" section of the marketplace
    Then The user only sees cards belonging to the current section

  Scenario: AC1 - Logged in user can view cards in each section
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" navigates to page 0 of the "Exchange" section of the marketplace
    Then The user only sees cards belonging to the current section

  Scenario: AC1 - When not logged in, user cannot view cards in any section
    Given There is no user logged in
    When There is an unauthorized access to the "ForSale" section of the marketplace
    Then An error prevents any data from being returned

  Scenario: AC1 - Logged in user views a limited number of cards through pagination
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    And the number of cards per page is set to 5
    When the user "jdo@gmail.com" navigates to page 0 of the "ForSale" section of the marketplace
    Then the user only sees the 5 cards in the given section

  Scenario: AC1 - Logged in users can page through to view different pages of cards
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    And the number of cards per page is set to 5
    When the user "jdo@gmail.com" navigates to page 1 of the "ForSale" section of the marketplace
    Then the user only sees the 2 cards in the given section

  Scenario: AC2 - Logged in users can view the title of each card
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" navigates to page 0 of the "Wanted" section of the marketplace
    Then the user can view the non-empty titles of each card in the section

  Scenario: AC2 - Logged in users can view the title of each card
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" navigates to page 0 of the "ForSale" section of the marketplace
    Then the user can view the non-empty titles of each card in the section

  Scenario: AC2 - Other information such as description, creator, location are also available
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" navigates to page 0 of the "ForSale" section of the marketplace
    Then the description is available on the displayed cards
    And the creator is available on the displayed cards
    And the creator's location is available on the displayed cards

  Scenario: AC 3 - The display order can be changed (title ascending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" changes the ordering of "ForSale" section to "title", "asc"
    Then the ordering of the cards is changed to:
      |1|2|3|4|5|6|7|

  Scenario: AC 3 - The display order can be changed (title descending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" changes the ordering of "ForSale" section to "title", "desc"
    Then the ordering of the cards is changed to:
      |7|6|5|4|3|2|1|

  Scenario: AC 3 - The display order can be changed (date ascending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" changes the ordering of "ForSale" section to "created", "asc"
    Then the ordering of the cards is changed to:
      |4|2|6|5|3|1|7|

  Scenario: AC 3 - The display order can be changed (date descending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" changes the ordering of "ForSale" section to "created", "desc"
    Then the ordering of the cards is changed to:
      |7|1|3|5|6|2|4|

  Scenario: AC 3 - The display order can be changed (suburb ascending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" changes the ordering of "ForSale" section to "creator.homeAddress.suburb", "asc"
    Then the ordering of the cards is changed to:
      |6|2|5|7|3|1|4|

  Scenario: AC 3 - The display order can be changed (suburb descending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" changes the ordering of "ForSale" section to "creator.homeAddress.suburb", "desc"
    Then the ordering of the cards is changed to:
      |4|1|3|7|5|2|6|

  Scenario: AC 3 - The display order can be changed (city ascending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" changes the ordering of "ForSale" section to "creator.homeAddress.city", "asc"
    Then the ordering of the cards is changed to:
      |6|2|5|7|3|1|4|

  Scenario: AC 3 - The display order can be changed (city descending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When the user "jdo@gmail.com" changes the ordering of "ForSale" section to "creator.homeAddress.city", "desc"
    Then the ordering of the cards is changed to:
      |4|1|3|7|5|2|6|