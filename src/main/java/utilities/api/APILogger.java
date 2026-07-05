package utilities.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utilities.allure.AllureAttachment;
import utilities.common_utils.log_utils;
import utilities.common_utils.report_utils;

import java.util.Map;

public class APILogger {

    private static long startTime;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Logs the complete API request details including method, URL, headers, and payload
     * @param requestSpec RequestSpecification containing the request details
     * @param method HTTP method (GET, POST, PUT, DELETE, etc.)
     * @param endpoint API endpoint URL
     */
    public static void logRequest(RequestSpecification requestSpec, String method, String endpoint) {
        startTime = System.currentTimeMillis();
        
        String requestDetails = "Method: " + method + "\nEndpoint: " + endpoint;
        
        report_utils.info("API Request");
        report_utils.info("Method: " + method);
        report_utils.info("Endpoint: " + endpoint);
        
        AllureAttachment.attachApiRequest(requestDetails);
        log_utils.info("API Request - " + method + " " + endpoint);
        
        // Note: Headers cannot be retrieved from RequestSpecification
        // Log headers separately using logRequestHeaders(Map<String, String> headers) if needed
    }

    /**
     * Logs the request payload/body
     * @param payload Request body as string
     */
    public static void logPayload(String payload) {
        if (payload != null && !payload.isEmpty()) {
            String formattedJson = formatJson(payload);
            report_utils.info("<pre>" + formattedJson + "</pre>");
            AllureAttachment.attachApiPayload(formattedJson);
            log_utils.info("Request Payload: " + payload);
        } else {
            report_utils.info("Payload: No payload present");
        }
    }

    /**
     * Logs the API response including status code, response body, and execution time
     * @param response Rest Assured Response object
     */
    public static void logResponse(Response response) {
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();
        
        report_utils.info("Status Code: " + statusCode);
        String formattedJson = formatJson(responseBody);
        report_utils.info("<pre>" + formattedJson + "</pre>");
        report_utils.info("Execution Time: " + executionTime + " ms");
        
        String responseDetails = "Status Code: " + statusCode + "\nExecution Time: " + executionTime + " ms\nResponse:\n" + formattedJson;
        AllureAttachment.attachApiResponse(responseDetails);
        
        log_utils.info("API Response - Status: " + statusCode + ", Time: " + executionTime + "ms");
        
        // Log pass/fail based on status code
        if (statusCode >= 200 && statusCode < 300) {
            report_utils.pass("API call successful - Status: " + statusCode);
        } else {
            report_utils.fail("API call failed - Status: " + statusCode);
        }
    }

    /**
     * Formats JSON string for better readability in reports
     * @param json JSON string to format
     * @return Formatted JSON string
     */
    private static String formatJson(String json) {
        if (json == null || json.isEmpty()) {
            return "";
        }
        
        try {
            // Use Gson for proper JSON pretty-printing
            Object jsonObject = gson.fromJson(json, Object.class);
            return gson.toJson(jsonObject);
        } catch (Exception e) {
            log_utils.error("Failed to format JSON: " + e.getMessage());
            return json;
        }
    }

    /**
     * Logs response headers
     * @param response Rest Assured Response object
     */
    public static void logResponseHeaders(Response response) {
        try {
            io.restassured.http.Headers headers = response.getHeaders();
            if (headers != null) {
                StringBuilder headerLog = new StringBuilder("Response Headers:\n");
                for (io.restassured.http.Header header : headers) {
                    headerLog.append("  ").append(header.getName()).append(": ").append(header.getValue()).append("\n");
                }
                report_utils.info(headerLog.toString().trim());
                log_utils.info("Response Headers: " + headers);
            }
        } catch (Exception e) {
            log_utils.error("Failed to log response headers: " + e.getMessage());
        }
    }
}
