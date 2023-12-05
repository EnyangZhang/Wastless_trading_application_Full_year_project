Feature: U4 Default global admin

  Scenario: The DGAA can make an individual an admin
    Given A user exists with user ID: 2
    And A user whose role is "DGAA" is logged in
    When The user tries to "add" the user with ID = "2" as an admin
    Then The server responds to the DGAA request with a 200 status code
    And The user with user ID: 2 has the role: "global_admin"

  Scenario: If someone who is not logged in tried to make someone an admin, a 401 response is sent.
    Given A user exists with user ID: 2
    And There is no user logged in
    When The user tries to "add" the user with ID = "2" as an admin
    Then The server responds to the DGAA request with a 401 status code

  Scenario: If someone who is logged in but is not the DGAA tries to make someone an admin, a 403 response is sent.
    Given A user exists with user ID: 2
    And A user whose role is "user" is logged in
    When The user tries to "add" the user with ID = "2" as an admin
    Then The server responds to the DGAA request with a 403 status code

  Scenario: If the DGAA tries to make a user who does not exist an admin, a 406 response is sent
    Given A user with user ID: 99 does not exist
    And A user whose role is "DGAA" is logged in
    When The user tries to "add" the user with ID = "99" as an admin
    Then The server responds to the DGAA request with a 406 status code

  Scenario: The DGAA can remove admin rights from any individual.
    Given A user whose role is "DGAA" is logged in
    And A global application admin exists with user ID: 2
    When The user tries to "remove" the user with ID = "2" as an admin
    Then The server responds to the DGAA request with a 200 status code
    And The user with user ID: 2 has the role: "user"

  Scenario: If someone who is not logged in tries to revoke someone's admin status, a 401 response is sent.
    Given A user exists with user ID: 2
    And There is no user logged in
    When The user tries to "remove" the user with ID = "2" as an admin
    Then The server responds to the DGAA request with a 401 status code

  Scenario: If someone who is logged in but is not the DGAA tries to remove someone as admin, a 403 response is sent.
    Given A user exists with user ID: 2
    And A user whose role is "user" is logged in
    When The user tries to "remove" the user with ID = "2" as an admin
    Then The server responds to the DGAA request with a 403 status code

  Scenario: If the DGAA tries to remove a user who does not exist as admin, a 406 response is sent
    Given A user with user ID: 99 does not exist
    And A user whose role is "DGAA" is logged in
    When The user tries to "remove" the user with ID = "99" as an admin
    Then The server responds to the DGAA request with a 406 status code