module.exports = {
  format_test_results(data, reportName) {
    data = data.filter(test => test.testRun.name == reportName);

    let features = [...new Set(data.map(item => item.automatedTestStorage))];
    let latestRun = Math.max(
      ...data.map(item => parseInt(item.testRun.id, 10))
    );
    return features.map(value => {
      let relevantTests = data.filter(
        entry =>
          entry.automatedTestStorage === value &&
          parseInt(entry.testRun.id, 10) === latestRun
      );
      return {
        feature: value,
        tests: relevantTests.length,
        passes: relevantTests.filter(test => test.outcome === 'Passed').length,
        errors: relevantTests.filter(test => test.outcome !== 'Passed').length,
      };
    });
  },

  sum_tests(formattedResults) {
    return formattedResults.reduce((total, item) => total + item.tests, 0);
  }
};
