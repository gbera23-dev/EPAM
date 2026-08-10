@component @trainer-assignment
Feature: Managing the trainers assigned to a trainee

  Background:
    Given the database is empty
    And a training type "Yoga" exists
    And a trainee is registered with first name "John" and last name "Doe"
    And a trainer is registered with first name "Anna" and last name "Adams" specialising in "Yoga"
    And a trainer is registered with first name "Bob" and last name "Brown" specialising in "Yoga"
    And a trainer is registered with first name "Cara" and last name "Clark" specialising in "Yoga"
    And "John.Doe" is logged in

  @assignment
  Scenario: Assigning trainers to a trainee with none assigned
    When the trainers of "John.Doe" are set to "Anna.Adams, Bob.Brown"
    Then the response status should be 200
    And the trainers assigned to "John.Doe" should be "Anna.Adams, Bob.Brown"
    And the response should list exactly 2 trainers

  @assignment @replacement
  Scenario: Assigning a new list replaces the previous one entirely
    Given the trainers of "John.Doe" are "Anna.Adams, Bob.Brown"
    When the trainers of "John.Doe" are set to "Bob.Brown, Cara.Clark"
    Then the trainers assigned to "John.Doe" should be "Bob.Brown, Cara.Clark"
    And "Anna.Adams" should not be assigned to "John.Doe"

  @assignment @replacement
  Scenario: A removed trainer no longer lists the trainee on their side
    Given the trainers of "John.Doe" are "Anna.Adams, Bob.Brown"
    When the trainers of "John.Doe" are set to "Bob.Brown"
    Then the trainees of "Anna.Adams" should not include "John.Doe"
    And the trainees of "Bob.Brown" should include "John.Doe"

  @assignment @replacement
  Scenario: Assigning an empty list clears every assignment
    Given the trainers of "John.Doe" are "Anna.Adams, Bob.Brown"
    When the trainers of "John.Doe" are set to an empty list
    Then no trainers should be assigned to "John.Doe"
    And the trainees of "Anna.Adams" should not include "John.Doe"

  @assignment @idempotence
  Scenario: Assigning the same list twice leaves one assignment per trainer
    Given the trainers of "John.Doe" are "Anna.Adams, Bob.Brown"
    When the trainers of "John.Doe" are set to "Anna.Adams, Bob.Brown"
    Then the trainers assigned to "John.Doe" should be "Anna.Adams, Bob.Brown"
    And the response should list exactly 2 trainers

  @unassigned
  Scenario: The unassigned list excludes trainers already assigned
    Given the trainers of "John.Doe" are "Anna.Adams"
    When the trainers not assigned to "John.Doe" are requested
    Then the response status should be 200
    And the returned trainers should be "Bob.Brown, Cara.Clark"

  @unassigned
  Scenario: Every trainer is unassigned when the trainee has none
    When the trainers not assigned to "John.Doe" are requested
    Then the returned trainers should be "Anna.Adams, Bob.Brown, Cara.Clark"

  @error
  Scenario: Assigning trainers to an unknown trainee is rejected
    When the trainers of "no.such.user" are set to "Anna.Adams"
    Then the response status should be 404

  @error
  Scenario: Assigning an unknown trainer is rejected or ignored
    When the trainers of "John.Doe" are set to "no.such.trainer"
    Then no trainers should be assigned to "John.Doe"
