package pages.api;

import constants.APIConstants;
import factories.APIClientFactory;
import io.restassured.response.Response;
import org.json.JSONObject;
import utilities.common_utils.report_utils;

public class DELETE_Pages {
    private Response response;
    public static final String DELETE_POST = "/posts/{id}";
    public void Delete_Request() {
        report_utils.info("Endpoint : " + APIConstants.BASE_URI + DELETE_POST);

        for (int i = 0; i <= 5; i++) {
            response = APIClientFactory.APIRequest(APIConstants.BASE_URI)
                    .pathParam("id", i)
                    .when().delete(DELETE_POST);
            report_utils.info("Delete " + i + " sent successfully");
            report_utils.info(response.prettyPrint());
        }
    }
    public void verifyStatusCode(int arg0) {
        response.then().statusCode(arg0);

    }
}
