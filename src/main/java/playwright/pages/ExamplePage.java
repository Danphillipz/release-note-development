package playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import playwright.interfaces.NavigateTo;
import playwright.managers.PlaywrightManager;

/**
 * An example page object extending from the core BasePage.
 */
public class ExamplePage extends BasePage implements NavigateTo {

  /**
   * Creates an example page and registers a locator handler to dismiss the settings popup when it
   * appears.
   */
  public ExamplePage() {
    super();
    var settingsPopUp = getPage().getByRole(AriaRole.BUTTON,
        new Page.GetByRoleOptions().setName("Accept all").setExact(true));
    getPage().addLocatorHandler(settingsPopUp, Locator::click);
  }

  /**
   * Searches for the specified search term.
   *
   * @param searchTerm Value to search for
   */
  public void performSearch(String searchTerm) {
    var searchBox =
        PlaywrightManager.get().isMobile()
            ? getPage().getByRole(AriaRole.TEXTBOX)
            : getPage().getByTitle("Search");
    searchBox.fill(searchTerm);
    searchBox.press("Enter");
  }

  /**
   * Finds the first heading which matches the search term.
   *
   * @param searchTerm Heading value to search for
   * @return first locator matching the search terms
   */
  public Locator getResult(String searchTerm) {
    return getPage()
        .getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(searchTerm).setExact(true))
        .first();
  }

  @Override
  public String getPageUrlExtension() {
    return "";
  }
}
