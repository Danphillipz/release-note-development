package playwright.Pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import playwright.managers.BrowserFactory;

import java.net.URI;
import java.util.Optional;

public class BasePage {

    private Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    public Page page() {
        return this.page;
    }

    public Response navigateTo() {
        return navigateTo(null, null);
    }

    public Response navigateTo(String route) {
        return navigateTo(route, null);
    }
    public Response navigateTo(String route, Page.NavigateOptions options) {
        URI uri = URI.create(String.valueOf(BrowserFactory.get().getConfiguration("baseURL")));
        String url = String.valueOf(uri.resolve(Optional.ofNullable(route).orElse("")));
        return this.page.navigate(url, options);
    }
}
