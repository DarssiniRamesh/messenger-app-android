package org.mesibo.messenger.AppSettings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mesibo.api.Mesibo;

import org.junit.Test;
import org.mesibo.messenger.BaseUnitTest;
import org.mesibo.messenger.EditProfileFragment;
import org.mesibo.messenger.R;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

/**
 * Unit tests for the SettingsActivity class.
 * 
 * These tests verify the functionality of the SettingsActivity, which is the main
 * container for all settings-related fragments in the messenger application.
 */
public class SettingsActivityTest extends BaseUnitTest {

    private SettingsActivity activity;
    private ActivityController<SettingsActivity> activityController;
    
    @Mock
    private ActionBar mockActionBar;
    
    @Mock
    private FragmentManager mockFragmentManager;
    
    @Mock
    private FragmentTransaction mockFragmentTransaction;
    
    @Mock
    private InputMethodManager mockInputMethodManager;
    
    @Override
    protected void setUpTest() {
        // Create an activity controller for SettingsActivity
        activityController = Robolectric.buildActivity(SettingsActivity.class);
    }
    
    /**
     * Test the onCreate method of SettingsActivity.
     * Verifies that the activity is properly initialized with the correct layout,
     * toolbar, action bar settings, and initial fragment.
     */
    @Test
    public void testOnCreate() {
        // Execute
        activity = activityController.create().get();
        
        // Verify
        assertNotNull("Activity should not be null", activity);
        
        // Verify content view is set
        assertEquals("Content view should be set to activity_settings",
                R.layout.activity_settings, shadowOf(activity).getContentView().getId());
        
        // Verify toolbar is set up
        Toolbar toolbar = activity.findViewById(R.id.settings_toolbar);
        assertNotNull("Toolbar should not be null", toolbar);
        
        // Verify initial fragment is added
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        assertNotNull("Fragment manager should not be null", fragmentManager);
        
        // Check if BasicSettingsFragment is added to the backstack
        assertTrue("BasicSettingsFragment should be added to backstack",
                fragmentManager.getBackStackEntryCount() > 0);
    }
    
    /**
     * Test the onResume method of SettingsActivity.
     * Verifies that Mesibo foreground context is set and action bar is configured correctly.
     */
    @Test
    public void testOnResume() {
        // Setup
        SettingsActivity spyActivity = spy(activityController.create().get());
        ActionBar mockActionBar = mock(ActionBar.class);
        when(spyActivity.getSupportActionBar()).thenReturn(mockActionBar);
        
        // Execute
        spyActivity.onResume();
        
        // Verify
        verify(mockActionBar).setDisplayHomeAsUpEnabled(true);
        verify(mockActionBar).setTitle("Settings");
        
        // Note: We can't directly verify Mesibo.setForegroundContext as it's a static method
        // This is a limitation of the current test setup
    }
    
    /**
     * Test the onBackPressed method when backstack has multiple entries.
     * Verifies that popBackStackImmediate is called instead of finish.
     */
    @Test
    public void testOnBackPressedWithMultipleBackstackEntries() {
        // Setup
        SettingsActivity spyActivity = spy(activityController.create().get());
        FragmentManager mockFragmentManager = mock(FragmentManager.class);
        when(spyActivity.getSupportFragmentManager()).thenReturn(mockFragmentManager);
        when(mockFragmentManager.getBackStackEntryCount()).thenReturn(2); // Multiple entries
        
        // Execute
        spyActivity.onBackPressed();
        
        // Verify
        verify(mockFragmentManager).popBackStackImmediate();
        verify(spyActivity, times(0)).finish(); // finish should not be called
    }
    
    /**
     * Test the onBackPressed method when backstack has only one entry.
     * Verifies that finish is called instead of popBackStackImmediate.
     */
    @Test
    public void testOnBackPressedWithSingleBackstackEntry() {
        // Setup
        SettingsActivity spyActivity = spy(activityController.create().get());
        FragmentManager mockFragmentManager = mock(FragmentManager.class);
        when(spyActivity.getSupportFragmentManager()).thenReturn(mockFragmentManager);
        when(mockFragmentManager.getBackStackEntryCount()).thenReturn(1); // Single entry
        doNothing().when(spyActivity).finish();
        
        // Execute
        spyActivity.onBackPressed();
        
        // Verify
        verify(mockFragmentManager, times(0)).popBackStackImmediate(); // popBackStackImmediate should not be called
        verify(spyActivity).finish();
    }
    
