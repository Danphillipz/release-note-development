package stepDefinition;

import io.cucumber.java.*;
import playwright.managers.BrowserFactory;

public class Hooks {

	@BeforeAll
	public static void setup() {
		BrowserFactory.start();
		System.out.println("Browser is initialized");
	}
	
	@Before
	public void beforeScenario() {
		BrowserFactory.get().page();
		System.out.println("Browser Context is initialized");
	}
	
	@After
	public void afterScenario(Scenario scenario) {
		BrowserFactory.get().context().close();
//		String screenshotName = scenario.getName().replaceAll(" ", "_");
//		context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get("target/" + screenshotName + ".zip")));
//		context.close();
		System.out.println("BrowserContext has been closed");
	}
	
	@AfterAll
	public static void tearDown() {
		BrowserFactory.shutdown();
		System.out.println("PlayWright has been closed");
	}
}
