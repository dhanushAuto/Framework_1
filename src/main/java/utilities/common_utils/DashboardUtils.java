package utilities.common_utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class DashboardUtils {

    public static void generatePerformanceDashboard(Map<String, Long> executionTimes, int passCount, int failCount, int flakyCount) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Performance Dashboard</title>");
        html.append("<style>body{font-family:Arial; background:#121212; color:white;} .card{background:#1e1e1e; padding:20px; margin:10px; border-radius:10px; border-left:5px solid #00bcd4;}</style>");
        html.append("</head><body>");
        html.append("<h1>🚀 Performance & Execution Dashboard</h1>");
        
        html.append("<div class='card'><h2>Summary</h2>");
        html.append("<p>Total Tests: ").append(passCount + failCount).append("</p>");
        html.append("<p style='color:#4caf50;'>Pass: ").append(passCount).append("</p>");
        html.append("<p style='color:#f44336;'>Fail: ").append(failCount).append("</p>");
        html.append("<p style='color:#ff9800;'>Flaky: ").append(flakyCount).append("</p>");
        html.append("</div>");

        html.append("<div class='card'><h2>Execution Times</h2><table>");
        for (Map.Entry<String, Long> entry : executionTimes.entrySet()) {
            html.append("<tr><td>").append(entry.getKey()).append("</td><td>").append(entry.getValue()).append(" ms</td></tr>");
        }
        html.append("</table></div>");

        html.append("</body></html>");

        try (FileWriter writer = new FileWriter("reports/dashboard.html")) {
            writer.write(html.toString());
            LogUtils.info("Dashboard generated at reports/dashboard.html");
        } catch (IOException e) {
            LogUtils.error("Failed to generate dashboard: " + e.getMessage());
        }
    }
}
