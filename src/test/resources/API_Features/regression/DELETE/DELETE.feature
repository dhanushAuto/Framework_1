@DELETE
  Feature: DELETE requests


    @Regression
    Scenario: Delete the resource from server
      When send a DELETE request
      Then Response code should be 200


