package org.mesibo.messenger;

import android.os.Bundle;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboFileTransfer;
import com.mesibo.api.MesiboHttp;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the MesiboFileTransferHelper class.
 * Tests file upload and download operations, progress tracking, callbacks, and error handling.
 */
public class MesiboFileTransferHelperTest extends BaseUnitTest {

    private MesiboFileTransferHelper fileTransferHelper;

    @Mock
    private MesiboFileTransfer mockFileTransfer;

    @Mock
    private MesiboHttp mockHttp;

    @Mock
    private MesiboHttp.Queue mockQueue;

    private static final String TEST_FILE_PATH = "/path/to/test/file.jpg";
    private static final String TEST_UPLOAD_URL = "https://example.com/upload";
    private static final String TEST_DOWNLOAD_URL = "https://example.com/download";
    private static final String TEST_TOKEN = "test_token";
    private static final long TEST_MID = 12345L;
    private static final String TEST_PEER = "test_peer";
    private static final String TEST_RESULT_URL = "https://example.com/files/file.jpg";

    @Override
    protected void setUpTest() {
        // Create the file transfer helper
        fileTransferHelper = new MesiboFileTransferHelper();

        // Mock SampleAPI static methods
        mockSampleAPI();

        // Set up common mock behavior
        setupMockFileTransfer();
        setupMockHttp();

        // Replace the queue with our mock
        try {
            Field queueField = MesiboFileTransferHelper.class.getDeclaredField("mQueue");
            queueField.setAccessible(true);
            queueField.set(null, mockQueue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mock queue", e);
        }
    }

    /**
     * Mock the SampleAPI static methods
     */
    private void mockSampleAPI() {
        // Use reflection to mock static methods
        try {
            TestUtils.setPrivateField(SampleAPI.class, "mToken", TEST_TOKEN);
            TestUtils.setPrivateField(SampleAPI.class, "mUploadUrl", TEST_UPLOAD_URL);
            TestUtils.setPrivateField(SampleAPI.class, "mDownloadUrl", TEST_DOWNLOAD_URL);
            TestUtils.setPrivateField(SampleAPI.class, "mMediaAutoDownload", true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to mock SampleAPI", e);
        }
    }

    /**
     * Set up common mock behavior for MesiboFileTransfer
     */
    private void setupMockFileTransfer() {
        when(mockFileTransfer.getPath()).thenReturn(TEST_FILE_PATH);
        when(mockFileTransfer.mid).thenReturn(TEST_MID);
        when(mockFileTransfer.peer).thenReturn(TEST_PEER);
    }

    /**
     * Set up common mock behavior for MesiboHttp
     */
    private void setupMockHttp() {
        when(mockHttp.execute()).thenReturn(true);
    }

    /**
     * Test starting a file upload operation
     */
    @Test
    public void testStartUpload_Success() {
        // Set up
        when(mockFileTransfer.upload).thenReturn(true);
        when(mockFileTransfer.priority).thenReturn(1);

        // Execute
        boolean result = fileTransferHelper.Mesibo_onStartFileTransfer(mockFileTransfer);

        // Verify
        assertTrue("Upload should start successfully", result);
        verify(mockQueue).queue(any(MesiboHttp.class));
        verify(mockFileTransfer).setFileTransferContext(any(MesiboHttp.class));

        // Capture the HTTP object to verify its properties
        ArgumentCaptor<MesiboHttp> httpCaptor = ArgumentCaptor.forClass(MesiboHttp.class);
        verify(mockQueue).queue(httpCaptor.capture());
        MesiboHttp capturedHttp = httpCaptor.getValue();

        assertEquals("Upload URL should match", TEST_UPLOAD_URL, capturedHttp.url);
        assertEquals("Upload file should match", TEST_FILE_PATH, capturedHttp.uploadFile);
        assertEquals("Upload file field should be 'photo'", "photo", capturedHttp.uploadFileField);
        assertNotNull("Bundle should not be null", capturedHttp.postBundle);
        assertEquals("Operation should be 'upload'", "upload", capturedHttp.postBundle.getString("op"));
        assertEquals("Token should match", TEST_TOKEN, capturedHttp.postBundle.getString("token"));
        assertEquals("MID should match", TEST_MID, capturedHttp.postBundle.getLong("mid"));
    }

    /**
     * Test starting a file upload operation with too many uploads in progress
     */
    @Test
    public void testStartUpload_TooManyUploads() {
        // Set up
        when(mockFileTransfer.upload).thenReturn(true);
        when(mockFileTransfer.priority).thenReturn(0);

        // Set upload counter to exceed limit
        try {
            Field uploadCounterField = MesiboFileTransferHelper.class.getDeclaredField("mUploadCounter");
            uploadCounterField.setAccessible(true);
            uploadCounterField.set(fileTransferHelper, 6);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set upload counter", e);
        }

        // Execute
        boolean result = fileTransferHelper.Mesibo_onStartFileTransfer(mockFileTransfer);

        // Verify
        assertFalse("Upload should not start", result);
        verify(mockQueue, never()).queue(any(MesiboHttp.class));
    }

    /**
     * Test starting a file download operation
     */
    @Test
    public void testStartDownload_Success() {
        // Set up
        when(mockFileTransfer.upload).thenReturn(false);
        when(mockFileTransfer.getUrl()).thenReturn(TEST_RESULT_URL);
        when(mockFileTransfer.origin).thenReturn(Mesibo.ORIGIN_REALTIME);
        when(mockFileTransfer.priority).thenReturn(1);

        // Execute
        boolean result = fileTransferHelper.Mesibo_onStartFileTransfer(mockFileTransfer);

        // Verify
        assertTrue("Download should start successfully", result);
        verify(mockQueue).queue(any(MesiboHttp.class));
        verify(mockFileTransfer).setFileTransferContext(any(MesiboHttp.class));

        // Capture the HTTP object to verify its properties
        ArgumentCaptor<MesiboHttp> httpCaptor = ArgumentCaptor.forClass(MesiboHttp.class);
        verify(mockQueue).queue(httpCaptor.capture());
        MesiboHttp capturedHttp = httpCaptor.getValue();

        assertEquals("Download URL should match", TEST_RESULT_URL, capturedHttp.url);
        assertEquals("Download file should match", TEST_FILE_PATH, capturedHttp.downloadFile);
        assertTrue("Resume should be true", capturedHttp.resume);
        assertEquals("Max retries should be 10", 10, capturedHttp.maxRetries);
    }

    /**
     * Test starting a download with an empty URL
     */
    @Test
    public void testStartDownload_EmptyUrl() {
        // Set up
        when(mockFileTransfer.upload).thenReturn(false);
        when(mockFileTransfer.getUrl()).thenReturn("");
        when(mockFileTransfer.origin).thenReturn(Mesibo.ORIGIN_REALTIME);

        // Execute
        boolean result = fileTransferHelper.Mesibo_onStartFileTransfer(mockFileTransfer);

        // Verify
        assertFalse("Download should not start with empty URL", result);
        verify(mockQueue, never()).queue(any(MesiboHttp.class));
    }

    /**
     * Test starting a download with a relative URL
     */
    @Test
    public void testStartDownload_RelativeUrl() {
        // Set up
        when(mockFileTransfer.upload).thenReturn(false);
        when(mockFileTransfer.getUrl()).thenReturn("relative/path/file.jpg");
        when(mockFileTransfer.origin).thenReturn(Mesibo.ORIGIN_REALTIME);
        when(mockFileTransfer.priority).thenReturn(1);

        // Execute
        boolean result = fileTransferHelper.Mesibo_onStartFileTransfer(mockFileTransfer);

        // Verify
        assertTrue("Download should start successfully", result);
        
        // Capture the HTTP object to verify its properties
        ArgumentCaptor<MesiboHttp> httpCaptor = ArgumentCaptor.forClass(MesiboHttp.class);
        verify(mockQueue).queue(httpCaptor.capture());
        MesiboHttp capturedHttp = httpCaptor.getValue();

        // The URL should be prefixed with the download URL
        assertEquals("Download URL should be prefixed", 
                TEST_DOWNLOAD_URL + "relative/path/file.jpg", capturedHttp.url);
    }

    /**
     * Test stopping a file transfer operation
     */
    @Test
    public void testStopFileTransfer() {
        // Set up
        when(mockFileTransfer.getFileTransferContext()).thenReturn(mockHttp);

        // Execute
        boolean result = fileTransferHelper.Mesibo_onStopFileTransfer(mockFileTransfer);

        // Verify
        assertTrue("Stop should be successful", result);
        verify(mockHttp).cancel();
    }

    /**
     * Test upload progress callback
     */
    @Test
    public void testUploadProgressCallback() {
        // Set up
        when(mockFileTransfer.upload).thenReturn(true);
        when(mockFileTransfer.priority).thenReturn(1);

        // Capture the HTTP listener
        ArgumentCaptor<MesiboHttp> httpCaptor = ArgumentCaptor.forClass(MesiboHttp.class);
        
        // Execute
        fileTransferHelper.Mesibo_onStartFileTransfer(mockFileTransfer);
        
        // Capture the HTTP object
        verify(mockQueue).queue(httpCaptor.capture());
        MesiboHttp capturedHttp = httpCaptor.getValue();
        MesiboHttp.Listener listener = capturedHttp.listener;
        
        // Simulate progress updates
        listener.Mesibo_onHttpProgress(capturedHttp, MesiboHttp.STATE_UPLOAD, 50);
        
        // Verify
        verify(mockFileTransfer).setProgress(50);
    }

    /**
     * Test successful upload completion
     */
    @Test
    public void testUploadCompletionSuccess() throws JSONException {
        // Set up
        when(mockFileTransfer.upload).thenReturn(true);
        when(mockFileTransfer.priority).thenReturn(1);
        
        // Create a mock HTTP object that will return our test response
        MesiboHttp mockHttpWithResponse = mock(MesiboHttp.class);
        when(mockHttpWithResponse.other).thenReturn(mockFileTransfer);
        
        // Create a JSON response
        String jsonResponse = "{\"result\":true,\"url\":\"" + TEST_RESULT_URL + "\"}";
        when(mockHttpWithResponse.getDataString()).thenReturn(jsonResponse);
        
        // Execute
        fileTransferHelper.Mesibo_onStartFileTransfer(mockFileTransfer);
        
        // Capture the HTTP listener
        ArgumentCaptor<MesiboHttp> httpCaptor = ArgumentCaptor.forClass(MesiboHttp.class);
        verify(mockQueue).queue(httpCaptor.capture());
        MesiboHttp.Listener listener = httpCaptor.getValue().listener;
        
        // Simulate completion
        boolean result = listener.Mesibo_onHttpProgress(mockHttpWithResponse, MesiboHttp.STATE_DOWNLOAD, 100);
        
        // Verify
        assertTrue("Listener should return true", result);
        verify(mockFileTransfer).setResult(true, TEST_RESULT_URL);
    }

    /**
     * Test upload failure
     */
    @Test
    public void testUploadFailure() {
        // Set up
        when(mockFileTransfer.upload).thenReturn(true);
        when(mockFileTransfer.priority).thenReturn(1);
        
        // Create a mock HTTP object
        MesiboHttp mockHttpWithResponse = mock(MesiboHttp.class);
        when(mockHttpWithResponse.other).thenReturn(mockFileTransfer);
        
        // Execute
        fileTransferHelper.Mesibo_onStartFileTransfer(mockFileTransfer);
        
        // Capture the HTTP listener
        ArgumentCaptor<MesiboHttp> httpCaptor = ArgumentCaptor.forClass(MesiboHttp.class);
        verify(mockQueue).queue(httpCaptor.capture());
        MesiboHttp.Listener listener = httpCaptor.getValue().listener;
        
        // Simulate failure
        boolean result = listener.Mesibo_onHttpProgress(mockHttpWithResponse, MesiboHttp.STATE_UPLOAD, -1);
        
        // Verify
        assertTrue("Listener should return true", result);
        verify(mockFileTransfer).setResult(false, null);
    }

    /**
     * Test download progress callback
     */
    @Test
    public void testDownloadProgressCallback() {
        // Set up
        when(mockFileTransfer.upload).thenReturn(false);
        when(mockFileTransfer.getUrl()).thenReturn(TEST_RESULT_URL);
        when(mockFileTransfer.origin).thenReturn(Mesibo.ORIGIN_REALTIME);
        when(mockFileTransfer.priority).thenReturn(1);
        
        // Execute
        fileTransferHelper.Mesibo_onStartFileTransfer(mockFileTransfer);
        
        // Capture the HTTP listener
        ArgumentCaptor<MesiboHttp> httpCaptor = ArgumentCaptor.forClass(MesiboHttp.class);
        verify(mockQueue).queue(httpCaptor.capture());
        MesiboHttp.Listener listener = httpCaptor.getValue().listener;
        
        // Simulate progress updates
        listener.Mesibo_onHttpProgress(httpCaptor.getValue(), MesiboHttp.STATE_DOWNLOAD, 75);
        
        // Verify
        verify(mockFileTransfer).setProgress(75);
    }

    /**
     * Test successful download completion
     */
    @Test
    public void testDownloadCompletionSuccess() {
        // Set up
        when(mockFileTransfer.upload).thenReturn(false);
        when(mockFileTransfer.getUrl()).thenReturn(TEST_RESULT_URL);
        when(mockFileTransfer.origin).thenReturn(Mesibo.ORIGIN_REALTIME);
        when(mockFileTransfer.priority).thenReturn(1);
        
        // Create a mock HTTP object
        MesiboHttp mockHttpWithResponse = mock(MesiboHttp.class);
        when(mockHttpWithResponse.other).thenReturn(mockFileTransfer);
        
        // Execute
        fileTransferHelper.Mesibo_onStartFileTransfer(mockFileTransfer);
        
        // Capture the HTTP listener
        ArgumentCaptor<MesiboHttp> httpCaptor = ArgumentCaptor.forClass(MesiboHttp.class);
        verify(mockQueue).queue(httpCaptor.capture());
        MesiboHttp.Listener listener = httpCaptor.getValue().listener;
        
        // Simulate completion
        boolean result = listener.Mesibo_onHttpProgress(mockHttpWithResponse, MesiboHttp.STATE_DOWNLOAD, 100);
        
        // Verify
        assertTrue("Listener should return true", result);
        verify(mockFileTransfer).setResult(true, null);
    }

    /**
     * Test download failure
     */
    @Test
    public void testDownloadFailure() {
        // Set up
        when(mockFileTransfer.upload).thenReturn(false);
        when(mockFileTransfer.getUrl()).thenReturn(TEST_RESULT_URL);
        when(mockFileTransfer.origin).thenReturn(Mesibo.ORIGIN_REALTIME);
        when(mockFileTransfer.priority).thenReturn(1);
        
        // Create a mock HTTP object
        MesiboHttp mockHttpWithResponse = mock(MesiboHttp.class);
        when(mockHttpWithResponse.other).thenReturn(mockFileTransfer);
        
        // Execute
        fileTransferHelper.Mesibo_onStartFileTransfer(mockFileTransfer);
        
        // Capture the HTTP listener
        ArgumentCaptor<MesiboHttp> httpCaptor = ArgumentCaptor.forClass(MesiboHttp.class);
        verify(mockQueue).queue(httpCaptor.capture());
        MesiboHttp.Listener listener = httpCaptor.getValue().listener;
        
        // Simulate failure
        boolean result = listener.Mesibo_onHttpProgress(mockHttpWithResponse, MesiboHttp.STATE_DOWNLOAD, -1);
        
        // Verify
        assertTrue("Listener should return true", result);
        verify(mockFileTransfer).setResult(false, null);
    }

    /**
     * Test counter updates for uploads and downloads
     */
    @Test
    public void testCounterUpdates() {
        // Test upload counter
        int uploadCount = fileTransferHelper.updateUploadCounter(2);
        assertEquals("Upload counter should be incremented by 2", 2, uploadCount);
        
        // Test download counter
        int downloadCount = fileTransferHelper.updateDownloadCounter(3);
        assertEquals("Download counter should be incremented by 3", 3, downloadCount);
        
        // Test multiple increments
        uploadCount = fileTransferHelper.updateUploadCounter(1);
        assertEquals("Upload counter should be incremented to 3", 3, uploadCount);
        
        downloadCount = fileTransferHelper.updateDownloadCounter(2);
        assertEquals("Download counter should be incremented to 5", 5, downloadCount);
    }

    /**
     * Test download with non-WiFi connectivity and auto-download disabled
     */
    @Test
    public void testDownloadWithNonWifiAndAutoDownloadDisabled() {
        // Set up
        when(mockFileTransfer.upload).thenReturn(false);
        when(mockFileTransfer.getUrl()).thenReturn(TEST_RESULT_URL);
        when(mockFileTransfer.origin).thenReturn(Mesibo.ORIGIN_REALTIME);
        when(mockFileTransfer.priority).thenReturn(0);
        
        // Mock SampleAPI.getMediaAutoDownload() to return false
        try {
            TestUtils.setPrivateField(SampleAPI.class, "mMediaAutoDownload", false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to mock SampleAPI", e);
        }
        
        // Mock Mesibo.getNetworkConnectivity() to return non-WiFi
        Mockito.mockStatic(Mesibo.class);
        when(Mesibo.getNetworkConnectivity()).thenReturn(Mesibo.CONNECTIVITY_MOBILE);
        
        // Execute
        boolean result = fileTransferHelper.Mesibo_onStartFileTransfer(mockFileTransfer);
        
        // Verify
        assertFalse("Download should not start with auto-download disabled on mobile data", result);
        verify(mockQueue, never()).queue(any(MesiboHttp.class));
    }

    /**
     * Test download with non-realtime origin and low priority
     */
    @Test
    public void testDownloadWithNonRealtimeOrigin() {
        // Set up
        when(mockFileTransfer.upload).thenReturn(false);
        when(mockFileTransfer.getUrl()).thenReturn(TEST_RESULT_URL);
        when(mockFileTransfer.origin).thenReturn(Mesibo.ORIGIN_UNKNOWN);
        when(mockFileTransfer.priority).thenReturn(0);
        
        // Execute
        boolean result = fileTransferHelper.Mesibo_onStartFileTransfer(mockFileTransfer);
        
        // Verify
        assertFalse("Download should not start with non-realtime origin and low priority", result);
        verify(mockQueue, never()).queue(any(MesiboHttp.class));
    }

    /**
     * Test direct execution when queue is null
     */
    @Test
    public void testDirectExecutionWhenQueueIsNull() {
        // Set up
        when(mockFileTransfer.upload).thenReturn(true);
        when(mockFileTransfer.priority).thenReturn(1);
        
        // Set queue to null
        try {
            Field queueField = MesiboFileTransferHelper.class.getDeclaredField("mQueue");
            queueField.setAccessible(true);
            queueField.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set queue to null", e);
        }
        
        // Create a mock HTTP that will be returned by the execute method
        MesiboHttp mockHttpWithExecute = mock(MesiboHttp.class);
        when(mockHttpWithExecute.execute()).thenReturn(true);
        
        // Use a spy to return our mock HTTP
        MesiboFileTransferHelper spy = Mockito.spy(fileTransferHelper);
        doAnswer(new Answer<MesiboHttp>() {
            @Override
            public MesiboHttp answer(InvocationOnMock invocation) throws Throwable {
                return mockHttpWithExecute;
            }
        }).when(spy).Mesibo_onStartUpload(any(MesiboFileTransfer.class));
        
        // Execute
        boolean result = spy.Mesibo_onStartFileTransfer(mockFileTransfer);
        
        // Verify
        assertTrue("Upload should start successfully", result);
        verify(mockHttpWithExecute).execute();
    }
}