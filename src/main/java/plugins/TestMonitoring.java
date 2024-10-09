package plugins;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestStepStarted;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The TestMonitoring class is a Cucumber plugin that monitors the execution of test cases and
 * enforces time limits on individual test executions.
 *
 * <p>This plugin is essential because Cucumber does not provide a built-in mechanism to set
 * execution time limits for individual tests. By implementing this functionality, TestMonitoring
 * ensures that tests do not run indefinitely, which can lead to resource exhaustion and hinder the
 * overall testing process.</p>
 *
 * <p>Key features include:</p>
 * <ul>
 *   <li>Utilizes a thread pool to manage concurrent test monitors.</li>
 *   <li>Registers event handlers for test case lifecycle events.</li>
 *   <li>Monitors test steps and terminates monitoring if specific conditions are met.</li>
 * </ul>
 *
 * <p>Event Handling:</p>
 * <ul>
 *   <li><strong>TestCaseStarted:</strong>
 *   Initiates monitoring for the test case and starts a new thread.</li>
 *   <li><strong>TestStepStarted:</strong>
 *   Monitors individual test steps and can terminate monitoring based on specific conditions.</li>
 *   <li><strong>TestCaseFinished:</strong>
 *   Cleans up and terminates the monitoring thread once the test case execution is complete.</li>
 * </ul>
 *
 * <p>Thread Management:</p>
 * <ul>
 *   <li>Uses an ExecutorService to handle multiple test monitors concurrently.</li>
 *   <li>Ensures that monitoring threads are properly terminated to free up resources.</li>
 * </ul>
 */
public class TestMonitoring implements ConcurrentEventListener {

  private final ExecutorService executor = Executors.newCachedThreadPool();
  private final Map<TestCase, TestMonitor> testMonitors = new HashMap<>();

  @Override
  public void setEventPublisher(EventPublisher eventPublisher) {
    eventPublisher.registerHandlerFor(TestCaseStarted.class, this::registerTestMonitor);
    eventPublisher.registerHandlerFor(TestStepStarted.class, this::monitorTestStep);
    eventPublisher.registerHandlerFor(TestCaseFinished.class, this::handleTestFinish);
  }

  private void registerTestMonitor(TestCaseStarted testCaseStarted) {
    var testMonitor = TestMonitor.forTest(Thread.currentThread(), testCaseStarted.getTestCase());
    testMonitors.put(testCaseStarted.getTestCase(), testMonitor);
    executor.submit(testMonitor);
  }

  private void monitorTestStep(TestStepStarted testStepStarted) {
    var codeLocation = testStepStarted.getTestStep().getCodeLocation();
    if (codeLocation == null
        || codeLocation.startsWith("stepdefinitions.Hooks.") && !codeLocation.startsWith(
        "stepdefinitions.Hooks.start")) {
      terminateMonitoringThread(testStepStarted.getTestCase());
    }
  }

  private void handleTestFinish(TestCaseFinished testCaseFinished) {
    terminateMonitoringThread(testCaseFinished.getTestCase());
  }

  private void terminateMonitoringThread(TestCase testCase) {
    var testCaseMonitor = testMonitors.get(testCase);
    if (Optional.ofNullable(testCaseMonitor).isPresent()) {
      testCaseMonitor.setShutdownFlag();
      testMonitors.remove(testCase);
    }
  }
}
