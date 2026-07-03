package pages.api;

import constants.APIConstants;
import factories.APIClientFactory;
import io.restassured.response.Response;
import utilities.common_utils.json_utils;
import utilities.common_utils.report_utils;

public class POST_Pages {

    private String payload;
    private Response response;

    public void setBody(String bodyFile) {

        payload = json_utils.getPayload(bodyFile);

        report_utils.info("Payload loaded");
    }

    public void sendPOSTRequest() {
        report_utils.info("Endpoint : " + APIConstants.BASE_URI + APIConstants.CREATE_POST);

        response = APIClientFactory
                .APIRequest(APIConstants.BASE_URI)
                .body(payload)
                .post(APIConstants.CREATE_POST);

        report_utils.info(response.prettyPrint());

    }

    public void iReceiveValidHTTPResponseCode(int expected) {

        response.then().statusCode(expected);
    }
}