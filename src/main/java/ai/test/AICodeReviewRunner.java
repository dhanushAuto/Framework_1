package ai.test;

import ai.analyzer.AICodeReviewer;
import utilities.common_utils.LogUtils;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class AICodeReviewRunner {

    public static void main(String[] args) throws Exception {
        runAICodeReview("src/main/java/ai/client/AIClient.java");
    }

    public static void runAICodeReview(String filePath) throws Exception {
        LogUtils.info("=== Starting AI Code Review ===");
        LogUtils.info("File: " + filePath);

        String sourceCode = Files.readString(Paths.get(filePath));
        AICodeReviewer reviewer = new AICodeReviewer();

        List<AICodeReviewer.CodeReviewFinding> findings = reviewer.reviewCode(filePath, sourceCode);

        LogUtils.info("Findings Found: " + findings.size());
        for (AICodeReviewer.CodeReviewFinding finding : findings) {
            LogUtils.info("LINE: " + finding.getLine() + " | SEVERITY: " + finding.getSeverity() + " | CATEGORY: " + finding.getCategory() + " | MESSAGE: " + finding.getMessage());
        }
        LogUtils.info("=== AI Code Review Completed ===");
    }
}
