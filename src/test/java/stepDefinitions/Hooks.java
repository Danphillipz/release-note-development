package stepDefinitions;

import com.microsoft.playwright.Tracing;
import io.cucumber.java.*;
import playwright.managers.PlaywrightManager;
import playwright.managers.ConfigurationManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Class containing Cucumber hooks for setup and teardown actions.
 */
public class Hooks {

    /**
     * Sets up PlaywrightManager before all scenarios.
     */
    @BeforeAll
    public static void setup() {
        PlaywrightManager.startPlaywright(ConfigurationManager.get().configuration().asRequiredString("browser"));
    }

    /**
     * Tears down PlaywrightManager after all scenarios.
     */
    @AfterAll
    public static void tearDown() {
        PlaywrightManager.perform().shutdown();
    }

    /**
     * Launches the test before each scenario.
     */
    @Before
    public void beforeScenario() {
        PlaywrightManager.perform().launchBrowser();
    }

    /**
     * Performs cleanup actions after each scenario.
     * Will capture and attach a screenshot to reports upon failure.
     *
     * If the <b>trace</b> flag is enabled, this will call {@link Hooks#attachTrace(Scenario, String)}.
     *
     * @param scenario The scenario that just ran.
     */
    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            String name = scenario.getName().replace(" ", "-");
            if (ConfigurationManager.get().configuration().asFlag("trace", false)) {
                attachTrace(scenario, name);
            }
            byte[] screenshot = PlaywrightManager.get().page().screenshot();
            scenario.attach(screenshot, "image/png", String.format("%s-failure-screenshot", name));
        }

        PlaywrightManager.perform().endTest();
    }

    /**
     * Attaches a trace file to the scenario and creates a link to this within reports.
     *
     * @param scenario The scenario to which the trace will be attached.
     * @param name     The name of the scenario.
     */
    private void attachTrace(Scenario scenario, String name) {
        Path trace = Paths.get(String.format("target/trace/%s-%s.zip", name, UUID.randomUUID()));
        String linkHtml = String.format(
                "<p>To view this trace file, upload it to <b>\"https://trace.playwright.dev/\"</b>: "
                        + "<a href=\"../%s\">Download Trace File</a>",
                Paths.get("target").relativize(trace)
        );
        scenario.attach(linkHtml.getBytes(), "text/html", "Trace File");
        PlaywrightManager.get().browserContext().tracing().stop(new Tracing.StopOptions().setPath(trace));
    }
}
