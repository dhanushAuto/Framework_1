@POST
  Feature: CREATE requests


    @Regression
    Scenario: Create a new resource
      When I set POST request with body "POST"
      Then I send the POST request
      Then I receive valid response code 201


