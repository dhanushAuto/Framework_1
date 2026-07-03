package listeners;

import org.testng.ISuite;
import org.testng.ISuiteListener;

    public class SuiteListener implements ISuiteListener {

        @Override
        public void onStart(ISuite suite) {

            System.out.println("==================================");
            System.out.println("Suite Started : " + suite.getName());
            System.out.println("==================================");

        }

        @Override
        public void onFinish(ISuite suite) {

            System.out.println("==================================");
            System.out.println("Suite Finished : " + suite.getName());
            System.out.println("==================================");

        }
    }

