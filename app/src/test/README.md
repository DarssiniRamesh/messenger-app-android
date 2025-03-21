# Unit Tests

This directory contains unit tests for the Mesibo Messenger Android application. These tests run on the JVM and do not require an Android device or emulator.

## Directory Structure

The directory structure mirrors the main application structure:

```
org/
└── mesibo/
    └── messenger/
        ├── AppSettings/
        ├── fcm/
        ├── Utils/
        └── ExampleUnitTest.java
```

## Writing Unit Tests

When writing unit tests:

1. Follow the same package structure as the class you're testing
2. Name test classes with the format `[ClassUnderTest]Test.java`
3. Test methods should be named to clearly describe what they're testing
4. Use appropriate assertions from JUnit
5. Consider using Mockito for mocking dependencies

## Running Tests

Run unit tests with:

```
./gradlew test
```

Or from Android Studio by right-clicking on the test class or method and selecting "Run".