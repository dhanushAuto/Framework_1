package Runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/resources/API_Features/regression/GET",
        glue = "StepDef",
        plugin = {
                "pretty",
                "html:target/cucumber.html",
                "json:target/cucumber.json"
        }
)
public class regression_Runner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}