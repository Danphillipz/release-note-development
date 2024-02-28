Feature: Search Function
  I want to confirm the function of the Search feature

  @example
  Scenario: User makes a search
    Given I am on the Next home page
    When I make a search for "T-Shirt"
    Then I should be presented with the correct results

 