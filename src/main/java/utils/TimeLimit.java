package utils;

import exceptions.ConfigurationException;
import exceptions.TimeLimitReachedError;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Utility class used to set time limits and check if a time limit has been reached.
 */
public class TimeLimit {

  private Instant end;
  private final Duration time;

  /**
   * Create a TimeLimit with a specified {@link Duration}.
   *
   * @param time {@link Duration} to set as the limit.
   */
  public TimeLimit(Duration time) {
    this.time = time;
    reset();
  }

  /**
   * Creates a TimeLimit with the specified duration.
   *
   * @param time The duration to set as the limit.
   * @return A new TimeLimit instance.
   */
  public static TimeLimit of(Duration time) {
    return new TimeLimit(time);
  }

  /**
   * Resets the timer.
   */
  public void reset() {
    end = Clock.systemDefaultZone().instant().plus(getDuration());
  }

  /**
   * Checks to see whether the time limit has been reached.
   *
   * @return true if the limit has not yet been reached.
   */
  public boolean timeLeft() {
    return !end.isBefore(Clock.systemDefaultZone().instant());
  }

  /**
   * Checks to see whether the time limit has been reached, if it has been reached, an
   * {@link TimeLimitReachedError} is thrown.
   *
   * @return true if the limit has not been reached.
   * @throws TimeLimitReachedError if the time limit has been reached.
   */
  public boolean timeLeftElseThrow() throws TimeLimitReachedError {
    if (!timeLeft()) {
      throw new TimeLimitReachedError("The specified time limit of %d seconds has been reached",
          getDuration().toSeconds());
    }
    return true;
  }

  /**
   * Checks to see whether the time limit has been reached, if it has been reached, an
   * {@link TimeLimitReachedError} is thrown with the provided Throwable as the reason.
   *
   * @param throwableSupplier Supplier which will retrieve the cause of the TimeLimitReachedError.
   * @return true if the limit has not been reached.
   * @throws TimeLimitReachedError if the time limit has been reached.
   */
  public boolean timeLeftElseThrow(Supplier<Throwable> throwableSupplier)
      throws TimeLimitReachedError {
    if (!timeLeft()) {
      throw new TimeLimitReachedError(
          "The specified time limit of %d seconds has been reached".formatted(
              getDuration().toSeconds()), throwableSupplier.get());
    }
    return true;
  }

  /**
   * Given a {@link Supplier} which supplies a Boolean value, this function will continue to invoke
   * the supplier method until it returns false, or until the time limit has been reached where an
   * error will be thrown {@link #timeLeftElseThrow()}.
   *
   * @param method {@link Supplier} which must return a Boolean value. i.e. return true if the
   *               method needs to be called again as X process has not finished or reached the
   *               desired result.
   * @throws TimeLimitReachedError if the time limit has been reached.
   */
  public void doWhileTrue(BooleanSupplier method) throws TimeLimitReachedError {
    do {
      timeLeftElseThrow();
    } while (method.getAsBoolean());
  }

  /**
   * Works the same as {@link #doWhileTrue(BooleanSupplier)} however accepts an additional Throwable
   * parameter to state the cause of the TimeLimitReachedException.
   *
   * @param method            {@link BooleanSupplier} which must return a Boolean value.
   * @param throwableSupplier A supplier which retrieves the cause of the
   *                          TimeLimitReachedException.
   * @throws TimeLimitReachedError if the time limit has been reached.
   */
  public void doWhileTrue(BooleanSupplier method, Supplier<Throwable> throwableSupplier)
      throws TimeLimitReachedError {
    do {
      timeLeftElseThrow(throwableSupplier);
    } while (method.getAsBoolean());
  }

  /**
   * Works like {@link #doWhileTrue(BooleanSupplier)} however the evaluation is inversed.
   *
   * @param method {@link BooleanSupplier} which must return a Boolean value.
   * @throws TimeLimitReachedError if the time limit has been reached.
   */
  public void doWhileFalse(BooleanSupplier method) throws TimeLimitReachedError {
    do {
      timeLeftElseThrow();
    } while (!method.getAsBoolean());
  }

  /**
   * Works the same as {@link #doWhileFalse(BooleanSupplier)} however accepts an additional
   * Throwable parameter to state the cause of the TimeLimitReachedException.
   *
   * @param method            {@link BooleanSupplier} which must return a Boolean value.
   * @param throwableSupplier A supplier which retrieves the cause of the
   *                          TimeLimitReachedException.
   * @throws TimeLimitReachedError if the time limit has been reached.
   */
  public void doWhileFalse(BooleanSupplier method, Supplier<Throwable> throwableSupplier)
      throws TimeLimitReachedError {
    do {
      timeLeftElseThrow(throwableSupplier);
    } while (!method.getAsBoolean());
  }

  /**
   * Given an object supplier this will continue to execute the supplier method, waiting for the
   * poll amount in between each attempt.
   *
   * @param classCast         Class object indicating the type of returned object.
   * @param pollTime          Duration to wait on each call to the supplier.
   * @param supplier          The method to call which will return an object of type T.
   * @param throwableSupplier A supplier which retrieves the cause of the
   *                          TimeLimitReachedException.
   * @param <T>               The type of object being returned.
   * @return The required object.
   * @throws TimeLimitReachedError if the time limit has been reached.
   */
  public <T> T poll(Class<T> classCast,
      Duration pollTime,
      Supplier<T> supplier,
      Supplier<Throwable> throwableSupplier)
      throws TimeLimitReachedError {
    do {
      var supplied = supplier.get();
      if (supplied != null) {
        return supplied;
      }
      wait(pollTime);
    } while (timeLeftElseThrow(throwableSupplier));
    return null;
  }

  /**
   * Waits for a given duration in the current Thread.
   *
   * @param waitAmount How long to wait (sleep the thread).
   */
  public void wait(Duration waitAmount) {
    try {
      Thread.sleep(waitAmount.toMillis());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ConfigurationException("An error occurred while waiting", e);
    }
  }

  /**
   * Waits for the full amount of time specified for this TimeLimit.
   */
  public void waitFullDuration() {
    wait(getDuration());
  }

  /**
   * Gets the duration of this TimeLimit.
   *
   * @return The duration.
   */
  public Duration getDuration() {
    return this.time;
  }
}
