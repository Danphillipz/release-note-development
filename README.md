# playwright-java-core

This repository provides the core setup required for testing with [Cucumber](https://cucumber.io/)
, [Playwright](https://playwright.dev/java/) and Java.
The aim of this repository is to reduce setup time by providing ready-to-go implementations for many
of the core requirements of a test framework.

[[_TOC_]]

## Key Features:

- Ability to run all tests in parallel
- Mechanism to switch browsers
- Mechanism to emulate mobile devices
- Mechanism to switch environments
- Configurable through configuration files or CLI arguments
- Retry test mechanism
- Generate HTML test reports
- Capture trace files

> <span style="color:cornflowerblue">🛈️ Note</span>
>
> This framework is using the latest LTS java version and requires Maven >= 3.8.x
> - [OpenJDK 21](https://learn.microsoft.com/en-us/java/openjdk/download#openjdk-21)
> - [Maven](https://maven.apache.org/download.cgi)

## Code Quality

This repository uses the [Maven Checkstyle Plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/index.html) to verify and enforce coding style standards.
We use the [Google Java Style](https://google.github.io/styleguide/javaguide.html) rules which is a recommended standard.

To scan the repository for style violations you can run the following command:

```shell
mvn validate
```

## Pre-Commit Hooks

The project enforces linting checks using pre-commit hooks. This is implemented via the
the `com.rudikershaw.gitbuildhook` maven plugin. When attempting to commit, the hooks specified in
folder `./hooks` folder must run successfully. A failure in the pre-commit hooks will stop the
commit from being pushed.

### Required Setup

1. We recommend using the [IntelliJ IDE](https://www.jetbrains.com/idea/), all subsequent recommendations are based upon using this IDE.
2. We recommend installing the following plugin(s) for your IDE to assist with managing code quality:
    - [Sonarlint](https://www.sonarsource.com/products/sonarlint/): A linting tool with real-time
      feedback
3. Enable auto formatting in accordance with the project formatting settings:
   - Go to: File > Settings > Tools > Actions on Save
   - Enable "Reformat Code"
   - Enable "Optimize imports"

## Running Tests

There are multiple ways to execute tests:

1. Via your IDE at the scenario/feature level
2. Via the [Cucumber test runner](./src/test/java/testrunner/CucumberRunnerIT.java) (Right click >
   Run)
3. Via the command line:

```shell
mvn verify
```

### Supported Browsers

A number of browsers are supported, including the ability to emulate mobile devices.

To set the browser you can update the following property in the test configuration file (see [test configuration](#test-configuration)):

```properties
browser=chromium | firefox | webkit | chrome | edge
```

Alternatively, this can be overridden with the corresponding CLI argument:

```shell
mvn verify -Dbrowser=edge
```

A list of supported mobile device configurations are defined in the [deviceDescriptors](./src/main/java/devices/deviceDescriptors.json) file.
This list has been copied from the official playwright [device descriptors source](https://github.com/microsoft/playwright/blob/main/packages/playwright-core/src/server/deviceDescriptorsSource.json).

To add a new device, simply update this json file with the relevant configuration details.

You can test against an emulated device in the exact same way as you would set a browser, for example:

```properties
browser=Galaxy S9+ | iPhone 8 | iPhone 13 Pro landscape
```

> <span style="color:lightgreen">🛈️ Tip</span>
>
> When writing tests which are intended to run on both desktop and mobile devices you may need to know what type of device you are testing on.
> This can be determined through the `PlaywrightManager` with the following function call: `PlaywrightManager.get().isMobile()`

### Running tests via their tag

Cucumber tags are mapped to JUnit tags. Note that the `@` symbol is not part of the JUnit tag. So the scenarios below are tagged with `Smoke` and `Sanity`.

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

When using Maven, tags can be provided from the CLI using the `groups` and `excludedGroups` parameters.
These take a [JUnit5 Tag Expression](https://junit.org/junit5/docs/current/user-guide/#running-tests-tag-expressions)

```shell
mvn verify -DexcludedGroups="Ignore" -Dgroups="Smoke | Sanity"
```

For more information on how to select tags, see the relevant documentation:

* [JUnit 5 Suite: @Include Tags](https://junit.org/junit5/docs/current/api/org.junit.platform.suite.api/org/junit/platform/suite/api/IncludeTags.html)
* [JUnit 5 Suite: @Exclude Tags](https://junit.org/junit5/docs/current/api/org.junit.platform.suite.api/org/junit/platform/suite/api/ExcludeTags.html)

### Test Retries

#### Why can't we use surefire or failsafe for test retries?

When using `cucumber-junit-platform-engine` cucumber rerun files are not supported, additionally
when using the built in `rerunFailingTestsCount` for failsafe or surefire it has no contextual aware of the previous cucumber run.
As a result, when it retries any failed tests it instructs cucumber to perform a new test run for the failed tests.

While this allows us to rerun any failed scenarios we lose all information surrounding the original test run,
consequently all test and HTML reports will only ever be for the latest set of test retries.

#### Solution

A custom solution has been developed consisting of a custom cucumber plugin and JUnit test runner.

1. Run tests as normal using the appropriate mechanism as defined in the [running tests](#running-tests) section.
2. Review the JUnit or [HTML reports](#html-reports) as normal.
3. Rerun any failed scenarios using the custom [CucumberTestRerunner](#cucumbertestrerunner).

```shell
mvn test-compile exec:java -Dexec.mainClass=testrunner.CucumberTestRerunner
```

##### RerunMonitor

The [RerunMonitor](./src/main/java/plugins/RerunMonitor.java) is a custom plugin which listens for any failed cucumber tests. 
Each failure is logged to a `txt` file with a URI reference to the failed scenario. 
This report is then used by the [CucumberTestRerunner](#cucumbertestrerunner) to determine which scenarios to run.

##### CucumberTestRerunner

A custom [CucumberTestRunner](src/test/java/testrunner/CucumberTestRerunner.java) has been set up using the [JUnit Platform Launcher API](https://junit.org/junit5/docs/5.0.3/api/org/junit/platform/launcher/Launcher.html) to discover and rerun any failed tests from a test run. 
This uses a [UriSelector](https://junit.org/junit5/docs/5.0.0-M3/api/org/junit/platform/engine/discovery/UriSelector.html) to identify the feature/scenario/example which failed as reported by the [RerunMonitor](#rerunmonitor).

##### CI Solution

In CI we first run the tests as normal using the relevant approach for selecting and [running tests](#running-tests).
The pipeline will then capture and upload the JUnit and HTML reports for this initial run as an artefact to the build.

If any test failures have been detected it then uses the [CucumberTestRerunner](#cucumbertestrerunner) to retest just the failed scenarios, again
capturing and uploading the JUnit and HTML reports as separate artefacts following the rerun.
The Azure pipeline has a custom mechanism in place to retry any failed tests.

## Test Configuration

### Framework Configuration file

The configuration for test execution can be set in `src/test/resources/config/configuration.properties`.

There are various different properties which affect how the tests will run, for the latest properties and descriptions, please review the configuration file.

### Environment configuration files

Environment configuration files exist alongside the framework configuration file. 
They should have the `.env.properties` extension.
During execution the framework will read the `environment` value from the corresponding CLI argument
or value within the test configuration file to dictate which environment data gets loaded in.

For example the following environment property will attempt to load the `development.env.properties`
file.

```properties
environment=development
```

#### Environment Secret Files

Environment configuration for secret values can utilise `<environment>.env.secrets` files. 
During execution values in this file will override values from `.env.properties`. Secret files are included
in the'.gitignore' to prevent secrets from being pushed to the repository. Files are for local use only.

To setup secrets for an environment:

1. Create the `.env.secrets` file in the `config` folder
2. Manually set any secret values required e.g. dBConnectionString

### Setting configuration via Environment Variables

The framework will automatically check the system Environment variables for matching values. 
If found environment variables take precedence over either `.env.properties` or `.env.secrets`.

### Set configuration via the CLI

You can override any of the configuration file values through the corresponding CLI argument.

For example, to run all tests on `webkit` in `headless` mode you could run the following

```shell
mvn verify -Dbrowser=safari -Dheadless=false
```

### JUnit Configuration (Parallelism)

Parallel testing is enabled by default and has been set to use `dynamic parallelism` which computes 
the desired parallelism as `available cores` * `dynamic factor`.

This can be further configured via the [junit-platform.properties](./src/test/resources/junit-platform.properties) file.
See the [cucumber-junit-platform-engine](https://github.com/cucumber/cucumber-jvm/tree/main/cucumber-junit-platform-engine#parallel-execution) documentation for more information.

### Custom Strategy for Test Timeouts

To ensure our tests do not run indefinitely and to manage resource utilisation effectively, we have
implemented a custom strategy for test timeouts using a custom Cucumber plugin. This strategy
involves monitoring the execution time of individual test cases and enforcing time limits.

#### Implementation

Our custom strategy is implemented through two main classes: `TestMonitor` and `TestMonitoring`.

The `TestMonitoring` class is a Cucumber plugin that monitors the execution of test cases and
enforces time limits on individual test executions. It utilises a thread pool to manage concurrent
test monitors and registers event handlers for test case lifecycle events.

**Key Features:**

- Thread Pool Management: Manages multiple test monitors concurrently using an ExecutorService.
- Event Handling: Registers handlers for TestCaseStarted, TestStepStarted, and TestCaseFinished
  events.
- Test Step Monitoring: Monitors individual test steps and terminates monitoring based on specific
  conditions.

The `TestMonitor` class is responsible for monitoring the execution of individual test cases within
the Cucumber framework. It enforces a time limit on their execution by running in a separate thread
dedicated to monitoring the test case's execution time.

**Key Features:**

- Dynamic Time Limit Configuration: Configures time limits based on settings from
  the `ConfigurationManager`.
- Handling of Time Limit Breaches: Attempts to interrupt the test thread if the time limit is
  exceeded.
- Shutdown Flag: Safely terminates monitoring when the test completes or is canceled.

## Test Reporting

Test results will be output to the console post completion. Additionally, we can generate HTML
reports, capture trace files and view automatically captured screenshots upon failure.

### HTML Reports

Cucumber has been configured to capture all test results so that we can generate HTML reports.
HTML reports are generated using [cluecumber Maven](https://github.com/trivago/cluecumber/tree/main/maven) and can be generated via the following command:

```shell
mvn cluecumber:reporting
```

### Playwright Trace Files

To capture playwright trace files you must set the following value in the [test configuration file](#test-configuration).

```properties
traceOnFailure=true
```

Upon test failure this will save a trace file to `target/trace/[scenario-name][UUID].zip`. These can
easily be found for any failed tests by generating and viewing the HTML report.
A link to the trace file has been embedded to the report in the `After Hooks` sections.

### Unit Testing

Unit tests can be added to verify the behaviour of core features in the library. To execute unit
tests in isolation:

```shell
mvn test
```

Unit tests will also execute by default in the `mvn verify` goal, to skip execution of the unit
tests in the `verify` goal:

```shell
mvn verify -Dskip.surefire.tests` 
```
