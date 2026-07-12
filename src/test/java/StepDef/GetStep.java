package StepDef;

import pages.api.GetPage;
import io.cucumber.java.en.*;

public class GetStep {
    GetPage getPages = new GetPage();


    @When("I send GET request")
    public void iSendGETRequest() {
        getPages.iSendGETRequest();
    }

    @Then("I receive valid HTTP response code {int}")
    public void iReceiveValidHTTPResponseCode(int arg0) {
        getPages.iReceiveValidHTTPResponseCode(arg0);
    }
}
