# Playwright-Java-poc

# TODO

- [ X ] Mechanism to run tests single threaded (via CLI and in IDE)
- [ X ] Parallel test execution
- [ X ] Mechanism to switch environments
- [ X ] Mechanism to switch browsers (And set up mobile emulation maybe?)
- [ ] Mobile emulation system from deviceDescriptors
- [ ] Mechanism to rerun tests automatically upon failure based upon a configuration
- [ X ] Come up with a CLI run option (with params for env, browsers etc)
- [ X ] Set up trace files
- [ X ] Set up evidence system
- [ X ] Set up HTML reports
- [ ] Mechanism for handling multiple context and pages per context
- [ ] Javadoc everything
- [ ] (GitHub matrix can then instruct on browsers to run) - Maybe?
- [ ] Document junit-platform

## Test Tags

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
- `trace`: Enables tracing for failed tests. See [trace files](#todo)

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
HTML reports are generated using [Cluecumber Maven](https://github.com/trivago/cluecumber/tree/main/maven) and can be
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