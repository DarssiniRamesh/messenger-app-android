# UI Tests for Messenger App

This directory contains UI tests for the Messenger App Android application using the Espresso testing framework.

## Test Structure

The tests are organized by activity:

- `EditProfileActivityTest.java`: Tests for the profile editing functionality
- `StartUpActivityTest.java`: Tests for the application startup flow
- `EspressoTestUtils.java`: Utility methods for Espresso tests
- `CustomMatchers.java`: Custom matchers for Espresso tests

## Running Tests

To run the tests, use the following command:

```bash
./gradlew connectedAndroidTest
```

Or run individual test classes:

```bash
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=org.mesibo.messenger.EditProfileActivityTest
```

## Test Coverage

### EditProfileActivityTest

Tests for the EditProfileActivity cover:

1. **Form inputs and validation**
   - Name field validation (character limits, counter updates)
   - Status field validation (character limits, counter updates)
   - Input field interactions

2. **Profile picture selection**
   - Profile image click handling
   - Edit profile image button functionality

3. **Save button functionality**
   - Form submission
   - Data validation

4. **Navigation and UI elements**
   - UI element visibility
   - Back button navigation
   - Toolbar navigation

### StartUpActivityTest

Tests for the StartUpActivity cover:

1. **Initialization and navigation**
   - Activity launch
   - Intent handling
   - Activity recreation

2. **Login flow**
   - Skip tour functionality
   - Background launch

3. **UI elements and interactions**
   - Layout verification
   - Back button behavior

4. **Error handling**
   - Intent flag combinations
   - Edge cases

## Test Utilities

### EspressoTestUtils

Contains helper methods for common Espresso testing operations:

- `setTextInTextView()`: Sets text in a TextView without keyboard
- `clickXYPosition()`: Clicks at a specific position in a view
- `waitFor()`: Waits for a specific duration

### CustomMatchers

Contains custom matchers for Espresso tests:

- `hasErrorText()`: Checks if an EditText has a specific error message
- `hasImage()`: Checks if an ImageView has an image set
- `isEnabled()`: Checks if a View is enabled

## Limitations

Some aspects of the application are difficult to test in instrumented tests:

1. Backend interactions (API calls, data persistence)
2. Full navigation flows that involve multiple activities
3. System components like camera or file picker
4. Emoji keyboard interactions

These limitations are noted in the test code with appropriate comments.