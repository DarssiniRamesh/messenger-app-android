package org.mesibo.messenger.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.junit.Before;
import org.junit.Test;
import org.mesibo.messenger.BaseUnitTest;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link AppUtils} class.
 * 
 * These tests verify the permission handling functionality in the AppUtils class,
 * including scenarios for granted permissions, denied permissions, rationale needed,
 * and edge cases like null parameters and empty permission arrays.
 */
public class AppUtilsTest extends BaseUnitTest {

    private static final int TEST_REQUEST_CODE = 123;
    private static final String TEST_PERMISSION = Manifest.permission.CAMERA;

    @Mock
    private AppCompatActivity mockActivity;

    /**
     * Setup for each test.
     */
    @Override
    protected void setUpTest() {
        // Additional setup specific to AppUtilsTest
    }

    /**
     * Test that aquireUserPermission returns true when permission is already granted.
     */
    @Test
    public void testPermissionAlreadyGranted() {
        try (MockedStatic<ContextCompat> contextCompatMock = Mockito.mockStatic(ContextCompat.class)) {
            // Mock ContextCompat to return PERMISSION_GRANTED
            contextCompatMock.when(() -> ContextCompat.checkSelfPermission(any(Context.class), anyString()))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            // Call the method under test
            boolean result = AppUtils.aquireUserPermission(mockActivity, TEST_PERMISSION, TEST_REQUEST_CODE);

            // Verify the result is true (permission granted)
            assertTrue("Should return true when permission is already granted", result);
            
            // Verify that requestPermissions was not called
            try (MockedStatic<ActivityCompat> activityCompatMock = Mockito.mockStatic(ActivityCompat.class)) {
                activityCompatMock.verify(() -> 
                    ActivityCompat.requestPermissions(any(AppCompatActivity.class), any(String[].class), anyInt()), 
                    never());
            }
        }
    }

    /**
     * Test that aquireUserPermission returns false and requests permission when permission is denied
     * and rationale is not needed.
     */
    @Test
    public void testPermissionDeniedNoRationale() {
        try (MockedStatic<ContextCompat> contextCompatMock = Mockito.mockStatic(ContextCompat.class);
             MockedStatic<ActivityCompat> activityCompatMock = Mockito.mockStatic(ActivityCompat.class)) {
            
            // Mock ContextCompat to return PERMISSION_DENIED
            contextCompatMock.when(() -> ContextCompat.checkSelfPermission(any(Context.class), anyString()))
                    .thenReturn(PackageManager.PERMISSION_DENIED);
            
            // Mock ActivityCompat.shouldShowRequestPermissionRationale to return false
            activityCompatMock.when(() -> ActivityCompat.shouldShowRequestPermissionRationale(any(AppCompatActivity.class), anyString()))
                    .thenReturn(false);
            
            // Call the method under test
            boolean result = AppUtils.aquireUserPermission(mockActivity, TEST_PERMISSION, TEST_REQUEST_CODE);
            
            // Verify the result is false (permission not granted)
            assertFalse("Should return false when permission is denied", result);
            
            // Verify that requestPermissions was called with the correct parameters
            activityCompatMock.verify(() -> 
                ActivityCompat.requestPermissions(
                    eq(mockActivity), 
                    eq(new String[]{TEST_PERMISSION}), 
                    eq(TEST_REQUEST_CODE)
                ), 
                times(1));
        }
    }

    /**
     * Test that aquireUserPermission returns false when permission is denied
     * and rationale is needed.
     */
    @Test
    public void testPermissionDeniedWithRationale() {
        try (MockedStatic<ContextCompat> contextCompatMock = Mockito.mockStatic(ContextCompat.class);
             MockedStatic<ActivityCompat> activityCompatMock = Mockito.mockStatic(ActivityCompat.class)) {
            
            // Mock ContextCompat to return PERMISSION_DENIED
            contextCompatMock.when(() -> ContextCompat.checkSelfPermission(any(Context.class), anyString()))
                    .thenReturn(PackageManager.PERMISSION_DENIED);
            
            // Mock ActivityCompat.shouldShowRequestPermissionRationale to return true
            activityCompatMock.when(() -> ActivityCompat.shouldShowRequestPermissionRationale(any(AppCompatActivity.class), anyString()))
                    .thenReturn(true);
            
            // Call the method under test
            boolean result = AppUtils.aquireUserPermission(mockActivity, TEST_PERMISSION, TEST_REQUEST_CODE);
            
            // Verify the result is false (permission not granted)
            assertFalse("Should return false when permission is denied and rationale is needed", result);
            
            // Verify that requestPermissions was not called
            activityCompatMock.verify(() -> 
                ActivityCompat.requestPermissions(
                    any(AppCompatActivity.class), 
                    any(String[].class), 
                    anyInt()
                ), 
                never());
        }
    }

