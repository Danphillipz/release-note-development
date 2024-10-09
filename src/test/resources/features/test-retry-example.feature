@Smoke
Feature: Showcasing the retry functionality within the azure pipeline
  The first time this feature runs it should fail, rerunning should pass.

  Take a look at the test retry section in the ReadMe to understand the test retry mechanism.

  @retry
  Scenario: Test should pass on rerun

    Given I should only pass on a retry
