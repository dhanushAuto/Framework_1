package ai.test;

import ai.healing.SonarAutoFix;
import org.testng.annotations.Test;
import utilities.common_utils.LogUtils;

public class SonarAITestRunner {

    @Test
    public void runSonarAI() {

        new SonarAutoFix().execute();

    }

    @Test
    public void runAICodeReview() {
        // Example review of a small file to avoid hanging
        try {
            AICodeReviewRunner.runAICodeReview("src/main/java/ai/analyzer/SonarIssueAnalyzer.java");
        } catch (Exception e) {
            LogUtils.error("AI Code Review failed: " + e.getMessage());
        }
    }
}