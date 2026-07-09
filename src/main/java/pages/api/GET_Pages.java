package pages.api;


import ai.analyzer.APIFailureAnalyzer;
import constants.APIConstants;
import factories.APIClientFactory;
import io.restassured.response.Response;
import utilities.common_utils.report_utils;


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

    public void iReceiveValidHTTPResponseCode(int expected) {

        if (response.getStatusCode() != expected) {

            report_utils.addAIAnalysis(
                    APIFailureAnalyzer.analyze(response, expected)
            );
        }

        response.then().statusCode(expected);
    }
}