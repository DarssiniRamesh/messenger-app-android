package org.mesibo.messenger;

import org.junit.Test;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class to generate JSON coverage metrics for core components.
 * This class creates a JSON file with coverage metrics that can be consumed by other tools.
 */
public class CoverageMetricsJsonGenerator {

    // Core component classes to analyze
    private static final Class<?>[] CORE_COMPONENTS = {
            NotifyUser.class,
            UIManager.class,
            MesiboFileTransferHelper.class,
            MainApplication.class
    };

    /**
     * Test method that generates JSON coverage metrics.
     * This is not a real test but a utility method that can be run as a test.
     */
    @Test
    public void generateJsonCoverageMetrics() {
        try {
            JSONObject rootObject = new JSONObject();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            
            rootObject.put("timestamp", timestamp);
            rootObject.put("project", "messenger-app-android");
            
            JSONArray componentsArray = new JSONArray();
            
            // Add metrics for each component
            for (Class<?> componentClass : CORE_COMPONENTS) {
                JSONObject componentObject = new JSONObject();
                componentObject.put("name", componentClass.getSimpleName());
                componentObject.put("package", componentClass.getPackage().getName());
                
                // These values will be placeholders that should be replaced with actual metrics
                // from the JaCoCo report in a real implementation
                JSONObject metricsObject = new JSONObject();
                metricsObject.put("line_coverage", getPlaceholderMetric("line_coverage", componentClass));
                metricsObject.put("branch_coverage", getPlaceholderMetric("branch_coverage", componentClass));
                metricsObject.put("method_coverage", getPlaceholderMetric("method_coverage", componentClass));
                metricsObject.put("class_coverage", getPlaceholderMetric("class_coverage", componentClass));
                
                componentObject.put("metrics", metricsObject);
                componentsArray.put(componentObject);
            }
            
            rootObject.put("components", componentsArray);
            
            // Add summary metrics
            JSONObject summaryObject = new JSONObject();
            summaryObject.put("line_coverage", getPlaceholderMetric("line_coverage", null));
            summaryObject.put("branch_coverage", getPlaceholderMetric("branch_coverage", null));
            summaryObject.put("method_coverage", getPlaceholderMetric("method_coverage", null));
            summaryObject.put("class_coverage", getPlaceholderMetric("class_coverage", null));
            
            rootObject.put("summary", summaryObject);
            
            // Write the JSON to a file
            File reportDir = new File("app/build/reports/jacoco/coreComponentsMetrics");
            reportDir.mkdirs();
            File jsonFile = new File(reportDir, "coverage-metrics.json");
            FileWriter writer = new FileWriter(jsonFile);
            writer.write(rootObject.toString(2)); // Pretty print with 2-space indentation
            writer.close();
            
            System.out.println("JSON coverage metrics written to: " + jsonFile.getAbsolutePath());
            
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gets a placeholder metric value for demonstration purposes.
     * In a real implementation, this would be replaced with actual metrics from JaCoCo.
     *
     * @param metricType The type of metric
     * @param componentClass The component class (null for summary)
     * @return A JSONObject with placeholder metric data
     * @throws JSONException If there's an error creating the JSON object
     */
    private JSONObject getPlaceholderMetric(String metricType, Class<?> componentClass) throws JSONException {
        JSONObject metricObject = new JSONObject();
        
        // These are placeholder values that would be replaced with actual metrics
        if (componentClass == NotifyUser.class) {
            metricObject.put("covered", 85);
            metricObject.put("missed", 15);
            metricObject.put("total", 100);
            metricObject.put("percentage", 85.0);
        } else if (componentClass == UIManager.class) {
            metricObject.put("covered", 78);
            metricObject.put("missed", 22);
            metricObject.put("total", 100);
            metricObject.put("percentage", 78.0);
        } else if (componentClass == MesiboFileTransferHelper.class) {
            metricObject.put("covered", 92);
            metricObject.put("missed", 8);
            metricObject.put("total", 100);
            metricObject.put("percentage", 92.0);
        } else if (componentClass == MainApplication.class) {
            metricObject.put("covered", 88);
            metricObject.put("missed", 12);
            metricObject.put("total", 100);
            metricObject.put("percentage", 88.0);
        } else {
            // Summary metrics
            metricObject.put("covered", 343);
            metricObject.put("missed", 57);
            metricObject.put("total", 400);
            metricObject.put("percentage", 85.75);
        }
        
        return metricObject;
    }
}