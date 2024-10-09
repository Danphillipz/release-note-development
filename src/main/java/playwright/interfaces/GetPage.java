package playwright.interfaces;

import com.microsoft.playwright.Page;

/**
 * Interface for Page objects to return their playwright {@link com.microsoft.playwright.Page}.
 */
public interface GetPage {

  /**
   * Retrieves the Playwright Page associated with this page object.
   *
   * @return The Playwright Page object.
   */
  Page getPage();
}
