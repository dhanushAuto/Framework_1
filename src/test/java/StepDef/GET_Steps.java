package StepDef;

import pages.api.GET_Pages;
import io.cucumber.java.en.*;

public class GET_Steps {
    GET_Pages getPages = new GET_Pages();

    @Given("I set GET request with endpoint {string} and resource path {string}")
    public void iSetGETRequestWithEndpointResourcePath(String endpoint, String resource_path) {
        getPages.GET_Request_With_Endpoint(endpoint, resource_path);
    }

    @When("I send GET request")
    public void iSendGETRequest() {
        getPages.iSendGETRequest();
    }

    @Then("I receive valid HTTP response code {int}")
    public void iReceiveValidHTTPResponseCode(int arg0) {
        getPages.iReceiveValidHTTPResponseCode(arg0);
    }
}
