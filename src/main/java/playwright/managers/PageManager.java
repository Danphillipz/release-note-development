package playwright.managers;

import enums.PageDefinition;
import exceptions.ConfigurationException;
import io.cucumber.java.After;
import java.util.Arrays;

/**
 * Manages the current page definition within the application.
 */
@SuppressWarnings("java:S6548")
public class PageManager {

  private static PageManager instance;
  private final ThreadLocal<PageDefinition> currentPage = new ThreadLocal<>();

  /**
   * Returns the singleton instance of the PageManager.
   *
   * @return The PageManager instance.
   */
  public static synchronized PageManager instance() {
    if (instance == null) {
      instance = new PageManager();
    }
    return instance;
  }

  /**
   * Sets the current page.
   *
   * @param page The page definition to set.
   */
  public void setCurrentPage(PageDefinition page) {
    currentPage.set(page);
  }

  /**
   * Retrieves the current page the PageManager is actively on.
   *
   * @return The current page definition.
   */
  public PageDefinition getPage() {
    return currentPage.get();
  }

  /**
   * Sets the current page to the provided page definition and then returns that PageDefinition.
   *
   * @param page The page definition to set.
   * @return The corresponding page definition.
   */
  public PageDefinition getPage(PageDefinition page) {
    setCurrentPage(page);
    return getPage();
  }

  /**
   * Sets the current page to the provided page name and then returns the corresponding
   * PageDefinition.
   *
   * @param page The name of the page.
   * @return The corresponding page definition.
   */
  public PageDefinition getPage(String page) {
    return getPage(PageDefinition.get(page));
  }

  /**
   * Checks if a page exists with the specified name.
   *
   * @param pageName The name of the page to look for.
   * @return True if the page exists, false otherwise.
   */
  public Boolean exists(String pageName) {
    return Arrays.stream(PageDefinition.values())
        .anyMatch(x -> x.getName().equals(pageName));
  }

  /**
   * Cleans up resources after each scenario.
   */
  @After
  public void shutdown() {
    currentPage.remove();
  }

  /**
   * Asserts that the current page matches the specified PageDefinition.
   *
   * @param pageDefinition The expected page definition.
   * @param error          The error message to include in the exception if the assertion fails.
   * @throws ConfigurationException if the current page does not match the expected page
   *                                definition.
   */
  public void assertCurrentPageIs(PageDefinition pageDefinition, String error)
      throws ConfigurationException {
    if (!isCurrentPage(pageDefinition)) {
      throw new ConfigurationException(error);
    }
  }

  /**
   * Checks if the current page matches the specified PageDefinition.
   *
   * @param pageDefinition The expected page definition.
   * @return True if the current page matches the specified PageDefinition, false otherwise.
   */
  public boolean isCurrentPage(PageDefinition pageDefinition) {
    return PageManager.instance().getPage() == pageDefinition;
  }
}
