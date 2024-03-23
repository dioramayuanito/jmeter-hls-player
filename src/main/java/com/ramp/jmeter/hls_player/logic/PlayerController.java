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

    // postData domain ezdrm.detik.com
    //private static String postData = "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";
 
    // postData domain live.detiknetwork.com
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
        RequestInfo masterResponse = tryGetMasterList(); // AMBIL PLAYLIST.M3U8 DULU
        
        boolean isEzdrmPostOK = getEzdrmPost(); // KIRIM EZDRM POST MESSAGE DI SINI
        
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
        boolean isCertOK = getCert(); // AMBIL CERTIFICATE EZDRM DI SINI
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
