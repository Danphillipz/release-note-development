package devices;

import com.microsoft.playwright.options.ScreenSize;
import com.microsoft.playwright.options.ViewportSize;

public class Device {
    private String userAgent;
    private ScreenSize screen;

    private ViewportSize viewport;
    private double deviceScaleFactor;
    private boolean isMobile;
    private boolean hasTouch;
    private String defaultBrowserType;

    public String getUserAgent() {
        return userAgent;
    }

    public ScreenSize getScreen() {
        return screen;
    }

    public ViewportSize getViewport() {
        return viewport;
    }

    public double getDeviceScaleFactor() {
        return deviceScaleFactor;
    }

    public boolean isMobile() {
        return isMobile;
    }

    public boolean hasTouch() {
        return hasTouch;
    }

    public String getDefaultBrowserType() {
        return defaultBrowserType;
    }

    public class Size {
        public int width, height;

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}

