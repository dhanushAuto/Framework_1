package listeners;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryListener implements IRetryAnalyzer {

        private int count = 0;
        private static final int MAX_RETRY = 3;

        @Override
        public boolean retry(ITestResult result) {

            if (count < MAX_RETRY) {
                count++;
                return true;
            }

            return false;
        }
    }

