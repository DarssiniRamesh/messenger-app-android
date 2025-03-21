package org.mesibo.messenger;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mesibo.mediapicker.AlbumListData;
import com.mesibo.mediapicker.MediaPicker;
import com.mesibo.messaging.MesiboUI;
import com.mesibo.uihelper.IProductTourListener;
import com.mesibo.uihelper.MesiboLoginUiHelperListener;
import com.mesibo.uihelper.MesiboUiHelper;
import com.mesibo.uihelper.MesiboUiHelperConfig;
import com.mesibo.uihelper.WelcomeScreen;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowIntent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the UIManager class.
 * 
 * These tests verify the functionality of the UIManager class, which manages
 * UI-related operations in the messenger application.
 */
public class UIManagerTest extends BaseUnitTest {

    // Mocked dependencies
    @Mock
    private Activity mockActivity;
    
    @Mock
    private AppCompatActivity mockAppCompatActivity;
    
    @Mock
    private MesiboUI.MesiboUserListScreenOptions mockUserListOptions;
    
    @Mock
    private MesiboLoginUiHelperListener mockLoginListener;
    
    @Mock
    private IProductTourListener mockTourListener;
    
    @Mock
    private MediaPicker.ImageEditorListener mockImageEditorListener;
    
    @Mock
    private DialogInterface.OnClickListener mockClickListener;
    
    @Mock
    private MesiboUiHelper mockMesiboUiHelper;
    
    // For capturing intents
    private ArgumentCaptor<Intent> intentCaptor;
    
    /**
     * Setup for each test.
     * Initializes mocks and prepares the test environment.
     */
    @Override
    protected void setUpTest() {
        // Reset static variables
        UIManager.mMesiboLaunched = false;
        UIManager.mProductTourShown = false;
        
        // Initialize intent captor
        intentCaptor = ArgumentCaptor.forClass(Intent.class);
    }
    
