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
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class PlayerController extends GenericController {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PlayerController.class);

    private static String postData = "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";
 
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
        Date date = new Date();
        String fileUrl = this.getURLEzdrmPostData()+ "?p1=" + String.valueOf(date.getTime());
        boolean result = false;
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");            
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/octet-stream");

            // Read binary data from the file
            byte[] binaryData = Base64.getDecoder().decode(PlayerController.postData);

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
