package StepDef;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.*;
import pages.api.DELETE_Pages;

public class DELETE_Steps {
    DELETE_Pages delete = new DELETE_Pages();

    @When("send a DELETE request")
    public void Delete_Request() {
        delete.Delete_Request();
    }

    @Then("Response code should be {int}")
    public void responseCodeShouldBe(int arg0) {
        delete.verifyStatusCode(arg0);
    }
}