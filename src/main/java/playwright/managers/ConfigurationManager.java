package playwright.managers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/** Singleton class to manage Environment and Testing configuration. */
public class ConfigurationManager {

  private static ConfigurationManager instance;
  private final PropertyHandler configuration;
  private PropertyHandler environment;

  /** Constructs a ConfigurationManager and initialises the configuration PropertyHandler. */
  private ConfigurationManager() {
    configuration = new PropertyHandler("./src/test/resources/config/configuration.properties");
  }

  /**
   * Retrieves the singleton instance of ConfigurationManager.
   *
   * @return The singleton instance of ConfigurationManager.
   */
  public static ConfigurationManager get() {
    return instance == null ? instance = new ConfigurationManager() : instance;
  }

  /**
   * Retrieves the environment PropertyHandler.
   *
   * @return The environment PropertyHandler.
   */
  public PropertyHandler environment() {
    return environment == null
        ? environment =
            new PropertyHandler(
                String.format(
                    "./src/test/resources/config/%s.env.properties",
                    configuration.asRequiredString("environment")))
        : environment;
  }

  /**
   * Retrieves the configuration PropertyHandler.
   *
   * @return The configuration PropertyHandler.
   */
  public PropertyHandler configuration() {
    return configuration;
  }

  /** A utility class which provides mechanisms to retrieve configuration data. */
  public class PropertyHandler {
    private final Properties properties;

    /**
     * Constructs a PropertyHandler and loads properties from the given path.
     *
     * @param path The path to the properties file.
     */
    public PropertyHandler(String path) {
      try {
        properties = new Properties();
        properties.load(new FileInputStream(path));
      } catch (IOException e) {
        throw new Error(e);
      }
    }

    /**
     * Retrieves a configuration property with the given name.
     *
     * @param property The name of the property.
     * @param strict Indicates whether to throw an error if the property is not found.
     * @return The value of the property.
     * @throws NoSuchFieldError If the property is not found and strict mode is enabled.
     */
    private Object getConfiguration(String property, boolean strict) throws NoSuchFieldError {
      Object config = System.getProperty(property);
      if (strict) {
        return Optional.ofNullable(config)
            .orElse(
                Optional.ofNullable(properties.get(property))
                    .orElseThrow(
                        () ->
                            new NoSuchFieldError(
                                String.format("No configuration value found for %s", property))));
      }
      return Optional.ofNullable(config).orElse(properties.get(property));
    }

    /**
     * Retrieves a configuration property as a boolean.
     *
     * @param property The name of the property.
     * @return The boolean value of the property, or null if not found.
     */
    public Boolean asFlag(String property) {
      var configurationValue = getConfiguration(property, false);
      return configurationValue == null
          ? null
          : Boolean.parseBoolean(String.valueOf(configurationValue));
    }

    /**
     * Retrieves a configuration property as a boolean but returns the default value if no matching
     * property found.
     *
     * @param property The name of the property.
     * @param defaultValue The default value.
     * @return The boolean value of the property, or the default value if not found.
     */
    public boolean asFlag(String property, boolean defaultValue) {
      return Optional.ofNullable(asFlag(property)).orElse(defaultValue);
    }

    /**
     * Retrieves a required configuration property as a boolean.
     *
     * @param property The name of the property.
     * @return The boolean value of the property.
     * @throws NoSuchFieldError If the property is not found.
     */
    public boolean asRequiredFlag(String property) throws NoSuchFieldError {
      return Boolean.parseBoolean(String.valueOf(getConfiguration(property, true)));
    }

    /**
     * Retrieves a configuration property as a string.
     *
     * @param property The name of the property.
     * @return The string value of the property, or null if not found.
     */
    public String asString(String property) {
      var configurationValue = getConfiguration(property, false);
      return configurationValue == null ? null : String.valueOf(configurationValue);
    }

    /**
     * Retrieves a configuration property as a string but returns the default value if no matching
     * property found.
     *
     * @param property The name of the property.
     * @param defaultValue The default value.
     * @return The string value of the property, or the default value if not found.
     */
    public String asString(String property, String defaultValue) {
      return Optional.ofNullable(asString(property)).orElse(defaultValue);
    }

    /**
     * Retrieves a required configuration property as a string.
     *
     * @param property The name of the property.
     * @return The string value of the property.
     * @throws NoSuchFieldError If the property is not found.
     */
    public String asRequiredString(String property) throws NoSuchFieldError {
      return String.valueOf(getConfiguration(property, true));
    }

    /**
     * Retrieves a configuration property as an integer.
     *
     * @param property The name of the property.
     * @return The integer value of the property, or null if not found.
     */
    public Integer asInteger(String property) {
      var configurationValue = getConfiguration(property, false);
      return configurationValue == null
          ? null
          : Integer.valueOf(String.valueOf(configurationValue));
    }

    /**
     * Retrieves a configuration property as an integer but returns the default value if no matching
     * property found.
     *
     * @param property The name of the property.
     * @param defaultValue The default value.
     * @return The integer value of the property, or the default value if not found.
     */
    public Integer asInteger(String property, Integer defaultValue) {
      return Optional.ofNullable(asInteger(property)).orElse(defaultValue);
    }

    /**
     * Retrieves a required configuration property as an integer.
     *
     * @param property The name of the property.
     * @return The integer value of the property.
     * @throws NoSuchFieldError If the property is not found.
     */
    public Integer asRequiredInteger(String property) throws NoSuchFieldError {
      return Integer.valueOf(String.valueOf(getConfiguration(property, true)));
    }
  }
}
