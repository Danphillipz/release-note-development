package playwright.managers;

import com.microsoft.playwright.*;

import java.util.NoSuchElementException;
import java.util.Optional;

public class BrowserFactory {

    private final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static BrowserFactory instance;

    private BrowserFactory() {}

    public static BrowserFactory get() {
        return Optional.ofNullable(instance).orElseThrow(() -> new NullPointerException("Browser factory has not started"));
    }

    public static BrowserFactory perform() {
        return get();
    }

    public Browser browser() {
        return this.browserThreadLocal.get();
    }

    public BrowserContext browserContext() {
        if (this.contextThreadLocal.get() == null) {
            BrowserContext context = browser().newContext();
            var timeout = ConfigurationManager.get().configuration().integer("timeout");
            var navigationTimeout = ConfigurationManager.get().configuration().integer("navigationTimeout");
            if(timeout != null) {
                context.setDefaultTimeout(timeout);
            }
            if(navigationTimeout != null) {
                context.setDefaultNavigationTimeout(navigationTimeout);
            }
            this.contextThreadLocal.set(context);
        }
        return this.contextThreadLocal.get();
    }

    public Page page() {
        if (this.pageThreadLocal.get() == null) {
            this.pageThreadLocal.set(browserContext().newPage());
        }
        return this.pageThreadLocal.get();
    }

    public Playwright playwright() {
        return this.playwrightThreadLocal.get();
    }

    public static void startFactory() {
        if (instance != null) {
            throw new Error("Browser factory is already running");
        }
        instance = new BrowserFactory();
    }

    public void launchTest() {
        launchBrowser(ConfigurationManager.get().configuration().strictString("browser"));
    }


    public void endTest() {
        page().close();
        browserContext().close();
        pageThreadLocal.set(null);
        contextThreadLocal.set(null);
    }

    public void shutdown() {
        browser().close();
    }

    private void launchBrowser(String browser) {
        boolean headless = ConfigurationManager.get().configuration().flag("headless", true);
        playwrightThreadLocal.set(Playwright.create());
        browserThreadLocal.set(switch (browser.toLowerCase()) {
            case "chromium" -> playwright().chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
            case "firefox" ->
                //Force headless due to issue with browser not responding
                    playwright().firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
            case "safari" -> playwright().webkit().launch(new BrowserType.LaunchOptions().setHeadless(headless));
            case "chrome" ->
                    playwright().chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(headless));
            default -> throw new NoSuchElementException(String.format("%s Browser unsupported", browser));
        });
    }
}
