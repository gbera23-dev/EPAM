@component @training-search
Feature: Searching trainings by criteria

  Background:
    Given the database is empty
    And a training type "Yoga" exists
    And a training type "Cardio" exists
    And a trainee is registered with first name "John" and last name "Doe"
    And a trainee is registered with first name "Mary" and last name "Moore"
    And a trainer is registered with first name "Anna" and last name "Adams" specialising in "Yoga"
    And a trainer is registered with first name "Bob" and last name "Brown" specialising in "Cardio"
    And the following trainings exist
      | name       | trainee    | trainer    | date       | duration |
      | Session A  | John.Doe   | Anna.Adams | 2025-03-01 | 4        |
      | Session B  | John.Doe   | Anna.Adams | 2025-03-20 | 5        |
      | Session C  | John.Doe   | Bob.Brown  | 2025-04-10 | 6        |
      | Session D  | Mary.Moore | Anna.Adams | 2025-03-05 | 7        |
    And "John.Doe" is logged in

  @trainee-side
  Scenario: Searching without criteria returns every training of that trainee
    When the trainings of trainee "John.Doe" are searched with no criteria
    Then the response status should be 200
    And the returned trainings should be "Session A, Session B, Session C"

  @trainee-side
  Scenario: Another trainee's trainings are never returned
    When the trainings of trainee "John.Doe" are searched with no criteria
    Then the returned trainings should not include "Session D"

  @trainee-side @date-range
  Scenario: A date range narrows the results to that window
    When the trainings of trainee "John.Doe" are searched from "2025-03-10" to "2025-03-31"
    Then the returned trainings should be "Session B"

  @trainee-side @date-range
  Scenario: The date range boundaries are inclusive
    When the trainings of trainee "John.Doe" are searched from "2025-03-01" to "2025-03-20"
    Then the returned trainings should be "Session A, Session B"

  @trainee-side @date-range
  Scenario: A range matching nothing returns an empty list
    When the trainings of trainee "John.Doe" are searched from "2025-01-01" to "2025-01-31"
    Then the returned trainings should be empty

  @trainee-side @filter
  Scenario: Filtering by trainer name narrows the results
    When the trainings of trainee "John.Doe" are searched with trainer name "Anna.Adams"
    Then the returned trainings should be "Session A, Session B"

  @trainee-side @filter
  Scenario: Filtering by training type narrows the results
    When the trainings of trainee "John.Doe" are searched with training type "Cardio"
    Then the returned trainings should be "Session C"

  @trainee-side @filter
  Scenario: Criteria combine rather than widen the results
    When the trainings of trainee "John.Doe" are searched with trainer name "Anna.Adams" from "2025-03-10" to "2025-03-31"
    Then the returned trainings should be "Session B"

  @trainee-side @filter
  Scenario: A trainee with no trainings returns an empty list
    When the trainings of trainee "Mary.Moore" are searched with trainer name "Bob.Brown"
    Then the returned trainings should be empty

  @trainer-side
  Scenario: Searching a trainer's trainings returns every trainee they trained
    When the trainings of trainer "Anna.Adams" are searched with no criteria
    Then the response status should be 200
    And the returned trainings should be "Session A, Session B, Session D"

  @trainer-side @filter
  Scenario: Filtering a trainer's trainings by trainee name narrows the results
    When the trainings of trainer "Anna.Adams" are searched with trainee name "Mary.Moore"
    Then the returned trainings should be "Session D"

  @trainer-side @date-range
  Scenario: Filtering a trainer's trainings by date range narrows the results
    When the trainings of trainer "Anna.Adams" are searched from "2025-03-01" to "2025-03-10"
    Then the returned trainings should be "Session A, Session D"
