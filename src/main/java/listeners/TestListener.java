package listeners;

import ai.analyzer.FailureAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utilities.common_utils.LogUtils;
import utilities.common_utils.ReportUtils;
import utilities.ui.ScreenshotUtils;
import drivermanager.Driver;

public class TestListener implements ITestListener {

    private static final String RUN_SCENARIO = "runScenario";

    @Override
    public void onTestStart(ITestResult result) {
        // Skip Cucumber tests - they are handled by Hooks.java
        if (result.getMethod().getMethodName().equals(RUN_SCENARIO)) {
            return;
        }
        // Create a test entry in the extent report for TestNG tests
        ReportUtils.createTest(result.getMethod().getMethodName());
        ReportUtils.info("Test started: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Skip Cucumber tests - they are handled by Hooks.java
        if (result.getMethod().getMethodName().equals(RUN_SCENARIO)) {
            return;
        }
        ReportUtils.pass("Test passed: " + result.getMethod().getMethodName());
        // Capture and attach screenshot on pass
        try {
            ScreenshotUtils screenshotUtils = new ScreenshotUtils(Driver.getDriver());
            String screenshotPath = screenshotUtils.captureScreenshot(result.getMethod().getMethodName() + "_PASS");
            ReportUtils.attachScreenshot(screenshotPath);
        } catch (Exception e) {
            ReportUtils.info("Could not capture screenshot on pass: " + e.getMessage());
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {

        // Skip Cucumber tests
        if (result.getMethod().getMethodName().equals(RUN_SCENARIO)) {
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
                ReportUtils.addAIAnalysis(aiAnalysis);

                // Optional: Log AI analysis
                LogUtils.info(aiAnalysis);

            }

        } catch (Exception e) {

            LogUtils.error("AI Analysis failed: " + e.getMessage());

        }

        // Log failure in Extent Report
        ReportUtils.fail(
                "Test Failed : "
                        + result.getMethod().getMethodName()
                        + "<br><br>"
                        + "<b>Exception :</b><br>"
                        + result.getThrowable());

        result.setAttribute("retryAnalyzer", RetryListener.class);

        // Screenshot
        try {

            ScreenshotUtils screenshotUtils =
                    new ScreenshotUtils(Driver.getDriver());

            String screenshotPath =
                    screenshotUtils.captureScreenshot(
                            result.getMethod().getMethodName() + "_FAILURE");

            ReportUtils.attachScreenshot(screenshotPath);

        } catch (Exception e) {

            ReportUtils.info(
                    "Could not capture screenshot on failure: "
                            + e.getMessage());

        }
    }


    @Override
    public void onFinish(ITestContext context) {
        ReportUtils.flushReport();
        ReportUtils.cleanup();
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