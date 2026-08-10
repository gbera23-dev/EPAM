@component @profile
Feature: Managing trainee and trainer profiles

  Background:
    Given the database is empty
    And a training type "Yoga" exists
    And a training type "Cardio" exists
    And a trainee is registered with first name "John" and last name "Doe"
    And a trainer is registered with first name "Jane" and last name "Smith" specialising in "Yoga"
    And "John.Doe" is logged in

  @status
  Scenario Outline: Deactivating an active profile succeeds
    Given the profile "<username>" is active
    When the "<role>" profile "<username>" is set to inactive
    Then the response status should be 200
    And the profile "<username>" should be inactive

    Examples:
      | role    | username   |
      | trainee | John.Doe   |
      | trainer | Jane.Smith |

  @status
  Scenario Outline: Activating an inactive profile succeeds
    Given the profile "<username>" is inactive
    When the "<role>" profile "<username>" is set to active
    Then the response status should be 200
    And the profile "<username>" should be active

    Examples:
      | role    | username   |
      | trainee | John.Doe   |
      | trainer | Jane.Smith |

  @status @error
  Scenario Outline: Activating an already active profile is rejected
    Given the profile "<username>" is active
    When the "<role>" profile "<username>" is set to active
    Then the request should be rejected
    And the profile "<username>" should be active

    Examples:
      | role    | username   |
      | trainee | John.Doe   |
      | trainer | Jane.Smith |

  @status @error
  Scenario Outline: Deactivating an already inactive profile is rejected
    Given the profile "<username>" is inactive
    When the "<role>" profile "<username>" is set to inactive
    Then the request should be rejected
    And the profile "<username>" should be inactive

    Examples:
      | role    | username   |
      | trainee | John.Doe   |
      | trainer | Jane.Smith |

  @status @error
  Scenario: Changing the status of an unknown user is rejected
    When the "trainee" profile "no.such.user" is set to inactive
    Then the response status should be 404

  @update
  Scenario: Updating a trainee replaces the name and fills in the optional fields
    When the trainee "John.Doe" is updated with the following data
      | firstName | lastName | dateOfBirth | address        | isActive |
      | Jonathan  | Doe      | 1995-06-01  | 12 Rustaveli   | true     |
    Then the response status should be 200
    And the trainee "John.Doe" should have first name "Jonathan" and last name "Doe"
    And the trainee "John.Doe" should have date of birth "1995-06-01"
    And the trainee "John.Doe" should have address "12 Rustaveli"

  @update @partial
  Scenario: Omitting the optional trainee fields leaves the stored values untouched
    Given the trainee "John.Doe" has date of birth "1995-06-01" and address "12 Rustaveli"
    When the trainee "John.Doe" is updated with first name "Jonathan" and last name "Doe" only
    Then the response status should be 200
    And the trainee "John.Doe" should have first name "Jonathan" and last name "Doe"
    And the trainee "John.Doe" should have date of birth "1995-06-01"
    And the trainee "John.Doe" should have address "12 Rustaveli"

  @update @partial
  Scenario: Omitting the active flag leaves the trainee status untouched
    Given the profile "John.Doe" is inactive
    When the trainee "John.Doe" is updated with first name "Jonathan" and last name "Doe" only
    Then the profile "John.Doe" should be inactive

  @update
  Scenario: Updating a trainer changes the specialisation
    When the trainer "Jane.Smith" is updated with specialisation "Cardio"
    Then the response status should be 200
    And the trainer "Jane.Smith" should specialise in "Cardio"

  @update @partial
  Scenario: Omitting the trainer specialisation leaves it untouched
    When the trainer "Jane.Smith" is updated without a specialisation
    Then the response status should be 200
    And the trainer "Jane.Smith" should specialise in "Yoga"

  @update @error
  Scenario: Updating a trainer with an unknown specialisation is rejected
    When the trainer "Jane.Smith" is updated with training type id 9999
    Then the response status should be 404
    And the trainer "Jane.Smith" should specialise in "Yoga"

  @update @error
  Scenario: Updating an unknown trainee is rejected
    When the trainee "no.such.user" is updated with first name "Jonathan" and last name "Doe" only
    Then the response status should be 404

  @read
  Scenario: Fetching a trainee profile returns the stored details
    When the trainee profile of "John.Doe" is requested
    Then the response status should be 200
    And the returned profile should have first name "John" and last name "Doe"

  @read @error
  Scenario: Fetching an unknown trainee profile is rejected
    When the trainee profile of "no.such.user" is requested
    Then the response status should be 404
