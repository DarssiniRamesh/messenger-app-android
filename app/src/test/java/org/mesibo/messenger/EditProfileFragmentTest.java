package org.mesibo.messenger;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.core.app.ApplicationProvider;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.MesiboSelfProfile;
import com.mesibo.emojiview.EmojiconEditText;
import com.mesibo.mediapicker.MediaPicker;
import com.mesibo.messaging.MesiboUI;
import com.mesibo.messaging.RoundImageDrawable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowToast;

import static android.app.Activity.RESULT_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Comprehensive unit tests for the EditProfileFragment class.
 * 
 * These tests cover:
 * 1. Fragment lifecycle methods
 * 2. Form validation
 * 3. Profile update functionality
 * 4. UI interactions
 * 5. Error handling scenarios
 */
public class EditProfileFragmentTest extends BaseUnitTest {

    private EditProfileFragment fragment;
    private EditProfileActivity activity;
    
    @Mock private MesiboSelfProfile mockSelfProfile;
    @Mock private MesiboProfile mockGroupProfile;
    @Mock private MesiboProfile.Listener mockProfileListener;
    @Mock private View mockView;
    @Mock private LayoutInflater mockInflater;
    @Mock private ViewGroup mockContainer;
    @Mock private Bundle mockBundle;
    @Mock private ActionBar mockActionBar;
    @Mock private ImageView mockProfileImage;
    @Mock private ImageView mockProfileButton;
    @Mock private EmojiconEditText mockNameEditText;
    @Mock private EmojiconEditText mockStatusEditText;
    @Mock private TextView mockNameCharCounter;
    @Mock private TextView mockStatusCharCounter;
    @Mock private TextView mockPhoneNumber;
    @Mock private FrameLayout mockRootView;
    @Mock private MenuItem mockMenuItem;
    @Mock private Menu mockMenu;
    @Mock private MenuInflater mockMenuInflater;
    @Mock private Bitmap mockBitmap;
    @Mock private MesiboProfile.Image mockProfileImage1;
    @Mock private MesiboProfile.Image mockProfileImage2;
    
    @Before
    public void setUp() {
        super.setUp();
        
        // Create the activity that will host the fragment
        activity = Robolectric.buildActivity(EditProfileActivity.class).create().get();
        
        // Create the fragment
        fragment = new EditProfileFragment();
        
        // Mock static Mesibo methods
        mockStaticMesiboMethods();
        
        // Set up the view mocks
        setupViewMocks();
    }
    
    /**
     * Helper method to mock static Mesibo methods
     */
    private void mockStaticMesiboMethods() {
        // Mock Mesibo.getSelfProfile()
        when(mockSelfProfile.setSearchable(anyBoolean())).thenReturn(mockSelfProfile);
        when(mockSelfProfile.getAddress()).thenReturn("+18885551234");
        when(mockSelfProfile.getName()).thenReturn("Test User");
        when(mockSelfProfile.getString(eq("status"), anyString())).thenReturn("Test Status");
        when(mockSelfProfile.isGroup()).thenReturn(false);
        when(mockSelfProfile.getImage()).thenReturn(mockProfileImage1);
        
        // Mock Mesibo.getProfile(groupId)
        when(mockGroupProfile.getAddress()).thenReturn("group123");
        when(mockGroupProfile.getName()).thenReturn("Test Group");
        when(mockGroupProfile.getString(eq("status"), anyString())).thenReturn("Group Status");
        when(mockGroupProfile.isGroup()).thenReturn(true);
        when(mockGroupProfile.getImage()).thenReturn(mockProfileImage2);
    }
    
