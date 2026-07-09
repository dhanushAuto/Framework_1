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
import utilities.api.config_utils;

import java.util.concurrent.TimeUnit;

public class AIClient {

    private static final Logger logger = LogManager.getLogger(AIClient.class);
    
    private final ObjectMapper mapper = new ObjectMapper();
    private final CloseableHttpClient httpClient;
    private final int maxRetries;
    private final long retryDelayMs;
    private final long timeoutMs;

    public AIClient() {
        this.maxRetries = Integer.parseInt(config_utils.getProperty("ai.retry", "3"));
        this.retryDelayMs = 1000;
        this.timeoutMs = Long.parseLong(config_utils.getProperty("ai.timeout", "60000"));
        
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(20);
        connectionManager.setDefaultMaxPerRoute(10);
        
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(30000))
                .setResponseTimeout(Timeout.ofMilliseconds(this.timeoutMs))
                .build();
        
        this.httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config)
                .build();
        
        logger.info("AIClient initialized with maxRetries={}, timeout={}ms", maxRetries, timeoutMs);
    }

    public String askAI(String prompt) throws Exception {
        return askAIWithRetry(prompt, maxRetries);
    }

    private String askAIWithRetry(String prompt, int retriesLeft) throws Exception {
        if (retriesLeft <= 0) {
            throw new RuntimeException("Max retries exceeded for AI request");
        }

        OllamaRequest request = new OllamaRequest();
        request.setModel(config_utils.getProperty("ai.model"));
        request.setPrompt(prompt);
        request.setStream(false);
        request.setThink(false);

        String json = mapper.writeValueAsString(request);
        HttpPost post = new HttpPost(config_utils.getProperty("ai.url"));
        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try {
            logger.debug("Sending AI request (retries left: {})", retriesLeft);
            
            String body = httpClient.execute(post, httpResponse -> {
                int statusCode = httpResponse.getCode();
                String responseBody = new String(
                    httpResponse.getEntity().getContent().readAllBytes());

                if (statusCode != 200) {
                    logger.warn("AI request failed with status {}: {}", statusCode, responseBody);
                    throw new RuntimeException("Ollama Error: " + statusCode + "\n" + responseBody);
                }

                logger.debug("AI request successful");
                return responseBody;
            });

            OllamaResponse aiResponse = mapper.readValue(body, OllamaResponse.class);
            return aiResponse.getResponse();

        } catch (Exception e) {
            logger.warn("AI request failed: {}. Retrying... ({})", e.getMessage(), retriesLeft - 1);
            
            if (retriesLeft > 1) {
                try {
                    TimeUnit.MILLISECONDS.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
                return askAIWithRetry(prompt, retriesLeft - 1);
            }
            
            throw new RuntimeException("AI request failed after " + maxRetries + " retries: " + e.getMessage(), e);
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
