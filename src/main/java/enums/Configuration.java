package enums;

/**
 * enum to represent the different configuration options.
 */
public enum Configuration {
  ACTION_TIMEOUT("actionTimeout"),
  ASSERTION_TIMEOUT("assertionTimeout"),
  BASE_URL("baseURL"),
  BROWSER("browser"),
  ENVIRONMENT("environment"),
  HEADLESS("headless"),
  INDIVIDUAL_TEST_TIMEOUT("individualTestTimeoutInMinutes"),
  LOG_TO_FILE_ON_FAILURE("logToFileOnFailure"),
  LOG_TO_FILE_ALWAYS("logToFileAlways"),
  MINIMUM_LOG_LEVEL_CONSOLE("minimumLogLevelConsole"),
  MINIMUM_LOG_LEVEL_FILE("minimumLogLevelFile"),
  NAVIGATION_TIMEOUT("navigationTimeout"),
  TRACE_ALWAYS("traceAlways"),
  TRACE_ON_FAILURE("traceOnFailure"),
  VIDEO_ALWAYS("videoAlways");

  private final String property;

  Configuration(String property) {
    this.property = property;
  }

  /**
   * Gets the Property name as specified in the Configuration file.
   *
   * @return the Property name.
   */
  public String getProperty() {
    return this.property;
  }
}
