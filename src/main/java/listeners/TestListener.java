package listeners;

import ai.analyzer.FailureAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
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

        // Skip Cucumber tests
        if (result.getMethod().getMethodName().equals("runScenario")) {
            return;
        }

        try {

            if (result.getThrowable() != null) {

                FailureAnalyzer analyzer = new FailureAnalyzer();

                String aiAnalysis = analyzer.analyze(
                        result.getMethod().getMethodName(),
                        result.getThrowable(),
                        getShortStackTrace(result.getThrowable()));

                // Display AI analysis in Extent Report
                report_utils.addAIAnalysis(aiAnalysis);

                // Optional: Print in console
                System.out.println(aiAnalysis);

            }

        } catch (Exception e) {

            System.out.println("AI Analysis failed: " + e.getMessage());

        }

        // Log failure in Extent Report
        report_utils.fail(
                "Test Failed : "
                        + result.getMethod().getMethodName()
                        + "<br><br>"
                        + "<b>Exception :</b><br>"
                        + result.getThrowable());

        result.setAttribute("retryAnalyzer", RetryListener.class);

        // Screenshot
        try {

            ScreenshotUtils screenshotUtils =
                    new ScreenshotUtils(driver.getDriver());

            String screenshotPath =
                    screenshotUtils.captureScreenshot(
                            result.getMethod().getMethodName() + "_FAILURE");

            report_utils.attachScreenshot(screenshotPath);

        } catch (Exception e) {

            report_utils.info(
                    "Could not capture screenshot on failure: "
                            + e.getMessage());

        }
    }


    @Override
    public void onFinish(ITestContext context) {
        report_utils.flushReport();
    }

    private String getShortStackTrace(Throwable throwable) {

        StringBuilder trace = new StringBuilder();

        StackTraceElement[] stack = throwable.getStackTrace();

        for (int i = 0; i < Math.min(stack.length, 10); i++) {

            trace.append(stack[i]).append("\n");

        }

        return trace.toString();
    }
}