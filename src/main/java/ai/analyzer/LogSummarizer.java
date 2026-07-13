package ai.analyzer;

import ai.service.AIService;
import java.util.ArrayList;
import java.util.List;

public class LogSummarizer {

    private final AIService aiService = new AIService();

    public LogSummary summarizeLog(String logContent, String context) throws Exception {
        String prompt = buildSummaryPrompt(logContent, context);
        String response = aiService.ask(prompt);
        return parseLogSummary(response);
    }

    private String buildSummaryPrompt(String logContent, String context) {
        String truncatedLog = logContent.length() > 5000 ? logContent.substring(0, 5000) + "..." : logContent;
        return """
            Analyze log for context: %s
            Log Content:
            %s
            Provide SUMMARY, KEY_EVENTS, ERROR_COUNT, WARNING_COUNT, OVERALL_STATUS.
            """.formatted(context, truncatedLog);
    }

    private LogSummary parseLogSummary(String aiResponse) {
        LogSummary summary = new LogSummary();
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            if (line.startsWith("SUMMARY:")) summary.setSummary(line.split(":", 2)[1].trim());
            else if (line.startsWith("ERROR_COUNT:")) try { summary.setErrorCount(Integer.parseInt(line.split(":")[1].trim())); } catch (Exception e) {}
            else if (line.startsWith("WARNING_COUNT:")) try { summary.setWarningCount(Integer.parseInt(line.split(":")[1].trim())); } catch (Exception e) {}
            else if (line.startsWith("OVERALL_STATUS:")) summary.setStatus(line.split(":")[1].trim());
        }
        return summary;
    }

    public static class LogSummary {
        private String summary;
        private List<String> keyEvents = new ArrayList<>();
        private int errorCount;
        private int warningCount;
        private String status;

        public String getSummary() { return summary; }
        public void setSummary(String s) { this.summary = s; }
        public List<String> getKeyEvents() { return keyEvents; }
        public void setKeyEvents(List<String> ke) { this.keyEvents = ke; }
        public int getErrorCount() { return errorCount; }
        public void setErrorCount(int ec) { this.errorCount = ec; }
        public int getWarningCount() { return warningCount; }
        public void setWarningCount(int wc) { this.warningCount = wc; }
        public String getStatus() { return status; }
        public void setStatus(String s) { this.status = s; }
    }
}
