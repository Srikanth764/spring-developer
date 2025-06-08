Feature: User Management API
  As a client application
  I want to manage users through REST API
  So that I can perform CRUD operations on user data

  Background:
    Given the application is running
    And the database is clean

  Scenario: Create a new user successfully
    When I create a user with name "John Doe", email "john@example.com", and age 25
    Then the user should be created successfully
    And the response should contain user details
    And the user should have a valid ID

  Scenario: Create user with duplicate email
    Given a user exists with email "john@example.com"
    When I create a user with name "Jane Doe", email "john@example.com", and age 30
    Then the creation should fail with conflict error
    And the error message should indicate email already exists

  Scenario: Create user with invalid data
    When I create a user with name "", email "invalid-email", and age -1
    Then the creation should fail with validation error
    And the error should contain validation details

  Scenario: Get all users
    Given users exist in the system:
      | name     | email           | age |
      | John Doe | john@example.com| 25  |
      | Jane Doe | jane@example.com| 30  |
    When I request all users
    Then I should receive a list of 2 users
    And the list should contain user "John Doe"
    And the list should contain user "Jane Doe"

  Scenario: Get user by ID
    Given a user exists with name "John Doe", email "john@example.com", and age 25
    When I request the user by ID
    Then I should receive the user details
    And the user name should be "John Doe"
    And the user email should be "john@example.com"

  Scenario: Get non-existent user
    When I request a user with ID 999
    Then the request should fail with not found error
    And the error message should indicate user not found

  Scenario: Update user successfully
    Given a user exists with name "John Doe", email "john@example.com", and age 25
    When I update the user with name "John Smith", email "john.smith@example.com", and age 26
    Then the user should be updated successfully
    And the user name should be "John Smith"
    And the user email should be "john.smith@example.com"
    And the user age should be 26

  Scenario: Update user with duplicate email
    Given users exist in the system:
      | name     | email           | age |
      | John Doe | john@example.com| 25  |
      | Jane Doe | jane@example.com| 30  |
    When I update user "John Doe" with email "jane@example.com"
    Then the update should fail with conflict error
    And the error message should indicate email already exists

  Scenario: Update non-existent user
    When I update a user with ID 999
    Then the update should fail with not found error
    And the error message should indicate user not found

  Scenario: Delete user successfully
    Given a user exists with name "John Doe", email "john@example.com", and age 25
    When I delete the user
    Then the user should be deleted successfully
    And the user should no longer exist in the system

  Scenario: Delete non-existent user
    When I delete a user with ID 999
    Then the deletion should fail with not found error
    And the error message should indicate user not found
