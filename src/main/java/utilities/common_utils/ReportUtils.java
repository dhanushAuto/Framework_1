package utilities.common_utils;

import ai.model.SonarFix;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import utilities.ui.BrowserUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportUtils {

    private static final String UNKNOWN = "unknown";
    private static final String EMPTY_STRING = "";
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
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(LocalDateTime.now(ZoneId.systemDefault()));
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
        if (extent == null) getReport();
        test.set(extent.createTest(testName));
    }

    public static void info(String message) {
        if (test.get() != null) test.get().log(Status.INFO, message);
    }

    public static void pass(String message) {
        if (test.get() != null) test.get().log(Status.PASS, message);
    }

    public static void fail(String message) {
        if (test.get() != null) test.get().log(Status.FAIL, message);
    }

    public static void skip(String message) {
        if (test.get() != null) test.get().log(Status.SKIP, message);
    }

    public static void error(String message) {
        if (test.get() != null) test.get().log(Status.FAIL, "ERROR: " + message);
    }

    public static void attachScreenshot(String screenshotPath) {
        if (test.get() != null && screenshotPath != null) {
            try {
                test.get().addScreenCaptureFromPath(screenshotPath);
            } catch (Exception e) {
                LogUtils.error("Failed to attach screenshot: " + e.getMessage());
            }
        }
    }

    public static void addAIAnalysis(String aiAnalysis) {
        if (test.get() != null) {
            test.get().info("<div style='background:#1E1E1E; border-left:5px solid #00BCD4; padding:10px; border-radius:5px;'>" +
                "<h3>🤖 AI Failure Analysis</h3><pre>" + aiAnalysis + "</pre></div>");
        }
    }

    public static void addSonarAIAnalysis(List<SonarFix> fixes) {
        if (test.get() != null && fixes != null && !fixes.isEmpty()) {
            String tableHtml = buildSonarAnalysisTable(fixes);
            test.get().info(tableHtml);
        }
    }

    private static String buildSonarAnalysisTable(List<SonarFix> fixes) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='background:#1E1E1E; border-left:5px solid #00BCD4; padding:10px; border-radius:5px;'>");
        sb.append(EMPTY_STRING).append("<h3>🤖 Sonar AI Analysis</h3>");
        sb.append(EMPTY_STRING).append("<table style='width:100%; border-collapse:collapse; color:#E0E0E0;'>");
        sb.append(EMPTY_STRING).append("<tr style='background:#2D2D2D;'><th style='padding:8px; text-align:left;'>File</th><th style='padding:8px; text-align:left;'>Issue</th><th style='padding:8px; text-align:left;'>Status</th><th style='padding:8px; text-align:left;'>Confidence</th><th style='padding:8px; text-align:left;'>Risk</th></tr>");

        for (SonarFix fix : fixes) {
            appendFixRow(sb, fix);
        }

        sb.append(EMPTY_STRING).append("</table>");
        sb.append(EMPTY_STRING).append("</div>");
        return sb.toString();
    }

    private static void appendFixRow(StringBuilder sb, SonarFix fix) {
        String file = fix.getIssue() != null ? fix.getIssue().getComponent() : "Unknown";
        String message = fix.getIssue() != null ? fix.getIssue().getMessage() : "Unknown";
        String status = fix.isFixed() ? "<span style='color:#4CAF50;'>✓ Fixed</span>" : "<span style='color:#F44336;'>✗ Not Fixed</span>";
        int confidence = fix.getConfidence();
        String risk = fix.getRiskRating();
        String riskColor = getRiskColor(risk);

        sb.append(EMPTY_STRING).append("<tr style='border-bottom:1px solid #3D3D3D;'>");
        sb.append(EMPTY_STRING).append("<td style='padding:8px;'>").append(file).append("</td>");
        sb.append(EMPTY_STRING).append("<td style='padding:8px;'>").append(message).append("</td>");
        sb.append(EMPTY_STRING).append("<td style='padding:8px;'>").append(status).append("</td>");
        sb.append(EMPTY_STRING).append("<td style='padding:8px;'>").append(confidence).append("%</td>");
        sb.append(EMPTY_STRING).append("<td style='padding:8px; color:").append(riskColor).append(";'>").append(risk).append("</td>");
        sb.append(EMPTY_STRING).append("</tr>");
    }

    public static void flushReport() {
        if (extent != null) extent.flush();
        test.remove();
    }

    private static String getRiskColor(String risk) {
        if (risk == null) {
            return "#4CAF50";
        }
        if (risk.equalsIgnoreCase("High")) {
            return "#F44336";
        }
        if (risk.equalsIgnoreCase("Medium")) {
            return "#FF9800";
        }
        return "#4CAF50";
    }
}
