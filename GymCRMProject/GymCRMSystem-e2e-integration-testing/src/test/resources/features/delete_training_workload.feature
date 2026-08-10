@e2e @workload @delete-training
Feature: Deleting a training reaches the trainer history service
  Deleting a training publishes a removal message for the same month
  the training belonged to. The history service should give the hours back.

  Background:
    Given a registered trainee
    And a registered trainer
    And the trainee is logged in

  @happy-path
  Scenario: Deleting the only training gives the hours back
    Given a training was added for the trainee with the trainer on "2025-03-14" lasting 5 hours
    And the history service eventually records 5 hours for the trainer in "2025-03"
    When that training is deleted
    Then the request should be accepted
    And the history service should eventually record no hours for the trainer in "2025-03"

  @partial
  Scenario: Deleting one of two trainings leaves the remainder standing
    Given a training was added for the trainee with the trainer on "2025-03-03" lasting 4 hours
    And a training was added for the trainee with the trainer on "2025-03-27" lasting 6 hours
    And the history service eventually records 10 hours for the trainer in "2025-03"
    When the training on "2025-03-03" is deleted
    Then the history service should eventually record 6 hours for the trainer in "2025-03"

  @months
  Scenario: A deletion only touches the month the training belonged to
    Given a training was added for the trainee with the trainer on "2025-03-14" lasting 5 hours
    And a training was added for the trainee with the trainer on "2025-04-02" lasting 2 hours
    And the history service eventually records 2 hours for the trainer in "2025-04"
    When the training on "2025-04-02" is deleted
    Then the history service should eventually record no hours for the trainer in "2025-04"
    And the history service should eventually record 5 hours for the trainer in "2025-03"

  @round-trip
  Scenario: Adding and removing the same training nets out to nothing
    When a training is added for the trainee with the trainer on "2025-03-14" lasting 5 hours
    And the history service eventually records 5 hours for the trainer in "2025-03"
    And that training is deleted
    Then the history service should eventually record no hours for the trainer in "2025-03"

  @error
  Scenario: Deleting a training that does not exist takes nobody's hours away
    Given a training was added for the trainee with the trainer on "2025-03-14" lasting 5 hours
    And the history service eventually records 5 hours for the trainer in "2025-03"
    When a training that does not exist is deleted
    Then the request should be rejected as not found
    And the history service should keep recording 5 hours for the trainer in "2025-03" for the next 3 seconds
