@smoke @diagnostic
Feature: Framework Feature Discovery

  Scenario: Verify feature discovery
    Given the BDD test framework is running
    When it scans the features folder
    Then it should discover and execute this scenario
