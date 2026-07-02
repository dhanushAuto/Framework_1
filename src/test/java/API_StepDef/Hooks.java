package API_StepDef;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utilities.report_utils;

public class Hooks {

    @Before
    public void beforeScenario(Scenario scenario) {
        // Initialize report and create test for each scenario
        report_utils.getReport();
        report_utils.createTest(scenario.getName());
        report_utils.info("Scenario started: " + scenario.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        // Log scenario status and flush the report to generate the HTML file
        if (scenario.isFailed()) {
            report_utils.info("Scenario failed: " + scenario.getName());
        } else {
            report_utils.info("Scenario passed: " + scenario.getName());
        }
        report_utils.flushReport();
    }
}


