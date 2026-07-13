package pages.api;


import ai.analyzer.APIFailureAnalyzer;
import constants.APIConstants;
import factories.APIClientFactory;
import io.restassured.response.Response;
import utilities.common_utils.ReportUtils;


public class GetPage {

    private Response response;

    public void createTestLog(String testName) {
        ReportUtils.createTest(testName);
        ReportUtils.info("GET API CALL: " + testName);
    }

    public void iSendGETRequest() {
        try {
            ReportUtils.info("Endpoint : " + APIConstants.BASE_URI + APIConstants.USERS);

            response = APIClientFactory.getRequestSpec(APIConstants.BASE_URI)
                    .get(APIConstants.USERS)
                    .then()
                    .extract()
                    .response();

            ReportUtils.info(response.prettyPrint());

        } catch (Exception e) {
            ReportUtils.fail("Failed to send GET request: " + e.getMessage());
            throw e;
        }
    }

    public void iReceiveValidHTTPResponseCode(int expected) {

        if (response.getStatusCode() != expected) {

            ReportUtils.addAIAnalysis(
                    APIFailureAnalyzer.analyze(response, expected)
            );
        }

        response.then().statusCode(expected);
    }
}