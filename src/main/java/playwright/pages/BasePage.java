package playwright.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import java.net.URI;
import java.util.Optional;
import playwright.managers.ConfigurationManager;

/** A base class representing a web page. */
public class BasePage {

  private final Page page;

  /**
   * Constructs a BasePage with the provided Playwright Page.
   *
   * @param page The Playwright Page object representing the web page.
   */
  public BasePage(Page page) {
    this.page = page;
  }

  /**
   * Retrieves the Playwright Page associated with this BasePage.
   *
   * @return The Playwright Page object.
   */
  public Page page() {
    return this.page;
  }

  /**
   * Navigates to the base URL specified in the configuration.
   *
   * @return The response received after navigating to the base URL.
   */
  public Response navigateTo() {
    return navigateTo(null, null);
  }

  /**
   * Navigates to the specified route under the base URL specified in the configuration.
   *
   * @param route The route to navigate to.
   * @return The response received after navigating to the specified route.
   */
  public Response navigateTo(String route) {
    return navigateTo(route, null);
  }

  /**
   * Navigates to the specified route under the base URL specified in the configuration, with
   * additional navigation options.
   *
   * @param route The route to navigate to.
   * @param options Additional navigation options.
   * @return The response received after navigating to the specified route.
   */
  public Response navigateTo(String route, Page.NavigateOptions options) {
    URI uri = URI.create(ConfigurationManager.get().environment().asString("baseURL"));
    String url = String.valueOf(uri.resolve(Optional.ofNullable(route).orElse("")));
    return this.page.navigate(url, options);
  }
}
