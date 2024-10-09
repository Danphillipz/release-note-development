package plugins;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestRunFinished;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * A Cucumber plugin that monitors test execution and records failed test cases. The failed test
 * cases are written to a file for later re-execution.
 */
public class RerunMonitor implements ConcurrentEventListener {

  private final List<TestCase> failedTestCaseList = new ArrayList<>();

  /**
   * Registers event handlers for test case finished and test run finished events.
   *
   * @param eventPublisher the event publisher
   */
  @Override
  public void setEventPublisher(EventPublisher eventPublisher) {
    eventPublisher.registerHandlerFor(TestCaseFinished.class, this::testCaseFinishedHandler);
    eventPublisher.registerHandlerFor(TestRunFinished.class, this::testRunFinishedHandler);
  }

  /**
   * Handles the TestCaseFinished event. Adds the test case to the failed test case list if the test
   * did not pass.
   *
   * @param testCaseFinished the event indicating a test case has finished
   */
  private void testCaseFinishedHandler(TestCaseFinished testCaseFinished) {
    if (!testCaseFinished.getResult().getStatus().isOk()) {
      failedTestCaseList.add(testCaseFinished.getTestCase());
    }
  }

  /**
   * Handles the TestRunFinished event. Writes the list of failed test cases to a file.
   *
   * @param testRunFinished the event indicating the test run has finished
   */
  private void testRunFinishedHandler(TestRunFinished testRunFinished) {
    Path filePath = Paths.get("target/failedScenarios.txt");
    try {
      Files.deleteIfExists(filePath);
      try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE)) {
        for (TestCase testCase : failedTestCaseList) {
          String formattedFeature = String.format("%s?line=%d%n",
              testCase.getUri().toString().replace(":", ":/"), testCase.getLocation().getLine());
          writer.write(formattedFeature);
        }
      }
    } catch (IOException e) {
      System.out.println("Error writing to the file: " + e.getMessage());
    }
  }
}
