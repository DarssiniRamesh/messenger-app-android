#!/bin/bash

# Script to generate test coverage metrics for core components
# This script runs the unit tests and generates detailed coverage reports

echo "====================================================="
echo "  GENERATING TEST COVERAGE METRICS FOR CORE COMPONENTS"
echo "====================================================="

# Navigate to the project directory
cd "$(dirname "$0")"

# Clean the project
echo "Cleaning project..."
./gradlew clean

# Run the unit tests
echo "Running unit tests..."
./gradlew testDebugUnitTest

# Generate all coverage metrics and reports
echo "Generating coverage metrics and reports..."
./gradlew generateAllCoverageReports

# Check if the report was generated successfully
REPORT_DIR="app/build/reports/jacoco/coreComponentsMetrics"
if [ -d "$REPORT_DIR" ]; then
    echo "====================================================="
    echo "  COVERAGE METRICS GENERATED SUCCESSFULLY"
    echo "====================================================="
    echo "Report location: $REPORT_DIR/index.html"
    echo "CSV data: $REPORT_DIR/core-components-metrics.csv"
    echo "Summary: $REPORT_DIR/metrics-summary.txt"
    
    # Display the summary if available
    if [ -f "$REPORT_DIR/metrics-summary.txt" ]; then
        echo ""
        echo "SUMMARY:"
        echo "--------"
        cat "$REPORT_DIR/metrics-summary.txt"
    fi
else
    echo "ERROR: Failed to generate coverage metrics"
    exit 1
fi

echo ""
echo "To view the HTML report, open the following file in a browser:"
echo "$(pwd)/$REPORT_DIR/index.html"
echo "====================================================="
