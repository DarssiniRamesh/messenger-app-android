package org.mesibo.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowLog;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the AppConfig class.
 * 
 * These tests verify the functionality of the AppConfig class, which manages application
 * configuration and settings using SharedPreferences.
 */
public class AppConfigTest extends BaseUnitTest {

    // Test constants
    private static final String TEST_KEY = "test_key";
    private static final String TEST_STRING_VALUE = "test_value";
    private static final int TEST_INT_VALUE = 42;
    private static final long TEST_LONG_VALUE = 1234567890L;
    private static final boolean TEST_BOOLEAN_VALUE = true;
    
    // The class under test
    private AppConfig appConfig;
    
    // Mocked dependencies
    @Mock
    private SharedPreferences mockSharedPreferences;
    
    @Mock
    private SharedPreferences.Editor mockEditor;
    
    /**
     * Setup for each test.
     * Creates a spy of AppConfig with mocked dependencies.
     */
    @Override
    protected void setUpTest() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
        
        // Setup mock SharedPreferences
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        when(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor);
        when(mockEditor.putLong(anyString(), anyLong())).thenReturn(mockEditor);
        when(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor);
        when(mockEditor.commit()).thenReturn(true);
        
        // Create a real context that will be used by AppConfig
        Context realContext = RuntimeEnvironment.application;
        
        // Create the AppConfig instance with the real context
        appConfig = new AppConfig(realContext);
        
        // Reset the static instance for each test
        try {
            TestUtils.setPrivateField(AppConfig.class, "_instance", null);
        } catch (Exception e) {
            // Ignore exception
        }
        
