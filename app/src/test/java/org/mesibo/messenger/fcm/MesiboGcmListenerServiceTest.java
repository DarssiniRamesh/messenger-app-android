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
import org.mesibo.messenger.MainApplication;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
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

    @Mock
    private RemoteMessage mockRemoteMessage;

    @Mock
    private Context mockContext;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @Mock
    private SharedPreferences.Editor mockEditor;

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
}