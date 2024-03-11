package com.ramp.jmeter.hls_player.logic;

import java.io.ByteArrayOutputStream;
import org.apache.jmeter.control.GenericController;
import org.apache.jmeter.control.NextIsNullException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class PlayerController extends GenericController {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PlayerController.class);

    //Test Duration
    private long startTime;
    private long duration;

    //Last Sampler
    private MediaPlaylistSampler lastSampler;

    //Priority Queue
    private PriorityQueue<MediaPlaylistSampler> priorityQueue;
    private Queue<MediaPlaylistSampler> nextSamplers;

    public PlayerController() {
        super();
        setName("HLS Player");

    }

    @Override
    public void initialize() {
        log.debug("initialize");
        parser = new Parser();
        RequestInfo masterResponse = tryGetMasterList();
        
        boolean isEzdrmPostOK = getEzdrmPost();
        
        if (isEzdrmPostOK) {

            priorityQueue = new PriorityQueue<>(new MediaPlaylistSamplerComparator());
            nextSamplers = new LinkedList<>();

            for (TestElement te : subControllersAndSamplers) {
                if (te instanceof MediaPlaylistSampler) {
                    MediaPlaylistSampler mediaPlaylistSampler = (MediaPlaylistSampler) te;
                    mediaPlaylistSampler.setMasterPlaylist(masterResponse);
                    this.nextSamplers.add(mediaPlaylistSampler);
                }
            }
            startTime = -1;

            if (this.getPropertyAsBoolean(IS_CUSTOM_DURATION)) {
                duration = this.getPropertyAsLong(CUSTOM_DURATION);
            } else {
                duration = -1;
            }
            super.initialize();

        } else {
            log.error("EZDRM Post is ERROR");
        }
    }

    protected TestElement getCurrentElement() throws NextIsNullException {
        if (nextSamplers.size() > 0) {
            int sz = nextSamplers.size();
            log.debug("nextSamplers size:" + sz);
            lastSampler = nextSamplers.remove();
            return lastSampler;
        }

        if (priorityQueue.size() <= 0) {
            log.error("Priority Queue is empty");
            throw new NextIsNullException();
        }

        lastSampler = priorityQueue.remove();

        long now = System.currentTimeMillis();
        while (priorityQueue.size() > 0
                && (priorityQueue.comparator().compare(lastSampler, priorityQueue.peek()) == 0
                || priorityQueue.peek().getNextCallTimeMillis() < now)) {
            nextSamplers.add(priorityQueue.remove());
        }
        if (lastSampler.getNextCallTimeMillis() > now) {
            try {
                long sleepTime = lastSampler.getNextCallTimeMillis() - now;
                log.debug("PlayerController sleep time: " + (float) (sleepTime / 1000.0));
                Thread.sleep(sleepTime);
            } catch (InterruptedException exception) {
                log.warn("Player sleep interrupted");
                this.setDone(true);
                return null;
            }
        }

        return lastSampler;
    }

    @Override
    public Sampler next() {
        log.debug("size of subControllersAndSamplers " + subControllersAndSamplers.size());
        log.debug("duration: " + duration);
        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        }
        if (duration != -1) {
            long timeElapsed = System.currentTimeMillis() - startTime;
            if (timeElapsed > duration * 1000) {
                this.setDone(true);
                log.debug("out of time!");
                return null;
            }
        }
        if (lastSampler != null && lastSampler.getNextCallTimeMillis() != -1) {
            log.debug("adding lastSampler to priorityQueue");
            priorityQueue.add(lastSampler);
        } else {
            log.debug("lastSampler: %s, nextCallTimeMillis: %s",
                    lastSampler, lastSampler == null ? "-" : lastSampler.getNextCallTimeMillis());
        }
        Sampler returnValue = super.next();
        if (returnValue == null && !isDone()) {
            log.error("sampler was null");
        }
        return returnValue;
    }

    @Override
    protected Sampler nextIsNull() throws NextIsNullException {
        log.debug("nextIsNull");
        return super.nextIsNull();
    }

    //---------------------------Master Playlist Getting-----------------------------------//
    public static final String MASTER_PLAYLIST_URL = "MASTER_PLAYLIST_URL";
    public static final String IS_CUSTOM_DURATION = "IS_CUSTOM_DURATION";
    public static final String CUSTOM_DURATION = "CUSTOM_DURATION";

    public static final String CERT_URL = "CERT_URL";
    public static final String EZDRM_POST_URL = "EZDRM_POST_URL";

    private Parser parser;

    private RequestInfo tryGetMasterList() {
        boolean isCertOK = getCert();
        if (isCertOK) {
            try {
                SampleResult masterResult = new SampleResult();
                return getMasterList(masterResult, parser);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }            
        } else {
            log.error("Certification EZDRM is ERROR");
            return null;
        }
    }

    private byte[] readBytesFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    private boolean getEzdrmPost() {
        String fileUrl = this.getURLEzdrmPostData(); // Replace with your actual file URL
        boolean result = false;
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");            
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/octet-stream");

            // Read binary data from the file
            byte[] binaryData = new byte[0];

            // Write binary data to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                os.write(binaryData);
            }

            // Handle the response (e.g., read response code, etc.)
            int responseCode = connection.getResponseCode();
            log.info("Post EZDRM is" + String.valueOf(responseCode));

            // Close the connection
            connection.disconnect();
            result = true;
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    private boolean getCert() {
        String fileUrl = getURLCertData(); // Replace with your actual file URL
        boolean result = false;
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set up connection properties (e.g., timeouts, request method, etc.)

            // Read the file content into a byte array
            byte[] byteArray = readBytesFromStream(connection.getInputStream());

            // Now you have the file content in the byteArray
            // You can process it further as needed

            // Close the connection
            connection.disconnect();
            log.info("Certification EZDRM is OK");
            result = true;
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    private RequestInfo getMasterList(SampleResult masterResult, Parser parser) throws IOException {
        masterResult.sampleStart();
        RequestInfo response = parser.getBaseUrl(new URL(getURLData()), masterResult, true);
        masterResult.sampleEnd();

        masterResult.setRequestHeaders(response.getRequestHeaders());
        masterResult.setSuccessful(response.isSuccess());
        masterResult.setResponseMessage(response.getResponseMessage());
        masterResult.setSampleLabel("master");
        masterResult.setResponseHeaders(response.getHeadersAsString());
        masterResult.setResponseData(response.getResponse().getBytes());
        masterResult.setResponseCode(response.getResponseCode());
        masterResult.setContentType(response.getContentType());
        masterResult.setBytes(masterResult.getBytesAsLong() + (long) masterResult.getRequestHeaders().length());

        int headerBytes = masterResult.getResponseHeaders().length() // condensed
                // length
                // (without
                // \r)
                + response.getHeaders().size() // Add \r for each header
                + 1 // Add \r for initial header
                + 2; // final \r\n before data

        masterResult.setHeadersSize(headerBytes);
        masterResult.setSentBytes(response.getSentBytes());
        masterResult.setDataEncoding(response.getContentEncoding());

        return response;

    }

    public String getURLData() {
        return this.getPropertyAsString(MASTER_PLAYLIST_URL);
    }

    public String getURLEzdrmPostData() {
        return this.getPropertyAsString(EZDRM_POST_URL);
    }

    public String getURLCertData() {
        return this.getPropertyAsString(CERT_URL);
    }

    public boolean isCustomDuration() {
        return this.getPropertyAsBoolean(IS_CUSTOM_DURATION);
    }

    public String getCustomDuration() {
        return this.getPropertyAsString(CUSTOM_DURATION);
    }
}