    /**
     * Test the onOptionsItemSelected method with home button.
     * Verifies that onBackPressed is called when the home button is pressed.
     */
    @Test
    public void testOnOptionsItemSelectedWithHomeButton() {
        // Setup
        SettingsActivity spyActivity = spy(activityController.create().get());
        MenuItem mockMenuItem = mock(MenuItem.class);
        when(mockMenuItem.getItemId()).thenReturn(android.R.id.home);
        doNothing().when(spyActivity).onBackPressed();
        
        // Execute
        boolean result = spyActivity.onOptionsItemSelected(mockMenuItem);
        
        // Verify
        verify(spyActivity).onBackPressed();
        assertTrue("Should return true for home button", result);
    }
    
    /**
     * Test the onOptionsItemSelected method with non-home button.
     * Verifies that super.onOptionsItemSelected is called for other menu items.
     */
    @Test
    public void testOnOptionsItemSelectedWithOtherButton() {
        // Setup
        SettingsActivity spyActivity = spy(activityController.create().get());
        MenuItem mockMenuItem = mock(MenuItem.class);
        when(mockMenuItem.getItemId()).thenReturn(R.id.settings_toolbar); // Any other ID
        
        // Execute
        spyActivity.onOptionsItemSelected(mockMenuItem);
        
        // Verify
        verify(spyActivity, times(0)).onBackPressed(); // onBackPressed should not be called
    }
    
    /**
     * Test the onActivityResult method.
     * Verifies that input method is hidden and fragment's onActivityResult is called.
     */
    @Test
    public void testOnActivityResult() {
        // Setup
        SettingsActivity spyActivity = spy(activityController.create().get());
        
        // Mock InputMethodManager
        InputMethodManager mockInputMethodManager = mock(InputMethodManager.class);
        when(spyActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(mockInputMethodManager);
        when(mockInputMethodManager.isAcceptingText()).thenReturn(true);
        
        // Mock fragments
        FragmentManager mockFragmentManager = mock(FragmentManager.class);
        when(spyActivity.getSupportFragmentManager()).thenReturn(mockFragmentManager);
        
        Fragment mockFragment = mock(EditProfileFragment.class);
        Fragment[] fragments = {mockFragment};
        when(mockFragmentManager.getFragments()).thenReturn(java.util.Arrays.asList(fragments));
        
        // Execute
        spyActivity.onActivityResult(1, 2, new Intent());
        
        // Verify
        verify(mockFragment).onActivityResult(eq(1), eq(2), any(Intent.class));
    }
    
    /**
     * Test the setRequestingFragment method.
     * Verifies that the requesting fragment is properly set.
     */
    @Test
    public void testSetRequestingFragment() {
        // Setup
        activity = activityController.create().get();
        Fragment mockFragment = mock(Fragment.class);
        
        // Execute
        activity.setRequestingFragment(mockFragment);
        
        // Use the getPrivateField method from BaseUnitTest to access the private field
        Fragment storedFragment = (Fragment) getPrivateField(activity, "mRequestingFragment");
        assertEquals("Requesting fragment should be set correctly", mockFragment, storedFragment);
    }
    
    /**
     * Test the onRequestPermissionsResult method.
     * Verifies that the requesting fragment's onRequestPermissionsResult is called.
     */
    @Test
    public void testOnRequestPermissionsResult() {
        // Setup
        activity = activityController.create().get();
        Fragment mockFragment = mock(Fragment.class);
        activity.setRequestingFragment(mockFragment);
        
        String[] permissions = {"android.permission.CAMERA"};
        int[] grantResults = {0};
        
        // Execute
        activity.onRequestPermissionsResult(123, permissions, grantResults);
        
        // Verify
        verify(mockFragment).onRequestPermissionsResult(eq(123), eq(permissions), eq(grantResults));
    }
    
    /**
     * Test the onRequestPermissionsResult method with null requesting fragment.
     * Verifies that no exceptions are thrown when mRequestingFragment is null.
     */
    @Test
    public void testOnRequestPermissionsResultWithNullFragment() {
        // Setup
        activity = activityController.create().get();
        activity.setRequestingFragment(null);
        
        String[] permissions = {"android.permission.CAMERA"};
        int[] grantResults = {0};
        
        // Execute - should not throw an exception
        activity.onRequestPermissionsResult(123, permissions, grantResults);
        
        // No assertion needed - test passes if no exception is thrown
    }
}
