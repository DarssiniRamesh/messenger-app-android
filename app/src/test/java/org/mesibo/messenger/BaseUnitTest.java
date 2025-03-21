package org.mesibo.messenger;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

/**
 * Base class for all unit tests in the messenger-app-android project.
 * Provides common setup and teardown methods, as well as utility methods for testing.
 * 
 * This class uses Robolectric to provide Android framework functionality in unit tests.
 * It also configures Mockito for mocking dependencies.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28) // Target SDK version for tests
public abstract class BaseUnitTest {

    // Context that can be used in tests
    protected Context context;
    
    // AutoCloseable for Mockito annotations
    private AutoCloseable mockitoCloseable;

    /**
     * Setup method that runs before each test.
     * Initializes Mockito annotations, sets up logging, and provides a context.
     */
    @Before
    public void setUp() {
        // Initialize Mockito annotations
        mockitoCloseable = MockitoAnnotations.openMocks(this);
        
        // Redirect Robolectric logs to System.out for better visibility in test reports
        ShadowLog.stream = System.out;
        
        // Get application context from Robolectric
        context = ApplicationProvider.getApplicationContext();
        
        // Call the template method for subclass-specific setup
        setUpTest();
    }

    /**
     * Template method for subclass-specific setup.
     * Override this in subclasses to add custom setup logic.
     */
    protected void setUpTest() {
        // Subclasses can override this method to add custom setup logic
    }

    /**
     * Teardown method that runs after each test.
     * Cleans up resources and mocks.
     */
    @After
    public void tearDown() throws Exception {
        // Clean up Mockito resources
        if (mockitoCloseable != null) {
            mockitoCloseable.close();
        }
        
        // Call the template method for subclass-specific teardown
        tearDownTest();
    }

    /**
     * Template method for subclass-specific teardown.
     * Override this in subclasses to add custom teardown logic.
     */
    protected void tearDownTest() {
        // Subclasses can override this method to add custom teardown logic
    }
    
    /**
     * Utility method to pause execution for a specified time.
     * Useful for tests that need to wait for asynchronous operations.
     *
     * @param milliseconds The time to pause in milliseconds
     */
    protected void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}