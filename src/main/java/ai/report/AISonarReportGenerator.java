package ai.report;

import ai.model.AISonarResult;
import ai.model.AISonarSummary;
import utilities.common_utils.LogUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Generates the standalone {@code reports/AISonarReport.html} artifact
 * summarising one AI Sonar Auto-Fix pipeline run: execution time, counts of
 * fixed/failed/skipped issues, remaining issues after the re-scan, and a
 * per-issue breakdown table.
 * <p>
 * This is independent of {@code utilities.common_utils.ReportUtils}
 * (the ExtentReports-based test report) so the AI Sonar report can be
 * consumed on its own by Jenkins/CI without needing the full test report.
 */
public final class AISonarReportGenerator {

    private static final String DEFAULT_REPORT_PATH = "reports/AISonarReport.html";
    private static final String NEUTRAL = "neutral";
    private static final String EMPTY_STRING = "";

    private AISonarReportGenerator() {
    }

    public static String generate(AISonarSummary summary, List<AISonarResult> results) {
        return generate(summary, results, DEFAULT_REPORT_PATH);
    }

    public static String generate(AISonarSummary summary, List<AISonarResult> results, String outputPath) {
        String html = buildHtml(summary, results);

        try {
            Path path = Paths.get(outputPath);
            if (path.getParent() != null && !Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, html);
            LogUtils.info("AI Sonar report written to: " + path.toAbsolutePath());
            return path.toAbsolutePath().toString();
        } catch (IOException e) {
            LogUtils.error("Failed to write AI Sonar report: " + e.getMessage());
            return null;
        }
    }

    private static String buildHtml(AISonarSummary summary, List<AISonarResult> results) {
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>")
          .append("<title>AI Sonar Auto-Fix Report</title>")
          .append("<style>")
          .append(css())
          .append("</style></head><body>");

        sb.append("<div class='container'>");
        sb.append("<h1>🤖 AI Sonar Auto-Fix Report</h1>");
        sb.append("<p class='timestamp'>Generated: ").append(escape(summary.getGeneratedAt())).append("</p>");

        String readyBadge = summary.isReadyForJenkins()
                ? "<span class='badge ready'>✔ Ready for Jenkins</span>"
                : "<span class='badge blocked'>✘ Not Ready - Remaining Issues Found</span>";
        sb.append("<div class='status'>").append(readyBadge).append("</div>");

        sb.append("<div class='cards'>");
        sb.append(card("Total Issues Found", String.valueOf(summary.getTotalIssuesFound()), NEUTRAL));
        sb.append(card("Safe Issues Selected", String.valueOf(summary.getSafeIssuesSelected()), NEUTRAL));
        sb.append(card("Fixed", String.valueOf(summary.getFixedCount()), "good"));
        sb.append(card("Failed", String.valueOf(summary.getFailedCount()), "bad"));
        sb.append(card("Skipped (Unsafe)", String.valueOf(summary.getSkippedUnsafeCount()), "warn"));
        sb.append(card("Skipped (Cached)", String.valueOf(summary.getSkippedCachedCount()), NEUTRAL));
        sb.append(card("Rolled Back", String.valueOf(summary.getRolledBackCount()), "warn"));
        sb.append(card("Compile Result", summary.isCompileSuccess() ? "SUCCESS" : "FAILED",
                summary.isCompileSuccess() ? "good" : "bad"));
        sb.append(card("Remaining After Re-scan",
                summary.getRemainingIssuesAfterRescan() >= 0 ? String.valueOf(summary.getRemainingIssuesAfterRescan()) : "N/A",
                summary.getRemainingIssuesAfterRescan() == 0 ? "good" : "bad"));
        sb.append(card("Execution Time", formatDuration(summary.getExecutionTimeMillis()), NEUTRAL));
        sb.append("</div>");

        sb.append("<h2>Issue Details</h2>");
        sb.append("<table><thead><tr>")
          .append("<th>File</th><th>Line</th><th>Rule</th><th>Severity</th><th>Message</th>")
          .append("<th>Status</th><th>Confidence</th><th>Risk</th>")
          .append("</tr></thead><tbody>");

        if (results == null || results.isEmpty()) {
            sb.append("<tr><td colspan='8' class='empty'>No issues processed.</td></tr>");
        } else {
            for (AISonarResult r : results) {
                sb.append("<tr>");
                sb.append("<td>").append(escape(r.getFilePath())).append("</td>");
                sb.append("<td>").append(r.getLine()).append("</td>");
                sb.append("<td>").append(escape(r.getRule())).append("</td>");
                sb.append("<td>").append(escape(r.getSeverity())).append("</td>");
                sb.append("<td>").append(escape(r.getMessage())).append("</td>");
                sb.append("<td>").append(statusBadge(r.getStatus())).append("</td>");
                sb.append("<td>").append(r.getConfidence() > 0 ? r.getConfidence() + "%" : "-").append("</td>");
                sb.append("<td>").append(escape(r.getRiskRating())).append("</td>");
                sb.append("</tr>");
            }
        }

        sb.append("</tbody></table>");
        sb.append("</div></body></html>");

        return sb.toString();
    }

