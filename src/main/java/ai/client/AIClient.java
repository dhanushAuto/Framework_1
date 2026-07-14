package ai.client;

import ai.model.OllamaRequest;
import ai.model.OllamaResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.util.Timeout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.ConfigUtils;

import java.util.concurrent.TimeUnit;

public class AIClient {

    private static final Logger logger = LogManager.getLogger(AIClient.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final CloseableHttpClient httpClient;
    private final int maxRetries;
    private final long retryDelayMs;
    private final long timeoutMs;
    private final String aiUrl;

    public AIClient() {
        this.maxRetries = Integer.parseInt(ConfigUtils.getProperty("ai.retry", "3"));
        this.retryDelayMs = 1000;
        this.timeoutMs = Long.parseLong(ConfigUtils.getProperty("ai.timeout", "300000"));
        this.aiUrl = ConfigUtils.getProperty("ai.url");

        if (aiUrl == null) {
            logger.error("ai.url property is missing! AI requests will fail.");
        } else if (!aiUrl.contains("/api/generate")) {
            logger.warn("ai.url does not look like a full Ollama generate endpoint: '{}'. " +
                    "Expected something like http://localhost:11434/api/generate", aiUrl);
        }

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(20);
        connectionManager.setDefaultMaxPerRoute(10);

        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(30000))
                // Fail fast if the host/port isn't reachable at all, instead of hanging
                // for the full response timeout. This is the most common cause of a
                // call that appears "stuck" right after httpClient.execute(...).
                .setResponseTimeout(Timeout.ofMilliseconds(this.timeoutMs))
                .build();

        this.httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config)
                .evictExpiredConnections()
                .evictIdleConnections(Timeout.ofMinutes(1))
                .disableAutomaticRetries() // We handle retries manually in generateWithRetry
                .build();

        checkServiceAvailability();
        logger.info("AIClient initialized with maxRetries={}, timeout={}ms, url={}", maxRetries, timeoutMs, aiUrl);
    }

    private void checkServiceAvailability() {
        if (aiUrl == null) return;
        
        // Quick check to see if the port is open
        try {
            java.net.URL url = new java.net.URL(aiUrl);
            String host = url.getHost();
            int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
            
            try (java.net.Socket socket = new java.net.Socket()) {
                socket.connect(new java.net.InetSocketAddress(host, port), 2000);
                logger.info("AI service is available at {}:{}", host, port);
            }
        } catch (Exception e) {
            logger.error("AI service is NOT reachable at {}. AI features will be disabled or fail. " +
                         "Please ensure Ollama is running.", aiUrl);
        }
    }

    /** Convenience wrapper returning just the response text. */
    public String generateResponse(String prompt) {
        return generateFullResponse(prompt).getResponse();
    }

    public OllamaResponse generateFullResponse(String prompt) {
        try {
            return generateWithRetry(prompt, maxRetries);
        } catch (java.net.ConnectException e) {
            logger.error("AI service is unreachable at {}. Please ensure Ollama is running and accessible. Error: {}", aiUrl, e.getMessage());
            OllamaResponse errorResponse = new OllamaResponse();
            errorResponse.setResponse("AI Analysis Failed: AI service unreachable. " + e.getMessage());
            return errorResponse;
        } catch (java.net.SocketTimeoutException e) {
            logger.error("AI request timed out after {}ms for URL: {}. The prompt might be too large or the model is slow.", timeoutMs, aiUrl);
            OllamaResponse errorResponse = new OllamaResponse();
            errorResponse.setResponse("AI Analysis Failed: Request timed out. " + e.getMessage());
            return errorResponse;
        } catch (Exception e) {
            // Log the full exception (not just getMessage()) so the real stack trace
            // isn't lost when something other than AIRequestException/AIException occurs.
            logger.error("AI request failed", e);
            OllamaResponse errorResponse = new OllamaResponse();
            errorResponse.setResponse("AI Analysis Failed: " + e.getMessage());
            return errorResponse;
        }
    }

    public String generateText(String prompt) {
        return generateFullResponse(prompt).getResponse();
    }

    private OllamaResponse generateWithRetry(String prompt, int retriesLeft) throws Exception {
        if (retriesLeft <= 0) {
            throw new AIException("Max retries exceeded for AI request");
        }

        OllamaRequest request = new OllamaRequest();
        request.setModel(ConfigUtils.getProperty("ai.model"));
        request.setPrompt(prompt);
        request.setStream(false);
        // Disable "thinking" mode: reasoning models (qwen3, etc.) otherwise emit a
        // full internal reasoning block before the actual answer, which roughly
        // doubles/triples generation time on CPU-bound Ollama installs and was
        // the main cause of AI requests timing out during Sonar auto-fix runs.
        request.setThink(false);

        // Increase response length for batch file fixes
        OllamaRequest.Options options = new OllamaRequest.Options();
        options.setNum_predict(4000); 
        request.setOptions(options);

        String json = mapper.writeValueAsString(request);
        logger.debug("Ollama request payload: {}", json);

        HttpPost post = new HttpPost(aiUrl);
        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try {
            logger.debug("Sending AI request to {} (retries left: {})", aiUrl, retriesLeft);

            String body = httpClient.execute(post, httpResponse -> {
                int statusCode = httpResponse.getCode();
                
                // Using a buffered input stream or similar might be better for very large responses,
                // but for non-streaming it should be fine.
                // We ensure we don't hang by checking if the entity is actually there.
                if (httpResponse.getEntity() == null) {
                    throw new AIRequestException("Empty response from AI service (status " + statusCode + ")");
                }

                byte[] responseBytes = httpResponse.getEntity().getContent().readAllBytes();
                String responseBody = new String(responseBytes, java.nio.charset.StandardCharsets.UTF_8);

                logger.debug("Ollama raw response ({}): {}", statusCode, responseBody);

                if (statusCode != 200) {
                    logger.warn("AI request failed with status {}: {}", statusCode, responseBody);
                    throw new AIRequestException("Ollama Error: " + statusCode + "\n" + responseBody);
                }

                return responseBody;
            });

            return mapper.readValue(body, OllamaResponse.class);

        } catch (java.net.ConnectException | java.net.SocketTimeoutException e) {
            // Re-throw these so generateFullResponse can handle them specially
            throw e;
        } catch (Exception e) {
            logger.warn("AI request failed: {}. Retrying... ({} left)", e.getMessage(), retriesLeft - 1);

            if (retriesLeft > 1) {
                try {
                    TimeUnit.MILLISECONDS.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new AIException("Retry interrupted", ie);
                }
                return generateWithRetry(prompt, retriesLeft - 1);
            }

            throw new AIException("AI request failed after " + maxRetries + " retries: " + e.getMessage(), e);
        }
    }

    public static class AIException extends Exception {
        public AIException(String message) {
            super(message);
        }
        public AIException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class AIRequestException extends RuntimeException {
        public AIRequestException(String message) {
            super(message);
        }
    }

    public void shutdown() {
        try {
            if (httpClient != null) {
                httpClient.close();
                logger.info("AIClient shutdown complete");
            }
        } catch (Exception e) {
            logger.error("Error shutting down AIClient", e);
        }
    }
}