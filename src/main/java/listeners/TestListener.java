package listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import utilities.common_utils.report_utils;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        result.setAttribute("retryAnalyzer", RetryListener.class);
    }


    @Override
    public void onFinish(ITestContext context) {
        report_utils.flushReport();
    }
}
