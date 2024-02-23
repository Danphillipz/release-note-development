package stepDefinition;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import playwright.Pages.HomePage;
import playwright.managers.BrowserFactory;

import static org.junit.Assert.assertEquals;

public class HomePageSteps {
	
  HomePage homePage;
	String searchTerm;
	
	public HomePageSteps() {
		homePage = new HomePage(BrowserFactory.get().page());
	}
	
  @Given("I am on the Next home page")
  public void goToHomePage(){
	  homePage.navigateTo();
	  homePage.clearPopUp();
	  assertEquals("Next Official Site: Online Fashion, Kids Clothes & Homeware",homePage.page().title());
  }

  @When("I make a search for {string}")
  public void I_make_a_search_for(String keyword){
		searchTerm = keyword;
	  homePage.performSearch(keyword);
  }

  @Then("I should be presented with the correct results")
  public void validateSearchResult(){
	  String actual = homePage.getResultHeader();
		assertEquals(actual.toLowerCase(),searchTerm.toLowerCase());
  }

}
