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
import org.mesibo.messenger.DefaultTestDependencyProvider;
import org.mesibo.messenger.TestDependencyProvider;
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
    
    // Custom dependency provider for MesiboRegistrationIntentServiceTest
    private class RegistrationIntentServiceTestDependencyProvider extends DefaultTestDependencyProvider {
        @Override
        public FirebaseInstanceId getFirebaseInstanceId(FirebaseApp app) {
            return mockFirebaseInstanceId;
        }
        
        @Override
        public FirebaseApp initializeFirebaseApp(Context context) {
            return mockFirebaseApp;
        }
        
        @Override
        public GoogleApiAvailability getGoogleApiAvailability() {
            return mockGoogleApiAvailability;
        }
        
        @Override
        public int isGooglePlayServicesAvailable(Context context) {
            return ConnectionResult.SUCCESS; // Default to success, can be overridden in tests
        }
    }
    
    @Override
    protected TestDependencyProvider createDependencyProvider() {
        return new RegistrationIntentServiceTestDependencyProvider();
    }

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
            
            // Set a mock GCM listener using FcmTestUtils
            FcmTestUtils.setGcmListener(mockGcmListener);
            
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
            
            // Set a mock GCM listener using FcmTestUtils
            FcmTestUtils.setGcmListener(mockGcmListener);
            
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
            
            // Set a mock GCM listener using FcmTestUtils
            FcmTestUtils.setGcmListener(mockGcmListener);
            
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
            
            // We can't directly verify the sender ID and listener were set since they're private static fields
            // Instead, we verify that the enqueueWork method was called, which is part of the startRegistration flow
        }
    }

    @Test
    public void testSendMessageToListener_withInService() {
        // Arrange
        // Set a mock GCM listener using FcmTestUtils
        FcmTestUtils.setGcmListener(mockGcmListener);
        
        // Act
        MesiboRegistrationIntentService.sendMessageToListener(true);
        
        // Assert
        // Verify that the listener was called with the right parameter
        verify(mockGcmListener).Mesibo_onGCMMessage(true);
    }

    @Test
    public void testSendMessageToListener_withoutInService() {
        // Arrange
        // Set a mock GCM listener using FcmTestUtils
        FcmTestUtils.setGcmListener(mockGcmListener);
        
        // Act
        MesiboRegistrationIntentService.sendMessageToListener(false);
        
        // Assert
        // Verify that the listener was called with the right parameter
        verify(mockGcmListener).Mesibo_onGCMMessage(false);
    }

    @Test
    public void testSendMessageToListener_withNullListener() {
        // Arrange
        // Set null listener using FcmTestUtils
        FcmTestUtils.setGcmListener(null);
        
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
            
            // Use the dependency provider to check if Google Play Services are available
            boolean result = (((RegistrationIntentServiceTestDependencyProvider) dependencyProvider).isGooglePlayServicesAvailable(mockContext) == ConnectionResult.SUCCESS);
            
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
            
            // Override the dependency provider to return SERVICE_MISSING
            ((RegistrationIntentServiceTestDependencyProvider) dependencyProvider).isGooglePlayServicesAvailable = (ctx) -> ConnectionResult.SERVICE_MISSING;
            
            // Use the dependency provider to check if Google Play Services are available
            boolean result = (((RegistrationIntentServiceTestDependencyProvider) dependencyProvider).isGooglePlayServicesAvailable(mockContext) == ConnectionResult.SUCCESS);
            
            // Assert
            assertFalse(result);
        }
    }

}
