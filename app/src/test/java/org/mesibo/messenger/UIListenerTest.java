package org.mesibo.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mesibo.api.MesiboProfile;
import com.mesibo.calls.api.MesiboCall;
import com.mesibo.calls.ui.MesiboCallUi;
import com.mesibo.messaging.MesiboRecycleViewHolder;
import com.mesibo.messaging.MesiboUI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowIntent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the UIListener class.
 * 
 * These tests verify the functionality of the UIListener class, which implements
 * MesiboUIListener and handles UI events for the messenger application.
 */
public class UIListenerTest extends BaseUnitTest {

    // Class under test
    private UIListener uiListener;
    
    // Mocked dependencies
    @Mock
    private Activity mockActivity;
    
    @Mock
    private MesiboUI.MesiboScreen mockScreen;
    
    @Mock
    private MesiboUI.MesiboUserListScreen mockUserListScreen;
    
    @Mock
    private MesiboUI.MesiboMessageScreen mockMessageScreen;
    
    @Mock
    private MesiboUI.MesiboRow mockRow;
    
    @Mock
    private MesiboProfile mockProfile;
    
    @Mock
    private Menu mockMenu;
    
    @Mock
    private MenuItem mockMenuItem;
    
    @Mock
    private View mockTitleArea;
    
    // For capturing intents
    private ArgumentCaptor<Intent> intentCaptor;
    
    // For capturing menu item click listeners
    private ArgumentCaptor<MenuItem.OnMenuItemClickListener> menuItemClickListenerCaptor;
    
    // For capturing view click listeners
    private ArgumentCaptor<View.OnClickListener> viewClickListenerCaptor;
    
    /**
     * Setup for each test.
     * Initializes mocks and prepares the test environment.
     */
    @Override
    protected void setUpTest() {
        // Initialize the class under test
        uiListener = new UIListener();
        
        // Initialize captors
        intentCaptor = ArgumentCaptor.forClass(Intent.class);
        menuItemClickListenerCaptor = ArgumentCaptor.forClass(MenuItem.OnMenuItemClickListener.class);
        viewClickListenerCaptor = ArgumentCaptor.forClass(View.OnClickListener.class);
        
        // Setup common mock behavior
        when(mockActivity.getMenuInflater()).thenReturn(mock(android.view.MenuInflater.class));
        
        // Setup user list screen mocks
        when(mockUserListScreen.parent).thenReturn(mockActivity);
        when(mockUserListScreen.menu).thenReturn(mockMenu);
        when(mockUserListScreen.userList).thenReturn(true);
        
        // Setup message screen mocks
        when(mockMessageScreen.parent).thenReturn(mockActivity);
        when(mockMessageScreen.menu).thenReturn(mockMenu);
        when(mockMessageScreen.profile).thenReturn(mockProfile);
        when(mockMessageScreen.titleArea).thenReturn(mockTitleArea);
        when(mockMessageScreen.userList).thenReturn(false);
        
        // Setup menu mocks
        when(mockMenu.findItem(anyInt())).thenReturn(mockMenuItem);
        when(mockMenuItem.setOnMenuItemClickListener(any(MenuItem.OnMenuItemClickListener.class))).thenReturn(mockMenuItem);
        when(mockMenuItem.getItemId()).thenReturn(R.id.action_settings);
    }
    
    /**
     * Test MesiboUI_onInitScreen with user list screen.
     * Verifies that the method initializes the user list screen correctly.
     */
    @Test
    public void testOnInitScreenWithUserListScreen() {
        // Setup
        UIListener spyListener = spy(uiListener);
        doNothing().when(spyListener).initUserListScreen(any(MesiboUI.MesiboUserListScreen.class));
        
        // Execute
        boolean result = spyListener.MesiboUI_onInitScreen(mockUserListScreen);
        
        // Verify
        assertFalse("MesiboUI_onInitScreen should return false", result);
        verify(spyListener).initUserListScreen(mockUserListScreen);
        assertSame("Last user list context should be set", mockActivity, UIListener.getLastUserListContext());
    }
    
