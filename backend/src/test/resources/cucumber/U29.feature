Feature: U29 Search/Browse Sale listings

  Background: Register a default user
    Given There are 8 sale items listed for sale by businesses
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

  Scenario: AC1 - Logged in user can browse all sale listings default ordering is applied
    Given The user is logged in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" navigates to page 1 of the browse sale listings page
    Then The user sees the currently unsold sale listings
    And The ordering of the cards is:
      |3|6|2|5|1|4|7|8|

  Scenario: AC1 - Logged in user views a limited number of listings through pagination
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    And The number of listings per page is set to 5
    When The user "jdo@gmail.com" navigates to page 1 of the browse sale listings page
    Then The user only sees 5 sale listings on the page

  Scenario: AC1 - Logged in users can page through to view different pages of cards
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    And The number of listings per page is set to 5
    When The user "jdo@gmail.com" navigates to page 2 of the browse sale listings page
    Then The user only sees the 3 listings on the page

  Scenario: AC2 - Listings contain the the relevant sale listing attributes
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" navigates to page 1 of the browse sale listings page
    Then The product name is available on the displayed listings
    And The total price is available on the displayed listings
    And The quantity is available on the displayed listings
    And The closing date is available on the displayed listings
    And The more info is available on the displayed listings

  Scenario: AC4 - Listings can be ordered by product name (descending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "name", "asc"
    Then The ordering of the listings is:
      |3|6|2|5|1|4|7|8|

  Scenario: AC4 - Listings can be ordered by seller name (ascending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "seller", "asc"
    Then The ordering of the listings is:
      |3|6|2|5|8|1|4|7|

  Scenario: AC4 - Listings can be ordered by seller name (descending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "seller", "desc"
    Then The ordering of the listings is:
      |1|4|7|8|2|5|3|6|

  Scenario: AC4 - Listings can be ordered by seller suburb (ascending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "suburb", "asc"
    Then The ordering of the listings is:
      |8|1|4|7|2|5|3|6|

  Scenario: AC4 - Listings can be ordered by seller suburb (descending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "suburb", "desc"
    Then The ordering of the listings is:
      |3|6|2|5|1|4|7|8|

  Scenario: AC4 - Listings can be ordered by seller city (ascending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "city", "asc"
    Then The ordering of the listings is:
      |1|2|3|4|5|6|7|8|

  Scenario: AC4 - Listings can be ordered by seller city (descending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "city", "desc"
    Then The ordering of the listings is:
      |1|2|3|4|5|6|7|8|

  Scenario: AC4 - Listings can be ordered by seller country (ascending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "country", "asc"
    Then The ordering of the listings is:
      |8|2|5|3|6|1|4|7|

  Scenario: AC4 - Listings can be ordered by seller country (descending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "country", "desc"
    Then The ordering of the listings is:
      |1|4|7|3|6|2|5|8|

  Scenario: AC4 - Listings can be ordered by saleItemId (ascending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "saleItemId", "asc"
    Then The ordering of the listings is:
      |1|2|3|4|5|6|7|8|

  Scenario: AC4 - Listings can be ordered by saleItemId (descending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "saleItemId", "desc"
    Then The ordering of the listings is:
      |1|2|3|4|5|6|7|8|

  Scenario: AC4 - Listings can be ordered by created date (ascending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "created", "asc"
    Then The ordering of the listings is:
      |1|2|3|4|5|6|7|8|

  Scenario: AC4 - Listings can be ordered by created date (descending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "created", "desc"
    Then The ordering of the listings is:
      |1|2|3|4|5|6|7|8|

  Scenario: AC4 - Listings can be ordered by price (ascending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "price", "asc"
    Then The ordering of the listings is:
      |8|2|6|3|7|5|4|1|

  Scenario: AC4 - Listings can be ordered by price (descending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "price", "desc"
    Then The ordering of the listings is:
      |1|4|5|3|7|2|6|8|

  Scenario: AC4 - Listings can be ordered by expiry date (ascending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "expires", "asc"
    Then The ordering of the listings is:
      |1|2|3|4|5|6|7|8|

  Scenario: AC4 - Listings can be ordered by expiry date (descending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "expires", "desc"
    Then The ordering of the listings is:
      |1|2|3|4|5|6|7|8|

  Scenario: AC4 - Listings can be ordered by quantity (ascending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "quantity", "asc"
    Then The ordering of the listings is:
      |1|2|3|4|5|6|7|8|

  Scenario: AC4 - Listings can be ordered by quantity (descending)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" changes the ordering to "quantity", "desc"
    Then The ordering of the listings is:
      |8|1|2|3|4|5|6|7|
    
  Scenario: AC5 - Listings can be filtered by business type (Accommodation and Food Services)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" filters the search results by "Accommodation and Food Services"
    Then The listings returned are:
      |1|4|7|

  Scenario: AC5 - Listings can be filtered by business type (Retail Trade)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" filters the search results by "Retail Trade"
    Then The listings returned are:
      |2|5|


  Scenario: AC5 - Listings can be filtered by business type (Charitable Organisation)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" filters the search results by "Charitable Organisation"
    Then The listings returned are:
      |3|6|

  Scenario: AC5 - Listings can be filtered by business type (Non-profit Organisation)
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" filters the search results by "Non-Profit Organisation"
    Then The listings returned are:
      |8|

  Scenario: AC6 - Listings can be searched for via product name
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" searches for a listing with "Ghost"
    Then The listings returned are:
    |3|6|

  Scenario: AC6 - Listings can be searched for via product name using AND predicate
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" searches for a listing with "Watties and beans"
    Then The listings returned are:
      |1|4|7|

  Scenario: AC6 - Listings can be searched for via product name using OR predicate
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" searches for a listing with "Watties or means"
    Then The listings returned are:
      |1|2|4|5|7|

  Scenario: AC6 - Listings can be searched for via product name using quotations predicate
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" searches for a listing with "\"Weeto-Bix\""
    Then The listings returned are:
      |8|

  Scenario: AC7 - Listings can be filtered by inputting a minimum price and maximum price
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" filters the search results with min price 30 and max price 90
    Then The listings returned are:
      |3|4|5|7|

  Scenario: AC8 - Listings can be searched for via seller name
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" searches for a listing with "McRonalds"
    Then The listings returned are:
      |2|5|

  Scenario: AC8 - Listings can be searched for via seller name using the AND predicate
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" searches for a listing with "Mc and Ronalds"
    Then The listings returned are:
      |2|3|5|6|

  Scenario: AC8 - Listings can be searched for via seller name using the OR predicate
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" searches for a listing with "McRonalds or Sanitiserium"
    Then The listings returned are:
      |2|5|8|

  Scenario: AC8 - Listings can be searched for via seller name using the quotations Predicate
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" searches for a listing with "\"McRonalds\""
    Then The listings returned are:
      |2|5|

  Scenario: AC9 - Listings can be searched for via the suburb of the seller
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" searches for a listing with "B Suburb"
    Then The listings returned are:
      |1|2|3|4|5|6|7|8|

  Scenario: AC9 - Listings can be searched for via the city of the seller
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" searches for a listing with "Christchurch"
    Then The listings returned are:
      |1|2|3|4|5|6|7|8|

  Scenario: AC9 - Listings can be searched for via the region of the seller
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" searches for a listing with "Louisiana"
    Then The listings returned are:
      |2|5|8|

  Scenario: AC9 - Listings can be searched for via the country of the seller
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" searches for a listing with "Canada"
    Then The listings returned are:
      |8|

  Scenario: AC10 - Listings can be filtered by inputting a earliest closing date
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" filters the search results with earliest closing date tomorrow
    Then The listings returned are:
      |8|

  Scenario: AC10 - Listings can be filtered by inputting a latest closing date
    Given The user logs in with email "jdo@gmail.com" and password "passwordabc123"
    When The user "jdo@gmail.com" filters the search results with latest closing date tomorrow
    Then The listings returned are:
      |1|2|3|4|5|6|7|




