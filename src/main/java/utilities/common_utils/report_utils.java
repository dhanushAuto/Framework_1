package utilities.common_utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class report_utils {

    private static ExtentReports extent;
    private static ExtentTest test;

    public static ExtentReports getReport() {

        if (extent == null) {

            ExtentSparkReporter spark = new ExtentSparkReporter(
                    "reports/ExtentReport.html");

            spark.config().setDocumentTitle("Automation Report");
            spark.config().setReportName("UI & API Automation");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("Tester", "Dhanush");
            extent.setSystemInfo("Environment", "QA");
        }

        return extent;
    }

    public static void createTest(String testName) {
        // Ensure extent is initialized before creating test
        if (extent == null) {
            getReport();
        }
        test = extent.createTest(testName);
    }

    public static void info(String message) {
        if (test != null) {
            test.log(Status.INFO, message);
        }
    }

    public static void pass(String message) {
        if (test != null) {
            test.log(Status.PASS, message);
        }
    }

    public static void fail(String message) {
        if (test != null) {
            test.log(Status.FAIL, message);
        }
    }

    public static void skip(String message) {
        if (test != null) {
            test.log(Status.SKIP, message);
        }
    }

    public static void error(String message) {
        if (test != null) {
            // 'Status.ERROR' is not available in this ExtentReports version - use FAIL
            test.log(Status.FAIL, message);
        }
    }

    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }

}

