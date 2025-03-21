#!/bin/bash
# Make this script executable with: chmod +x generate_fcm_coverage_metrics.sh

# Script to generate test coverage metrics for FCM components
# This script runs the unit tests and generates detailed coverage reports for FCM components

echo "======================================================"
echo "  GENERATING TEST COVERAGE METRICS FOR FCM COMPONENTS"
echo "======================================================"

# Navigate to the project directory
cd "$(dirname "$0")"

# Clean the project
echo "Cleaning project..."
./gradlew clean

# Run the unit tests
echo "Running unit tests..."
./gradlew testDebugUnitTest

# Generate FCM coverage metrics and reports
echo "Generating FCM coverage metrics and reports..."
./gradlew generateFcmComponentsCoverageMetrics

# Check if the report was generated successfully
REPORT_DIR="app/build/reports/jacoco/fcmComponentsMetrics"
if [ -d "$REPORT_DIR" ]; then
    echo "======================================================"
    echo "  FCM COVERAGE METRICS GENERATED SUCCESSFULLY"
    echo "======================================================"
    echo "Report location: $REPORT_DIR/index.html"
    echo "CSV data: $REPORT_DIR/fcm-components-metrics.csv"
    echo "JSON data: $REPORT_DIR/fcm-coverage-metrics.json"
    echo "Summary: $REPORT_DIR/fcm-metrics-summary.txt"
    
    # Display the summary if available
    if [ -f "$REPORT_DIR/fcm-metrics-summary.txt" ]; then
        echo ""
        echo "SUMMARY:"
        echo "--------"
        cat "$REPORT_DIR/fcm-metrics-summary.txt"
    fi
else
    echo "ERROR: Failed to generate FCM coverage metrics"
    exit 1
fi

echo ""
echo "To view the HTML report, open the following file in a browser:"
echo "$(pwd)/$REPORT_DIR/index.html"
echo "======================================================"
