package stepdefinitions;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import playwright.pages.ExamplePage;

@SuppressWarnings({"checkstyle:MissingJavadocMethod", "checkstyle:MissingJavadocType"})
public class ExampleSteps {

  private final ExamplePage homePage;

  public ExampleSteps() {
    homePage = new ExamplePage();
  }

  @When("I search for {string}")
  public void searchFor(String searchTerm) {
    homePage.performSearch(searchTerm);
  }

  @Then("{string} should be in the search results")
  public void shouldBeInTheSearchResults(String searchTerm) {
    assertThat(homePage.getResult(searchTerm)).isVisible();
  }
  
  @Given("I should only pass on a retry")
  public void shouldOnlyPassOnRetry() {
    if (Files.exists(Path.of("target/failedScenarios.txt"))) {
      return;
    }
    Assertions.fail("First time running so we fail");
  }
}
