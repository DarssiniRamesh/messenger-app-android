package org.mesibo.messenger.fcm;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

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
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MesiboJobIntentService}
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class MesiboJobIntentServiceTest extends BaseUnitTest {
    
    // Custom dependency provider for MesiboJobIntentServiceTest
    private class JobIntentServiceTestDependencyProvider extends DefaultTestDependencyProvider {
        @Override
        public void sendMessageToGcmListener(boolean inService) {
            // This will be mocked in the tests using MockedStatic
        }
    }
    
    @Override
    protected TestDependencyProvider createDependencyProvider() {
        return new JobIntentServiceTestDependencyProvider();
    }

    @Mock
    private Context mockContext;

    @Mock
    private Intent mockIntent;

    @Mock
    private Handler mockHandler;

    private MesiboJobIntentService service;

    @Before
    public void setUp() {
        super.setUp();
        service = spy(new MesiboJobIntentService());
        
        // Set the mock handler using setPrivateField
        setPrivateField(service, "mHandler", mockHandler);
        when(mockHandler.post(any(Runnable.class))).thenAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return true;
        });
    }

    @Test
    public void testEnqueueWork() {
        // Arrange
        try (MockedStatic<MesiboJobIntentService> mockedStatic = Mockito.mockStatic(MesiboJobIntentService.class)) {
            // Allow the real enqueueWork method to be called
            mockedStatic.when(() -> MesiboJobIntentService.enqueueWork(any(), any()))
                    .thenCallRealMethod();
            
            // Mock the parent enqueueWork method
            mockedStatic.when(() -> androidx.core.app.JobIntentService.enqueueWork(
                    any(Context.class), 
                    any(Class.class), 
                    anyInt(), 
                    any(Intent.class)))
                    .thenReturn();
            
            // Act
            MesiboJobIntentService.enqueueWork(mockContext, mockIntent);
            
            // Assert
            // Verify that the parent enqueueWork method was called with the right parameters
            mockedStatic.verify(() -> androidx.core.app.JobIntentService.enqueueWork(
                    eq(mockContext), 
                    eq(MesiboJobIntentService.class), 
                    eq(MesiboJobIntentService.JOB_ID), 
                    eq(mockIntent)));
        }
    }

    @Test
    public void testEnqueueWork_withException() {
        // Arrange
        try (MockedStatic<MesiboJobIntentService> mockedStatic = Mockito.mockStatic(MesiboJobIntentService.class)) {
            // Allow the real enqueueWork method to be called
            mockedStatic.when(() -> MesiboJobIntentService.enqueueWork(any(), any()))
                    .thenCallRealMethod();
            
            // Mock the parent enqueueWork method to throw an exception
            mockedStatic.when(() -> androidx.core.app.JobIntentService.enqueueWork(
                    any(Context.class), 
                    any(Class.class), 
                    anyInt(), 
                    any(Intent.class)))
                    .thenThrow(new RuntimeException("Test exception"));
            
            // Act - This should not throw an exception
            MesiboJobIntentService.enqueueWork(mockContext, mockIntent);
            
            // Assert - No assertion needed, we're just verifying that the exception is caught
        }
    }

    @Test
    public void testOnHandleWork() {
        // Arrange
        try (MockedStatic<MesiboRegistrationIntentService> mockedStatic = Mockito.mockStatic(MesiboRegistrationIntentService.class)) {
            // Act
            service.onHandleWork(mockIntent);
            
            // Assert
            // Verify that sendMessageToListener was called with the right parameter
            mockedStatic.verify(() -> MesiboRegistrationIntentService.sendMessageToListener(true));
        }
    }

    @Test
    public void testOnHandleWork_withException() {
        // Arrange
        try (MockedStatic<MesiboRegistrationIntentService> mockedStatic = Mockito.mockStatic(MesiboRegistrationIntentService.class)) {
            // Mock sendMessageToListener to throw an exception
            mockedStatic.when(() -> MesiboRegistrationIntentService.sendMessageToListener(true))
                    .thenThrow(new RuntimeException("Test exception"));
            
            // Act - This should not throw an exception
            service.onHandleWork(mockIntent);
            
            // Assert - No assertion needed, we're just verifying that the exception is caught
        }
    }

    @Test
    public void testOnDestroy() {
        // Arrange
        doNothing().when(service).superOnDestroy();
        
        // Act
        service.onDestroy();
        
        // Assert
        // Verify that super.onDestroy() was called
        verify(service).superOnDestroy();
    }

    @Test
    public void testToast() {
        // Arrange
        String testMessage = "Test toast message";
        
        // Act
        service.toast(testMessage);
        
        // Assert
        // Verify that the toast was shown with the right message
        assertEquals(testMessage, ShadowToast.getTextOfLatestToast());
    }

    // Extension of MesiboJobIntentService for testing
    private static abstract class TestableService extends MesiboJobIntentService {
        public void superOnDestroy() {
            super.onDestroy();
        }
    }
}
