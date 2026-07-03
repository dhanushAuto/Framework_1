package StepDef;

import pages.api.GET_Pages;
import io.cucumber.java.en.*;

public class GET_Steps {
    GET_Pages getPages = new GET_Pages();


    @When("I send GET request")
    public void iSendGETRequest() {
        getPages.iSendGETRequest();
    }

    @Then("I receive valid HTTP response code {int}")
    public void iReceiveValidHTTPResponseCode(int arg0) {
        getPages.iReceiveValidHTTPResponseCode(arg0);
    }
}
