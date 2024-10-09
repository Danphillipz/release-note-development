@isolated
Feature: Google Search Example (Isolated feature, will run sequentially)
  I want to confirm the functionality of the google search feature

  Scenario Outline: User searches google for <Search Phrase>

    Given I navigate to the "Home" page
    When I search for "<Search Phrase>"
    Then "<Expected Result>" should be in the search results

    @TestCaseId_5
    Examples:
      | Search Phrase | Expected Result |
      | playwright    | Playwright      |

