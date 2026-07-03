package listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {

        result.setAttribute("retryAnalyzer", RetryListener.class);

    }
}
