package stepdefinitions;

import io.cucumber.java.en.Given;
import playwright.interfaces.NavigateTo;
import playwright.managers.PageManager;

/**
 * Step definitions for common steps which drive pages through the {@link PageManager} and interface
 * casting.
 */
@SuppressWarnings({"checkstyle:MissingJavadocMethod", "checkstyle:MissingJavadocType"})
public class CommonSteps {

  @Given("I navigate to the {string} page")
  public void navigateToThePage(String pageName) {
    PageManager.instance().getPage(pageName).as(NavigateTo.class).navigateTo();
  }

}
