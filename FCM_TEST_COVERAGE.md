# FCM Components Test Coverage

This document provides information about the test coverage for Firebase Cloud Messaging (FCM) components in the messenger-app-android project.

## Components Analyzed

The following FCM components are analyzed for test coverage:

1. **MesiboGcmListenerService** - Handles FCM notifications and token management
2. **MesiboRegistrationIntentService** - Manages FCM registration and token handling
3. **MesiboJobIntentService** - Processes background tasks related to FCM

## Coverage Metrics

The following metrics are collected for each component:

- **Line Coverage** - Percentage of code lines executed during tests
- **Branch Coverage** - Percentage of code branches (if/else, switch cases) executed during tests
- **Method Coverage** - Percentage of methods called during tests
- **Class Coverage** - Percentage of classes instantiated during tests

## Test Cases

The test cases for each component are defined in the following files:

1. **MesiboGcmListenerServiceTest.java** - Tests for MesiboGcmListenerService
2. **MesiboRegistrationIntentServiceTest.java** - Tests for MesiboRegistrationIntentService
3. **MesiboJobIntentServiceTest.java** - Tests for MesiboJobIntentService

## Generating Coverage Reports

To generate coverage reports for FCM components, run the following command:

```bash
./generate_fcm_coverage_metrics.sh
```

This script will:
1. Clean the project
2. Run the unit tests
3. Generate detailed coverage metrics for FCM components
4. Create HTML, CSV, and JSON reports

## Viewing Coverage Reports

After running the script, you can find the coverage reports in the following location:

```
app/build/reports/jacoco/fcmComponentsMetrics/
```

The reports include:
- HTML report: `index.html`
- CSV data: `fcm-components-metrics.csv`
- JSON data: `fcm-coverage-metrics.json`
- Summary: `fcm-metrics-summary.txt`

## Coverage Report Format

The JSON coverage report follows this structure:

```json
{
  "timestamp": "2023-07-01 12:00:00",
  "project": "messenger-app-android",
  "components": [
    {
      "name": "MesiboGcmListenerService",
      "package": "org.mesibo.messenger.fcm",
      "metrics": {
        "line_coverage": {
          "covered": 27,
          "missed": 3,
          "total": 30,
          "percentage": 90.0
        },
        "branch_coverage": {
          "covered": 8,
          "missed": 2,
          "total": 10,
          "percentage": 80.0
        },
        "method_coverage": {
          "covered": 4,
          "missed": 0,
          "total": 4,
          "percentage": 100.0
        },
        "class_coverage": {
          "covered": 1,
          "missed": 0,
          "total": 1,
          "percentage": 100.0
        }
      }
    },
    // Similar entries for other components
  ],
  "summary": {
    "line_coverage": {
      "covered": 101,
      "missed": 14,
      "total": 115,
      "percentage": 87.83
    },
    // Similar entries for other metrics
  }
}
```

## Interpreting the Results

- **High Coverage (>80%)**: Good test coverage, most code paths are tested
- **Medium Coverage (50-80%)**: Acceptable coverage, but some code paths may not be tested
- **Low Coverage (<50%)**: Insufficient coverage, many code paths are not tested

## Improving Test Coverage

To improve test coverage:

1. Add tests for untested methods
2. Add tests for edge cases and error conditions
3. Add tests for different input values
4. Add tests for different execution paths

## Integration with CI/CD

The coverage metrics generation is integrated with the project's build system and can be run as part of the CI/CD pipeline using the following Gradle task:

```bash
./gradlew generateFcmComponentsCoverageMetrics
```