package plugins;

import enums.Configuration;
import exceptions.TimeLimitReachedError;
import io.cucumber.plugin.event.TestCase;
import java.time.Duration;
import playwright.managers.ConfigurationManager;
import utils.TimeLimit;

/**
 * The TestMonitor class is responsible for monitoring the execution of individual test cases within
 * the Cucumber framework, enforcing a time limit on their execution.
 *
 * <p>This class implements the Runnable interface, allowing it to run in a separate thread
 * dedicated to monitoring the test case's execution time. If the test exceeds the specified time
 * limit, the monitor attempts to interrupt the test thread.</p>
 *
 * <p>Key features include:</p>
 * <ul>
 *   <li>Dynamic time limit configuration based on settings from the ConfigurationManager.</li>
 *   <li>Graceful handling of time limit breaches by attempting to interrupt the test thread.</li>
 *   <li>Shutdown flag to safely terminate monitoring when the test completes or is canceled.</li>
 * </ul>
 */
public class TestMonitor implements Runnable {

  private final TimeLimit limit = TimeLimit.of(Duration.ofMinutes(
      ConfigurationManager.get().configuration()
          .asRequiredInteger(Configuration.INDIVIDUAL_TEST_TIMEOUT))
  );

  private final Thread testThread;
  private final TestCase test;
  private boolean shutdown = false;

  private TestMonitor(Thread testThread, TestCase test) {
    this.testThread = testThread;
    this.test = test;
  }

  /**
   * Factory method to create a new TestMonitor instance for a given test case.
   *
   * @param testThread the thread executing the test case
   * @param test       the TestCase instance to monitor
   * @return a new TestMonitor instance
   */
  public static TestMonitor forTest(Thread testThread, TestCase test) {
    return new TestMonitor(testThread, test);
  }

  @Override
  public void run() {
    try {
      limit.doWhileFalse(() -> {
        if (!shutdown) {
          limit.wait(Duration.ofSeconds(10));
        }
        return shutdown;
      });
    } catch (TimeLimitReachedError limitReachedError) {
      System.out.printf(
          "%s has exceeded the allowed runtime of %s seconds, "
              + "attempting to interrupt test thread: %s%n",
          test.getName(), limit.getDuration().toSeconds(), testThread);
      while (!shutdown && !testThread.isInterrupted()) {
        testThread.interrupt();
        TimeLimit.of(Duration.ofSeconds(5)).waitFullDuration();
      }
    }
  }

  /**
   * Sets the shutdown flag to true, indicating that the monitoring should stop.
   */
  public void setShutdownFlag() {
    this.shutdown = true;
  }
}
