package org.mesibo.messenger.AppSettings;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.junit.Test;
import org.mesibo.messenger.BaseUnitTest;
import org.mesibo.messenger.BuildConfig;
import org.mesibo.messenger.DefaultTestDependencyProvider;
import org.mesibo.messenger.R;
import org.mesibo.messenger.TestDependencyProvider;
import org.mockito.Mock;
import org.robolectric.Robolectric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the AboutFragment class.
 * 
 * These tests verify the functionality of the AboutFragment, which displays
 * information about the application.
 */
public class AboutFragmentTest extends BaseUnitTest {

    private AboutFragment fragment;
    private SettingsActivity activity;
    
    @Mock
    private TextView mockMesiboLogo;
    
    @Mock
    private TextView mockVersion;
    
    @Mock
    private TextView mockBuildDate;
    
    @Mock
    private AssetManager mockAssetManager;
    
    @Mock
    private Typeface mockTypeface;
    
    // Custom dependency provider for AboutFragmentTest
    private class AboutFragmentTestDependencyProvider extends DefaultTestDependencyProvider {
        @Override
        public Typeface getTypefaceFromAsset(Context context, String path) {
            // Return the mock typeface for testing
            return mockTypeface;
        }
    }
    
    @Override
    protected TestDependencyProvider createDependencyProvider() {
        return new AboutFragmentTestDependencyProvider();
    }
    
    @Override
    protected void setUpTest() {
        // Create the activity that will host the fragment
        activity = Robolectric.buildActivity(SettingsActivity.class).create().get();
        
        // Create the fragment
        fragment = new AboutFragment();
    }
    
