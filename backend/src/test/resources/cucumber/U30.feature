@Skip
Feature: U30 Individual Full Sale Listing
  Scenario: AC3 I can like a sale listing. When I like a sale listing, a notification is displayed

  Scenario: AC3 I can like a sale listing. When I like a sale listing, the number of likes increases by 1
    Given I am logged in
    And a sale listing exists that I have not previously liked
    When I like the sale listing
    Then the number of likes in the sale listing increments by 1
    And I receive a notification that the sale listing has been liked

  Scenario: AC3 I cannot like a sale listing I have previously liked
    Given I am logged in
    And a sale listing exists that I have already previously liked
    When I like the sale listing
    Then I receive an error preventing me from doing so

  Scenario: AC5 I can see how many users have also bookmarked the sale listing
    Given I am logged in
    When I view an individual full sale listing
    Then I can view the number of users who have liked the sale listing

  Scenario: AC6 I can unlike the sale listing - the number of likes decrements by 1
    Given I am logged in
    And a sale listing exists that I have already previously liked
    When I unlike the sale listing
    Then the number of likes in the sale listing decrements by 1

  Scenario: AC6 I can unlike the sale listing
    Given I am logged in
    And a sale listing exists that I have already previously liked
    When I unlike the sale listing
    Then I receive a notification that the sale listing has been unliked
