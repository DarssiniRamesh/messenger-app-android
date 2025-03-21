package org.mesibo.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.mesibo.emojiview.EmojiconEditText;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * UI tests for EditProfileActivity
 * 
 * Tests cover:
 * - Form inputs and validation
 * - Profile picture selection
 * - Save button functionality
 * - Navigation and UI elements
 */
@RunWith(AndroidJUnit4.class)
public class EditProfileActivityTest {

    @Rule
    public ActivityTestRule<EditProfileActivity> activityRule =
            new ActivityTestRule<>(EditProfileActivity.class, true, false);

    @Before
    public void setUp() {
        Intents.init();
        // Create intent with necessary extras
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EditProfileActivity.class);
        Bundle args = new Bundle();
        args.putLong("groupid", 0); // Default value for non-group profile
        args.putBoolean("launchMesibo", false);
        intent.putExtras(args);
        
        // Launch activity with intent
        activityRule.launchActivity(intent);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Test that all UI elements are displayed correctly
     */
    @Test
    public void testUIElementsDisplayed() {
        // Check that profile image is displayed
        onView(withId(R.id.self_user_image)).check(matches(isDisplayed()));
        
        // Check that edit profile image button is displayed
        onView(withId(R.id.edit_user_image)).check(matches(isDisplayed()));
        
        // Check that name input field is displayed
        onView(withId(R.id.name_emoji_edittext)).check(matches(isDisplayed()));
        
        // Check that status input field is displayed
        onView(withId(R.id.status_emoji_edittext)).check(matches(isDisplayed()));
        
        // Check that phone number field is displayed
        onView(withId(R.id.profile_self_phone)).check(matches(isDisplayed()));
        
        // Check that save button is displayed
        onView(withId(R.id.register_profile_save)).check(matches(isDisplayed()));
        
        // Check that name character counter is displayed
        onView(withId(R.id.name_char_counter)).check(matches(isDisplayed()));
        
        // Check that status character counter is displayed
        onView(withId(R.id.status_char_counter)).check(matches(isDisplayed()));
        
        // Check that emoji buttons are displayed
        onView(withId(R.id.name_emoji_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.status_emoji_btn)).check(matches(isDisplayed()));
    }

    /**
     * Test form input validation for name field
     */
    @Test
    public void testNameInputValidation() {
        // Type a valid name
        onView(withId(R.id.name_emoji_edittext))
                .perform(click(), replaceText("Test User"), closeSoftKeyboard());
        
        // Verify character counter updates correctly (50 - 9 = 41)
        onView(withId(R.id.name_char_counter)).check(matches(withText("41")));
        
        // Type a very long name (should be limited to 50 chars)
        String longName = "This is a very long name that should be truncated to 50 characters maximum";
        onView(withId(R.id.name_emoji_edittext))
                .perform(click(), replaceText(longName), closeSoftKeyboard());
        
        // Verify character counter shows 0 (max reached)
        onView(withId(R.id.name_char_counter)).check(matches(withText("0")));
        
        // Verify text is truncated to 50 chars
        onView(withId(R.id.name_emoji_edittext))
                .check(matches(withText(longName.substring(0, 50))));
        
        // Clear the field and verify counter resets
        onView(withId(R.id.name_emoji_edittext))
                .perform(click(), replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.name_char_counter)).check(matches(withText("50")));
    }

    /**
     * Test status input validation
     */
    @Test
    public void testStatusInputValidation() {
        // Type a valid status
        onView(withId(R.id.status_emoji_edittext))
                .perform(click(), replaceText("Available"), closeSoftKeyboard());
        
        // Verify character counter updates correctly (150 - 9 = 141)
        onView(withId(R.id.status_char_counter)).check(matches(withText("141")));
        
        // Type a very long status (should be limited to 150 chars)
        String longStatus = "This is a very long status message that should be truncated to 150 characters maximum. " +
                "I am adding more text to make sure it exceeds the limit and gets truncated properly by the app.";
        onView(withId(R.id.status_emoji_edittext))
                .perform(click(), replaceText(longStatus), closeSoftKeyboard());
        
        // Verify character counter shows 0 (max reached)
        onView(withId(R.id.status_char_counter)).check(matches(withText("0")));
        
        // Verify text is truncated to 150 chars
        onView(withId(R.id.status_emoji_edittext))
                .check(matches(withText(longStatus.substring(0, 150))));
        
        // Clear the field and verify counter resets
        onView(withId(R.id.status_emoji_edittext))
                .perform(click(), replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.status_char_counter)).check(matches(withText("150")));
    }

    /**
     * Test profile picture interaction
     */
    @Test
    public void testProfilePictureInteraction() {
        // Click on profile image
        onView(withId(R.id.self_user_image)).perform(click());
        
        // Click on edit profile image button
        onView(withId(R.id.edit_user_image)).perform(click());
        
        // Note: We can't fully test the image picker dialog in an instrumented test
        // without mocking the system components
    }

    /**
     * Test save button functionality
     */
    @Test
    public void testSaveButtonFunctionality() {
        // Enter test data
        onView(withId(R.id.name_emoji_edittext))
                .perform(click(), replaceText("Test User"), closeSoftKeyboard());
        
        onView(withId(R.id.status_emoji_edittext))
                .perform(click(), replaceText("Test Status"), closeSoftKeyboard());
        
        // Click save button
        onView(withId(R.id.register_profile_save)).perform(click());
        
        // Note: Since we can't fully test the save functionality in an instrumented test
        // without mocking the backend, we're just verifying the button click works
    }

    /**
     * Test emoji button functionality
     */
    @Test
    public void testEmojiButtonFunctionality() {
        // Click on name emoji button
        onView(withId(R.id.name_emoji_btn)).perform(click());
        
        // Click on status emoji button
        onView(withId(R.id.status_emoji_btn)).perform(click());
        
        // Note: Full emoji keyboard testing is limited in instrumented tests
    }

    /**
     * Test form interaction sequence
     */
    @Test
    public void testFormInteractionSequence() {
        // Test a complete form interaction sequence
        
        // 1. Enter name
        onView(withId(R.id.name_emoji_edittext))
                .perform(click(), replaceText("John Doe"), closeSoftKeyboard());
        
        // 2. Enter status
        onView(withId(R.id.status_emoji_edittext))
                .perform(click(), replaceText("Testing the app"), closeSoftKeyboard());
        
        // 3. Verify inputs
        onView(withId(R.id.name_emoji_edittext)).check(matches(withText("John Doe")));
        onView(withId(R.id.status_emoji_edittext)).check(matches(withText("Testing the app")));
        
        // 4. Click save
        onView(withId(R.id.register_profile_save)).perform(click());
    }

    /**
     * Test navigation (back button)
     */
    @Test
    public void testNavigation() {
        // Press back (should finish activity)
        pressBack();
        
        // Note: We can't easily verify activity is finished in instrumented test
    }
    
    /**
     * Test toolbar navigation
     */
    @Test
    public void testToolbarNavigation() {
        // Click on the home/up button in the toolbar
        onView(withId(android.R.id.home)).perform(click());
        
        // Note: We can't easily verify activity is finished in instrumented test
    }
}
