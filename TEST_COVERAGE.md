# Test Coverage Metrics for Messenger App Android

This document provides information about test coverage metrics for the core components of the messenger-app-android project.

## Core Components

The following core components are included in the coverage metrics:

1. **NotifyUser** - Manages notifications in the messenger application
2. **UIManager** - Handles UI-related operations and navigation
3. **MesiboFileTransferHelper** - Manages file upload and download operations
4. **MainApplication** - The main application class that initializes the app

## Coverage Metrics

The following metrics are included in the coverage reports:

- **Line Coverage** - Percentage of code lines that are executed by tests
- **Branch Coverage** - Percentage of branches (if/else, switch cases) that are executed by tests
- **Method Coverage** - Percentage of methods that are called by tests
- **Class Coverage** - Percentage of classes that are instantiated by tests

## Generating Coverage Metrics

### Using the Script

The easiest way to generate coverage metrics is to use the provided script:

```bash
# Make the script executable (if needed)
chmod +x generate_coverage_metrics.sh

# Run the script
./generate_coverage_metrics.sh
```

### Using Gradle Directly

You can also generate the coverage metrics using Gradle directly:

```bash
# Clean the project
./gradlew clean

# Run the tests
./gradlew testDebugUnitTest

# Generate the coverage metrics
./gradlew generateCoreComponentsCoverageMetrics
```

## Viewing the Reports

After generating the coverage metrics, you can find the reports in the following locations:

- **HTML Report**: `app/build/reports/jacoco/coreComponentsMetrics/index.html`
- **CSV Data**: `app/build/reports/jacoco/coreComponentsMetrics/core-components-metrics.csv`
- **JSON Data**: `app/build/reports/jacoco/coreComponentsMetrics/coverage-metrics.json`
- **Summary**: `app/build/reports/jacoco/coreComponentsMetrics/metrics-summary.txt`
- **Detailed Report**: `app/build/reports/jacoco/detailedCoverageReport/detailed-coverage-report.md`
- **Component Structure Analysis**: `build/reports/component-structure-analysis.md`

Open the HTML report in a web browser to view detailed coverage information.

## Understanding the Reports

### HTML Report

The HTML report provides a visual representation of the coverage metrics:

- **Green** - Fully covered code
- **Yellow** - Partially covered code
- **Red** - Uncovered code

You can drill down into packages, classes, and methods to see detailed coverage information.

### CSV Data

The CSV file contains raw coverage data that can be imported into spreadsheet applications or data analysis tools.

### JSON Data

The JSON file provides coverage metrics in a structured format that can be easily consumed by other tools and systems. The JSON structure includes:

```json
{
  "timestamp": "2023-05-15 10:30:45",
  "project": "messenger-app-android",
  "components": [
    {
      "name": "NotifyUser",
      "package": "org.mesibo.messenger",
      "metrics": {
        "line_coverage": {
          "covered": 85,
          "missed": 15,
          "total": 100,
          "percentage": 85.0
        },
        "branch_coverage": { ... },
        "method_coverage": { ... },
        "class_coverage": { ... }
      }
    },
    // Other components...
  ],
  "summary": {
    "line_coverage": { ... },
    "branch_coverage": { ... },
    "method_coverage": { ... },
    "class_coverage": { ... }
  }
}
```

### Summary

The summary file provides a quick overview of the coverage metrics for each core component.

## Improving Coverage

To improve test coverage:

1. Identify areas with low coverage in the reports
2. Write additional tests targeting those areas
3. Focus on critical paths and edge cases
4. Regenerate the coverage metrics to verify improvements

## Continuous Integration

You can integrate coverage metrics generation into your CI/CD pipeline by adding the following step:

```yaml
- name: Generate Coverage Metrics
  run: ./gradlew generateCoreComponentsCoverageMetrics
```

This will generate the coverage reports as part of your CI/CD process.
