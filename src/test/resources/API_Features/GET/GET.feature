@GET
  Feature: GET request to retrieve user information


    @Smoke
    Scenario Outline: Retrieve data from server
      Given I set GET request with endpoint "<endpoint>" and resource path "<resource_path>"
      When I send GET request
      Then I receive valid HTTP response code 200

      Examples:
      | endpoint | resource_path |
      | baseURI  | resourcePath   |