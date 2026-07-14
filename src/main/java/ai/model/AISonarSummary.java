package ai.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Aggregate summary produced at the end of one AI Sonar Auto-Fix pipeline
 * execution. Consumed by {@code ai.report.AISonarReportGenerator}.
 */
public class AISonarSummary {

    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private int totalIssuesFound;
    private int safeIssuesSelected;
    private int skippedUnsafeCount;
    private int skippedCachedCount;
    private int fixedCount;
    private int failedCount;
    private int rolledBackCount;
    private int remainingIssuesAfterRescan = -1;
    private boolean compileSuccess;
    private boolean sonarRescanPerformed;
    private long executionTimeMillis;
    private final String generatedAt = LocalDateTime.now(ZoneId.systemDefault()).format(TS_FORMAT);

    public boolean isReadyForJenkins() {
        return compileSuccess && remainingIssuesAfterRescan == 0;
    }

    public int getTotalIssuesFound() {
        return totalIssuesFound;
    }

    public void setTotalIssuesFound(int totalIssuesFound) {
        this.totalIssuesFound = totalIssuesFound;
    }

    public int getSafeIssuesSelected() {
        return safeIssuesSelected;
    }

    public void setSafeIssuesSelected(int safeIssuesSelected) {
        this.safeIssuesSelected = safeIssuesSelected;
    }

    public int getSkippedUnsafeCount() {
        return skippedUnsafeCount;
    }

    public void setSkippedUnsafeCount(int skippedUnsafeCount) {
        this.skippedUnsafeCount = skippedUnsafeCount;
    }

    public int getSkippedCachedCount() {
        return skippedCachedCount;
    }

    public void setSkippedCachedCount(int skippedCachedCount) {
        this.skippedCachedCount = skippedCachedCount;
    }

    public int getFixedCount() {
        return fixedCount;
    }

    public void setFixedCount(int fixedCount) {
        this.fixedCount = fixedCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public int getRolledBackCount() {
        return rolledBackCount;
    }

    public void setRolledBackCount(int rolledBackCount) {
        this.rolledBackCount = rolledBackCount;
    }

    public int getRemainingIssuesAfterRescan() {
        return remainingIssuesAfterRescan;
    }

    public void setRemainingIssuesAfterRescan(int remainingIssuesAfterRescan) {
        this.remainingIssuesAfterRescan = remainingIssuesAfterRescan;
    }

    public boolean isCompileSuccess() {
        return compileSuccess;
    }

    public void setCompileSuccess(boolean compileSuccess) {
        this.compileSuccess = compileSuccess;
    }

    public boolean isSonarRescanPerformed() {
        return sonarRescanPerformed;
    }

    public void setSonarRescanPerformed(boolean sonarRescanPerformed) {
        this.sonarRescanPerformed = sonarRescanPerformed;
    }

    public long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    public void setExecutionTimeMillis(long executionTimeMillis) {
        this.executionTimeMillis = executionTimeMillis;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }
}
