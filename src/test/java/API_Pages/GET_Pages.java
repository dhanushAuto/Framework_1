package API_Pages;

import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utilities.json_utils;
import utilities.report_utils;

public class GET_Pages {
    private static final String API_DATA_FILE = "src/test/resources/Payloads/api_data.json";
    private static final String GET_API_NODE = "GET_API";

    private Response response;
    private String endpoint;
    private String resourcePath;

    public void createTestLog(String testName) {
        report_utils.createTest(testName);
        report_utils.info("Test started: " + testName);
    }

    public void GET_Request_With_Endpoint(String endpoint, String resource_path) {
        this.endpoint = getApiValue(endpoint);
        this.resourcePath = getApiValue(resource_path);
    }

    public void iSendGETRequest() {
        try {
            report_utils.info("Sending GET request to: " + endpoint + this.resourcePath);
            response = RestAssured.given()
                    .baseUri(endpoint)
                    .when()
                    .get(resourcePath)
                    .then()
                    .extract()
                    .response();

            report_utils.info("GET request completed successfully. Response status: " + response.prettyPrint());
        } catch (Exception e) {
            report_utils.fail("Failed to send GET request: " + e.getMessage());
            throw e;
        }
    }

    public void iReceiveValidHTTPResponseCode(int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        if (actualStatusCode == expectedStatusCode) {
            report_utils.pass("Status code validation passed. Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
        } else {
            report_utils.fail("Status code validation failed. Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
            throw new AssertionError("Expected status code: " + expectedStatusCode + " but got: " + actualStatusCode);
        }
    }

    private String getApiValue(String valueOrKey) {
        if (valueOrKey == null || valueOrKey.isBlank()) {
            throw new IllegalArgumentException("API value cannot be null or blank");
        }

        if (valueOrKey.startsWith("http") || valueOrKey.startsWith("/")) {
            return valueOrKey;
        }

        JsonNode apiData = json_utils.readJson(API_DATA_FILE).path(GET_API_NODE);
        String key = "baseURI".equals(valueOrKey) ? "endpoint" : valueOrKey;
        JsonNode value = apiData.path(key);

        if (value.isMissingNode() || value.asText().isBlank()) {
            throw new IllegalArgumentException("No API data found for key: " + valueOrKey);
        }

        return value.asText();
    }
}
