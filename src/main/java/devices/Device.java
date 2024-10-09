package devices;

import com.microsoft.playwright.options.ScreenSize;
import com.microsoft.playwright.options.ViewportSize;

/**
 * A record representing device characteristics for Playwright automation.
 */
public record Device(
    String userAgent,
    ScreenSize screen,
    ViewportSize viewport,
    Double deviceScaleFactor,
    Boolean isMobile,
    Boolean hasTouch,
    String defaultBrowserType
) {

}
