package org.mesibo.messenger.fcm;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Utility class for FCM tests.
 * Provides methods for common FCM test operations without relying on reflection.
 */
public class FcmTestUtils {
    
    /**
     * Private constructor to prevent instantiation
     */
    private FcmTestUtils() {
        // Utility class should not be instantiated
    }
    
    /**
     * Interface for GCM listener callbacks
     */
    public interface GcmListenerCallback {
        void onGcmToken(String token);
        void onGcmMessage(boolean inService);
    }
    
    /**
     * Set the GCM listener for tests
     * 
     * @param listener The listener to set
     */
    public static void setGcmListener(MesiboRegistrationIntentService.GCMListener listener) {
        // This is a test utility method that would normally use reflection
        // Instead, we'll use the MesiboRegistrationIntentService.startRegistration method
        // which sets the listener internally
        MesiboRegistrationIntentService.startRegistration(null, "", listener);
    }
    
    /**
     * Create a test intent for FCM tests
     * 
     * @param action The action for the intent
     * @param extras The extras to add to the intent
     * @return The created intent
     */
    public static Intent createTestIntent(String action, Object... extras) {
        Intent intent = new Intent(action);
        for (int i = 0; i < extras.length; i += 2) {
            String key = (String) extras[i];
            Object value = extras[i + 1];
            if (value instanceof String) {
                intent.putExtra(key, (String) value);
            } else if (value instanceof Integer) {
                intent.putExtra(key, (Integer) value);
            } else if (value instanceof Boolean) {
                intent.putExtra(key, (Boolean) value);
            } else if (value instanceof Long) {
                intent.putExtra(key, (Long) value);
            }
        }
        return intent;
    }
}