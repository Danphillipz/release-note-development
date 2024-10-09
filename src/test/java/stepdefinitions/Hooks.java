package stepdefinitions;

import enums.Configuration;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import loggers.FileLogger;
import playwright.managers.ConfigurationManager;
import playwright.managers.PlaywrightManager;
import playwright.managers.ScenarioManager;

/**
 * Class containing Cucumber hooks for setup and teardown actions.
 */
public class Hooks {

  /**
   * Sets up PlaywrightManager before all scenarios.
   */
  @BeforeAll
  public static void setup() {
    PlaywrightManager.startPlaywright(
        ConfigurationManager.get().configuration().asRequiredString(Configuration.BROWSER));
  }

  /**
   * Sets up the cucumber scenario and checks that it has been correctly tagged.
   *
   * @param scenario CucumberScenario
   */
  @Before(order = 1)
  public static void start(Scenario scenario) {
    FileLogger.instance().setScenario(scenario);
    ScenarioManager.get().setScenario(scenario);
  }

  /**
   * Performs cleanup actions after each scenario. Will pass the completed scenario to the scenario
   * manager to perform tidy up actions.
   */
  @After()
  public void afterScenario(Scenario scenario) {
    FileLogger.log().info("Test Complete");
    ScenarioManager.get().endScenario(scenario);
    PlaywrightManager.perform().endTest();
    FileLogger.instance().shutdown();
  }

  /**
   * Tears down PlaywrightManager after all scenarios.
   */
  @AfterAll
  public static void tearDown() {
    PlaywrightManager.perform().shutdown();
  }

}
