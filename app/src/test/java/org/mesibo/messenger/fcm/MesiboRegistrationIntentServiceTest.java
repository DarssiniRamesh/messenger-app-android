package org.mesibo.messenger.fcm;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mesibo.messenger.BaseUnitTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MesiboRegistrationIntentService}
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class MesiboRegistrationIntentServiceTest extends BaseUnitTest {

    @Mock
    private Context mockContext;

    @Mock
    private Intent mockIntent;

    @Mock
    private FirebaseInstanceId mockFirebaseInstanceId;

    @Mock
    private FirebaseApp mockFirebaseApp;

    @Mock
    private GoogleApiAvailability mockGoogleApiAvailability;

    @Mock
    private MesiboRegistrationIntentService.GCMListener mockGcmListener;

    private MesiboRegistrationIntentService service;

    @Before
    public void setUp() {
        super.setUp();
        service = spy(new MesiboRegistrationIntentService());
        
        // Mock context
        doReturn(mockContext).when(service).getApplicationContext();
    }

    @Test
    public void testOnHandleWork() {
        // Arrange
        try (MockedStatic<FirebaseInstanceId> mockedFirebaseInstanceId = Mockito.mockStatic(FirebaseInstanceId.class);
             MockedStatic<FirebaseApp> mockedFirebaseApp = Mockito.mockStatic(FirebaseApp.class)) {
            
            // Setup mocks
            mockedFirebaseApp.when(() -> FirebaseApp.initializeApp(any(Context.class))).thenReturn(mockFirebaseApp);
            mockedFirebaseInstanceId.when(() -> FirebaseInstanceId.getInstance(any(FirebaseApp.class))).thenReturn(mockFirebaseInstanceId);
            when(mockFirebaseInstanceId.getToken()).thenReturn("test-token");
            
            // Set a mock GCM listener
            setPrivateStaticField(MesiboRegistrationIntentService.class, "mListener", mockGcmListener);
            
            // Act
            service.onHandleWork(mockIntent);
            
            // Assert
            // Verify that the token was retrieved and passed to the listener
            verify(mockGcmListener).Mesibo_onGCMToken("test-token");
        }
    }

    @Test
    public void testOnHandleWork_whenFirebaseInstanceIdIsNull() {
        // Arrange
        try (MockedStatic<FirebaseInstanceId> mockedFirebaseInstanceId = Mockito.mockStatic(FirebaseInstanceId.class);
             MockedStatic<FirebaseApp> mockedFirebaseApp = Mockito.mockStatic(FirebaseApp.class)) {
            
            // Setup mocks to return null
            mockedFirebaseApp.when(() -> FirebaseApp.initializeApp(any(Context.class))).thenReturn(mockFirebaseApp);
            mockedFirebaseInstanceId.when(() -> FirebaseInstanceId.getInstance(any(FirebaseApp.class))).thenReturn(null);
            
            // Set a mock GCM listener
            setPrivateStaticField(MesiboRegistrationIntentService.class, "mListener", mockGcmListener);
            
            // Act
            service.onHandleWork(mockIntent);
            
            // Assert
            // Verify that the listener was not called with a token
            verify(mockGcmListener, never()).Mesibo_onGCMToken(any());
        }
    }

    @Test
    public void testOnHandleWork_whenExceptionOccurs() {
        // Arrange
        try (MockedStatic<FirebaseInstanceId> mockedFirebaseInstanceId = Mockito.mockStatic(FirebaseInstanceId.class);
             MockedStatic<FirebaseApp> mockedFirebaseApp = Mockito.mockStatic(FirebaseApp.class)) {
            
            // Setup mocks to throw exception
            mockedFirebaseApp.when(() -> FirebaseApp.initializeApp(any(Context.class))).thenReturn(mockFirebaseApp);
            mockedFirebaseInstanceId.when(() -> FirebaseInstanceId.getInstance(any(FirebaseApp.class))).thenReturn(mockFirebaseInstanceId);
            when(mockFirebaseInstanceId.getToken()).thenThrow(new RuntimeException("Test exception"));
            
            // Set a mock GCM listener
            setPrivateStaticField(MesiboRegistrationIntentService.class, "mListener", mockGcmListener);
            
            // Act
            service.onHandleWork(mockIntent);
            
            // Assert
            // Verify that the listener was called with null token
            verify(mockGcmListener).Mesibo_onGCMToken(null);
        }
    }

    @Test
    public void testStartRegistration_withValidParameters() {
        // Arrange
        String testSenderId = "test-sender-id";
        try (MockedStatic<MesiboRegistrationIntentService> mockedStatic = Mockito.mockStatic(MesiboRegistrationIntentService.class)) {
            // Allow the real startRegistration method to be called
            mockedStatic.when(() -> MesiboRegistrationIntentService.startRegistration(any(), any(), any()))
                    .thenCallRealMethod();
            
            // Mock the enqueueWork method
            mockedStatic.when(() -> MesiboRegistrationIntentService.enqueueWork(any(), any()))
                    .thenAnswer(invocation -> null);
            
            // Act
            MesiboRegistrationIntentService.startRegistration(mockContext, testSenderId, mockGcmListener);
            
            // Assert
            // Verify that enqueueWork was called with the right context and intent
            ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
            mockedStatic.verify(() -> MesiboRegistrationIntentService.enqueueWork(eq(mockContext), intentCaptor.capture()));
            
            // Verify the sender ID and listener were set
            assertEquals(testSenderId, getPrivateStaticField(MesiboRegistrationIntentService.class, "SENDER_ID"));
            assertEquals(mockGcmListener, getPrivateStaticField(MesiboRegistrationIntentService.class, "mListener"));
        }
    }

    @Test
    public void testSendMessageToListener_withInService() {
        // Arrange
        // Set a mock GCM listener
        setPrivateStaticField(MesiboRegistrationIntentService.class, "mListener", mockGcmListener);
        
        // Act
        MesiboRegistrationIntentService.sendMessageToListener(true);
        
        // Assert
        // Verify that the listener was called with the right parameter
        verify(mockGcmListener).Mesibo_onGCMMessage(true);
    }

    @Test
    public void testSendMessageToListener_withoutInService() {
        // Arrange
        // Set a mock GCM listener
        setPrivateStaticField(MesiboRegistrationIntentService.class, "mListener", mockGcmListener);
        
        // Act
        MesiboRegistrationIntentService.sendMessageToListener(false);
        
        // Assert
        // Verify that the listener was called with the right parameter
        verify(mockGcmListener).Mesibo_onGCMMessage(false);
    }

    @Test
    public void testSendMessageToListener_withNullListener() {
        // Arrange
        // Set null listener
        setPrivateStaticField(MesiboRegistrationIntentService.class, "mListener", null);
        
        // Act & Assert
        // This should not throw an exception
        MesiboRegistrationIntentService.sendMessageToListener(true);
    }

    @Test
    public void testCheckPlayServices_whenAvailable() {
        // Arrange
        try (MockedStatic<GoogleApiAvailability> mockedGoogleApi = Mockito.mockStatic(GoogleApiAvailability.class)) {
            mockedGoogleApi.when(GoogleApiAvailability::getInstance).thenReturn(mockGoogleApiAvailability);
            when(mockGoogleApiAvailability.isGooglePlayServicesAvailable(any(Context.class))).thenReturn(ConnectionResult.SUCCESS);
            
            // Use reflection to access the private method
            boolean result = invokePrivateMethod(service, "checkPlayServices");
            
            // Assert
            assertTrue(result);
        }
    }

    @Test
    public void testCheckPlayServices_whenNotAvailable() {
        // Arrange
        try (MockedStatic<GoogleApiAvailability> mockedGoogleApi = Mockito.mockStatic(GoogleApiAvailability.class)) {
            mockedGoogleApi.when(GoogleApiAvailability::getInstance).thenReturn(mockGoogleApiAvailability);
            when(mockGoogleApiAvailability.isGooglePlayServicesAvailable(any(Context.class))).thenReturn(ConnectionResult.SERVICE_MISSING);
            
            // Use reflection to access the private method
            boolean result = invokePrivateMethod(service, "checkPlayServices");
            
            // Assert
            assertFalse(result);
        }
    }

    // Helper methods for accessing private fields and methods using reflection
    private static void setPrivateStaticField(Class<?> clazz, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            java.lang.reflect.Modifier.setModifiers(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
            field.set(null, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set private static field", e);
        }
    }

    private static Object getPrivateStaticField(Class<?> clazz, String fieldName) {
        try {
            java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get private static field", e);
        }
    }

    private static <T> T invokePrivateMethod(Object object, String methodName, Object... args) {
        try {
            Class<?>[] argTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }
            
            java.lang.reflect.Method method = object.getClass().getDeclaredMethod(methodName, argTypes);
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            T result = (T) method.invoke(object, args);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke private method", e);
        }
    }
}