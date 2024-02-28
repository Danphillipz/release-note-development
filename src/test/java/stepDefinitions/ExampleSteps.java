package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import playwright.Pages.ExamplePage;
import playwright.managers.PlaywrightManager;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ExampleSteps {

    ExamplePage homePage;

    public ExampleSteps() {
        homePage = new ExamplePage(PlaywrightManager.get().page());
    }

    @Given("I am on the home page")
    public void i_am_on_the_home_page() {
        homePage.navigateTo();
    }

    @When("I search for {string}")
    public void i_search_for(String searchTerm) {
        homePage.performSearch(searchTerm);
    }

    @Then("{string} should be in the search results")
    public void should_be_in_the_search_results(String searchTerm) {
        assertThat(homePage.getResult(searchTerm)).isVisible();
    }

}