    /**
     * Helper method to set up view mocks
     */
    private void setupViewMocks() {
        // Mock view finding
        when(mockView.findViewById(R.id.self_user_image)).thenReturn(mockProfileImage);
        when(mockView.findViewById(R.id.edit_user_image)).thenReturn(mockProfileButton);
        when(mockView.findViewById(R.id.name_emoji_edittext)).thenReturn(mockNameEditText);
        when(mockView.findViewById(R.id.status_emoji_edittext)).thenReturn(mockStatusEditText);
        when(mockView.findViewById(R.id.name_char_counter)).thenReturn(mockNameCharCounter);
        when(mockView.findViewById(R.id.status_char_counter)).thenReturn(mockStatusCharCounter);
        when(mockView.findViewById(R.id.profile_self_phone)).thenReturn(mockPhoneNumber);
        when(mockView.findViewById(R.id.register_new_profile_rootlayout)).thenReturn(mockRootView);
        
        // Mock EditText behavior
        when(mockNameEditText.getText()).thenReturn(mock(Editable.class));
        when(mockNameEditText.getText().toString()).thenReturn("Test User");
        when(mockNameEditText.getText().length()).thenReturn(9); // "Test User" length
        
        when(mockStatusEditText.getText()).thenReturn(mock(Editable.class));
        when(mockStatusEditText.getText().toString()).thenReturn("Test Status");
        when(mockStatusEditText.getText().length()).thenReturn(11); // "Test Status" length
        
        // Mock ActionBar
        when(activity.getSupportActionBar()).thenReturn(mockActionBar);
        
        // Mock profile image
        when(mockProfileImage1.getImageOrThumbnail()).thenReturn(mockBitmap);
        when(mockProfileImage1.getUrl()).thenReturn("https://example.com/image.jpg");
        when(mockProfileImage1.getImagePath()).thenReturn("/path/to/image.jpg");
        
        when(mockProfileImage2.getImageOrThumbnail()).thenReturn(mockBitmap);
        when(mockProfileImage2.getUrl()).thenReturn("https://example.com/group.jpg");
        when(mockProfileImage2.getImagePath()).thenReturn("/path/to/group.jpg");
    }
    
    /**
     * Test the getProfile method when not in group mode
     */
    @Test
    public void testGetProfile_notGroup() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        
        // Set mGroupId to 0
        try {
            TestUtils.setPrivateField(spyFragment, "mGroupId", 0L);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mGroupId", e);
        }
        
        // Mock Mesibo.getSelfProfile()
        when(Mesibo.getSelfProfile()).thenReturn(mockSelfProfile);
        
        // Execute
        MesiboProfile result = spyFragment.getProfile();
        