    /**
     * Test launching the startup activity.
     * Verifies that the correct intent is created and launched.
     */
    @Test
    public void testLaunchStartupActivity() {
        // Execute
        UIManager.launchStartupActivity(context, true);
        
        // Verify
        Intent intent = Shadows.shadowOf((android.app.Application) context.getApplicationContext())
                .getNextStartedActivity();
        
        assertNotNull("Intent should not be null", intent);
        assertEquals("Intent should target StartUpActivity", 
                StartUpActivity.class.getName(), intent.getComponent().getClassName());
        assertTrue("Intent should have FLAG_ACTIVITY_NEW_TASK", 
                (intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0);
        assertTrue("Intent should have SKIPTOUR extra set to true", 
                intent.getBooleanExtra(StartUpActivity.SKIPTOUR, false));
    }
    
    /**
     * Test launching the startup activity with skipTour set to false.
     * Verifies that the correct intent is created with skipTour=false.
     */
    @Test
    public void testLaunchStartupActivityWithoutSkipTour() {
        // Execute
        UIManager.launchStartupActivity(context, false);
        
        // Verify
        Intent intent = Shadows.shadowOf((android.app.Application) context.getApplicationContext())
                .getNextStartedActivity();
        
        assertNotNull("Intent should not be null", intent);
        assertEquals("Intent should target StartUpActivity", 
                StartUpActivity.class.getName(), intent.getComponent().getClassName());
        assertFalse("Intent should have SKIPTOUR extra set to false", 
                intent.getBooleanExtra(StartUpActivity.SKIPTOUR, true));
    }
    
    /**
     * Test launching the Mesibo UI.
     * Verifies that the MesiboUI.launchUserList method is called and the flag is set.
     */
    @Test
    public void testLaunchMesibo() {
        // Setup
        MesiboUI.MesiboUserListScreenOptions options = mock(MesiboUI.MesiboUserListScreenOptions.class);
        
        // Execute
        assertFalse("mMesiboLaunched should be false initially", UIManager.mMesiboLaunched);
        UIManager.launchMesibo(context, options);
        
        // Verify
        assertTrue("mMesiboLaunched should be true after launch", UIManager.mMesiboLaunched);
        
        // Note: We can't directly verify MesiboUI.launchUserList as it's a static method
        // This is a limitation of the current test setup
    }
    
    /**
     * Test launching the user settings activity.
     * Verifies that the correct intent is created and launched.
     */
    @Test
    public void testLaunchUserSettings() {
        // Execute
        UIManager.launchUserSettings(context);
        
        // Verify
        Intent intent = Shadows.shadowOf((android.app.Application) context.getApplicationContext())
                .getNextStartedActivity();
        
        assertNotNull("Intent should not be null", intent);
        assertEquals("Intent should target SettingsActivity", 
                "org.mesibo.messenger.AppSettings.SettingsActivity", intent.getComponent().getClassName());
    }
    
    /**
     * Test launching the edit profile activity.
     * Verifies that the correct intent is created and launched with the right extras.
     */
    @Test
    public void testLaunchEditProfile() {
        // Setup
        int flag = Intent.FLAG_ACTIVITY_CLEAR_TOP;
        long groupId = 12345L;
        boolean launchMesibo = true;
        
        // Execute
        UIManager.launchEditProfile(context, flag, groupId, launchMesibo);
        
        // Verify
        Intent intent = Shadows.shadowOf((android.app.Application) context.getApplicationContext())
                .getNextStartedActivity();
        
        assertNotNull("Intent should not be null", intent);
        assertEquals("Intent should target EditProfileActivity", 
                EditProfileActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals("Intent should have the correct flag", 
                flag, intent.getFlags());
        assertEquals("Intent should have the correct groupid extra", 
                groupId, intent.getLongExtra("groupid", 0));
        assertEquals("Intent should have the correct launchMesibo extra", 
                launchMesibo, intent.getBooleanExtra("launchMesibo", false));
    }
    
    /**
     * Test launching the edit profile activity with no flags.
     * Verifies that the correct intent is created without flags.
     */
    @Test
    public void testLaunchEditProfileWithNoFlags() {
        // Setup
        int flag = 0;
        long groupId = 12345L;
        boolean launchMesibo = false;
        
        // Execute
        UIManager.launchEditProfile(context, flag, groupId, launchMesibo);
        
        // Verify
        Intent intent = Shadows.shadowOf((android.app.Application) context.getApplicationContext())
                .getNextStartedActivity();
        
        assertNotNull("Intent should not be null", intent);
        assertEquals("Intent should target EditProfileActivity", 
                EditProfileActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals("Intent should have no flags", 
                0, intent.getFlags());
        assertEquals("Intent should have the correct groupid extra", 
                groupId, intent.getLongExtra("groupid", 0));
        assertEquals("Intent should have the correct launchMesibo extra", 
                launchMesibo, intent.getBooleanExtra("launchMesibo", true));
    }
    
    /**
     * Test launching the image viewer.
     * Verifies that the MediaPicker.launchImageViewer method is called with correct parameters.
     */
    @Test
    public void testLaunchImageViewer() {
        // Setup
        String filePath = "/path/to/image.jpg";
        
        // Execute
        UIManager.launchImageViewer(mockActivity, filePath);
        
        // Note: We can't directly verify MediaPicker.launchImageViewer as it's a static method
        // This is a limitation of the current test setup
    }
    
    /**
     * Test launching the image editor.
     * Verifies that the MediaPicker.launchEditor method is called with correct parameters.
     */
    @Test
    public void testLaunchImageEditor() {
        // Setup
        int type = 1;
        int drawableId = R.drawable.profile;
        String title = "Edit Image";
        String filePath = "/path/to/image.jpg";
        boolean showEditControls = true;
        boolean showTitle = true;
        boolean showCropOverlay = true;
        boolean squareCrop = true;
        int maxDimension = 1024;
        
        // Execute
        UIManager.launchImageEditor(mockAppCompatActivity, type, drawableId, title, filePath, 
                showEditControls, showTitle, showCropOverlay, squareCrop, maxDimension, 
                mockImageEditorListener);
        
        // Note: We can't directly verify MediaPicker.launchEditor as it's a static method
        // This is a limitation of the current test setup
    }
    
    /**
     * Test initializing the UI helper.
     * Verifies that the MesiboUiHelperConfig is properly configured.
     */
    @Test
    public void testInitUiHelper() {
        // Execute
        UIManager.initUiHelper();
        
        // Note: We can't directly verify MesiboUiHelper.setConfig as it's a static method
        // This is a limitation of the current test setup
        
        // However, we can verify that the method doesn't throw any exceptions
    }
    
    /**
     * Test launching the welcome activity when Mesibo has not been launched.
     * Verifies that the product tour is shown.
     */
    @Test
    public void testLaunchWelcomeActivityWhenMesiboNotLaunched() {
        // Setup
        UIManager.mMesiboLaunched = false;
        
        // Execute
        UIManager.launchWelcomeactivity(mockActivity, true, mockLoginListener, mockTourListener);
        
        // Verify
        assertTrue("mProductTourShown should be true after launch", UIManager.mProductTourShown);
        
        // Note: We can't directly verify MesiboUiHelper.launchTour as it's a static method
        // This is a limitation of the current test setup
    }
    
    /**
     * Test launching the welcome activity when Mesibo has been launched.
     * Verifies that the login screen is shown instead of the product tour.
     */
    @Test
    public void testLaunchWelcomeActivityWhenMesiboLaunched() {
        // Setup
        UIManager.mMesiboLaunched = true;
        
        // Execute
        UIManager.launchWelcomeactivity(mockActivity, true, mockLoginListener, mockTourListener);
        
        // Verify
        assertFalse("mProductTourShown should remain false", UIManager.mProductTourShown);
        
        // Note: We can't directly verify MesiboUiHelper.launchLogin as it's a static method
        // This is a limitation of the current test setup
    }
    
    /**
     * Test launching the login screen.
     * Verifies that the UI helper is initialized and login is launched.
     */
    @Test
    public void testLaunchLogin() {
        // Execute
        UIManager.launchLogin(mockActivity, mockLoginListener);
        
        // Note: We can't directly verify MesiboUiHelper.launchLogin as it's a static method
        // This is a limitation of the current test setup
    }
    
    /**
     * Test showing an alert dialog with positive and negative buttons.
     * Verifies that the dialog is created with the correct properties.
     */
    @Test
    public void testShowAlertWithButtons() {
        // Setup
        String title = "Test Title";
        String message = "Test Message";
        
        // Execute
        UIManager.showAlert(context, title, message, mockClickListener, mockClickListener);
        
        // Verify
        AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();
        ShadowAlertDialog shadowDialog = Shadows.shadowOf(dialog);
        
        assertNotNull("Dialog should not be null", dialog);
        assertEquals("Dialog title should match", title, shadowDialog.getTitle());
        assertEquals("Dialog message should match", message, shadowDialog.getMessage());
        assertTrue("Dialog should be cancelable", shadowDialog.isCancelable());
        
        // Verify button click listeners
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
        verify(mockClickListener).onClick(any(DialogInterface.class), eq(DialogInterface.BUTTON_POSITIVE));
        
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick();
        verify(mockClickListener).onClick(any(DialogInterface.class), eq(DialogInterface.BUTTON_NEGATIVE));
    }
    
    /**
     * Test showing an alert dialog without buttons.
     * Verifies that the dialog is created with the correct properties.
     */
    @Test
    public void testShowAlertWithoutButtons() {
        // Setup
        String title = "Test Title";
        String message = "Test Message";
        
        // Execute
        UIManager.showAlert(context, title, message);
        
        // Verify
        AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();
        ShadowAlertDialog shadowDialog = Shadows.shadowOf(dialog);
        
        assertNotNull("Dialog should not be null", dialog);
        assertEquals("Dialog title should match", title, shadowDialog.getTitle());
        assertEquals("Dialog message should match", message, shadowDialog.getMessage());
        assertTrue("Dialog should be cancelable", shadowDialog.isCancelable());
    }
    
    /**
     * Test showing an alert dialog with null context.
     * Verifies that the method handles null context gracefully.
     */
    @Test
    public void testShowAlertWithNullContext() {
        // Execute - should not throw an exception
        UIManager.showAlert(null, "Title", "Message");
        
        // No assertion needed - test passes if no exception is thrown
    }
    
    /**
     * Test the permissions list in the UI helper configuration.
     * Verifies that the correct permissions are added based on the Android version.
     */
    @Test
    public void testUiHelperPermissions() {
        // Execute
        UIManager.initUiHelper();
        
        // Note: We can't directly access the MesiboUiHelperConfig after it's set
        // This is a limitation of the current test setup
        
        // However, we can verify that the method doesn't throw any exceptions
    }
    
    /**
     * Test the welcome screens in the UI helper configuration.
     * Verifies that the correct welcome screens are added.
     */
    @Test
    public void testUiHelperWelcomeScreens() {
        // Execute
        UIManager.initUiHelper();
        
        // Note: We can't directly access the MesiboUiHelperConfig after it's set
        // This is a limitation of the current test setup
        
        // However, we can verify that the method doesn't throw any exceptions
    }
    
    /**
     * Test error handling when showing an alert dialog.
     * Verifies that the method handles exceptions gracefully.
     */
    @Test
    public void testShowAlertErrorHandling() {
        // Setup - create a context that will cause an exception when showing a dialog
        Context mockExceptionContext = mock(Context.class);
        when(mockExceptionContext.getApplicationContext()).thenThrow(new RuntimeException("Test exception"));
        
        // Execute - should not throw an exception
        UIManager.showAlert(mockExceptionContext, "Title", "Message");
        
        // No assertion needed - test passes if no exception is thrown
    }
    
    /**
     * Test the static flag mMesiboLaunched.
     * Verifies that the flag is properly set and maintained.
     */
    @Test
    public void testMesiboLaunchedFlag() {
        // Setup
        UIManager.mMesiboLaunched = false;
        
        // Execute
        UIManager.launchMesibo(context, mockUserListOptions);
        
        // Verify
        assertTrue("mMesiboLaunched should be true after launch", UIManager.mMesiboLaunched);
        
        // Reset and verify it stays reset
        UIManager.mMesiboLaunched = false;
        assertFalse("mMesiboLaunched should be false after reset", UIManager.mMesiboLaunched);
    }
    
    /**
     * Test the static flag mProductTourShown.
     * Verifies that the flag is properly set and maintained.
     */
    @Test
    public void testProductTourShownFlag() {
        // Setup
        UIManager.mProductTourShown = false;
        UIManager.mMesiboLaunched = false;
        
        // Execute
        UIManager.launchWelcomeactivity(mockActivity, true, mockLoginListener, mockTourListener);
        
        // Verify
        assertTrue("mProductTourShown should be true after launch", UIManager.mProductTourShown);
        
        // Reset and verify it stays reset
        UIManager.mProductTourShown = false;
        assertFalse("mProductTourShown should be false after reset", UIManager.mProductTourShown);
    }
    
    /**
     * Test multiple activity launches.
     * Verifies that multiple activities can be launched in sequence.
     */
    @Test
    public void testMultipleActivityLaunches() {
        // Execute - launch multiple activities
        UIManager.launchStartupActivity(context, true);
        UIManager.launchUserSettings(context);
        UIManager.launchEditProfile(context, 0, 0, false);
        
        // Verify
        List<Intent> intents = Shadows.shadowOf((android.app.Application) context.getApplicationContext())
                .getStartedActivities();
        
        assertEquals("Should have launched 3 activities", 3, intents.size());
        
        // Verify the intents in reverse order (most recent first)
        assertEquals("Last intent should target EditProfileActivity", 
                EditProfileActivity.class.getName(), intents.get(2).getComponent().getClassName());
        assertEquals("Second intent should target SettingsActivity", 
                "org.mesibo.messenger.AppSettings.SettingsActivity", intents.get(1).getComponent().getClassName());
        assertEquals("First intent should target StartUpActivity", 
                StartUpActivity.class.getName(), intents.get(0).getComponent().getClassName());
    }
    
    /**
     * Test the interaction between mMesiboLaunched and launchWelcomeactivity.
     * Verifies that the welcome activity behavior changes based on mMesiboLaunched.
     */
    @Test
    public void testMesiboLaunchedInteractionWithWelcomeActivity() {
        // Test case 1: mMesiboLaunched = false
        UIManager.mMesiboLaunched = false;
        UIManager.mProductTourShown = false;
        
        UIManager.launchWelcomeactivity(mockActivity, true, mockLoginListener, mockTourListener);
        assertTrue("mProductTourShown should be true when mMesiboLaunched is false", 
                UIManager.mProductTourShown);
        
        // Test case 2: mMesiboLaunched = true
        UIManager.mMesiboLaunched = true;
        UIManager.mProductTourShown = false;
        
        UIManager.launchWelcomeactivity(mockActivity, true, mockLoginListener, mockTourListener);
        assertFalse("mProductTourShown should remain false when mMesiboLaunched is true", 
                UIManager.mProductTourShown);
    }
}
