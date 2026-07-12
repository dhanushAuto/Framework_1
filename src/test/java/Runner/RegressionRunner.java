package Runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = {
                "src/test/resources/API_Features/regression/POST/POST.feature",
               // "src/test/resources/API_Features/regression/PUT/PUT.feature",
               // "src/test/resources/API_Features/regression/PATCH/PATCH.feature",
                "src/test/resources/API_Features/regression/GET/GET.feature",
                "src/test/resources/API_Features/regression/DELETE/DELETE.feature"
        },
        glue = {"StepDef", "Hooks"},
        tags = "@Regression",
        plugin = {
                "pretty",
                "html:target/cucumber.html",
                "json:target/cucumber.json"
        }
)
public class RegressionRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}