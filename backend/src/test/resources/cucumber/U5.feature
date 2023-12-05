@Skip
# These scenarios were not fully implemented in the relevant sprint.
Feature: U5 - Creating Business Accounts

  Scenario: Test
    Given A user is not logged in
    Then Controller returns 401 code when I get business account

  Scenario: AC1 - As a logged in user, I can create a business account
    Given No user exists
    When I create a new user
    And I am logged in
    And I create a new business account with name "My Business", description "", address "21" "Madeup Road" "Christchurch" "Ilam" "Canterbury", "New Zealand" "8041", type "Accommodation and Food Services"
    Then The server responds with a 201 created response
    And I retrieve business "1"
    Then The retrieved business maps to the correct business

    Given I am logged in
    And I create a new business account with name "My Business 2", description "2nd business", address "180" "Madeup Road" "Ilam" "Christchurch" "Canterbury", "New Zealand" "8941", type "Accommodation and Food Services"
    When The server responds with a 201 created response
    And I retrieve business "2"
    Then The retrieved business maps to the correct business