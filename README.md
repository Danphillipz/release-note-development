# Playwright-Java-poc
# TODO
- [ ] Mechanism to run tests single threaded (via CLI and in IDE)
- [ ] Parallel test execution (For locally running against multiple browsers)
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



# Notes

How to run tests via the cli

```shell
mvn test
```

```shell
mvn test -Dcucumber.options="--tags @tagname"
```