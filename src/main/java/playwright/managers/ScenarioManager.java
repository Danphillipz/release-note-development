package playwright.managers;

import com.microsoft.playwright.Page.ScreenshotOptions;
import enums.Configuration;
import io.cucumber.java.Scenario;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import loggers.FileLogger;
import playwright.managers.ConfigurationManager.PropertyHandler;

/**
 * Manages the lifecycle and context of a Cucumber scenario, including attaching logs, screenshots,
 * and traces.
 */
public class ScenarioManager {

  private final ThreadLocal<Scenario> scenarioThreadLocal = new ThreadLocal<>();

  private static ScenarioManager instance;

  private final PropertyHandler configuration;

  private ScenarioManager() {
    configuration = ConfigurationManager.get().configuration();
  }

  private final Path targetFolder = Paths.get("target");

  /**
   * Returns the singleton instance of ScenarioManager.
   *
   * @return the singleton instance.
   */
  public static synchronized ScenarioManager get() {
    if (instance == null) {
      instance = new ScenarioManager();
    }
    return instance;
  }

  /**
   * Sets the current scenario and validates its tags.
   *
   * @param scenario the current Cucumber scenario.
   */
  public void setScenario(Scenario scenario) {
    scenarioThreadLocal.set(scenario);
  }

  /**
   * Ends the current scenario, attaching logs, screenshots, and traces if necessary.
   *
   * @param scenario the current Cucumber scenario.
   */
  public void endScenario(Scenario scenario) {
    var logFileName = FileLogger.instance().getLogFileName();
    var traceAlways = configuration.asFlag(Configuration.TRACE_ALWAYS, false);
    var traceOnFailure = (configuration.asFlag(Configuration.TRACE_ON_FAILURE, false)
        && scenario.isFailed());
    if (traceAlways || traceOnFailure) {
      String name = sanitiseName(scenario.getName());
      attachTrace(scenario, name);
      attachScreenshot(scenario, name);
    }
    attachLog(scenario, logFileName);
    attachVideo(scenario);
    scenarioThreadLocal.remove();
  }

  /**
   * If a browser exists, attaches a trace file to the scenario and creates a link to this within
   * reports.
   *
   * @param scenario The scenario to which the trace will be attached.
   * @param name     The name of the scenario.
   */
  private void attachTrace(Scenario scenario, String name) {
    if (PlaywrightManager.get().hasBrowserLaunched()) {
      try {
        Path path = Paths.get(String.format("target/trace/%s-%s.zip", name, scenario.getId()));
        String linkHtml = """
            <div style='border: 2px solid #4CAF50; padding: 20px; border-radius: 10px; font-family:
             Arial, sans-serif;'>
                <h2 style='color: #4CAF50;'>Playwright Trace File</h2>
                <p>To view this trace file, upload the attached ZIP file to the trace viewer below.
                 If there are any issues, open the trace viewer manually.</p>
                <div style='display: flex; align-items: center;'>
                    <img src='https://img.icons8.com/ios-filled/50/000000/zip.png'
                    alt='ZIP file icon' style='margin-right: 10px;'/>
                    <a href='../%s' style='font-size: 18px; color: #0000EE; text-decoration: none;'>
                    Download Trace File</a>
                </div>
                <div style='border: 2px solid black; border-radius: 10px; padding: 10px;'>
                    <button onclick='maximizeIframe()' style='margin-bottom: 10px;
                      padding: 10px 20px; background-color: #4CAF50; color: white; border: none;
                      border-radius: 5px; cursor: pointer;'>Maximise Trace Viewer
                    </button>
                    <button onclick='navigateToTraceViewer()' style='margin-bottom: 10px;
                    margin-left: 10px; padding: 10px 20px; background-color: #4CAF50; color: white;
                    border: none; border-radius: 5px; cursor: pointer;'>Go to Trace Viewer
                    </button>
                    <iframe id='traceViewer' src='https://trace.playwright.dev' width='100%%'
                    height='500px' style="border: none; border-radius: 10px;">
                    </iframe>
                </div>
            </div>
            <script>
                function maximizeIframe() {
                    var iframe = document.getElementById('traceViewer');
                    if (iframe.requestFullscreen) {
                        iframe.requestFullscreen();
                    } else if (iframe.mozRequestFullScreen) { /* Firefox */
                        iframe.mozRequestFullScreen();
                    } else if (iframe.webkitRequestFullscreen) { /* Chrome, Safari and Opera */
                        iframe.webkitRequestFullscreen();
                    } else if (iframe.msRequestFullscreen) { /* IE/Edge */
                        iframe.msRequestFullscreen();
                    }
                }
                function navigateToTraceViewer() {
                    window.open('https://trace.playwright.dev', '_blank');
                }
            </script>
            """.formatted(targetFolder.relativize(path));
        scenario.attach(linkHtml.getBytes(), "text/html", "Trace File");
        PlaywrightManager.perform().saveTrace(path);
      } catch (Exception e) {
        FileLogger.log().severe("Error attaching trace for test %s", e.getMessage());
      }
    }
  }


