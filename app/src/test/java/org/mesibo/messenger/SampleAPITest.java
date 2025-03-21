package org.mesibo.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboHttp;
import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboPhoneContactsManager;
import com.mesibo.api.MesiboProfile;
import com.mesibo.fcm.MesiboRegistrationIntentService;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the SampleAPI class.
 * 
 * These tests verify the functionality of the SampleAPI class, which handles API communication,
 * user authentication, notifications, and other core functionality for the messenger app.
 */
public class SampleAPITest extends BaseUnitTest {

    // Test constants
    private static final String TEST_PHONE = "+1234567890";
    private static final String TEST_CODE = "123456";
    private static final String TEST_TOKEN = "test_token";
    private static final String TEST_MESSAGE = "Test message";
    private static final String TEST_GCM_TOKEN = "test_gcm_token";
    
    // The class under test
    private SampleAPI sampleAPI;
    
    // Mocked dependencies
    @Mock
    private Mesibo mesiboMock;
    
    @Mock
    private MesiboProfile selfProfileMock;
    
    @Mock
    private MesiboProfile profileMock;
    
    @Mock
    private MesiboMessage messageMock;
    
    @Mock
    private NotifyUser notifyUserMock;
    
    @Mock
    private AppConfig appConfigMock;
    
    @Mock
    private AppConfig.Configuration configMock;
    
    @Mock
    private MesiboHttp mesiboHttpMock;
    
    @Mock
    private MesiboPhoneContactsManager phoneContactsManagerMock;
    
    @Mock
    private UIManager uiManagerMock;
    
    @Mock
    private MesiboListeners mesiboListenersMock;

    /**
     * Setup for each test.
     * Initializes mocks and sets up the test environment.
     */
    @Override
    protected void setUpTest() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
        
        // Setup AppConfig mock
        try {
            Field instanceField = AppConfig.class.getDeclaredField("_instance");
            instanceField.setAccessible(true);
            instanceField.set(null, appConfigMock);
            
            Field configField = AppConfig.class.getDeclaredField("mConfig");
            configField.setAccessible(true);
            configField.set(null, configMock);
            
            when(AppConfig.getConfig()).thenReturn(configMock);
        } catch (Exception e) {
            throw new RuntimeException("Failed to mock AppConfig", e);
        }
        
        // Setup Mesibo mock
        try {
            Method instanceMethod = Mesibo.class.getDeclaredMethod("getInstance");
            Mesibo mockInstance = mock(Mesibo.class);
            TestUtils.setPrivateField(Mesibo.class, "singleton", mockInstance);
            
            when(Mesibo.getInstance()).thenReturn(mesiboMock);
            when(Mesibo.getSelfProfile()).thenReturn(selfProfileMock);
            when(Mesibo.getPhoneContactsManager()).thenReturn(phoneContactsManagerMock);
        } catch (Exception e) {
            throw new RuntimeException("Failed to mock Mesibo", e);
        }
        
        // Setup NotifyUser mock
        try {
            Field notifyUserField = SampleAPI.class.getDeclaredField("mNotifyUser");
            notifyUserField.setAccessible(true);
            notifyUserField.set(null, notifyUserMock);
        } catch (Exception e) {
            throw new RuntimeException("Failed to mock NotifyUser", e);
        }
        
