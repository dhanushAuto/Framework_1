package ai.analyzer;

import ai.model.SonarIssue;
import ai.util.IssueCache;
import ai.util.SafeFixFilter;
import utilities.common_utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes the raw list of Sonar issues fetched from the server and splits
 * them into buckets the rest of the AI Sonar Auto-Fix pipeline can act on:
 * <ul>
 *     <li>{@code safeIssues} - eligible for AI-generated fixes this run</li>
 *     <li>{@code alreadyFixedIssues} - previously fixed, skipped for performance</li>
 *     <li>{@code unsafeIssues} - never sent to the AI (see {@link SafeFixFilter})</li>
 * </ul>
 */
public class SonarIssueAnalyzer {

    public AnalysisResult analyze(List<SonarIssue> issues) {
        AnalysisResult result = new AnalysisResult();

        if (issues == null || issues.isEmpty()) {
            LogUtils.info("SonarIssueAnalyzer: no issues to analyze.");
            return result;
        }

        for (SonarIssue issue : issues) {
            if (IssueCache.isAlreadyFixed(issue)) {
                result.alreadyFixedIssues.add(issue);
                continue;
            }

            if (SafeFixFilter.isSafeIssue(issue)) {
                result.safeIssues.add(issue);
            } else {
                result.unsafeIssues.add(issue);
            }
        }

        LogUtils.info(String.format(
                "SonarIssueAnalyzer: %d total | %d safe | %d unsafe | %d already fixed (cached)",
                issues.size(), result.safeIssues.size(), result.unsafeIssues.size(), result.alreadyFixedIssues.size()));

        return result;
    }

    public static class AnalysisResult {
        private final List<SonarIssue> safeIssues = new ArrayList<>();
        private final List<SonarIssue> unsafeIssues = new ArrayList<>();
        private final List<SonarIssue> alreadyFixedIssues = new ArrayList<>();

        public List<SonarIssue> getSafeIssues() {
            return safeIssues;
        }

        public List<SonarIssue> getUnsafeIssues() {
            return unsafeIssues;
        }

        public List<SonarIssue> getAlreadyFixedIssues() {
            return alreadyFixedIssues;
        }
    }
}
