@Skip
Feature: U41 Sales Report

  Scenario: Not logged in
    Given I am not logged in
    When I try to view the sales report for the business with ID 1 with granularity "Daily", start date "12/12/2020", and end date "12/12/2021"
    Then I am prevented from seeing the sales report with the error "Unauthorised"

  Scenario: AC2 - Report for a specific day
    Given I am logged in
    And I am the administrator of a business with ID 1
    When I try to view the sales report for the business with ID 1 for the "day" "12/12/2020"
    Then I receive a valid sales report for the "day" "12/12/2020"

  Scenario: AC2 - Report for a specific week
    Given I am logged in
    And I am the administrator of a business with ID 1
    When I try to view the sales report for the business with ID 1 for the "week starting" "12/12/2020"
    Then I receive a valid sales report for the "week starting" "12/12/2020"

  Scenario: AC2 - Report for a specific month
    Given I am logged in
    And I am the administrator of a business with ID 1
    When I try to view the sales report for the business with ID 1 for the "month starting" "1/12/2020"
    Then I receive a valid sales report for the "month starting" "12/12/2020"

  Scenario: AC2 - Report for a specific year
    Given I am logged in
    And I am the administrator of a business with ID 1
    When I try to view the sales report for the business with ID 1 for the "year starting" "12/12/2020"
    Then I receive a valid sales report for the "year starting" "1/1/2020"

  Scenario: AC3 - Request a report for a custom period over one year
    Given I am logged in
    And I am the administrator of a business with ID 1
    When I try to view the sales report for the business with ID 1 with granularity "Default", start date "14/7/2020", and end date "12/12/2021"
    Then I receive a sales report for business 1 with 517 data point(s)
    # consider rewording the above then statement - might be 516 points depending if last day is included

  Scenario: AC3 - Request a report for a custom period less than one year
    Given I am logged in
    And I am the administrator of a business with ID 1
    When I try to view the sales report for the business with ID 1 with granularity "Default", start date "14/7/2021", and end date "12/8/2021"
    Then I receive a sales report for business 1 with 30 data point(s)
    # consider rewording the above then statement - might be 29 points depending if last day is included

  Scenario: AC5 - Request a report for default granularity
    Given I am logged in
    And I am the administrator of a business with ID 1
    When I try to view the sales report for the business with ID 1 with granularity "Default", start date "12/12/2020", and end date "12/12/2021"
    Then I receive a sales report for business 1 with 1 data point(s)

  Scenario: AC5 - Request a report for daily granularity
    Given I am logged in
    And I am the administrator of a business with ID 1
    When I try to view the sales report for the business with ID 1 with granularity "Daily", start date "12/12/2020", and end date "12/12/2021"
    Then I receive a sales report for business 1 with 365 data point(s)
    # consider rewording the above then statement

  Scenario: AC5 - Request a report for weekly granularity
    Given I am logged in
    And I am the administrator of a business with ID 1
    When I try to view the sales report for the business with ID 1 with granularity "Weekly", start date "12/12/2020", and end date "12/12/2021"
    Then I receive a sales report for business 1 with 52 data point(s)
    # consider rewording the above then statement

  Scenario: AC5 - Request a report for monthly granularity
    Given I am logged in
    And I am the administrator of a business with ID 1
    When I try to view the sales report for the business with ID 1 with granularity "Monthly", start date "12/12/2020", and end date "12/12/2021"
    Then I receive a sales report for business 1 with 12 data point(s)
    # consider rewording the above then statement

  Scenario: AC5 - Request a report for yearly granularity
    Given I am logged in
    And I am the administrator of a business with ID 1
    When I try to view the sales report for the business with ID 1 with granularity "Yearly", start date "12/12/2020", and end date "12/12/2021"
    Then I receive a sales report for business 1 with 1 data point(s)
    # consider rewording the above then statement