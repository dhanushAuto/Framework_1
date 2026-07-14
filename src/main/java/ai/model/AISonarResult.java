package ai.model;

/**
 * Represents the outcome of processing a single Sonar issue through the
 * AI Sonar Auto-Fix pipeline. Used purely for reporting (see
 * {@code ai.report.AISonarReportGenerator}) and does not affect the
 * fix/apply logic itself.
 */
public class AISonarResult {

    public enum Status {
        FIXED,
        FAILED,
        SKIPPED_UNSAFE,
        SKIPPED_CACHED,
        ROLLED_BACK
    }

    private String filePath;
    private String rule;
    private String severity;
    private String message;
    private int line;
    private Status status;
    private int confidence;
    private String riskRating;
    private String detail;

    public AISonarResult() {
    }

    public AISonarResult(SonarIssue issue, Status status) {
        if (issue != null) {
            this.filePath = issue.getComponent();
            this.rule = issue.getRule();
            this.severity = issue.getSeverity();
            this.message = issue.getMessage();
            this.line = issue.getLine();
        }
        this.status = status;
    }

    public static AISonarResult fromFix(SonarFix fix) {
        AISonarResult result = new AISonarResult(fix.getIssue(), fix.isFixed() ? Status.FIXED : Status.FAILED);
        result.setConfidence(fix.getConfidence());
        result.setRiskRating(fix.getRiskRating());
        result.setDetail(fix.getAiExplanation());
        return result;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public String getRiskRating() {
        return riskRating;
    }

    public void setRiskRating(String riskRating) {
        this.riskRating = riskRating;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