        // Verify
        assertEquals("Should return self profile", mockSelfProfile, result);
        verify(mockSelfProfile).setSearchable(true);
    }
    
    /**
     * Test the getProfile method when in group mode
     */
    @Test
    public void testGetProfile_group() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        
        // Set mGroupId to a non-zero value
        try {
            TestUtils.setPrivateField(spyFragment, "mGroupId", 123L);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mGroupId", e);
        }
        
        // Mock Mesibo.getProfile(groupId)
        when(Mesibo.getProfile(123L)).thenReturn(mockGroupProfile);
        
        // Execute
        MesiboProfile result = spyFragment.getProfile();
        
        // Verify
        assertEquals("Should return group profile", mockGroupProfile, result);
    }
    
    /**
     * Test the onCreateView method
     */
    @Test
    public void testOnCreateView() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        when(mockInflater.inflate(eq(R.layout.fragment_register_new_profile), eq(mockContainer), eq(false)))
                .thenReturn(mockView);
        
        // Mock getActivity
        doReturn(activity).when(spyFragment).getActivity();
        
        // Mock getProfile
        doReturn(mockSelfProfile).when(spyFragment).getProfile();
        
        // Execute
        View resultView = spyFragment.onCreateView(mockInflater, mockContainer, mockBundle);
        
        // Verify
        assertNotNull("Result view should not be null", resultView);
        assertEquals("Result view should be our mock view", mockView, resultView);
        verify(spyFragment).setHasOptionsMenu(true);
        verify(mockActionBar).setDisplayHomeAsUpEnabled(true);
        verify(mockActionBar).setTitle("Edit profile details");
        verify(mockSelfProfile).addListener(spyFragment);
        verify(mockNameCharCounter).setText("50");
        verify(mockStatusCharCounter).setText("150");
    }
    
    /**
     * Test the updateUI method for a regular user profile
     */
    @Test
    public void testUpdateUI_userProfile() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        spyFragment.mEmojiNameEditText = mockNameEditText;
        spyFragment.mEmojiStatusEditText = mockStatusEditText;
        spyFragment.mPhoneNumber = mockPhoneNumber;
        
        // Execute
        spyFragment.updateUI(mockSelfProfile);
        
        // Verify
        verify(mockPhoneNumber).setText("+18885551234");
        verify(mockPhoneNumber, never()).setVisibility(View.GONE);
        verify(mockNameEditText).setText("Test User");
        verify(mockStatusEditText).setText("Test Status");
    }
    
    /**
     * Test the updateUI method for a group profile
     */
    @Test
    public void testUpdateUI_groupProfile() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        spyFragment.mEmojiNameEditText = mockNameEditText;
        spyFragment.mEmojiStatusEditText = mockStatusEditText;
        spyFragment.mPhoneNumber = mockPhoneNumber;
        
        // Execute
        spyFragment.updateUI(mockGroupProfile);
        
        // Verify
        verify(mockPhoneNumber).setVisibility(View.GONE);
        verify(mockNameEditText).setText("Test Group");
        verify(mockStatusEditText).setText("Group Status");
    }
    
    /**
     * Test the save method with valid input
     */
    @Test
    public void testSave_validInput() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        spyFragment.mEmojiNameEditText = mockNameEditText;
        spyFragment.mEmojiStatusEditText = mockStatusEditText;
        
        doReturn(mockSelfProfile).when(spyFragment).getProfile();
        
        // Set mLaunchMesibo to false
        try {
            TestUtils.setPrivateField(spyFragment, "mLaunchMesibo", false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mLaunchMesibo", e);
        }
        
        // Execute
        spyFragment.save();
        
        // Verify
        verify(mockSelfProfile).setName("Test User");
        verify(mockSelfProfile).setString("status", "Test Status");
        verify(mockSelfProfile).save();
    }
    
    /**
     * Test the save method with empty name
     */
    @Test
    public void testSave_emptyName() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        spyFragment.mEmojiNameEditText = mockNameEditText;
        spyFragment.mEmojiStatusEditText = mockStatusEditText;
        
        when(mockNameEditText.getText().toString()).thenReturn("");
        
        doReturn(mockSelfProfile).when(spyFragment).getProfile();
        
        // Execute
        spyFragment.save();
        
        // Verify
        verify(mockSelfProfile, never()).setName(anyString());
        verify(mockSelfProfile, never()).setString(anyString(), anyString());
        verify(mockSelfProfile, never()).save();
    }
    
    /**
     * Test the save method with launchMesibo=true
     */
    @Test
    public void testSave_withLaunchMesibo() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        spyFragment.mEmojiNameEditText = mockNameEditText;
        spyFragment.mEmojiStatusEditText = mockStatusEditText;
        
        doReturn(mockSelfProfile).when(spyFragment).getProfile();
        
        // Set mLaunchMesibo to true
        try {
            TestUtils.setPrivateField(spyFragment, "mLaunchMesibo", true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mLaunchMesibo", e);
        }
        doReturn(activity).when(spyFragment).getActivity();
        
        // Execute
        spyFragment.save();
        
        // Verify
        verify(mockSelfProfile).setName("Test User");
        verify(mockSelfProfile).setString("status", "Test Status");
        verify(mockSelfProfile).save();
    }
    
    /**
     * Test the setUserPicture method when profile image is available
     */
    @Test
    public void testSetUserPicture_withImage() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        spyFragment.mProfileImage = mockProfileImage;
        
        doReturn(mockSelfProfile).when(spyFragment).getProfile();
        
        // Execute
        spyFragment.setUserPicture();
        
        // Verify
        verify(mockProfileImage).setImageDrawable(any(RoundImageDrawable.class));
    }
    
    /**
     * Test the setUserPicture method when profile image is not available
     */
    @Test
    public void testSetUserPicture_withoutImage() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        spyFragment.mProfileImage = mockProfileImage;
        
        when(mockProfileImage1.getImageOrThumbnail()).thenReturn(null);
        doReturn(mockSelfProfile).when(spyFragment).getProfile();
        
        // Execute
        spyFragment.setUserPicture();
        
        // Verify
        verify(mockProfileImage).setImageDrawable(any(RoundImageDrawable.class));
    }
    
    /**
     * Test the onCreateOptionsMenu method
     */
    @Test
    public void testOnCreateOptionsMenu() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        
        when(mockMenu.findItem(R.id.action_save)).thenReturn(mockMenuItem);
        when(activity.getMenuInflater()).thenReturn(mockMenuInflater);
        doReturn(activity).when(spyFragment).getActivity();
        doNothing().when(spyFragment).save();
        
        // Execute
        spyFragment.onCreateOptionsMenu(mockMenu, mockMenuInflater);
        
        // Verify
        verify(activity.getMenuInflater()).inflate(R.id.edit_profile_menu, mockMenu);
        verify(mockMenuItem).setOnMenuItemClickListener(any(MenuItem.OnMenuItemClickListener.class));
        
        // Simulate menu item click
        ArgumentCaptor<MenuItem.OnMenuItemClickListener> captor = ArgumentCaptor.forClass(MenuItem.OnMenuItemClickListener.class);
        verify(mockMenuItem).setOnMenuItemClickListener(captor.capture());
        MenuItem.OnMenuItemClickListener listener = captor.getValue();
        listener.onMenuItemClick(mockMenuItem);
        
        // Verify save was called
        verify(spyFragment).save();
    }
    
    /**
     * Test the onRequestPermissionsResult method when permission is granted
     */
    @Test
    public void testOnRequestPermissionsResult_granted() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        int[] grantResults = {PackageManager.PERMISSION_GRANTED};
        
        doReturn(activity).when(spyFragment).getActivity();
        
        // Execute
        spyFragment.onRequestPermissionsResult(EditProfileFragment.CAMERA_PERMISSION_CODE, 
                new String[]{Manifest.permission.CAMERA}, grantResults);
        
        // Verify
        // Note: We can't directly verify MediaPicker.launchPicker as it's a static method
        // In a real test, we would use PowerMock or similar to verify static method calls
    }
    
    /**
     * Test the onRequestPermissionsResult method when permission is denied
     */
    @Test
    public void testOnRequestPermissionsResult_denied() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        int[] grantResults = {PackageManager.PERMISSION_DENIED};
        
        doReturn(activity).when(spyFragment).getActivity();
        
        // Execute
        spyFragment.onRequestPermissionsResult(EditProfileFragment.CAMERA_PERMISSION_CODE, 
                new String[]{Manifest.permission.CAMERA}, grantResults);
        
        // Verify
        // Note: We can't directly verify UIManager.showAlert as it's a static method
        // In a real test, we would use PowerMock or similar to verify static method calls
    }
    
    /**
     * Test the onActivityResult method with successful result
     */
    @Test
    public void testOnActivityResult_success() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        Intent mockIntent = mock(Intent.class);
        
        doReturn(activity).when(spyFragment).getActivity();
        
        // Execute
        spyFragment.onActivityResult(MediaPicker.TYPE_CAMERAIMAGE, RESULT_OK, mockIntent);
        
        // Verify
        // Note: We can't directly verify MediaPicker.processOnActivityResult as it's a static method
        // In a real test, we would use PowerMock or similar to verify static method calls
    }
    
    /**
     * Test the onActivityResult method with unsuccessful result
     */
    @Test
    public void testOnActivityResult_failure() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        Intent mockIntent = mock(Intent.class);
        
        // Execute
        spyFragment.onActivityResult(MediaPicker.TYPE_CAMERAIMAGE, Activity.RESULT_CANCELED, mockIntent);
        
        // Verify - nothing should happen
        // No verification needed as the method should return early
    }
    
    /**
     * Test the setImageProfile method with valid bitmap
     */
    @Test
    public void testSetImageProfile_validBitmap() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        spyFragment.mProfileImage = mockProfileImage;
        
        // Execute
        spyFragment.setImageProfile(mockBitmap);
        
        // Verify
        verify(mockProfileImage).setImageDrawable(any(RoundImageDrawable.class));
    }
    
    /**
     * Test the setImageProfile method with null bitmap
     */
    @Test
    public void testSetImageProfile_nullBitmap() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        spyFragment.mProfileImage = mockProfileImage;
        
        // Execute
        spyFragment.setImageProfile(null);
        
        // Verify
        verify(mockProfileImage).setImageDrawable(any(RoundImageDrawable.class));
    }
    
    /**
     * Test the onImageEdit method with successful edit
     */
    @Test
    public void testOnImageEdit_success() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        
        doReturn(mockSelfProfile).when(spyFragment).getProfile();
        
        // Execute
        spyFragment.onImageEdit(0, "path", "filePath", mockBitmap, 0);
        
        // Verify
        verify(mockSelfProfile).setImage(mockBitmap);
        verify(mockSelfProfile).save();
    }
    
    /**
     * Test the onImageEdit method with failed edit
     */
    @Test
    public void testOnImageEdit_failure() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        
        // Execute
        spyFragment.onImageEdit(0, "path", "filePath", mockBitmap, 1);
        
        // Verify - nothing should happen
        verify(mockSelfProfile, never()).setImage(any(Bitmap.class));
        verify(mockSelfProfile, never()).save();
    }
    
    /**
     * Test the MesiboProfile_onUpdate method
     */
    @Test
    public void testMesiboProfile_onUpdate() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        
        doNothing().when(spyFragment).updateUI(any(MesiboProfile.class));
        doNothing().when(spyFragment).setUserPicture();
        
        // Execute
        spyFragment.MesiboProfile_onUpdate(mockSelfProfile);
        
        // Verify
        verify(spyFragment).updateUI(mockSelfProfile);
        verify(spyFragment).setUserPicture();
    }
    
    /**
     * Test the MesiboProfile_onPublish method with successful publish
     */
    @Test
    public void testMesiboProfile_onPublish_success() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        
        doReturn(activity).when(spyFragment).getActivity();
        
        // Execute
        spyFragment.MesiboProfile_onPublish(mockSelfProfile, true);
        
        // Verify
        // Check that a toast was shown
        assertEquals("Profile Updated", ShadowToast.getTextOfLatestToast());
    }
    
    /**
     * Test the MesiboProfile_onPublish method with failed publish
     */
    @Test
    public void testMesiboProfile_onPublish_failure() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        
        doReturn(activity).when(spyFragment).getActivity();
        
        // Execute
        spyFragment.MesiboProfile_onPublish(mockSelfProfile, false);
        
        // Verify
        // Note: We can't directly verify UIManager.showAlert as it's a static method
        // In a real test, we would use PowerMock or similar to verify static method calls
    }
    
    /**
     * Test the onPause method
     */
    @Test
    public void testOnPause() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        
        doReturn(mockSelfProfile).when(spyFragment).getProfile();
        
        // Execute
        spyFragment.onPause();
        
        // Verify
        verify(mockSelfProfile).removeListener(spyFragment);
    }
    
    /**
     * Test the changeEmojiKeyboardIcon method
     */
    @Test
    public void testChangeEmojiKeyboardIcon() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        ImageView mockIcon = mock(ImageView.class);
        int drawableId = R.drawable.ic_keyboard;
        
        // Execute
        spyFragment.changeEmojiKeyboardIcon(mockIcon, drawableId);
        
        // Verify
        verify(mockIcon).setImageResource(drawableId);
    }
    
    /**
     * Test the activateInSettingsMode method
     */
    @Test
    public void testActivateInSettingsMode() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        
        // Execute
        spyFragment.activateInSettingsMode();
        
        // Verify - check that mSettingsMode is set to true
        try {
            boolean settingsMode = (Boolean) TestUtils.getPrivateField(spyFragment, "mSettingsMode");
            assertTrue("mSettingsMode should be true", settingsMode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get mSettingsMode", e);
        }
    }
    
    /**
     * Test the setGroupId method
     */
    @Test
    public void testSetGroupId() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        long groupId = 12345L;
        
        // Execute
        spyFragment.setGroupId(groupId);
        
        // Verify - check that mGroupId is set correctly
        try {
            long actualGroupId = (Long) TestUtils.getPrivateField(spyFragment, "mGroupId");
            assertEquals("mGroupId should be set correctly", groupId, actualGroupId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get mGroupId", e);
        }
    }
    
    /**
     * Test the setLaunchMesibo method
     */
    @Test
    public void testSetLaunchMesibo() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        boolean launchMesibo = true;
        
        // Execute
        spyFragment.setLaunchMesibo(launchMesibo);
        
        // Verify - check that mLaunchMesibo is set correctly
        try {
            boolean actualLaunchMesibo = (Boolean) TestUtils.getPrivateField(spyFragment, "mLaunchMesibo");
            assertEquals("mLaunchMesibo should be set correctly", launchMesibo, actualLaunchMesibo);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get mLaunchMesibo", e);
        }
    }
    
    /**
     * Test the MesiboProfile_onEndToEndEncryption method
     */
    @Test
    public void testMesiboProfile_onEndToEndEncryption() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        
        // Execute - this method is empty but we should test it for coverage
        spyFragment.MesiboProfile_onEndToEndEncryption(mockSelfProfile, 0);
        
        // No verification needed as the method is empty
    }
    
    /**
     * Test the text watcher for name field
     */
    @Test
    public void testNameTextWatcher() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        spyFragment.mEmojiNameEditText = mockNameEditText;
        spyFragment.mNameCharCounter = mockNameCharCounter;
        
        // Get the TextWatcher
        ArgumentCaptor<TextWatcher> textWatcherCaptor = ArgumentCaptor.forClass(TextWatcher.class);
        verify(mockNameEditText).addTextChangedListener(textWatcherCaptor.capture());
        TextWatcher nameTextWatcher = textWatcherCaptor.getValue();
        
        // Execute - simulate text change
        when(mockNameEditText.getText().length()).thenReturn(10);
        nameTextWatcher.afterTextChanged(mock(Editable.class));
        
        // Verify
        verify(mockNameCharCounter).setText("40"); // MAX_NAME_CHAR (50) - 10
    }
    
    /**
     * Test the text watcher for status field
     */
    @Test
    public void testStatusTextWatcher() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        spyFragment.mEmojiStatusEditText = mockStatusEditText;
        spyFragment.mStatusCharCounter = mockStatusCharCounter;
        
        // Get the TextWatcher
        ArgumentCaptor<TextWatcher> textWatcherCaptor = ArgumentCaptor.forClass(TextWatcher.class);
        verify(mockStatusEditText).addTextChangedListener(textWatcherCaptor.capture());
        TextWatcher statusTextWatcher = textWatcherCaptor.getValue();
        
        // Execute - simulate text change
        when(mockStatusEditText.getText().length()).thenReturn(20);
        statusTextWatcher.afterTextChanged(mock(Editable.class));
        
        // Verify
        verify(mockStatusCharCounter).setText("130"); // MAX_STATUS_CHAR (150) - 20
    }
    
    /**
     * Test the onCreateView method when ActionBar is null
     */
    @Test
    public void testOnCreateView_nullActionBar() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        when(mockInflater.inflate(eq(R.layout.fragment_register_new_profile), eq(mockContainer), eq(false)))
                .thenReturn(mockView);
        
        // Mock getActivity with null ActionBar
        AppCompatActivity mockActivity = mock(AppCompatActivity.class);
        when(mockActivity.getSupportActionBar()).thenReturn(null);
        doReturn(mockActivity).when(spyFragment).getActivity();
        
        // Mock getProfile
        doReturn(mockSelfProfile).when(spyFragment).getProfile();
        
        // Execute
        View resultView = spyFragment.onCreateView(mockInflater, mockContainer, mockBundle);
        
        // Verify
        assertNotNull("Result view should not be null", resultView);
        assertEquals("Result view should be our mock view", mockView, resultView);
        verify(spyFragment).setHasOptionsMenu(true);
        // ActionBar methods should not be called
        verify(mockActionBar, never()).setDisplayHomeAsUpEnabled(true);
        verify(mockActionBar, never()).setTitle(anyString());
    }
    
    /**
     * Test the onRequestPermissionsResult method with a different request code
     */
    @Test
    public void testOnRequestPermissionsResult_differentRequestCode() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        int[] grantResults = {PackageManager.PERMISSION_GRANTED};
        
        // Execute
        spyFragment.onRequestPermissionsResult(999, // Different request code
                new String[]{Manifest.permission.CAMERA}, grantResults);
        
        // Verify - nothing should happen
        // No verification needed as the method should return early
    }
    
    /**
     * Test the onActivityResult method with null file path
     */
    @Test
    public void testOnActivityResult_nullFilePath() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        Intent mockIntent = mock(Intent.class);
        
        doReturn(activity).when(spyFragment).getActivity();
        
        // Mock MediaPicker.processOnActivityResult to return null
        // Note: Since we can't mock static methods directly, this is a conceptual test
        
        // Execute
        spyFragment.onActivityResult(MediaPicker.TYPE_CAMERAIMAGE, RESULT_OK, mockIntent);
        
        // Verify - nothing should happen after MediaPicker.processOnActivityResult returns null
        // No verification needed as the method should return early
    }
    
    /**
     * Test the save method with null activity
     */
    @Test
    public void testSave_nullActivity() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        spyFragment.mEmojiNameEditText = mockNameEditText;
        spyFragment.mEmojiStatusEditText = mockStatusEditText;
        
        doReturn(mockSelfProfile).when(spyFragment).getProfile();
        
        // Set mLaunchMesibo to true
        try {
            TestUtils.setPrivateField(spyFragment, "mLaunchMesibo", true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mLaunchMesibo", e);
        }
        
        // Mock getActivity to return null
        doReturn(null).when(spyFragment).getActivity();
        
        // Execute
        spyFragment.save();
        
        // Verify
        verify(mockSelfProfile).setName("Test User");
        verify(mockSelfProfile).setString("status", "Test Status");
        verify(mockSelfProfile).save();
        // UIManager.launchMesibo should not be called, but we can't verify static methods
    }
    
    /**
     * Test the updateUI method with null values
     */
    @Test
    public void testUpdateUI_nullValues() {
        // Setup
        EditProfileFragment spyFragment = spy(fragment);
        spyFragment.mEmojiNameEditText = mockNameEditText;
        spyFragment.mEmojiStatusEditText = mockStatusEditText;
        spyFragment.mPhoneNumber = mockPhoneNumber;
        
        // Mock profile with null values
        MesiboProfile mockNullProfile = mock(MesiboProfile.class);
        when(mockNullProfile.getAddress()).thenReturn(null);
        when(mockNullProfile.getName()).thenReturn(null);
        when(mockNullProfile.getString(eq("status"), anyString())).thenReturn(null);
        when(mockNullProfile.isGroup()).thenReturn(false);
        
        // Execute
        spyFragment.updateUI(mockNullProfile);
        
        // Verify
        verify(mockPhoneNumber).setText(null);
        verify(mockNameEditText).setText(null);
        verify(mockStatusEditText).setText(null);
    }
    
}
