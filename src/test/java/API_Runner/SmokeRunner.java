package API_Runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/API_Features/GET",
    glue = "API_StepDef",
    plugin = {"json:target/cucumber.json", "html:target/cucumber.html"}
)
public class SmokeRunner extends AbstractTestNGCucumberTests {
}
