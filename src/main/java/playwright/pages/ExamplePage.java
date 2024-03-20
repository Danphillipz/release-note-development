package playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import playwright.managers.PlaywrightManager;

/**
 * An example page object extending from the core BasePage.
 */
public class ExamplePage extends BasePage {

  /**
   * Creates an example page and registers a locator handler to dismiss the settings popup when it
   * appears.
   */
  public ExamplePage(Page page) {
    super(page);
    var settingsPopUp = page().getByRole(AriaRole.BUTTON,
        new Page.GetByRoleOptions().setName("Got it").setExact(true));
    page().addLocatorHandler(settingsPopUp, settingsPopUp::click);
  }

  /**
   * Searches for the specified search term.
   *
   * @param searchTerm Value to search for
   */
  public void performSearch(String searchTerm) {
    var searchBox =
        PlaywrightManager.get().isMobile()
            ? page().getByRole(AriaRole.TEXTBOX)
            : page().getByTitle("Search");
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
    return page()
        .getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(searchTerm).setExact(true))
        .first();
  }
}
