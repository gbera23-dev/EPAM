@e2e @workload @add-training
Feature: Adding a training reaches the trainer history service
  The main app publishes a workload message when a training is created.
  Every assertion here is made against the history service's own store,
  so a passing scenario means the message really crossed the broker.

  Background:
    Given a registered trainee
    And a registered trainer
    And the trainee is logged in

  @happy-path
  Scenario: The hours of a new training show up in the history service
    When a training is added for the trainee with the trainer on "2025-03-14" lasting 5 hours
    Then the request should be accepted
    And the history service should eventually record 5 hours for the trainer in "2025-03"

  @profile
  Scenario: The trainer's identity travels with the workload
    When a training is added for the trainee with the trainer on "2025-03-14" lasting 5 hours
    Then the history service should eventually record 5 hours for the trainer in "2025-03"
    And the history service should know the trainer's first name, last name and active flag

  @accumulation
  Scenario: Two trainings in the same month accumulate into one total
    When a training is added for the trainee with the trainer on "2025-03-03" lasting 4 hours
    And a training is added for the trainee with the trainer on "2025-03-27" lasting 6 hours
    Then the history service should eventually record 10 hours for the trainer in "2025-03"

  @accumulation
  Scenario: Trainings in different months are kept apart
    When a training is added for the trainee with the trainer on "2025-03-14" lasting 5 hours
    And a training is added for the trainee with the trainer on "2025-04-02" lasting 2 hours
    Then the history service should eventually record 5 hours for the trainer in "2025-03"
    And the history service should eventually record 2 hours for the trainer in "2025-04"

  @accumulation
  Scenario: A month boundary does not leak hours into the neighbouring month
    When a training is added for the trainee with the trainer on "2025-03-31" lasting 7 hours
    Then the history service should eventually record 7 hours for the trainer in "2025-03"
    And the history service should record no hours for the trainer in "2025-04"

  @isolation
  Scenario: Each trainer accumulates only their own hours
    Given another registered trainer
    When a training is added for the trainee with the trainer on "2025-03-14" lasting 5 hours
    And a training is added for the trainee with the second trainer on "2025-03-14" lasting 3 hours
    Then the history service should eventually record 5 hours for the trainer in "2025-03"
    And the history service should eventually record 3 hours for the second trainer in "2025-03"

  @error
  Scenario: A training rejected for an unknown trainee credits nobody
    When a training is added for an unknown trainee with the trainer on "2025-03-14" lasting 5 hours
    Then the request should be rejected as not found
    And the history service should record no hours for the trainer in "2025-03" for the next 3 seconds

  @error
  Scenario: A training rejected for an unknown trainer credits nobody
    When a training is added for the trainee with an unknown trainer on "2025-03-14" lasting 5 hours
    Then the request should be rejected as not found
    And the history service should record no hours for the trainer in "2025-03" for the next 3 seconds
