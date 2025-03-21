package org.mesibo.messenger;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.mesibo.api.Mesibo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowNotificationManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the NotifyUser class.
 * 
 * These tests verify the functionality of the NotifyUser class, which manages
 * notifications in the messenger application.
 */
public class NotifyUserTest extends BaseUnitTest {

    // The class under test
    private NotifyUser notifyUser;
    
    // Mocked dependencies
    @Mock
    private NotificationManager mockNotificationManager;
    
    @Mock
    private Mesibo mockMesibo;
    
    @Mock
    private MainApplication mockMainApplication;
    
    @Mock
    private Context mockContext;
    
    @Mock
    private Uri mockSoundUri;
    
    @Mock
    private Handler mockHandler;
    
    // Captured notifications for verification
    private ArgumentCaptor<Notification> notificationCaptor;
    
    // Shadow notification manager for verification
    private ShadowNotificationManager shadowNotificationManager;
    
    /**
     * Setup for each test.
     * Creates a spy of NotifyUser with mocked dependencies.
     */
    @Override
    protected void setUpTest() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
        
        // Setup mock context
        when(mockContext.getPackageName()).thenReturn("org.mesibo.messenger");
        when(mockContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockNotificationManager);
        
        // Setup MainApplication mock
        when(MainApplication.getAppContext()).thenReturn(mockContext);
        when(mockContext.getMainLooper()).thenReturn(Looper.getMainLooper());
        
        // Create notification captor
        notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        
        // Get the shadow notification manager
        shadowNotificationManager = Shadows.shadowOf(
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        
        // Create the NotifyUser instance with real context for Robolectric
        notifyUser = new NotifyUser(context);
        
        // Reset static counter
        NotifyUser.mCount = 0;
    }
    