    private static String statusBadge(AISonarResult.Status status) {
        if (status == null) {
            return "-";
        }
        String cssClass;
        switch (status) {
            case FIXED:
                cssClass = "good";
                break;
            case FAILED:
                cssClass = "bad";
                break;
            case ROLLED_BACK:
                cssClass = "warn";
                break;
            default:
                cssClass = NEUTRAL;
        }
        return "<span class='pill " + cssClass + "'>" + status.name() + "</span>";
    }

    private static String card(String label, String value, String cssClass) {
        return "<div class='card " + cssClass + "'><div class='card-value'>" + escape(value)
                + "</div><div class='card-label'>" + escape(label) + "</div></div>";
    }

    private static String formatDuration(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return minutes > 0 ? (minutes + "m " + seconds + "s") : (seconds + "s " + (millis % 1000) + "ms");
    }

    private static String escape(String s) {
        if (s == null) {
            return EMPTY_STRING;
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static String css() {
        return "body{font-family:Segoe UI,Arial,sans-serif;background:#121212;color:#E0E0E0;margin:0;padding:20px;}"
                + EMPTY_STRING + ".container{max-width:1200px;margin:0 auto;}"
                + EMPTY_STRING + "h1{color:#00BCD4;} .timestamp{color:#9E9E9E;margin-top:-10px;}"
                + EMPTY_STRING + ".status{margin:15px 0;}"
                + EMPTY_STRING + ".badge{padding:8px 16px;border-radius:6px;font-weight:bold;}"
                + EMPTY_STRING + ".badge.ready{background:#1B5E20;color:#A5D6A7;}"
                + EMPTY_STRING + ".badge.blocked{background:#5D1A1A;color:#EF9A9A;}"
                + EMPTY_STRING + ".cards{display:flex;flex-wrap:wrap;gap:12px;margin:20px 0;}"
                + EMPTY_STRING + ".card{background:#1E1E1E;border-radius:8px;padding:14px 18px;min-width:150px;border-left:4px solid #555;}"
                + EMPTY_STRING + ".card.good{border-left-color:#4CAF50;} .card.bad{border-left-color:#F44336;} "
                + EMPTY_STRING + ".card.warn{border-left-color:#FF9800;} .card.neutral{border-left-color:#00BCD4;}"
                + EMPTY_STRING + ".card-value{font-size:24px;font-weight:bold;} .card-label{color:#9E9E9E;font-size:12px;margin-top:4px;}"
                + EMPTY_STRING + "table{width:100%;border-collapse:collapse;margin-top:10px;background:#1A1A1A;}"
                + EMPTY_STRING + "th,td{padding:8px 10px;border-bottom:1px solid #333;text-align:left;font-size:13px;word-break:break-word;}"
                + EMPTY_STRING + "th{background:#2A2A2A;color:#00BCD4;}"
                + EMPTY_STRING + ".pill{padding:3px 10px;border-radius:12px;font-size:12px;font-weight:bold;}"
                + EMPTY_STRING + ".pill.good{background:#1B5E20;color:#A5D6A7;} .pill.bad{background:#5D1A1A;color:#EF9A9A;}"
                + EMPTY_STRING + ".pill.warn{background:#5D4A1A;color:#FFE082;} .pill.neutral{background:#263238;color:#B0BEC5;}"
                + EMPTY_STRING + ".empty{text-align:center;color:#9E9E9E;padding:20px;}";
    }
}