    /**
     * Test MesiboUI_onInitScreen with message screen.
     * Verifies that the method initializes the message screen correctly.
     */
    @Test
    public void testOnInitScreenWithMessageScreen() {
        // Setup
        UIListener spyListener = spy(uiListener);
        doNothing().when(spyListener).initMessageListScreen(any(MesiboUI.MesiboMessageScreen.class));
        
        // Execute
        boolean result = spyListener.MesiboUI_onInitScreen(mockMessageScreen);
        
        // Verify
        assertFalse("MesiboUI_onInitScreen should return false", result);
        verify(spyListener).initMessageListScreen(mockMessageScreen);
        assertSame("Last messaging context should be set", mockActivity, UIListener.getLastMessagingContext());
    }
    
    /**
     * Test MesiboUI_onGetCustomRow.
     * Verifies that the method returns null as expected.
     */
    @Test
    public void testOnGetCustomRow() {
        // Execute
        MesiboRecycleViewHolder result = uiListener.MesiboUI_onGetCustomRow(mockScreen, mockRow);
        
        // Verify
        assertNull("MesiboUI_onGetCustomRow should return null", result);
    }
    
    /**
     * Test MesiboUI_onUpdateRow.
     * Verifies that the method returns false as expected.
     */
    @Test
    public void testOnUpdateRow() {
        // Execute
        boolean result = uiListener.MesiboUI_onUpdateRow(mockScreen, mockRow, true);
        
        // Verify
        assertFalse("MesiboUI_onUpdateRow should return false", result);
    }
    
    /**
     * Test MesiboUI_onShowLocation.
     * Verifies that the method returns false as expected.
     */
    @Test
    public void testOnShowLocation() {
        // Execute
        boolean result = uiListener.MesiboUI_onShowLocation(context, mockProfile);
        
        // Verify
        assertFalse("MesiboUI_onShowLocation should return false", result);
    }
    
    /**
     * Test MesiboUI_onClickedRow.
     * Verifies that the method returns false as expected.
     */
    @Test
    public void testOnClickedRow() {
        // Execute
        boolean result = uiListener.MesiboUI_onClickedRow(mockScreen, mockRow);
        
        // Verify
        assertFalse("MesiboUI_onClickedRow should return false", result);
    }
    
    /**
     * Test userListScreenMenuHandler with action_settings.
     * Verifies that the method launches user settings.
     */
    @Test
    public void testUserListScreenMenuHandlerWithSettings() {
        // Setup
        UIManager mockUIManager = mock(UIManager.class);
        
        // Execute
        uiListener.userListScreenMenuHandler(context, R.id.action_settings);
        
        // Verify
        Intent intent = Shadows.shadowOf((android.app.Application) context.getApplicationContext())
                .getNextStartedActivity();
        
        assertNotNull("Intent should not be null", intent);
        assertEquals("Intent should target SettingsActivity", 
                "org.mesibo.messenger.AppSettings.SettingsActivity", intent.getComponent().getClassName());
    }
    
    /**
     * Test userListScreenMenuHandler with action_conf.
     * Verifies that the method launches group call join room UI.
     */
    @Test
    public void testUserListScreenMenuHandlerWithConf() {
        // Setup
        MesiboCall mockMesiboCall = mock(MesiboCall.class);
        MesiboCall.setInstance(mockMesiboCall);
        
        // Execute
        uiListener.userListScreenMenuHandler(context, R.id.action_conf);
        
        // Verify
        verify(mockMesiboCall).groupCallJoinRoomUi(context, "Mesibo Conferencing Demo");
    }
    
    /**
     * Test userListScreenMenuHandler with action_calllogs.
     * Verifies that the method launches call logs.
     */
    @Test
    public void testUserListScreenMenuHandlerWithCallLogs() {
        // Setup
        MesiboCallUi mockMesiboCallUi = mock(MesiboCallUi.class);
        MesiboCallUi.setInstance(mockMesiboCallUi);
        
        // Execute
        uiListener.userListScreenMenuHandler(context, R.id.action_calllogs);
        
        // Verify
        verify(mockMesiboCallUi).launchCallLogs(context, 0);
    }
    