  /**
   * If a browser exists, attaches a screenshot to the scenario.
   *
   * @param scenario The scenario to which the trace will be attached.
   * @param name     The name of the scenario.
   */
  private void attachScreenshot(Scenario scenario, String name) {
    try {
      if (PlaywrightManager.get().hasBrowserLaunched()) {
        byte[] screenshot = PlaywrightManager.get().page()
            .screenshot(new ScreenshotOptions().setFullPage(true));
        scenario.attach(screenshot, "image/png", String.format("%s-final-screenshot", name));
      }
    } catch (Exception e) {
      FileLogger.log().severe("Unable to capture screenshot on failure");
    }
  }

  /**
   * Attaches the log file to the scenario if logging is enabled.
   *
   * @param scenario    The scenario to which the log will be attached.
   * @param logFileName The name of the log file.
   */
  private void attachLog(Scenario scenario, String logFileName) {
    var logAlways = configuration.asFlag(Configuration.LOG_TO_FILE_ALWAYS, false);
    var logOnFailure = (scenario.isFailed() && configuration.asFlag(
        Configuration.LOG_TO_FILE_ON_FAILURE, false));
    if (logAlways || logOnFailure) {
      try {
        var logFilePath = Path.of(logFileName);
        var logFile = new String(Files.readAllBytes(logFilePath));
        scenario.attach(
            logFile.getBytes(),
            "text/plain",
            logFilePath.getFileName().toString());
      } catch (IOException e) {
        FileLogger.log().warning("Failed to attach log file %s", logFileName);
      }
    }
  }

  /**
   * Attaches a video file to the scenario if video recording is enabled.
   *
   * @param scenario The scenario to which the video will be attached.
   */
  private void attachVideo(Scenario scenario) {
    if (configuration.asFlag(Configuration.VIDEO_ALWAYS, false)) {
      var videoPath = PlaywrightManager.get().page().video().path().toString();
      int start = videoPath.indexOf("target");
      var relativePath = Path.of(videoPath.substring(start));
      String linkHtml =
          String.format(
              "<p>Download to view this video file: <a href=\"../%s\">Download Video File</a>",
              targetFolder.relativize(relativePath));
      scenario.attach(linkHtml.getBytes(), "text/html", "Video File");
    }
  }

  /**
   * Replaces invalid path characters with '_'.
   *
   * @param name The name to be sanitized.
   * @return The sanitized name.
   */
  public static String sanitiseName(String name) {
    var pattern = Pattern.compile("[<>:\"/|?*]");
    return name.replaceAll(pattern.pattern(), "_");
  }

  /**
   * Gets the currently running cucumber scenario.
   *
   * @return Running scenario.
   */
  public Scenario getCurrentScenario() {
    return this.scenarioThreadLocal.get();
  }
}
