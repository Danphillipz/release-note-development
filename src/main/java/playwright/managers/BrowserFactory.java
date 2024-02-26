package playwright.managers;

import com.microsoft.playwright.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;

public class BrowserFactory {

    private final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>(); //For Parallel execution
    private final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>(); //For Parallel execution
    private final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static BrowserFactory instance;

    private Properties prop;

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
        browserThreadLocal.set(launchBrowser("chrome"));
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

    private Browser launchBrowser(String browserName) {
        playwrightThreadLocal.set(Playwright.create());

        return switch (browserName.toLowerCase()) {
            case "chromium" -> playwright().chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            case "firefox" ->
                //Force headless due to issue with browser not responding
                    playwright().firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
            case "safari" -> playwright().webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
            case "chrome" ->
                    playwright().chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
            default -> throw new NoSuchElementException("Browser unsupported");
        };
    }

    public Properties config() {
        try {
            FileInputStream ip = new FileInputStream("./src/test/resources/config/config.properties");
            prop = new Properties();
            prop.load(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
