package playwright.interfaces;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Page.NavigateOptions;
import com.microsoft.playwright.options.WaitUntilState;
import enums.Configuration;
import exceptions.UnexpectedStatusCodeException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import org.opentest4j.AssertionFailedError;
import playwright.managers.ConfigurationManager;
import utils.TimeLimit;

/**
 * Interface for pages to implement when they can be navigated to directly via a URL.
 */
public interface NavigateTo extends GetPage {

  /**
   * Gets the URL extension which the implementing page can be navigated to with. For example
   * {baseURL}+{extension}
   *
   * @return the URL extension
   */
  String getPageUrlExtension();

  /**
   * Navigates to the base URL specified in the configuration.
   */
  default void navigateTo() {
    navigateTo(getPageUrlExtension(), new NavigateOptions().setWaitUntil(WaitUntilState.LOAD));
  }

  /**
   * Navigates to the specified route under the base URL specified in the configuration.
   *
   * @param route The route to navigate to.
   */
  default void navigateTo(String route) {
    navigateTo(route, new NavigateOptions().setWaitUntil(WaitUntilState.LOAD));
  }

  /**
   * Navigates to the specified route under the base URL specified in the configuration, with
   * additional navigation options. Method will retry navigation once if the URL does not match the
   * expected URL.  If URL still does not match exception is thrown.
   *
   * @param route   The route to navigate to.
   * @param options Additional navigation options.
   */
  default void navigateTo(String route, NavigateOptions options) {
    String url = String.format("%s%s", ConfigurationManager.get().environment().asString(
            Configuration.BASE_URL),
        Optional.ofNullable(route).orElse(""));
    AtomicReference<Integer> navigationResponse = new AtomicReference<>();
    TimeLimit.of(Duration.ofMillis(ConfigurationManager.get().configuration()
            .asInteger(Configuration.NAVIGATION_TIMEOUT, 20_000)))
        .doWhileFalse(() -> {
          var response = this.getPage().navigate(url, options);
          if (route != null && !route.isEmpty()) {
            var regex = route.replace("/", "\\/");
            var pattern = Pattern.compile(regex);
            var matcher = pattern.matcher(getPage().url());
            if (!matcher.find()) {
              return false;
            }
            try {
              assertThat(getPage()).hasURL(pattern);
            } catch (AssertionFailedError failed) {
              return false;
            }
          }
          if (response != null) {
            navigationResponse.set(response.status());
          }
          if (navigationResponse.get() != null && response == null) {
            return false;
          }
          return response == null || response.ok();
        }, () -> new UnexpectedStatusCodeException(200, navigationResponse.get(), route));
    waitForPageReadyState();
  }

  /**
   * For pages where me must wait for a specific page state before we can interact with it, this
   * method can be overwridden and will automatically be called upon {@link #navigateTo()}.
   */
  default void waitForPageReadyState() {
  }
}