    /**
     * Test the onCreateView method of AboutFragment.
     * Verifies that the fragment view is properly initialized with the correct layout
     * and UI components.
     */
    @Test
    public void testOnCreateView() {
        // Setup
        AboutFragment spyFragment = spy(fragment);
        LayoutInflater inflater = activity.getLayoutInflater();
        ViewGroup container = new ViewGroup(activity) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                // Do nothing
            }
        };
        
        // Mock the view returned by inflater.inflate
        View mockView = mock(View.class);
        when(mockView.findViewById(R.id.mesibologo)).thenReturn(mockMesiboLogo);
        when(mockView.findViewById(R.id.version)).thenReturn(mockVersion);
        when(mockView.findViewById(R.id.builddate)).thenReturn(mockBuildDate);
        
        // Mock inflater.inflate to return our mock view
        doReturn(mockView).when(spyFragment).getView();
        
        // Mock getActivity and getAssets
        when(spyFragment.getActivity()).thenReturn(activity);
        when(activity.getAssets()).thenReturn(mockAssetManager);
        
        // The mock typeface will be provided by our custom dependency provider
        
        // Execute
        View resultView = spyFragment.onCreateView(inflater, container, null);
        
        // Verify
        verify(mockMesiboLogo).setTypeface(mockTypeface);
        verify(mockVersion).setText(eq("Version: " + BuildConfig.BUILD_VERSION));
        verify(mockBuildDate).setText(eq("Build Time: " + BuildConfig.BUILD_TIMESTAMP));
    }
    
    /**
     * Test the onCreateView method when Typeface.createFromAsset returns null.
     * Verifies that the method handles null typeface gracefully.
     */
    @Test
    public void testOnCreateViewWithNullTypeface() {
        // Setup
        AboutFragment spyFragment = spy(fragment);
        LayoutInflater inflater = activity.getLayoutInflater();
        ViewGroup container = new ViewGroup(activity) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                // Do nothing
            }
        };
        
        // Mock the view returned by inflater.inflate
        View mockView = mock(View.class);
        when(mockView.findViewById(R.id.mesibologo)).thenReturn(mockMesiboLogo);
        when(mockView.findViewById(R.id.version)).thenReturn(mockVersion);
        when(mockView.findViewById(R.id.builddate)).thenReturn(mockBuildDate);
        
        // Mock inflater.inflate to return our mock view
        doReturn(mockView).when(spyFragment).getView();
        
        // Mock getActivity and getAssets
        when(spyFragment.getActivity()).thenReturn(activity);
        when(activity.getAssets()).thenReturn(mockAssetManager);
        
        // Override the dependency provider to return null for typeface
        ((AboutFragmentTestDependencyProvider) dependencyProvider).getTypefaceFromAsset = (ctx, path) -> null;
        
        // Execute
        View resultView = spyFragment.onCreateView(inflater, container, null);
        
        // Verify
        verify(mockMesiboLogo, times(0)).setTypeface(any(Typeface.class)); // Should not be called
        verify(mockVersion).setText(eq("Version: " + BuildConfig.BUILD_VERSION));
        verify(mockBuildDate).setText(eq("Build Time: " + BuildConfig.BUILD_TIMESTAMP));
    }
    
    
    /**
     * Test the setHasOptionsMenu method call in onCreateView.
     * Verifies that setHasOptionsMenu is called with the correct value.
     */
    @Test
    public void testSetHasOptionsMenu() {
        // Setup
        AboutFragment spyFragment = spy(fragment);
        LayoutInflater inflater = activity.getLayoutInflater();
        ViewGroup container = new ViewGroup(activity) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                // Do nothing
            }
        };
        
        // Mock the view returned by inflater.inflate
        View mockView = mock(View.class);
        when(mockView.findViewById(R.id.mesibologo)).thenReturn(mockMesiboLogo);
        when(mockView.findViewById(R.id.version)).thenReturn(mockVersion);
        when(mockView.findViewById(R.id.builddate)).thenReturn(mockBuildDate);
        
        // Mock inflater.inflate to return our mock view
        when(inflater.inflate(eq(R.layout.about), eq(container), eq(false))).thenReturn(mockView);
        
        // Mock getActivity and getAssets
        when(spyFragment.getActivity()).thenReturn(activity);
        when(activity.getAssets()).thenReturn(mockAssetManager);
        
        // Execute
        spyFragment.onCreateView(inflater, container, null);
        
        // Verify
        verify(spyFragment).setHasOptionsMenu(true);
    }
    
    /**
     * Test the onCreateView method with null container.
     * Verifies that the method handles null container gracefully.
     */
    @Test
    public void testOnCreateViewWithNullContainer() {
        // Setup
        AboutFragment spyFragment = spy(fragment);
        LayoutInflater inflater = activity.getLayoutInflater();
        
        // Mock the view returned by inflater.inflate
        View mockView = mock(View.class);
        when(mockView.findViewById(R.id.mesibologo)).thenReturn(mockMesiboLogo);
        when(mockView.findViewById(R.id.version)).thenReturn(mockVersion);
        when(mockView.findViewById(R.id.builddate)).thenReturn(mockBuildDate);
        
        // Mock inflater.inflate to return our mock view
        when(inflater.inflate(eq(R.layout.about), eq(null), eq(false))).thenReturn(mockView);
        
        // Mock getActivity and getAssets
        when(spyFragment.getActivity()).thenReturn(activity);
        when(activity.getAssets()).thenReturn(mockAssetManager);
        
        // Execute
        View resultView = spyFragment.onCreateView(inflater, null, null);
        
        // Verify
        assertNotNull("Result view should not be null", resultView);
        assertEquals("Result view should be our mock view", mockView, resultView);
    }
    
    /**
     * Test the onCreateView method with null bundle.
     * Verifies that the method handles null bundle gracefully.
     */
    @Test
    public void testOnCreateViewWithNullBundle() {
        // Setup
        AboutFragment spyFragment = spy(fragment);
        LayoutInflater inflater = activity.getLayoutInflater();
        ViewGroup container = new ViewGroup(activity) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                // Do nothing
            }
        };
        
        // Mock the view returned by inflater.inflate
        View mockView = mock(View.class);
        when(mockView.findViewById(R.id.mesibologo)).thenReturn(mockMesiboLogo);
        when(mockView.findViewById(R.id.version)).thenReturn(mockVersion);
        when(mockView.findViewById(R.id.builddate)).thenReturn(mockBuildDate);
        
        // Mock inflater.inflate to return our mock view
        when(inflater.inflate(eq(R.layout.about), eq(container), eq(false))).thenReturn(mockView);
        
        // Mock getActivity and getAssets
        when(spyFragment.getActivity()).thenReturn(activity);
        when(activity.getAssets()).thenReturn(mockAssetManager);
        
        // Execute
        View resultView = spyFragment.onCreateView(inflater, container, null);
        
        // Verify
        assertNotNull("Result view should not be null", resultView);
        assertEquals("Result view should be our mock view", mockView, resultView);
    }
}
