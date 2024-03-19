package playwright.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import devices.Device;
import exceptions.ConfigurationException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Singleton class to manage Playwright instances and browser configurations.
 */
public class PlaywrightManager {

  private static final String CHROMIUM_BROWSER_NAME = "chromium";
  private static final String FIREFOX_BROWSER_NAME = "firefox";
  private static final String WEBKIT_BROWSER_NAME = "webkit";
  private static final String CHROME_BROWSER_NAME = "chrome";
  private static final String EDGE_BROWSER_NAME = "edge";
  private static PlaywrightManager instance;
  private final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
  private final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
  private final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();
  private final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
  private final ConfigurationManager.PropertyHandler getProperty =
      ConfigurationManager.get().configuration();
  private final Gson gson = new Gson();
  private Map<String, Device> deviceInformation;
  private Device device;

  private PlaywrightManager(String browser) {
    if (!isSupportedBrowser(browser)) {
      throw new ConfigurationException(
          String.format("%s browser is not supported", browser));
    }
  }

  /**
   * Retrieves the singleton instance of PlaywrightManager.
   *
   * @return The singleton instance of PlaywrightManager.
   * @throws NullPointerException If the browser factory has not been started.
   */
  public static PlaywrightManager get() {
    return Optional.ofNullable(instance)
        .orElseThrow(() -> new NullPointerException("Playwright manager has not started"));
  }

  /**
   * Convenience/readability method for interacting with the PlaywrightManager instance.
   *
   * @return The PlaywrightManager instance.
   */
  public static PlaywrightManager perform() {
    return get();
  }

  /**
   * Starts the PlaywrightManager by creating a new instance of it.
   */
  public static void startPlaywright(String browser) {
    instance = new PlaywrightManager(browser);
  }

  /**
   * Retrieves the current browser instance for the current thread.
   *
   * @return The current browser instance.
   */
  public Browser browser() {
    return this.browserThreadLocal.get();
  }

  /**
   * Checks if the current device is a mobile device.
   *
   * @return True if the device is mobile, false otherwise.
   */
  public boolean isMobile() {
    return device != null && device.isMobile();
  }

  /**
   * Retrieves the current browser context for the current thread. If not yet instantiated, this
   * will first call {@link PlaywrightManager#setContextThreadLocal(BrowserContext)} with a new
   * BrowserContext which loads state from storage.
   *
   * @return The current browser context.
   */
  public BrowserContext browserContext() {
    if (this.contextThreadLocal.get() == null) {
      setContextThreadLocal(
          browser()
              .newContext(
                  new Browser.NewContextOptions()
                      .setStorageStatePath(Paths.get("./src/test/resources/state.json"))));
    }
    return this.contextThreadLocal.get();
  }

  /**
   * Configures the browser context with user-defined configurations.
   *
   * @param context The context to be configured.
   */
  private void setContextThreadLocal(BrowserContext context) {
    var timeout = getProperty.asInteger("timeout");
    if (timeout != null) {
      context.setDefaultTimeout(timeout);
    }
    var navigationTimeout = getProperty.asInteger("navigationTimeout");
    if (navigationTimeout != null) {
      context.setDefaultNavigationTimeout(navigationTimeout);
    }
    var assertionTimeout = getProperty.asInteger("assertionTimeout");
    if (assertionTimeout != null) {
      PlaywrightAssertions.setDefaultAssertionTimeout(assertionTimeout);
    }
    if (getProperty.asFlag("trace", false)) {
      context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
    }
    this.contextThreadLocal.set(context);
  }

  /**
   * Retrieves the current page for the current thread. If one does not exist, this will first get
   * the browser context with {@link PlaywrightManager#browserContext()} and create a new page.
   *
   * @return The current page.
   */
  public Page page() {
    if (this.pageThreadLocal.get() == null) {
      this.pageThreadLocal.set(browserContext().newPage());
    }
    return this.pageThreadLocal.get();
  }

  /**
   * Retrieves the current Playwright instance for the current thread.
   *
   * @return The current Playwright instance.
   */
  public Playwright playwright() {
    return this.playwrightThreadLocal.get();
  }