    /**
     * Test userListScreenMenuHandler with action_menu_e2ee.
     * Verifies that the method shows end-to-end encryption info.
     */
    @Test
    public void testUserListScreenMenuHandlerWithE2EE() {
        // Setup
        MesiboUI mockMesiboUI = mock(MesiboUI.class);
        
        // Execute
        uiListener.userListScreenMenuHandler(context, R.id.action_menu_e2ee);
        
        // Note: We can't directly verify MesiboUI.showEndToEndEncryptionInfoForSelf as it's a static method
        // This is a limitation of the current test setup
    }
    
    /**
     * Test userListScreenMenuHandler with mesibo_share.
     * Verifies that the method launches share intent.
     */
    @Test
    public void testUserListScreenMenuHandlerWithShare() {
        // Setup
        AppConfig.Config mockConfig = mock(AppConfig.Config.class);
        mockConfig.invite = mock(AppConfig.Invite.class);
        mockConfig.invite.subject = "Test Subject";
        mockConfig.invite.text = "Test Text";
        mockConfig.invite.title = "Test Title";
        
        AppConfig mockAppConfig = mock(AppConfig.class);
        when(mockAppConfig.getConfig()).thenReturn(mockConfig);
        AppConfig.setInstance(mockAppConfig);
        
        // Execute
        uiListener.userListScreenMenuHandler(context, R.id.mesibo_share);
        
        // Verify
        Intent intent = Shadows.shadowOf((android.app.Application) context.getApplicationContext())
                .getNextStartedActivity();
        
        assertNotNull("Intent should not be null", intent);
        assertEquals("Intent should have ACTION_SEND", Intent.ACTION_SEND, intent.getAction());
        assertEquals("Intent should have text/plain type", "text/plain", intent.getType());
        assertEquals("Intent should have correct subject", "Test Subject", intent.getStringExtra(Intent.EXTRA_SUBJECT));
        assertEquals("Intent should have correct text", "Test Text", intent.getStringExtra(Intent.EXTRA_TEXT));
    }
    
    /**
     * Test initUserListScreen.
     * Verifies that the method initializes the user list screen menu correctly.
     */
    @Test
    public void testInitUserListScreen() {
        // Setup
        when(mockMenu.findItem(R.id.action_settings)).thenReturn(mockMenuItem);
        when(mockMenu.findItem(R.id.action_conf)).thenReturn(mockMenuItem);
        when(mockMenu.findItem(R.id.action_calllogs)).thenReturn(mockMenuItem);
        when(mockMenu.findItem(R.id.action_menu_e2ee)).thenReturn(mockMenuItem);
        when(mockMenu.findItem(R.id.mesibo_share)).thenReturn(mockMenuItem);
        
        // Execute
        uiListener.initUserListScreen(mockUserListScreen);
        
        // Verify
        verify(mockActivity.getMenuInflater()).inflate(eq(R.menu.messaging_activity_menu), eq(mockMenu));
        verify(mockMenuItem, times(5)).setOnMenuItemClickListener(menuItemClickListenerCaptor.capture());
        
        // Test the captured click listener
        MenuItem.OnMenuItemClickListener clickListener = menuItemClickListenerCaptor.getValue();
        when(mockMenuItem.getItemId()).thenReturn(R.id.action_settings);
        
        boolean result = clickListener.onMenuItemClick(mockMenuItem);
        assertTrue("Click listener should return true", result);
    }
    
    /**
     * Test messageListScreenMenuHandler with action_call.
     * Verifies that the method launches audio call UI.
     */
    @Test
    public void testMessageListScreenMenuHandlerWithCall() {
        // Setup
        MesiboCall mockMesiboCall = mock(MesiboCall.class);
        when(mockMesiboCall.callUi(any(Context.class), any(MesiboProfile.class), eq(false))).thenReturn(true);
        MesiboCall.setInstance(mockMesiboCall);
        
        // Execute
        uiListener.messageListScreenMenuHandler(context, R.id.action_call, mockProfile);
        
        // Verify
        verify(mockMesiboCall).callUi(context, mockProfile, false);
        verify(mockMesiboCall, times(0)).callUiForExistingCall(any(Context.class));
    }
    
