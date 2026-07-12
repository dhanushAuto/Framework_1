package StepDef;

import io.cucumber.java.en.*;
import pages.api.DeletePage;

public class DeleteStep {
    DeletePage delete = new DeletePage();

    @When("send a DELETE request")
    public void deleteRequestStep() {
        delete.deleteRequest();
    }

    @Then("Response code should be {int}")
    public void responseCodeShouldBe(int arg0) {
        delete.verifyStatusCode(arg0);
    }
}