# Test Framework for Mesibo Messenger Android App

## Overview

This directory contains the unit tests for the Mesibo Messenger Android application. The tests have been refactored to reduce reliance on reflection and improve testability through proper dependency injection.

## Key Components

### TestDependencyProvider

The `TestDependencyProvider` interface defines methods for accessing dependencies that were previously accessed via reflection or static mocking. This interface is implemented by `DefaultTestDependencyProvider`, which provides default implementations that delegate to the actual implementations.

Test classes can extend `DefaultTestDependencyProvider` to provide custom implementations for specific tests, making it easier to mock dependencies without using reflection.

### BaseUnitTest

The `BaseUnitTest` class has been updated to include a `TestDependencyProvider` instance that can be used by test classes. It also includes methods for setting up and accessing the dependency provider.

For backward compatibility, `BaseUnitTest` still includes utility methods for reflection, but these are marked as deprecated and should not be used in new tests.

### FcmTestUtils

The `FcmTestUtils` class provides utility methods for FCM tests without relying on reflection. This includes methods for setting the GCM listener and creating test intents.

## Best Practices

1. **Use Dependency Injection**: Instead of using reflection to access private fields or methods, use dependency injection to provide dependencies to the classes being tested.

2. **Create Custom Dependency Providers**: For each test class, create a custom dependency provider that provides mock implementations of the dependencies needed by the test.

3. **Avoid Static Method Mocking**: Instead of mocking static methods, use dependency injection to provide mock implementations of the dependencies that use those static methods.

4. **Use Interfaces**: Define interfaces for dependencies to make them easier to mock in tests.

5. **Keep Tests Focused**: Each test should focus on testing a single aspect of the code, with minimal setup and dependencies.

## Migration Guide

If you need to update existing tests to use the new framework:

1. Create a custom dependency provider for your test class by extending `DefaultTestDependencyProvider`.
2. Override the `createDependencyProvider` method in your test class to return your custom dependency provider.
3. Replace reflection-based field access with calls to your dependency provider.
4. Replace static method mocking with dependency injection.

Example:

```java
// Before
private void mockStaticMethod(Class<?> clazz, String methodName, Object returnValue) {
    // Reflection-based mocking
}

// After
private class CustomTestDependencyProvider extends DefaultTestDependencyProvider {
    @Override
    public SomeType getSomeDependency() {
        return mockDependency;
    }
}

@Override
protected TestDependencyProvider createDependencyProvider() {
    return new CustomTestDependencyProvider();
}
```
