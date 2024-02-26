# Playwright-Java-poc
# TODO
- [ X ] Mechanism to run tests single threaded (via CLI and in IDE)
- [ X ] Parallel test execution
- [ ] Mechanism to switch environments
- [ ] Mechanism to switch browsers (And set up mobile emulation maybe?)
- [ ] Mechanism to rerun tests automatically upon failure based upon a configuration
- [ ] Come up with a CLI run option (with params for env, browsers etc)
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