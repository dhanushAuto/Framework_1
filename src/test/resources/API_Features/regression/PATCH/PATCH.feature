@PATCH
  Feature: PATCH requests

    @Regression
    Scenario: Partially update an existing resource
      When I set PATCH request with body "PATCH"
      Then I send the PATCH request
      Then I receive valid response code 200
