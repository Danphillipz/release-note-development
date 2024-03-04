package devices;

import com.microsoft.playwright.options.ScreenSize;
import com.microsoft.playwright.options.ViewportSize;

/**
 * A class representing device characteristics for Playwright automation. Object values are populated through deserialisation.
 */
public class Device {
    private String userAgent;
    private ScreenSize screen;
    private ViewportSize viewport;
    private double deviceScaleFactor;
    private boolean isMobile;
    private boolean hasTouch;
    private String defaultBrowserType;

    /**
     * Retrieves the user agent string of the device.
     *
     * @return The user agent string.
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Retrieves the screen size of the device.
     *
     * @return The screen size.
     */
    public ScreenSize getScreen() {
        return screen;
    }

    /**
     * Retrieves the viewport size of the device.
     *
     * @return The viewport size.
     */
    public ViewportSize getViewport() {
        return viewport;
    }

    /**
     * Retrieves the device scale factor.
     *
     * @return The device scale factor.
     */
    public double getDeviceScaleFactor() {
        return deviceScaleFactor;
    }

    /**
     * Checks if the device is a mobile device.
     *
     * @return True if the device is mobile, false otherwise.
     */
    public boolean isMobile() {
        return isMobile;
    }

    /**
     * Checks if the device has touch capability.
     *
     * @return True if the device has touch capability, false otherwise.
     */
    public boolean hasTouch() {
        return hasTouch;
    }

    /**
     * Retrieves the default browser type of the device.
     *
     * @return The default browser type.
     */
    public String getDefaultBrowserType() {
        return defaultBrowserType;
    }
}
