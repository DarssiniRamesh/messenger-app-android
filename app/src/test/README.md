# Messenger App Android Test Documentation

This document provides comprehensive information about the testing approach, methodologies, and practices for the messenger-app-android codebase.

## Table of Contents

1. [Overview of the Testing Approach](#overview-of-the-testing-approach)
2. [Instructions for Running Tests](#instructions-for-running-tests)
3. [Guidelines for Writing New Tests](#guidelines-for-writing-new-tests)
4. [Explanation of Test Utilities and Base Classes](#explanation-of-test-utilities-and-base-classes)
5. [Test Coverage Summary](#test-coverage-summary)
6. [Known Limitations and Future Improvements](#known-limitations-and-future-improvements)

## Overview of the Testing Approach

The messenger-app-android project employs a comprehensive testing strategy that includes both unit tests and instrumented (UI) tests. This dual approach ensures both the internal logic and the user interface behave as expected.

### Testing Layers

1. **Unit Tests**: Located in `app/src/test/`, these tests run on the JVM without requiring an Android device or emulator. They focus on testing individual components in isolation, with dependencies mocked or stubbed.

2. **Instrumented Tests**: Located in `app/src/androidTest/`, these tests run on an Android device or emulator. They focus on testing UI components, interactions, and integration between components in a real Android environment.

### Testing Frameworks and Libraries

The project uses the following testing frameworks and libraries:

- **JUnit 4**: The base testing framework for both unit and instrumented tests
- **Mockito**: For creating mock objects in tests
- **Robolectric**: For running Android-dependent unit tests on the JVM
- **Espresso**: For UI testing in instrumented tests
- **AndroidX Test**: For additional testing utilities and rules
- **Truth**: For more readable assertions

## Instructions for Running Tests

### Running Unit Tests

Unit tests can be run in several ways:

1. **From the command line**:
   ```bash
   ./gradlew test
   ```

2. **From Android Studio**:
   - Right-click on a test class or method and select "Run"
   - Right-click on the `app/src/test` directory and select "Run Tests"

3. **Running specific test classes**:
   ```bash
   ./gradlew test --tests "org.mesibo.messenger.SampleAPITest"
   ```

### Running Instrumented Tests

Instrumented tests require a connected Android device or emulator:

1. **From the command line**:
   ```bash
   ./gradlew connectedAndroidTest
   ```

2. **From Android Studio**:
   - Right-click on a test class or method and select "Run"
   - Right-click on the `app/src/androidTest` directory and select "Run Tests"

3. **Running specific test classes**:
   ```bash
   ./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=org.mesibo.messenger.StartUpActivityTest
   ```

### Viewing Test Results

Test results are available in several formats:

1. **HTML Reports**:
   - Unit tests: `app/build/reports/tests/testDebugUnitTest/index.html`
   - Instrumented tests: `app/build/reports/androidTests/connected/index.html`

2. **XML Reports**:
   - Unit tests: `app/build/test-results/testDebugUnitTest/`
   - Instrumented tests: `app/build/outputs/androidTest-results/connected/`

3. **Android Studio**: Test results are displayed in the "Run" tool window

## Guidelines for Writing New Tests

### General Guidelines

1. **Test One Thing at a Time**: Each test method should test a single functionality or behavior.
2. **Use Descriptive Test Names**: Test method names should clearly describe what they're testing.
3. **Follow AAA Pattern**: Arrange, Act, Assert - set up the test conditions, perform the action, verify the results.
4. **Keep Tests Independent**: Tests should not depend on each other or on external state.
5. **Use Test Doubles**: Use mocks, stubs, and fakes to isolate the code under test.

### Unit Test Guidelines

1. **Extend BaseUnitTest**: All unit tests should extend the `BaseUnitTest` class to inherit common setup and utility methods.
2. **Mock Dependencies**: Use Mockito to mock external dependencies.
3. **Test Edge Cases**: Include tests for boundary conditions and error scenarios.
4. **Use TestUtils**: Leverage the utility methods in `TestUtils` for common testing tasks.

Example unit test structure:

```java
public class MyClassTest extends BaseUnitTest {

    private MyClass myClass;
    
    @Mock
    private Dependency dependencyMock;
    
    @Override
    protected void setUpTest() {
        // Initialize the class under test with mocked dependencies
        myClass = new MyClass(dependencyMock);
    }
    
    @Test
    public void methodName_condition_expectedBehavior() {
        // Arrange
        when(dependencyMock.someMethod()).thenReturn(expectedValue);
        
        // Act
        Result result = myClass.methodName();
        
        // Assert
        assertEquals(expectedValue, result.getValue());
    }
}
```

### Instrumented Test Guidelines

1. **Use ActivityTestRule**: For testing activities, use `ActivityTestRule` to manage activity lifecycle.
2. **Use Espresso for UI Interactions**: Use Espresso to interact with UI elements.
3. **Use EspressoTestUtils**: Leverage the utility methods in `EspressoTestUtils` for common UI testing tasks.
4. **Test UI Flows**: Test complete user flows rather than just individual screens.

Example instrumented test structure:

```java
@RunWith(AndroidJUnit4.class)
public class MyActivityTest {

    @Rule
    public ActivityTestRule<MyActivity> activityRule =
            new ActivityTestRule<>(MyActivity.class);
    
    @Test
    public void buttonClick_opensNewScreen() {
        // Arrange - Activity is launched by the rule
        
        // Act - Perform UI interaction
        onView(withId(R.id.my_button)).perform(click());
        
        // Assert - Verify the expected outcome
        onView(withId(R.id.new_screen_element)).check(matches(isDisplayed()));
    }
}
```

## Explanation of Test Utilities and Base Classes

### BaseUnitTest

`BaseUnitTest` is the base class for all unit tests in the project. It provides:

- **Robolectric Setup**: Configures Robolectric for Android framework simulation
- **Mockito Setup**: Initializes Mockito annotations and provides cleanup
- **Context Access**: Provides access to an Android context
- **Template Methods**: Offers `setUpTest()` and `tearDownTest()` for subclass-specific setup and teardown
- **Utility Methods**: Includes methods like `pause()` for test convenience

### TestUtils

`TestUtils` is a utility class that provides helper methods for unit tests:

- **Mock Creation**: Methods to create mock Mesibo profiles and messages
- **Random Data Generation**: Methods to generate random phone numbers, names, and UUIDs
- **JSON Handling**: Methods to create and parse JSON objects
- **File Reading**: Methods to read resource files and filesystem files
- **Reflection Utilities**: Methods to access and modify private fields
- **Intent Verification**: Methods to verify intents launched from activities
- **Bundle Creation**: Methods to create bundles with test data

### EspressoTestUtils

`EspressoTestUtils` provides utility methods for Espresso UI tests:

- **Custom ViewActions**: Methods to perform custom actions on views
- **Text Setting**: Methods to set text in EditText views without keyboard interaction
- **Position Clicking**: Methods to click at specific positions within views
- **Waiting**: Methods to wait for specific durations during tests

## Test Coverage Summary

### Unit Test Coverage

The unit tests cover the following components:

1. **SampleAPI**: Tests for API communication, authentication, notifications, and core functionality
2. **AppConfig**: Tests for configuration management
3. **MesiboListeners**: Tests for Mesibo event handling
4. **Utils/AppUtils**: Tests for utility methods

### Instrumented Test Coverage

The instrumented tests cover the following components:

1. **StartUpActivity**: Tests for initialization, navigation, and intent handling
2. **EditProfileActivity**: Tests for profile editing functionality
3. **UI Interactions**: Tests for user interface elements and interactions

### Coverage Gaps

The following areas have limited or no test coverage:

1. **FCM Integration**: Firebase Cloud Messaging integration is not fully tested
2. **Media Handling**: File upload/download and media playback are not fully tested
3. **Complex UI Flows**: Some complex UI interactions are not fully tested
4. **Edge Cases**: Some edge cases and error scenarios are not fully tested

## Known Limitations and Future Improvements

### Known Limitations

1. **Static Method Testing**: Testing static methods requires reflection, which can be brittle
2. **UI Thread Testing**: Testing code that runs on the UI thread can be challenging
3. **External Dependencies**: Some tests depend on mocking external libraries, which may not fully simulate real behavior
4. **Test Data**: Some tests use hardcoded test data, which may need to be updated as the app evolves

### Future Improvements

1. **Increase Test Coverage**: Add tests for untested components and edge cases
2. **Implement Integration Tests**: Add tests that verify integration between components
3. **Add Performance Tests**: Add tests to verify performance characteristics
4. **Improve Test Data Management**: Implement better test data management
5. **Implement Test Fixtures**: Create reusable test fixtures for common test scenarios
6. **Add Screenshot Testing**: Implement screenshot testing for UI components
7. **Implement Continuous Integration**: Set up CI/CD pipeline for automated testing
8. **Add Code Coverage Reporting**: Implement code coverage reporting to identify untested code

### Recommended Next Steps

1. Add tests for FCM components
2. Increase coverage of UI tests
3. Add tests for media handling
4. Implement integration tests for key user flows
5. Set up continuous integration for automated testing