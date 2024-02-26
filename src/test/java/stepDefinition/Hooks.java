package stepDefinition;

import io.cucumber.java.*;
import playwright.managers.BrowserFactory;

public class Hooks {

	@BeforeAll
	public static void setup() {
		BrowserFactory.startFactory();
	}
	
	@Before
	public void beforeScenario() {
		BrowserFactory.perform().launchTest();
	}
	
	@After
	public void afterScenario(Scenario scenario) {
		BrowserFactory.perform().endTest();
//		String screenshotName = scenario.getName().replaceAll(" ", "_");
//		context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get("target/" + screenshotName + ".zip")));
//		context.close();
	}
	
	@AfterAll
	public static void tearDown() {
		BrowserFactory.perform().shutdown();
	}
}
