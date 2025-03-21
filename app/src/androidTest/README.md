# Instrumented Tests

This directory contains instrumented tests for the Mesibo Messenger Android application. These tests run on an Android device or emulator.

## Directory Structure

The directory structure mirrors the main application structure:

```
org/
└── mesibo/
    └── messenger/
        ├── AppSettings/
        ├── fcm/
        ├── Utils/
        └── ExampleInstrumentedTest.java
```

## Writing Instrumented Tests

When writing instrumented tests:

1. Follow the same package structure as the class you're testing
2. Name test classes with the format `[ClassUnderTest]InstrumentedTest.java`
3. Use `@RunWith(AndroidJUnit4.class)` annotation
4. Use Espresso for UI testing
5. Use ActivityScenarioRule for launching activities
6. Consider using UI Automator for system-level interactions

## Running Tests

Run instrumented tests with:

```
./gradlew connectedAndroidTest
```

Or from Android Studio by right-clicking on the test class or method and selecting "Run".