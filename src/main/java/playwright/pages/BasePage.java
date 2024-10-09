package playwright.pages;

import com.microsoft.playwright.Page;
import playwright.interfaces.GetPage;
import playwright.managers.PlaywrightManager;

/**
 * A base class representing a web page.
 */
public class BasePage implements GetPage {

  @Override
  public Page getPage() {
    return PlaywrightManager.get().page();
  }

}
