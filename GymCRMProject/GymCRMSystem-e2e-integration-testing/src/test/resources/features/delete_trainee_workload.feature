@e2e @workload @delete-trainee
Feature: Deleting a trainee reaches the trainer history service in one batch
  Removing a trainee wipes out every training they had, and the main app
  reports that as a single batch message covering all affected trainers.

  Background:
    Given a registered trainee
    And a registered trainer
    And the trainee is logged in

  @happy-path
  Scenario: Deleting a trainee returns their trainer's hours
    Given a training was added for the trainee with the trainer on "2025-03-14" lasting 5 hours
    And the history service eventually records 5 hours for the trainer in "2025-03"
    When the trainee is deleted
    Then the request should be accepted
    And the history service should eventually record no hours for the trainer in "2025-03"

  @batch
  Scenario: One deletion reaches every trainer the trainee worked with
    Given another registered trainer
    And a training was added for the trainee with the trainer on "2025-03-14" lasting 5 hours
    And a training was added for the trainee with the second trainer on "2025-03-20" lasting 3 hours
    And the history service eventually records 3 hours for the second trainer in "2025-03"
    When the trainee is deleted
    Then the history service should eventually record no hours for the trainer in "2025-03"
    And the history service should eventually record no hours for the second trainer in "2025-03"

  @batch @aggregation
  Scenario: Several trainings with one trainer in one month collapse into a single reduction
    Given a training was added for the trainee with the trainer on "2025-03-03" lasting 4 hours
    And a training was added for the trainee with the trainer on "2025-03-14" lasting 6 hours
    And a training was added for the trainee with the trainer on "2025-03-27" lasting 2 hours
    And the history service eventually records 12 hours for the trainer in "2025-03"
    When the trainee is deleted
    Then the history service should eventually record no hours for the trainer in "2025-03"

  @batch @months
  Scenario: A batch removal reduces each month on its own
    Given a training was added for the trainee with the trainer on "2025-03-14" lasting 5 hours
    And a training was added for the trainee with the trainer on "2025-04-02" lasting 2 hours
    And the history service eventually records 2 hours for the trainer in "2025-04"
    When the trainee is deleted
    Then the history service should eventually record no hours for the trainer in "2025-03"
    And the history service should eventually record no hours for the trainer in "2025-04"

  @batch @partial
  Scenario: Hours from another trainee survive the deletion
    Given another registered trainee
    And a training was added for the trainee with the trainer on "2025-03-14" lasting 5 hours
    And a training was added for the second trainee with the trainer on "2025-03-20" lasting 3 hours
    And the history service eventually records 8 hours for the trainer in "2025-03"
    When the trainee is deleted
    Then the history service should eventually record 3 hours for the trainer in "2025-03"

  @batch @empty
  Scenario: Deleting a trainee with no trainings changes nothing
    Given another registered trainee
    And a training was added for the second trainee with the trainer on "2025-03-14" lasting 5 hours
    And the history service eventually records 5 hours for the trainer in "2025-03"
    When the trainee is deleted
    Then the request should be accepted
    And the history service should keep recording 5 hours for the trainer in "2025-03" for the next 3 seconds

  @error
  Scenario: Deleting a trainee who does not exist takes nobody's hours away
    Given a training was added for the trainee with the trainer on "2025-03-14" lasting 5 hours
    And the history service eventually records 5 hours for the trainer in "2025-03"
    When an unknown trainee is deleted
    Then the request should be rejected as not found
    And the history service should keep recording 5 hours for the trainer in "2025-03" for the next 3 seconds