    /**
     * Test that aquireUserPermission handles null context parameter.
     * This test expects a NullPointerException to be thrown.
     */
    @Test(expected = NullPointerException.class)
    public void testNullContext() {
        AppUtils.aquireUserPermission(null, TEST_PERMISSION, TEST_REQUEST_CODE);
    }

    /**
     * Test that aquireUserPermission handles null permission parameter.
     * This test expects a NullPointerException to be thrown.
     */
    @Test(expected = NullPointerException.class)
    public void testNullPermission() {
        AppUtils.aquireUserPermission(mockActivity, null, TEST_REQUEST_CODE);
    }

    /**
     * Test that aquireUserPermission handles empty permission string.
     */
    @Test
    public void testEmptyPermission() {
        try (MockedStatic<ContextCompat> contextCompatMock = Mockito.mockStatic(ContextCompat.class)) {
            // Mock ContextCompat to return PERMISSION_DENIED for empty permission
            contextCompatMock.when(() -> ContextCompat.checkSelfPermission(any(Context.class), eq("")))
                    .thenReturn(PackageManager.PERMISSION_DENIED);
            
            try (MockedStatic<ActivityCompat> activityCompatMock = Mockito.mockStatic(ActivityCompat.class)) {
                // Mock ActivityCompat.shouldShowRequestPermissionRationale to return false
                activityCompatMock.when(() -> ActivityCompat.shouldShowRequestPermissionRationale(any(AppCompatActivity.class), eq("")))
                        .thenReturn(false);
                
                // Call the method under test
                boolean result = AppUtils.aquireUserPermission(mockActivity, "", TEST_REQUEST_CODE);
                
                // Verify the result is false (permission not granted)
                assertFalse("Should return false for empty permission string", result);
                
                // Verify that requestPermissions was called with the empty permission
                activityCompatMock.verify(() -> 
                    ActivityCompat.requestPermissions(
                        eq(mockActivity), 
                        eq(new String[]{""}), 
                        eq(TEST_REQUEST_CODE)
                    ), 
                    times(1));
            }
        }
    }

    /**
     * Test that aquireUserPermission handles multiple permission requests correctly.
     * Note: This test is to verify the behavior if the method were to be extended to support
     * multiple permissions, which it currently doesn't directly support.
     */
    @Test
    public void testMultiplePermissionRequests() {
        try (MockedStatic<ContextCompat> contextCompatMock = Mockito.mockStatic(ContextCompat.class)) {
            // First call: Permission granted
            contextCompatMock.when(() -> ContextCompat.checkSelfPermission(any(Context.class), eq(Manifest.permission.CAMERA)))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            
            boolean result1 = AppUtils.aquireUserPermission(mockActivity, Manifest.permission.CAMERA, TEST_REQUEST_CODE);
            assertTrue("Should return true for granted permission", result1);
            
            // Second call: Permission denied
            contextCompatMock.when(() -> ContextCompat.checkSelfPermission(any(Context.class), eq(Manifest.permission.RECORD_AUDIO)))
                    .thenReturn(PackageManager.PERMISSION_DENIED);
            
            try (MockedStatic<ActivityCompat> activityCompatMock = Mockito.mockStatic(ActivityCompat.class)) {
                activityCompatMock.when(() -> ActivityCompat.shouldShowRequestPermissionRationale(any(AppCompatActivity.class), eq(Manifest.permission.RECORD_AUDIO)))
                        .thenReturn(false);
                
                boolean result2 = AppUtils.aquireUserPermission(mockActivity, Manifest.permission.RECORD_AUDIO, TEST_REQUEST_CODE);
                assertFalse("Should return false for denied permission", result2);
                
                // Verify requestPermissions was called for the second permission
                activityCompatMock.verify(() -> 
                    ActivityCompat.requestPermissions(
                        eq(mockActivity), 
                        eq(new String[]{Manifest.permission.RECORD_AUDIO}), 
                        eq(TEST_REQUEST_CODE)
                    ), 
                    times(1));
            }
        }
    }
}