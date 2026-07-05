package listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import utilities.common_utils.report_utils;
import utilities.ui.ScreenshotUtils;
import DriverManager.driver;

public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        // Skip Cucumber tests - they are handled by Hooks.java
        if (result.getMethod().getMethodName().equals("runScenario")) {
            return;
        }
        // Create a test entry in the extent report for TestNG tests
        report_utils.createTest(result.getMethod().getMethodName());
        report_utils.info("Test started: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Skip Cucumber tests - they are handled by Hooks.java
        if (result.getMethod().getMethodName().equals("runScenario")) {
            return;
        }
        report_utils.pass("Test passed: " + result.getMethod().getMethodName());
        // Capture and attach screenshot on pass
        try {
            ScreenshotUtils screenshotUtils = new ScreenshotUtils(driver.getDriver());
            String screenshotPath = screenshotUtils.captureScreenshot(result.getMethod().getMethodName() + "_PASS");
            report_utils.attachScreenshot(screenshotPath);
        } catch (Exception e) {
            report_utils.info("Could not capture screenshot on pass: " + e.getMessage());
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Skip Cucumber tests - they are handled by Hooks.java
        if (result.getMethod().getMethodName().equals("runScenario")) {
            return;
        }
        report_utils.fail("Test failed: " + result.getMethod().getMethodName());
        result.setAttribute("retryAnalyzer", RetryListener.class);
        // Capture and attach screenshot on failure
        try {
            ScreenshotUtils screenshotUtils = new ScreenshotUtils(driver.getDriver());
            String screenshotPath = screenshotUtils.captureScreenshot(result.getMethod().getMethodName() + "_FAILURE");
            report_utils.attachScreenshot(screenshotPath);
        } catch (Exception e) {
            report_utils.info("Could not capture screenshot on failure: " + e.getMessage());
        }
    }


    @Override
    public void onFinish(ITestContext context) {
        report_utils.flushReport();
    }
}
