package playwright.managers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import devices.Device;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class PlaywrightManager {

    private static PlaywrightManager instance;
    private final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private ConfigurationManager.PropertyHandler getProperty = ConfigurationManager.get().configuration();

    private Device device;

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

    public boolean isMobile() {
        return device == null ? false : device.isMobile();
    }

    public BrowserContext browserContext() {
        if (this.contextThreadLocal.get() == null) {
            setContextThreadLocal(browser().newContext(new Browser.NewContextOptions().setStorageStatePath(Paths.get("./src/test/resources/state.json"))));
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
     */
    private void setContextThreadLocal(BrowserContext context) {
        var timeout = getProperty.asInteger("timeout");
        if (timeout != null) context.setDefaultTimeout(timeout);
        var navigationTimeout = getProperty.asInteger("navigationTimeout");
        if (navigationTimeout != null) context.setDefaultNavigationTimeout(navigationTimeout);
        var assertionTimeout = getProperty.asInteger("assertionTimeout");
        if (assertionTimeout != null) PlaywrightAssertions.setDefaultAssertionTimeout(assertionTimeout);
        if (getProperty.asFlag("trace", false)) {
            context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
        }
        this.contextThreadLocal.set(context);
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
        browserThreadLocal.set(Optional.ofNullable(getBrowser(browser)).orElseGet(() -> getCustomDevice(browser)));
        if (device != null) {
            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions().setStorageStatePath(Paths.get("./src/test/resources/state.json"));
            setContextThreadLocal(browser().newContext(contextOptions
                    .setUserAgent(device.getUserAgent())
                    .setViewportSize(Optional.ofNullable(device.getViewport()).orElse(contextOptions.viewportSize == null ? null : contextOptions.viewportSize.orElse(null)))
                    .setScreenSize(Optional.ofNullable(device.getScreen()).orElse(contextOptions.screenSize))
                    .setDeviceScaleFactor(device.getDeviceScaleFactor())
                    .setIsMobile(device.isMobile())
                    .setHasTouch(device.hasTouch())));
        }
    }

    private Browser getBrowser(String browser) {
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(
                getProperty.asFlag("headless", true));
        return switch (browser.toLowerCase()) {
            case "chromium" -> playwright().chromium().launch(options);
            case "firefox" -> playwright().firefox().launch(options);
            case "webkit" -> playwright().webkit().launch(options);
            case "chrome" -> playwright().chromium().launch(options.setChannel("chrome"));
            case "edge" -> playwright().chromium().launch(options.setChannel("msedge"));
            default -> null;
        };
    }

    private Browser getCustomDevice(String browser) {
        Gson gson = new Gson();
        try {
            // Checking for null so other threads don't reread the file.
            if (device == null) {
                Map deviceInformation = gson.fromJson(Files.readString(
                        Path.of("./src/main/java/devices/deviceDescriptors.json")), Map.class);
                device = gson.fromJson(gson.toJson(deviceInformation.get(browser)), Device.class);
            }
            return Optional.ofNullable(getBrowser(device.getDefaultBrowserType())).orElseThrow(
                    () -> new NoSuchElementException(String.format("%s Browser unsupported", browser)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
