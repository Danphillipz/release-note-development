package playwright.Pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;

public class ExamplePage extends BasePage {

    public ExamplePage(Page page) {
        super(page);
    }

    @Override
    public Response navigateTo() {
        var response = super.navigateTo();
        page().getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Accept all")).click();
        return response;
    }

    public void performSearch(String searchTerm) {
        var seachBox = page().getByTitle("Search");
        seachBox.fill(searchTerm);
        seachBox.press("Enter");

    }

    public Locator getResult(String searchTerm) {
        return page().getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(searchTerm));
    }
}
