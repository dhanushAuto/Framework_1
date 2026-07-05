package Hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Parameters;
import utilities.allure.AllureAttachment;
import utilities.common_utils.report_utils;
import utilities.ui.ScreenshotUtils;
import DriverManager.driver;

public class Hooks {

    private static String browser;
    private static String environment;

    @Before
    @Parameters({"browser", "environment"})
    public void beforeScenario(Scenario scenario) {
        // Get parameters from TestNG or system properties
        browser = System.getProperty("browser", "chrome");
        environment = System.getProperty("environment", "dev").toUpperCase();
        
        // Validate environment
        if (!environment.equals("QA") && !environment.equals("UAT") && !environment.equals("PROD")) {
            environment = "DEV";
        }
        
        // Set test type based on scenario URI
        String scenarioUri = scenario.getUri().toString();
        String testType = "Unknown";
        
        if (scenarioUri.contains("API_Features")) {
            if (scenarioUri.contains("regression")) {
                testType = "API_Regression";
            } else if (scenarioUri.contains("smoke")) {
                testType = "API_Smoke";
            } else if (scenarioUri.contains("sanity")) {
                testType = "API_Sanity";
            }
        } else if (scenarioUri.contains("UI_Features")) {
            if (scenarioUri.contains("regression")) {
                testType = "UI_Regression";
            } else if (scenarioUri.contains("smoke")) {
                testType = "UI_Smoke";
            } else if (scenarioUri.contains("sanity")) {
                testType = "UI_Sanity";
            }
        }
        
        report_utils.setTestType(testType);
        report_utils.setEnvironment(environment);
        
        // Initialize report and create test for each scenario
        report_utils.getReport();
        report_utils.createTest(scenario.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {

        if (scenario.isFailed()) {
            report_utils.fail("Scenario failed: " + scenario.getName());
            // Capture and attach screenshot on failure (only for UI tests)
            try {
                WebDriver webDriver = driver.getDriver();
                if (webDriver != null) {
                    ScreenshotUtils screenshotUtils = new ScreenshotUtils(webDriver);
                    String screenshotPath = screenshotUtils.captureScreenshot(scenario.getName() + "_FAILURE");
                    report_utils.attachScreenshot(screenshotPath);
                    AllureAttachment.attachScreenshotOnFailure(webDriver, scenario.getName());
                }
            } catch (Exception e) {
                report_utils.info("Could not capture screenshot on failure: " + e.getMessage());
            }
        } else {
            report_utils.pass("Scenario passed: " + scenario.getName());
            // Capture and attach screenshot on pass (only for UI tests)
            try {
                WebDriver webDriver = driver.getDriver();
                if (webDriver != null) {
                    ScreenshotUtils screenshotUtils = new ScreenshotUtils(webDriver);
                    String screenshotPath = screenshotUtils.captureScreenshot(scenario.getName() + "_PASS");
                    report_utils.attachScreenshot(screenshotPath);
                    AllureAttachment.attachScreenshot(webDriver);
                }
            } catch (Exception e) {
                report_utils.info("Could not capture screenshot on pass: " + e.getMessage());
            }
        }

        report_utils.flushReport();
    }

}
