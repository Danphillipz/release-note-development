package playwright.managers;

import com.microsoft.playwright.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;

public class BrowserFactory {

    private ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>(); //For Parallel execution
    private ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>(); //For Parallel execution
    private ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static BrowserFactory instance;

    private Properties prop;

    private BrowserFactory() {}

    public static BrowserFactory get() {
        return Optional.ofNullable(instance).orElseThrow(() -> new NullPointerException("Browser factory has not started"));
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

    public static void start() {
        if (instance != null) {
            throw new Error("Browser factory is already running");
        }
        instance = new BrowserFactory();
    }

    public void launchTest() {
        browserThreadLocal.set(launchBrowser("chrome"));
    }


    public static void endTest() {
        get().page().close();
        get().browserContext().close();
        get().pageThreadLocal.set(null);
        get().contextThreadLocal.set(null);
    }

    public static void shutdown() {
        get().browser().close();
    }

    private Browser launchBrowser(String browserName) {
        playwrightThreadLocal.set(Playwright.create());

        switch (browserName.toLowerCase()) {
            case "chromium":
                return playwright().chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            case "firefox":
                //Force headless due to issue with browser not responding
                return playwright().firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
            case "safari":
                return playwright().webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
            case "chrome":
                return playwright().chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
            default:
                throw new NoSuchElementException("Browser unsupported");
        }
    }

    public Properties config() {
        try {
            FileInputStream ip = new FileInputStream("./src/test/resources/config/config.properties");
            prop = new Properties();
            prop.load(ip);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