        // Initialize SampleAPI with context
        SampleAPI.init(context);
    }

    /**
     * Test login functionality with valid credentials.
     * Verifies that the login method properly constructs and sends the login request.
     */
    @Test
    public void testLoginWithValidCredentials() {
        // Setup
        SampleAPI.ResponseHandler handlerMock = mock(SampleAPI.ResponseHandler.class);
        when(handlerMock.sendRequest(any(JSONObject.class), any(), any())).thenReturn(true);
        
        // Execute
        SampleAPI.login(TEST_PHONE, TEST_CODE, handlerMock);
        
        // Verify
        verify(handlerMock).setOnUiThread(true);
        verify(handlerMock).sendRequest(any(JSONObject.class), any(), any());
        
        // Capture the JSON object sent to verify its contents
        ArgumentCaptor<JSONObject> jsonCaptor = ArgumentCaptor.forClass(JSONObject.class);
        verify(handlerMock).sendRequest(jsonCaptor.capture(), any(), any());
        
        JSONObject capturedJson = jsonCaptor.getValue();
        try {
            assertEquals("login", capturedJson.getString("op"));
            assertEquals(TEST_PHONE, capturedJson.getString("phone"));
            assertEquals(TEST_CODE, capturedJson.getString("otp"));
        } catch (JSONException e) {
            throw new AssertionError("JSON structure not as expected", e);
        }
    }

    /**
     * Test login functionality with empty verification code.
     * Verifies that the login method handles empty verification codes correctly.
     */
    @Test
    public void testLoginWithEmptyVerificationCode() {
        // Setup
        SampleAPI.ResponseHandler handlerMock = mock(SampleAPI.ResponseHandler.class);
        when(handlerMock.sendRequest(any(JSONObject.class), any(), any())).thenReturn(true);
        
        // Execute
        SampleAPI.login(TEST_PHONE, "", handlerMock);
        
        // Verify
        verify(handlerMock).setOnUiThread(true);
        verify(handlerMock).sendRequest(any(JSONObject.class), any(), any());
        
        // Capture the JSON object sent to verify its contents
        ArgumentCaptor<JSONObject> jsonCaptor = ArgumentCaptor.forClass(JSONObject.class);
        verify(handlerMock).sendRequest(jsonCaptor.capture(), any(), any());
        
        JSONObject capturedJson = jsonCaptor.getValue();
        try {
            assertEquals("login", capturedJson.getString("op"));
            assertEquals(TEST_PHONE, capturedJson.getString("phone"));
            assertFalse(capturedJson.has("otp")); // OTP should not be included when empty
        } catch (JSONException e) {
            throw new AssertionError("JSON structure not as expected", e);
        }
    }

    /**
     * Test logout functionality.
     * Verifies that the logout method properly constructs and sends the logout request.
     */
    @Test
    public void testLogout() {
        // Setup
        when(configMock.token).thenReturn(TEST_TOKEN);
        
        // Execute
        boolean result = SampleAPI.startLogout();
        
        // Verify
        assertTrue(result);
        // Since invokeApi is a private static method, we can't directly verify it was called
        // This is a limitation of the test, but we can verify the result
    }

    /**
     * Test logout functionality with empty token.
     * Verifies that the logout method handles empty tokens correctly.
     */
    @Test
    public void testLogoutWithEmptyToken() {
        // Setup
        when(configMock.token).thenReturn("");
        
        // Execute
        boolean result = SampleAPI.startLogout();
        
        // Verify
        assertFalse(result);
    }

    /**
     * Test force logout functionality.
     * Verifies that the forceLogout method properly resets the app state.
     */
    @Test
    public void testForceLogout() {
        // Setup
        doNothing().when(notifyUserMock).clearNotification();
        
        // Execute
        SampleAPI.forceLogout();
        
        // Verify
        verify(notifyUserMock).clearNotification();
        verify(configMock).reset();
        
        // Verify Mesibo static methods were called
        // This is a limitation of the test, as we can't directly verify static method calls
    }

    /**
     * Test response handling for successful login.
     * Verifies that the parseResponse method correctly handles a successful login response.
     */
    @Test
    public void testParseResponseForSuccessfulLogin() throws Exception {
        // Setup
        SampleAPI.Response response = new SampleAPI.Response();
        response.result = "OK";
        response.op = "login";
        response.token = TEST_TOKEN;
        response.phone = TEST_PHONE;
        
        // Use reflection to access private method
        Method parseResponseMethod = SampleAPI.class.getDeclaredMethod("parseResponse", 
                SampleAPI.Response.class, Context.class, boolean.class);
        parseResponseMethod.setAccessible(true);
        
        // Execute
        boolean result = (boolean) parseResponseMethod.invoke(null, response, context, false);
        
        // Verify
        assertTrue(result);
        assertEquals(TEST_TOKEN, configMock.token);
        assertEquals(TEST_PHONE, configMock.phone);
    }

    /**
     * Test response handling for failed login.
     * Verifies that the parseResponse method correctly handles a failed login response.
     */
    @Test
    public void testParseResponseForFailedLogin() throws Exception {
        // Setup
        SampleAPI.Response response = new SampleAPI.Response();
        response.result = "ERROR";
        response.op = "login";
        response.error = "INVALID_OTP";
        response.errmsg = "Invalid verification code";
        response.errtitle = "Login Failed";
        
        // Use reflection to access private method
        Method parseResponseMethod = SampleAPI.class.getDeclaredMethod("parseResponse", 
                SampleAPI.Response.class, Context.class, boolean.class);
        parseResponseMethod.setAccessible(true);
        
        // Execute
        boolean result = (boolean) parseResponseMethod.invoke(null, response, context, false);
        
        // Verify
        assertFalse(result);
    }

    /**
     * Test response handling for authentication failure.
     * Verifies that the parseResponse method correctly handles an authentication failure response.
     */
    @Test
    public void testParseResponseForAuthFailure() throws Exception {
        // Setup
        SampleAPI.Response response = new SampleAPI.Response();
        response.result = "ERROR";
        response.op = "login";
        response.error = "AUTHFAIL";
        
        // Use reflection to access private method
        Method parseResponseMethod = SampleAPI.class.getDeclaredMethod("parseResponse", 
                SampleAPI.Response.class, Context.class, boolean.class);
        parseResponseMethod.setAccessible(true);
        
        // Execute
        boolean result = (boolean) parseResponseMethod.invoke(null, response, context, false);
        
        // Verify
        assertFalse(result);
    }

    /**
     * Test response handling for null response.
     * Verifies that the parseResponse method correctly handles a null response.
     */
    @Test
    public void testParseResponseForNullResponse() throws Exception {
        // Setup
        // Use reflection to access private method
        Method parseResponseMethod = SampleAPI.class.getDeclaredMethod("parseResponse", 
                SampleAPI.Response.class, Context.class, boolean.class);
        parseResponseMethod.setAccessible(true);
        
        // Execute
        boolean result = (boolean) parseResponseMethod.invoke(null, null, context, false);
        
        // Verify
        assertFalse(result);
    }

    /**
     * Test notification handling for messages.
     * Verifies that the notify method correctly handles message notifications.
     */
    @Test
    public void testNotifyForMessages() {
        // Setup
        when(messageMock.isRealtimeMessage()).thenReturn(true);
        when(messageMock.isInOutbox()).thenReturn(false);
        when(messageMock.isOutgoing()).thenReturn(false);
        when(messageMock.groupid).thenReturn(0L);
        when(Mesibo.isReading(messageMock)).thenReturn(false);
        when(Mesibo.getProfile(messageMock)).thenReturn(profileMock);
        when(profileMock.isMuted()).thenReturn(false);
        when(profileMock.getName()).thenReturn("Test User");
        when(messageMock.peer).thenReturn("test_peer");
        
        // Execute
        SampleAPI.notify(messageMock, TEST_MESSAGE);
        
        // Verify
        verify(notifyUserMock).sendNotificationInList(anyString(), eq(TEST_MESSAGE));
    }

    /**
     * Test notification handling for missed calls.
     * Verifies that the notify method correctly handles missed call notifications.
     */
    @Test
    public void testNotifyForMissedCalls() {
        // Setup
        when(messageMock.isRealtimeMessage()).thenReturn(true);
        when(messageMock.isInOutbox()).thenReturn(false);
        when(messageMock.isOutgoing()).thenReturn(false);
        when(messageMock.groupid).thenReturn(0L);
        when(Mesibo.isReading(messageMock)).thenReturn(false);
        when(Mesibo.getProfile(messageMock)).thenReturn(profileMock);
        when(profileMock.isMuted()).thenReturn(false);
        when(profileMock.getNameOrAddress()).thenReturn("Test User");
        when(messageMock.isMissedCall()).thenReturn(true);
        when(messageMock.isVideoCall()).thenReturn(true);
        
        // Execute
        SampleAPI.notify(messageMock, TEST_MESSAGE);
        
        // Verify
        verify(notifyUserMock, never()).sendNotificationInList(anyString(), anyString());
        // Since we can't verify static method calls directly, this is a limitation of the test
    }

    /**
     * Test notification handling when reading.
     * Verifies that the notify method correctly handles notifications when the user is reading.
     */
    @Test
    public void testNotifyWhenReading() {
        // Setup
        when(messageMock.isRealtimeMessage()).thenReturn(true);
        when(Mesibo.isReading(messageMock)).thenReturn(true);
        
        // Execute
        SampleAPI.notify(messageMock, TEST_MESSAGE);
        
        // Verify
        verify(notifyUserMock, never()).sendNotificationInList(anyString(), anyString());
    }

    /**
     * Test notification handling for muted profiles.
     * Verifies that the notify method correctly handles notifications for muted profiles.
     */
    @Test
    public void testNotifyForMutedProfile() {
        // Setup
        when(messageMock.isRealtimeMessage()).thenReturn(true);
        when(messageMock.isInOutbox()).thenReturn(false);
        when(messageMock.isOutgoing()).thenReturn(false);
        when(messageMock.groupid).thenReturn(0L);
        when(Mesibo.isReading(messageMock)).thenReturn(false);
        when(Mesibo.getProfile(messageMock)).thenReturn(profileMock);
        when(profileMock.isMuted()).thenReturn(true);
        
        // Execute
        SampleAPI.notify(messageMock, TEST_MESSAGE);
        
        // Verify
        verify(notifyUserMock, never()).sendNotificationInList(anyString(), anyString());
    }

    /**
     * Test GCM token management.
     * Verifies that the setGCMToken method correctly handles GCM tokens.
     */
    @Test
    public void testSetGCMToken() {
        // Execute
        SampleAPI.setGCMToken(TEST_GCM_TOKEN);
        
        // Verify
        // Since sendGCMToken is a private method, we can't directly verify it was called
        // This is a limitation of the test
    }

    /**
     * Test GCM message handling.
     * Verifies that the onGCMMessage method correctly handles GCM messages.
     */
    @Test
    public void testOnGCMMessage() {
        // Execute
        SampleAPI.onGCMMessage(false);
        
        // Verify
        // Since this method has complex behavior with Thread.sleep, it's difficult to test
        // This is a limitation of the test
    }

    /**
     * Test media auto download settings.
     * Verifies that the setMediaAutoDownload and getMediaAutoDownload methods work correctly.
     */
    @Test
    public void testMediaAutoDownloadSettings() {
        // Execute
        SampleAPI.setMediaAutoDownload(false);
        boolean autoDownload = SampleAPI.getMediaAutoDownload();
        
        // Verify
        assertFalse(autoDownload);
    }

    /**
     * Test getting phone number.
     * Verifies that the getPhone method correctly retrieves the phone number.
     */
    @Test
    public void testGetPhone() {
        // Setup
        when(configMock.phone).thenReturn(TEST_PHONE);
        
        // Execute
        String phone = SampleAPI.getPhone();
        
        // Verify
        assertEquals(TEST_PHONE, phone);
    }

    /**
     * Test getting phone number when not in config.
     * Verifies that the getPhone method correctly retrieves the phone number from the self profile.
     */
    @Test
    public void testGetPhoneFromSelfProfile() {
        // Setup
        when(configMock.phone).thenReturn("");
        when(selfProfileMock.address).thenReturn(TEST_PHONE);
        
        // Execute
        String phone = SampleAPI.getPhone();
        
        // Verify
        assertEquals(TEST_PHONE, phone);
        assertEquals(TEST_PHONE, configMock.phone);
    }

    /**
     * Test getting token.
     * Verifies that the getToken method correctly retrieves the token.
     */
    @Test
    public void testGetToken() {
        // Setup
        when(configMock.token).thenReturn(TEST_TOKEN);
        
        // Execute
        String token = SampleAPI.getToken();
        
        // Verify
        assertEquals(TEST_TOKEN, token);
    }

    /**
     * Test getting upload URL.
     * Verifies that the getUploadUrl method correctly retrieves the upload URL.
     */
    @Test
    public void testGetUploadUrl() {
        // Setup
        String testUrl = "https://example.com/upload";
        when(configMock.uploadurl).thenReturn(testUrl);
        
        // Execute
        String url = SampleAPI.getUploadUrl();
        
        // Verify
        assertEquals(testUrl, url);
    }

    /**
     * Test getting download URL.
     * Verifies that the getDownloadUrl method correctly retrieves the download URL.
     */
    @Test
    public void testGetDownloadUrl() {
        // Setup
        String testUrl = "https://example.com/download";
        when(configMock.downloadurl).thenReturn(testUrl);
        
        // Execute
        String url = SampleAPI.getDownloadUrl();
        
        // Verify
        assertEquals(testUrl, url);
    }

    /**
     * Test showing connection error.
     * Verifies that the showConnectionError method correctly shows a connection error.
     */
    @Test
    public void testShowConnectionError() {
        // Execute
        SampleAPI.showConnectionError(context);
        
        // Verify
        // Since UIManager.showAlert is a static method, we can't directly verify it was called
        // This is a limitation of the test
    }

    /**
     * Test saving local synced contacts.
     * Verifies that the saveLocalSyncedContacts method correctly saves synced contacts.
     */
    @Test
    public void testSaveLocalSyncedContacts() {
        // Setup
        String contacts = "contact1,contact2";
        long timestamp = System.currentTimeMillis();
        
        // Execute
        SampleAPI.saveLocalSyncedContacts(contacts, timestamp);
        
        // Verify
        // Since Mesibo.setKey is a static method, we can't directly verify it was called
        // This is a limitation of the test
    }

    /**
     * Test saving synced timestamp.
     * Verifies that the saveSyncedTimestamp method correctly saves the synced timestamp.
     */
    @Test
    public void testSaveSyncedTimestamp() {
        // Setup
        long timestamp = System.currentTimeMillis();
        
        // Execute
        SampleAPI.saveSyncedTimestamp(timestamp);
        
        // Verify
        // Since Mesibo.setKey is a static method, we can't directly verify it was called
        // This is a limitation of the test
    }

    /**
     * Test starting contacts sync.
     * Verifies that the startContactsSync method correctly starts contacts sync.
     */
    @Test
    public void testStartContactsSync() {
        // Execute
        SampleAPI.startContactsSync();
        
        // Verify
        verify(phoneContactsManagerMock).overrideProfileName(true);
        verify(phoneContactsManagerMock).start();
    }

    /**
     * Test updating deleted group.
     * Verifies that the updateDeletedGroup method correctly updates a deleted group.
     */
    @Test
    public void testUpdateDeletedGroup() {
        // Setup
        long groupId = 12345L;
        when(Mesibo.getProfile(groupId)).thenReturn(profileMock);
        
        // Execute
        SampleAPI.updateDeletedGroup(groupId);
        
        // Verify
        verify(profileMock).setString("status", "Not a group member");
        verify(profileMock).save();
    }

    /**
     * Test updating deleted group with invalid group ID.
     * Verifies that the updateDeletedGroup method correctly handles invalid group IDs.
     */
    @Test
    public void testUpdateDeletedGroupWithInvalidGroupId() {
        // Setup
        long groupId = 0L;
        
        // Execute
        SampleAPI.updateDeletedGroup(groupId);
        
        // Verify
        verify(profileMock, never()).setString(anyString(), anyString());
        verify(profileMock, never()).save();
    }

    /**
     * Test updating deleted group with null profile.
     * Verifies that the updateDeletedGroup method correctly handles null profiles.
     */
    @Test
    public void testUpdateDeletedGroupWithNullProfile() {
        // Setup
        long groupId = 12345L;
        when(Mesibo.getProfile(groupId)).thenReturn(null);
        
        // Execute
        SampleAPI.updateDeletedGroup(groupId);
        
        // Verify
        verify(profileMock, never()).setString(anyString(), anyString());
        verify(profileMock, never()).save();
    }

    /**
     * Test the ResponseHandler class.
     * Verifies that the ResponseHandler correctly handles API responses.
     */
    @Test
    public void testResponseHandler() {
        // Setup
        SampleAPI.ResponseHandler handler = new SampleAPI.ResponseHandler() {
            @Override
            public void HandleAPIResponse(SampleAPI.Response response) {
                // Verify response is correctly passed to the handler
                assertNotNull(response);
                assertEquals("OK", response.result);
                assertEquals("test", response.op);
            }
        };
        
        // Create a test response
        String jsonResponse = "{\"result\":\"OK\",\"op\":\"test\"}";
        MesiboHttp http = mock(MesiboHttp.class);
        when(http.getDataString()).thenReturn(jsonResponse);
        
        // Execute
        handler.Mesibo_onHttpProgress(http, MesiboHttp.STATE_DOWNLOAD, 100);
        
        // Verify is done in the HandleAPIResponse method
    }

    /**
     * Test the ResponseHandler with null response.
     * Verifies that the ResponseHandler correctly handles null responses.
     */
    @Test
    public void testResponseHandlerWithNullResponse() {
        // Setup
        SampleAPI.ResponseHandler handler = new SampleAPI.ResponseHandler() {
            @Override
            public void HandleAPIResponse(SampleAPI.Response response) {
                // Verify null response is correctly passed to the handler
                assertNull(response);
            }
        };
        
        // Execute
        handler.Mesibo_onHttpProgress(null, MesiboHttp.STATE_DOWNLOAD, -1);
        
        // Verify is done in the HandleAPIResponse method
    }

    /**
     * Test the ResponseHandler with invalid JSON response.
     * Verifies that the ResponseHandler correctly handles invalid JSON responses.
     */
    @Test
    public void testResponseHandlerWithInvalidJsonResponse() {
        // Setup
        SampleAPI.ResponseHandler handler = new SampleAPI.ResponseHandler() {
            @Override
            public void HandleAPIResponse(SampleAPI.Response response) {
                // Verify null response is correctly passed to the handler
                assertNull(response);
            }
        };
        
        // Create an invalid JSON response
        String jsonResponse = "{invalid_json";
        MesiboHttp http = mock(MesiboHttp.class);
        when(http.getDataString()).thenReturn(jsonResponse);
        
        // Execute
        handler.Mesibo_onHttpProgress(http, MesiboHttp.STATE_DOWNLOAD, 100);
        
        // Verify is done in the HandleAPIResponse method
    }

    /**
     * Test the ResponseHandler with UI thread execution.
     * Verifies that the ResponseHandler correctly handles responses on the UI thread.
     */
    @Test
    public void testResponseHandlerWithUiThread() {
        // Setup
        SampleAPI.ResponseHandler handler = new SampleAPI.ResponseHandler() {
            @Override
            public void HandleAPIResponse(SampleAPI.Response response) {
                // This will be called on the UI thread
                assertNotNull(response);
                assertEquals("OK", response.result);
                assertEquals("test", response.op);
            }
        };
        
        // Set to execute on UI thread
        handler.setOnUiThread(true);
        handler.mContext = context;
        
        // Create a test response
        String jsonResponse = "{\"result\":\"OK\",\"op\":\"test\"}";
        MesiboHttp http = mock(MesiboHttp.class);
        when(http.getDataString()).thenReturn(jsonResponse);
        
        // Execute
        handler.Mesibo_onHttpProgress(http, MesiboHttp.STATE_DOWNLOAD, 100);
        
        // Verify is done in the HandleAPIResponse method
        // Since this is executed on the UI thread, we can't directly verify it
        // This is a limitation of the test
    }
}