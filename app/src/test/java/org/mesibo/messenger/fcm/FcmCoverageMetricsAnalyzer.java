package org.mesibo.messenger.fcm;

import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to analyze test coverage metrics for FCM components.
 * This class provides methods to analyze and report on test coverage.
 */
public class FcmCoverageMetricsAnalyzer {

    // FCM component classes to analyze
    private static final Class<?>[] FCM_COMPONENTS = {
            MesiboGcmListenerService.class,
            MesiboRegistrationIntentService.class,
            MesiboJobIntentService.class
    };

    /**
     * Test method that generates a detailed analysis of the FCM components.
     * This is not a real test but a utility method that can be run as a test.
     */
    @Test
    public void analyzeFcmComponentStructure() {
        StringBuilder report = new StringBuilder();
        report.append("# FCM Components Structure Analysis\n\n");
        report.append("This report provides an analysis of the structure of FCM components.\n\n");

        Map<Class<?>, ComponentMetrics> metricsMap = new HashMap<>();

        for (Class<?> componentClass : FCM_COMPONENTS) {
            ComponentMetrics metrics = analyzeComponent(componentClass);
            metricsMap.put(componentClass, metrics);
            
            report.append("## ").append(componentClass.getSimpleName()).append("\n\n");
            report.append("- **Total Methods**: ").append(metrics.methodCount).append("\n");
            report.append("- **Public Methods**: ").append(metrics.publicMethodCount).append("\n");
            report.append("- **Private Methods**: ").append(metrics.privateMethodCount).append("\n");
            report.append("- **Protected Methods**: ").append(metrics.protectedMethodCount).append("\n");
            report.append("- **Static Methods**: ").append(metrics.staticMethodCount).append("\n");
            report.append("\n");
            
            report.append("### Methods\n\n");
            for (String methodName : metrics.methodNames) {
                report.append("- `").append(methodName).append("`\n");
            }
            report.append("\n");
        }

        // Write the report to a file
        try {
            File reportDir = new File("app/build/reports/jacoco/fcmComponentsMetrics");
            reportDir.mkdirs();
            File reportFile = new File(reportDir, "fcm-component-structure-analysis.md");
            FileWriter writer = new FileWriter(reportFile);
            writer.write(report.toString());
            writer.close();
            System.out.println("FCM component structure analysis written to: " + reportFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Analyzes a component class and returns metrics about its structure.
     *
     * @param componentClass The class to analyze
     * @return Metrics about the component
     */
    private ComponentMetrics analyzeComponent(Class<?> componentClass) {
        ComponentMetrics metrics = new ComponentMetrics();
        Method[] methods = componentClass.getDeclaredMethods();
        
        for (Method method : methods) {
            metrics.methodCount++;
            metrics.methodNames.add(method.getName() + getParameterTypes(method));
            
            if (java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
                metrics.publicMethodCount++;
            } else if (java.lang.reflect.Modifier.isPrivate(method.getModifiers())) {
                metrics.privateMethodCount++;
            } else if (java.lang.reflect.Modifier.isProtected(method.getModifiers())) {
                metrics.protectedMethodCount++;
            }
            
            if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                metrics.staticMethodCount++;
            }
        }
        
        return metrics;
    }

    /**
     * Gets a string representation of a method's parameter types.
     *
     * @param method The method to analyze
     * @return A string representation of the parameter types
     */
    private String getParameterTypes(Method method) {
        StringBuilder sb = new StringBuilder("(");
        Class<?>[] paramTypes = method.getParameterTypes();
        
        for (int i = 0; i < paramTypes.length; i++) {
            sb.append(paramTypes[i].getSimpleName());
            if (i < paramTypes.length - 1) {
                sb.append(", ");
            }
        }
        
        sb.append(")");
        return sb.toString();
    }

    /**
     * Class to hold metrics about a component.
     */
    private static class ComponentMetrics {
        int methodCount = 0;
        int publicMethodCount = 0;
        int privateMethodCount = 0;
        int protectedMethodCount = 0;
        int staticMethodCount = 0;
        List<String> methodNames = new ArrayList<>();
    }
}