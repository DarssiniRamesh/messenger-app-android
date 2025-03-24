package org.mesibo.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import androidx.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.mesibo.messenger.fcm.MesiboJobIntentService;
import org.mesibo.messenger.fcm.MesiboRegistrationIntentService;

/**
 * Interface for providing dependencies to tests.
 * This reduces the need for reflection and static method mocking.
 */
public interface TestDependencyProvider {
    
    /**
     * Get a Typeface instance from assets
     * 
     * @param context The context to use
     * @param path The path to the font file
     * @return A Typeface instance
     */
    Typeface getTypefaceFromAsset(Context context, String path);
    
    /**
     * Get the default SharedPreferences
     * 
     * @param context The context to use
     * @return The default SharedPreferences
     */
    SharedPreferences getDefaultSharedPreferences(Context context);
    
    /**
     * Check if media auto download is enabled
     * 
     * @return true if media auto download is enabled, false otherwise
     */
    boolean getMediaAutoDownload();
    
    /**
     * Set media auto download
     * 
     * @param enabled true to enable media auto download, false to disable
     */
    void setMediaAutoDownload(boolean enabled);
    
    /**
     * Send a message to the GCM listener
     * 
     * @param inService true if the message is sent from a service, false otherwise
     */
    void sendMessageToGcmListener(boolean inService);
    
    /**
     * Enqueue work for the MesiboJobIntentService
     * 
     * @param context The context to use
     * @param intent The intent to enqueue
     */
    void enqueueJobIntentServiceWork(Context context, android.content.Intent intent);
    
    /**
     * Get the Firebase Instance ID
     * 
     * @param app The Firebase app
     * @return The Firebase Instance ID
     */
    FirebaseInstanceId getFirebaseInstanceId(FirebaseApp app);
    
    /**
     * Initialize a Firebase app
     * 
     * @param context The context to use
     * @return The initialized Firebase app
     */
    FirebaseApp initializeFirebaseApp(Context context);
    
    /**
     * Get the Google API Availability instance
     * 
     * @return The Google API Availability instance
     */
    GoogleApiAvailability getGoogleApiAvailability();
    
    /**
     * Check if Google Play Services are available
     * 
     * @param context The context to use
     * @return A ConnectionResult code
     */
    int isGooglePlayServicesAvailable(Context context);
}