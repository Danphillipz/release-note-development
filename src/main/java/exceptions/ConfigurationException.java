package exceptions;

/**
 * Custom Unchecked Exception related to framework configuration issues.
 */
public class ConfigurationException extends RuntimeException {

  /**
   * Constructs a new ConfigurationException with a formatted detail message.
   *
   * @param message The detail message.
   * @param format  The format arguments.
   */
  public ConfigurationException(String message, Object... format) {
    super(String.format(message, format));
  }

  /**
   * Constructs a new ConfigurationException with the specified detail message and cause.
   *
   * @param message The detail message.
   * @param cause   The cause of the exception.
   */
  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
