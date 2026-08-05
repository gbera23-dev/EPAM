@component @authentication
Feature: Authenticating users and managing credentials

  Background:
    Given the database is empty
    And a training type "Yoga" exists
    And a trainee is registered with first name "John" and last name "Doe"

  @login
  Scenario: Correct credentials return a token
    When "John.Doe" logs in with the correct password
    Then the response status should be 200
    And the response should contain a token
    And the token should be accepted on a protected endpoint

  @login
  Scenario: A wrong password is rejected
    When "John.Doe" logs in with password "wrong-password"
    Then the login should be rejected
    And the response should not contain a token

  @login
  Scenario: An unknown username is rejected
    When "no.such.user" logs in with password "anything"
    Then the login should be rejected

  @access
  Scenario: A protected endpoint without a token is rejected
    When the trainee profile of "John.Doe" is requested without an authorization header
    Then the response status should be 403

  @access
  Scenario: A protected endpoint with a malformed token is rejected
    When the trainee profile of "John.Doe" is requested with token "not-a-real-token"
    Then the response status should be 401

  @lockout
  Scenario: Three failed attempts lock the account
    Given "John.Doe" has failed to log in 3 times
    When "John.Doe" logs in with the correct password
    Then the login should be rejected because the account is temporarily locked

  @lockout
  Scenario: Two failed attempts do not lock the account
    Given "John.Doe" has failed to log in 2 times
    When "John.Doe" logs in with the correct password
    Then the response status should be 200
    And the response should contain a token

  @lockout
  Scenario: A successful login clears earlier failed attempts
    Given "John.Doe" has failed to log in 2 times
    And "John.Doe" logs in with the correct password
    When "John.Doe" fails to log in 2 more times
    And "John.Doe" logs in with the correct password
    Then the response status should be 200

  @logout
  Scenario: A token stops working after logout
    Given "John.Doe" is logged in
    When "John.Doe" logs out
    Then the response status should be 204
    And the previous token should be rejected on a protected endpoint

  @logout
  Scenario: Logging out without an authorization header is forbidden
    When a logout is submitted without an authorization header
    Then the response status should be 403

  @password-change
  Scenario: Changing the password replaces the old one
    Given "John.Doe" is logged in
    When "John.Doe" changes the password to "new-password"
    Then the response status should be 200
    And "John.Doe" should be able to log in with "new-password"
    And the original password should no longer be accepted for "John.Doe"

  @password-change @error
  Scenario: Changing the password with the wrong current password is rejected
    Given "John.Doe" is logged in
    When "John.Doe" changes the password to "new-password" using current password "wrong-password"
    Then the response status should be 403
    And the original password should still be accepted for "John.Doe"

  @password-change @error
  Scenario: Changing the password of an unknown user is rejected
    Given "John.Doe" is logged in
    When a password change is submitted for "no.such.user"
    Then the response status should be 404

  @password-change @security
  Scenario: The new password is stored encoded
    Given "John.Doe" is logged in
    When "John.Doe" changes the password to "new-password"
    Then the stored password for "John.Doe" should not be "new-password"
