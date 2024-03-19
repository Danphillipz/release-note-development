package stepdefinitions;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import playwright.managers.PlaywrightManager;
import playwright.pages.ExamplePage;

/**
 * Example Step Definitions.
 */
public class ExampleSteps {

  ExamplePage homePage;

  public ExampleSteps() {
    homePage = new ExamplePage(PlaywrightManager.get().page());
  }

  @Given("I am on the home page")
  public void navigateToHomePage() {
    homePage.navigateTo();
  }

  @When("I search for {string}")
  public void searchFor(String searchTerm) {
    homePage.performSearch(searchTerm);
  }

  @Then("{string} should be in the search results")
  public void shouldBeInTheSearchResults(String searchTerm) {
    assertThat(homePage.getResult(searchTerm)).isVisible();
  }
}
