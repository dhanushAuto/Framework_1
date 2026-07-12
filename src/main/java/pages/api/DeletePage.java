package pages.api;

import ai.analyzer.APIFailureAnalyzer;
import constants.APIConstants;
import factories.APIClientFactory;
import io.restassured.response.Response;
import utilities.common_utils.ReportUtils;

public class DeletePage {
    private Response response;

    public static final String DELETE_POST = "/posts/{id}";
    public void deleteRequest() {
        ReportUtils.info("Endpoint : " + APIConstants.BASE_URI + DELETE_POST);

        for (int i = 0; i <= 1; i++) {
            response = APIClientFactory.APIRequest(APIConstants.BASE_URI)
                    .pathParam("id", i)
                    .when().delete(DELETE_POST);
            ReportUtils.info("Delete " + i + " sent successfully");
            ReportUtils.info(response.prettyPrint());

        }
    }
    public void verifyStatusCode(int arg0) {
        response.then().statusCode(arg0);

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
