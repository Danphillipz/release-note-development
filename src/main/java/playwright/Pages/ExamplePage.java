package playwright.Pages;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;
import playwright.managers.PlaywrightManager;

import java.nio.file.Paths;

public class ExamplePage extends BasePage {

    public ExamplePage(Page page) {
        super(page);
    }

    public void performSearch(String searchTerm) {
        var searchBox = PlaywrightManager.get().isMobile() ? page().getByRole(AriaRole.TEXTBOX) :
                page().getByTitle("Search");
        searchBox.fill(searchTerm);
        searchBox.press("Enter");
    }

    public Locator getResult(String searchTerm) {
        return page().getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(searchTerm).setExact(true)).first();
    }
}
