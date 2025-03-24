#!/bin/bash
# Make this script executable with: chmod +x generate_settings_coverage_metrics.sh

# Script to generate test coverage metrics for Settings components
# This script runs the unit tests and generates detailed coverage reports

echo "======================================================"
echo "  GENERATING TEST COVERAGE METRICS FOR SETTINGS COMPONENTS"
echo "======================================================"

# Navigate to the project directory
cd "$(dirname "$0")"

# Clean the project
echo "Cleaning project..."
./gradlew clean

# Run the unit tests for Settings components
echo "Running unit tests for Settings components..."
./gradlew testDebugUnitTest --tests "org.mesibo.messenger.AppSettings.*"

# Create a directory for the Settings coverage metrics
REPORT_DIR="app/build/reports/jacoco/settingsComponentsMetrics"
mkdir -p "$REPORT_DIR"

# Run the Settings coverage metrics generators
echo "Generating coverage metrics and reports..."
./gradlew test --tests "org.mesibo.messenger.AppSettings.SettingsCoverageMetricsJsonGenerator"
./gradlew test --tests "org.mesibo.messenger.AppSettings.SettingsDetailedCoverageReport"
./gradlew test --tests "org.mesibo.messenger.AppSettings.SettingsCoverageMetricsAnalyzer"

# Check if the reports were generated successfully
if [ -d "$REPORT_DIR" ]; then
    echo "======================================================"
    echo "  SETTINGS COVERAGE METRICS GENERATED SUCCESSFULLY"
    echo "======================================================"
    echo "Report location: $REPORT_DIR"
    echo "JSON metrics: $REPORT_DIR/settings-coverage-metrics.json"
    echo "Detailed report: $REPORT_DIR/settings-detailed-coverage-report.md"
    echo "Component structure: $REPORT_DIR/settings-component-structure.md"
    
    # Display the summary if available
    if [ -f "$REPORT_DIR/settings-metrics-summary.txt" ]; then
        echo ""
        echo "SUMMARY:"
        echo "--------"
        cat "$REPORT_DIR/settings-metrics-summary.txt"
    fi
else
    echo "ERROR: Failed to generate Settings coverage metrics"
    exit 1
fi

echo ""
echo "To view the reports, open the following files:"
echo "$(pwd)/$REPORT_DIR/settings-coverage-metrics.json"
echo "$(pwd)/$REPORT_DIR/settings-detailed-coverage-report.md"
echo "$(pwd)/$REPORT_DIR/settings-component-structure.md"
echo "======================================================"
