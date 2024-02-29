package playwright.managers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class ConfigurationManager {

    private static ConfigurationManager instance;
    private final PropertyHandler configuration;
    private PropertyHandler environment;

    private ConfigurationManager() {
        configuration = new PropertyHandler("./src/test/resources/config/configuration.properties");
    }

    public static ConfigurationManager get() {
        return instance == null ? instance = new ConfigurationManager() : instance;
    }

    public PropertyHandler environment() {
        return environment == null ? environment = new PropertyHandler(
                String.format("./src/test/resources/config/%s.env.properties",
                        configuration.asRequiredString("environment")))
                : environment;
    }

    public PropertyHandler configuration() {
        return configuration;
    }


    public class PropertyHandler {
        private final Properties properties;

        public PropertyHandler(String path) {
            try {
                properties = new Properties();
                properties.load(new FileInputStream(path));
            } catch (IOException e) {
                throw new Error(e);
            }
        }

        private Object getConfiguration(String property, boolean strict) throws NoSuchFieldError {
            Object config = System.getProperty(property);
            if (strict) {
                return Optional.ofNullable(config).orElse(Optional.ofNullable(properties.get(property)).orElseThrow(() -> new NoSuchFieldError(String.format("No configuration value found for %s", property))));
            }
            return Optional.ofNullable(config).orElse(properties.get(property));

        }

        public Boolean asFlag(String property) {
            var configurationValue = getConfiguration(property, false);
            return configurationValue == null ? null : Boolean.parseBoolean(String.valueOf(configurationValue));
        }

        public boolean asFlag(String property, boolean defaultValue) {
            return Optional.ofNullable(asFlag(property)).orElse(defaultValue);
        }

        public boolean asRequiredFlag(String property) throws NoSuchFieldError {
            return Boolean.parseBoolean(String.valueOf(getConfiguration(property, true)));
        }

        public String asString(String property) {
            var configurationValue = getConfiguration(property, false);
            return configurationValue == null ? null : String.valueOf(configurationValue);
        }

        public String asString(String property, String defaultValue) {
            return Optional.ofNullable(asString(property)).orElse(defaultValue);
        }

        public String asRequiredString(String property) throws NoSuchFieldError {
            return String.valueOf(getConfiguration(property, true));
        }

        public Integer asInteger(String property) {
            var configurationValue = getConfiguration(property, false);
            return configurationValue == null ? null : Integer.valueOf(String.valueOf(configurationValue));
        }

        public Integer asInteger(String property, Integer defaultValue) {
            return Optional.ofNullable(asInteger(property)).orElse(defaultValue);
        }

        public Integer asRequiredInteger(String property) throws NoSuchFieldError {
            return Integer.valueOf(String.valueOf(getConfiguration(property, true)));
        }

    }

}
