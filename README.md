# Playwright Java Test Framework Template

# TODO

- [X] Mechanism to run tests single threaded (via CLI and in IDE)
- [X] Parallel test execution
- [X] Mechanism to switch environments
- [X] Mechanism to switch browsers (And set up mobile emulation maybe?)
- [X] Mobile emulation system from deviceDescriptors
- [X] Mechanism to rerun tests automatically upon failure
- [X] Come up with a CLI run option (with params for env, browsers etc.)
- [X] Set up trace files
- [X] Set up evidence system
- [X] Set up HTML reports
- [ ] Javadoc everything
- [ ] Document junit-platform

## Get started

### Overview

## Running Tests

### Supported Browsers

A number of browsers are supported by this test framework, including the ability to emulate mobile devices.

To set the browser you can update the following property in the test configuration file (see [test configuration](#configuration-file):
```properties
browser=chromium | firefox | webkit | chrome | edge
```

A list of supported mobile device confugrations are defined in the [deviceDescriptors](./src/main/java/devices/deviceDescriptors.json) file.
This list has been copied from the official playwright [device descriptors source](https://github.com/microsoft/playwright/blob/main/packages/playwright-core/src/server/deviceDescriptorsSource.json).

To add a new device, simply update this json file with the relevant configuration details.

You can test against an emulated device in the exact same way as you would set a browser, for example:
```properties
browser=Galaxy S9+ | iPhone 8 | iPhone 13 Pro landscape
```

> [!TIP]
> When writing tests which are intended to run on both desktop and mobile devices you may need to know what type of device you are testing on. 
> This can be determined through the `PlaywrightManager` with the following function call: `PlaywrightManager.get().isMobile()`
> 
### Test Tags

Cucumber tags are mapped to JUnit tags. Note that the `@` symbol is not part of
the JUnit tag. So the scenarios below are tagged with `Smoke` and `Sanity`.

```gherkin
@Smoke
@Ignore
Scenario: A tagged scenario
Given I tag a scenario
When I select tests with that tag for execution
Then my tagged scenario is executed

@Sanity
Scenario: Another tagged scenario
Given I tag a scenario
When I select tests with that tag for execution
Then my tagged scenario is executed
```

When using Maven, tags can be provided from the CLI using the `groups` and `excludedGroups` parameters. These take a
[JUnit5 Tag Expression](https://junit.org/junit5/docs/current/user-guide/#running-tests-tag-expressions).

```shell
mvn verify -DexcludedGroups="Ignore" -Dgroups="Smoke | Sanity"
```

For more information on how to select tags, see the relevant documentation:

* [JUnit 5 Suite: @Include Tags](https://junit.org/junit5/docs/current/api/org.junit.platform.suite.api/org/junit/platform/suite/api/IncludeTags.html)
* [JUnit 5 Suite: @Exclude Tags](https://junit.org/junit5/docs/current/api/org.junit.platform.suite.api/org/junit/platform/suite/api/ExcludeTags.html)

### Test Retries

To automatically rerun any failed tests as part of the build you can set `rerunFailingTestsCount` property in
the `pom.xml`

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>${maven.surefire.version}</version>
    <configuration>
        <rerunFailingTestsCount>2</rerunFailingTestsCount>
        <!-->OTHER CONFIGURATION<-->
    </configuration>
</plugin>
```

> [!CAUTION]
> When this setting is enabled the test runner will overwrite the `cucumber.json` report, consequently any HTML reports
> generated via [cucable](#html-reports) will only ever contain information pertaining to the latest rerun.

## Test Configuration

### Configuration file

The configuration for test execution can be set in `src/test/resources/config/configuration.properties`.

In this file you can define the following configuration:

- `browser`: Which browser to run tests against
- `headless`: Whether to run the browser headless
- `environment`: Which environment the test framework should point to (this maps to the corresponding `X.env.properties`
  file)
- `timeout`:
  Calls [setDefaultTimeout](https://playwright.dev/java/docs/api/class-browsercontext#browser-context-set-default-timeout)
  at the browser context level
- `navigationTimeout`:
  Calls [setDefaultNavigationTimeout](https://playwright.dev/java/docs/api/class-browsercontext#browser-context-set-default-navigation-timeout)
  for at the browser context level
- `assertionTimeout`: Changes default timeout for Playwright assertions from 5 seconds to the specified value by
  calling [setDefaultAssertionTimeout](https://playwright.dev/java/docs/api/class-playwrightassertions#playwright-assertions-set-default-assertion-timeout)
- `trace`: Enables tracing for failed tests. See [trace files](#playwright-trace-files)

### Environment files

Environment configuration files exist alongside the framework configuration file. They should have the `.env.properties`
extension.
During execution the framework will read the `environment` value from the corresponding CLI argument or value within the
test configuration file to dictate which environment data gets loaded in.

For example the following environment property will attempt to load the `development.env.properties` file.

```properties
environment=development
```

### CLI Arguments

You can override any of the configuration file values through the corresponding CLI argument.

For example, to run all tests on `webkit` in `headless` mode you could run the following

```shell
mvn verify -Dbrowser=safari -Dheadless=false
```

## Test Reporting

Test results will be output to the console post completion. Additionally, we can generate HTML reports, capture trace
files and view automatically captured screenshots upon failure.

### HTML Reports

Cucumber has been configured to capture all test results so that we can generate HTML reports.
HTML reports are generated using [cluecumber Maven](https://github.com/trivago/cluecumber/tree/main/maven) and can be
generated via the following command:

```shell
mvn cluecumber:reporting
```

### Playwright Trace Files

To capture playwright trace files you must set the following value in the [test configuration file](#test-configuration)
.

```properties
trace=true
```

Upon test failure this will save a trace file to `target/trace/[scenario-name][UUID].zip`. These can easily be found for
any failed tests by generating and viewing the HTML report. A link to the trace file has been embedded to the report in
the `After Hooks` sections.