  /**
   * Launches the browser specified with the {@code browser} property defined within configuration
   * or CLI properties.
   */
  public void launchBrowser() {
    String browser = getProperty.asRequiredString("browser");
    playwrightThreadLocal.set(Playwright.create());
    browserThreadLocal.set(
        Optional.ofNullable(getBrowser(browser)).orElseGet(() -> getCustomDevice(browser)));
    if (device != null) {
      configureCustomDevice();
    }
  }

  /**
   * Ends the current test session.
   */
  public void endTest() {
    page().close();
    browserContext().close();
    pageThreadLocal.remove();
    contextThreadLocal.remove();
  }

  /**
   * Shuts down the browser.
   */
  public void shutdown() {
    browser().close();
    browserThreadLocal.remove();
    playwrightThreadLocal.remove();
  }

  /**
   * Checks to see whether the specified browser is supported by the framework.
   *
   * @param browser Browser to check
   * @return true if browser supported
   * @throws ConfigurationException if an error occurs while reading the custom device descriptors
   */
  public boolean isSupportedBrowser(String browser) {
    if (!Arrays.asList(CHROME_BROWSER_NAME, EDGE_BROWSER_NAME, CHROMIUM_BROWSER_NAME,
            FIREFOX_BROWSER_NAME, WEBKIT_BROWSER_NAME)
        .contains(browser.toLowerCase())) {
      try {
        deviceInformation =
            gson.fromJson(
                Files.readString(Path.of("./src/main/java/devices/deviceDescriptors.json")),
                new TypeToken<Map<String, Device>>() {
                }.getType());
      } catch (IOException e) {
        throw new ConfigurationException("Unable to read the device description json", e);
      }
      return deviceInformation.get(browser) != null;
    }
    return true;
  }

  /**
   * Retrieves the browser instance based on the specified browser name.
   *
   * @param browser The name of the browser.
   * @return The browser instance.
   */
  private Browser getBrowser(String browser) {
    BrowserType.LaunchOptions options =
        new BrowserType.LaunchOptions().setHeadless(getProperty.asFlag("headless", true));
    return switch (browser.toLowerCase()) {
      case CHROMIUM_BROWSER_NAME -> playwright().chromium().launch(options);
      case FIREFOX_BROWSER_NAME -> playwright().firefox().launch(options);
      case WEBKIT_BROWSER_NAME -> playwright().webkit().launch(options);
      case CHROME_BROWSER_NAME ->
          playwright().chromium().launch(options.setChannel("chrome")); //NOSONAR
      case EDGE_BROWSER_NAME -> playwright().chromium().launch(options.setChannel("msedge"));
      default -> null;
    };
  }

  /**
   * Retrieves a custom device instance based on the specified browser name.
   *
   * <p>Reads in the <b>deviceDescriptors.json</b> list and deserialises the JSON object
   * corresponding to the specified browser into a {@link Device}.
   *
   * @param browser The name of the browser.
   * @return The browser instance.
   */
  private Browser getCustomDevice(String browser) {
    device = deviceInformation.get(browser);
    return Optional.ofNullable(getBrowser(device.getDefaultBrowserType()))
        .orElseThrow(
            () -> new NoSuchElementException(String.format("%s Browser unsupported", browser)));
  }

  /**
   * Configures the custom device settings for the browser context.
   */
  private void configureCustomDevice() {
    Browser.NewContextOptions contextOptions =
        new Browser.NewContextOptions()
            .setStorageStatePath(Paths.get("./src/test/resources/state.json"));
    setContextThreadLocal(
        browser()
            .newContext(
                contextOptions
                    .setUserAgent(device.getUserAgent())
                    .setViewportSize(
                        Optional.ofNullable(device.getViewport())
                            .orElse(
                                contextOptions.viewportSize == null //NOSONAR
                                    ? null
                                    : contextOptions.viewportSize.orElse(null)))
                    .setScreenSize(
                        Optional.ofNullable(device.getScreen()).orElse(contextOptions.screenSize))
                    .setDeviceScaleFactor(device.getDeviceScaleFactor())
                    .setIsMobile(device.isMobile())
                    .setHasTouch(device.hasTouch())));
  }
}
