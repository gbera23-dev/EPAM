@component @training
Feature: Creating and deleting trainings

  Background:
    Given the database is empty
    And a training type "Yoga" exists
    And a training type "Cardio" exists
    And a trainee is registered with first name "John" and last name "Doe"
    And a trainer is registered with first name "Anna" and last name "Adams" specialising in "Yoga"
    And "John.Doe" is logged in

  @creation
  Scenario: Adding a training links the trainee and the trainer
    When a training named "Morning session" is added for trainee "John.Doe" with trainer "Anna.Adams" on "2025-03-14" lasting 5
    Then the response status should be 200
    And a training named "Morning session" should exist for trainee "John.Doe"
    And that training should have trainer "Anna.Adams"
    And that training should be on "2025-03-14" with duration 5

  @creation @specialisation
  Scenario: The training type comes from the trainer's specialisation
    When a training named "Morning session" is added for trainee "John.Doe" with trainer "Anna.Adams" on "2025-03-14" lasting 5
    Then that training should have training type "Yoga"

  @creation @specialisation
  Scenario: Changing the trainer's specialisation changes the type of later trainings
    Given the trainer "Anna.Adams" is updated with specialisation "Cardio"
    When a training named "Evening session" is added for trainee "John.Doe" with trainer "Anna.Adams" on "2025-03-14" lasting 5
    Then that training should have training type "Cardio"

  @creation @error
  Scenario: Adding a training for an unknown trainee is rejected
    When a training named "Morning session" is added for trainee "no.such.user" with trainer "Anna.Adams" on "2025-03-14" lasting 5
    Then the response status should be 404
    And no training named "Morning session" should exist

  @creation @error
  Scenario: Adding a training with an unknown trainer is rejected
    When a training named "Morning session" is added for trainee "John.Doe" with trainer "no.such.trainer" on "2025-03-14" lasting 5
    Then the response status should be 404
    And no training named "Morning session" should exist

  @creation @validation
  Scenario Outline: A training with invalid data is rejected
    When a training named "<name>" is added for trainee "John.Doe" with trainer "Anna.Adams" on "<date>" lasting <duration>
    Then the response status should be 400

    Examples:
      | name            | date       | duration |
      |                 | 2025-03-14 | 5        |
      | Morning session |            | 5        |

  @deletion
  Scenario: Deleting a training removes it
    Given a training named "Morning session" exists for trainee "John.Doe" with trainer "Anna.Adams" on "2025-03-14" lasting 5
    When that training is deleted
    Then the response status should be 200
    And no training named "Morning session" should exist

  @deletion
  Scenario: Deleting one training leaves the others in place
    Given a training named "Morning session" exists for trainee "John.Doe" with trainer "Anna.Adams" on "2025-03-14" lasting 5
    And a training named "Evening session" exists for trainee "John.Doe" with trainer "Anna.Adams" on "2025-03-15" lasting 3
    When the training named "Morning session" is deleted
    Then no training named "Morning session" should exist
    And a training named "Evening session" should exist for trainee "John.Doe"

  @deletion @error
  Scenario: Deleting a training that does not exist is rejected
    When the training with id 9999 is deleted
    Then the response status should be 404

  @deletion @cascade
  Scenario: Deleting a trainee removes their trainings
    Given a training named "Morning session" exists for trainee "John.Doe" with trainer "Anna.Adams" on "2025-03-14" lasting 5
    And a training named "Evening session" exists for trainee "John.Doe" with trainer "Anna.Adams" on "2025-03-15" lasting 3
    When the trainee "John.Doe" is deleted
    Then the response status should be 200
    And no trainee profile should exist for "John.Doe"
    And no trainings should exist for trainee "John.Doe"

  @deletion @cascade
  Scenario: Deleting a trainee leaves their trainers in place
    Given the trainers of "John.Doe" are "Anna.Adams"
    When the trainee "John.Doe" is deleted
    Then a trainer profile should exist for "Anna.Adams"
    And the trainees of "Anna.Adams" should not include "John.Doe"

  @deletion @error
  Scenario: Deleting an unknown trainee is rejected
    When the trainee "no.such.user" is deleted
    Then the response status should be 404
