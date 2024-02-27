package playwright.managers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class ConfigurationManager {

    private static ConfigurationManager instance;
    private PropertyHandler environment, configuration;

    private ConfigurationManager() {
        configuration = new PropertyHandler("./src/test/resources/config/configuration.properties");
        environment = new PropertyHandler(String.format("./src/test/resources/config/%s.env.properties", configuration.strictString("environment")));
    }

    public static ConfigurationManager get() {
        return instance == null ? instance = new ConfigurationManager() : instance;
    }

    public PropertyHandler environment() {
        return environment;
    }

    public PropertyHandler configuration() {
        return configuration;
    }


    public class PropertyHandler {
        private Properties properties;

        public PropertyHandler(String path) {
            try {
                FileInputStream ip = new FileInputStream(path);
                properties = new Properties();
                properties.load(ip);
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

        public Boolean flag(String property) {
            var configurationValue = getConfiguration(property, false);
            return configurationValue == null ? null : Boolean.parseBoolean(String.valueOf(configurationValue));
        }

        public boolean flag(String property, boolean defaultValue) {
            return Optional.ofNullable(flag(property)).orElse(defaultValue);
        }

        public boolean strictFlag(String property) throws NoSuchFieldError {
            return Boolean.parseBoolean(String.valueOf(getConfiguration(property, true)));
        }

        public String string(String property) {
            var configurationValue = getConfiguration(property, false);
            return configurationValue == null ? null : String.valueOf(configurationValue);
        }

        public String string(String property, String defaultValue) {
            return Optional.ofNullable(string(property)).orElse(defaultValue);
        }

        public String strictString(String property) throws NoSuchFieldError {
            return String.valueOf(getConfiguration(property, true));
        }

        public Integer integer(String property) {
            var configurationValue = getConfiguration(property, false);
            return configurationValue == null ? null : Integer.valueOf(String.valueOf(configurationValue));
        }

        public Integer integer(String property, Integer defaultValue) {
            return Optional.ofNullable(integer(property)).orElse(defaultValue);
        }

        public Integer strictInteger(String property) throws NoSuchFieldError {
            return Integer.valueOf(String.valueOf(getConfiguration(property, true)));
        }

    }

}
