package pages.api;

import com.fasterxml.jackson.databind.JsonNode;
import constants.APIConstants;
import factories.APIClientFactory;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utilities.common_utils.json_utils;
import utilities.common_utils.report_utils;

import static factories.APIClientFactory.APIRequest;

public class GET_Pages {
    private static final String API_DATA_FILE = "src/test/resources/Payloads/api_data.json";
    private static final String GET_API_NODE = "GET_API";

    private Response response;

    public void createTestLog(String testName) {
        report_utils.createTest(testName);
        report_utils.info("GET API CALL: " + testName);
    }

    public void iSendGETRequest() {
        try {
            report_utils.info("Endpoint : " + APIConstants.BASE_URI + APIConstants.USERS);

            response = APIClientFactory.APIRequest(APIConstants.BASE_URI)
                    .get(APIConstants.USERS)
                    .then()
                    .extract()
                    .response();

            report_utils.info(response.prettyPrint());
        } catch (Exception e) {
            report_utils.fail("Failed to send GET request: " + e.getMessage());
            throw e;
        }
    }

    public void iReceiveValidHTTPResponseCode(int expectedStatusCode) {

        response.then().statusCode(expectedStatusCode);
    }
}