package playwright.managers;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

import java.util.NoSuchElementException;
import java.util.Optional;

public class PlaywrightManager {

    private static PlaywrightManager instance;
    private final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private ConfigurationManager.PropertyHandler getProperty = ConfigurationManager.get().configuration();

    private PlaywrightManager() {
    }

    public static PlaywrightManager get() {
        return Optional.ofNullable(instance).orElseThrow(() -> new NullPointerException("Browser factory has not started"));
    }

    public static PlaywrightManager perform() {
        return get();
    }

    public static void startPlaywright() {
        instance = new PlaywrightManager();
    }

    public Browser browser() {
        return this.browserThreadLocal.get();
    }

    public BrowserContext browserContext() {
        if (this.contextThreadLocal.get() == null) {
            BrowserContext context = browser().newContext();
            this.contextThreadLocal.set(configureTest(context));
        }
        return this.contextThreadLocal.get();
    }

    /**
     * Configures the browser context with user defined configurations
     * The following properties are pulled from the browser configuration
     * - timeout - Default timeout
     * - navigationTimeout - Timeout for browser navigations
     * - trace - Whether to enable tracing for failed tests
     *
     * @param context - The context to be configured
     * @return the configured context
     */
    private BrowserContext configureTest(BrowserContext context) {
        var timeout = getProperty.asInteger("timeout");
        if (timeout != null) context.setDefaultTimeout(timeout);
        var navigationTimeout = getProperty.asInteger("navigationTimeout");
        if (navigationTimeout != null) context.setDefaultNavigationTimeout(navigationTimeout);
        var assertionTimeout = getProperty.asInteger("assertionTimeout");
        if (assertionTimeout != null) PlaywrightAssertions.setDefaultAssertionTimeout(assertionTimeout);
        if (getProperty.asFlag("trace", false)) {
            context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
        }
        return context;
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

    public void launchTest() {
        launchBrowser(getProperty.asRequiredString("browser"));
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
        playwrightThreadLocal.set(Playwright.create());
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(
                getProperty.asFlag("headless", true));
        browserThreadLocal.set(switch (browser.toLowerCase()) {
            case "chromium" -> playwright().chromium().launch(options);
            case "firefox" -> playwright().firefox().launch(options);
            case "safari" -> playwright().webkit().launch(options);
            case "chrome" -> playwright().chromium().launch(options.setChannel("chrome"));
            case "edge" -> playwright().chromium().launch(options.setChannel("msedge"));
            default -> throw new NoSuchElementException(String.format("%s Browser unsupported", browser));
        });
    }
}
