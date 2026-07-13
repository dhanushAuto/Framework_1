package pages.api;


import ai.analyzer.APIFailureAnalyzer;
import constants.APIConstants;
import factories.APIClientFactory;
import io.restassured.response.Response;
import utilities.common_utils.JsonUtils;
import utilities.common_utils.ReportUtils;
import org.json.JSONObject;

public class PostPage {

    private String payload;
    private Response response;


    public void setBody(String bodyFile) {

        payload = JsonUtils.getPayload(bodyFile);

        ReportUtils.info("Payload loaded");
    }

    public void sendPOSTRequest() {
        JSONObject jsonObject = new JSONObject(payload);
        ReportUtils.info("Endpoint : " + APIConstants.BASE_URI + APIConstants.CREATE_POST);

        for (int i = 0; i <= 1; i++) {
            jsonObject.getJSONObject("POST_API").put("userId", i);
            jsonObject.getJSONObject("POST_API").put("id", i);

            response = APIClientFactory
                    .getRequestSpec(APIConstants.BASE_URI)
                    .body(jsonObject.toString())
                    .post(APIConstants.CREATE_POST);

            ReportUtils.info("Post " + i + " sent successfully");
            ReportUtils.info(response.prettyPrint());


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