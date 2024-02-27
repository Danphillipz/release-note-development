package playwright.managers;

import com.microsoft.playwright.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;

public class BrowserFactory {

    private final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static BrowserFactory instance;

    private Properties browserProperties;

    private BrowserFactory() {
        try {
            FileInputStream ip = new FileInputStream("./src/test/resources/config/browser.config.properties");
            browserProperties = new Properties();
            browserProperties.load(ip);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

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
            this.contextThreadLocal.set(browser().newContext());
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
        launchBrowser(String.valueOf(getConfiguration("browser")).toLowerCase());
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
        boolean headless = getFlag("headless");
        playwrightThreadLocal.set(Playwright.create());
        browserThreadLocal.set(switch (browser) {
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

    public Object getConfiguration(String property) {
        return Optional.ofNullable(browserProperties.get(property)).orElseThrow(() -> new NoSuchFieldError(String.format("No browser configuration value found for %s", property)));
    }

    public boolean getFlag(String property) {
        return Boolean.parseBoolean(String.valueOf(getConfiguration(property)));
    }

}
