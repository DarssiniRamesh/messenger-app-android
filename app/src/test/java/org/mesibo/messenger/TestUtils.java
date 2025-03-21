package org.mesibo.messenger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboProfile;

import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Utility class providing helper methods for unit tests in the messenger-app-android project.
 * Contains methods for creating test data, mocking objects, and other testing utilities.
 */
public class TestUtils {

    private static final Random random = new Random();
    private static final Gson gson = new Gson();

    /**
     * Private constructor to prevent instantiation
     */
    private TestUtils() {
        // Utility class should not be instantiated
    }

    /**
     * Creates a mock MesiboProfile for testing purposes
     *
     * @param address The address (phone number or user ID) for the profile
     * @param name The name for the profile
     * @param isGroup Whether this profile represents a group
     * @return A mocked MesiboProfile object
     */
    public static MesiboProfile createMockProfile(String address, String name, boolean isGroup) {
        MesiboProfile profile = Mockito.mock(MesiboProfile.class);
        
        Mockito.when(profile.getAddress()).thenReturn(address);
        Mockito.when(profile.getName()).thenReturn(name);
        Mockito.when(profile.getNameOrAddress()).thenReturn(name != null ? name : address);
        Mockito.when(profile.isGroup()).thenReturn(isGroup);
        
        if (isGroup) {
            long groupId = Math.abs(random.nextLong());
            Mockito.when(profile.getGroupId()).thenReturn(groupId);
        }
        
        return profile;
    }

    /**
     * Creates a mock MesiboMessage for testing purposes
     *
     * @param sender The sender profile
     * @param text The message text
     * @param isIncoming Whether the message is incoming or outgoing
     * @return A mocked MesiboMessage object
     */
    public static MesiboMessage createMockMessage(MesiboProfile sender, String text, boolean isIncoming) {
        MesiboMessage message = Mockito.mock(MesiboMessage.class);
        
        Mockito.when(message.getMessage()).thenReturn(text);
        Mockito.when(message.isIncoming()).thenReturn(isIncoming);
        Mockito.when(message.isOutgoing()).thenReturn(!isIncoming);
        Mockito.when(message.getPeer()).thenReturn(sender.getAddress());
        Mockito.when(message.profile).thenReturn(sender);
        
        if (sender.isGroup()) {
            Mockito.when(message.groupid).thenReturn(sender.getGroupId());
            Mockito.when(message.groupProfile).thenReturn(sender);
        }
        
        return message;
    }

    /**
     * Generates a random phone number for testing
     *
     * @return A random phone number string
     */
    public static String generateRandomPhoneNumber() {
        StringBuilder phoneNumber = new StringBuilder("+1");
        for (int i = 0; i < 10; i++) {
            phoneNumber.append(random.nextInt(10));
        }
        return phoneNumber.toString();
    }

    /**
     * Generates a random name for testing
     *
     * @return A random name string
     */
    public static String generateRandomName() {
        String[] firstNames = {"John", "Jane", "Michael", "Emily", "David", "Sarah", "Robert", "Lisa"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson"};
        
        return firstNames[random.nextInt(firstNames.length)] + " " + 
               lastNames[random.nextInt(lastNames.length)];
    }

    /**
     * Creates a mock SampleAPI.Response object for testing
     *
     * @param result The result status ("OK" or error code)
     * @param op The operation type
     * @return A SampleAPI.Response object
     */
    public static SampleAPI.Response createMockResponse(String result, String op) {
        SampleAPI.Response response = new SampleAPI.Response();
        response.result = result;
        response.op = op;
        return response;
    }

    /**
     * Creates a JSON object from a string for testing
     *
     * @param jsonString The JSON string
     * @return A JSONObject
     * @throws JSONException If the string is not valid JSON
     */
    public static JSONObject createJsonObject(String jsonString) throws JSONException {
        return new JSONObject(jsonString);
    }

    /**
     * Reads a test resource file and returns its contents as a string
     *
     * @param context The test context
     * @param resourcePath The path to the resource file
     * @return The contents of the file as a string
     * @throws IOException If the file cannot be read
     */
    public static String readResourceFile(Context context, String resourcePath) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = context.getClassLoader().getResourceAsStream(resourcePath);
        
        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            reader.close();
        }
        
        return stringBuilder.toString();
    }

    /**
     * Reads a file from the filesystem and returns its contents as a string
     *
     * @param filePath The path to the file
     * @return The contents of the file as a string
     * @throws IOException If the file cannot be read
     */
    public static String readFile(String filePath) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
        
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append('\n');
        }
        reader.close();
        
        return stringBuilder.toString();
    }

    /**
     * Gets the value of a private field using reflection
     *
     * @param object The object containing the field
     * @param fieldName The name of the field
     * @return The value of the field
     * @throws Exception If the field cannot be accessed
     */
    public static Object getPrivateField(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * Sets the value of a private field using reflection
     *
     * @param object The object containing the field
     * @param fieldName The name of the field
     * @param value The value to set
     * @throws Exception If the field cannot be accessed
     */
    public static void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    /**
     * Captures arguments passed to a mocked method
     *
     * @param <T> The type of the argument to capture
     * @return An ArgumentCaptor for the specified type
     */
    public static <T> ArgumentCaptor<T> argumentCaptor(Class<T> clazz) {
        return ArgumentCaptor.forClass(clazz);
    }

    /**
     * Gets the last launched intent from a context
     *
     * @param context The context
     * @return The last launched intent
     */
    public static Intent getLastLaunchedIntent(Context context) {
        ShadowApplication shadowApplication = Shadows.shadowOf((android.app.Application) context.getApplicationContext());
        return shadowApplication.getNextStartedActivity();
    }

    /**
     * Gets all launched intents from a context
     *
     * @param context The context
     * @return A list of all launched intents
     */
    public static List<Intent> getAllLaunchedIntents(Context context) {
        ShadowApplication shadowApplication = Shadows.shadowOf((android.app.Application) context.getApplicationContext());
        return shadowApplication.getStartedActivities();
    }

    /**
     * Creates a Bundle with test data
     *
     * @param keyValuePairs Key-value pairs to add to the bundle (must be even number of arguments)
     * @return A Bundle containing the specified key-value pairs
     */
    public static Bundle createBundle(Object... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Must provide an even number of arguments");
        }
        
        Bundle bundle = new Bundle();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            String key = (String) keyValuePairs[i];
            Object value = keyValuePairs[i + 1];
            
            if (value instanceof String) {
                bundle.putString(key, (String) value);
            } else if (value instanceof Integer) {
                bundle.putInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                bundle.putBoolean(key, (Boolean) value);
            } else if (value instanceof Long) {
                bundle.putLong(key, (Long) value);
            } else {
                bundle.putString(key, value.toString());
            }
        }
        
        return bundle;
    }

    /**
     * Generates a random UUID string
     *
     * @return A random UUID string
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}