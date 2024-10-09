@Smoke
Feature: I want to test the retry logic
  I want to confirm the functionality of the google search feature

  @playwright @poc @examples
  Scenario Outline: User searches google for <Search Phrase>

    Given I navigate to the "Home" page
    When I search for "<Search Phrase>"
    Then "<Expected Result>" should be in the search results

    @TestCaseId_1
    Examples:
      | Search Phrase | Expected Result |
      | playwright    | Playwright      |

    @TestCaseId_2
    Examples:
      | Search Phrase | Expected Result                |
      | Ensono stacks | Ensono Stacks \| Ensono Stacks |