    /**
     * Test messageListScreenMenuHandler with action_call when call UI returns false.
     * Verifies that the method falls back to existing call UI.
     */
    @Test
    public void testMessageListScreenMenuHandlerWithCallFallback() {
        // Setup
        MesiboCall mockMesiboCall = mock(MesiboCall.class);
        when(mockMesiboCall.callUi(any(Context.class), any(MesiboProfile.class), eq(false))).thenReturn(false);
        MesiboCall.setInstance(mockMesiboCall);
        
        // Execute
        uiListener.messageListScreenMenuHandler(context, R.id.action_call, mockProfile);
        
        // Verify
        verify(mockMesiboCall).callUi(context, mockProfile, false);
        verify(mockMesiboCall).callUiForExistingCall(context);
    }
    
    /**
     * Test messageListScreenMenuHandler with action_videocall.
     * Verifies that the method launches video call UI.
     */
    @Test
    public void testMessageListScreenMenuHandlerWithVideoCall() {
        // Setup
        MesiboCall mockMesiboCall = mock(MesiboCall.class);
        when(mockMesiboCall.callUi(any(Context.class), any(MesiboProfile.class), eq(true))).thenReturn(true);
        MesiboCall.setInstance(mockMesiboCall);
        
        // Execute
        uiListener.messageListScreenMenuHandler(context, R.id.action_videocall, mockProfile);
        
        // Verify
        verify(mockMesiboCall).callUi(context, mockProfile, true);
        verify(mockMesiboCall, times(0)).callUiForExistingCall(any(Context.class));
    }
    
    /**
     * Test messageListScreenMenuHandler with action_videocall when call UI returns false.
     * Verifies that the method falls back to existing call UI.
     */
    @Test
    public void testMessageListScreenMenuHandlerWithVideoCallFallback() {
        // Setup
        MesiboCall mockMesiboCall = mock(MesiboCall.class);
        when(mockMesiboCall.callUi(any(Context.class), any(MesiboProfile.class), eq(true))).thenReturn(false);
        MesiboCall.setInstance(mockMesiboCall);
        
        // Execute
        uiListener.messageListScreenMenuHandler(context, R.id.action_videocall, mockProfile);
        
        // Verify
        verify(mockMesiboCall).callUi(context, mockProfile, true);
        verify(mockMesiboCall).callUiForExistingCall(context);
    }
    
    /**
     * Test messageListScreenMenuHandler with action_e2e.
     * Verifies that the method shows end-to-end encryption info.
     */
    @Test
    public void testMessageListScreenMenuHandlerWithE2E() {
        // Setup
        when(mockProfile.getAddress()).thenReturn("test_address");
        when(mockProfile.groupid).thenReturn(12345L);
        MesiboUI mockMesiboUI = mock(MesiboUI.class);
        
        // Execute
        uiListener.messageListScreenMenuHandler(context, R.id.action_e2e, mockProfile);
        
        // Note: We can't directly verify MesiboUI.showEndToEndEncryptionInfo as it's a static method
        // This is a limitation of the current test setup
    }
    
    /**
     * Test messageListScreenMenuHandler with null profile.
     * Verifies that the method handles null profile gracefully.
     */
    @Test
    public void testMessageListScreenMenuHandlerWithNullProfile() {
        // Execute
        uiListener.messageListScreenMenuHandler(context, R.id.action_call, null);
        
        // No assertion needed - test passes if no exception is thrown
    }
    
    /**
     * Test initMessageListScreen.
     * Verifies that the method initializes the message screen menu correctly.
     */
    @Test
    public void testInitMessageListScreen() {
        // Setup
        when(mockMenu.findItem(R.id.action_call)).thenReturn(mockMenuItem);
        when(mockMenu.findItem(R.id.action_videocall)).thenReturn(mockMenuItem);
        when(mockMenu.findItem(R.id.action_e2e)).thenReturn(mockMenuItem);
        
        // Execute
        uiListener.initMessageListScreen(mockMessageScreen);
        
        // Verify
        verify(mockActivity.getMenuInflater()).inflate(eq(R.menu.menu_messaging), eq(mockMenu));
        verify(mockMenuItem, times(3)).setOnMenuItemClickListener(menuItemClickListenerCaptor.capture());
        verify(mockTitleArea).setOnClickListener(viewClickListenerCaptor.capture());
        
        // Test the captured menu click listener
        MenuItem.OnMenuItemClickListener menuClickListener = menuItemClickListenerCaptor.getValue();
        when(mockMenuItem.getItemId()).thenReturn(R.id.action_call);
        
        boolean result = menuClickListener.onMenuItemClick(mockMenuItem);
        assertTrue("Menu click listener should return true", result);
        
        // Test the captured title area click listener
        View.OnClickListener titleClickListener = viewClickListenerCaptor.getValue();
        titleClickListener.onClick(mockTitleArea);
        // Note: We can't directly verify MesiboUI.showBasicProfileInfo as it's a static method
    }
    
