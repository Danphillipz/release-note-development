
Feature: Search Function
  I want to confirm the function of the Search feature in this second feature file

  Scenario: User makes a search on this second feature file
    Given I am on the Next home page
    When I make a search for "T-Shirt"
    Then I should be presented with the correct results

  @example
  Scenario: User makes another search on this second feature file
    Given I am on the Next home page
    When I make a search for "T-Shirt"
    Then I should be presented with the correct results
