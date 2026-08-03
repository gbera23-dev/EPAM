@unit @security @jwt @not-wired
Feature: JWT token validation

  Background:
    Given the service is configured with a JWT signing key

  @extraction
  Scenario: The username is read from the subject claim
    Given a JWT token signed with the configured key for subject "john.doe"
    When the username is extracted from the token
    Then the extracted username should be "john.doe"

  @extraction @error
  Scenario: A token without a subject claim is rejected
    Given a JWT token signed with the configured key with no subject claim
    When the username is extracted from the token
    Then the operation should fail with a "JwtException"

  @extraction @error
  Scenario Outline: A token that cannot be parsed is rejected
    Given a "<tokenKind>" JWT token
    When the username is extracted from the token
    Then the operation should fail with a "UserCannotBeAuthorizedException"

    Examples:
      | tokenKind                    |
      | malformed                    |
      | empty                        |
      | signed with a different key  |
      | with a tampered payload      |


  @validation @error
  Scenario Outline: An untrusted token cannot be validated
    Given a "<tokenKind>" JWT token
    When the token is checked for validity
    Then the operation should fail with a "UserCannotBeAuthorizedException"

    Examples:
      | tokenKind                    |
      | malformed                    |
      | empty                        |
      | signed with a different key  |
      | with a tampered payload      |