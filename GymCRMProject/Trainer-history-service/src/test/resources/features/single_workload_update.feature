@component @workload @single-update
Feature: Applying a single trainer workload update

  Background:
    Given the trainer history service is running with an embedded Mongo
    And the trainer workload collection is empty
    And the monthly summary collection is empty

  @creation
  Scenario: An update for an unknown trainer creates the workload and its first summary
    When the following workload update is submitted to the facade
      | username | firstName | lastName | isActive | trainingDate | duration | actionType |
      | john.doe | John      | Doe      | true     | 2025-03-14   | 5        | ADD        |
    Then a trainer workload should exist for "john.doe"
    And the workload for "john.doe" should have first name "John" and last name "Doe"
    And the workload for "john.doe" should be active
    And the monthly summary for "john.doe" for month "2025-03" should have duration 5

  @creation
  Scenario: An update for a known trainer does not create a second workload
    Given a trainer workload exists with the following data
      | username | firstName | lastName | isActive |
      | john.doe | John      | Doe      | true     |
    When the following workload update is submitted to the facade
      | username | firstName | lastName | isActive | trainingDate | duration | actionType |
      | john.doe | John      | Doe      | true     | 2025-03-14   | 5        | ADD        |
    Then the monthly summary for "john.doe" for month "2025-03" should have duration 5

  @creation
  Scenario: An inactive trainer is stored with an inactive status
    When the following workload update is submitted to the facade
      | username   | firstName | lastName | isActive | trainingDate | duration | actionType |
      | jane.smith | Jane      | Smith    | false    | 2025-03-14   | 3        | ADD        |
    Then a trainer workload should exist for "jane.smith"
    And the workload for "jane.smith" should be inactive

  @add
  Scenario: Adding hours to an existing month increases the stored duration
    Given a trainer workload exists for "john.doe"
    And a monthly summary exists for "john.doe" for month "2025-03" with duration 10
    When an "ADD" workload update is submitted for "john.doe" on "2025-03-20" with duration 5
    Then the monthly summary for "john.doe" for month "2025-03" should have duration 15
    And exactly 1 monthly summary should exist for "john.doe"

  @add @month-normalisation
  Scenario: Two updates for different days of one month share a single summary
    Given a trainer workload exists for "john.doe"
    When an "ADD" workload update is submitted for "john.doe" on "2025-03-01" with duration 4
    And an "ADD" workload update is submitted for "john.doe" on "2025-03-31" with duration 6
    Then the monthly summary for "john.doe" for month "2025-03" should have duration 10
    And exactly 1 monthly summary should exist for "john.doe"

  @add @month-normalisation
  Scenario: Updates for adjacent months are kept in separate summaries
    Given a trainer workload exists for "john.doe"
    When an "ADD" workload update is submitted for "john.doe" on "2025-03-31" with duration 4
    And an "ADD" workload update is submitted for "john.doe" on "2025-04-01" with duration 6
    Then the monthly summary for "john.doe" for month "2025-03" should have duration 4
    And the monthly summary for "john.doe" for month "2025-04" should have duration 6
    And exactly 2 monthly summary should exist for "john.doe"

  @add @month-normalisation
  Scenario: The same month in different years is kept apart
    Given a trainer workload exists for "john.doe"
    When an "ADD" workload update is submitted for "john.doe" on "2024-03-10" with duration 4
    And an "ADD" workload update is submitted for "john.doe" on "2025-03-10" with duration 6
    Then the monthly summary for "john.doe" for month "2024-03" should have duration 4
    And the monthly summary for "john.doe" for month "2025-03" should have duration 6

  @add
  Scenario: Hours of two trainers never mix
    Given a trainer workload exists for "john.doe"
    And a trainer workload exists for "jane.smith"
    When an "ADD" workload update is submitted for "john.doe" on "2025-03-10" with duration 4
    And an "ADD" workload update is submitted for "jane.smith" on "2025-03-10" with duration 9
    Then the monthly summary for "john.doe" for month "2025-03" should have duration 4
    And the monthly summary for "jane.smith" for month "2025-03" should have duration 9

  @delete
  Scenario: Deleting hours reduces the stored duration
    Given a trainer workload exists for "john.doe"
    And a monthly summary exists for "john.doe" for month "2025-03" with duration 10
    When a "DELETE" workload update is submitted for "john.doe" on "2025-03-20" with duration 4
    Then the monthly summary for "john.doe" for month "2025-03" should have duration 6

  @delete
  Scenario: Deleting the last remaining hours removes the summary
    Given a trainer workload exists for "john.doe"
    And a monthly summary exists for "john.doe" for month "2025-03" with duration 10
    When a "DELETE" workload update is submitted for "john.doe" on "2025-03-20" with duration 10
    Then no monthly summary should exist for "john.doe" for month "2025-03"
    And a trainer workload should exist for "john.doe"

  @delete
  Scenario: Deleting affects only the month named by the training date
    Given a trainer workload exists for "john.doe"
    And a monthly summary exists for "john.doe" for month "2025-03" with duration 10
    And a monthly summary exists for "john.doe" for month "2025-04" with duration 8
    When a "DELETE" workload update is submitted for "john.doe" on "2025-04-15" with duration 3
    Then the monthly summary for "john.doe" for month "2025-04" should have duration 5
    And the monthly summary for "john.doe" for month "2025-03" should have duration 10

  @error
  Scenario: Deleting more hours than are recorded leaves the summary untouched
    Given a trainer workload exists for "john.doe"
    And a monthly summary exists for "john.doe" for month "2025-03" with duration 10
    When a "DELETE" workload update is submitted for "john.doe" on "2025-03-20" with duration 11
    Then the update should be rejected with a message mentioning "training hours cannot become negative!"
    And the monthly summary for "john.doe" for month "2025-03" should have duration 10

  @error
  Scenario: Deleting from a month with no summary is rejected
    Given a trainer workload exists for "john.doe"
    And no monthly summary exists for "john.doe" for month "2025-03"
    When a "DELETE" workload update is submitted for "john.doe" on "2025-03-20" with duration 1
    Then the update should be rejected
    And no monthly summary should exist for "john.doe" for month "2025-03"

  @error @validation
  Scenario Outline: A negative duration is rejected for either action type
    Given a trainer workload exists for "john.doe"
    And a monthly summary exists for "john.doe" for month "2025-03" with duration 10
    When a "<actionType>" workload update is submitted for "john.doe" on "2025-03-20" with duration <duration>
    Then the update should be rejected with a message mentioning "Number of hours cannot be negative!"
    And the monthly summary for "john.doe" for month "2025-03" should have duration 10

    Examples:
      | actionType | duration |
      | ADD        | -1       |
      | DELETE     | -5       |