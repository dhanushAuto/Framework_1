package utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class report_utils {

    private static ExtentReports extent;

    public static ExtentReports getReport() {

        if (extent == null) {

            ExtentSparkReporter spark = new ExtentSparkReporter(
                    "test-output/ExtentReport.html");

            spark.config().setDocumentTitle("Automation Report");
            spark.config().setReportName("UI & API Automation");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("Tester", "Dhanush");
            extent.setSystemInfo("Environment", "QA");
        }

        return extent;
    }
}

