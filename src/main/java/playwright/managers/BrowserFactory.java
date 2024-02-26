package playwright.managers;

import com.microsoft.playwright.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;

public class BrowserFactory {

    private BrowserContext context;
    private Page page;
    private Properties prop;
    private Browser browser;
    private static BrowserFactory instance;

    private BrowserFactory(String browser) {
        this.browser = launchBrowser(browser);
    }

    public static void start() {
        if (instance != null) {
            throw new Error("Browser factory is already running");
        }
        instance = new BrowserFactory("chrome");
    }

    public static BrowserFactory get() {
        return Optional.ofNullable(instance).orElseThrow(() -> new NullPointerException("Browser factory has not started"));
    }

    public static void endTest() {
        get().page.close();
        get().context.close();
        get().page = null;
        get().context = null;
    }
    public static void shutdown() {
        get().browser.close();
    }

    public BrowserContext context() {
        return context != null ? context : (context = browser.newContext());
    }

    public Page page() {
        return page != null ? page : (page = context().newPage());
    }

    private Browser launchBrowser(String browserName) {
        Playwright playwright = Playwright.create();

        switch (browserName.toLowerCase()) {
            case "chromium":
                return playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            case "firefox":
                //Force headless due to issue with browser not responding
                return playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
            case "safari":
                return playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
            case "chrome":
                return playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
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
