@TestCaseId_12345
Feature: Google Search Example 2
  I want to confirm the functionality of the google search feature

  @playwright @TestCaseId_3
  Scenario: User searches google for playwright
    Given I navigate to the "Home" page
    When I search for "playwright"
    Then "Playwright" should be in the search results

  @stacks @TestCaseId_4
  Scenario: User searches google for Ensono stacks
    Given I navigate to the "Home" page
    When I search for "Ensono stacks"
    Then "Ensono Stacks | Ensono Stacks" should be in the search results

  @TestCaseId_5 @blocked @smoke
  Scenario: Blocked scenario which will be omitted from pipeline runs
    Given I navigate to the "Home" page
    When I search for "Ensono stacks"
    Then "This test will fail" should be in the search results