        // Create a spy of AppConfig that uses our mocked SharedPreferences
        appConfig = spy(new AppConfig(context));
        doReturn(mockSharedPreferences).when(appConfig).getSharedPreferences();
    }
    
    /**
     * Helper method to inject mocked SharedPreferences into AppConfig
     */
    private void injectMockedSharedPreferences() {
        try {
            TestUtils.setPrivateField(appConfig, "mSharedPref", mockSharedPreferences);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocked SharedPreferences", e);
        }
    }
    
    /**
     * Helper method to get SharedPreferences for testing
     */
    private SharedPreferences getSharedPreferences() {
        return mockSharedPreferences;
    }

    /**
     * Test initialization of configuration values.
     * Verifies that AppConfig properly initializes when first created.
     */
    @Test
    public void testInitialization() {
        // Setup
        when(mockSharedPreferences.contains(AppConfig.systemPreferenceKey)).thenReturn(false);
        when(mockSharedPreferences.getString(eq(AppConfig.systemPreferenceKey), anyString())).thenReturn("");
        
        // Create a new AppConfig instance with our mocked dependencies
        AppConfig config = new AppConfig(context);
        injectMockedSharedPreferences();
        
        // Verify
        assertTrue(config.isFirstTime());
        assertNotNull(AppConfig.getConfig());
        assertNotNull(AppConfig.getConfig().uniqueid);
        verify(mockEditor).putString(eq(AppConfig.systemPreferenceKey), anyString());
        verify(mockEditor).commit();
    }

    /**
     * Test storage and retrieval of string values.
     * Verifies that string values can be stored and retrieved correctly.
     */
    @Test
    public void testStringValueStorageAndRetrieval() {
        // Setup
        injectMockedSharedPreferences();
        when(mockSharedPreferences.contains(TEST_KEY)).thenReturn(true);
        when(mockSharedPreferences.getString(TEST_KEY, null)).thenReturn(TEST_STRING_VALUE);
        
        // Execute
        boolean setResult = appConfig.setStringValue(TEST_KEY, TEST_STRING_VALUE);
        String retrievedValue = appConfig.getStringValue(TEST_KEY, null);
        
        // Verify
        assertTrue(setResult);
        assertEquals(TEST_STRING_VALUE, retrievedValue);
        verify(mockEditor).putString(TEST_KEY, TEST_STRING_VALUE);
        verify(mockEditor).commit();
    }

    /**
     * Test storage and retrieval of integer values.
     * Verifies that integer values can be stored and retrieved correctly.
     */
    @Test
    public void testIntValueStorageAndRetrieval() {
        // Setup
        injectMockedSharedPreferences();
        when(mockSharedPreferences.contains(TEST_KEY)).thenReturn(true);
        when(mockSharedPreferences.getInt(TEST_KEY, 0)).thenReturn(TEST_INT_VALUE);
        
        // Execute
        boolean setResult = appConfig.setIntValue(TEST_KEY, TEST_INT_VALUE);
        int retrievedValue = appConfig.getIntValue(TEST_KEY, 0);
        
        // Verify
        assertTrue(setResult);
        assertEquals(TEST_INT_VALUE, retrievedValue);
        verify(mockEditor).putInt(TEST_KEY, TEST_INT_VALUE);
        verify(mockEditor).commit();
    }

    /**
     * Test storage and retrieval of long values.
     * Verifies that long values can be stored and retrieved correctly.
     */
    @Test
    public void testLongValueStorageAndRetrieval() {
        // Setup
        injectMockedSharedPreferences();
        when(mockSharedPreferences.contains(TEST_KEY)).thenReturn(true);
        when(mockSharedPreferences.getLong(TEST_KEY, 0L)).thenReturn(TEST_LONG_VALUE);
        
        // Execute
        boolean setResult = appConfig.setLongValue(TEST_KEY, TEST_LONG_VALUE);
        long retrievedValue = appConfig.getLongValue(TEST_KEY, 0L);
        
        // Verify
        assertTrue(setResult);
        assertEquals(TEST_LONG_VALUE, retrievedValue);
        verify(mockEditor).putLong(TEST_KEY, TEST_LONG_VALUE);
        verify(mockEditor).commit();
    }

    /**
     * Test storage and retrieval of boolean values.
     * Verifies that boolean values can be stored and retrieved correctly.
     */
    @Test
    public void testBooleanValueStorageAndRetrieval() {
        // Setup
        injectMockedSharedPreferences();
        when(mockSharedPreferences.contains(TEST_KEY)).thenReturn(true);
        when(mockSharedPreferences.getBoolean(TEST_KEY, false)).thenReturn(TEST_BOOLEAN_VALUE);
        
        // Execute
        boolean setResult = appConfig.setBooleanValue(TEST_KEY, TEST_BOOLEAN_VALUE);
        boolean retrievedValue = appConfig.getBooleanValue(TEST_KEY, false);
        
        // Verify
        assertTrue(setResult);
        assertEquals(TEST_BOOLEAN_VALUE, retrievedValue);
        verify(mockEditor).putBoolean(TEST_KEY, TEST_BOOLEAN_VALUE);
        verify(mockEditor).commit();
    }

    /**
     * Test reset functionality.
     * Verifies that the reset method properly clears configuration values.
     */
    @Test
    public void testReset() {
        // Setup
        injectMockedSharedPreferences();
        AppConfig.Configuration config = AppConfig.getConfig();
        config.token = "test_token";
        config.phone = "test_phone";
        config.uploadurl = "test_upload_url";
        config.downloadurl = "test_download_url";
        String uniqueId = "test_unique_id";
        config.uniqueid = uniqueId;
        
        // Execute
        AppConfig.reset();
        
        // Verify
        assertEquals("", config.token);
        assertEquals("", config.phone);
        assertEquals("", config.uploadurl);
        assertEquals("", config.downloadurl);
        assertEquals(uniqueId, config.uniqueid); // uniqueid should not be reset
        verify(mockEditor).putString(eq(AppConfig.systemPreferenceKey), anyString());
        verify(mockEditor).commit();
    }

    /**
     * Test handling of null values.
     * Verifies that null values are handled correctly.
     */
    @Test
    public void testNullValueHandling() {
        // Setup
        injectMockedSharedPreferences();
        
        // Execute & Verify for String
        boolean setStringResult = appConfig.setStringValue(TEST_KEY, null);
        assertTrue(setStringResult);
        verify(mockEditor).putString(TEST_KEY, null);
        
        // Setup for getString with null return
        when(mockSharedPreferences.contains(TEST_KEY)).thenReturn(true);
        when(mockSharedPreferences.getString(TEST_KEY, "default")).thenReturn(null);
        
        // Execute & Verify getString with null
        String defaultValue = "default";
        String retrievedValue = appConfig.getStringValue(TEST_KEY, defaultValue);
        assertNull(retrievedValue);
    }

    /**
     * Test handling of empty strings.
     * Verifies that empty strings are handled correctly.
     */
    @Test
    public void testEmptyStringHandling() {
        // Setup
        injectMockedSharedPreferences();
        String emptyString = "";
        
        // Execute
        boolean setResult = appConfig.setStringValue(TEST_KEY, emptyString);
        
        // Setup for retrieval
        when(mockSharedPreferences.contains(TEST_KEY)).thenReturn(true);
        when(mockSharedPreferences.getString(TEST_KEY, "default")).thenReturn(emptyString);
        
        // Execute retrieval
        String retrievedValue = appConfig.getStringValue(TEST_KEY, "default");
        
        // Verify
        assertTrue(setResult);
        assertEquals(emptyString, retrievedValue);
        verify(mockEditor).putString(TEST_KEY, emptyString);
    }

    /**
     * Test default value handling.
     * Verifies that default values are returned when a key doesn't exist.
     */
    @Test
    public void testDefaultValueHandling() {
        // Setup
        injectMockedSharedPreferences();
        String defaultString = "default_string";
        int defaultInt = 99;
        long defaultLong = 999L;
        boolean defaultBoolean = false;
        
        // Setup SharedPreferences to not contain the key
        when(mockSharedPreferences.contains(TEST_KEY)).thenReturn(false);
        
        // Execute & Verify
        assertEquals(defaultString, appConfig.getStringValue(TEST_KEY, defaultString));
        assertEquals(defaultInt, appConfig.getIntValue(TEST_KEY, defaultInt));
        assertEquals(defaultLong, appConfig.getLongValue(TEST_KEY, defaultLong));
        assertEquals(defaultBoolean, appConfig.getBooleanValue(TEST_KEY, defaultBoolean));
        
        // Verify no retrieval methods were called since the key doesn't exist
        verify(mockSharedPreferences, never()).getString(eq(TEST_KEY), anyString());
        verify(mockSharedPreferences, never()).getInt(eq(TEST_KEY), anyInt());
        verify(mockSharedPreferences, never()).getLong(eq(TEST_KEY), anyLong());
        verify(mockSharedPreferences, never()).getBoolean(eq(TEST_KEY), anyBoolean());
    }

    /**
     * Test exception handling during storage operations.
     * Verifies that exceptions during storage operations are handled gracefully.
     */
    @Test
    public void testExceptionHandlingDuringStorage() {
        // Setup
        injectMockedSharedPreferences();
        when(mockEditor.commit()).thenThrow(new RuntimeException("Test exception"));
        
        // Execute
        boolean stringResult = appConfig.setStringValue(TEST_KEY, TEST_STRING_VALUE);
        boolean intResult = appConfig.setIntValue(TEST_KEY, TEST_INT_VALUE);
        boolean longResult = appConfig.setLongValue(TEST_KEY, TEST_LONG_VALUE);
        boolean booleanResult = appConfig.setBooleanValue(TEST_KEY, TEST_BOOLEAN_VALUE);
        
        // Verify
        assertFalse(stringResult);
        assertFalse(intResult);
        assertFalse(longResult);
        assertFalse(booleanResult);
    }

    /**
     * Test exception handling during retrieval operations.
     * Verifies that exceptions during retrieval operations are handled gracefully.
     */
    @Test
    public void testExceptionHandlingDuringRetrieval() {
        // Setup
        injectMockedSharedPreferences();
        when(mockSharedPreferences.contains(TEST_KEY)).thenReturn(true);
        when(mockSharedPreferences.getString(eq(TEST_KEY), anyString())).thenThrow(new RuntimeException("Test exception"));
        when(mockSharedPreferences.getInt(eq(TEST_KEY), anyInt())).thenThrow(new RuntimeException("Test exception"));
        when(mockSharedPreferences.getLong(eq(TEST_KEY), anyLong())).thenThrow(new RuntimeException("Test exception"));
        when(mockSharedPreferences.getBoolean(eq(TEST_KEY), anyBoolean())).thenThrow(new RuntimeException("Test exception"));
        
        String defaultString = "default_string";
        int defaultInt = 99;
        long defaultLong = 999L;
        boolean defaultBoolean = false;
        
        // Execute & Verify
        assertEquals(defaultString, appConfig.getStringValue(TEST_KEY, defaultString));
        assertEquals(defaultInt, appConfig.getIntValue(TEST_KEY, defaultInt));
        assertEquals(defaultLong, appConfig.getLongValue(TEST_KEY, defaultLong));
        assertEquals(defaultBoolean, appConfig.getBooleanValue(TEST_KEY, defaultBoolean));
    }

    /**
     * Test the singleton pattern implementation.
     * Verifies that getInstance() returns the same instance.
     */
    @Test
    public void testSingletonPattern() {
        // Setup - create a new AppConfig instance
        AppConfig newConfig = new AppConfig(context);
        
        // Verify
        assertEquals(newConfig, AppConfig.getInstance());
        
        // Create another instance and verify it becomes the new singleton
        AppConfig anotherConfig = new AppConfig(context);
        assertEquals(anotherConfig, AppConfig.getInstance());
        
        // Verify they're different instances
        assertFalse(newConfig == anotherConfig);
    }

    /**
     * Test the getBooleanValue method.
     * Verifies that boolean values can be retrieved correctly.
     */
    @Test
    public void testGetBooleanValue() {
        // Setup
        injectMockedSharedPreferences();
        when(mockSharedPreferences.contains(TEST_KEY)).thenReturn(true);
        when(mockSharedPreferences.getBoolean(TEST_KEY, false)).thenReturn(TEST_BOOLEAN_VALUE);
        
        // Execute
        boolean retrievedValue = appConfig.getBooleanValue(TEST_KEY, false);
        
        // Verify
        assertEquals(TEST_BOOLEAN_VALUE, retrievedValue);
    }

}
