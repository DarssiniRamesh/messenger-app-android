package org.mesibo.messenger.fcm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.messaging.RemoteMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mesibo.messenger.BaseUnitTest;
import org.mesibo.messenger.DefaultTestDependencyProvider;
import org.mesibo.messenger.MainApplication;
import org.mesibo.messenger.NotifyUser;
import org.mesibo.messenger.TestDependencyProvider;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
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
 * Unit tests for {@link MesiboGcmListenerService}
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class MesiboGcmListenerServiceTest extends BaseUnitTest {
    
    // Custom dependency provider for MesiboGcmListenerServiceTest
    private class GcmListenerServiceTestDependencyProvider extends DefaultTestDependencyProvider {
        @Override
        public void sendMessageToGcmListener(boolean inService) {
            // This will be mocked in the tests using MockedStatic
        }
        
        @Override
        public void enqueueJobIntentServiceWork(Context context, Intent intent) {
            // This will be mocked in the tests using MockedStatic
        }
    }
    
    @Override
    protected TestDependencyProvider createDependencyProvider() {
        return new GcmListenerServiceTestDependencyProvider();
    }

    @Mock
    private RemoteMessage mockRemoteMessage;

    @Mock
    private RemoteMessage.Notification mockNotification;

    @Mock
    private Context mockContext;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @Mock
    private SharedPreferences.Editor mockEditor;

    @Mock
    private NotifyUser mockNotifyUser;

    private MesiboGcmListenerService service;

    @Before
    public void setUp() {
        super.setUp();
        service = spy(new MesiboGcmListenerService());
        
        // Mock the remote message
        when(mockRemoteMessage.getFrom()).thenReturn("test-sender");
        
        // Mock shared preferences for token storage
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        
        // Provide the mock context to the service
        doReturn(mockContext).when(service).getApplicationContext();
    }

    @Test
    public void testOnMessageReceived_withValidMessage() {
        // Arrange
        try (MockedStatic<MesiboRegistrationIntentService> mockedStatic = Mockito.mockStatic(MesiboRegistrationIntentService.class);
             MockedStatic<MesiboJobIntentService> mockedJobStatic = Mockito.mockStatic(MesiboJobIntentService.class);
             MockedStatic<MainApplication> mockedAppStatic = Mockito.mockStatic(MainApplication.class)) {
            
            // Mock static methods
            mockedAppStatic.when(MainApplication::getAppContext).thenReturn(mockContext);
            
            // Act
            service.onMessageReceived(mockRemoteMessage);
            
            // Assert
            // Verify that sendMessageToListener was called
            mockedStatic.verify(() -> MesiboRegistrationIntentService.sendMessageToListener(false));
            
            // Verify that enqueueWork was called with the right intent
            ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
            mockedJobStatic.verify(() -> MesiboJobIntentService.enqueueWork(any(Context.class), intentCaptor.capture()));
            
            // Verify the intent has the expected action and extras
            Intent capturedIntent = intentCaptor.getValue();
            assertEquals("com.mesibo.someintent", capturedIntent.getAction());
            assertEquals("newPushNotification", capturedIntent.getStringExtra("body"));
        }
    }

    @Test
    public void testOnMessageReceived_withNotificationPayload() {
        // Arrange
        when(mockRemoteMessage.getNotification()).thenReturn(mockNotification);
        when(mockNotification.getTitle()).thenReturn("Test Title");
        when(mockNotification.getBody()).thenReturn("Test Body");
        
        try (MockedStatic<MesiboRegistrationIntentService> mockedStatic = Mockito.mockStatic(MesiboRegistrationIntentService.class);
             MockedStatic<MesiboJobIntentService> mockedJobStatic = Mockito.mockStatic(MesiboJobIntentService.class);
             MockedStatic<MainApplication> mockedAppStatic = Mockito.mockStatic(MainApplication.class)) {
            
            // Mock static methods
            mockedAppStatic.when(MainApplication::getAppContext).thenReturn(mockContext);
            
            // Act
            service.onMessageReceived(mockRemoteMessage);
            
            // Assert
            // Verify that sendMessageToListener was called
            mockedStatic.verify(() -> MesiboRegistrationIntentService.sendMessageToListener(false));
            
            // Verify that enqueueWork was called with the right intent
            ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
            mockedJobStatic.verify(() -> MesiboJobIntentService.enqueueWork(any(Context.class), intentCaptor.capture()));
            
            // Verify the intent has the expected action and extras
            Intent capturedIntent = intentCaptor.getValue();
            assertEquals("com.mesibo.someintent", capturedIntent.getAction());
            assertEquals("newPushNotification", capturedIntent.getStringExtra("body"));
        }
    }

    @Test
    public void testOnMessageReceived_withDataPayload() {
        // Arrange
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("message_type", "chat");
        dataMap.put("sender", "user123");
        dataMap.put("content", "Hello, world!");
        
        when(mockRemoteMessage.getData()).thenReturn(dataMap);
        
        try (MockedStatic<MesiboRegistrationIntentService> mockedStatic = Mockito.mockStatic(MesiboRegistrationIntentService.class);
             MockedStatic<MesiboJobIntentService> mockedJobStatic = Mockito.mockStatic(MesiboJobIntentService.class);
             MockedStatic<MainApplication> mockedAppStatic = Mockito.mockStatic(MainApplication.class)) {
            
            // Mock static methods
            mockedAppStatic.when(MainApplication::getAppContext).thenReturn(mockContext);
            
            // Act
            service.onMessageReceived(mockRemoteMessage);
            
            // Assert
            // Verify that sendMessageToListener was called
            mockedStatic.verify(() -> MesiboRegistrationIntentService.sendMessageToListener(false));
            
            // Verify that enqueueWork was called with the right intent
            ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
            mockedJobStatic.verify(() -> MesiboJobIntentService.enqueueWork(any(Context.class), intentCaptor.capture()));
            
            // Verify the intent has the expected action and extras
            Intent capturedIntent = intentCaptor.getValue();
            assertEquals("com.mesibo.someintent", capturedIntent.getAction());
            assertEquals("newPushNotification", capturedIntent.getStringExtra("body"));
        }
    }

    @Test
    public void testOnMessageReceived_withNullMessage() {
        // Arrange
        try (MockedStatic<MesiboRegistrationIntentService> mockedStatic = Mockito.mockStatic(MesiboRegistrationIntentService.class);
             MockedStatic<MesiboJobIntentService> mockedJobStatic = Mockito.mockStatic(MesiboJobIntentService.class)) {
            
            // Act
            service.onMessageReceived(null);
            
            // Assert
            // Verify that no methods were called
            mockedStatic.verifyNoInteractions();
            mockedJobStatic.verifyNoInteractions();
        }
    }

    @Test
    public void testOnMessageReceived_withExceptionInProcessing() {
        // Arrange
        try (MockedStatic<MesiboRegistrationIntentService> mockedStatic = Mockito.mockStatic(MesiboRegistrationIntentService.class);
             MockedStatic<MesiboJobIntentService> mockedJobStatic = Mockito.mockStatic(MesiboJobIntentService.class);
             MockedStatic<MainApplication> mockedAppStatic = Mockito.mockStatic(MainApplication.class)) {
            
            // Mock static methods to throw exception
            mockedAppStatic.when(MainApplication::getAppContext).thenReturn(mockContext);
            mockedStatic.when(() -> MesiboRegistrationIntentService.sendMessageToListener(anyBoolean()))
                    .thenThrow(new RuntimeException("Test exception"));
            
            // Act - should not throw exception
            service.onMessageReceived(mockRemoteMessage);
            
            // Assert - no assertion needed, we're just verifying that the exception is caught
            // and doesn't crash the service
        }
    }

    @Test
    public void testOnNewToken() {
        // Arrange
        String testToken = "test-fcm-token";
        
        // Act
        service.onNewToken(testToken);
        
        // Assert
        // Verify that the token was stored in SharedPreferences
        verify(mockEditor).putString("fb", testToken);
        verify(mockEditor).apply();
    }

    @Test
    public void testOnNewToken_withEmptyToken() {
        // Arrange
        String testToken = "";
        
        // Act
        service.onNewToken(testToken);
        
        // Assert
        // Verify that the token was stored in SharedPreferences even if empty
        verify(mockEditor).putString("fb", testToken);
        verify(mockEditor).apply();
    }

    @Test
    public void testOnNewToken_withNullToken() {
        // Arrange
        String testToken = null;
        
        // Act
        service.onNewToken(testToken);
        
        // Assert
        // Verify that the token was stored in SharedPreferences even if null
        verify(mockEditor).putString("fb", null);
        verify(mockEditor).apply();
    }

    @Test
    public void testGetToken() {
        // Arrange
        String expectedToken = "stored-fcm-token";
        when(mockSharedPreferences.getString(eq("fb"), anyString())).thenReturn(expectedToken);
        
        // Act
        String actualToken = MesiboGcmListenerService.getToken(mockContext);
        
        // Assert
        assertEquals(expectedToken, actualToken);
        verify(mockContext).getSharedPreferences("_", Context.MODE_PRIVATE);
    }

    @Test
    public void testGetToken_whenTokenNotFound() {
        // Arrange
        when(mockSharedPreferences.getString(eq("fb"), anyString())).thenReturn("empty");
        
        // Act
        String actualToken = MesiboGcmListenerService.getToken(mockContext);
        
        // Assert
        assertEquals("empty", actualToken);
    }

    @Test
    public void testGetToken_withNullContext() {
        // Act
        try {
            MesiboGcmListenerService.getToken(null);
        } catch (NullPointerException e) {
            // Expected exception
            assertNotNull(e);
        }
    }

    @Test
    public void testBackgroundForegroundProcessing() {
        // Arrange
        try (MockedStatic<MesiboRegistrationIntentService> mockedStatic = Mockito.mockStatic(MesiboRegistrationIntentService.class);
             MockedStatic<MesiboJobIntentService> mockedJobStatic = Mockito.mockStatic(MesiboJobIntentService.class);
             MockedStatic<MainApplication> mockedAppStatic = Mockito.mockStatic(MainApplication.class)) {
            
            // Mock static methods
            mockedAppStatic.when(MainApplication::getAppContext).thenReturn(mockContext);
            
            // Act - simulate background message
            service.onMessageReceived(mockRemoteMessage);
            
            // Assert
            // Verify that sendMessageToListener was called with false (indicating background)
            mockedStatic.verify(() -> MesiboRegistrationIntentService.sendMessageToListener(false));
            
            // Verify that enqueueWork was called
            mockedJobStatic.verify(() -> MesiboJobIntentService.enqueueWork(any(Context.class), any(Intent.class)));
        }
    }
}
