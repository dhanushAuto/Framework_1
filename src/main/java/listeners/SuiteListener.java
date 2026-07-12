package listeners;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import utilities.common_utils.LogUtils;

public class SuiteListener implements ISuiteListener {

    private static final String SEPARATOR = "==================================";

        @Override
        public void onStart(ISuite suite) {
            LogUtils.info(SEPARATOR);
            LogUtils.info("Suite Started : " + suite.getName());
            LogUtils.info(SEPARATOR);
        }

        @Override
        public void onFinish(ISuite suite) {
            LogUtils.info(SEPARATOR);
            LogUtils.info("Suite Finished : " + suite.getName());
            LogUtils.info(SEPARATOR);
        }
    }
