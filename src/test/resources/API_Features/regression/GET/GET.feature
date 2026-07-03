@GET
  Feature: GET request to retrieve user information


    @Regression
    Scenario: Retrieve data from server
      When I send GET request
      Then I receive valid HTTP response code 200
