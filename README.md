# Playwright-Java-poc

# TODO

- [ X ] Mechanism to run tests single threaded (via CLI and in IDE)
- [ X ] Parallel test execution
- [ X ] Mechanism to switch environments
- [ X ] Mechanism to switch browsers (And set up mobile emulation maybe?)
- [ ] Mobile emulation system from deviceDescriptors
- [ ] Mechanism to rerun tests automatically upon failure based upon a configuration
- [ X ] Come up with a CLI run option (with params for env, browsers etc)
- [ ] Set up trace files
- [ ] Set up evidence system
- [ ] Mechanism for linting
- [ ] Mechanism for auto formatting
- [ ] Mechanism for handling multiple context and pages per context
- [ ] Javadoc everything
- [ ] (GitHub matrix can then instruct on browsers to run) - Maybe?

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
- `environment`: Which environment the test framework should point to (this maps to the corresponding `X.env.properties`
  file)
- `timeout`:
  Calls [setDefaultTimeout](https://playwright.dev/java/docs/api/class-browsercontext#browser-context-set-default-timeout)
  at the browser context level
- `navigationTimeout`:
  Calls [setDefaultNavigationTimeout](https://playwright.dev/java/docs/api/class-browsercontext#browser-context-set-default-navigation-timeout)
  for at the browser context level

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
mvn test -Dbrowser=safari -Dheadless=false
```