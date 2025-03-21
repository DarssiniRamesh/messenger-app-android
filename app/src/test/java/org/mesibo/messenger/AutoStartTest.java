package org.mesibo.messenger;

import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the AutoStart class.
 * Tests the broadcast receiver functionality for handling boot completion and app restart intents.
 */
public class AutoStartTest extends BaseUnitTest {

    // Class under test
    private AutoStart autoStart;

    // Mocks
    @Mock
    private Context mockContext;

    @Override
    protected void setUpTest() {
        // Create the AutoStart instance
        autoStart = new AutoStart();
        
        // Initialize mock context
        mockContext = spy(context);
    }

    /**
     * Test that onReceive properly handles BOOT_COMPLETED intent.
     * Verifies that StartUpActivity is launched with the correct parameters.
     */
    @Test
    public void testOnReceiveWithBootCompletedIntent() {
        // Create a BOOT_COMPLETED intent
        Intent bootIntent = new Intent(Intent.ACTION_BOOT_COMPLETED);
        
        // Call the method under test
        autoStart.onReceive(mockContext, bootIntent);
        
        // Verify that the correct intent was sent to start the activity
        ShadowApplication shadowApplication = Shadows.shadowOf((android.app.Application) context.getApplicationContext());
        Intent startedIntent = shadowApplication.getNextStartedActivity();
        
        // Verify the intent properties
        assertNotNull("StartUpActivity should be launched", startedIntent);
        assertEquals("Intent should target StartUpActivity", StartUpActivity.class.getName(), startedIntent.getComponent().getClassName());
        assertTrue("Intent should have FLAG_ACTIVITY_NEW_TASK", (startedIntent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0);
        assertTrue("Intent should have FLAG_ACTIVITY_CLEAR_TOP", (startedIntent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP) != 0);
        assertTrue("Intent should have startInBackground=true", startedIntent.getBooleanExtra(StartUpActivity.STARTINBACKGROUND, false));
    }

    /**
     * Test that onReceive properly handles the app restart intent.
     * Verifies that StartUpActivity is launched with the correct parameters.
     */
    @Test
    public void testOnReceiveWithRestartIntent() {
        // Create a restart intent
        Intent restartIntent = new Intent(MainApplication.getRestartIntent());
        
        // Call the method under test
        autoStart.onReceive(mockContext, restartIntent);
        
        // Verify that the correct intent was sent to start the activity
        ShadowApplication shadowApplication = Shadows.shadowOf((android.app.Application) context.getApplicationContext());
        Intent startedIntent = shadowApplication.getNextStartedActivity();
        
        // Verify the intent properties
        assertNotNull("StartUpActivity should be launched", startedIntent);
        assertEquals("Intent should target StartUpActivity", StartUpActivity.class.getName(), startedIntent.getComponent().getClassName());
        assertTrue("Intent should have FLAG_ACTIVITY_NEW_TASK", (startedIntent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0);
        assertTrue("Intent should have FLAG_ACTIVITY_CLEAR_TOP", (startedIntent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP) != 0);
        assertTrue("Intent should have startInBackground=true", startedIntent.getBooleanExtra(StartUpActivity.STARTINBACKGROUND, false));
    }

    /**
     * Test that onReceive does not launch StartUpActivity for intents with different actions.
     * Verifies that no activity is started for unrelated intents.
     */
    @Test
    public void testOnReceiveWithDifferentIntent() {
        // Create an intent with a different action
        Intent differentIntent = new Intent("android.intent.action.DIFFERENT_ACTION");
        
        // Call the method under test
        autoStart.onReceive(mockContext, differentIntent);
        
        // Verify that no activity was started
        ShadowApplication shadowApplication = Shadows.shadowOf((android.app.Application) context.getApplicationContext());
        Intent startedIntent = shadowApplication.getNextStartedActivity();
        
        // No intent should be started for different actions
        assertEquals("No activity should be launched for different intent action", null, startedIntent);
    }

    /**
     * Test that onReceive handles null intent gracefully.
     * This is an edge case that should not occur in practice but should be handled safely.
     */
    @Test
    public void testOnReceiveWithNullIntent() {
        try {
            // Call the method under test with null intent
            autoStart.onReceive(mockContext, null);
            
            // If we reach here, no exception was thrown, which is not expected
            // The test should fail
            assertTrue("NullPointerException should be thrown for null intent", false);
        } catch (NullPointerException e) {
            // Expected behavior - a NullPointerException should be thrown
            assertTrue(true);
        }
    }

    /**
     * Test that onReceive handles null action in intent gracefully.
     * This is an edge case that should be handled safely.
     */
    @Test
    public void testOnReceiveWithNullAction() {
        // Create an intent with null action
        Intent nullActionIntent = new Intent();
        nullActionIntent.setAction(null);
        
        try {
            // Call the method under test
            autoStart.onReceive(mockContext, nullActionIntent);
            
            // If we reach here, no exception was thrown, which is not expected
            // The test should fail
            assertTrue("NullPointerException should be thrown for null action", false);
        } catch (NullPointerException e) {
            // Expected behavior - a NullPointerException should be thrown
            assertTrue(true);
        }
    }

    /**
     * Test that onReceive handles empty action in intent gracefully.
     * This is an edge case that should be handled safely.
     */
    @Test
    public void testOnReceiveWithEmptyAction() {
        // Create an intent with empty action
        Intent emptyActionIntent = new Intent("");
        
        // Call the method under test
        autoStart.onReceive(mockContext, emptyActionIntent);
        
        // Verify that no activity was started
        ShadowApplication shadowApplication = Shadows.shadowOf((android.app.Application) context.getApplicationContext());
        Intent startedIntent = shadowApplication.getNextStartedActivity();
        
        // No intent should be started for empty action
        assertEquals("No activity should be launched for empty intent action", null, startedIntent);
    }

    /**
     * Test that onReceive properly handles multiple intents in sequence.
     * Verifies that StartUpActivity is launched correctly for each valid intent.
     */
    @Test
    public void testOnReceiveWithMultipleIntents() {
        // Create intents
        Intent bootIntent = new Intent(Intent.ACTION_BOOT_COMPLETED);
        Intent restartIntent = new Intent(MainApplication.getRestartIntent());
        Intent differentIntent = new Intent("android.intent.action.DIFFERENT_ACTION");
        
        // Call the method under test for each intent
        autoStart.onReceive(mockContext, bootIntent);
        autoStart.onReceive(mockContext, differentIntent);
        autoStart.onReceive(mockContext, restartIntent);
        
        // Verify that the correct intents were sent to start activities
        ShadowApplication shadowApplication = Shadows.shadowOf((android.app.Application) context.getApplicationContext());
        List<Intent> startedIntents = shadowApplication.getStartedActivities();
        
        // Should have 2 started activities (for boot and restart intents)
        assertEquals("Two activities should be launched", 2, startedIntents.size());
        
        // Verify both intents target StartUpActivity
        for (Intent intent : startedIntents) {
            assertEquals("Intent should target StartUpActivity", StartUpActivity.class.getName(), intent.getComponent().getClassName());
            assertTrue("Intent should have startInBackground=true", intent.getBooleanExtra(StartUpActivity.STARTINBACKGROUND, false));
        }
    }

    /**
     * Test that onReceive properly handles case-sensitive intent actions.
     * Verifies that only exact matches trigger StartUpActivity launch.
     */
    @Test
    public void testOnReceiveWithCaseSensitiveActions() {
        // Create intents with different case
        Intent properCaseIntent = new Intent(Intent.ACTION_BOOT_COMPLETED);
        Intent lowerCaseIntent = new Intent(Intent.ACTION_BOOT_COMPLETED.toLowerCase());
        Intent upperCaseIntent = new Intent(Intent.ACTION_BOOT_COMPLETED.toUpperCase());
        
        // Call the method under test for each intent
        autoStart.onReceive(mockContext, properCaseIntent);
        autoStart.onReceive(mockContext, lowerCaseIntent);
        autoStart.onReceive(mockContext, upperCaseIntent);
        
        // Verify that the correct intents were sent to start activities
        ShadowApplication shadowApplication = Shadows.shadowOf((android.app.Application) context.getApplicationContext());
        List<Intent> startedIntents = shadowApplication.getStartedActivities();
        
        // Should have 1 started activity (only for the proper case intent)
        assertEquals("Only one activity should be launched", 1, startedIntents.size());
    }
}
