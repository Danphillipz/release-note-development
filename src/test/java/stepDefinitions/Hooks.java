package stepDefinitions;

import com.microsoft.playwright.Tracing;
import io.cucumber.java.*;
import playwright.managers.PlaywrightManager;
import playwright.managers.ConfigurationManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class Hooks {

    @BeforeAll
    public static void setup() {
        PlaywrightManager.startPlaywright();
    }

    @AfterAll
    public static void tearDown() {
        PlaywrightManager.perform().shutdown();
    }

    @Before
    public void beforeScenario() {
        PlaywrightManager.perform().launchTest();
    }

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
