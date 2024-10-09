package testrunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.discovery.UriSelector;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

/**
 * Custom test runner for rerunning failed Cucumber tests.
 */
public class CucumberTestRerunner {

  private static final Logger logger = LoggerFactory.getLogger(CucumberTestRerunner.class);

  /**
   * Main method to execute the rerun of failed Cucumber tests.
   *
   * @param args Command line arguments (not used).
   * @throws IOException If an I/O error occurs.
   */
  public static void main(String[] args) throws IOException {
    var retryFile = Path.of("target/failedScenarios.txt");
    if (!Files.exists(retryFile)) {
      logger.info(() -> "No retry file has been found");
      return;
    }

    cleanUpDirectories("target/logs", "target/trace");

    List<UriSelector> selectors = Files.readAllLines(retryFile).stream()
        .map(DiscoverySelectors::selectUri)
        .toList();

    if (selectors.isEmpty()) {
      logger.info(() -> "No failed tests found which require retest");
      return;
    }

    logger.info(() -> String.format("Found %d tests requiring a rerun", selectors.size()));

    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
        .selectors(selectors)
        .build();

    Launcher launcher = LauncherFactory.create();
    SummaryGeneratingListener listener = new SummaryGeneratingListener();
    launcher.registerTestExecutionListeners(listener);
    launcher.execute(request);

    TestExecutionSummary summary = listener.getSummary();
    System.exit(
        summary.getTotalFailureCount() > 0 ? Status.FAILED.ordinal() : Status.SUCCESSFUL.ordinal());
  }

  /**
   * Cleans up the specified directories by deleting their contents.
   *
   * @param directories The directories to clean up.
   * @throws IOException If an I/O error occurs during directory deletion.
   */
  private static void cleanUpDirectories(String... directories) throws IOException {
    for (String dir : directories) {
      deleteDirectory(Path.of(dir));
    }
  }

  /**
   * Deletes the contents of the specified directory.
   *
   * @param path The path to the directory to delete.
   * @throws IOException If an I/O error occurs during directory deletion.
   */
  private static void deleteDirectory(Path path) throws IOException {
    if (Files.exists(path)) {
      try (Stream<Path> pathStream = Files.walk(path)) {
        pathStream.sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
      } catch (IOException e) {
        logger.error(e, () -> String.format("Failed to delete directory: %s", path));
        throw e;
      }
    }
  }
}
