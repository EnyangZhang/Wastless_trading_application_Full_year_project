Feature: U31 Purchases
  Background: I can make purchases, as a logged-in user, I can buy something offered for sale.
    Given There are 2 inventory items in seller's inventory with 10 quantity for each of inventory item
    Given Inventory ID 1 has 5 corresponding sale item quantity
    And Inventory ID 2 has 8 corresponding sale item quantity

  Scenario: AC3: The seller’s inventory is updated to reflect the fact that the goods sold are no longer in stock.
    Given I am logged in with the email "test123@test.com" with the role "user" - U31
    When The user purchase the saleItem ID 1 that has 5 quantity in seller's sale listings with correct business ID 1
    Then The quantity of inventory id 1 of seller will be decremented to 5
    And The server responds 200

  Scenario: AC3: A logged in user tries to purchase a sale listing but with wrong request.
    Given I am logged in with the email "test123@test.com" with the role "user" - U31
    When The user purchase the saleItem ID 1 that has 5 quantity in seller's sale listings but with incorrect request
    Then The server responds 400

  Scenario: AC3: A user tries to purchase a sale listing without logging in.
    Given I am not logged in as a user - U31
    When The user purchase the saleItem ID 1 that has 5 quantity in seller's sale listings with correct business ID 1
    Then The server responds 401

  Scenario: AC3: A logged in user tries to purchase a sale listing but with wrong business.
    Given I am logged in with the email "test123@test.com" with the role "user" - U31
    When The user purchase the saleItem ID 1 that has 5 quantity in seller's sale listings but with incorrect business Id 2
    Then The server responds 400

  Scenario: AC3: A logged in user tries to purchase a sale listing but business not exist.
    Given I am logged in with the email "test123@test.com" with the role "user" - U31
    When The user purchase the saleItem ID 1 that has 5 quantity in seller's sale listings but with incorrect business Id 3
    Then The server responds 406

  Scenario: AC4: Before a user purchase a sale listing the sale listing will appear in search result.
    Given I am logged in with the email "test123@test.com" with the role "user" - U31
    When The user has not purchase sale listings with ID 1 and 2
    Then The sale listings with ID 1 and 2 belongs to business id 1 will appear in the search result

  Scenario: AC4: A user tries to purchase a sale listing without logging in, the sale listings will still appear in the search result.
    Given I am not logged in as a user - U31
    When The user purchase the saleItem ID 1 that has 5 quantity in seller's sale listings with correct business ID 1
    Then The sale listings with ID 1 and 2 belongs to business id 1 will appear in the search result
    And The server responds 401

  Scenario: AC4: A Logged in user purchased a sale listing, the search result will only show the sale listings that is not sold.
    Given I am logged in with the email "test123@test.com" with the role "user" - U31
    When The user purchase the saleItem ID 1 that has 5 quantity in seller's sale listings with correct business ID 1
    Then The sale listings with ID 2 belongs to business id 1 will appear in the search result
    And The server responds 200

  Scenario: AC4: A Logged in user purchased a sale listing with wrong business., the sale listings will still appear in the search result.
    Given I am logged in with the email "test123@test.com" with the role "user" - U31
    When The user purchase the saleItem ID 1 that has 5 quantity in seller's sale listings but with incorrect business Id 2
    Then The sale listings with ID 1 and 2 belongs to business id 1 will appear in the search result
    And The server responds 400

  Scenario: AC4: A Logged in user purchased a sale listing with wrong request, the sale listings will still appear in the search result.
    Given I am logged in with the email "test123@test.com" with the role "user" - U31
    When The user purchase the saleItem ID 1 that has 5 quantity in seller's sale listings but with incorrect request
    Then The sale listings with ID 1 and 2 belongs to business id 1 will appear in the search result
    And The server responds 400

  Scenario: AC4: A Logged in user purchased a sale listing, the sale listings will still appear in the search result.
    Given I am logged in with the email "test123@test.com" with the role "user" - U31
    When The user purchase the saleItem ID 1 that has 5 quantity in seller's sale listings but with incorrect business Id 3
    Then The sale listings with ID 1 and 2 belongs to business id 1 will appear in the search result
    And The server responds 406

  Scenario: AC5: I can request to get all sold sale listings for a business
    Given I am logged in with the email "test123@test.com" with the role "user" - U31
    When The user purchase the saleItem ID 2 that has 8 quantity in seller's sale listings with correct business ID 1
    And The user requests to see all sold listings for the business with ID 1
    Then The user receives a list of sale listings of length 1
    And The first sale listing in the list for business ID 1 has an ID of 2

  Scenario: AC5: I can request to get all sold sale listings for a business with no sold sale listings
    Given I am logged in with the email "test123@test.com" with the role "user" - U31
    And The business with ID 1 has no sold sale listings
    When The user requests to see all sold listings for the business with ID 1
    Then The user receives a list of sale listings of length 0

  @Skip
  Scenario: AC1: When I purchase something, other users that have liked the sale listing will get notification and that sale listing will no longer available
    Given I am logged in with the email "test123@test.com" with the role "user" - U31
    And the user with email "test2@test.com" likes the sale listing with ID 1
    When Another user purchased the sale listing with ID 1
    Then The user with email "test2@test.com" will get the notification

  Scenario: AC2: A notification appears on my home feed to remind me what I have purchased, the payment I need to make and where I need to collect my goods (the business location)
    Given I am logged in with the email "test123@test.com" with the role "user" - U31
    When The user purchase the saleItem ID 2 that has 8 quantity in seller's sale listings with correct business ID 1
    Then The user receives a notification about the purchase
    And The notification message contains "Watties Baked Beans - 420g can"
    And The notification message contains "Seller may be willing to consider near offers"