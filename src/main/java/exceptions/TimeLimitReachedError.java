package exceptions;

/**
 * An error indicating that a given time limit has been reached.
 */
public class TimeLimitReachedError extends Error {

  /**
   * Constructs a new TimeLimitReachedError with a formatted detail message.
   *
   * @param s      The detail message.
   * @param format The format arguments.
   */
  public TimeLimitReachedError(String s, Object... format) {
    super(String.format(s, format));
  }

  /**
   * Constructs a new TimeLimitReachedError with the specified detail message and cause.
   *
   * @param s     The detail message.
   * @param cause The cause of the error.
   */
  public TimeLimitReachedError(String s, Throwable cause) {
    super(s, cause);
  }
}
