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
 * Default implementation of TestDependencyProvider.
 * This class provides default implementations for all methods in the TestDependencyProvider interface.
 * It can be extended or mocked in tests to provide custom implementations.
 */
public class DefaultTestDependencyProvider implements TestDependencyProvider {
    
    @Override
    public Typeface getTypefaceFromAsset(Context context, String path) {
        return Typeface.createFromAsset(context.getAssets(), path);
    }
    
    @Override
    public SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    @Override
    public boolean getMediaAutoDownload() {
        return SampleAPI.getMediaAutoDownload();
    }
    
    @Override
    public void setMediaAutoDownload(boolean enabled) {
        SampleAPI.setMediaAutoDownload(enabled);
    }
    
    @Override
    public void sendMessageToGcmListener(boolean inService) {
        MesiboRegistrationIntentService.sendMessageToListener(inService);
    }
    
    @Override
    public void enqueueJobIntentServiceWork(Context context, android.content.Intent intent) {
        MesiboJobIntentService.enqueueWork(context, intent);
    }
    
    @Override
    public FirebaseInstanceId getFirebaseInstanceId(FirebaseApp app) {
        return FirebaseInstanceId.getInstance(app);
    }
    
    @Override
    public FirebaseApp initializeFirebaseApp(Context context) {
        return FirebaseApp.initializeApp(context);
    }
    
    @Override
    public GoogleApiAvailability getGoogleApiAvailability() {
        return GoogleApiAvailability.getInstance();
    }
    
    @Override
    public int isGooglePlayServicesAvailable(Context context) {
        return getGoogleApiAvailability().isGooglePlayServicesAvailable(context);
    }
}