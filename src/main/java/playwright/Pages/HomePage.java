package playwright.Pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class HomePage extends BasePage {

    private final String searchBox = "//*[@id='header-big-screen-search-box']";

    public HomePage(Page page) {
        super(page);
    }

    public void clearPopUp() {
        page().getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Accept All Cookies")).click();
    }

    public void performSearch(String searchTerm) {
        page().fill(searchBox, searchTerm);
        String searchBoxSubmit = "#header-search-form > button";
        page().click(searchBoxSubmit);
    }

    public String getResultHeader() {
        String val = page().getByTestId("plp-product-title-text").textContent();
        val = val.replace("\"", "");
        return val;
    }
}
