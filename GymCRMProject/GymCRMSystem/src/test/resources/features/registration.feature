@component @registration
Feature: Registering trainee and trainer profiles

  Background:
    Given the database is empty
    And a training type "Yoga" exists
    And a training type "Cardio" exists

  @credentials
  Scenario: Registering a trainee returns generated credentials
    When a trainee is registered with first name "John" and last name "Doe"
    Then the response status should be 201
    And the returned username should be "John.Doe"
    And the response should contain a generated password
    And a trainee profile should exist for "John.Doe"

  @credentials
  Scenario: Registering a trainer returns generated credentials
    When a trainer is registered with first name "Jane" and last name "Smith" specialising in "Yoga"
    Then the response status should be 201
    And the returned username should be "Jane.Smith"
    And a trainer profile should exist for "Jane.Smith"
    And the trainer "Jane.Smith" should specialise in "Yoga"

  @credentials @collision
  Scenario: A second person with the same name gets a suffixed username
    Given a trainee is registered with first name "John" and last name "Doe"
    When a trainee is registered with first name "John" and last name "Doe"
    Then the returned username should be "John.Doe1"

  @credentials @collision
  Scenario: Suffixes keep climbing for each further collision
    Given a trainee is registered with first name "John" and last name "Doe"
    And a trainee is registered with first name "John" and last name "Doe"
    When a trainee is registered with first name "John" and last name "Doe"
    Then the returned username should be "John.Doe2"

  @credentials @collision
  Scenario: Trainees and trainers share one username namespace
    Given a trainee is registered with first name "John" and last name "Doe"
    When a trainer is registered with first name "John" and last name "Doe" specialising in "Cardio"
    Then the returned username should be "John.Doe1"

  @credentials @collision
  Scenario: A different name is unaffected by an existing collision chain
    Given a trainee is registered with first name "John" and last name "Doe"
    And a trainee is registered with first name "John" and last name "Doe"
    When a trainee is registered with first name "John" and last name "Roe"
    Then the returned username should be "John.Roe"

  @credentials @security
  Scenario: The stored password is encoded, not the password that was returned
    When a trainee is registered with first name "John" and last name "Doe"
    Then the stored password for "John.Doe" should not equal the returned password
    And the returned password should be accepted when logging in as "John.Doe"

  @validation
  Scenario: Registering a trainer with an unknown specialisation is rejected
    When a trainer is registered with first name "Jane" and last name "Smith" specialising in training type id 9999
    Then the response status should be 404
    And no trainer profile should exist for "Jane.Smith"

  @validation
  Scenario Outline: A trainee registration missing a required field is rejected
    When a trainee is registered with first name "<firstName>" and last name "<lastName>"
    Then the response status should be 400
    And no user should exist with first name "<firstName>" and last name "<lastName>"

    Examples:
      | firstName | lastName |
      |           | Doe      |
      | John      |          |
