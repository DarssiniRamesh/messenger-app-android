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
 * 
 * This class has been updated to use dependency injection instead of reflection for accessing
 * dependencies. This makes the tests more maintainable and less brittle.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28) // Target SDK version for tests
public abstract class BaseUnitTest {

    // Context that can be used in tests
    protected Context context;
    
    // AutoCloseable for Mockito annotations
    private AutoCloseable mockitoCloseable;
    
    // Dependency provider for tests
    protected TestDependencyProvider dependencyProvider;

    /**
     * Setup method that runs before each test.
     * Initializes Mockito annotations, sets up logging, provides a context, and initializes the dependency provider.
     */
    @Before
    public void setUp() {
        // Initialize Mockito annotations
        mockitoCloseable = MockitoAnnotations.openMocks(this);
        
        // Redirect Robolectric logs to System.out for better visibility in test reports
        ShadowLog.stream = System.out;
        
        // Get application context from Robolectric
        context = ApplicationProvider.getApplicationContext();
        
        // Initialize the dependency provider
        dependencyProvider = createDependencyProvider();
        
        // Call the template method for subclass-specific setup
        setUpTest();
    }
    
    /**
     * Creates a dependency provider for tests.
     * This method can be overridden in subclasses to provide a custom dependency provider.
     * 
     * @return A TestDependencyProvider instance
     */
    protected TestDependencyProvider createDependencyProvider() {
        return new DefaultTestDependencyProvider();
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
    
    /**
     * Utility method to access a private field of an object.
     * This method is provided for backward compatibility with existing tests.
     * New tests should use dependency injection instead.
     *
     * @param object The object containing the field
     * @param fieldName The name of the field
     * @return The value of the field
     * @throws RuntimeException If the field cannot be accessed
     * @deprecated Use dependency injection instead
     */
    @Deprecated
    protected Object getPrivateField(Object object, String fieldName) {
        try {
            java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get private field", e);
        }
    }
    
    /**
     * Utility method to set a private field of an object.
     * This method is provided for backward compatibility with existing tests.
     * New tests should use dependency injection instead.
     *
     * @param object The object containing the field
     * @param fieldName The name of the field
     * @param value The value to set
     * @throws RuntimeException If the field cannot be accessed
     * @deprecated Use dependency injection instead
     */
    @Deprecated
    protected void setPrivateField(Object object, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set private field", e);
        }
    }
    
    /**
     * Utility method to set a private static field of a class.
     * This method is provided for backward compatibility with existing tests.
     * New tests should use dependency injection instead.
     *
     * @param clazz The class containing the field
     * @param fieldName The name of the field
     * @param value The value to set
     * @throws RuntimeException If the field cannot be accessed
     * @deprecated Use dependency injection instead
     */
    @Deprecated
    protected void setPrivateStaticField(Class<?> clazz, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            java.lang.reflect.Modifier.setModifiers(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
            field.set(null, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set private static field", e);
        }
    }
    
    /**
     * Utility method to get a private static field of a class.
     * This method is provided for backward compatibility with existing tests.
     * New tests should use dependency injection instead.
     *
     * @param clazz The class containing the field
     * @param fieldName The name of the field
     * @return The value of the field
     * @throws RuntimeException If the field cannot be accessed
     * @deprecated Use dependency injection instead
     */
    @Deprecated
    protected Object getPrivateStaticField(Class<?> clazz, String fieldName) {
        try {
            java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get private static field", e);
        }
    }
    
    /**
     * Utility method to invoke a private method of an object.
     * This method is provided for backward compatibility with existing tests.
     * New tests should use dependency injection instead.
     *
     * @param object The object containing the method
     * @param methodName The name of the method
     * @param args The arguments to pass to the method
     * @return The result of the method invocation
     * @throws RuntimeException If the method cannot be accessed or invoked
     * @deprecated Use dependency injection instead
     */
    @Deprecated
    protected Object invokePrivateMethod(Object object, String methodName, Object... args) {
        try {
            Class<?>[] argTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }
            
            java.lang.reflect.Method method = object.getClass().getDeclaredMethod(methodName, argTypes);
            method.setAccessible(true);
            return method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke private method", e);
        }
    }
}
