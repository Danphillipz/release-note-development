Feature: Google Search Example 2
  I want to confirm the functionality of the google search feature

  @playwright
  Scenario: User searches google for playwright
    Given I am on the home page
    When I search for "playwright"
    Then "Playwright" should be in the search results

  @stacks
  Scenario: User searches google for Ensono stacks
    Given I am on the home page
    When I search for "Ensono stacks"
    Then "Ensono Stacks | Ensono Stacks" should be in the search results

