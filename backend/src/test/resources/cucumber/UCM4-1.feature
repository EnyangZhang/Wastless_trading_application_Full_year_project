Feature: UCM4-1: Card deletion
  Scenario: Bluesky scenario - deleting my own card
    Given I am logged in with the email "test123@test.com" with the role "user" - UCM4.1
    And I have a created a marketplace card with ID 1
    When I try to delete the card with ID 1
    Then I can no longer see the card with ID 1

  Scenario: Trying to delete another user's card
    Given I am logged in with the email "test123@test.com" with the role "user" - UCM4.1
    And another user has created a card with ID 1
    When I try to delete the card with ID 1
    Then I am prevented from deleting the card with the error "Forbidden"

  Scenario: Deleting a card as an admin
    Given I am logged in with the email "admin@test.com" with the role "global_admin" - UCM4.1
    And another user has created a card with ID 1
    When I try to delete the card with ID 1
    Then I can no longer see the card with ID 1

  Scenario: Deleting a card as an DGAA
    Given I am logged in as the DGAA
    And another user has created a card with ID 1
    When I try to delete the card with ID 1
    Then I can no longer see the card with ID 1

  Scenario: Deleting a card when I am not logged in
    Given I am not logged in as a user
    When I try to delete the card with ID 1
    Then I am prevented from deleting the card with the error "Unauthorised"

  Scenario: Deleting a non-existent card
    Given I am logged in with the email "admin@test.com" with the role "global_admin" - UCM4.1
    When I try to delete the card with ID 99
    Then I am prevented from deleting the card with the error "Not acceptable"