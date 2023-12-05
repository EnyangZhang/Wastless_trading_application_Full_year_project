Feature: U19 Create Inventory

  Background:
    Given I am logged in with the email "jdo@gmail.com" with the role "user" - U19
    And The user with email "jdo@gmail.com" creates a new business
    And The user with email "jdo@gmail.com" creates a new product with the following details:
      | businessId             | 1                        |
      | id                     | Test                     |
      | name                   | TestNam                  |
      | description            | Test Description         |
      | manufacturer           | FakeManufacturer         |
      | recommendedRetailPrice | 10.0                     |
      | created                | 2021-05-14T04:54:05.767Z |


  Scenario: Creating an inventory item as global admin with all fields valid should succeed
    Given I am logged in with the email "admin@global.co.nz" with the role "global_admin" - U19
    When The user with email "admin@global.co.nz" creates an inventory item with the following details:
      | id           | 8          |
      | businessId   | 1          |
      | productId    | Test       |
      | quantity     | 10         |
      | pricePerItem | 5.5        |
      | totalPrice   | 55.0       |
      | manufactured | 1990-12-01 |
      | sellBy       | 2100-12-01 |
      | bestBefore   | 2100-12-01 |
      | expires      | 2100-12-01 |
    Then The inventory item will be created

  Scenario: Creating an inventory item as business admin with all fields valid should succeed
    Given I am logged in with the email "jdo@gmail.com" with the role "user" - U19
    When The user with email "jdo@gmail.com" creates an inventory item with the following details:
      | id           | 7          |
      | businessId   | 1          |
      | productId    | Test       |
      | quantity     | 10         |
      | pricePerItem | 5.5        |
      | totalPrice   | 55.0       |
      | manufactured | 1990-12-01 |
      | sellBy       | 2100-12-01 |
      | bestBefore   | 2100-12-01 |
      | expires      | 2100-12-01 |
    Then The inventory item will be created

  Scenario: Creating an inventory item as business admin with any manufactured date in the future will fail
    Given I am logged in with the email "jdo@gmail.com" with the role "user" - U19
    When The user with email "jdo@gmail.com" creates an inventory item with the following details:
      | id           | 6          |
      | businessId   | 1          |
      | productId    | Test       |
      | quantity     | 10         |
      | pricePerItem | 5.5        |
      | totalPrice   | 55.0       |
      | manufactured | 4000-12-01 |
      | sellBy       | 1990-12-01 |
      | bestBefore   | 1990-12-01 |
      | expires      | 1990-12-01 |
    Then The inventory item will not be created with a "bad request" error

  Scenario: Creating an inventory item as business admin with all dates in the past will succeed
    Given I am logged in with the email "jdo@gmail.com" with the role "user" - U19
    When The user with email "jdo@gmail.com" creates an inventory item with the following details:
      | id           | 5          |
      | businessId   | 1          |
      | productId    | Test       |
      | quantity     | 10         |
      | pricePerItem | 5.5        |
      | totalPrice   | 55.0       |
      | manufactured | 1990-12-01 |
      | sellBy       | 1990-12-01 |
      | bestBefore   | 1990-12-01 |
      | expires      | 1990-12-01 |
    Then The inventory item will be created

  Scenario: Creating an inventory item as business admin with all product life dates in the future will succeed
    Given I am logged in with the email "jdo@gmail.com" with the role "user" - U19
    When The user with email "jdo@gmail.com" creates an inventory item with the following details:
      | id           | 4          |
      | businessId   | 1          |
      | productId    | Test       |
      | quantity     | 10         |
      | pricePerItem | 5.5        |
      | totalPrice   | 55.0       |
      | manufactured | 1990-12-01 |
      | sellBy       | 4000-12-01 |
      | bestBefore   | 4000-12-01 |
      | expires      | 4000-12-01 |
    Then The inventory item will be created

  Scenario: Creating an inventory item as for a non existent business should fail
    Given I am logged in with the email "jdo@gmail.com" with the role "user" - U19
    When The user with email "jdo@gmail.com" creates an inventory item with the following details:
      | id           | 1            |
      | businessId   | 1            |
      | productId    | DoesNotExist |
      | quantity     | 10           |
      | pricePerItem | 5.5          |
      | totalPrice   | 55.0         |
      | manufactured | 1990-12-01   |
      | sellBy       | 3000-12-01   |
      | bestBefore   | 3000-12-01   |
      | expires      | 3000-12-01   |
    Then The inventory item will not be created with a "bad request" error

  Scenario: Creating an inventory item as business admin with non existent product ID should fail
    Given I am logged in with the email "jdo@gmail.com" with the role "user" - U19
    When The user with email "jdo@gmail.com" creates an inventory item with the following details:
      | id           | 2            |
      | businessId   | 1            |
      | productId    | DoesNotExist |
      | quantity     | 10           |
      | pricePerItem | 5.5          |
      | totalPrice   | 55.0         |
      | manufactured | 1990-12-01   |
      | sellBy       | 3000-12-01   |
      | bestBefore   | 3000-12-01   |
      | expires      | 3000-12-01   |
    Then The inventory item will not be created with a "bad request" error

  Scenario: Creating an inventory item as an unauthorized user should fail
    Given I am logged in with the email "non_admin@gmail.com" with the role "user" - U19
    When The user with email "non_admin@gmail.com" creates an inventory item with the following details:
      | id           | 3          |
      | businessId   | 1          |
      | productId    | Test       |
      | quantity     | 10         |
      | pricePerItem | 5.5        |
      | totalPrice   | 55.0       |
      | manufactured | 1990-12-01 |
      | sellBy       | 2100-12-01 |
      | bestBefore   | 2100-12-01 |
      | expires      | 2100-12-01 |
    Then The inventory item will not be created with a "forbidden" error