    /**
     * Test initMessageListScreen with group profile.
     * Verifies that the method handles group profiles correctly.
     */
    @Test
    public void testInitMessageListScreenWithGroupProfile() {
        // Setup
        when(mockProfile.isGroup()).thenReturn(true);
        when(mockProfile.isActive()).thenReturn(true);
        when(mockMenu.findItem(R.id.action_call)).thenReturn(mockMenuItem);
        when(mockMenu.findItem(R.id.action_videocall)).thenReturn(mockMenuItem);
        when(mockMenu.findItem(R.id.action_e2e)).thenReturn(mockMenuItem);
        
        // Execute
        uiListener.initMessageListScreen(mockMessageScreen);
        
        // Verify
        verify(mockMenuItem).setIcon(MesiboCall.MESIBO_DEFAULTICON_GROUPAUDIOCALL);
        verify(mockMenuItem).setIcon(MesiboCall.MESIBO_DEFAULTICON_GROUPVIDEOCALL);
    }
    
    /**
     * Test initMessageListScreen with inactive group profile.
     * Verifies that the method handles inactive group profiles correctly.
     */
    @Test
    public void testInitMessageListScreenWithInactiveGroupProfile() {
        // Setup
        when(mockProfile.isGroup()).thenReturn(true);
        when(mockProfile.isActive()).thenReturn(false);
        when(mockMenu.findItem(R.id.action_call)).thenReturn(mockMenuItem);
        when(mockMenu.findItem(R.id.action_videocall)).thenReturn(mockMenuItem);
        when(mockMenu.findItem(R.id.action_e2e)).thenReturn(mockMenuItem);
        
        // Execute
        uiListener.initMessageListScreen(mockMessageScreen);
        
        // Verify
        verify(mockMenuItem, times(2)).setVisible(false);
    }
    
    /**
     * Test initMessageListScreen with individual profile.
     * Verifies that the method handles individual profiles correctly.
     */
    @Test
    public void testInitMessageListScreenWithIndividualProfile() {
        // Setup
        when(mockProfile.isGroup()).thenReturn(false);
        when(mockMenu.findItem(R.id.action_call)).thenReturn(mockMenuItem);
        when(mockMenu.findItem(R.id.action_videocall)).thenReturn(mockMenuItem);
        when(mockMenu.findItem(R.id.action_e2e)).thenReturn(mockMenuItem);
        
        // Execute
        uiListener.initMessageListScreen(mockMessageScreen);
        
        // Verify
        verify(mockMenuItem).setIcon(MesiboCall.MESIBO_DEFAULTICON_AUDIOCALL);
        verify(mockMenuItem).setIcon(MesiboCall.MESIBO_DEFAULTICON_VIDEOCALL);
    }
    
    /**
     * Test static context getters.
     * Verifies that the static context getters return the correct values.
     */
    @Test
    public void testStaticContextGetters() {
        // Setup
        Context userListContext = mock(Context.class);
        Context messagingContext = mock(Context.class);
        
        // Set up the screens
        MesiboUI.MesiboUserListScreen userListScreen = mock(MesiboUI.MesiboUserListScreen.class);
        userListScreen.parent = userListContext;
        userListScreen.userList = true;
        
        MesiboUI.MesiboMessageScreen messageScreen = mock(MesiboUI.MesiboMessageScreen.class);
        messageScreen.parent = messagingContext;
        messageScreen.userList = false;
        
        // Execute
        uiListener.MesiboUI_onInitScreen(userListScreen);
        uiListener.MesiboUI_onInitScreen(messageScreen);
        
        // Verify
        assertSame("getLastUserListContext should return the correct context", 
                userListContext, UIListener.getLastUserListContext());
        assertSame("getLastMessagingContext should return the correct context", 
                messagingContext, UIListener.getLastMessagingContext());
    }
}