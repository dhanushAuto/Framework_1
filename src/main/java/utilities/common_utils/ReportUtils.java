package utilities.common_utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import utilities.ui.BrowserUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ReportUtils {

    private static final String UNKNOWN = "unknown";
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static String testType;
    private static String environment;
    
    private ReportUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void setTestType(String type) {
        testType = type;
    }

    public static void setEnvironment(String env) {
        environment = env;
    }

    public static ExtentReports getReport() {

        if (extent == null) {
            // Generate timestamp for filename
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(LocalDateTime.now(ZoneId.systemDefault()));
            
            // Format report name: env_testType_date_time
            String reportFileName = String.format("reports/%s_%s_%s.html", 
                environment != null ? environment : UNKNOWN,
                testType != null ? testType : UNKNOWN,
                timestamp);

            ExtentSparkReporter spark = new ExtentSparkReporter(reportFileName);

            spark.config().setDocumentTitle("Automation Test Report");
            spark.config().setReportName("UI & API Automation");
            spark.config().setEncoding("utf-8");
            spark.config().setTheme(Theme.DARK);
            spark.config().setTimeStampFormat("dd/MM/yyyy hh:mm:ss a");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("Tester", "Dhanush");
            extent.setSystemInfo("Environment", environment != null ? environment : UNKNOWN);
            extent.setSystemInfo("Test Type", testType != null ? testType : UNKNOWN);
            extent.setSystemInfo("Browser", BrowserUtils.getBrowser());
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        }

        return extent;
    }

    public static void createTest(String testName) {
        // Ensure extent is initialized before creating test
        if (extent == null) {
            getReport();
        }
        test.set(extent.createTest(testName));
    }

    public static void info(String message) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.log(Status.INFO, message);
        }
    }

    public static void pass(String message) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.log(Status.PASS, message);
        }
    }

    public static void fail(String message) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.log(Status.FAIL, message);
        }
    }

    public static void skip(String message) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.log(Status.SKIP, message);
        }
    }

    public static void error(String message) {
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            // 'Status.ERROR' is not available in this ExtentReports version - use FAIL
            extentTest.log(Status.FAIL, "ERROR: " + message);
        }
    }

    public static void attachScreenshot(String screenshotPath) {
        ExtentTest extentTest = test.get();
        if (extentTest != null && screenshotPath != null) {
            try {
                extentTest.addScreenCaptureFromPath(screenshotPath);
                LogUtils.info("Screenshot attached to report: " + screenshotPath);
            } catch (Exception e) {
                LogUtils.error("Failed to attach screenshot: " + e.getMessage());
            }
        }
    }
    public static void addAIAnalysis(String aiAnalysis) {

        ExtentTest extentTest = test.get();

        if (extentTest != null) {

            extentTest.info("""
                <div style='background:#1E1E1E;
                            border-left:5px solid #00BCD4;
                            padding:10px;
                            border-radius:5px;'>

                <h3>🤖 AI Failure Analysis</h3>

                <pre>
                """ + aiAnalysis + """
                </pre>

                </div>
                """);

        }

    }
    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }

    public static void cleanup() {
        test.remove();
    }

}

