function getTestRunUrl(apiUrl, runId) {
    const urlParts = apiUrl.split('/');
    const baseUrl = urlParts.slice(0, -2).join('/').replace('_apis/test', '_TestManagement/Runs');
    return `${baseUrl}?runId=${runId}&_a=runCharts`;
}

function generateTestSummary(testResults) {
    const testRuns = {};
    testResults.forEach(result => {
        const testRunId = result.testRun.id;
        if (!testRuns[testRunId]) {
            testRuns[testRunId] = {
                name: result.testRun.name,
                totalTests: 0,
                passedTests: 0,
                url: getTestRunUrl(result.testRun.url, testRunId)
            };
        }
        testRuns[testRunId].totalTests += 1;
        if (result.outcome === "Passed") {
            testRuns[testRunId].passedTests += 1;
        }
    });

    // Create the summary array
    const summary = Object.keys(testRuns).map(testRunId => {
        const totalTests = testRuns[testRunId].totalTests;
        const passedTests = testRuns[testRunId].passedTests;
        const passRate = `${passedTests}/${totalTests} ${(passedTests / totalTests * 100).toFixed(2)}%`;
        return {
            name: testRuns[testRunId].name,
            testRunId: testRunId,
            totalTests: totalTests,
            passRate: passRate,
            url: testRuns[testRunId].url
        };
    });

    return summary;
}

function parseJson(json) {
  return JSON.parse(jsonString);
}

function splitWorkItems(workItems) {
    const bugs = [];
    const otherWorkItems = [];
    workItems.forEach(item => {
        if (item.fields['System.WorkItemType'] == 'Bug') {
            bugs.push(item);
        } else {
            otherWorkItems.push(item);
        }
    });
    return {
        bugs,
        otherWorkItems
    };
}

module.exports = {
    generateTestSummary,
    parseJson,
    splitWorkItems
};
