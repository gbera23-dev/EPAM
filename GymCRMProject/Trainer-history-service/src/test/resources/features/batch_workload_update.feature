@component @workload @batch-update
Feature: Applying a batch trainer workload update

  Background:
    Given the trainer history service is running with an embedded Mongo
    And the trainer workload collection is empty
    And the monthly summary collection is empty

  @aggregation
  Scenario: Several additions for one trainer and month are summed
    Given a trainer workload exists for "john.doe"
    When the following batch workload update is submitted to the facade
      | username | firstName | lastName | isActive | trainingDate | duration | actionType |
      | john.doe | John      | Doe      | true     | 2025-03-04   | 3        | ADD        |
      | john.doe | John      | Doe      | true     | 2025-03-11   | 4        | ADD        |
      | john.doe | John      | Doe      | true     | 2025-03-25   | 5        | ADD        |
    Then the monthly summary for "john.doe" for month "2025-03" should have duration 12
    And exactly 1 monthly summary should exist for "john.doe"

  @aggregation
  Scenario: Additions and deletions in one month net out to a single addition
    Given a trainer workload exists for "john.doe"
    When the following batch workload update is submitted to the facade
      | username | firstName | lastName | isActive | trainingDate | duration | actionType |
      | john.doe | John      | Doe      | true     | 2025-03-04   | 10       | ADD        |
      | john.doe | John      | Doe      | true     | 2025-03-11   | 3        | DELETE     |
    Then the monthly summary for "john.doe" for month "2025-03" should have duration 7

  @aggregation
  Scenario: A batch with a negative net total removes hours from the stored summary
    Given a trainer workload exists for "john.doe"
    And a monthly summary exists for "john.doe" for month "2025-03" with duration 20
    When the following batch workload update is submitted to the facade
      | username | firstName | lastName | isActive | trainingDate | duration | actionType |
      | john.doe | John      | Doe      | true     | 2025-03-04   | 10       | DELETE     |
      | john.doe | John      | Doe      | true     | 2025-03-11   | 3        | ADD        |
    Then the monthly summary for "john.doe" for month "2025-03" should have duration 13

  @aggregation
  Scenario: A net removal that empties the month deletes the summary
    Given a trainer workload exists for "john.doe"
    And a monthly summary exists for "john.doe" for month "2025-03" with duration 12
    When the following batch workload update is submitted to the facade
      | username | firstName | lastName | isActive | trainingDate | duration | actionType |
      | john.doe | John      | Doe      | true     | 2025-03-04   | 10       | DELETE     |
      | john.doe | John      | Doe      | true     | 2025-03-11   | 2        | DELETE     |
    Then no monthly summary should exist for "john.doe" for month "2025-03"

  @aggregation @error
  Scenario: A net removal larger than the stored duration is rejected
    Given a trainer workload exists for "john.doe"
    And a monthly summary exists for "john.doe" for month "2025-03" with duration 5
    When the following batch workload update is submitted to the facade
      | username | firstName | lastName | isActive | trainingDate | duration | actionType |
      | john.doe | John      | Doe      | true     | 2025-03-04   | 10       | DELETE     |
      | john.doe | John      | Doe      | true     | 2025-03-11   | 2        | ADD        |
    Then the update should be rejected with a message mentioning "training hours cannot become negative!"
    And the monthly summary for "john.doe" for month "2025-03" should have duration 5

  @aggregation @grouping
  Scenario: Different months of one trainer are not merged
    Given a trainer workload exists for "john.doe"
    When the following batch workload update is submitted to the facade
      | username | firstName | lastName | isActive | trainingDate | duration | actionType |
      | john.doe | John      | Doe      | true     | 2025-03-28   | 3        | ADD        |
      | john.doe | John      | Doe      | true     | 2025-04-02   | 4        | ADD        |
    Then the monthly summary for "john.doe" for month "2025-03" should have duration 3
    And the monthly summary for "john.doe" for month "2025-04" should have duration 4
    And exactly 2 monthly summary should exist for "john.doe"

  @aggregation @grouping
  Scenario: Different trainers in one month are not merged
    Given a trainer workload exists for "john.doe"
    And a trainer workload exists for "jane.smith"
    When the following batch workload update is submitted to the facade
      | username   | firstName | lastName | isActive | trainingDate | duration | actionType |
      | john.doe   | John      | Doe      | true     | 2025-03-04   | 3        | ADD        |
      | jane.smith | Jane      | Smith    | true     | 2025-03-04   | 4        | ADD        |
    Then the monthly summary for "john.doe" for month "2025-03" should have duration 3
    And the monthly summary for "jane.smith" for month "2025-03" should have duration 4

  @aggregation @grouping
  Scenario: One batch may mix known and unknown trainers
    Given a trainer workload exists for "john.doe"
    And no trainer workload exists for "jane.smith"
    When the following batch workload update is submitted to the facade
      | username   | firstName | lastName | isActive | trainingDate | duration | actionType |
      | john.doe   | John      | Doe      | true     | 2025-03-04   | 3        | ADD        |
      | jane.smith | Jane      | Smith    | true     | 2025-03-04   | 4        | ADD        |
    Then a trainer workload should exist for "jane.smith"
    And the workload for "jane.smith" should have first name "Jane" and last name "Smith"
    And the monthly summary for "jane.smith" for month "2025-03" should have duration 4

  @edge
  Scenario: An empty batch changes nothing
    Given a trainer workload exists for "john.doe"
    And a monthly summary exists for "john.doe" for month "2025-03" with duration 10
    When an empty batch workload update is submitted to the facade
    Then the monthly summary for "john.doe" for month "2025-03" should have duration 10
    And exactly 1 monthly summary should exist for "john.doe"

  @edge
  Scenario: A batch holding one request behaves like a single update
    Given a trainer workload exists for "john.doe"
    When the following batch workload update is submitted to the facade
      | username | firstName | lastName | isActive | trainingDate | duration | actionType |
      | john.doe | John      | Doe      | true     | 2025-03-04   | 6        | ADD        |
    Then the monthly summary for "john.doe" for month "2025-03" should have duration 6

  @edge
  Scenario: A batch whose additions and deletions cancel out leaves the duration unchanged
    Given a trainer workload exists for "john.doe"
    And a monthly summary exists for "john.doe" for month "2025-03" with duration 10
    When the following batch workload update is submitted to the facade
      | username | firstName | lastName | isActive | trainingDate | duration | actionType |
      | john.doe | John      | Doe      | true     | 2025-03-04   | 4        | ADD        |
      | john.doe | John      | Doe      | true     | 2025-03-11   | 4        | DELETE     |
    Then the monthly summary for "john.doe" for month "2025-03" should have duration 10