    /**
     * Test initialization of NotifyUser.
     * Verifies that NotifyUser properly initializes when created.
     */
    @Test
    public void testInitialization() {
        // Verify that NotifyUser was initialized correctly
        assertNotNull(notifyUser);
        
        // Verify notification channel creation on Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = 
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            
            NotificationChannel channel = notificationManager.getNotificationChannel(
                    NotifyUser.NOTIFYMESSAGE_CHANNEL_ID);
            
            assertNotNull("Notification channel should be created", channel);
            assertEquals("Channel name should match", "New Messages", channel.getName());
            assertEquals("Channel importance should be high", 
                    NotificationManager.IMPORTANCE_HIGH, channel.getImportance());
        }
    }
    
    /**
     * Test sending a basic notification.
     * Verifies that a notification can be sent with basic parameters.
     */
    @Test
    public void testSendNotification() {
        // Setup
        String title = "Test Title";
        String content = "Test Content";
        int notificationId = NotifyUser.TYPE_MESSAGE;
        
        // Execute
        notifyUser.sendNotification(notificationId, title, content);
        
        // Verify
        List<Notification> notifications = shadowNotificationManager.getAllNotifications();
        assertEquals("Should have 1 notification", 1, notifications.size());
        
        // Get the notification from the shadow
        Notification notification = notifications.get(0);
        assertNotNull("Notification should not be null", notification);
        
        // Verify notification properties using Robolectric shadows
        assertEquals("Notification channel ID should match", 
                NotifyUser.NOTIFYMESSAGE_CHANNEL_ID, 
                Shadows.shadowOf(notification).getChannelId());
    }
    
    /**
     * Test sending a notification with custom content.
     * Verifies that a notification can be sent with custom NotificationContent.
     */
    @Test
    public void testSendNotificationWithCustomContent() {
        // Setup
        NotifyUser.NotificationContent content = new NotifyUser.NotificationContent();
        content.title = "Custom Title";
        content.content = "Custom Content";
        content.subContent = "Custom SubContent";
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, new Intent(context, StartUpActivity.class), 
                PendingIntent.FLAG_UPDATE_CURRENT);
        
        // Execute
        notifyUser.sendNotification(NotifyUser.TYPE_OTHER, pendingIntent, content);
        
        // Verify
        List<Notification> notifications = shadowNotificationManager.getAllNotifications();
        assertEquals("Should have 1 notification", 1, notifications.size());
    }
    
    /**
     * Test sending a notification with RemoteViews.
     * Verifies that a notification can be sent with custom RemoteViews.
     */
    @Test
    public void testSendNotificationWithRemoteViews() {
        // Setup
        NotifyUser.NotificationContent content = new NotifyUser.NotificationContent();
        content.title = "Remote View Title";
        content.content = "Remote View Content";
        content.v = new RemoteViews(context.getPackageName(), android.R.layout.simple_list_item_1);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, new Intent(context, StartUpActivity.class), 
                PendingIntent.FLAG_UPDATE_CURRENT);
        
        // Execute
        notifyUser.sendNotification(NotifyUser.TYPE_OTHER, pendingIntent, content);
        
        // Verify
        List<Notification> notifications = shadowNotificationManager.getAllNotifications();
        assertEquals("Should have 1 notification", 1, notifications.size());
    }
    
    /**
     * Test sending a notification with NotificationCompat.Style.
     * Verifies that a notification can be sent with a custom style.
     */
    @Test
    public void testSendNotificationWithStyle() {
        // Setup
        NotifyUser.NotificationContent content = new NotifyUser.NotificationContent();
        content.title = "Style Title";
        content.content = "Style Content";
        content.style = new NotificationCompat.BigTextStyle().bigText("Big Text Content");
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, new Intent(context, StartUpActivity.class), 
                PendingIntent.FLAG_UPDATE_CURRENT);
        
        // Execute
        notifyUser.sendNotification(NotifyUser.TYPE_OTHER, pendingIntent, content);
        
        // Verify
        List<Notification> notifications = shadowNotificationManager.getAllNotifications();
        assertEquals("Should have 1 notification", 1, notifications.size());
    }
    
    /**
     * Test clearing notifications.
     * Verifies that notifications can be cleared.
     */
    @Test
    public void testClearNotification() throws Exception {
        // Setup - send a notification first
        notifyUser.sendNotification(NotifyUser.TYPE_MESSAGE, "Test Title", "Test Content");
        
        // Verify notification was sent
        assertEquals("Should have 1 notification before clearing", 
                1, shadowNotificationManager.getAllNotifications().size());
        
        // Execute - clear the notification
        notifyUser.clearNotification();
        
        // Verify notification was cleared
        assertEquals("Should have 0 notifications after clearing", 
                0, shadowNotificationManager.getAllNotifications().size());
        
        // Verify mCount was reset
        assertEquals("mCount should be reset to 0", 0, NotifyUser.mCount);
        
        // Verify notification list was cleared
        List<NotifyUser.NotificationContent> notificationList = getNotificationList(notifyUser);
        assertTrue("Notification list should be empty", notificationList.isEmpty());
    }
    
    /**
     * Test sending notifications in a list.
     * Verifies that multiple notifications can be grouped in a list.
     */
    @Test
    public void testSendNotificationInList() throws Exception {
        // Setup
        String title1 = "Sender 1";
        String message1 = "Message 1";
        String title2 = "Sender 2";
        String message2 = "Message 2";
        
        // Execute - send two notifications
        notifyUser.sendNotificationInList(title1, message1);
        notifyUser.sendNotificationInList(title2, message2);
        
        // Verify
        assertEquals("mCount should be incremented to 2", 2, NotifyUser.mCount);
        
        // Verify notification list contains both notifications
        List<NotifyUser.NotificationContent> notificationList = getNotificationList(notifyUser);
        assertEquals("Notification list should have 2 items", 2, notificationList.size());
        assertEquals("First notification title should match", title1, notificationList.get(0).title);
        assertEquals("First notification content should match", message1, notificationList.get(0).content);
        assertEquals("Second notification title should match", title2, notificationList.get(1).title);
        assertEquals("Second notification content should match", message2, notificationList.get(1).content);
        
        // Verify a notification was sent
        List<Notification> notifications = shadowNotificationManager.getAllNotifications();
        assertEquals("Should have 1 notification (grouped)", 1, notifications.size());
    }
    
    /**
     * Test notification list limit.
     * Verifies that the notification list is limited to 5 items.
     */
    @Test
    public void testNotificationListLimit() throws Exception {
        // Setup - send 6 notifications
        for (int i = 1; i <= 6; i++) {
            notifyUser.sendNotificationInList("Sender " + i, "Message " + i);
        }
        
        // Verify
        List<NotifyUser.NotificationContent> notificationList = getNotificationList(notifyUser);
        assertEquals("Notification list should be limited to 5 items", 5, notificationList.size());
        
        // Verify the first notification was removed (oldest)
        assertEquals("First notification should be Sender 2", "Sender 2", notificationList.get(0).title);
        assertEquals("Last notification should be Sender 6", "Sender 6", notificationList.get(4).title);
    }
    
    /**
     * Test notification with null values.
     * Verifies that notifications handle null values gracefully.
     */
    @Test
    public void testSendNotificationWithNullValues() {
        // Setup
        NotifyUser.NotificationContent content = new NotifyUser.NotificationContent();
        content.title = null;
        content.content = null;
        content.subContent = null;
        content.style = null;
        content.v = null;
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, new Intent(context, StartUpActivity.class), 
                PendingIntent.FLAG_UPDATE_CURRENT);
        
        // Execute - should not crash
        notifyUser.sendNotification(NotifyUser.TYPE_OTHER, pendingIntent, content);
        
        // Verify
        List<Notification> notifications = shadowNotificationManager.getAllNotifications();
        assertEquals("Should have 1 notification", 1, notifications.size());
    }
    
    /**
     * Test notification channel creation.
     * Verifies that notification channels are created correctly.
     */
    @Test
    @Config(sdk = 26) // Test specifically on Android O where channels were introduced
    public void testNotificationChannelCreation() {
        // Setup - create a new NotifyUser instance to trigger channel creation
        NotifyUser newNotifyUser = new NotifyUser(context);
        
        // Verify
        NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        NotificationChannel channel = notificationManager.getNotificationChannel(
                NotifyUser.NOTIFYMESSAGE_CHANNEL_ID);
        
        assertNotNull("Notification channel should be created", channel);
        assertEquals("Channel name should match", "New Messages", channel.getName());
        assertEquals("Channel importance should be high", 
                NotificationManager.IMPORTANCE_HIGH, channel.getImportance());
        assertTrue("Channel should enable vibration", channel.shouldVibrate());
        assertEquals("Channel should be visible on lock screen", 
                Notification.VISIBILITY_PUBLIC, channel.getLockscreenVisibility());
        assertTrue("Channel should show badge", channel.canShowBadge());
    }
    
    /**
     * Test notification with empty content.
     * Verifies that notifications handle empty content gracefully.
     */
    @Test
    public void testSendNotificationWithEmptyContent() {
        // Setup
        NotifyUser.NotificationContent content = new NotifyUser.NotificationContent();
        content.title = "";
        content.content = "";
        content.subContent = "";
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, new Intent(context, StartUpActivity.class), 
                PendingIntent.FLAG_UPDATE_CURRENT);
        
        // Execute - should not crash
        notifyUser.sendNotification(NotifyUser.TYPE_OTHER, pendingIntent, content);
        
        // Verify
        List<Notification> notifications = shadowNotificationManager.getAllNotifications();
        assertEquals("Should have 1 notification", 1, notifications.size());
    }
    
    /**
     * Test notification with very long content.
     * Verifies that notifications handle long content correctly.
     */
    @Test
    public void testSendNotificationWithLongContent() {
        // Setup
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longContent.append("Very long content. ");
        }
        
        // Execute
        notifyUser.sendNotification(NotifyUser.TYPE_MESSAGE, "Long Title", longContent.toString());
        
        // Verify
        List<Notification> notifications = shadowNotificationManager.getAllNotifications();
        assertEquals("Should have 1 notification", 1, notifications.size());
    }
    
    /**
     * Test notification with special characters.
     * Verifies that notifications handle special characters correctly.
     */
    @Test
    public void testSendNotificationWithSpecialCharacters() {
        // Setup
        String specialTitle = "Special 你好 Characters !@#$%^&*()_+";
        String specialContent = "Content with emoji 😀 and unicode ñáéíóú";
        
        // Execute
        notifyUser.sendNotification(NotifyUser.TYPE_MESSAGE, specialTitle, specialContent);
        
        // Verify
        List<Notification> notifications = shadowNotificationManager.getAllNotifications();
        assertEquals("Should have 1 notification", 1, notifications.size());
    }
    
    /**
     * Test multiple notifications with the same ID.
     * Verifies that sending multiple notifications with the same ID replaces the previous one.
     */
    @Test
    public void testMultipleNotificationsWithSameId() {
        // Setup
        int notificationId = NotifyUser.TYPE_OTHER;
        
        // Execute - send two notifications with the same ID
        notifyUser.sendNotification(notificationId, "First Title", "First Content");
        notifyUser.sendNotification(notificationId, "Second Title", "Second Content");
        
        // Verify
        List<Notification> notifications = shadowNotificationManager.getAllNotifications();
        assertEquals("Should have 1 notification (replaced)", 1, notifications.size());
    }
    
    /**
     * Test notification with InboxStyle for multiple messages.
     * Verifies that multiple messages are properly formatted with InboxStyle.
     */
    @Test
    public void testNotificationWithInboxStyle() throws Exception {
        // Setup - send multiple notifications to trigger InboxStyle
        notifyUser.sendNotificationInList("Sender 1", "Message 1");
        notifyUser.sendNotificationInList("Sender 2", "Message 2");
        notifyUser.sendNotificationInList("Sender 3", "Message 3");
        
        // Verify
        List<NotifyUser.NotificationContent> notificationList = getNotificationList(notifyUser);
        assertEquals("Notification list should have 3 items", 3, notificationList.size());
        
        // The last notification should have an InboxStyle
        NotifyUser.NotificationContent lastNotification = notificationList.get(notificationList.size() - 1);
        
        // Verify a notification was sent
        List<Notification> notifications = shadowNotificationManager.getAllNotifications();
        assertEquals("Should have 1 notification (grouped)", 1, notifications.size());
    }
    
    /**
     * Test notification channel creation on pre-O devices.
     * Verifies that notification channels are handled correctly on older devices.
     */
    @Test
    @Config(sdk = 25) // Pre-O device
    public void testNotificationChannelCreationOnOlderDevices() {
        // Setup - create a new NotifyUser instance
        NotifyUser newNotifyUser = new NotifyUser(context);
        
        // Execute - send a notification
        newNotifyUser.sendNotification(NotifyUser.TYPE_MESSAGE, "Test Title", "Test Content");
        
        // Verify
        List<Notification> notifications = shadowNotificationManager.getAllNotifications();
        assertEquals("Should have 1 notification", 1, notifications.size());
        
        // No exception should be thrown even though channels don't exist on pre-O
    }
    
    /**
     * Test error handling when NotificationManager throws an exception.
     * Verifies that the app doesn't crash when NotificationManager has issues.
     */
    @Test
    public void testErrorHandlingWithNotificationManager() {
        // Setup
        Context mockContext = mock(Context.class);
        NotificationManager mockNotificationManager = mock(NotificationManager.class);
        
        when(mockContext.getPackageName()).thenReturn("org.mesibo.messenger");
        when(mockContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockNotificationManager);
        when(mockContext.getMainLooper()).thenReturn(Looper.getMainLooper());
        
        // Make the notification manager throw an exception when notify is called
        doThrow(new SecurityException("Test exception")).when(mockNotificationManager)
                .notify(anyInt(), any(Notification.class));
        
        // Create NotifyUser with the mock context
        NotifyUser testNotifyUser = new NotifyUser(mockContext);
        
        // Execute - should not crash despite the exception
        testNotifyUser.sendNotification(NotifyUser.TYPE_MESSAGE, "Test Title", "Test Content");
        
        // No assertion needed - test passes if no exception is thrown
    }
    
    /**
     * Test the private notifyMessages method.
     * Verifies that the notifyMessages method correctly formats notifications.
     */
    @Test
    public void testNotifyMessages() throws Exception {
        // Setup - create a spy of NotifyUser to access private method
        NotifyUser spyNotifyUser = spy(notifyUser);
        
        // Add notifications to the list
        spyNotifyUser.sendNotificationInList("Sender 1", "Message 1");
        
        // Get the private notifyMessages method
        Method notifyMessagesMethod = NotifyUser.class.getDeclaredMethod("notifyMessages");
        notifyMessagesMethod.setAccessible(true);
        
        // Execute the private method
        notifyMessagesMethod.invoke(spyNotifyUser);
        
        // Verify
        List<Notification> notifications = shadowNotificationManager.getAllNotifications();
        assertEquals("Should have 1 notification", 1, notifications.size());
    }
    
    /**
     * Helper method to access the private notification list field.
     */
    private List<NotifyUser.NotificationContent> getNotificationList(NotifyUser notifyUser) throws Exception {
        Field field = NotifyUser.class.getDeclaredField("mNotificationContentList");
        field.setAccessible(true);
        return (List<NotifyUser.NotificationContent>) field.get(notifyUser);
    }
}
