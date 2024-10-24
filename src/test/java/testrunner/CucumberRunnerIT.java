package testrunner;


import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * JUni5 test runner using the Cucumber engine.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "stepdefinitions")
@SuppressWarnings({"java:S2187", "checkstyle:AbbreviationAsWordInName"})
public class CucumberRunnerIT {

  private CucumberRunnerIT() {
  }
}
