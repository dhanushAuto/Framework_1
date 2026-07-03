package StepDef;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.*;
import pages.api.POST_Pages;

public class POST_Steps {
    POST_Pages postPages = new POST_Pages();

    @When("I set POST request with body {string}")
        public void iSetPOSTRequestWithBody(String bodyFile) {
        postPages.setBody(bodyFile);
    }

    @Then("I send the POST request")
        public void iSendThePOSTRequest() {
        postPages.sendPOSTRequest();
    }

    @Then("I receive valid response code {int}")
    public void iReceiveValidHTTPResponseCode(int arg0) {
        postPages.iReceiveValidHTTPResponseCode(arg0);
    }
}



